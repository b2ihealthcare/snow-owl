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
package com.b2international.snowowl.scripting.services.api;

import java.util.List;

import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.ICodeSystemVersion;

/**
 * This service provides access to global code systems related information.
 * 
 * This service is not specific to a particular code system.
 * 
 *
 */
public interface ICodeSystemService {

	/**
	 * Retuns the list of standard code system object IDs representing the code systems supported by
	 * Snow Owl. If there is none the operation returns an empty list.
	 * 
	 * @return
	 */
	List<ICodeSystem> getCodeSystems();

	/**
	 * Returns a list of versions support for a particular code system.
	 * Returns an empty list if no version support is available for the code system, either because the 
	 * code system does not support the concept of versioning or Snow Owl does not support versioning for 
	 * the code system.
	 * 
	 * @param codeSystemOID
	 * @return
	 */
	List<ICodeSystemVersion> getCodeSystemVersions(final String codeSystemOID);
	
	/**
	 * Returns a list of versions support for a particular code system.
	 * Returns an empty list if no version support is available for the code system, either because the 
	 * code system does not support the concept of versioning or Snow Owl does not support versioning for 
	 * the code system.
	 * 
	 * @param codeSystem
	 * @return
	 */
	List<ICodeSystemVersion> getCodeSystemVersions(final ICodeSystem codeSystem);

	/**
	 * Returns the code system specified by the code system object id.
	 * 
	 * For example for  2.16.840.1.113883.6.96 it would return SNOMED CT.
	 * 
	 * @param codeSystemOID
	 * @return the code system
	 */
	ICodeSystem getCodeSystem(final String codeSystemOID);

	/**
	 * Returns the supported languages for a particular code system within Snow Owl.
	 * 
	 * It returns an empty list in case the code system does not support internationalization.
	 * @param codeSystemOID
	 * @return
	 */
	List<String> getSupportedLanguages(final String codeSystemOID);

}