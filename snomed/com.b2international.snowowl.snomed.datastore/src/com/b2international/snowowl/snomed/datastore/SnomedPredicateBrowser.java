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
package com.b2international.snowowl.snomed.datastore;

import java.util.Collection;
import java.util.Set;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.browser.IPredicateBrowser;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.ConstraintDomain;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;

/**
 * Service for the SNOMED&nbsp;CT MRCM rule based attribute constraints.
 * 
 * @see IPredicateBrowser
 */
public interface SnomedPredicateBrowser extends IPredicateBrowser<PredicateIndexEntry> {

	/**
	 * Returns with the MRCM constraint
	 * 
	 * @param identifierId
	 *            reference set identifier concept ID.
	 * @param branchPath
	 *            the branch path
	 * @return a collection of predicate unique IDs applied to the reference set identified by the specified ID.
	 */
	Collection<String> getRefSetPredicateKeys(final IBranchPath branchPath, final String identifierId);

	/**
	 * Returns constraint domain definitions for the specified constraint storage key.
	 * 
	 * @param branchPath
	 * @param storageKey
	 * @return
	 */
	Set<ConstraintDomain> getConstraintDomains(IBranchPath branchPath, long storageKey);
	
	/**
	 * Returns all constraint domain definitions for the specified branchPath.
	 * 
	 * @param branchPath
	 * @return
	 */
	Set<ConstraintDomain> getAllConstraintDomains(IBranchPath branchPath);
}