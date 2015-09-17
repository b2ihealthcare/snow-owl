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

/**
 * Interface for browsing predicates.
 * 
 *
 * @param <P> the predicate type
 */
public interface IClientPredicateBrowser<P> {

	/**
	 * Returns with a collection of SNOMED&nbsp;CT concept attribute predicate identified by their UUIDs.
	 * @param storageKeys the storageKeys of the concept attribute predicates. Cannot be {@code null}.
	 * @return the lightweight representation of the concept attribute predicates.
	 */
	Collection<P> getPredicate(long... storageKeys);

	/**
	 * Returns with all the predicates.
	 * @return the collection of all concept attribute predicates.
	 */
	Collection<P> getAllPredicates();

	/**
	 * Returns with a collection of MRCM attribute predicates, using the specified parent IDs and reference set ID, if given, to
	 * determine applicability.
	 * 
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept.
	 * @param ruleRefSetId
	 * @return the collection of predicates associated with a SNOMED&nbsp;CT concept.
	 */
	Collection<P> getPredicates(String conceptId, @Nullable String ruleRefSetId);
	
	/**
	 * Returns with a collection of MRCM attribute predicates, using the specified parent IDs and reference set ID, if given, to
	 * determine applicability.
	 * 
	 * @param ruleParentIds
	 * @param ruleRefSetId
	 * @return the collection of predicates associated with a SNOMED&nbsp;CT concept.
	 */
	Collection<P> getPredicates(Iterable<String> ruleParentIds, @Nullable String ruleRefSetId);
	
	/**
	 * Returns with the human readable name of the concrete domain type concept attribute predicate identified by the specified unique name. 
	 * @param dataTypeName the unique camel-case name of the concrete domain type predicate.
	 * @param conceptId the unique ID of the concept. 
	 * @return the human readable name of the concrete domain type predicate.
	 */
	String getDataTypePredicateLabel(String dataTypeName, String conceptId);
	
	/**
	 * Returns with the human readable name of the concrete domain type concept attribute predicate identified by the specified unique name. 
	 * @param dataTypeName the unique camel-case name of the concrete domain type predicate.
	 * @return the human readable name of the concrete domain type predicate.
	 */
	String getDataTypePredicateLabel(String dataTypeName);
}