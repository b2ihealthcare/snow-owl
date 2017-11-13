/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.DependencyPredicate;
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
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
	public <T extends ConceptModelComponent> String getHumanReadableRendering(T component) {
		return getHumanReadableRendering(component, DEFAULT_LENGTH_LIMIT);
	}

	public <T extends ConceptModelComponent> String getHumanReadableRendering(T component, int limit) {
		String fullText = "";
		
		if (component instanceof ReferenceSetConceptSetDefinition) {
			fullText = getReferenceSetConceptSetDefinitionRendering((ReferenceSetConceptSetDefinition)component);
		} else if (component instanceof RelationshipConceptSetDefinition) {
			fullText = getRelationshipConceptSetDefinitionRendering((RelationshipConceptSetDefinition)component);
		} else if (component instanceof HierarchyConceptSetDefinition) {
			fullText = getHierarchyConceptSetDefinitionRendering((HierarchyConceptSetDefinition)component);
		} else if (component instanceof EnumeratedConceptSetDefinition) {
			fullText = getEnumeratedConceptSetDefinitionRendering((EnumeratedConceptSetDefinition)component);
		} else if (component instanceof CompositeConceptSetDefinition) {
			fullText = getCompositeConceptSetDefinitionRendering((CompositeConceptSetDefinition)component, limit);
		} else if (component instanceof RelationshipPredicate) {
			fullText = getRelationshipPredicateRendering((RelationshipPredicate)component, limit);
		} else if (component instanceof ConcreteDomainElementPredicate) {
			fullText = getConcreteDomainElementPredicateRendering((ConcreteDomainElementPredicate)component); 
		} else if (component instanceof CardinalityPredicate) {
			fullText = getCardinalityPredicateRendering((CardinalityPredicate)component, limit);
		} else if (component instanceof DependencyPredicate) {
			fullText = getDependencyPredicateRendering((DependencyPredicate)component, limit);
		} else if (component instanceof DescriptionPredicate) {
			fullText = getDescriptionPredicateRendering((DescriptionPredicate)component);
		} else if (component instanceof AttributeConstraint) {
			fullText = getAttributeConstraintRendering((AttributeConstraint)component, limit);
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
	
	private String getReferenceSetConceptSetDefinitionRendering(ReferenceSetConceptSetDefinition referenceSetConceptSetDefinition) {
		return "^" + getLabel(referenceSetConceptSetDefinition.getRefSetIdentifierConceptId());
	}
	
	private String getRelationshipConceptSetDefinitionRendering(RelationshipConceptSetDefinition relationshipConceptSetDefinition) {
		StringBuilder builder = new StringBuilder();
		builder.append(getLabel(relationshipConceptSetDefinition.getTypeConceptId()));
		builder.append(" ");
		builder.append(getLabel(relationshipConceptSetDefinition.getDestinationConceptId()));
		return builder.toString();
	}
	
	private String getHierarchyConceptSetDefinitionRendering(HierarchyConceptSetDefinition hierarchyConceptSetDefinition) {
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
		builder.append(getLabel(hierarchyConceptSetDefinition.getFocusConceptId()));
		return builder.toString();
	}
	
	private String getEnumeratedConceptSetDefinitionRendering(EnumeratedConceptSetDefinition enumeratedConceptSetDefinition) {
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
	
	private String getCompositeConceptSetDefinitionRendering(CompositeConceptSetDefinition compositeConceptSetDefinition, int limit) {
		StringBuilder builder = new StringBuilder();
		Iterator<ConceptSetDefinition> iterator = compositeConceptSetDefinition.getChildren().iterator();
		while (iterator.hasNext()) {
			ConceptSetDefinition next = iterator.next();
			builder.append(getHumanReadableRendering(next, limit));
			if (iterator.hasNext())
				builder.append(", ");
		}
		return builder.toString();
	}
	
	private String getRelationshipPredicateRendering(RelationshipPredicate relationshipPredicate, int limit) {
		StringBuilder builder = new StringBuilder();
		builder.append(getHumanReadableRendering(relationshipPredicate.getAttribute(), limit));
		builder.append(" ");
		builder.append(getHumanReadableRendering(relationshipPredicate.getRange(), limit));
		return builder.toString();
	}
	
	private String getDescriptionPredicateRendering(DescriptionPredicate descriptionPredicate) {
		return getLabel(descriptionPredicate.getTypeId());
	}
	
	private String getConcreteDomainElementPredicateRendering(ConcreteDomainElementPredicate concreteDomainElementPredicate) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(concreteDomainElementPredicate.getName());
		builder.append(" [");
		builder.append(concreteDomainElementPredicate.getType());
		builder.append("]");
		return builder.toString();
	}
	
	private String getCardinalityPredicateRendering(CardinalityPredicate cardinalityPredicate, int limit) {
		StringBuilder builder = new StringBuilder();
		builder.append(cardinalityPredicate.getMinCardinality());
		builder.append("..");
		builder.append(renderMaxCardinality(cardinalityPredicate.getMaxCardinality()));
		builder.append(" ");
		builder.append(getHumanReadableRendering(cardinalityPredicate.getPredicate(), limit));
		return builder.toString();
	}
	
	private String getDependencyPredicateRendering(DependencyPredicate dependencyPredicate, int limit) {
		StringBuilder builder = new StringBuilder();
		Iterator<ConceptModelPredicate> iterator = dependencyPredicate.getChildren().iterator();
		while (iterator.hasNext()) {
			ConceptModelPredicate next = iterator.next();
			builder.append(getHumanReadableRendering(next, limit));
			if (iterator.hasNext())
				builder.append(", ");
		}
		return builder.toString();
	}
	
	private String getAttributeConstraintRendering(AttributeConstraint attributeConstraint, int limit) {
		return String.format("%s %s", 
				getHumanReadableRendering(attributeConstraint.getDomain(), limit), 
				getHumanReadableRendering(attributeConstraint.getPredicate(), limit));
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
