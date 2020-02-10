/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.store;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;

/**
 * @since 4.5
 */
public final class SnomedDescriptionBuilder extends SnomedComponentBuilder<SnomedDescriptionBuilder, SnomedDescriptionIndexEntry.Builder, SnomedDescriptionIndexEntry> {

	private String caseSignificanceId = Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE;
	private String type;
	private String term;
	private String languageCode = "en";
	private String concept;

	/**
	 * Specifies the type of the new SNOMED CT Description.
	 * 
	 * @param type
	 * @return
	 */
	public SnomedDescriptionBuilder withType(String type) {
		this.type = type;
		return getSelf();
	}

	/**
	 * Specifies the case significance of the new SNOMED CT Description.
	 * 
	 * @param caseSignificanceId
	 * @return
	 */
	public SnomedDescriptionBuilder withCaseSignificanceId(String caseSignificanceId) {
		this.caseSignificanceId = caseSignificanceId;
		return getSelf();
	}

	/**
	 * Specifies the term of the new SNOMED CT Description.
	 * 
	 * @param term
	 * @return
	 */
	public SnomedDescriptionBuilder withTerm(String term) {
		this.term = term;
		return getSelf();
	}

	/**
	 * Specifies the language code of the new SNOMED CT Description.
	 * 
	 * @param languageCode
	 * @return
	 */
	public SnomedDescriptionBuilder withLanguageCode(String languageCode) {
		this.languageCode = languageCode;
		return getSelf();
	}

	/**
	 * Adds the new SNOMED CT Description to the specified existing SNOMED CT Concept.
	 * 
	 * @param concept
	 * @return
	 */
	public SnomedDescriptionBuilder withConcept(String concept) {
		this.concept = concept;
		return getSelf();
	}

	@Override
	public void init(SnomedDescriptionIndexEntry.Builder component, TransactionContext context) {
		super.init(component, context);
		component.caseSignificanceId(context.lookup(caseSignificanceId, SnomedConceptDocument.class).getId());
		component.typeId(context.lookup(type, SnomedConceptDocument.class).getId());
		component.term(term);
		component.languageCode(languageCode);
		if (concept != null) {
			component.conceptId(context.lookup(concept, SnomedConceptDocument.class).getId());
		}
	}

	@Override
	protected SnomedDescriptionIndexEntry.Builder create() {
		return SnomedDescriptionIndexEntry.builder();
	}

}
