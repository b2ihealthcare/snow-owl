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

import java.util.List;
import java.util.Locale;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.snomed.core.domain.SearchKind;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.5
 */
public final class SnomedConceptSearchRequestBuilder extends SearchRequestBuilder<SnomedConceptSearchRequestBuilder, SnomedConcepts> {

	SnomedConceptSearchRequestBuilder(String repositoryId) {
		super(repositoryId);
	}

	public final SnomedConceptSearchRequestBuilder filterByPt(String term) {
		return addSearchKind(SearchKind.PT, term);
	}

	public final SnomedConceptSearchRequestBuilder filterByFsn(String term) {
		return addSearchKind(SearchKind.FSN, term);
	}

	public final SnomedConceptSearchRequestBuilder filterBySyn(String term) {
		return addSearchKind(SearchKind.SYN, term);
	}

	public final SnomedConceptSearchRequestBuilder filterByOther(String term) {
		return addSearchKind(SearchKind.OTHER, term);
	}

	public final SnomedConceptSearchRequestBuilder filterByEscg(String expression) {
		return addSearchKind(SearchKind.ESCG, expression);
	}

	public SnomedConceptSearchRequestBuilder filterByActive(Boolean active) {
		return addSearchKind(SearchKind.ACTIVE, active);
	}

	public SnomedConceptSearchRequestBuilder setExpand(List<String> expand) {
		if (!CompareUtils.isEmpty(expand)) {
			addOption(SnomedConceptSearchRequest.OPTION_EXPAND, ImmutableList.copyOf(expand));
		}
		return getSelf();
	}

	public SnomedConceptSearchRequestBuilder setLocales(List<Locale> locales) {
		if (!CompareUtils.isEmpty(locales)) {
			addOption(SnomedConceptSearchRequest.OPTION_LOCALES, ImmutableList.copyOf(locales));
		}
		return getSelf();
	}

	private SnomedConceptSearchRequestBuilder addSearchKind(SearchKind searchKind, Object value) {
		if (!CompareUtils.isEmpty(value)) {
			addOption(searchKind.name(), value);
		}
		return getSelf();
	}

	@Override
	protected SearchRequest<SnomedConcepts> create() {
		return new SnomedConceptSearchRequest();
	}
}
