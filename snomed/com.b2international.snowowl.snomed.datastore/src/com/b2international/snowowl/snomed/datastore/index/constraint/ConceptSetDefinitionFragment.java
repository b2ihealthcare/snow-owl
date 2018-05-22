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
package com.b2international.snowowl.snomed.datastore.index.constraint;

import java.util.stream.Collectors;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableSet;

/**
 * @since 6.5
 */
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME, 
		include = JsonTypeInfo.As.PROPERTY, 
		property = "type")
@JsonSubTypes({ 
	@Type(value = CompositeDefinitionFragment.class, name = "composite"), 
	@Type(value = EnumeratedDefinitionFragment.class, name = "enumerated"), 
	@Type(value = HierarchyDefinitionFragment.class, name = "hierarchy"), 
	@Type(value = ReferenceSetDefinitionFragment.class, name = "referenceSet"), 
	@Type(value = RelationshipDefinitionFragment.class, name = "relationship"), 
})
public abstract class ConceptSetDefinitionFragment extends ConceptModelComponentFragment {

	protected ConceptSetDefinitionFragment(
			final String uuid, 
			final boolean active, 
			final long effectiveTime, 
			final String author) {

		super(uuid, active, effectiveTime, author);
	}

	public static ConceptSetDefinitionFragment from(final ConceptSetDefinition definition) {

		if (definition instanceof CompositeConceptSetDefinition) {
			return new CompositeDefinitionFragment(definition.getUuid(), 
					definition.isActive(), 
					EffectiveTimes.getEffectiveTime(definition.getEffectiveTime()),
					definition.getAuthor(),
					((CompositeConceptSetDefinition) definition).getChildren().stream()
						.map(ConceptSetDefinitionFragment::from)
						.collect(Collectors.toSet()));

		} else if (definition instanceof EnumeratedConceptSetDefinition) {
			return new EnumeratedDefinitionFragment(definition.getUuid(), 
					definition.isActive(), 
					EffectiveTimes.getEffectiveTime(definition.getEffectiveTime()),
					definition.getAuthor(),
					ImmutableSet.copyOf(((EnumeratedConceptSetDefinition) definition).getConceptIds()));

		} else if (definition instanceof HierarchyConceptSetDefinition) {
			return new HierarchyDefinitionFragment(definition.getUuid(), 
					definition.isActive(), 
					EffectiveTimes.getEffectiveTime(definition.getEffectiveTime()),
					definition.getAuthor(),
					((HierarchyConceptSetDefinition) definition).getConceptId(),
					((HierarchyConceptSetDefinition) definition).getInclusionType());

		} else if (definition instanceof ReferenceSetConceptSetDefinition) {
			return new ReferenceSetDefinitionFragment(definition.getUuid(), 
					definition.isActive(), 
					EffectiveTimes.getEffectiveTime(definition.getEffectiveTime()),
					definition.getAuthor(),
					((ReferenceSetConceptSetDefinition) definition).getRefSetIdentifierConceptId());

		} else if (definition instanceof RelationshipConceptSetDefinition) {
			return new RelationshipDefinitionFragment(definition.getUuid(), 
					definition.isActive(), 
					EffectiveTimes.getEffectiveTime(definition.getEffectiveTime()),
					definition.getAuthor(),
					((RelationshipConceptSetDefinition) definition).getTypeConceptId(),
					((RelationshipConceptSetDefinition) definition).getDestinationConceptId());

		} else {
			throw new IllegalArgumentException("Unexpected concept set definition class '" + definition.getClass().getSimpleName() + "'.");
		}
	}
}
