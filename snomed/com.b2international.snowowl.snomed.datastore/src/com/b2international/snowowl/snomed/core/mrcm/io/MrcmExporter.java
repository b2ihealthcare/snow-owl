/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.mrcm.io;

import java.io.OutputStream;

/**
 * @since 4.4
 */
public interface MrcmExporter {

	/**
	 * Exports the current state of the MRCM rules.
	 * 
	 * @param user
	 *            - the authenticated user to perform the export
	 * @param stream
	 *            - the outputstream to write MRCM release content to
	 *            
	 * @param exportFormat the export format to use           
	 * @return - the exported file path
	 */
	void doExport(String user, OutputStream content, final MrcmExportFormat exportFormat);

}
