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
package com.b2international.snowowl.snomed.importer.net4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.snomed.importer.net4j.SnomedSubsetImportConfiguration.SubsetEntry;

/**
 * Import request to send the data to the server and to receive the answer data
 * from the server.
 * 
 * 
 */
public class SnomedSubsetImportRequest extends RequestWithMonitoring<SnomedUnimportedRefSets> {

	private final File importFile;
	private final SubsetEntry entry;
	private final String branchPath;

	public SnomedSubsetImportRequest(final String branchPath, final SubsetEntry entry, final SnomedImportClientProtocol protocol, final File importFile) {
		super(protocol, SnomedImportProtocolConstants.SIGNAL_IMPORT_SUBSET);
		this.branchPath = branchPath;
		this.entry = entry;
		this.importFile = importFile;
	}

	@Override
	protected int getMonitorTimeoutSeconds() {
		return 100;
	}

	@Override
	protected int getRequestingWorkPercent() {
		return 50;
	}

	@Override
	protected void requesting(final ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {
		long size = importFile.length();

		BufferedInputStream in = null;

		monitor.begin(size);
		
		final SnomedSubsetImportUtil importUtil = new SnomedSubsetImportUtil();
		importUtil.updateNullProperties(entry);

		//e.g.: MAIN or MAIN/NationalReleaseCenter/TaskId
		out.writeUTF(branchPath);

		//write requesting user ID
		out.writeUTF(ApplicationContext.getInstance().getService(ICDOConnectionManager.class).getUserId());
		
		out.writeBoolean(entry.isHasHeader());
		out.writeBoolean(entry.isSkipEmptyLines());
		out.writeInt(entry.getIdColumnNumber());
		out.writeInt(entry.getFirstConceptRowNumber());
		out.writeInt(entry.getSheetNumber());
		out.writeUTF(entry.getRefSetParent());
		out.writeUTF(entry.getSubsetName());
		out.writeUTF(entry.getExtension());
		out.writeUTF(entry.getEffectiveTime()); 
		out.writeUTF(entry.getNamespace());
		out.writeUTF(entry.getFieldSeparator());
		out.writeUTF(entry.getQuoteCharacter());
		out.writeUTF(entry.getLineFeedCharacter());
		
		out.writeLong(size);
		out.writeUTF(importFile.getName());

		try {
			in = new BufferedInputStream(new FileInputStream(importFile));
			while (size != 0L) {

				int chunk = Net4jProtocolConstants.BUFFER_SIZE;
				if (size < Net4jProtocolConstants.BUFFER_SIZE) {
					chunk = (int) size;
				}

				monitor.worked(chunk);

				final byte[] buffer = new byte[chunk];
				final int actualRead = in.read(buffer);

				out.writeByteArray(Arrays.copyOf(buffer, actualRead));

				size -= actualRead;
			}
		} finally {
			in.close();
			monitor.done();
		}
	}

	@Override
	protected SnomedUnimportedRefSets confirming(final ExtendedDataInputStream in, final OMMonitor monitor) throws Exception {
		final String fileName = in.readString();
		final String subsetName = in.readString();
		final String nameSpace = in.readString();
		final String effectiveTime = in.readString();
		final int size = in.readInt();

		final SnomedUnimportedRefSets unimportedRefSets = new SnomedUnimportedRefSets(fileName, subsetName, nameSpace, effectiveTime);

		for (int i = 0; i < size; i++) {
			final String reason = in.readString();
			final String conceptId = in.readString();
			final String fullySpecifiedName = in.readString();
			unimportedRefSets.addRefSetMember(reason, conceptId, fullySpecifiedName);
		}
		return unimportedRefSets;
	}

}