/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator.generateConceptId;
import static com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator.generateDescriptionId;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.uri.ComponentURI;
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
import com.b2international.snowowl.snomed.datastore.index.entry.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @since 6.4
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class GenericValidationRuleTest extends BaseGenericValidationRuleTest {
	
	@Test
	public void affectedComponentURI() throws Exception {
		final String ruleId = "45a";
		indexRule(ruleId);

		// index invalid hierarchical relationship to group 1
		final SnomedRelationshipIndexEntry relationship = relationship(Concepts.FINDING_SITE, Concepts.IS_A, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.group(1)
				.build();
		
		indexRevision(MAIN, relationship);

		ValidationIssues issues = validate(ruleId);
		ComponentURI componentURI = issues.stream().map(issue -> issue.getAffectedComponentURI()).findFirst().orElse(null);
		assertThat(componentURI)
			.isEqualTo(ComponentURI.of(CODESYSTEM, SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship.getId()));
	}

	
	@Test
	public void rule34() throws Exception {
		// Relationships must be unique within a relationship group
		final String ruleId = "34";
		indexRule(ruleId);

		final SnomedRelationshipIndexEntry relationship1 = relationship(Concepts.FINDING_SITE, Concepts.IS_A, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.group(1).build();

		final SnomedRelationshipIndexEntry relationship2 = relationship(Concepts.FINDING_SITE, Concepts.IS_A, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.group(1).build();

		final SnomedRelationshipIndexEntry relationship3 = relationship(Concepts.FINDING_SITE, Concepts.IS_A, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.group(2).build();
		
		final SnomedRelationshipIndexEntry relationship4 = relationship(Concepts.FINDING_SITE, Concepts.IS_A, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.group(0).build();
		
		final SnomedRelationshipIndexEntry relationship5 = relationship(Concepts.FINDING_SITE, Concepts.IS_A, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.group(0).build();
		
		indexRevision(MAIN, relationship1, relationship2, relationship3, relationship4, relationship5);
		
		ValidationIssues issues = validate(ruleId);
		assertAffectedComponents(issues, ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship1.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship2.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship4.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship5.getId()));
	}
	
	@Test
	public void rule38a() throws Exception {
		// Active concepts should have at least one active inferred parent.
		final String ruleId = "38a";
		indexRule(ruleId);
		
		final SnomedConceptDocument activeConceptWithInferredParent = concept(generateConceptId())
				.active(true)
				.parents(Long.valueOf(Concepts.MODULE_SCT_MODEL_COMPONENT))
				.build();
		
		final SnomedConceptDocument activeConceptWithoutInferredParent = concept(generateConceptId()).active(true).build();
		
		final SnomedConceptDocument inactiveConceptWithoutInferredParent = concept(generateConceptId()).active(false).build();
				
		indexRevision(MAIN, activeConceptWithInferredParent, activeConceptWithoutInferredParent, inactiveConceptWithoutInferredParent);
		
		ValidationIssues issues = validate(ruleId);
		assertThat(issues.stream().map(ValidationIssue::getAffectedComponent).collect(Collectors.toSet()))
			.contains(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, activeConceptWithoutInferredParent.getId()))
			.doesNotContainAnyElementsOf(ImmutableList.of(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, activeConceptWithInferredParent.getId()),
					ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, inactiveConceptWithoutInferredParent.getId())));
	}
	
	@Test
	public void rule38b() throws Exception {
		// Active concepts should have at least one active stated parent
		final String ruleId = "38b";
		indexRule(ruleId);
		
		final SnomedConceptDocument activeConceptWithStatedParent = concept(generateConceptId())
				.active(true)
				.statedParents(Long.valueOf(Concepts.MODULE_SCT_MODEL_COMPONENT))
				.build();
		
		final SnomedConceptDocument activeConceptWithoutStatedParent = concept(generateConceptId()).active(true).build();
		
		final SnomedConceptDocument inactiveConceptWithoutStatedParent = concept(generateConceptId()).active(false).build();
				
		indexRevision(MAIN, activeConceptWithStatedParent, activeConceptWithoutStatedParent, inactiveConceptWithoutStatedParent);
		
		ValidationIssues issues = validate(ruleId);
		assertThat(issues.stream().map(ValidationIssue::getAffectedComponent).collect(Collectors.toSet()))
			.contains(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, activeConceptWithoutStatedParent.getId()))
			.doesNotContainAnyElementsOf(ImmutableList.of(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, activeConceptWithStatedParent.getId()),
					ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, inactiveConceptWithoutStatedParent.getId())));
	}
	
	@Test
	public void rule45a() throws Exception {
		final String ruleId = "45a";
		indexRule(ruleId);

		// index invalid hierarchical relationship to group 1
		final SnomedRelationshipIndexEntry relationship = relationship(Concepts.FINDING_SITE, Concepts.IS_A, Concepts.MODULE_SCT_MODEL_COMPONENT)
				.group(1)
				.build();
		
		indexRevision(MAIN, relationship);

		ValidationIssues issues = validate(ruleId);
		assertAffectedComponents(issues, ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship.getId()));
	}

	@Test
	public void rule45b() throws Exception {
		final String ruleId = "45b";
		indexRule(ruleId);

		// index invalid non-defining relationship to group 1
		final SnomedRelationshipIndexEntry relationship = relationship(Concepts.MORPHOLOGY, Concepts.CAUSATIVE_AGENT, Concepts.MODULE_SCT_MODEL_COMPONENT, Concepts.ADDITIONAL_RELATIONSHIP)
				.group(1)
				.build();
		
		indexRevision(MAIN, relationship);

		ValidationIssues issues = validate(ruleId);

		assertAffectedComponents(issues, ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationship.getId()));
	}

	@Test
	public void rule45c() throws Exception {
		final String ruleId = "45c";
		indexRule(ruleId);
		
		// index invalid non-defining relationship to group 0
		final SnomedRelationshipIndexEntry nonDefiningRelationshipInGroup0 = relationship(Concepts.MORPHOLOGY, Concepts.CAUSATIVE_AGENT, Concepts.MODULE_SCT_MODEL_COMPONENT, Concepts.ADDITIONAL_RELATIONSHIP)
				.group(0)
				.build();

		// index valid defining relationship to group 0
		final SnomedRelationshipIndexEntry definingRelationshipInGroup0 = relationship(Concepts.MORPHOLOGY, Concepts.CAUSATIVE_AGENT, Concepts.MODULE_SCT_MODEL_COMPONENT, Concepts.STATED_RELATIONSHIP)
				.group(0)
				.build();
		
		// index valid non-defining relationship to group 2
		final SnomedRelationshipIndexEntry relationshipInGroup2 = relationship(Concepts.MORPHOLOGY, Concepts.CAUSATIVE_AGENT, Concepts.MODULE_SCT_MODEL_COMPONENT, Concepts.ADDITIONAL_RELATIONSHIP)
				.group(2)
				.build();
		
		indexRevision(MAIN, nonDefiningRelationshipInGroup0, definingRelationshipInGroup0, relationshipInGroup2);
		
		ValidationIssues issues = validate(ruleId);
		
		assertThat(issues.stream().map(ValidationIssue::getAffectedComponent).collect(Collectors.toSet()))
			.contains(ComponentIdentifier.of(RELATIONSHIP_NUMBER, nonDefiningRelationshipInGroup0.getId()))
			.doesNotContainAnyElementsOf(ImmutableList.of(ComponentIdentifier.of(RELATIONSHIP_NUMBER, definingRelationshipInGroup0.getId()),
				ComponentIdentifier.of(RELATIONSHIP_NUMBER, relationshipInGroup2.getId())));		
	}
	
	@Test
	public void rule55() throws Exception {
		final String ruleId = "55";
		indexRule(ruleId);
		
		//wrong examples
		SnomedConceptDocument invalidDescriptionConcept = concept(generateConceptId()).build();
		SnomedDescriptionIndexEntry descWithInvalidHypenSpacing = description(generateDescriptionId(), Concepts.SYNONYM, "Hello -Cruel!")
				.moduleId(Concepts.UK_DRUG_EXTENSION_MODULE)
				.conceptId(invalidDescriptionConcept.getId())
				.build();
		
		SnomedConceptDocument invalidDescriptionConcept2 = concept(generateConceptId()).build();
		SnomedDescriptionIndexEntry descWithMultipleInvalidHypenSpacing = description(generateDescriptionId(), Concepts.SYNONYM, "Hello -Cruel- World!")
				.moduleId(Concepts.UK_DRUG_EXTENSION_MODULE)
				.conceptId(invalidDescriptionConcept2.getId())
				.build();
		
		SnomedConceptDocument invalidDescriptionConcept3 = concept(generateConceptId()).build();
		SnomedDescriptionIndexEntry descWithSymbolAndInvalidHypenSpacing = description(generateDescriptionId(), Concepts.SYNONYM, "Hello -? -a")
				.moduleId(Concepts.UK_DRUG_EXTENSION_MODULE)
				.conceptId(invalidDescriptionConcept3.getId())
				.build();

		//good examples
		SnomedConceptDocument validDescriptionConcept = concept(generateConceptId()).build();
		SnomedDescriptionIndexEntry descWithValidHypenSpacing= description(generateDescriptionId(), Concepts.SYNONYM, "Hello-Cruel!")
				.moduleId(Concepts.UK_DRUG_EXTENSION_MODULE)
				.conceptId(validDescriptionConcept.getId())
				.build();
		
		SnomedConceptDocument validDescriptionConcept2 = concept(generateConceptId()).build();
		SnomedDescriptionIndexEntry descWithSymbolNextToHypen = description(generateDescriptionId(), Concepts.SYNONYM, "Hello -?")
				.moduleId(Concepts.UK_DRUG_EXTENSION_MODULE)
				.conceptId(validDescriptionConcept2.getId())
				.build();
		
		indexRevision(Branch.MAIN_PATH, invalidDescriptionConcept, invalidDescriptionConcept2, invalidDescriptionConcept3,
				descWithInvalidHypenSpacing, descWithMultipleInvalidHypenSpacing, descWithSymbolAndInvalidHypenSpacing,
				descWithSymbolNextToHypen, descWithValidHypenSpacing, validDescriptionConcept, validDescriptionConcept2);

		ValidationIssues issues = validate(ruleId);

		assertAffectedComponents(issues,
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, descWithInvalidHypenSpacing.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, descWithMultipleInvalidHypenSpacing.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, descWithSymbolAndInvalidHypenSpacing.getId())
		);
	}
	
	@Test
	public void rule75() throws Exception {
		// Message: Relationships in group 0 should not be duplicated in any other group.
		final String ruleId = "75";
		indexRule(ruleId);

		SnomedRelationshipIndexEntry goodRel1 = relationship(Concepts.MODULE_ROOT, Concepts.IS_A, Concepts.CONCEPT_INACTIVATION_VALUE).active(true)
				.group(0).build();

		SnomedRelationshipIndexEntry badRel1 = relationship(Concepts.MODULE_ROOT, Concepts.IS_A, Concepts.CONCEPT_INACTIVATION_VALUE).active(true)
				.group(1).build();

		SnomedRelationshipIndexEntry badRel2 = relationship(Concepts.MODULE_ROOT, Concepts.IS_A, Concepts.CONCEPT_INACTIVATION_VALUE).active(true)
				.group(2).build();

		SnomedRelationshipIndexEntry goodRel2 = relationship(Concepts.MODULE_ROOT, Concepts.IS_A, Concepts.CORE_NAMESPACE).active(true).group(0)
				.build();

		indexRevision(MAIN, goodRel1, goodRel2, badRel1, badRel2);

		ValidationIssues issues = validate(ruleId);

		assertAffectedComponents(issues, ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, badRel1.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, badRel2.getId()));
	}
	
	@Test
	public void rule80() throws Exception {
		final String ruleId = "80";
		indexRule(ruleId);
		
		SnomedConceptDocument invalidConcept = concept(generateConceptId())
				.active(true)
				.build();
		
		SnomedConceptDocument validConcept1 = concept(generateConceptId())
				.active(true)
				.build();
		
		SnomedConceptDocument validConcept2 = concept(generateConceptId())
				.active(true)
				.build();
		
		SnomedRelationshipIndexEntry relationshipOnValidConcept = relationship(validConcept1.getId(), Concepts.IS_A, invalidConcept.getId()).build();
		
		SnomedRefSetMemberIndexEntry owlAxiomMemberOnValidConcpet = member(validConcept2.getId(), Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.IS_A, validConcept1.getId(), 0)))
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.build();
		
		indexRevision(MAIN, invalidConcept, validConcept1, validConcept2, relationshipOnValidConcept, owlAxiomMemberOnValidConcpet);
		
		ValidationIssues issues = validate(ruleId);
		
		assertThat(issues.stream().map(ValidationIssue::getAffectedComponent).collect(Collectors.toSet()))
			.contains(ComponentIdentifier.of(CONCEPT_NUMBER, invalidConcept.getId()))
			.doesNotContainAnyElementsOf(ImmutableList.of(ComponentIdentifier.of(CONCEPT_NUMBER, validConcept1.getId()),
					ComponentIdentifier.of(CONCEPT_NUMBER, validConcept2.getId())));
	}

	
	@Test	
	public void rule110() throws Exception {	
		// Subsets should not contain retired concepts	
		final String ruleId = "110";	
		indexRule(ruleId);	

		// index relationship that doesn't belong to attribute	
		SnomedRelationshipIndexEntry relationship1 = relationship(Concepts.FULLY_SPECIFIED_NAME, Concepts.SYNONYM, Concepts.MODULE_ROOT)	
				.build();	

		//index relationship that belongs to attribute	
		SnomedRelationshipIndexEntry relationship2 = relationship(Concepts.SYNONYM, Concepts.IS_A, Concepts.MODULE_ROOT)	
				.build();	
		
		SnomedConceptDocument validConcept = concept(generateConceptId())
				.build();
		
		SnomedRefSetMemberIndexEntry owlAxiomMember1 = member(validConcept.getId(), Concepts.REFSET_OWL_AXIOM)
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.IS_A, Concepts.PHYSICAL_OBJECT, 0)))
				.build();
		
		SnomedConceptDocument concept = concept(generateConceptId())
				.build();
		
		SnomedRefSetMemberIndexEntry owlAxiomMember2 = member(concept.getId(), Concepts.REFSET_OWL_AXIOM)
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.SYNONYM, Concepts.PHYSICAL_OBJECT, 0)))
				.build();
		
		SnomedRefSetMemberIndexEntry owlAxiomMemberWithoutClassAxioms = member(concept.getId(), Concepts.REFSET_OWL_AXIOM)
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList())
				.gciAxiomRelationships(Lists.newArrayList())
				.build();

		indexRevision(MAIN, relationship1, relationship2, validConcept, concept, owlAxiomMember1, owlAxiomMember2, owlAxiomMemberWithoutClassAxioms);	

		ValidationIssues issues = validate(ruleId);	

		assertAffectedComponents(issues, 
			ComponentIdentifier.of(RELATIONSHIP_NUMBER, relationship1.getId()),
			ComponentIdentifier.of(REFSET_MEMBER_NUMBER, owlAxiomMember2.getId())
		);	
	}
	
	@Test
	public void rule266() throws Exception {
		final String ruleId = "266";
		indexRule(ruleId);
		
		SnomedConceptDocument concept = concept(generateConceptId()).build();

		SnomedDescriptionIndexEntry validDescription1 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Clinical finding (semantic tag)")
				.moduleId(Concepts.UK_DRUG_EXTENSION_MODULE)
				.conceptId(concept.getId())
				.build();
		
		SnomedDescriptionIndexEntry irrelevantDescription1 = description(generateDescriptionId(), Concepts.TEXT_DEFINITION, "Clinical finding (semantic tag)")
				.moduleId(Concepts.UK_DRUG_EXTENSION_MODULE)
				.conceptId(concept.getId())
				.build();
		
		SnomedDescriptionIndexEntry validDescription2 = description(generateDescriptionId(), Concepts.SYNONYM, "Clinical finding ( regime/therapy )")
				.moduleId(Concepts.UK_DRUG_EXTENSION_MODULE)
				.conceptId(concept.getId())
				.build();

		SnomedDescriptionIndexEntry invalidDescription1 = description(generateDescriptionId(), Concepts.SYNONYM, "Clinical finding (regime/therapy)")
				.moduleId(Concepts.UK_DRUG_EXTENSION_MODULE)
				.conceptId(concept.getId())
				.build();

		SnomedDescriptionIndexEntry invalidDescription2 = description(generateDescriptionId(), Concepts.SYNONYM, "Clinical finding (specimen)")
				.moduleId(Concepts.UK_DRUG_EXTENSION_MODULE)
				.conceptId(concept.getId())
				.build();
		
		indexRevision(MAIN, concept, validDescription1, irrelevantDescription1, validDescription2,
				invalidDescription1, invalidDescription2);

		ValidationIssues issues = validate(ruleId);

		assertAffectedComponents(issues, 
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, invalidDescription1.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, invalidDescription2.getId())
		);
	}
	
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

	@Test
	public void rule667() throws Exception {
		final String ruleId = "667";
		indexRule(ruleId);
		
		//Populate the Description Type Refset
		final String newDescriptionTypeId = generateConceptId();
		final SnomedRefSetMemberIndexEntry descriptionFormatEntry1 = member(newDescriptionTypeId, Concepts.REFSET_DESCRIPTION_TYPE)
				.field(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 50)
				.build();
		final SnomedRefSetMemberIndexEntry descriptionFormatEntry2 = member(Concepts.SYNONYM, Concepts.REFSET_DESCRIPTION_TYPE)
				.field(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 255)
				.build();
		final SnomedRefSetMemberIndexEntry descriptionFormatEntry3 = member(Concepts.FULLY_SPECIFIED_NAME, Concepts.REFSET_DESCRIPTION_TYPE)
				.field(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 255)
				.build();
		final SnomedRefSetMemberIndexEntry descriptionFormatEntry4 = member(Concepts.TEXT_DEFINITION, Concepts.REFSET_DESCRIPTION_TYPE)
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
	public void rule668() throws Exception {
		final String ruleId = "668";
		indexRule(ruleId);

		SnomedConceptDocument activeSourceConcept = concept(generateConceptId()).active(true).build();
		SnomedConceptDocument inactiveSourceConcept = concept(generateConceptId()).active(false).build();
		SnomedConceptDocument inactiveTypeConcept = concept(generateConceptId()).active(false).build();
		SnomedConceptDocument activeTypeConcept = concept(generateConceptId()).active(true).build();
		SnomedConceptDocument inactiveDestinationConcept = concept(generateConceptId()).active(false).build();
		SnomedConceptDocument activeDestinationConcept = concept(generateConceptId()).active(true).build();

		SnomedRefSetMemberIndexEntry invalidSourceAxiomMember = member(inactiveSourceConcept.getId(), Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.IS_A, activeDestinationConcept.getId(), 0)))
				.build();
		
		SnomedRefSetMemberIndexEntry validSourceAxiomMember = member(activeSourceConcept.getId(), Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.IS_A, activeDestinationConcept.getId(), 0)))
				.build();
		
		SnomedRefSetMemberIndexEntry invalidDestinationAxiomMember = member(activeSourceConcept.getId(), Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.IS_A, inactiveDestinationConcept.getId(), 0)))
				.build();
		
		SnomedRefSetMemberIndexEntry invalidDestinationGciAxiomMember = member(activeSourceConcept.getId(), Concepts.REFSET_OWL_AXIOM)
				.gciAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(activeTypeConcept.getId(), inactiveDestinationConcept.getId(), 0)))
				.build();
		
		SnomedRefSetMemberIndexEntry invalidTypeAxiomMember = member(activeSourceConcept.getId(), Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(inactiveTypeConcept.getId(), activeDestinationConcept.getId(), 0)))
				.build();
		SnomedRefSetMemberIndexEntry validGciAxiomMember = member(activeSourceConcept.getId(), Concepts.REFSET_OWL_AXIOM)
				.gciAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(activeTypeConcept.getId(), activeDestinationConcept.getId(), 0)))
				.build();

		indexRevision(MAIN, 
			activeSourceConcept,
			inactiveSourceConcept,
			inactiveTypeConcept,
			activeTypeConcept,
			inactiveDestinationConcept,
			activeDestinationConcept,
			validSourceAxiomMember,
			invalidSourceAxiomMember,
			invalidDestinationAxiomMember,
			invalidDestinationGciAxiomMember,
			invalidTypeAxiomMember,
			validGciAxiomMember
		);

		ValidationIssues validationIssues = validate(ruleId);

		assertAffectedComponents(validationIssues,
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, invalidSourceAxiomMember.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, invalidDestinationAxiomMember.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, invalidDestinationGciAxiomMember.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, invalidTypeAxiomMember.getId()));
	}
	
	@Test
	public void rule669() throws Exception {
		final String ruleId = "669";
		indexRule(ruleId);
		
		final long publishedEffectiveTime = Instant.now().toEpochMilli();
		
		final SnomedRefSetMemberIndexEntry duplicateSimpleMember1 = member(Concepts.IS_A, Concepts.MODULE_ROOT).referenceSetType(SnomedRefSetType.SIMPLE).effectiveTime(publishedEffectiveTime).build();
		final SnomedRefSetMemberIndexEntry duplicateSimpleMember2 = member(Concepts.IS_A, Concepts.MODULE_ROOT).referenceSetType(SnomedRefSetType.SIMPLE).build();
		final SnomedRefSetMemberIndexEntry correctSimpleMember = member(Concepts.IS_A, Concepts.MODULE_SCT_CORE).referenceSetType(SnomedRefSetType.SIMPLE).build();
		
		final SnomedRefSetMemberIndexEntry duplicateLanguageMember1 = member(Concepts.REFSET_ROOT_CONCEPT, Concepts.REFSET_LANGUAGE_TYPE_UK).referenceSetType(SnomedRefSetType.LANGUAGE).effectiveTime(publishedEffectiveTime).build();
		final SnomedRefSetMemberIndexEntry duplicateLanguageMember2 = member(Concepts.REFSET_ROOT_CONCEPT, Concepts.REFSET_LANGUAGE_TYPE_UK).referenceSetType(SnomedRefSetType.LANGUAGE).build();
		final SnomedRefSetMemberIndexEntry correctLanguageMember = member(Concepts.REFSET_ROOT_CONCEPT, Concepts.REFSET_LANGUAGE_TYPE_US).referenceSetType(SnomedRefSetType.LANGUAGE).build();
		
		final SnomedRefSetMemberIndexEntry duplicateAttributeMember1 = member(Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT, Concepts.REFSET_ATTRIBUTE_VALUE_TYPE).referenceSetType(SnomedRefSetType.ATTRIBUTE_VALUE).effectiveTime(publishedEffectiveTime).build();
		final SnomedRefSetMemberIndexEntry duplicateAttributeMember2 = member(Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT, Concepts.REFSET_ATTRIBUTE_VALUE_TYPE).referenceSetType(SnomedRefSetType.ATTRIBUTE_VALUE).build();
		final SnomedRefSetMemberIndexEntry correctAttributeMember = member(Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT, Concepts.MODULE_SCT_CORE).referenceSetType(SnomedRefSetType.ATTRIBUTE_VALUE).build();
		
		final SnomedRefSetMemberIndexEntry duplicateAttributeMemberWithModule1 = member(Concepts.ATTRIBUTE, Concepts.REFSET_ATTRIBUTE_VALUE_TYPE).moduleId(Concepts.MODULE_SCT_CORE).referenceSetType(SnomedRefSetType.ATTRIBUTE_VALUE).build();
		final SnomedRefSetMemberIndexEntry duplicateAttributeMemberWithModule2 = member(Concepts.ATTRIBUTE, Concepts.REFSET_ATTRIBUTE_VALUE_TYPE).moduleId(Concepts.MODULE_SCT_CORE).referenceSetType(SnomedRefSetType.ATTRIBUTE_VALUE).build();
		final SnomedRefSetMemberIndexEntry correctAttributeMemberWithModule = member(Concepts.ATTRIBUTE, Concepts.REFSET_ATTRIBUTE_VALUE_TYPE).moduleId(Concepts.UK_CLINICAL_EXTENSION_MODULE).referenceSetType(SnomedRefSetType.ATTRIBUTE_VALUE).build();
		
		indexRevision(MAIN, duplicateSimpleMember1, duplicateSimpleMember2, correctSimpleMember, 
				duplicateLanguageMember1, duplicateLanguageMember2, correctLanguageMember, 
				duplicateAttributeMember1, duplicateAttributeMember2, correctAttributeMember, 
				duplicateAttributeMemberWithModule1, duplicateAttributeMemberWithModule2, correctAttributeMemberWithModule);
		
		final ValidationIssues issues = validate(ruleId);
		
		assertAffectedComponents(issues, 
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.IS_A),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_ROOT_CONCEPT),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.ATTRIBUTE_TYPE_CONCEPT_TYPE_COMPONENT),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.ATTRIBUTE));
	}
	
	@Test
	public void rule670() throws Exception {
		final String ruleId = "670";
		indexRule(ruleId);
		
		final SnomedRefSetMemberIndexEntry duplicateSimpleMember1 = member(Concepts.IS_A, Concepts.MODULE_ROOT).referenceSetType(SnomedRefSetType.ASSOCIATION).targetComponent(Concepts.ATTRIBUTE_TYPE_ASSOCIATION_TARGET).build();
		final SnomedRefSetMemberIndexEntry duplicateSimpleMember2 = member(Concepts.IS_A, Concepts.MODULE_ROOT).referenceSetType(SnomedRefSetType.ASSOCIATION).targetComponent(Concepts.ATTRIBUTE_TYPE_ASSOCIATION_TARGET).build();
		final SnomedRefSetMemberIndexEntry correctSimpleMember = member(Concepts.IS_A, Concepts.MODULE_SCT_CORE).referenceSetType(SnomedRefSetType.ASSOCIATION).targetComponent(Concepts.ATTRIBUTE_TYPE_COMPONENT_TYPE).build();
		
		indexRevision(MAIN, duplicateSimpleMember1, duplicateSimpleMember2, correctSimpleMember);
		
		final ValidationIssues issues = validate(ruleId);
		
		assertAffectedComponents(issues, 
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.IS_A));
	}

	@Test
	public void rule671() throws Exception {
		final String ruleId = "671";
		indexRule(ruleId);
		
		String conceptId1 = generateConceptId();
		SnomedDescriptionIndexEntry fsn1 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Fully specified name 1 (tag)")
				.conceptId(conceptId1)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.build();
		
		SnomedDescriptionIndexEntry pt1 = description(generateDescriptionId(), Concepts.SYNONYM, "Preferred term 1")
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.conceptId(conceptId1)
				.build();
		
		SnomedDescriptionIndexEntry pt2 = description(generateDescriptionId(), Concepts.SYNONYM, "Preferred term 2")
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.conceptId(conceptId1)
				.build();
		
		SnomedRefSetMemberIndexEntry ptMember1 = member(pt1.getId(), Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR).valueId(Concepts.CONCEPT_NON_CURRENT).build();
		SnomedRefSetMemberIndexEntry ptMember2 = member(pt2.getId(), Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR).valueId(Concepts.ERRONEOUS).build();
		
		SnomedConceptDocument conceptWithActiveDescription = concept(conceptId1)
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn1.getId(), fsn1.getTypeId(), fsn1.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES),
						new SnomedDescriptionFragment(pt1.getId(), pt1.getTypeId(), pt1.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES),
						new SnomedDescriptionFragment(pt2.getId(), pt2.getTypeId(), pt2.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES)
						))
				.active(false)
				.build();
		
		String conceptId2 = generateConceptId();
		SnomedDescriptionIndexEntry fsn2 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Fully specified name 2 (tag)")
				.conceptId(conceptId2)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.build();
		
		SnomedDescriptionIndexEntry pt3 = description(generateDescriptionId(), Concepts.SYNONYM, "Preferred term 3")
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_ES, Acceptability.PREFERRED)
				.active(false)
				.conceptId(conceptId2)
				.build();
		
		
		SnomedRefSetMemberIndexEntry ptMember3 = member(pt3.getId(), Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR).valueId(Concepts.CONCEPT_NON_CURRENT).build();
		
		SnomedConceptDocument conceptWithInactiveDescription = concept(conceptId2)
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn2.getId(), fsn2.getTypeId(), fsn2.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES),
						new SnomedDescriptionFragment(pt3.getId(), pt3.getTypeId(), pt3.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_ES)
						))
				.active(false)
				.build();
		
		indexRevision(MAIN, conceptWithActiveDescription, fsn1, pt1, ptMember1, conceptWithInactiveDescription, fsn2, pt2, pt3, ptMember2, ptMember3);
		
		final ValidationIssues issues = validate(ruleId);
		
		assertAffectedComponents(issues, 
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, pt2.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, pt3.getId()));
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
		
		SnomedRefSetMemberIndexEntry axiomMember1 = member(Concepts.CONCEPT_MODEL_ATTRIBUTE, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE))
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.build();
		
		SnomedRefSetMemberIndexEntry axiomMember2 = member(Concepts.CONCEPT_MODEL_ATTRIBUTE, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT))
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.build();
		
		SnomedRefSetMemberIndexEntry axiomMember3 = member(Concepts.ROOT_CONCEPT, Concepts.REFSET_OWL_AXIOM)
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
		SnomedRefSetMemberIndexEntry axiomMember1 = member(Concepts.CONCEPT_MODEL_ATTRIBUTE, Concepts.REFSET_OWL_AXIOM)
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE))
				.build();
		
		SnomedRefSetMemberIndexEntry axiomMember2 = member(Concepts.TEXT_DEFINITION, Concepts.REFSET_OWL_AXIOM)
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT))
				.build();
		
		SnomedRefSetMemberIndexEntry axiomMember3 = member(Concepts.ROOT_CONCEPT, Concepts.REFSET_OWL_AXIOM)
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
	
	private SnomedRefSetMemberIndexEntry createLanguageRefsetMember(SnomedDescriptionIndexEntry description) {
		return member(description.getId(), Concepts.REFSET_LANGUAGE_TYPE_ES)
				.referenceSetType(SnomedRefSetType.LANGUAGE)
				.build();
	}
	
}
