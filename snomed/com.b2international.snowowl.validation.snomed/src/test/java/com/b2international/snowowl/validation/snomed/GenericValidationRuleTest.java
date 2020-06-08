/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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


import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
import static com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator.generateConceptId;
import static com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator.generateDescriptionId;

import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.constraint.HierarchyInclusionType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.constraint.HierarchyDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.RelationshipPredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * 
 * @since 6.4
 */
@RunWith(Parameterized.class)
public class GenericValidationRuleTest extends BaseGenericValidationRuleTest {
	
	@Test
	public void rule663() throws Exception {
		final String ruleId = "663";
		indexRule(ruleId);
		
		SnomedConceptDocument inactiveDestinationConcept = concept(generateConceptId()).active(false).build();
		SnomedConceptDocument inactiveSourceConcept = concept(generateConceptId()).active(false).build();
		SnomedConceptDocument inactiveTypeConcept = concept(generateConceptId()).active(false).build();
		SnomedConceptDocument activeConcept = concept(generateConceptId()).build();
		SnomedRelationshipIndexEntry invalidSourceRelationship = relationship(inactiveSourceConcept.getId(), Concepts.IS_A, activeConcept.getId()).build();
		SnomedRelationshipIndexEntry invalidDestinationRelationship = relationship(activeConcept.getId(), Concepts.IS_A, inactiveDestinationConcept.getId()).build();
		SnomedRelationshipIndexEntry invalidTypeRelationship = relationship(activeConcept.getId(), inactiveTypeConcept.getId(), Concepts.FINDING_SITE).build();
		SnomedRelationshipIndexEntry validRelationship = relationship(activeConcept.getId(), Concepts.IS_A, Concepts.FINDING_SITE).build();
		
		
		SnomedRefSetMemberIndexEntry invalidSourceAxiom = member(inactiveSourceConcept.getId(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.IS_A, activeConcept.getId(), 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.IS_A, activeConcept.getId()))
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.build();
		
		SnomedRefSetMemberIndexEntry invalidDestinationAxiom = member(activeConcept.getId(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.IS_A, inactiveDestinationConcept.getId(), 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.IS_A, inactiveDestinationConcept.getId()))
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.build();
		
		SnomedRefSetMemberIndexEntry invalidTypeAxiom = member(activeConcept.getId(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(inactiveTypeConcept.getId(), Concepts.FINDING_SITE, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", inactiveTypeConcept.getId(), Concepts.FINDING_SITE))
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.build();
		
		SnomedRefSetMemberIndexEntry validAxiom = member(activeConcept.getId(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.IS_A, Concepts.FINDING_SITE, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.IS_A, Concepts.FINDING_SITE))
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.build();
		
		indexRevision(MAIN, 
			inactiveDestinationConcept, 
			inactiveSourceConcept,
			inactiveTypeConcept,
			activeConcept,
			invalidSourceRelationship,
			invalidDestinationRelationship,
			invalidTypeRelationship,
			validRelationship,
			invalidSourceAxiom,
			invalidTypeAxiom,
			invalidDestinationAxiom,
			validAxiom
		);
		
		ValidationIssues validationIssues = validate(ruleId);
		
		assertAffectedComponents(validationIssues, ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, invalidSourceRelationship.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, invalidDestinationRelationship.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, invalidTypeRelationship.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, invalidSourceAxiom.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, invalidTypeAxiom.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, invalidDestinationAxiom.getId())
		);
	}

	@Test
	public void rule668() throws Exception {
		final String ruleId = "668";
		indexRule(ruleId);

		SnomedConceptDocument activeSourceConcept = concept(generateConceptId()).active(true).build();
		SnomedConceptDocument inactiveTypeConcept = concept(generateConceptId()).active(false).build();
		SnomedConceptDocument activeTypeConcept = concept(generateConceptId()).active(true).build();
		SnomedConceptDocument inactiveDestinationConcept = concept(generateConceptId()).active(false).build();
		SnomedConceptDocument activeDestinationConcept = concept(generateConceptId()).active(true).build();

		SnomedRefSetMemberIndexEntry invalidDestinationAxiomMember = member(activeSourceConcept.getId(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.IS_A, inactiveDestinationConcept.getId(), 0)))
				.build();
		
		SnomedRefSetMemberIndexEntry invalidDestinationGciAxiomMember = member(activeSourceConcept.getId(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.gciAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(activeTypeConcept.getId(), inactiveDestinationConcept.getId(), 0)))
				.build();
		
		SnomedRefSetMemberIndexEntry invalidTypeAxiomMember = member(activeSourceConcept.getId(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(inactiveTypeConcept.getId(), activeDestinationConcept.getId(), 0)))
				.build();
		SnomedRefSetMemberIndexEntry validGciAxiomMember = member(activeSourceConcept.getId(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.gciAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(activeTypeConcept.getId(), activeDestinationConcept.getId(), 0)))
				.build();

		indexRevision(MAIN, 
			activeSourceConcept,
			inactiveTypeConcept,
			activeTypeConcept,
			inactiveDestinationConcept,
			activeDestinationConcept,
			invalidDestinationAxiomMember,
			invalidDestinationGciAxiomMember,
			invalidTypeAxiomMember,
			validGciAxiomMember
		);

		ValidationIssues validationIssues = validate(ruleId);

		assertAffectedComponents(validationIssues,
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, invalidDestinationAxiomMember.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, invalidDestinationGciAxiomMember.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, invalidTypeAxiomMember.getId()));
	}
	
	@Test
	public void rule664() throws Exception {
		final String ruleId = "664";
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
	public void rule665() throws Exception {
		final String ruleId = "665";
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
	public void rule666() throws Exception {
		final String ruleId = "666";
		indexRule(ruleId);
		
		// index concept with two FSNs in the same language refset
		String concept1Id = generateConceptId();
		SnomedDescriptionIndexEntry fsn1 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Fully specified name 1 (tag)")
				.conceptId(concept1Id)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.build();
		SnomedRefSetMemberIndexEntry fsn1Member = createLanguageRefsetMember(fsn1);
		SnomedDescriptionIndexEntry fsn2 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Fully specified name 2 (tag)")
				.conceptId(concept1Id)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.build();
		SnomedRefSetMemberIndexEntry fsn2Member = createLanguageRefsetMember(fsn2);
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
		SnomedRefSetMemberIndexEntry pt1Member = createLanguageRefsetMember(pt1);
		SnomedDescriptionIndexEntry pt2 = description(generateDescriptionId(), Concepts.SYNONYM, "Preferred term 2")
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.conceptId(concept2Id)
				.build();
		SnomedRefSetMemberIndexEntry pt2Member = createLanguageRefsetMember(pt2);
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
		SnomedRefSetMemberIndexEntry fsn3Member = createLanguageRefsetMember(fsn3);
		SnomedDescriptionIndexEntry pt3 = description(generateDescriptionId(), Concepts.SYNONYM, "Preferred term 3")
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.conceptId(concept3Id)
				.build();
		SnomedRefSetMemberIndexEntry pt3Member = createLanguageRefsetMember(pt3);
		SnomedConceptDocument c3 = concept(concept3Id)
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn3.getId(), fsn3.getTypeId(), fsn3.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES),
						new SnomedDescriptionFragment(pt3.getId(), pt3.getTypeId(), pt3.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES)
						))
				.build();
		indexRevision(MAIN, fsn1, fsn2, c1, pt1, pt2, c2, fsn3, pt3, c3, fsn1Member, fsn2Member,
				fsn3Member, pt1Member, pt2Member, pt3Member);
		
		ValidationIssues issues = validate(ruleId);
		assertAffectedComponents(issues, ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, c1.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, c2.getId()));
	}

	private SnomedRefSetMemberIndexEntry createLanguageRefsetMember(SnomedDescriptionIndexEntry description) {
			return member(description.getId(), DESCRIPTION_NUMBER, Concepts.REFSET_LANGUAGE_TYPE_ES)
					.referenceSetType(SnomedRefSetType.LANGUAGE)
					.build();
	}
	
	@Test
	public void rule667() throws Exception {
		final String ruleId = "667";
		indexRule(ruleId);
		
		//Populate the Description Type Refset
		final String newDescriptionTypeId = generateConceptId();
		final SnomedRefSetMemberIndexEntry descriptionFormatEntry1 = member(newDescriptionTypeId, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_DESCRIPTION_TYPE)
				.field(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 50)
				.build();
		final SnomedRefSetMemberIndexEntry descriptionFormatEntry2 = member(Concepts.SYNONYM, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_DESCRIPTION_TYPE)
				.field(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 255)
				.build();
		final SnomedRefSetMemberIndexEntry descriptionFormatEntry3 = member(Concepts.FULLY_SPECIFIED_NAME, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_DESCRIPTION_TYPE)
				.field(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 255)
				.build();
		final SnomedRefSetMemberIndexEntry descriptionFormatEntry4 = member(Concepts.TEXT_DEFINITION, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_DESCRIPTION_TYPE)
				.field(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 4096)
				.build();
		
		//Create descriptions shorter and longer than the limit for each description type
		final SnomedDescriptionIndexEntry correctSynonym = description(generateDescriptionId(), Concepts.SYNONYM, "correct synonym length").build();
		final SnomedDescriptionIndexEntry incorrectSynonym = description(generateDescriptionId(), Concepts.SYNONYM, generateTermOfLength(256)).build();
		final SnomedDescriptionIndexEntry correctFsn = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "correct FSN length(this is an fsn)").build();
		final SnomedDescriptionIndexEntry incorrectFsn = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, generateTermOfLength(256)).build();
		final SnomedDescriptionIndexEntry correctTextDefinition = description(generateDescriptionId(), Concepts.TEXT_DEFINITION, "Correct text definition length").build();
		final SnomedDescriptionIndexEntry incorrectTextDefinition = description(generateDescriptionId(), Concepts.TEXT_DEFINITION, generateTermOfLength(4097)).build();
		final SnomedDescriptionIndexEntry newCorrectDescriptionTypedDesc = description(generateConceptId(), newDescriptionTypeId, "correct new description type length").build();
		final SnomedDescriptionIndexEntry newIncorrectDescriptionTypedDesc = description(generateConceptId(), newDescriptionTypeId, generateTermOfLength(51)).build();
		
		indexRevision(MAIN, 
			correctSynonym, 
			incorrectSynonym, 
			correctFsn, 
			incorrectFsn, 
			correctTextDefinition, 
			incorrectTextDefinition,
			descriptionFormatEntry1,
			descriptionFormatEntry2,
			descriptionFormatEntry3,
			descriptionFormatEntry4,
			newCorrectDescriptionTypedDesc,
			newIncorrectDescriptionTypedDesc
		);
		
		final ValidationIssues issues = validate(ruleId);
		assertAffectedComponents(issues, 
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, incorrectSynonym.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, incorrectFsn.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, incorrectTextDefinition.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, newIncorrectDescriptionTypedDesc.getId()));
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
		
		SnomedRefSetMemberIndexEntry axiomMember1 = member(Concepts.CONCEPT_MODEL_ATTRIBUTE, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE))
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.build();
		
		SnomedRefSetMemberIndexEntry axiomMember2 = member(Concepts.CONCEPT_MODEL_ATTRIBUTE, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT))
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.build();
		
		SnomedRefSetMemberIndexEntry axiomMember3 = member(Concepts.ROOT_CONCEPT, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE))
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.build();
		
		indexRevision(MAIN, relationship1, relationship2, relationship3, axiomMember1, axiomMember2, axiomMember3);
		
		ValidationIssues issues = validate(ruleId);
		assertAffectedComponents(issues, 
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship1.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, axiomMember1.getId()));
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
		
		// OWL axioms
		SnomedRefSetMemberIndexEntry axiomMember1 = member(Concepts.CONCEPT_MODEL_ATTRIBUTE, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE))
				.build();
		
		SnomedRefSetMemberIndexEntry axiomMember2 = member(Concepts.TEXT_DEFINITION, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT))
				.build();
		
		SnomedRefSetMemberIndexEntry axiomMember3 = member(Concepts.ROOT_CONCEPT, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.PHYSICAL_OBJECT, Concepts.CONCEPT_MODEL_ATTRIBUTE, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.PHYSICAL_OBJECT, Concepts.CONCEPT_MODEL_ATTRIBUTE))
				.build();
		
		indexRevision(MAIN, attributeConstraint1, attributeConstraint2,
			relationship1, relationship2, relationship3, relationship4, relationship5,
			axiomMember1, axiomMember2,	axiomMember3);
		
		ValidationIssues issues = validate(ruleId);
		Assertions.assertThat(issues.stream().map(ValidationIssue::getAffectedComponent).collect(Collectors.toSet()))
			.contains(
					ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, axiomMember2.getId()),
					ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, axiomMember3.getId()),
					ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship3.getId()),
					ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship4.getId()))
			.doesNotContain(ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, axiomMember1.getId()),
					ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship1.getId()),
					ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship2.getId()),
					ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship5.getId()));
	}
	
	@Test
	public void rule669() throws Exception {
		final String ruleId = "669";
		indexRule(ruleId);
		
		final SnomedRefSetMemberIndexEntry duplicateSimpleMember1 = member(Concepts.IS_A, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.MODULE_ROOT).referenceSetType(SnomedRefSetType.SIMPLE).build();
		final SnomedRefSetMemberIndexEntry duplicateSimpleMember2 = member(Concepts.IS_A, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.MODULE_ROOT).referenceSetType(SnomedRefSetType.SIMPLE).build();
		final SnomedRefSetMemberIndexEntry correctSimpleMember = member(Concepts.IS_A, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.MODULE_SCT_CORE).referenceSetType(SnomedRefSetType.SIMPLE).build();
		
		final SnomedRefSetMemberIndexEntry duplicateLanguageMember1 = member(Concepts.REFSET_ROOT_CONCEPT, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_LANGUAGE_TYPE_UK).referenceSetType(SnomedRefSetType.LANGUAGE).build();
		final SnomedRefSetMemberIndexEntry duplicateLanguageMember2 = member(Concepts.REFSET_ROOT_CONCEPT, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_LANGUAGE_TYPE_UK).referenceSetType(SnomedRefSetType.LANGUAGE).build();
		final SnomedRefSetMemberIndexEntry correctLanguageMember = member(Concepts.REFSET_ROOT_CONCEPT, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_LANGUAGE_TYPE_US).referenceSetType(SnomedRefSetType.LANGUAGE).build();
		
		final SnomedRefSetMemberIndexEntry duplicateAttributeMember1 = member(Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_ATTRIBUTE_VALUE_TYPE).referenceSetType(SnomedRefSetType.ATTRIBUTE_VALUE).build();
		final SnomedRefSetMemberIndexEntry duplicateAttributeMember2 = member(Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_ATTRIBUTE_VALUE_TYPE).referenceSetType(SnomedRefSetType.ATTRIBUTE_VALUE).build();
		final SnomedRefSetMemberIndexEntry correctAttributeMember = member(Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.MODULE_SCT_CORE).referenceSetType(SnomedRefSetType.ATTRIBUTE_VALUE).build();
		
		indexRevision(MAIN, duplicateSimpleMember1, duplicateSimpleMember2, correctSimpleMember, duplicateLanguageMember1, duplicateLanguageMember2, correctLanguageMember, duplicateAttributeMember1, duplicateAttributeMember2, correctAttributeMember);
		
		final ValidationIssues issues = validate(ruleId);
		
		assertAffectedComponents(issues, 
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.IS_A),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_ROOT_CONCEPT),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT));
	}
	
	@Test
	public void rule670() throws Exception {
		final String ruleId = "670";
		indexRule(ruleId);
		
		final SnomedRefSetMemberIndexEntry duplicateSimpleMember1 = member(Concepts.IS_A, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.MODULE_ROOT).referenceSetType(SnomedRefSetType.ASSOCIATION).targetComponent(Concepts.ATTRIBUTE_TYPE_ASSOCIATION_TARGET).build();
		final SnomedRefSetMemberIndexEntry duplicateSimpleMember2 = member(Concepts.IS_A, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.MODULE_ROOT).referenceSetType(SnomedRefSetType.ASSOCIATION).targetComponent(Concepts.ATTRIBUTE_TYPE_ASSOCIATION_TARGET).build();
		final SnomedRefSetMemberIndexEntry correctSimpleMember = member(Concepts.IS_A, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.MODULE_SCT_CORE).referenceSetType(SnomedRefSetType.ASSOCIATION).targetComponent(Concepts.ATTRIBUTE_TYPE_COMPONENT_TYPE).build();
		
		indexRevision(MAIN, duplicateSimpleMember1, duplicateSimpleMember2, correctSimpleMember);
		
		final ValidationIssues issues = validate(ruleId);
		
		assertAffectedComponents(issues, 
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.IS_A));
	}
	
}
