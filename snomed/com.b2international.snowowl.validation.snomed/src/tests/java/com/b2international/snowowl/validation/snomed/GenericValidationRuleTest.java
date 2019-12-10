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
import static org.assertj.core.api.Assertions.assertThat;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;

import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.b2international.collections.PrimitiveSets;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * @since 6.4
 */
@RunWith(Parameterized.class)
public class GenericValidationRuleTest extends BaseGenericValidationRuleTest {
	
	@Test
	public void rule663() throws Exception {
		final String ruleId = "663";
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
	public void rule668() throws Exception {
		final String ruleId = "668";
		indexRule(ruleId);
		
		SnomedConceptDocument activeSourceConcept = concept(generateConceptId()).active(true).build();
		indexRevision(MAIN, nextStorageKey(), activeSourceConcept);		
		SnomedConceptDocument inactiveTypeConcept = concept(generateConceptId()).active(false).build();
		indexRevision(MAIN, nextStorageKey(), inactiveTypeConcept);
		SnomedConceptDocument activeTypeConcept = concept(generateConceptId()).active(true).build();
		indexRevision(MAIN, nextStorageKey(), activeTypeConcept);
		SnomedConceptDocument inactiveDestinationConcept = concept(generateConceptId()).active(false).build();
		indexRevision(MAIN, nextStorageKey(), inactiveDestinationConcept);
		SnomedConceptDocument activeDestinationConcept = concept(generateConceptId()).active(true).build();
		indexRevision(MAIN, nextStorageKey(), activeDestinationConcept);
		
		SnomedRefSetMemberIndexEntry invalidDestinationAxiomMember = member(activeSourceConcept.getId(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.IS_A, inactiveDestinationConcept.getId(), 0)))
				.build();
		indexRevision(MAIN, nextStorageKey(), invalidDestinationAxiomMember);
		
		SnomedRefSetMemberIndexEntry invalidDestinationGciAxiomMember = member(activeSourceConcept.getId(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.gciAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(activeTypeConcept.getId(), inactiveDestinationConcept.getId(), 0)))
				.build();
		indexRevision(MAIN, nextStorageKey(), invalidDestinationGciAxiomMember);
		
		SnomedRefSetMemberIndexEntry invalidTypeAxiomMember = member(activeSourceConcept.getId(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(inactiveTypeConcept.getId(), activeDestinationConcept.getId(), 0)))
				.build();
		indexRevision(MAIN, nextStorageKey(), invalidTypeAxiomMember);
		
		SnomedRefSetMemberIndexEntry validGciAxiomMember = member(activeSourceConcept.getId(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.gciAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(activeTypeConcept.getId(), activeDestinationConcept.getId(), 0)))
				.build();
		indexRevision(MAIN, nextStorageKey(), validGciAxiomMember);
		
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
	public void rule665() throws Exception {
		final String ruleId = "665";
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
	public void rule666() throws Exception {
		final String ruleId = "666";
		indexRule(ruleId);
		
		SnomedReferenceSet refset = new SnomedReferenceSet();
		refset.setActive(true);
		refset.setStorageKey(nextStorageKey());
		refset.setType(SnomedRefSetType.LANGUAGE);
		refset.setReferencedComponentType(SnomedTerminologyComponentConstants.DESCRIPTION);
		
		SnomedConceptDocument languageRefset = concept(generateConceptId()).refSet(refset)
				.parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(SnomedConstants.Concepts.REFSET_ROOT_CONCEPT)))
				.build();
		
		// index concept with two FSNs in the same language refset
		String concept1Id = generateConceptId();
		SnomedDescriptionIndexEntry fsn1 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Fully specified name 1 (tag)")
				.conceptId(concept1Id)
				.activeMemberOf(ImmutableSet.of(languageRefset.getId()))
				.acceptability(languageRefset.getId(), Acceptability.PREFERRED)
				.build();
		SnomedRefSetMemberIndexEntry fsn1Member = member(UUID.randomUUID().toString(), fsn1.getId(), DESCRIPTION_NUMBER, languageRefset.getId())
					.active(true)
					.referenceSetType(SnomedRefSetType.LANGUAGE)
					.build();
		SnomedDescriptionIndexEntry fsn2 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Fully specified name 2 (tag)")
				.conceptId(concept1Id)
				.activeMemberOf(ImmutableSet.of(languageRefset.getId()))
				.acceptability(languageRefset.getId(), Acceptability.PREFERRED)
				.build();		
		SnomedRefSetMemberIndexEntry fsn2Member = member(UUID.randomUUID().toString(), fsn2.getId(), DESCRIPTION_NUMBER, languageRefset.getId())
				.active(true)
				.referenceSetType(SnomedRefSetType.LANGUAGE)
				.build();
		SnomedConceptDocument c1 = concept(concept1Id)
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn1.getId(), fsn1.getStorageKey(), fsn1.getTypeId(), fsn1.getTerm(), languageRefset.getId()),
						new SnomedDescriptionFragment(fsn2.getId(), fsn2.getStorageKey(), fsn2.getTypeId(), fsn2.getTerm(), languageRefset.getId())))
				.build();
		
		// index concept with two PTs in the same language refset
		String concept2Id = generateConceptId();
		SnomedDescriptionIndexEntry pt1 = description(generateDescriptionId(), Concepts.SYNONYM, "Preferred term 1")
				.activeMemberOf(ImmutableSet.of(languageRefset.getId()))
				.acceptability(languageRefset.getId(), Acceptability.PREFERRED)
				.conceptId(concept2Id)
				.build();
		SnomedRefSetMemberIndexEntry pt1Member = member(UUID.randomUUID().toString(), pt1.getId(), DESCRIPTION_NUMBER, languageRefset.getId())
				.active(true)
				.referenceSetType(SnomedRefSetType.LANGUAGE)
				.build();
		SnomedDescriptionIndexEntry pt2 = description(generateDescriptionId(), Concepts.SYNONYM, "Preferred term 2")
				.activeMemberOf(ImmutableSet.of(languageRefset.getId()))
				.acceptability(languageRefset.getId(), Acceptability.PREFERRED)
				.conceptId(concept2Id)
				.build();
		SnomedRefSetMemberIndexEntry pt2Member = member(UUID.randomUUID().toString(), pt2.getId(), DESCRIPTION_NUMBER, languageRefset.getId())
				.active(true)
				.referenceSetType(SnomedRefSetType.LANGUAGE)
				.build();
		SnomedConceptDocument c2 = concept(concept2Id)
				.preferredDescriptions(
						ImmutableList.of(
								new SnomedDescriptionFragment(pt1.getId(), pt1.getStorageKey(), pt1.getTypeId(), pt1.getTerm(), languageRefset.getId()),
								new SnomedDescriptionFragment(pt2.getId(), pt2.getStorageKey(), pt2.getTypeId(), pt2.getTerm(), languageRefset.getId())))
				.build();
		
		// index concept with only one PT and one FSN in a given language refset
		String concept3Id = generateConceptId();
		SnomedDescriptionIndexEntry fsn3 = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "Fully specified name 3 (tag)")
				.conceptId(concept3Id)
				.activeMemberOf(ImmutableSet.of(languageRefset.getId()))
				.acceptability(languageRefset.getId(), Acceptability.PREFERRED)
				.build();
		SnomedRefSetMemberIndexEntry fsn3Member = member(UUID.randomUUID().toString(), fsn3.getId(), DESCRIPTION_NUMBER, languageRefset.getId())
				.active(true)
				.referenceSetType(SnomedRefSetType.LANGUAGE)
				.build();
		SnomedDescriptionIndexEntry pt3 = description(generateDescriptionId(), Concepts.SYNONYM, "Preferred term 3")
				.activeMemberOf(ImmutableSet.of(languageRefset.getId()))
				.acceptability(languageRefset.getId(), Acceptability.PREFERRED)
				.conceptId(concept3Id)
				.build();
		SnomedRefSetMemberIndexEntry pt3Member = member(UUID.randomUUID().toString(), pt3.getId(), DESCRIPTION_NUMBER, languageRefset.getId())
				.active(true)
				.referenceSetType(SnomedRefSetType.LANGUAGE)
				.build();
		SnomedConceptDocument c3 = concept(concept3Id)
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn3.getId(), fsn3.getStorageKey(), fsn3.getTypeId(), fsn3.getTerm(), languageRefset.getId()),
						new SnomedDescriptionFragment(pt3.getId(), pt3.getStorageKey(), pt3.getTypeId(), pt3.getTerm(), languageRefset.getId())
						))
				.build();
		
		index().write(MAIN, currentTime(), writer -> {
			writer.put(nextStorageKey(), languageRefset);
			writer.put(nextStorageKey(), fsn1);
			writer.put(nextStorageKey(), fsn2);
			writer.put(nextStorageKey(), c1);
			writer.put(nextStorageKey(), pt1);
			writer.put(nextStorageKey(), pt2);
			writer.put(nextStorageKey(), c2);
			writer.put(nextStorageKey(), fsn3);
			writer.put(nextStorageKey(), pt3);
			writer.put(nextStorageKey(), c3);
			writer.put(nextStorageKey(), fsn1Member);
			writer.put(nextStorageKey(), fsn2Member);
			writer.put(nextStorageKey(), fsn3Member);
			writer.put(nextStorageKey(), pt1Member);
			writer.put(nextStorageKey(), pt2Member);
			writer.put(nextStorageKey(), pt3Member);
			
			writer.commit();
			return null;
		});
		
		ValidationIssues issues = validate(ruleId);
		assertAffectedComponents(issues, ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, c1.getId()),
				ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, c2.getId()));
	}
	
	@Test
	public void rule667() throws Exception {
		final String ruleId = "667";
		indexRule(ruleId);
		
		final SnomedDescriptionIndexEntry correctSynonym = description(generateDescriptionId(), Concepts.SYNONYM, "correct synonym length").build();
		indexRevision(MAIN, nextStorageKey(), correctSynonym);
		
		final SnomedDescriptionIndexEntry incorrectSynonym = description(generateDescriptionId(), Concepts.SYNONYM, generateTermOfLength(256)).build();
		indexRevision(MAIN, nextStorageKey(), incorrectSynonym);
		
		final SnomedDescriptionIndexEntry correctFsn = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, "correct FSN length(this is an fsn)").build();
		indexRevision(MAIN, nextStorageKey(), correctFsn);
		
		final SnomedDescriptionIndexEntry incorrectFsn = description(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, generateTermOfLength(256)).build();
		indexRevision(MAIN, nextStorageKey(), incorrectFsn);
		
		final SnomedDescriptionIndexEntry correctTextDefinition = description(generateDescriptionId(), Concepts.TEXT_DEFINITION, "Correct text definition length").build();
		indexRevision(MAIN, nextStorageKey(), correctTextDefinition);
		
		final SnomedDescriptionIndexEntry incorrectTextDefinition = description(generateDescriptionId(), Concepts.TEXT_DEFINITION, generateTermOfLength(4097)).build();
		indexRevision(MAIN, nextStorageKey(), incorrectTextDefinition);		
		
		final String newDescriptionTypeId = generateConceptId();
		final SnomedRefSetMemberIndexEntry descriptionFormatEntry = member(newDescriptionTypeId, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_DESCRIPTION_TYPE)
				.field(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 50)
				.build();
		indexRevision(MAIN, nextStorageKey(), descriptionFormatEntry);
		
		final SnomedDescriptionIndexEntry newCorrectDescriptionTypedDesc = description(generateConceptId(), newDescriptionTypeId, "correct new description type length").build();
		indexRevision(MAIN, nextStorageKey(), newCorrectDescriptionTypedDesc);
		
		final SnomedDescriptionIndexEntry newIncorrectDescriptionTypedDesc = description(generateConceptId(), newDescriptionTypeId, generateTermOfLength(51)).build();
		indexRevision(MAIN, nextStorageKey(), newIncorrectDescriptionTypedDesc);
		
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
		
		SnomedRefSetMemberIndexEntry axiomMember1 = member(Concepts.CONCEPT_MODEL_ATTRIBUTE, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE))
				.build();
		indexRevision(MAIN, nextStorageKey(), axiomMember1);
		
		SnomedRefSetMemberIndexEntry axiomMember2 = member(Concepts.CONCEPT_MODEL_ATTRIBUTE, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT))
				.build();
		indexRevision(MAIN, nextStorageKey(), axiomMember2);
		
		SnomedRefSetMemberIndexEntry axiomMember3 = member(Concepts.ROOT_CONCEPT, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE))
				.build();
		indexRevision(MAIN, nextStorageKey(), axiomMember3);
		
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
		
		// OWL axioms
		SnomedRefSetMemberIndexEntry axiomMember1 = member(Concepts.CONCEPT_MODEL_ATTRIBUTE, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.CONCEPT_MODEL_ATTRIBUTE))
				.build();
		indexRevision(MAIN, nextStorageKey(), axiomMember1);
		
		SnomedRefSetMemberIndexEntry axiomMember2 = member(Concepts.TEXT_DEFINITION, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.FINDING_SITE, Concepts.PHYSICAL_OBJECT))
				.build();
		indexRevision(MAIN, nextStorageKey(), axiomMember2);
		
		SnomedRefSetMemberIndexEntry axiomMember3 = member(Concepts.ROOT_CONCEPT, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_OWL_AXIOM)
				.classAxiomRelationships(Lists.newArrayList(new SnomedOWLRelationshipDocument(Concepts.PHYSICAL_OBJECT, Concepts.CONCEPT_MODEL_ATTRIBUTE, 0)))
				.owlExpression(String.format("ObjectSomeValuesFrom(:%s :%s)", Concepts.PHYSICAL_OBJECT, Concepts.CONCEPT_MODEL_ATTRIBUTE))
				.build();
		indexRevision(MAIN, nextStorageKey(), axiomMember3);
		
		ValidationIssues issues = validate(ruleId);
		
		assertThat(issues.stream().map(ValidationIssue::getAffectedComponent).collect(Collectors.toSet()))
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
	
}
