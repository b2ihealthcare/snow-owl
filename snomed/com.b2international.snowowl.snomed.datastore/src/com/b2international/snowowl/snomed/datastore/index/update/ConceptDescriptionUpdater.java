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
package com.b2international.snowowl.snomed.datastore.index.update;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;

/**
 * @since 4.3
 */
public class ConceptDescriptionUpdater extends DocumentUpdaterBase<SnomedDocumentBuilder> {

	private Set<String> synonymIds;
	private Concept concept;

	public ConceptDescriptionUpdater(Concept concept, Set<String> synonymIds) {
		super(concept.getId());
		this.concept = concept;
		this.synonymIds = synonymIds;
	}

	@Override
	public void doUpdate(SnomedDocumentBuilder doc) {
		
		for (DescriptionType type : DescriptionType.values()) {
			doc.removeAll(Mappings.textField(type.getFieldName()));
		}
		
		for (final DescriptionInfo descriptionInfo : getActiveDescriptionInfos()) {
			doc.tokenizedField(descriptionInfo.getType().getFieldName(), descriptionInfo.getTerm());
		}
		
	}
	
	private Set<DescriptionInfo> getActiveDescriptionInfos() {
		final Set<DescriptionInfo> results = newHashSet();
		
		for (final Description description : concept.getDescriptions()) {
			if (description.isActive()) {
				results.add(createDescriptionInfo(description));
			}
		}

		return results;
	}

	private DescriptionInfo createDescriptionInfo(final Description description) {
		return new DescriptionInfo(getDescriptionType(description), description.getTerm());
	}

	private DescriptionType getDescriptionType(final Description description) {
		final String typeId = description.getType().getId();
		
		if (Concepts.FULLY_SPECIFIED_NAME.equals(typeId)) {
			return DescriptionType.FULLY_SPECIFIED_NAME;
		} else if (synonymIds.contains(typeId)) {
			return DescriptionType.SYNONYM;
		} else {
			return DescriptionType.OTHER;
		}
	}
	
	public enum DescriptionType {
		FULLY_SPECIFIED_NAME(SnomedIndexBrowserConstants.CONCEPT_FULLY_SPECIFIED_NAME),
		SYNONYM(SnomedIndexBrowserConstants.CONCEPT_SYNONYM),
		OTHER(SnomedIndexBrowserConstants.CONCEPT_OTHER_DESCRIPTION);
		
		private final String fieldName;
		
		private DescriptionType(final String fieldName) {
			this.fieldName = fieldName;
		}
		
		public String getFieldName() {
			return fieldName;
		}
	}
	
	public static final class DescriptionInfo {
		
		private final DescriptionType type;
		private final String term;
		
		public DescriptionInfo(final DescriptionType type, final String term) {
			this.type = type;
			this.term = term;
		}
		
		public String getTerm() {
			return term;
		}
		
		public DescriptionType getType() {
			return type;
		}
		
	}

}
