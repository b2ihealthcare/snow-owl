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

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;

/**
 * Simple, user aware import request.
 */
public abstract class AbstractSimpleImportRequest extends RequestWithMonitoring<Boolean> {

	private static final int REQUEST_TIMEOUT_MILLIS = 60 * 30 * 1000;

	private final File importFile;
	private final String userId;

	public AbstractSimpleImportRequest(final SignalProtocol<?> protocol, final short signalId, final File importFile, final String userId) {
		super(protocol, signalId);
		this.importFile = importFile;
		this.userId = userId;
	}

	@Override
	protected int getRequestingWorkPercent() {
		return 50;
	}

	// Override send() methods without a timeout parameter to enforce per-request timeout

	@Override
	public Boolean send() throws Exception, RemoteException {
		return super.send(REQUEST_TIMEOUT_MILLIS);
	}

	@Override
	public Boolean send(final OMMonitor monitor) throws Exception, RemoteException {
		return super.send(REQUEST_TIMEOUT_MILLIS, monitor);
	}

	@Override
	protected int getMonitorTimeoutSeconds() {
		return REQUEST_TIMEOUT_MILLIS / 1000;
	}

	@Override
	protected void requesting(final ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {

		long size = importFile.length();

		monitor.begin(size);

		// size of the file
		out.writeLong(size);

		// name of the file
		out.writeUTF(importFile.getName());

		// requesting user ID for the import
		out.writeUTF(userId);

		try (FileInputStream fileInputStream = new FileInputStream(importFile)) {
			try (BufferedInputStream in = new BufferedInputStream(fileInputStream)) {
				while (size != 0L) {

					int chunk = Net4jProtocolConstants.BUFFER_SIZE;

					if (size < Net4jProtocolConstants.BUFFER_SIZE) {
						chunk = (int) size;
					}

					monitor.worked(chunk);

					final byte[] buffer = new byte[chunk];
					in.read(buffer);
					out.writeByteArray(buffer);

					size -= chunk;
				}
			}
		}

		monitor.done();
	}

	@Override
	protected Boolean confirming(final ExtendedDataInputStream in, final OMMonitor monitor) throws Exception {
		return in.readBoolean();
	}

}
