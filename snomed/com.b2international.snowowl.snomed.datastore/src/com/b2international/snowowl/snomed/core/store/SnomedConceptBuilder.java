/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;

/**
 * @since 4.5
 */
public final class SnomedConceptBuilder extends SnomedComponentBuilder<SnomedConceptBuilder, Concept> {

	private DefinitionStatus definitionStatus = DefinitionStatus.PRIMITIVE;
	private boolean exhaustive = false;
	private final Collection<Description> descriptions = newHashSet();
	private final Collection<Relationship> relationships = newHashSet();

	/**
	 * Specifies the exhaustive flag to use for the new concept.
	 * 
	 * @param exhaustive
	 * @return
	 */
	public final SnomedConceptBuilder withExhaustive(boolean exhaustive) {
		this.exhaustive = exhaustive;
		return getSelf();
	}

	/**
	 * Specifies the {@link DefinitionStatus} to use for the new concept.
	 * 
	 * @param definitionStatus
	 *            - the definition status to use
	 * @return
	 */
	public final SnomedConceptBuilder withDefinitionStatus(DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
		return getSelf();
	}

	/**
	 * Adds the given SNOMED CT Description to the list of descriptions of the new SNOMED CT Concept.
	 * 
	 * @param description
	 *            - the description to add
	 * @return
	 */
	public final SnomedConceptBuilder withDescription(Description description) {
		this.descriptions.add(description);
		return getSelf();
	}

	/**
	 * Adds the given SNOMED CT Relationship to the list of relationships of the new SNOMED CT Concept.
	 * 
	 * @param relationship
	 *            - the relationship to add
	 * @return
	 */
	public final SnomedConceptBuilder withRelationship(Relationship relationship) {
		this.relationships.add(relationship);
		return getSelf();
	}

	@Override
	protected Concept create() {
		return SnomedFactory.eINSTANCE.createConcept();
	}

	@Override
	public void init(Concept component, TransactionContext context) {
		super.init(component, context);
		component.setDefinitionStatus(context.lookup(definitionStatus.getConceptId(), Concept.class));
		component.setExhaustive(exhaustive);
		component.getDescriptions().addAll(descriptions);
		component.getOutboundRelationships().addAll(relationships);
	}

}
