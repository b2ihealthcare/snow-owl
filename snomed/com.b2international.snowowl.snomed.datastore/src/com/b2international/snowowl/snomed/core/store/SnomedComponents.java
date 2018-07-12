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
package com.b2international.snowowl.snomed.core.store;

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
	 * Creates and returns a new {@link SnomedRelationshipBuilder SNOMED CT Relationship builder}.
	 * 
	 * @return
	 */
	public static SnomedRelationshipBuilder newRelationship() {
		return new SnomedRelationshipBuilder();
	}

	/**
	 * Creates and returns a new {@link SnomedLanguageReferenceSetMemberBuilder SNOMED CT Language Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedLanguageReferenceSetMemberBuilder newLanguageMember() {
		return new SnomedLanguageReferenceSetMemberBuilder();
	}

	/**
	 * Creates and returns a new {@link SnomedSimpleReferenceSetMemberBuilder SNOMED CT Simple Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedSimpleReferenceSetMemberBuilder newSimpleMember() {
		return new SnomedSimpleReferenceSetMemberBuilder();
	}

	/**
	 * Creates and returns a new {@link SnomedQueryReferenceSetMemberBuilder SNOMED CT Query Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedQueryReferenceSetMemberBuilder newQueryMember() {
		return new SnomedQueryReferenceSetMemberBuilder();
	}
	
	/**
	 * Creates and returns a new {@link SnomedAssociationReferenceSetMemberBuilder SNOMED CT Association Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedAssociationReferenceSetMemberBuilder newAssociationMember() {
		return new SnomedAssociationReferenceSetMemberBuilder();
	}

	/**
	 * Creates and returns a new {@link SnomedAttributeValueReferenceSetMemberBuilder SNOMED CT Attribute Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedAttributeValueReferenceSetMemberBuilder newAttributeValueMember() {
		return new SnomedAttributeValueReferenceSetMemberBuilder();
	}
	
	/**
	 * Creates and returns a new {@link SnomedConcreteDomainReferenceSetMemberBuilder SNOMED CT Concrete Domain Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedConcreteDomainReferenceSetMemberBuilder newConcreteDomainReferenceSetMember() {
		return new SnomedConcreteDomainReferenceSetMemberBuilder();
	}

	/**
	 * Creates and returns a new {@link SnomedDescriptionTypeReferenceSetMemberBuilder SNOMED CT Description Format Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedDescriptionTypeReferenceSetMemberBuilder newDescriptionTypeMember() {
		return new SnomedDescriptionTypeReferenceSetMemberBuilder();
	}

	/**
	 * Creates and returns a new {@link SnomedModuleDependencyReferenceSetMemberBuilder SNOMED CT Module Dependency Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedModuleDependencyReferenceSetMemberBuilder newModuleDependencyMember() {
		return new SnomedModuleDependencyReferenceSetMemberBuilder();
	}
	
	/**
	 * Creates and returns a new {@link SnomedSimpleMapReferenceSetMemberBuilder SNOMED CT Simple Map Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedSimpleMapReferenceSetMemberBuilder newSimpleMapMember() {
		return new SnomedSimpleMapReferenceSetMemberBuilder();
	}

	/**
	 * Creates and returns a new {@link SnomedComplexMapReferenceSetMemberBuilder SNOMED CT Complex Map Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedComplexMapReferenceSetMemberBuilder newComplexMapMember() {
		return new SnomedComplexMapReferenceSetMemberBuilder();
	}

	/**
	 * Creates and returns a new {@link SnomedOWLExpressionReferenceSetMemberBuilder SNOMED CT OWL Expression Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedOWLExpressionReferenceSetMemberBuilder newOWLExpressionReferenceSetMember() {
		return new SnomedOWLExpressionReferenceSetMemberBuilder();
	}
	
	/**
	 * Creates and returns a new {@link SnomedMRCMDomainReferenceSetMemberBuilder SNOMED CT MRCM Domain Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedMRCMDomainReferenceSetMemberBuilder newMRCMDomainReferenceSetMember() {
		return new SnomedMRCMDomainReferenceSetMemberBuilder();
	}
	
	/**
	 * Creates and returns a new {@link SnomedMRCMAttributeDomainReferenceSetMemberBuilder SNOMED CT MRCM Attribute Domain Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedMRCMAttributeDomainReferenceSetMemberBuilder newMRCMAttributeDomainReferenceSetMember() {
		return new SnomedMRCMAttributeDomainReferenceSetMemberBuilder();
	}
	
	/**
	 * Creates and returns a new {@link SnomedMRCMAttributeRangeReferenceSetMemberBuilder SNOMED CT MRCM Attribute Range Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedMRCMAttributeRangeReferenceSetMemberBuilder newMRCMAttributeRangeReferenceSetMember() {
		return new SnomedMRCMAttributeRangeReferenceSetMemberBuilder();
	}
	
	/**
	 * Creates and returns a new {@link SnomedMRCMModuleScopeReferenceSetMemberBuilder SNOMED CT MRCM Module Scope Reference set member builder}.
	 * 
	 * @return
	 */
	public static SnomedMRCMModuleScopeReferenceSetMemberBuilder newMRCMModuleScopeReferenceSetMember() {
		return new SnomedMRCMModuleScopeReferenceSetMemberBuilder();
	}
}
