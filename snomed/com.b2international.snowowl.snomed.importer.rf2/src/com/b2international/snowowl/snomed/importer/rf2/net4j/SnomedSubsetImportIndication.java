/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.importer.rf2.net4j;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.snomed.importer.net4j.SnomedImportProtocolConstants;
import com.b2international.snowowl.snomed.importer.net4j.SnomedUnimportedRefSets;
import com.b2international.snowowl.snomed.importer.net4j.SnomedUnimportedRefSets.StoreRefSetMember;
import com.google.common.io.Closeables;

/**
 * Import indication to receive the data from the client, creates the temporary
 * file and send back the answer data to the client.
 * 
 * 
 */
public class SnomedSubsetImportIndication extends IndicationWithMonitoring {

	public final static String TEMP_DIR = System.getProperty("java.io.tmpdir");
	private File file;
	private boolean hasHeader;
	private boolean skipEmptyLines;
	private int idColumnNumber;
	private int firstConceptRowNumber;
	private String fieldSeparator;
	private String quoteCharacter;
	private String lineFeedCharacter;
	private String fileExtension;
	private String effectiveTime;
	private String subsetName;
	private String namespace;
	private String branchPath;
	private int sheetNumber;
	private String refSetParent;
	private String userId;

	public SnomedSubsetImportIndication(final SnomedImportServerProtocol protocol) {
		super(protocol, SnomedImportProtocolConstants.SIGNAL_IMPORT_SUBSET);
	}

	@Override
	protected void indicating(final ExtendedDataInputStream in, final OMMonitor monitor) throws Exception {

		//e.g.: MAIN, or MAIN/NRC_1/TASK_300
		branchPath = in.readUTF();
		//read requesting user ID
		userId = in.readUTF();
		hasHeader = in.readBoolean();
		skipEmptyLines = in.readBoolean();
		idColumnNumber = in.readInt();
		firstConceptRowNumber = in.readInt();
		sheetNumber = in.readInt();
		refSetParent = in.readUTF();
		subsetName = in.readUTF();
		fileExtension = in.readUTF();
		effectiveTime = in.readUTF();
		namespace = in.readUTF();
		fieldSeparator = in.readUTF();
		quoteCharacter = in.readUTF();
		lineFeedCharacter = in.readUTF();

		long size = in.readLong();

		final String fileName = in.readUTF();

		monitor.begin(size);

		BufferedOutputStream out = null;

		file = new File(TEMP_DIR, fileName);
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			while (size != 0L) {

				final byte[] buffer = in.readByteArray();
				final int chunk = buffer.length;

				monitor.worked(chunk);
				out.write(buffer);

				size -= chunk;
			}
		} finally {
			monitor.done();
			Closeables.close(out, true);
		}
	}

	@Override
	protected void responding(final ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {

		final SnomedSubsetImporter importer = new SnomedSubsetImporter(branchPath, userId, hasHeader, skipEmptyLines, idColumnNumber, firstConceptRowNumber, sheetNumber, refSetParent, subsetName,
				fileExtension, effectiveTime, namespace, fieldSeparator, quoteCharacter, lineFeedCharacter, file);

		monitor.begin(1);

		try {
			final SnomedUnimportedRefSets unImported = importer.doImport();
			final String effectimTime = null == unImported.getEffectiveTime() ? "Unpublished" : unImported.getEffectiveTime();
			
			out.writeString(unImported.getFileName());
			out.writeString(unImported.getRefSetName());
			out.writeString(unImported.getNameSpace());
			out.writeString(effectimTime);
			out.writeInt(unImported.getUnimportedRefSetMembers().size());

			final Iterator<StoreRefSetMember> iterator = unImported.getUnimportedRefSetMembers().iterator();
			while (iterator.hasNext()) {
				final StoreRefSetMember member = iterator.next();
				out.writeString(member.getReason());
				out.writeString(member.getConceptId());
				out.writeString(member.getFullySpecifiedName());
			}

			monitor.worked();
		} catch (final SnowowlRuntimeException e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage(), true);
		} finally {
			monitor.done();
		}
	}

}