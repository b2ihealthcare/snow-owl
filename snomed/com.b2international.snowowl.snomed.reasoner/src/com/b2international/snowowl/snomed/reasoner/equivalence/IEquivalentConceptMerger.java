/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.equivalence;

import java.util.Collections;
import java.util.Set;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.google.common.collect.Multimap;

/**
 * Implementations of this interface can define a customized way for
 * automatically handling equivalent concept resolution.
 * 
 * @since 6.14
 */
public interface IEquivalentConceptMerger {

	String EXTENSION_POINT = "com.b2international.snowowl.snomed.reasoner.equivalentConceptMerger";

	/**
	 * String prefix on component IDs that indicates that the component has been
	 * created in the equivalent concept merging process. The rest of the ID may not
	 * be a valid SCTID.
	 */
	String PREFIX_NEW = "N_";

	/**
	 * String prefix on component IDs to indicate that the component has changed
	 * during the equivalent concept merging process. The rest of the ID is the
	 * original component SCTID.
	 */
	String PREFIX_UPDATED = "U_";
	
	/**
	 * Adds changes to the specified bulk request builder that effectively merges
	 * equivalent concepts into their corresponding suggested replacement.
	 * 
	 * @param equivalentConcepts the list of equivalent concepts, keyed by the
	 *                           suggested concept to merge into
	 * @param bulkRequestBuilder the bulk request builder in which changes can be
	 *                           registered
	 * @return the ID of the concepts which were merged (and so do not need
	 *         additional inferences)
	 */
	Set<String> merge(Multimap<SnomedConcept, SnomedConcept> equivalentConcepts, 
			BulkRequestBuilder<TransactionContext> bulkRequestBuilder);

	/**
	 * The default implementation does not merge concepts, and returns no SCTIDs to
	 * ignore in the classification saving process.
	 * 
	 * @since 6.14
	 */
	class Default implements IEquivalentConceptMerger {

		@Override
		public Set<String> merge(final Multimap<SnomedConcept, SnomedConcept> equivalentConcepts,
				final BulkRequestBuilder<TransactionContext> bulkRequestBuilder) {
			
			return Collections.emptySet();
		}
	}
}
