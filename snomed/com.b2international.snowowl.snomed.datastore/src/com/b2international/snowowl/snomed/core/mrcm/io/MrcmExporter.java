/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.identity.User;

/**
 * @since 4.4
 */
public interface MrcmExporter {

	/**
	 * Exports the current state of the MRCM rules.
	 * 
	 * @param authorizationToken
	 *            - the token to use to authenticate and authorize the user before performing the export
	 * @param content
	 *            - the outputstream to write MRCM release content to
	 *            
	 * @param exportFormat the export format to use           
	 * @return - the exported file path
	 */
	void doExport(String authorizationToken, OutputStream content, final MrcmExportFormat exportFormat);
	
	void doExport(User user, OutputStream content, MrcmExportFormat exportFormat);

}
