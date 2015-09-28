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
package com.b2international.snowowl.core.api.browser;

import java.util.Collection;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Interface for browsing predicates.
 * 
 *
 * @param <P> the predicate type
 */
public interface IPredicateBrowser<P> {

	/**
	 * Returns with a collection of SNOMED&nbsp;CT concept attribute predicate identified by their UUIDs.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param storageKeys unique identifiers of the SNOMED&nbsp;CT concept attribute constraints
	 * @return the lightweight representation of the concept attribute predicates.
	 */
	Collection<P> getPredicate(final IBranchPath branchPath, final long...storageKeys);

	/**
	 * Returns with all the predicates.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @return the collection of all concept attribute predicates.
	 */
	Collection<P> getAllPredicates(final IBranchPath branchPath);

	/**
	 * Returns with a collection of MRCM attribute predicates, using the specified parent IDs and reference set ID, if given, to
	 * determine applicability.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept.
	 * @param ruleRefSetId
	 * @return the collection of predicates associated with a SNOMED&nbsp;CT concept.
	 */
	Collection<P> getPredicates(IBranchPath branchPath, String conceptId, @Nullable String ruleRefSetId);
	
	/**
	 * Returns with a collection of MRCM attribute predicates, using the specified parent IDs and reference set ID, if given, to
	 * determine applicability.
	 * 
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param ruleParentIds
	 * @param ruleRefSetId
	 * @return the collection of predicates associated with a SNOMED&nbsp;CT concept.
	 */
	Collection<P> getPredicates(IBranchPath branchPath, Iterable<String> ruleParentIds, @Nullable String ruleRefSetId);

	/**
	 * Returns with a bunch of predicate UUID that are associated with a particular SNOMED&nbsp;CT concept identified by its unique ID.
	 * @param branchPath the branch path reference limiting visibility to a particular branch.
	 * @param conceptId unique ID of the SNOMED&nbsp;CT concept.
	 * @return the collection of predicate keys associated with a SNOMED&nbsp;CT concept.
	 */
	Collection<String> getPredicateKeys(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with the human readable name of the concrete domain type concept attribute predicate identified by the specified unique name.
	 * @param branchPath the branch path reference limiting visibility to a particular branch. 
	 * @param dataTypeName the unique camel-case name of the concrete domain type predicate.
	 * @param conceptId the unique ID of the concept. 
	 * @return the human readable name of the concrete domain type predicate.
	 */
	String getDataTypePredicateLabel(final IBranchPath branchPath, final String dataTypeName, final String conceptId);
	
	/**
	 * Returns with the human readable name of the concrete domain type concept attribute predicate identified by the specified unique name.
	 * @param branchPath the branch path reference limiting visibility to a particular branch. 
	 * @param dataTypeName the unique camel-case name of the concrete domain type predicate.
	 * @return the human readable name of the concrete domain type predicate.
	 */
	String getDataTypePredicateLabel(final IBranchPath branchPath, final String dataTypeName);
}