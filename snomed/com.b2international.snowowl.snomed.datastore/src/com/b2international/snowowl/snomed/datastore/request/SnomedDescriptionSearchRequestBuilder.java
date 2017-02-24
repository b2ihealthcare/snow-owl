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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Collection;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.datastore.request.SearchRequest;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequest.OptionKey;

/**
 * @since 4.5
 */
public final class SnomedDescriptionSearchRequestBuilder extends SnomedSearchRequestBuilder<SnomedDescriptionSearchRequestBuilder, SnomedDescriptions> {

	SnomedDescriptionSearchRequestBuilder(String repositoryId) {
		super(repositoryId);
	}
	
	public SnomedDescriptionSearchRequestBuilder withFuzzySearch() {
		return addOption(OptionKey.USE_FUZZY, true);
	}

	public SnomedDescriptionSearchRequestBuilder withParsedTerm() {
		return addOption(OptionKey.PARSED_TERM, true);
	}
	
	public SnomedDescriptionSearchRequestBuilder filterByTerm(String termFilter) {
		return addOption(OptionKey.TERM, termFilter == null ? termFilter : termFilter.trim());
	}

	public SnomedDescriptionSearchRequestBuilder filterByConceptEscg(String conceptEscgFilter) {
		return addOption(OptionKey.CONCEPT_ESCG, conceptEscgFilter);
	}
	
	public SnomedDescriptionSearchRequestBuilder filterByConceptId(String conceptIdFilter) {
		return filterByConceptId(Long.valueOf(conceptIdFilter));
	}
	
	public SnomedDescriptionSearchRequestBuilder filterByConceptId(Long conceptIdFilter) {
		return addOption(OptionKey.CONCEPT_ID, conceptIdFilter);
	}
	
	public SnomedDescriptionSearchRequestBuilder filterByConceptId(Collection<Long> conceptIdFilter) {
		return addOption(OptionKey.CONCEPT_ID, Collections3.toImmutableSet(conceptIdFilter));
	}

	public SnomedDescriptionSearchRequestBuilder filterByType(String typeFilter) {
		return addOption(OptionKey.TYPE, typeFilter);
	}

	public SnomedDescriptionSearchRequestBuilder filterByLanguageCodes(Collection<String> languageCodes) {
		return addOption(OptionKey.LANGUAGE, Collections3.toImmutableSet(languageCodes));
	}
	
	public SnomedDescriptionSearchRequestBuilder filterByAcceptability(Acceptability acceptabilityFilter) {
		return addOption(OptionKey.ACCEPTABILITY, acceptabilityFilter);
	}
	
	@Override
	protected SearchRequest<SnomedDescriptions> createSearch() {
		return new SnomedDescriptionSearchRequest();
	}
}
