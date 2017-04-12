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
package com.b2international.snowowl.datastore;

import java.io.Serializable;

import javax.annotation.Nullable;

/**
 * Serializable representation of a code system.
 */
public interface ICodeSystem extends Serializable {

	/**
	 * Unique terminology component identifier for code systems.
	 */
	short TERMINOLOGY_COMPONENT_ID = 1;

	/**
	 * Returns the code system OID. Can be {@code null}.
	 * @return the OID.
	 */
	@Nullable String getOid();

	/**
	 * Returns with the name of the code system.
	 * @return the name of the code system.
	 */
	String getName();

	/**
	 * Returns with the code system short name.
	 * @return the code system short name.
	 */
	String getShortName();

	/**
	 * Returns with the maintaining organization link. Can be {@code null}.
	 * @return the link for the maintaining organization. 
	 */
	@Nullable String getOrgLink();

	/**
	 * Returns with the language of the code system.
	 * @return the language.
	 */
	String getLanguage();

	/**
	 * Returns with the citation of the code system.
	 * @return the citation of the code system.
	 */
	String getCitation();

	/**
	 * Returns with the application specific icon path of the code system. 
	 * @return the application specific icon path.
	 */
	String getIconPath();

	/**
	 * Returns with the application specific ID to associate the code 
	 * system with any application specific
	 *  feature or container repository.
	 * @return the application specific ID.
	 */
	String getTerminologyComponentId();
	
	/**
	 * Returns with the unique ID of the repository where the current code system belongs to. 
	 * @return the repository UUID for the code system.
	 */
	String getRepositoryUuid();
	
	/**
	 * Returns with the storage key of the code system.
	 * @return
	 */
	long getStorageKey();
	
	/**
	 * Returns the branch path of the code system.
	 * @return the path for the code system.
	 */
	String getBranchPath();
	
	/**
	 * Returns the unique ID of the base Code System.
	 */
	String getExtensionOf();

}