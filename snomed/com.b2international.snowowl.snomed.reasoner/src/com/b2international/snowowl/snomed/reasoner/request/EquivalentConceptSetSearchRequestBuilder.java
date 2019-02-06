/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.request;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.datastore.request.RepositoryIndexRequestBuilder;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSets;
import com.b2international.snowowl.snomed.reasoner.request.EquivalentConceptSetSearchRequest.OptionKey;

/**
 * @since 7.0
 */
public final class EquivalentConceptSetSearchRequestBuilder
		extends SearchResourceRequestBuilder<EquivalentConceptSetSearchRequestBuilder, RepositoryContext, EquivalentConceptSets> 
		implements RepositoryIndexRequestBuilder<EquivalentConceptSets> {

	EquivalentConceptSetSearchRequestBuilder() {}

	public EquivalentConceptSetSearchRequestBuilder filterByClassificationId(final String classificationId) {
		return addOption(OptionKey.CLASSIFICATION_ID, classificationId);
	}

	public EquivalentConceptSetSearchRequestBuilder filterByClassificationId(final Iterable<String> classificationIds) {
		return addOption(OptionKey.CLASSIFICATION_ID, classificationIds);
	}

	public EquivalentConceptSetSearchRequestBuilder filterByConceptId(final String conceptId) {
		return addOption(OptionKey.CONCEPT_ID, conceptId);
	}

	public EquivalentConceptSetSearchRequestBuilder filterByConceptId(final Iterable<String> conceptIds) {
		return addOption(OptionKey.CONCEPT_ID, conceptIds);
	}

	@Override
	protected SearchResourceRequest<RepositoryContext, EquivalentConceptSets> createSearch() {
		return new EquivalentConceptSetSearchRequest();
	}
}
