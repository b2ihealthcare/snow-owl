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
package com.b2international.snowowl.snomed.core.mrcm;

import static com.b2international.snowowl.snomed.mrcm.MrcmPackage.Literals.DESCRIPTION_PREDICATE__TYPE_ID;
import static com.b2international.snowowl.snomed.mrcm.MrcmPackage.Literals.ENUMERATED_CONCEPT_SET_DEFINITION__CONCEPT_IDS;
import static com.b2international.snowowl.snomed.mrcm.MrcmPackage.Literals.HIERARCHY_CONCEPT_SET_DEFINITION__CONCEPT_ID;
import static com.b2international.snowowl.snomed.mrcm.MrcmPackage.Literals.REFERENCE_SET_CONCEPT_SET_DEFINITION__REF_SET_IDENTIFIER_CONCEPT_ID;
import static com.b2international.snowowl.snomed.mrcm.MrcmPackage.Literals.RELATIONSHIP_CONCEPT_SET_DEFINITION__DESTINATION_CONCEPT_ID;
import static com.b2international.snowowl.snomed.mrcm.MrcmPackage.Literals.RELATIONSHIP_CONCEPT_SET_DEFINITION__TYPE_CONCEPT_ID;
import static com.b2international.snowowl.snomed.mrcm.MrcmPackage.Literals.RELATIONSHIP_PREDICATE__CHARACTERISTIC_TYPE_CONCEPT_ID;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;

import java.util.Collection;

import javax.annotation.Nullable;

import org.eclipse.emf.ecore.EAttribute;

import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;

/**
 * Utility class for {@link ConceptModel}.
 *
 */
public abstract class ConceptModelUtils {

	public static final Collection<EAttribute> CONCEPT_ID_FEATURES = unmodifiableSet(newHashSet(
		DESCRIPTION_PREDICATE__TYPE_ID,
		RELATIONSHIP_PREDICATE__CHARACTERISTIC_TYPE_CONCEPT_ID,
		ENUMERATED_CONCEPT_SET_DEFINITION__CONCEPT_IDS,
		HIERARCHY_CONCEPT_SET_DEFINITION__CONCEPT_ID,
		REFERENCE_SET_CONCEPT_SET_DEFINITION__REF_SET_IDENTIFIER_CONCEPT_ID,
		RELATIONSHIP_CONCEPT_SET_DEFINITION__TYPE_CONCEPT_ID,
		RELATIONSHIP_CONCEPT_SET_DEFINITION__DESTINATION_CONCEPT_ID
	));
	
	/**Returns with the bottom most predicate. extracts the bottom most predicate if it is wrapped into a cardinality predicate.*/
	public static ConceptModelPredicate getBottomMostPredicate(final ConceptModelPredicate predicate) {
		if (predicate instanceof CardinalityPredicate) {
			return getBottomMostPredicate(((CardinalityPredicate) predicate).getPredicate());
		} else {
			return predicate;
		}
		
	}
	
	/**Returns with the container attribute constraint for the concept set definition.*/
	@Nullable static public AttributeConstraint getContainerConstraint(final ConceptSetDefinition definition) {
		
		if (definition.eContainer() instanceof CompositeConceptSetDefinition) {
			return getContainerConstraint((ConceptSetDefinition) definition.eContainer());
		} else {
			
			if (definition.eContainer() instanceof AttributeConstraint) {
				return (AttributeConstraint) definition.eContainer();
			} else if (definition.eContainer() instanceof ConceptModelPredicate) {
				return getContainerConstraint((ConceptModelPredicate) definition.eContainer());
			}
			
			return null;
			
		}
		
	}
	
	/**Returns with the container attribute constraint for the concept model predicate. if the predicate is wrapped into a 
	 * cardinality predicate, this method traverse up to the top most container.*/
	@Nullable static public AttributeConstraint getContainerConstraint(final ConceptModelPredicate predicate) {
		
		if (predicate.eContainer() instanceof CardinalityPredicate) {
			return getContainerConstraint((CardinalityPredicate) predicate.eContainer());
		} else {
			return (AttributeConstraint) (predicate.eContainer() instanceof AttributeConstraint ? predicate.eContainer() : null);
		}
		
	}
	
	private ConceptModelUtils() {}
	
}