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
package com.b2international.snowowl.snomed.datastore;

import java.util.Set;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.util.MrcmSwitch;

/**
 * Utility class for concept model predicates and its subclasses.
 *
 */
public abstract class PredicateUtils {

	public static final String REFSET_PREDICATE_KEY_PREFIX = "RefSet";
	public static final String PREDICATE_SEPARATOR = "#";
	
	/**
	 * Descendant. {@value}
	 */
	public static final String DESCENDANT = "<";
	
	/**
	 * Self or descendant. {@value}
	 */
	public static final String SELF_OR_DESCENDANT = "<<";
	
	/**
	 * Or. {@value}
	 */
	public static final String OR = "OR";

	public static void collectDomainIds(ConceptSetDefinition domain, final Set<String> selfIds, final Set<String> descendantIds, 
			final Set<String> refSetIds, final Set<String> relationships) {
		new MrcmSwitch<ConceptSetDefinition>() {
			@Override
			public ConceptSetDefinition caseHierarchyConceptSetDefinition(HierarchyConceptSetDefinition domain) {
				final String focusConceptId = domain.getFocusConceptId();
				switch (domain.getInclusionType()) {
					case SELF:
						selfIds.add(focusConceptId);
						break;
					case DESCENDANT:
						descendantIds.add(focusConceptId);
						break;
					case SELF_OR_DESCENDANT:
						selfIds.add(focusConceptId);
						descendantIds.add(focusConceptId);
						break;
					default: throw new UnsupportedOperationException(domain.getInclusionType().toString());
				}
				return domain;
			}
			
			@Override
			public ConceptSetDefinition caseEnumeratedConceptSetDefinition(EnumeratedConceptSetDefinition domain) {
				selfIds.addAll(domain.getConceptIds());
				return domain;
			}
			
			@Override
			public ConceptSetDefinition caseReferenceSetConceptSetDefinition(ReferenceSetConceptSetDefinition domain) {
				refSetIds.add(domain.getRefSetIdentifierConceptId());
				return domain;
			}
			
			@Override
			public ConceptSetDefinition caseCompositeConceptSetDefinition(CompositeConceptSetDefinition domain) {
				for (ConceptSetDefinition childDomain : domain.getChildren()) {
					collectDomainIds(childDomain, selfIds, descendantIds, refSetIds, relationships);
				}
				return domain;
			}
			
			@Override
			public ConceptSetDefinition caseRelationshipConceptSetDefinition(RelationshipConceptSetDefinition domain) {
				relationships.add(SnomedConstraintDocument.relationshipDomainOf(domain.getTypeConceptId(), domain.getDestinationConceptId()));
				return domain;
			};
		}.doSwitch(domain);
	}
	
	/**
	 * Builds an ESCG expression that represents the specified concept set definition.
	 * <br>This method is a union of all {@link ConceptSetProcessor#getEscgExpression()} method behavior.
	 * 
	 * @param conceptSetDefinition the concept set definition.
	 * @return built expression that can be processed as an ESCG expression.
	 * 
	 * 
	 */
	public static String toEclExpression(final ConceptSetDefinition conceptSetDefinition) {
		if (conceptSetDefinition instanceof HierarchyConceptSetDefinition) {
			final HierarchyConceptSetDefinition hierarchyConceptSetDefinition = (HierarchyConceptSetDefinition)conceptSetDefinition;
			
			switch (hierarchyConceptSetDefinition.getInclusionType()) {
				
				case SELF: 
					return hierarchyConceptSetDefinition.getFocusConceptId();
					
				case DESCENDANT: 
					return new StringBuilder(DESCENDANT).append(hierarchyConceptSetDefinition.getFocusConceptId()).toString();
					
				case SELF_OR_DESCENDANT: 
					return new StringBuilder(SELF_OR_DESCENDANT).append(hierarchyConceptSetDefinition.getFocusConceptId()).toString();
				
				default: 
					throw new IllegalArgumentException("Unknown inclusion type: " + hierarchyConceptSetDefinition.getInclusionType());
			}
		} else if (conceptSetDefinition instanceof ReferenceSetConceptSetDefinition) {
			final ReferenceSetConceptSetDefinition referenceSetConceptSetDefinition = (ReferenceSetConceptSetDefinition) conceptSetDefinition;
			return new StringBuilder("^").append(referenceSetConceptSetDefinition.getRefSetIdentifierConceptId()).toString();
		} else if (conceptSetDefinition instanceof RelationshipConceptSetDefinition) {
			final RelationshipConceptSetDefinition relationshipConceptSetDefinition = (RelationshipConceptSetDefinition) conceptSetDefinition;

			final StringBuilder resultBuilder = new StringBuilder(SELF_OR_DESCENDANT);
			resultBuilder.append(Concepts.ROOT_CONCEPT);
			resultBuilder.append(":");
			resultBuilder.append(relationshipConceptSetDefinition.getTypeConceptId());
			resultBuilder.append("=");
			resultBuilder.append(relationshipConceptSetDefinition.getDestinationConceptId());
			return resultBuilder.toString();
		} else if (conceptSetDefinition instanceof EnumeratedConceptSetDefinition) {
			
			final EnumeratedConceptSetDefinition enumeratedConceptSetDefinition = (EnumeratedConceptSetDefinition) conceptSetDefinition;
			final StringBuilder sb = new StringBuilder();
			
			for (final String conceptId : enumeratedConceptSetDefinition.getConceptIds()) {
				if (sb.length() > 0) {
					sb.append(OR);
				}
				sb.append(conceptId);
			}
			return sb.toString();
			
		} else if (conceptSetDefinition instanceof CompositeConceptSetDefinition) {
			
			final CompositeConceptSetDefinition compositeConceptSetDefinition = (CompositeConceptSetDefinition) conceptSetDefinition;
			final StringBuilder sb = new StringBuilder();
			for (final ConceptSetDefinition child : compositeConceptSetDefinition.getChildren()) {
				if (sb.length() > 0) {
					sb.append(OR);
				}
				sb.append(toEclExpression(child));
			}
			return sb.toString();
			
		}
		
		throw new IllegalArgumentException("Unexpected concept set definition: " + conceptSetDefinition);
		
	}
	
//	public static Set<ConstraintDomain> processConstraintDomain(final long storageKey, final ConceptSetDefinition domain) {
//
//		final Set<ConstraintDomain> $ = Sets.newHashSet();
//		
//		if (domain instanceof HierarchyConceptSetDefinition) {
//			
//			final HierarchyConceptSetDefinition hierarchyConceptSetDefinition = (HierarchyConceptSetDefinition) domain;
//			final Long focusConceptId = Long.valueOf(hierarchyConceptSetDefinition.getFocusConceptId());
//			final HierarchyInclusionType inclusionType = hierarchyConceptSetDefinition.getInclusionType();
//			$.add(new ConstraintDomain(focusConceptId, inclusionType.name(), storageKey));
//			
//		} else if (domain instanceof ReferenceSetConceptSetDefinition) {
//			
//			final ReferenceSetConceptSetDefinition referenceSetConceptSetDefinition = (ReferenceSetConceptSetDefinition) domain;
//			final long refSetIdentifierConceptId = Long.valueOf(referenceSetConceptSetDefinition.getRefSetIdentifierConceptId());
//			$.add(new ConstraintDomain(refSetIdentifierConceptId, REFSET_PREDICATE_KEY_PREFIX, storageKey));
//			
//		} else if (domain instanceof RelationshipConceptSetDefinition) {
//			
//			final RelationshipConceptSetDefinition relationshipConceptSetDefinition = (RelationshipConceptSetDefinition) domain;
//			final long destinationConceptId = Long.valueOf(relationshipConceptSetDefinition.getDestinationConceptId());
//			$.add(new ConstraintDomain(destinationConceptId, relationshipConceptSetDefinition.getTypeConceptId(), storageKey));
//			
//		} else if (domain instanceof EnumeratedConceptSetDefinition) {
//			
//			final EnumeratedConceptSetDefinition enumeratedConceptSetDefinition = (EnumeratedConceptSetDefinition) domain;
//			for (final String conceptId : enumeratedConceptSetDefinition.getConceptIds()) {
//				final long id = Long.valueOf(conceptId);
//				$.add(new ConstraintDomain(id, HierarchyInclusionType.SELF.name(), storageKey));
//			}
//			
//		} else if (domain instanceof CompositeConceptSetDefinition) {
//			final CompositeConceptSetDefinition compositeConceptSetDefinition = (CompositeConceptSetDefinition) domain;
//			for (ConceptSetDefinition childConceptSetDefinition : compositeConceptSetDefinition.getChildren()) {
//				$.addAll(processConstraintDomain(storageKey, childConceptSetDefinition));
//			}
//		}
//		return $;
//	}

	/**
	 * Enumeration for concept set definition type.
	 */
	public enum DefinitionType {
		/**Concept.*/ CONCEPT,
		/**Reference set.*/ REFSET;
		
	}
	
	/**
	 * Returns {@code true} if the specified predicate represents mandatory cardinality. 
	 * @param predicate the predicate.
	 * @return {@code true} if mandatory cardinality, otherwise {@code false}.
	 */
	public static boolean isRequired(final CardinalityPredicate predicate) {
		return predicate.getMinCardinality() > 0;
	}

	/**
	 * Returns {@code true} if the specified predicate represents many cardinality. 
	 * @param predicate the predicate.
	 * @return {@code true} if many cardinality, otherwise {@code false}.
	 */
	public static boolean isMultiple(final CardinalityPredicate predicate) {
		return predicate.getMaxCardinality() == -1;
	}
	
	private PredicateUtils() { /*suppress instantiation*/ }

}