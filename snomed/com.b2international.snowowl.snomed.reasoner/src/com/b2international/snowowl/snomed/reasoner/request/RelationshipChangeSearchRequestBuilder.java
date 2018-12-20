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
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChanges;
import com.b2international.snowowl.snomed.reasoner.request.RelationshipChangeSearchRequest.OptionKey;

/**
 * @since 7.0
 */
public final class RelationshipChangeSearchRequestBuilder
		extends SearchResourceRequestBuilder<RelationshipChangeSearchRequestBuilder, RepositoryContext, RelationshipChanges> 
		implements RepositoryIndexRequestBuilder<RelationshipChanges> {

	RelationshipChangeSearchRequestBuilder() {}

	public RelationshipChangeSearchRequestBuilder filterByClassificationId(final String classificationId) {
		return addOption(OptionKey.CLASSIFICATION_ID, classificationId);
	}

	public RelationshipChangeSearchRequestBuilder filterByClassificationId(final Iterable<String> classificationIds) {
		return addOption(OptionKey.CLASSIFICATION_ID, classificationIds);
	}

	public RelationshipChangeSearchRequestBuilder filterBySourceId(final String sourceId) {
		return addOption(OptionKey.SOURCE_ID, sourceId);
	}

	public RelationshipChangeSearchRequestBuilder filterBySourceId(final Iterable<String> sourceIds) {
		return addOption(OptionKey.SOURCE_ID, sourceIds);
	}

	public RelationshipChangeSearchRequestBuilder filterByDestinationId(final String destinationId) {
		return addOption(OptionKey.DESTINATION_ID, destinationId);
	}
	
	public RelationshipChangeSearchRequestBuilder filterByDestinationId(final Iterable<String> destinationIds) {
		return addOption(OptionKey.DESTINATION_ID, destinationIds);
	}
	
	@Override
	protected SearchResourceRequest<RepositoryContext, RelationshipChanges> createSearch() {
		return new RelationshipChangeSearchRequest();
	}
}
