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
package com.b2international.snowowl.api.codesystem.domain;

/**
 * Captures metadata about a code system, which holds a set of real-world concepts of medical significance (optionally
 * along with different components forming a description of said concepts) and their corresponding unique code.
 */
public interface ICodeSystem {

	/**
	 * Returns the assigned object identifier (OID) of this code system.
	 * 
	 * @return the assigned object identifier of this code system, eg. "{@code 3.4.5.6.10000}" (can be {@code null})
	 */
	String getOid();

	/**
	 * Returns the name of this code system.
	 * 
	 * @return the name of this code system, eg. "{@code SNOMED Clinical Terms}"
	 */
	String getName();

	/**
	 * Returns the short name of this code system, which is usually an abbreviation of the name.
	 * 
	 * @return the short name of this code system, eg. "{@code SNOMEDCT}"
	 */
	String getShortName();

	/**
	 * Returns an URL for this code system which points to the maintaining organization's website.
	 * 
	 * @return the URL of the maintaining organization, eg. "{@code http://example.com/}" (can be {@code null}) 
	 */
	String getOrganizationLink();

	/**
	 * Returns the primary language tag for this code system.
	 * 
	 * @return the primary language tag, eg. "en_US"
	 */
	String getPrimaryLanguage();

	/**
	 * Returns a short paragraph describing the origins and purpose of this code system.
	 * 
	 * @return the citation for this code system (can be {@code null})
	 */
	String getCitation();
	
	/**
	 * Returns the branch path of the code system.
	 * 
	 * @return the path for the code system.
	 */
	String getBranchPath();

	/**
	 * Returns with the application specific icon path of the code system.
	 * 
	 * @return the application specific icon path.
	 */
	String getIconPath();

	/**
	 * Returns with the application specific ID to associate the code system
	 * with any application specific feature or container repository.
	 * 
	 * @return the application specific ID.
	 */
	String getTerminologyId();

	/**
	 * Returns with the unique ID of the repository where the current code
	 * system belongs to.
	 * 
	 * @return the repository UUID for the code system.
	 */
	String getRepositoryUuid();
	
	/**
	 * Returns the unique ID of the base Code System of this Code System.
	 */
	String getExtensionOf();
}
