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
package com.b2international.snowowl.snomed.mrcm.core.renderer;

import java.util.Iterator;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.core.api.INameProviderFactory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
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

/**
 * Utility class to render MRCM objects into a more-or-less human readable String.
 * 
 */
public class ConceptModelComponentRenderer {
	
	private static int HUMAN_READABLE_RENDERING_LENGTH_LIMIT = 60;
	
	/**
	 * @param component the component to render
	 * @return the human-readable rendering of the MRCM component
	 */
	public static <T extends ConceptModelComponent> String getHumanReadableRendering(T component) {
		String fullText = "";
		if (component instanceof ReferenceSetConceptSetDefinition) {
			fullText = getHumanReadableRendering((ReferenceSetConceptSetDefinition)component);
		} else if (component instanceof RelationshipConceptSetDefinition) {
			fullText = getHumanReadableRendering((RelationshipConceptSetDefinition)component);
		} else if (component instanceof HierarchyConceptSetDefinition) {
			fullText = getHumanReadableRendering((HierarchyConceptSetDefinition)component);
		} else if (component instanceof EnumeratedConceptSetDefinition) {
			fullText = getHumanReadableRendering((EnumeratedConceptSetDefinition)component);
		} else if (component instanceof CompositeConceptSetDefinition) {
			fullText = getHumanReadableRendering((CompositeConceptSetDefinition)component);
		} else if (component instanceof RelationshipPredicate) {
			fullText = getHumanReadableRendering((RelationshipPredicate)component);
		} else if (component instanceof ConcreteDomainElementPredicate) {
			fullText = getHumanReadableRendering((ConcreteDomainElementPredicate)component); 
		} else if (component instanceof CardinalityPredicate) {
			fullText = getHumanReadableRendering((CardinalityPredicate)component);
		} else if (component instanceof DependencyPredicate) {
			fullText = getHumanReadableRendering((DependencyPredicate)component);
		} else if (component instanceof DescriptionPredicate) {
			fullText = getHumanReadableRendering((DescriptionPredicate)component);
		} else if (component instanceof AttributeConstraint) {
			fullText = getHumanReadableRendering((AttributeConstraint)component);
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		if (fullText.length() > HUMAN_READABLE_RENDERING_LENGTH_LIMIT) {
			stringBuilder.append(fullText.substring(0, HUMAN_READABLE_RENDERING_LENGTH_LIMIT - 3));
			stringBuilder.append("...");
		} else {
			stringBuilder.append(fullText);
		}
		return stringBuilder.toString();
	}
	
	private static String getHumanReadableRendering(ReferenceSetConceptSetDefinition referenceSetConceptSetDefinition) {
		return "^" + renderConcept(referenceSetConceptSetDefinition.getRefSetIdentifierConceptId());
	}
	
	private static String getHumanReadableRendering(RelationshipConceptSetDefinition relationshipConceptSetDefinition) {
		StringBuilder builder = new StringBuilder();
		builder.append(renderConcept(relationshipConceptSetDefinition.getTypeConceptId()));
		builder.append(" ");
		builder.append(renderConcept(relationshipConceptSetDefinition.getDestinationConceptId()));
		return builder.toString();
	}
	
	private static String getHumanReadableRendering(HierarchyConceptSetDefinition hierarchyConceptSetDefinition) {
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
		builder.append(renderConcept(hierarchyConceptSetDefinition.getFocusConceptId()));
		return builder.toString();
	}
	
	private static String getHumanReadableRendering(EnumeratedConceptSetDefinition enumeratedConceptSetDefinition) {
		StringBuilder builder = new StringBuilder();
		Iterator<String> iterator = enumeratedConceptSetDefinition.getConceptIds().iterator();
		while (iterator.hasNext()) {
			String next = iterator.next();
			builder.append(renderConcept(next));
			if (iterator.hasNext())
				builder.append(", ");
		}
		return builder.toString();
	}
	
	private static String getHumanReadableRendering(CompositeConceptSetDefinition compositeConceptSetDefinition) {
		StringBuilder builder = new StringBuilder();
		Iterator<ConceptSetDefinition> iterator = compositeConceptSetDefinition.getChildren().iterator();
		while (iterator.hasNext()) {
			ConceptSetDefinition next = iterator.next();
			builder.append(getHumanReadableRendering(next));
			if (iterator.hasNext())
				builder.append(", ");
		}
		return builder.toString();
	}
	
	private static String getHumanReadableRendering(RelationshipPredicate relationshipPredicate) {
		StringBuilder builder = new StringBuilder();
		builder.append(getHumanReadableRendering(relationshipPredicate.getAttribute()));
		builder.append(" ");
		builder.append(getHumanReadableRendering(relationshipPredicate.getRange()));
		return builder.toString();
	}
	
	private static String getHumanReadableRendering(DescriptionPredicate descriptionPredicate) {
		return renderConcept(descriptionPredicate.getTypeId());
	}
	
	private static String getHumanReadableRendering(ConcreteDomainElementPredicate concreteDomainElementPredicate) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(concreteDomainElementPredicate.getName());
		builder.append(" [");
		builder.append(concreteDomainElementPredicate.getType());
		builder.append("]");
		return builder.toString();
	}
	
	private static String getHumanReadableRendering(CardinalityPredicate cardinalityPredicate) {
		StringBuilder builder = new StringBuilder();
		builder.append(cardinalityPredicate.getMinCardinality());
		builder.append("...");
		builder.append(renderMaxCardinality(cardinalityPredicate.getMaxCardinality()));
		builder.append(" ");
		builder.append(getHumanReadableRendering(cardinalityPredicate.getPredicate()));
		return builder.toString();
	}
	
	private static String getHumanReadableRendering(DependencyPredicate dependencyPredicate) {
		StringBuilder builder = new StringBuilder();
		Iterator<ConceptModelPredicate> iterator = dependencyPredicate.getChildren().iterator();
		while (iterator.hasNext()) {
			ConceptModelPredicate next = iterator.next();
			builder.append(getHumanReadableRendering(next));
			if (iterator.hasNext())
				builder.append(", ");
		}
		return builder.toString();
	}
	
	private static String getHumanReadableRendering(AttributeConstraint attributeConstraint) {
		StringBuilder builder = new StringBuilder();
		builder.append(getHumanReadableRendering(attributeConstraint.getDomain()));
		builder.append(" ");
		builder.append(getHumanReadableRendering(attributeConstraint.getPredicate()));
		return builder.toString();
	}
	
	private static String renderConcept(String conceptId) {
		INameProviderFactory conceptNameProviderFactory = CoreTerminologyBroker.getInstance().getNameProviderFactory(SnomedTerminologyComponentConstants.CONCEPT);
		IComponentNameProvider conceptNameProvider = conceptNameProviderFactory.getNameProvider();
		if (!StringUtils.isEmpty(conceptId)) {
			return conceptNameProvider.getComponentLabel(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), conceptId);
		} else {
			return "";
		}
	}
	
	private static String renderMaxCardinality(int max) {
		return max == -1 ? "*" : Integer.toString(max);
	}
}