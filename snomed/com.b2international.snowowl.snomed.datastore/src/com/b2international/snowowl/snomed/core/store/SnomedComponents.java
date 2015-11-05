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
package com.b2international.snowowl.snomed.core.store;

import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;

/**
 * Main entry point of SNOMED CT Component based APIs.
 * 
 * @since 4.5
 */
public class SnomedComponents {

	/**
	 * Creates and returns a new {@link SnomedConceptBuilder SNOMED CT Concept builder}.
	 * 
	 * @return
	 */
	public static SnomedConceptBuilder newConcept() {
		return new SnomedConceptBuilder();
	}

	/**
	 * Creates and returns a new {@link SnomedDescriptionBuilder SNOMED CT Description builder}.
	 * 
	 * @return
	 */
	public static SnomedDescriptionBuilder newDescription() {
		return new SnomedDescriptionBuilder();
	}

	/**
	 * Creates and returns a new {@link SnomedRelationshipBuilder SNOMED CT Relationship builder}
	 * 
	 * @return
	 */
	public static SnomedRelationshipBuilder newRelationship() {
		return new SnomedRelationshipBuilder();
	}

	/**
	 * Creates and returns a new {@link SnomedLanguageReferenceSetMemberBuilder SNOMED CT Language Reference set member builder}
	 * 
	 * @return
	 */
	public static SnomedLanguageReferenceSetMemberBuilder newLanguageMember() {
		return new SnomedLanguageReferenceSetMemberBuilder();
	}

	public static SnomedSimpleReferenceSetBuilder newSimpleTypeReferenceSet() {
		return new SnomedSimpleReferenceSetBuilder();
	}

}
