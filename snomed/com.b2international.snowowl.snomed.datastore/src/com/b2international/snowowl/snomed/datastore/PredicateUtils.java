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

import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition;
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

	public static void collectDomainIds(ConceptSetDefinition domain, final Set<String> selfIds, final Set<String> descendantIds, final Set<String> refSetIds) {
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
					collectDomainIds(childDomain, selfIds, descendantIds, refSetIds);
				}
				return domain;
			}
		}.doSwitch(domain);
	}

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