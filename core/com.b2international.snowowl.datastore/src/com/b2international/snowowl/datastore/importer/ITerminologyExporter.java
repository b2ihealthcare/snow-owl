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
package com.b2international.snowowl.datastore.importer;

import java.io.File;
import java.io.IOException;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * Interface for exporting terminology components.
 * 
 * @since 3.3
 */
public interface ITerminologyExporter {
	
	/**
	 * Runs the export process. The components will be exported into a file in the temp dir.
	 * 
	 * @param monitor
	 * @return the created file.
	 * @throws IOException
	 */
	public File doExport(final OMMonitor monitor) throws IOException;
	
	/**
	 * Runs the export process. The components will be exported into a file at the given file path.
	 * 
	 * @param exportFilePath the file path where the file will be created.
	 * @return the created file.
	 * @throws IOException
	 */
	public File doExport(final String exportFilePath) throws IOException;

}