/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.validation.snomed;


import static com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator.generateConceptId;
import static com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator.generateDescriptionId;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;

/**
 * 
 * @since 6.4
 */
@RunWith(Parameterized.class)
public class GenericValidationRuleTest extends BaseGenericValidationRuleTest {
	
	@Test
	public void ruleSnomedCommon1() throws Exception {
		final String ruleId = "snomed-common-1";
		indexRule(ruleId);
		
		SnomedConceptDocument inactiveDestinationConcept = concept(generateConceptId()).active(false).build();
		indexRevision(MAIN, nextStorageKey(), inactiveDestinationConcept);
		
		SnomedConceptDocument inactiveSourceConcept = concept(generateConceptId()).active(false).build();
		indexRevision(MAIN, nextStorageKey(), inactiveSourceConcept);

		SnomedConceptDocument inactiveTypeConcept = concept(generateConceptId()).active(false).build();
		indexRevision(MAIN, nextStorageKey(), inactiveTypeConcept);
		
		SnomedConceptDocument activeConcept = concept(generateConceptId()).build();
		indexRevision(MAIN, nextStorageKey(), activeConcept);
		
		SnomedRelationshipIndexEntry invalidSourceRelationship = relationship(inactiveSourceConcept.getId(), Concepts.IS_A, activeConcept.getId())
				.build();
		indexRevision(MAIN, nextStorageKey(), invalidSourceRelationship);
		
		SnomedRelationshipIndexEntry invalidDestinationRelationship = relationship(activeConcept.getId(), Concepts.IS_A, inactiveDestinationConcept.getId())
				.build();
		indexRevision(MAIN, nextStorageKey(), invalidDestinationRelationship);
		
		SnomedRelationshipIndexEntry invalidTypeRelationship = relationship(activeConcept.getId(), inactiveTypeConcept.getId(), Concepts.FINDING_SITE)
				.build();
		indexRevision(MAIN, nextStorageKey(), invalidTypeRelationship);
		
		SnomedRelationshipIndexEntry validRelationship = relationship(activeConcept.getId(), Concepts.IS_A, Concepts.FINDING_SITE)
				.build();
		indexRevision(MAIN, nextStorageKey(), validRelationship);
		
		ValidationIssues validationIssues = validate(ruleId);
		
		assertAffectedComponents(validationIssues, ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, invalidSourceRelationship.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, invalidDestinationRelationship.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, invalidTypeRelationship.getId()));
	}
	
	@Test
	public void ruleSnomedCommon2() throws Exception {
		final String ruleId = "snomed-common-2";
		indexRule(ruleId);

		// index three concepts
		SnomedConceptDocument c1 = concept(generateConceptId()).build();
		indexRevision(MAIN, nextStorageKey(), c1);
		SnomedDescriptionIndexEntry d1 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Hello World!")
				.conceptId(c1.getId())
				.build();
		indexRevision(MAIN, nextStorageKey(), d1);

		SnomedConceptDocument c2 = concept(generateConceptId()).build();
		indexRevision(MAIN, nextStorageKey(), c2);
		SnomedDescriptionIndexEntry d2 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Hello World!")
				.conceptId(c2.getId())
				.build();
		indexRevision(MAIN, nextStorageKey(), d2);

		SnomedConceptDocument c3 = concept(generateConceptId()).build();
		indexRevision(MAIN, nextStorageKey(), c3);
		SnomedDescriptionIndexEntry d3 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Hello Cruel World!")
				.conceptId(c3.getId())
				.build();
		indexRevision(MAIN, nextStorageKey(), d3);

		ValidationIssues issues = validate(ruleId);

		assertAffectedComponents(issues, ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, d1.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, d2.getId()));
	}
	
	@Test
	public void ruleSnomedCommon3() throws Exception {
		final String ruleId = "snomed-common-3";
		indexRule(ruleId);

		// Relationships with deprecated characteristic types
		SnomedRelationshipIndexEntry relationshipWithDefiningCharType = relationship(generateConceptId(), Concepts.IS_A, generateConceptId())
				.characteristicTypeId(Concepts.DEFINING_RELATIONSHIP)
				.build();
		indexRevision(MAIN, nextStorageKey(), relationshipWithDefiningCharType);
		
		SnomedRelationshipIndexEntry relationshipWithQualifingCharType = relationship(generateConceptId(), Concepts.IS_A, generateConceptId())
				.characteristicTypeId(Concepts.QUALIFYING_RELATIONSHIP)
				.build();
		indexRevision(MAIN, nextStorageKey(), relationshipWithQualifingCharType);

		
		// Relationships with acceptable characteristic types
		SnomedRelationshipIndexEntry  relationshipWithStatedCharType = relationship(generateConceptId(), Concepts.IS_A, generateConceptId())
				.characteristicTypeId(Concepts.STATED_RELATIONSHIP)
				.build();
		indexRevision(MAIN, nextStorageKey(), relationshipWithStatedCharType);

		SnomedRelationshipIndexEntry relationshipWithInferredCharType = relationship(generateConceptId(), Concepts.IS_A, generateConceptId())
				.characteristicTypeId(Concepts.INFERRED_RELATIONSHIP)
				.build();
		indexRevision(MAIN, nextStorageKey(), relationshipWithInferredCharType);
		
		SnomedRelationshipIndexEntry relationshipWithAdditionalCharType = relationship(generateConceptId(), Concepts.IS_A, generateConceptId())
				.characteristicTypeId(Concepts.ADDITIONAL_RELATIONSHIP)
				.build();
		indexRevision(MAIN, nextStorageKey(), relationshipWithAdditionalCharType);
		
		SnomedRelationshipIndexEntry relationshipWithCharTypeOutsideOfCharTypeHierarchy = relationship(generateConceptId(), Concepts.IS_A, generateConceptId())
				.characteristicTypeId(Concepts.ROOT_CONCEPT)
				.build();
		indexRevision(MAIN, nextStorageKey(), relationshipWithCharTypeOutsideOfCharTypeHierarchy);

		ValidationIssues issues = validate(ruleId);
		
		assertAffectedComponents(issues,
			ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationshipWithDefiningCharType.getId()),
			ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationshipWithQualifingCharType.getId())
		);

	}
	
	@Test
	public void rule_mrcm_constraint() throws Exception {
		final String ruleId = "rule_mrcm_constraint";
		indexRule(ruleId);
		
		//Create MRCM rule
		final HierarchyConceptSetDefinition conceptSetDefinition = hierarchyConceptSetDefinition(Concepts.CONCEPT_MODEL_ATTRIBUTE, HierarchyInclusionType.SELF);
		final HierarchyConceptSetDefinition predicateType = hierarchyConceptSetDefinition(Concepts.FINDING_SITE, HierarchyInclusionType.SELF);
		final HierarchyConceptSetDefinition predicateRange = hierarchyConceptSetDefinition(Concepts.PHYSICAL_OBJECT, HierarchyInclusionType.SELF);
		final RelationshipPredicate conceptModelPredicate = relationshipPredicate(predicateType, predicateRange);
		
		final AttributeConstraint attributeConstraint = attributeConstraint(conceptSetDefinition, conceptModelPredicate);
		long attributeConsrtaintStorageKey = nextStorageKey();
		((InternalCDOObject) attributeConstraint).cdoInternalSetID(CDOIDUtil.createLong(attributeConsrtaintStorageKey));
	
		final SnomedConstraintDocument constraint = constraint(attributeConstraint).build();
		indexRevision(MAIN, attributeConsrtaintStorageKey, constraint);
		
		final SnomedRelationshipIndexEntry relationship1 = relationship(Concepts.CONCEPT_MODEL_ATTRIBUTE, Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE)
				.group(1).build();
		indexRevision(MAIN, nextStorageKey(), relationship1);
		
		final SnomedRelationshipIndexEntry relationship2 = relationship(Concepts.CONCEPT_MODEL_ATTRIBUTE, Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT)
				.group(1).build();
		indexRevision(MAIN, nextStorageKey(), relationship2);
		
		final SnomedRelationshipIndexEntry relationship3 = relationship(Concepts.ROOT_CONCEPT, Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT)
				.group(2).build();
		indexRevision(MAIN, nextStorageKey(), relationship3);
		
		ValidationIssues issues = validate(ruleId);
		assertAffectedComponents(issues, 
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship1.getId()));
	}
	
	@Test
	public void rule_mrcm_constraint_type() throws Exception {
		final String ruleId = "rule_mrcm_constraint_type";
		indexRule(ruleId);
		
		//First mrcm rule
		final HierarchyConceptSetDefinition conceptSetDefinition1 = hierarchyConceptSetDefinition(Concepts.CONCEPT_MODEL_ATTRIBUTE, HierarchyInclusionType.SELF);
		final HierarchyConceptSetDefinition predicateType1 = hierarchyConceptSetDefinition(Concepts.FINDING_SITE, HierarchyInclusionType.SELF);
		final HierarchyConceptSetDefinition predicateRange1 = hierarchyConceptSetDefinition(Concepts.PHYSICAL_OBJECT, HierarchyInclusionType.SELF);
		final RelationshipPredicate conceptModelPredicate1 = relationshipPredicate(predicateType1, predicateRange1);
		
		final AttributeConstraint attributeConstraint1 = attributeConstraint(conceptSetDefinition1, conceptModelPredicate1);
		long attributeConsrtaintStorageKey1 = nextStorageKey();
		((InternalCDOObject) attributeConstraint1).cdoInternalSetID(CDOIDUtil.createLong(attributeConsrtaintStorageKey1));
	
		final SnomedConstraintDocument constraint1 = constraint(attributeConstraint1).build();
		indexRevision(MAIN, attributeConsrtaintStorageKey1, constraint1);
		
		//Second mrcm rule
		final HierarchyConceptSetDefinition conceptSetDefinition2 = hierarchyConceptSetDefinition(Concepts.PHYSICAL_OBJECT, HierarchyInclusionType.SELF);
		final HierarchyConceptSetDefinition predicateType2 = hierarchyConceptSetDefinition(Concepts.HAS_ACTIVE_INGREDIENT, HierarchyInclusionType.SELF);
		final HierarchyConceptSetDefinition predicateRange2 = hierarchyConceptSetDefinition(Concepts.TEXT_DEFINITION, HierarchyInclusionType.SELF);
		final RelationshipPredicate conceptModelPredicate2 = relationshipPredicate(predicateType2, predicateRange2);
		
		final AttributeConstraint attributeConstraint2 = attributeConstraint(conceptSetDefinition2, conceptModelPredicate2);
		long attributeConsrtaintStorageKey2 = nextStorageKey();
		((InternalCDOObject) attributeConstraint2).cdoInternalSetID(CDOIDUtil.createLong(attributeConsrtaintStorageKey2));
	
		final SnomedConstraintDocument constraint2 = constraint(attributeConstraint2).build();
		indexRevision(MAIN, attributeConsrtaintStorageKey2, constraint2);
		
		//Relationships
		final SnomedRelationshipIndexEntry relationship1 = relationship(Concepts.CONCEPT_MODEL_ATTRIBUTE, Concepts.IS_A, Concepts.CONCEPT_MODEL_ATTRIBUTE)
				.group(1).build();
		indexRevision(MAIN, nextStorageKey(), relationship1);
		
		final SnomedRelationshipIndexEntry relationship2 = relationship(Concepts.CONCEPT_MODEL_ATTRIBUTE, Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT)
				.group(1).build();
		indexRevision(MAIN, nextStorageKey(), relationship2);
		
		final SnomedRelationshipIndexEntry relationship3 = relationship(Concepts.ROOT_CONCEPT, Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT)
				.group(2).build();
		indexRevision(MAIN, nextStorageKey(), relationship3);
		
		final SnomedRelationshipIndexEntry relationship4 = relationship(Concepts.PHYSICAL_OBJECT, Concepts.FINDING_SITE, Concepts.TEXT_DEFINITION)
				.group(0).build();
		indexRevision(MAIN, nextStorageKey(), relationship4);
		
		final SnomedRelationshipIndexEntry relationship5 = relationship(Concepts.PHYSICAL_OBJECT, Concepts.HAS_ACTIVE_INGREDIENT, Concepts.PHYSICAL_OBJECT)
				.group(3).build();
		indexRevision(MAIN, nextStorageKey(), relationship5);
		
		ValidationIssues issues = validate(ruleId);
		assertAffectedComponents(issues, 
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship3.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship4.getId()));
	}
}
