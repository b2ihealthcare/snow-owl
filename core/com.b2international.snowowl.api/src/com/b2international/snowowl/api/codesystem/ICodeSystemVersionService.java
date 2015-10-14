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
package com.b2international.snowowl.api.codesystem;

import java.util.List;

import com.b2international.snowowl.api.codesystem.domain.ICodeSystemVersion;
import com.b2international.snowowl.api.codesystem.domain.ICodeSystemVersionProperties;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemVersionNotFoundException;

/**
 * Implementations of this interface allow browsing released versions of a code system.
 * 
 * @since 1.0
 */
public interface ICodeSystemVersionService {

	/**
	 * Lists all released code system versions for a single code system with the specified short name, if it exists.
	 * 
	 * @param shortName the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * 
	 * @return the requested code system's released versions, ordered by version ID
	 * 
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 */
	List<ICodeSystemVersion> getCodeSystemVersions(String shortName);

	/**
	 * Retrieves a single released code system version for the specified code system short name and version identifier, if it exists.
	 * 
	 * @param shortName the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * @param version   the code system version identifier to look for, eg. "{@code 2014-07-31}" (may not be {@code null})
	 * 
	 * @return the requested code system version
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier is not registered
	 */
	ICodeSystemVersion getCodeSystemVersionById(String shortName, String version);

	/**
	 * Creates a new version in terminology denoted by the given shortName parameter using the given {@link ICodeSystemVersionProperties} as base
	 * properties.
	 * 
	 * @param shortName  the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * @param properties the base properties of the code system version to create
	 * 
	 * @return the newly created code system version, as returned by {@link #getCodeSystemVersionById(String, String)}
	 */
	ICodeSystemVersion createVersion(String shortName, ICodeSystemVersionProperties properties);
}
