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
package com.b2international.snowowl.snomed.datastore.server.request;

import com.b2international.snowowl.snomed.core.domain.SearchKind;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
public final class SnomedConceptSearchRequestBuilder extends SearchRequestBuilder<SnomedConceptSearchRequestBuilder, SnomedConcepts> {
	
	SnomedConceptSearchRequestBuilder(String repositoryId) {
		super(repositoryId);
	}

	public final SnomedConceptSearchRequestBuilder filterByLabel(String label) {
		if (!Strings.isNullOrEmpty(label)) {
			addOption(SearchKind.LABEL.name(), label);
		}
		return getSelf();
	}
	
	public final SnomedConceptSearchRequestBuilder filterByEscg(String escg) {
		if (!Strings.isNullOrEmpty(escg)) {
			addOption(SearchKind.ESCG.name(), escg);
		}
		return getSelf();
	}

	@Override
	protected SearchRequest<SnomedConcepts> create() {
		return new SnomedConceptSearchRequest();
	}
	
}
