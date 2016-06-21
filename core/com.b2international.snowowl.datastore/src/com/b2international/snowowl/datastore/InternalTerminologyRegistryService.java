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
import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Representation of the internal terminology service.
 * This service assumes implicitly terminology content dependency, hence it's using
 * {@link IBranchPath} arguments unlike {@link TerminologyRegistryService}.
 * 
 */
public interface InternalTerminologyRegistryService {
	
	
	/**Returns with all the {@link ICodeSystem code systems } available in the application from a given branch.*/
	Collection<ICodeSystem> getCodeSystems(final IBranchPath branchPath);
	
	/**Returns with all {@link ICodeSystemVersion code system versions} for a given {@link ICodeSystem code system} given with its short name argument.
	 *<p>Clients should be aware with the followings: not all code system version represents a tag in the application.*/
	Collection<ICodeSystemVersion> getCodeSystemVersions(final IBranchPath branchPath, final String codeSystemShortName);
	
	/**Returns with all {@link ICodeSystemVersion code system versions} from a given repository. Includes the {@link ICodeSystemVersion#INITIAL_STATE} version.
	 *<p>Clients should be aware with the followings: not all code system version represents a tag in the application.*/
	Collection<ICodeSystemVersion> getCodeSystemVersionsFromRepositoryWithInitVersion(final IBranchPath branchPath, final String repositoryUuid);
	
	/**Returns with all {@link ICodeSystemVersion code system versions} from a given repository.
	 *<p>Clients should be aware with the followings: not all code system version represents a tag in the application.*/
	Collection<ICodeSystemVersion> getCodeSystemVersionsFromRepository(final IBranchPath branchPath, final String repositoryUuid);
	
	/**Returns with the {@link ICodeSystem code system} from a particular branch with the unique code system short name argument.*/
	ICodeSystem getCodeSystemByShortName(final IBranchPath branchPath, final String codeSystemShortName);
	
	/**Returns with the code system from a given branch identified by its unique code system OID argument.*/
	ICodeSystem getCodeSystemByOid(final IBranchPath branchPath, final String codeSystemOid);
	
	/**Returns with the mappings between the available application specific terminology component IDs (aka.: tooling IDs)  and the associated {@link ICodeSystem code system}s.
	 *<br>Mapping is one to many.
	 *<p>Consider local terminology concepts where application specific is the same but code system could be multiple.*/
	Map<String, Collection<ICodeSystem>> getTerminologyComponentIdWithMultipleCodeSystemsMap(final IBranchPath branchPath);
	
	/**Returns with the application specific tooling (terminology component) ID for the given unique code system short name.*/
	String getTerminologyComponentIdByShortName(final IBranchPath branchPath, final String codeSystemShortName);
	
	/**Returns with the most recent version ID of the given {@link ICodeSystem code system}.*/
	String getVersionId(final IBranchPath branchPath, final ICodeSystem codeSystem);
}