/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.mrcm;

import java.util.Iterator;
import java.util.Map;

import com.b2international.snowowl.snomed.core.domain.constraint.SnomedCardinalityPredicate;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedCompositeDefinition;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConceptSetDefinition;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConcreteDomainPredicate;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedDependencyPredicate;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedDescriptionPredicate;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedEnumeratedDefinition;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedHierarchyDefinition;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedPredicate;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedReferenceSetDefinition;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedRelationshipDefinition;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedRelationshipPredicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * Utility class to render MRCM objects into a more-or-less human readable String.
 */
public class ConceptModelComponentRenderer {
	private static int DEFAULT_LENGTH_LIMIT = 60;
	
	private final Map<String, String> componentLabelMap;
	
	public ConceptModelComponentRenderer(final String branch) {
		this(branch, ImmutableMap.<String, String>of());
	}
	
	public ConceptModelComponentRenderer(final String branch, Map<String, String> componentLabelMap) {
		this.componentLabelMap = componentLabelMap;
	}
	
	/**
	 * @param component the component to render
	 * @return the human-readable rendering of the MRCM component
	 */
	public String getHumanReadableRendering(Object component) {
		return getHumanReadableRendering(component, DEFAULT_LENGTH_LIMIT);
	}

	public String getHumanReadableRendering(Object component, int limit) {
		String fullText = "";
		
		if (component instanceof SnomedReferenceSetDefinition) {
			fullText = getReferenceSetConceptSetDefinitionRendering((SnomedReferenceSetDefinition)component);
		} else if (component instanceof SnomedRelationshipDefinition) {
			fullText = getRelationshipConceptSetDefinitionRendering((SnomedRelationshipDefinition)component);
		} else if (component instanceof SnomedHierarchyDefinition) {
			fullText = getHierarchyConceptSetDefinitionRendering((SnomedHierarchyDefinition)component);
		} else if (component instanceof SnomedEnumeratedDefinition) {
			fullText = getEnumeratedConceptSetDefinitionRendering((SnomedEnumeratedDefinition)component);
		} else if (component instanceof SnomedCompositeDefinition) {
			fullText = getCompositeConceptSetDefinitionRendering((SnomedCompositeDefinition)component, limit);
		} else if (component instanceof SnomedRelationshipPredicate) {
			fullText = getRelationshipPredicateRendering((SnomedRelationshipPredicate)component, limit);
		} else if (component instanceof SnomedConcreteDomainPredicate) {
			fullText = getConcreteDomainElementPredicateRendering((SnomedConcreteDomainPredicate)component, limit); 
		} else if (component instanceof SnomedCardinalityPredicate) {
			fullText = getCardinalityPredicateRendering((SnomedCardinalityPredicate)component, limit);
		} else if (component instanceof SnomedDependencyPredicate) {
			fullText = getDependencyPredicateRendering((SnomedDependencyPredicate)component, limit);
		} else if (component instanceof SnomedDescriptionPredicate) {
			fullText = getDescriptionPredicateRendering((SnomedDescriptionPredicate)component);
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		if (fullText.length() > limit) {
			stringBuilder.append(fullText.substring(0, limit - 3));
			stringBuilder.append("...");
		} else {
			stringBuilder.append(fullText);
		}
		
		return stringBuilder.toString();
	}
	
	private String getReferenceSetConceptSetDefinitionRendering(SnomedReferenceSetDefinition referenceSetConceptSetDefinition) {
		return "^" + getLabel(referenceSetConceptSetDefinition.getRefSetId());
	}
	
	private String getRelationshipConceptSetDefinitionRendering(SnomedRelationshipDefinition relationshipConceptSetDefinition) {
		StringBuilder builder = new StringBuilder();
		builder.append(getLabel(relationshipConceptSetDefinition.getTypeId()));
		builder.append(" ");
		builder.append(getLabel(relationshipConceptSetDefinition.getDestinationId()));
		return builder.toString();
	}
	
	private String getHierarchyConceptSetDefinitionRendering(SnomedHierarchyDefinition hierarchyConceptSetDefinition) {
		StringBuilder builder = new StringBuilder();
		switch (hierarchyConceptSetDefinition.getInclusionType()) {
		case SELF:
			builder.append("=");
			break;
		case SELF_OR_DESCENDANT:
			builder.append("<<");
			break;
		case DESCENDANT:
			builder.append("<");
			break;
		default:
			break;
		}
		builder.append(getLabel(hierarchyConceptSetDefinition.getConceptId()));
		return builder.toString();
	}
	
	private String getEnumeratedConceptSetDefinitionRendering(SnomedEnumeratedDefinition enumeratedConceptSetDefinition) {
		StringBuilder builder = new StringBuilder();
		Iterator<String> iterator = enumeratedConceptSetDefinition.getConceptIds().iterator();
		while (iterator.hasNext()) {
			String next = iterator.next();
			builder.append(getLabel(next));
			if (iterator.hasNext())
				builder.append(", ");
		}
		return builder.toString();
	}
	
	private String getCompositeConceptSetDefinitionRendering(SnomedCompositeDefinition compositeConceptSetDefinition, int limit) {
		StringBuilder builder = new StringBuilder();
		Iterator<SnomedConceptSetDefinition> iterator = compositeConceptSetDefinition.getChildren().iterator();
		while (iterator.hasNext()) {
			SnomedConceptSetDefinition next = iterator.next();
			builder.append(getHumanReadableRendering(next, limit));
			if (iterator.hasNext())
				builder.append(", ");
		}
		return builder.toString();
	}
	
	private String getRelationshipPredicateRendering(SnomedRelationshipPredicate relationshipPredicate, int limit) {
		StringBuilder builder = new StringBuilder();
		builder.append(getHumanReadableRendering(relationshipPredicate.getAttribute(), limit));
		builder.append(" ");
		builder.append(getHumanReadableRendering(relationshipPredicate.getRange(), limit));
		return builder.toString();
	}
	
	private String getDescriptionPredicateRendering(SnomedDescriptionPredicate descriptionPredicate) {
		return getLabel(descriptionPredicate.getTypeId());
	}
	
	private String getConcreteDomainElementPredicateRendering(SnomedConcreteDomainPredicate concreteDomainElementPredicate, int limit) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(getHumanReadableRendering(concreteDomainElementPredicate.getAttribute(), limit));
		builder.append(" [");
		builder.append(concreteDomainElementPredicate.getRange());
		builder.append("]");
		return builder.toString();
	}
	
	private String getCardinalityPredicateRendering(SnomedCardinalityPredicate cardinalityPredicate, int limit) {
		StringBuilder builder = new StringBuilder();
		builder.append(cardinalityPredicate.getMinCardinality());
		builder.append("..");
		builder.append(renderMaxCardinality(cardinalityPredicate.getMaxCardinality()));
		builder.append(" ");
		builder.append(getHumanReadableRendering(cardinalityPredicate.getPredicate(), limit));
		return builder.toString();
	}
	
	private String getDependencyPredicateRendering(SnomedDependencyPredicate dependencyPredicate, int limit) {
		StringBuilder builder = new StringBuilder();
		Iterator<SnomedPredicate> iterator = dependencyPredicate.getChildren().iterator();
		while (iterator.hasNext()) {
			SnomedPredicate next = iterator.next();
			builder.append(getHumanReadableRendering(next, limit));
			if (iterator.hasNext())
				builder.append(", ");
		}
		return builder.toString();
	}
	
	private String getLabel(String conceptId) {
		if (Strings.isNullOrEmpty(conceptId)) {
			return "";
		} else {
			return componentLabelMap.getOrDefault(conceptId, conceptId);
		}
	}
	
	private String renderMaxCardinality(int max) {
		return max == -1 ? "*" : Integer.toString(max);
	}
}
