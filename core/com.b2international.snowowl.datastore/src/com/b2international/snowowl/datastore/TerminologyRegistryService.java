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
package com.b2international.snowowl.datastore;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Representation of the global terminology registry service. 
 * @deprecated - UNSUPPORTED API
 */
public interface TerminologyRegistryService {
	
	/**Returns with all the {@link ICodeSystem code systems } available in the application from a given branch.*/
	Collection<ICodeSystem> getCodeSystems(final IBranchPathMap branchPathMap);
	
	/**Returns with all {@link ICodeSystemVersion code system versions} for a given {@link ICodeSystem code system} given with its short name argument.
	 *<p>Clients should be aware with the followings: not all code system version represents a tag in the application.*/
	Collection<ICodeSystemVersion> getCodeSystemVersions(final IBranchPathMap branchPathMap, final String codeSystemShortName);
	
	/**Returns with the {@link ICodeSystem code system} from a particular branch with the unique code system short name argument.*/
	ICodeSystem getCodeSystemByShortName(final IBranchPathMap branchPathMap, final String codeSystemShortName);
	
	/**Returns with the code system from a given branch identified by its unique code system OID argument.*/
	ICodeSystem getCodeSystemByOid(final IBranchPathMap branchPathMap, final String codeSystemOid);
	
	/**
	 * Returns with the application specific tooling (terminology component) ID for the given unique code system short name.
	 * @deprecated - UNSUPPORTED API
	 */
	String getTerminologyComponentIdByShortName(final IBranchPathMap branchPathMap, final String codeSystemShortName);
	
	/**Returns with the most recent version ID of the given {@link ICodeSystem code system}.*/
	String getVersionId(final IBranchPathMap branchPathMap, final ICodeSystem codeSystem);
	
	/**
	 * Returns with all versions for each repository which are visible from the HEAD of the MAIN branch. 
	 * Includes the {@link ICodeSystemVersion#INITIAL_STATE 'init'} version as well. Does not contain the 'MAIN' latest 
	 * entry version.
	 * @return a mapping between the repository UUIDs and the values are all available versions for
	 * the repository.
	 */
	Map<String, List<ICodeSystemVersion>> getAllVersion();
}