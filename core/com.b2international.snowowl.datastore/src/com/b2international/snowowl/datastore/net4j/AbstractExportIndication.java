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
package com.b2international.snowowl.datastore.net4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.Nullable;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.importer.ITerminologyExporter;

/**
 * Common superclass for single file based export indications required by the Net4j protocol.
 * Clients to extend.
 * 
 *
 */
public abstract class AbstractExportIndication extends IndicationWithMonitoring {
	
	private String userId;
	private IBranchPath branchPath;

	/**
	 * @param protocol
	 * @param valueSetExcelExportSignal
	 */
	public AbstractExportIndication(SignalProtocol<?> protocol, short exportSignal) {
		super(protocol, exportSignal);
	}
	
	/**
	 * Creates the terminology specific exporter.
	 * 
	 * @return
	 */
	protected abstract ITerminologyExporter getExporter();
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.IndicationWithMonitoring#indicating(org.eclipse.net4j.util.io.ExtendedDataInputStream, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception {
		
		userId = in.readUTF();
		final String branchPathString = in.readUTF();
		
		if (StringUtils.isEmpty(branchPathString)) {
			throw new SnowowlServiceException("Null or empty branch path is prohibited.");
		}
		
		try {
			branchPath = BranchPathUtils.createPath(branchPathString);
		} catch (final Throwable t) {
			throw new SnowowlServiceException("Failed to perform terminology export due to incorrect branch path.");
		}
		
		postIndicating(in);
	}
	
	protected void postIndicating(final ExtendedDataInputStream in) throws Exception {
		//does nothing by default. clients may override it.
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.IndicationWithMonitoring#responding(org.eclipse.net4j.util.io.ExtendedDataOutputStream, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	protected void responding(final ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {
		
		monitor.begin(100);
		monitor.worked(5);
		
		final File exportedFile = getExporter().doExport(monitor);

		sendFile(out, exportedFile, monitor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.IndicationWithMonitoring#getIndicatingWorkPercent()
	 */
	@Override
	protected int getIndicatingWorkPercent() {
		return 0;
	}
	
	/**
	 * Returns with the user ID. Could be {@code null}.
	 */
	@Nullable protected String getUserId() {
		return userId;
	}
	
	/**
	 * Returns with the branch path. Could be {@code null}.
	 */
	@Nullable protected IBranchPath getBranchPath() {
		return branchPath;
	}
	
	private void sendFile(ExtendedDataOutputStream out, File file, OMMonitor monitor) throws IOException, FileNotFoundException {
		long size = file.length();
		BufferedInputStream in = null;
		
		final Async async = monitor.forkAsync(10);
		
		out.writeLong(size);	
		
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			while (size != 0L) {
				int chunk = Net4jProtocolConstants.BUFFER_SIZE;
				if (size < Net4jProtocolConstants.BUFFER_SIZE) {
					chunk = (int)size;
				}
				
				monitor.worked(chunk);
	
				byte[] buffer = new byte[chunk];
				in.read(buffer);
				out.writeByteArray(buffer);
	
				size -= chunk;
			}
		}
		finally {
			async.stop();
			in.close();
		}
	}

}