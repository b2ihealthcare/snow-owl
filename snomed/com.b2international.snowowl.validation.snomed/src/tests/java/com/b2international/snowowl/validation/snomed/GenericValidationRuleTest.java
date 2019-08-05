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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.constraint.HierarchyInclusionType;
import com.b2international.snowowl.snomed.datastore.index.constraint.HierarchyDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.RelationshipPredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.ImmutableList;

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
		SnomedConceptDocument inactiveSourceConcept = concept(generateConceptId()).active(false).build();
		SnomedConceptDocument inactiveTypeConcept = concept(generateConceptId()).active(false).build();
		SnomedConceptDocument activeConcept = concept(generateConceptId()).build();
		SnomedRelationshipIndexEntry invalidSourceRelationship = relationship(inactiveSourceConcept.getId(), Concepts.IS_A, activeConcept.getId()).build();
		SnomedRelationshipIndexEntry invalidDestinationRelationship = relationship(activeConcept.getId(), Concepts.IS_A, inactiveDestinationConcept.getId()).build();
		SnomedRelationshipIndexEntry invalidTypeRelationship = relationship(activeConcept.getId(), inactiveTypeConcept.getId(), Concepts.FINDING_SITE).build();
		SnomedRelationshipIndexEntry validRelationship = relationship(activeConcept.getId(), Concepts.IS_A, Concepts.FINDING_SITE).build();
		
		indexRevision(MAIN, 
			inactiveDestinationConcept, 
			inactiveSourceConcept,
			inactiveTypeConcept,
			activeConcept,
			invalidSourceRelationship,
			invalidDestinationRelationship,
			invalidTypeRelationship,
			validRelationship
		);
		
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
		SnomedDescriptionIndexEntry d1 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Hello World!").conceptId(c1.getId())
				.build();
		
		SnomedConceptDocument c2 = concept(generateConceptId()).build();
		SnomedDescriptionIndexEntry d2 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Hello World!").conceptId(c2.getId())
				.build();
		
		SnomedConceptDocument c3 = concept(generateConceptId()).build();
		SnomedDescriptionIndexEntry d3 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Hello Cruel World!")
				.conceptId(c3.getId()).build();
		
		indexRevision(MAIN, 
			c1, d1,
			c2, d2,
			c3, d3
		);

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
		
		SnomedRelationshipIndexEntry relationshipWithQualifingCharType = relationship(generateConceptId(), Concepts.IS_A, generateConceptId())
				.characteristicTypeId(Concepts.QUALIFYING_RELATIONSHIP)
				.build();
		
		// Relationships with acceptable characteristic types
		SnomedRelationshipIndexEntry  relationshipWithStatedCharType = relationship(generateConceptId(), Concepts.IS_A, generateConceptId())
				.characteristicTypeId(Concepts.STATED_RELATIONSHIP)
				.build();
		
		SnomedRelationshipIndexEntry relationshipWithInferredCharType = relationship(generateConceptId(), Concepts.IS_A, generateConceptId())
				.characteristicTypeId(Concepts.INFERRED_RELATIONSHIP)
				.build();
		
		SnomedRelationshipIndexEntry relationshipWithAdditionalCharType = relationship(generateConceptId(), Concepts.IS_A, generateConceptId())
				.characteristicTypeId(Concepts.ADDITIONAL_RELATIONSHIP)
				.build();
		
		SnomedRelationshipIndexEntry relationshipWithCharTypeOutsideOfCharTypeHierarchy = relationship(generateConceptId(), Concepts.IS_A, generateConceptId())
				.characteristicTypeId(Concepts.ROOT_CONCEPT)
				.build();
		
		indexRevision(MAIN, 
				relationshipWithDefiningCharType, 
				relationshipWithQualifingCharType,
				relationshipWithStatedCharType,
				relationshipWithInferredCharType,
				relationshipWithAdditionalCharType,
				relationshipWithCharTypeOutsideOfCharTypeHierarchy);
		
		ValidationIssues issues = validate(ruleId);
		
		assertAffectedComponents(issues,
			ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationshipWithDefiningCharType.getId()),
			ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationshipWithQualifingCharType.getId())
		);

	}
	
	@Test
	public void ruleSnomedCommon4() throws Exception {
		final String ruleId = "snomed-common-4";
		indexRule(ruleId);

		// index concept with two FSNs in the same language refset
		String concept1Id = generateConceptId();
		SnomedDescriptionIndexEntry fsn1 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Fully specified name 1 (tag)")
				.conceptId(concept1Id)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.build();
		SnomedDescriptionIndexEntry fsn2 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Fully specified name 2 (tag)")
				.conceptId(concept1Id)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.build();
		SnomedConceptDocument c1 = concept(concept1Id)
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn1.getId(), fsn1.getTypeId(), fsn1.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES),
						new SnomedDescriptionFragment(fsn2.getId(), fsn2.getTypeId(), fsn2.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES)))
				.build();

		// index concept with two PTs in the same language refset
		String concept2Id = generateConceptId();
		SnomedDescriptionIndexEntry pt1 = description(generateDescriptionId(), Concepts.SYNONYM, "Preferred term 1")
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.conceptId(concept2Id)
				.build();
		SnomedDescriptionIndexEntry pt2 = description(generateDescriptionId(), Concepts.SYNONYM, "Preferred term 2")
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.conceptId(concept2Id)
				.build();
		SnomedConceptDocument c2 = concept(concept2Id)
				.preferredDescriptions(
						ImmutableList.of(
								new SnomedDescriptionFragment(pt1.getId(), pt1.getTypeId(), pt1.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES),
								new SnomedDescriptionFragment(pt2.getId(), pt2.getTypeId(), pt2.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES)))
				.build();

		// index concept with only one PT and one FSN in a given language refset
		String concept3Id = generateConceptId();
		SnomedDescriptionIndexEntry fsn3 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Fully specified name 3 (tag)")
				.conceptId(concept3Id)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.build();
		SnomedDescriptionIndexEntry pt3 = description(generateDescriptionId(), Concepts.SYNONYM, "Preferred term 3")
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.conceptId(concept3Id)
				.build();
		SnomedConceptDocument c3 = concept(concept3Id)
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn3.getId(), fsn3.getTypeId(), fsn3.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES),
						new SnomedDescriptionFragment(pt3.getId(), pt3.getTypeId(), pt3.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES)
						))
				.build();
		indexRevision(MAIN, fsn1, fsn2, c1, pt1, pt2, c2, fsn3, pt3, c3);
		
		ValidationIssues issues = validate(ruleId);
		assertAffectedComponents(issues, ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, c1.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, c2.getId()));
	}
	
	@Test
	public void rule_mrcm_constraint() throws Exception {
		final String ruleId = "rule_mrcm_constraint";
		indexRule(ruleId);
		
		//Create MRCM rule
		final HierarchyDefinitionFragment conceptSetDefinition = hierarchyConceptSetDefinition(Concepts.CONCEPT_MODEL_ATTRIBUTE, HierarchyInclusionType.SELF);
		final HierarchyDefinitionFragment predicateType = hierarchyConceptSetDefinition(Concepts.FINDING_SITE, HierarchyInclusionType.SELF);
		final HierarchyDefinitionFragment predicateRange = hierarchyConceptSetDefinition(Concepts.PHYSICAL_OBJECT, HierarchyInclusionType.SELF);
		final RelationshipPredicateFragment conceptModelPredicate = relationshipPredicate(predicateType, predicateRange);
		
		final SnomedConstraintDocument attributeConstraint = attributeConstraint(conceptSetDefinition, conceptModelPredicate);
		indexRevision(MAIN, attributeConstraint);
		
		final SnomedRelationshipIndexEntry relationship1 = relationship(Concepts.CONCEPT_MODEL_ATTRIBUTE, Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE)
				.group(1).build();
		final SnomedRelationshipIndexEntry relationship2 = relationship(Concepts.CONCEPT_MODEL_ATTRIBUTE, Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT)
				.group(1).build();
		final SnomedRelationshipIndexEntry relationship3 = relationship(Concepts.ROOT_CONCEPT, Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT)
				.group(2).build();
		
		indexRevision(MAIN, relationship1, relationship2, relationship3);
		
		ValidationIssues issues = validate(ruleId);
		assertAffectedComponents(issues, 
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship1.getId()));
	}
	
	@Test
	public void rule_mrcm_constraint_type() throws Exception {
		final String ruleId = "rule_mrcm_constraint_type";
		indexRule(ruleId);
		
		//First mrcm rule
		final HierarchyDefinitionFragment conceptSetDefinition1 = hierarchyConceptSetDefinition(Concepts.CONCEPT_MODEL_ATTRIBUTE, HierarchyInclusionType.SELF);
		final HierarchyDefinitionFragment predicateType1 = hierarchyConceptSetDefinition(Concepts.FINDING_SITE, HierarchyInclusionType.SELF);
		final HierarchyDefinitionFragment predicateRange1 = hierarchyConceptSetDefinition(Concepts.PHYSICAL_OBJECT, HierarchyInclusionType.SELF);
		final RelationshipPredicateFragment conceptModelPredicate1 = relationshipPredicate(predicateType1, predicateRange1);
		
		final SnomedConstraintDocument attributeConstraint1 = attributeConstraint(conceptSetDefinition1, conceptModelPredicate1);
		
		//Second mrcm rule
		final HierarchyDefinitionFragment conceptSetDefinition2 = hierarchyConceptSetDefinition(Concepts.PHYSICAL_OBJECT, HierarchyInclusionType.SELF);
		final HierarchyDefinitionFragment predicateType2 = hierarchyConceptSetDefinition(Concepts.HAS_ACTIVE_INGREDIENT, HierarchyInclusionType.SELF);
		final HierarchyDefinitionFragment predicateRange2 = hierarchyConceptSetDefinition(Concepts.TEXT_DEFINITION, HierarchyInclusionType.SELF);
		final RelationshipPredicateFragment conceptModelPredicate2 = relationshipPredicate(predicateType2, predicateRange2);
		
		final SnomedConstraintDocument attributeConstraint2 = attributeConstraint(conceptSetDefinition2, conceptModelPredicate2);
	
		
		//Relationships
		final SnomedRelationshipIndexEntry relationship1 = relationship(Concepts.CONCEPT_MODEL_ATTRIBUTE, Concepts.IS_A, Concepts.CONCEPT_MODEL_ATTRIBUTE)
				.group(1).build();
		
		final SnomedRelationshipIndexEntry relationship2 = relationship(Concepts.CONCEPT_MODEL_ATTRIBUTE, Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT)
				.group(1).build();
		
		final SnomedRelationshipIndexEntry relationship3 = relationship(Concepts.ROOT_CONCEPT, Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT)
				.group(2).build();
		
		final SnomedRelationshipIndexEntry relationship4 = relationship(Concepts.PHYSICAL_OBJECT, Concepts.FINDING_SITE, Concepts.TEXT_DEFINITION)
				.group(0).build();
		
		final SnomedRelationshipIndexEntry relationship5 = relationship(Concepts.PHYSICAL_OBJECT, Concepts.HAS_ACTIVE_INGREDIENT, Concepts.PHYSICAL_OBJECT)
				.group(3).build();
		
		indexRevision(
			MAIN, 
			attributeConstraint1, 
			attributeConstraint2,
			relationship1,
			relationship2,
			relationship3,
			relationship4,
			relationship5
		);
		
		ValidationIssues issues = validate(ruleId);
		assertAffectedComponents(issues, 
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship3.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship4.getId()));
	}
	
}
