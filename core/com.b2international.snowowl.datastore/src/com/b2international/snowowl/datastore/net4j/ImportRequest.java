/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.net4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.datastore.importer.TerminologyImportType;
import com.b2international.snowowl.datastore.importer.TerminologyImportValidationDefect;
import com.b2international.snowowl.datastore.importer.TerminologyImportValidationDefect.DefectType;
import com.google.common.collect.Sets;

/**
 * Abstract import request class for sending single files over the Net4j protocol.
 * Clients to extend.
 * 
 *
 */
public class ImportRequest extends RequestWithMonitoring<Boolean> {

	private final File sourceDir;
	private final TerminologyImportType importType;
	private Set<String> visitedComponentIds;
	private Set<TerminologyImportValidationDefect> defects;
	
	/**
	 * @param protocol
	 * @param sourceDir the directory for the file to be imported
	 * @param importSignal
	 * @param importType 
	 */
	public ImportRequest(final SignalProtocol<?> protocol, final short importSignal, final File sourceDir, final TerminologyImportType importType) {
		super(protocol, importSignal);
		this.sourceDir = sourceDir;
		this.importType = importType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#requesting(org.eclipse.net4j.util.io.ExtendedDataOutputStream, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor)
			throws Exception {

		long size = sourceDir.length();

		BufferedInputStream in = null;

		monitor.begin(size);
		
		// type of the import
		out.writeEnum(importType);

		//size of the file
		out.writeLong(size);

		//name of the file
		out.writeUTF(sourceDir.getName());

		try {
			in = new BufferedInputStream(new FileInputStream(sourceDir));
			while (size != 0L) {
				int chunk = Net4jProtocolConstants.BUFFER_SIZE;
				if (size < Net4jProtocolConstants.BUFFER_SIZE) {
					chunk = (int) size;
				}

				monitor.worked(chunk);

				byte[] buffer = new byte[chunk];
				in.read(buffer);
				out.writeByteArray(buffer);

				size -= chunk;
			}
			
			postFileRequesting(out);
			
		} finally {
			in.close();
			monitor.done();
		}
	}
	
	/**
	 * Triggered after the file has been written to the output stream. Clients may override this method 
	 * if would like to write any arbitrary information to the output stream.
	 * <br>Does nothing by default.
	 * @param out stream to write onto.
	 * @throws Exception 
	 */
	protected void postFileRequesting(final ExtendedDataOutputStream out) throws Exception {
		//clients may override
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#confirming(org.eclipse.net4j.util.io.ExtendedDataInputStream, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	protected Boolean confirming(ExtendedDataInputStream in, OMMonitor monitor)	throws Exception {

		boolean importOk = in.readBoolean();
		
		if (importOk) {
			visitedComponentIds = Sets.newHashSet();
			int size = in.readInt();
			
			for (int i = 0; i < size; i++) {
				visitedComponentIds.add(in.readUTF());
			}
		} else {
			int size = in.readInt();
			
			defects = Sets.newHashSet();
			
			for (int i = 0; i < size; i++) {
				final String sheetName = in.readUTF();
				final int defectsNumber = in.readInt();
				
				final TerminologyImportValidationDefect validationDefect = new TerminologyImportValidationDefect(sheetName);
				
				for (int j = 0; j < defectsNumber; j++) {
					final DefectType defectType = in.readEnum(DefectType.class);
					final String errorMessage = in.readUTF();
					validationDefect.addDefect(defectType, errorMessage);
				}
				
				defects.add(validationDefect);
			}
		}
		
		return importOk;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#getMonitorTimeoutSeconds()
	 */
	@Override
	protected int getMonitorTimeoutSeconds() {
		return 100;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#getRequestingWorkPercent()
	 */
	@Override
	protected int getRequestingWorkPercent() {
		return 10;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#getConfirmingWorkPercent()
	 */
	@Override
	protected int getConfirmingWorkPercent() {
		return 85;
	}

	public final Set<String> getVisitedComponentIds() {
		return visitedComponentIds;
	}
	
	public Set<TerminologyImportValidationDefect> getDefects() {
		return defects;
	}
	
}