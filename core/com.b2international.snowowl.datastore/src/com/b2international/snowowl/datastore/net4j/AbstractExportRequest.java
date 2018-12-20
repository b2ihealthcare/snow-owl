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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.Net4jProtocolConstants;

/**
 * Abstract export request class for retrieving single files over the net4j protocol.
 * Clients to extend.
 */
public abstract class AbstractExportRequest extends RequestWithMonitoring<File> {

	private final String userId;
	private final String exportPath;
	private final IBranchPath branchPath;
	
	/**
	 * @param protocol
	 * @param importSignal
	 * @param branchPath
	 * @param exportPath
	 * @param userId
	 */
	public AbstractExportRequest(final SignalProtocol<?> protocol, final short importSignal, final IBranchPath branchPath, final String exportPath, final String userId) {
		super(protocol, importSignal);
		this.exportPath = exportPath;
		this.branchPath = branchPath;
		this.userId = userId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#requesting(org.eclipse.net4j.util.io.ExtendedDataOutputStream, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception {
		
		out.writeUTF(userId); //user ID
		out.writeUTF(branchPath.getPath()); //branch path as string
		
		postRequesting(out);
	}
	
	protected void postRequesting(final ExtendedDataOutputStream out) throws Exception {
		//clients may override
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#confirming(org.eclipse.net4j.util.io.ExtendedDataInputStream, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	protected File confirming(ExtendedDataInputStream in, OMMonitor monitor)
			throws Exception {
		
		long size = in.readLong();
		
		monitor.begin(size);
				
		BufferedOutputStream out = null;
		
		File file = new File(exportPath);
		try { 
			out = new BufferedOutputStream(new FileOutputStream(file));
			while (size != 0L) {
				int chunk = Net4jProtocolConstants.BUFFER_SIZE;
				if (size < Net4jProtocolConstants.BUFFER_SIZE) {
					chunk = (int)size;
				}
				
				monitor.worked(chunk);
				
				byte[] buffer = in.readByteArray();
				out.write(buffer);
				
				size -= chunk;
			}
		} finally {
			monitor.done();
			if (out != null) {
				out.close();
			}
		}
		return file;
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
		return 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#getConfirmingWorkPercent()
	 */
	@Override
	protected int getConfirmingWorkPercent() {
		return 0;
	}
	
}