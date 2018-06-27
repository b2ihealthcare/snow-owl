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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;

/**
 * Simple, user aware import indication.
 */
public abstract class AbstractSimpleImportIndication extends IndicationWithMonitoring {

	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

	private File file;
	private String userId;

	public AbstractSimpleImportIndication(final SignalProtocol<?> protocol, final short signalId) {
		super(protocol, signalId);
	}

	@Override
	protected void indicating(final ExtendedDataInputStream in, final OMMonitor monitor) throws Exception {

		long size = in.readLong();

		monitor.begin(size);

		final String fileName = in.readUTF();

		// client-side, requesting user ID
		userId = in.readUTF();

		file = new File(TEMP_DIR, fileName);

		try (FileOutputStream fos = new FileOutputStream(file)) {
			try (BufferedOutputStream out = new BufferedOutputStream(fos)) {
				while (size != 0L) {

					int chunk = Net4jProtocolConstants.BUFFER_SIZE;

					if (size < Net4jProtocolConstants.BUFFER_SIZE) {
						chunk = (int) size;
					}

					monitor.worked(chunk);

					final byte[] buffer = in.readByteArray();
					out.write(buffer);

					size -= chunk;
				}
			}
		}

		monitor.done();
	}

	protected String getUserId() {
		return userId;
	}
	
	protected File getFile() {
		return file;
	}
}
