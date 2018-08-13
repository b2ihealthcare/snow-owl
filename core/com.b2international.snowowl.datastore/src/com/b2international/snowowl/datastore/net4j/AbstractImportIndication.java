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

import static com.b2international.commons.StringUtils.isEmpty;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.datastore.importer.AbstractTerminologyImportJob;
import com.b2international.snowowl.datastore.importer.TerminologyImportType;
import com.b2international.snowowl.datastore.importer.TerminologyImportValidationDefect;
import com.b2international.snowowl.datastore.importer.TerminologyImportValidationDefect.Defect;

/**
 * Common superclass for single file based import indications required by the Net4j protocol.
 * Clients to extend.
 * 
 *
 */
public abstract class AbstractImportIndication extends IndicationWithMonitoring {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractImportIndication.class);
	private final static String TEMP_DIR = System.getProperty("java.io.tmpdir");

	//the file to write the data into
	private File file;
	// the type of the import
	private TerminologyImportType importType;

	/**
	 * @param protocol
	 * @param importSignal
	 */
	public AbstractImportIndication(final SignalProtocol<?> protocol, final short importSignal) {
		super(protocol, importSignal);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.IndicationWithMonitoring#indicating(org.eclipse.net4j.util.io.ExtendedDataInputStream, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	protected void indicating(final ExtendedDataInputStream in, final OMMonitor monitor) throws Exception {
		// clear database
		importType = in.readEnum(TerminologyImportType.class);
		
		// size of the file
		long size = in.readLong();
		// name of the file
		final String fileName = in.readUTF();

		monitor.begin(size);

		BufferedOutputStream out = null;

		file = new File(TEMP_DIR, fileName);
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
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
			
			postFileIndicating(in);
			
		} finally {
			monitor.done();
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * Triggered after the file has been read from the input stream and other property has to be deserialized
	 * from the input stream.
	 * <br>Does nothing by default clients may override it.
	 * @param in the stream to read from.
	 * @throws Exception
	 */
	protected void postFileIndicating(final ExtendedDataInputStream in) throws Exception {
		//does nothing by default. clients may override it.
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.IndicationWithMonitoring#responding(org.eclipse.net4j.util.io.ExtendedDataOutputStream, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	protected void responding(final ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {

		Async forkAsync = null;

		try {
			final AbstractTerminologyImportJob importer = getImporterJob();
			importer.schedule();

			//single unit of work
			monitor.begin();
			forkAsync = monitor.forkAsync();
			importer.join();
			final IStatus result = importer.getResult();
			if (result.isOK()) {
				out.writeBoolean(true);
				
				final Set<String> visitedComponents = importer.getTerminologyImportResult().getVisitedComponents();
				
				out.writeInt(visitedComponents.size());
				
				for (final String componentId : visitedComponents) {
					out.writeUTF(componentId);
				}
			} else if (result.equals(Status.CANCEL_STATUS)) {
				out.writeBoolean(false);
				
				final Set<TerminologyImportValidationDefect> defects = importer.getTerminologyImportResult().getValidationDefects();
				out.writeInt(defects.size());
				
				for (final TerminologyImportValidationDefect validationDefect : defects) {
					out.writeUTF(validationDefect.getSheetName());
					out.writeInt(validationDefect.getDefects().size());
					
					for (final Defect  defect : validationDefect.getDefects()) {
						out.writeEnum(defect.getDefectType());
						out.writeUTF(defect.getErrorMessage());
					}
				}
			} else {
				
				if (null != result) {
					final Throwable exception = result.getException();
					if (null != exception && !isEmpty(exception.getMessage()) && !isEmpty(result.getMessage())) {
						throw new Exception(result.getMessage(), exception);
					}
					
				}

				throw new Exception("An error occurred while executing the import.");
			}
		} finally {
			if (forkAsync != null) {
				forkAsync.stop();
			}
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	/**
	 * Returns the importer job associated with this importer.
	 * @return
	 */
	protected abstract AbstractTerminologyImportJob getImporterJob();

	/**
	 * Returns the file where the to-be-imported data will be written on the server-side.
	 * @return
	 */
	protected File getFile() {
		return file;
	}

	/**
	 * Returns the logger associated with this importer
	 * @return
	 */
	protected Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Returns the server-side temporary path for the file to be imported.
	 * System.getProperty("java.io.tmpdir") by default.
	 * 
	 * @return
	 */
	protected String getTemporaryDir() {
		return TEMP_DIR;
	}

	public TerminologyImportType getImportType() {
		return importType;
	}

}