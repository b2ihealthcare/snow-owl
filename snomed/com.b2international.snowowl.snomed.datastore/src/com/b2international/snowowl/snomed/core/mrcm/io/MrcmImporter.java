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

import java.io.InputStream;

import com.b2international.snowowl.core.identity.User;

/**
 * @since 4.4
 */
public interface MrcmImporter {

	/**
	 * Imports the given MRCM file impersonating a user given with the user name
	 * argument.
	 * 
	 * @param authorizationToken - the authorization token to authenticate the user
	 * @param source - the MRCM file to process and import.
	 */
	void doImport(User user, InputStream source);
	
	void doImport(String authorizationToken, String username, InputStream source);

}
