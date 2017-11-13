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
package com.b2international.snowowl.api.codesystem;

import java.util.List;

import com.b2international.snowowl.api.codesystem.domain.ICodeSystem;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;

/**
 * Implementations of this interface allow browsing code system metadata.
 */
public interface ICodeSystemService {

	/**
	 * Lists all registered code systems.
	 * 
	 * @return a list containing all registered code systems, ordered by short name (never {@code null})
	 */
	List<ICodeSystem> getCodeSystems();

	/**
	 * Retrieves a single code system matches the given shortName or object identifier (OID) parameter, if it exists.
	 * 
	 * @param shortNameOrOid the code system short name or OID to look for, eg. "{@code SNOMEDCT}" or "{@code 3.4.5.6.10000}" (may not be {@code null})
	 * 
	 * @return the requested code system
	 * 
	 * @throws CodeSystemNotFoundException if a code system with the given short name or OID is not registered
	 */
	ICodeSystem getCodeSystemById(String shortNameOrOid);
	
}
