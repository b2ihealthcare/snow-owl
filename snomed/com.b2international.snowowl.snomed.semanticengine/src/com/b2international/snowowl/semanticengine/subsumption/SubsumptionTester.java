/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.semanticengine.subsumption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.AttributeValue;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.dsl.scg.ScgFactory;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.semanticengine.utils.SemanticUtils;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * Implementation of the subsumption testing rules described in the document 
 * <em>SNOMED CT Transforming Expressions to Normal Forms</em>. Method Javadocs contain 
 * excerpts from the above document.
 * 
 */
public class SubsumptionTester {
	
	private static final String CONCEPT_ID_FINDING_CONTEXT = "408729009";
	private static final String CONCEPT_ID_KNOWN_ABSENT = "410516002";
	private static final String CONCEPT_ID_DEFINITELY_NOT_PRESENT = "410594000";
	private static final String CONCEPT_ID_TEMPORAL_CONTEXT = "408731000";
	private static final String CONCEPT_ID_ASSOCIATED_FINDING = "246090004";
	private static final String CONCEPT_ID_SUBJECT_RELATIONSHIP_CONTEXT = "408732007";
	private static final String CONCEPT_ID_SAME_AS = "168666000";
	private static final String CONCEPT_ID_REPLACED_BY = "370124000";
	
	private final String branchPath;
	
	public SubsumptionTester(String branchPath) {
		this.branchPath = branchPath;
	}

	/**
	 * The following steps are applied to test if a normalized-predicate subsumes a normalized-candidate. 
	 * This assumes that these normal form expressions have been generated in accordance with 6.3.1.
	 * <ol>
	 * <li>Test that each focus concept referenced in the normalized-predicate subsumes at least 
	 * 		one focus concept in the normalized-candidate.<br>
	 * 		<ul>
	 * 		<li>If not, the normalized-predicate does not subsume the normalized-candidate. No further testing is required.</li>
	 * 			Exit with result false. <br>
	 * 		<li>The approach to testing concept subsumption is described in section 6.3.5</li>
	 * 		</ul>
	 * </li>
	 * <li> Test that each attribute group in the normalized-predicate subsumes at least one
	 * attribute group in the normalized-candidate.
	 * 		<ul>
	 * 		<li>If not, the normalized-predicate does not subsume the normalized-candidate. No further testing is required.</li> 
	 * 			Exit with result false.
	 * 		<li>The approach to testing attribute group subsumption is described in section 6.3.3</li>
	 * 		</ul>
	 * </li>	
	 * <li>Test that each ungrouped attribute in the normalized-predicate subsumes at least one
	 * attribute (either grouped or ungrouped) in the normalized-candidate.
	 * 		<ul>
	 * 		<li>If not, the normalized-predicate does not subsume the normalized-candidate.</li>
	 * 			Exit with result false. 
	 * 		<li>The approach to testing attribute subsumption is described in section 6.3.4</li>
	 * 		</ul>
	 * <li> If all these tests succeed, the normalized-predicate subsumes the normalized-candidate.
	 * </ol>
	 * 
	 * @param predicate	the predicate Expression in short normal form
	 * @param candidate	the candidate Expression in long normal form
	 * @return
	 */
	public boolean isSubsumed(Expression predicate, Expression candidate) {
		// focus concepts
		Collection<Concept> predicateFocusConcepts = predicate.getConcepts();
		Collection<Concept> candidateFocusConcepts = candidate.getConcepts();
		for (Concept predicateFocusConcept : predicateFocusConcepts) {
			// check if it subsumes at least one candidate focus concept
			boolean subsumed = false;
			for (Concept candidateFocusConcept : candidateFocusConcepts) {
				subsumed = isSubsumed(predicateFocusConcept, candidateFocusConcept);
				if (subsumed)
					break;
			}
			if (!subsumed)
				return false;
		}

		// attribute groups
		List<Group> predicateGroups = predicate.getGroups();
		List<Group> candidateGroups = candidate.getGroups();
			
		for (Group predicateGroup : predicateGroups) {
			// check if it subsumes at least one candidate attribute group
			boolean subsumed = false;
			for (Group candidateGroup : candidateGroups) {
				subsumed = isSubsumed(predicateGroup, candidateGroup);
				if (subsumed)
					break;
			}
			if (!subsumed)
				return false;
		}
		
		// ungrouped attributes
		List<Attribute> ungroupedPredicateAttributes = predicate.getAttributes();
		List<Attribute> ungroupedCandidateAttributes = candidate.getAttributes();
		List<Attribute> groupedCandidateAttributes = new ArrayList<Attribute>();
		
		for (Group candidateGroup : candidateGroups) {
			List<Attribute> attributes = candidateGroup.getAttributes();
			groupedCandidateAttributes.addAll(attributes);
		}
		
		List<Attribute> allCandidateAttributes = new ArrayList<Attribute>(ungroupedCandidateAttributes);
		allCandidateAttributes.addAll(groupedCandidateAttributes);
		
		for (Attribute ungroupedPredicateAttribute : ungroupedPredicateAttributes) {
			// Test that each ungrouped attribute in the normalized-predicate subsumes at least one
			// attribute (either grouped or ungrouped) in the normalized-candidate.
			boolean subsumed = false;
			for (Attribute candidateAttribute : allCandidateAttributes) {
				subsumed = isSubsumed(ungroupedPredicateAttribute, candidateAttribute);
				if (subsumed)
					break;
			}
			if (!subsumed)
				return false;
		}
		
		return true;
	}
	
	/**
	 * The following steps test if a predicate-concept subsumes a candidate-concept.
	 * <ol>
	 * <li>Test if candidate-concept is an inactive concept</li>
	 * 		<ul>
	 * 		<li>candidate-concept.conceptStatus NOT IN (0, 6, 11)</li>
	 * 			If the candidate-concept is inactive then look for an active concept 
	 * 			related by a historical relationship "SAME AS" or "REPLACED BY" and 
	 * 			treat this as the candidate-concept in subsequent steps.
	 * 		</ul>
	* <li>Test if the candidate-concept is identical to the predicate-concept.
	* 		<ul>
	* 		<li>If candidate-concept.conceptId == predicate-concept.conceptId 
	* 			the concepts are identical.</li>
	* 		Exit with result true (accept equivalent)
	* 		</ul>
	* <li>Test if the predicate-concept is one of the supertype ancestors of the candidate-concept.
	* 		<ul>
	* 		<li>This is true if a sequence of "is a" relationships leads from the candidate-concept 
	* 			(as source conceptId1) to the predicate-concept (as the target conceptId2).</li>
	* 		Exit returning the result of this test
	* 		<li>Various approaches to optimization of this test are described in the 
	* 			SNOMED CT Technical Implementation Guide. The recommended approach using 
	* 			a "transitive closure table" is summarized in Section 7.</li>
	* 		</ul>
	* </ol>
	 * 
	 * @param predicate	the predicate {@link Concept}
	 * @param candidate	the candidate {@link Concept}
	 * @return
	 */
	public boolean isSubsumed(Concept predicate, Concept candidate) {
		
		final SnomedConcept candidateConcept = SnomedRequests.prepareGetConcept(candidate.getId())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
		
		if (!candidateConcept.isActive()) {
			String replacementConceptId = null;
			final SnomedRelationships outboundRelationships = SnomedRequests.prepareSearchRelationship()
					.all()
					.filterByActive(true)
					.filterBySource(candidate.getId())
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync();
			//for (int i = 0; i < outgoingRelationships.length; i++) {
			for (SnomedRelationship relationship : outboundRelationships) {
				if (relationship.getTypeId().equals(CONCEPT_ID_SAME_AS) || relationship.getTypeId().equals(CONCEPT_ID_REPLACED_BY)) {
					replacementConceptId = relationship.getDestinationId();
					break;
				}
			}
			
			// if no replacement concept found, the candidate is not considered subsumed
			if (replacementConceptId == null) {
				return false;
			}
			
			Concept replacementCandidateConcept = ScgFactory.eINSTANCE.createConcept();
			replacementCandidateConcept.setId(replacementConceptId);
			candidate = replacementCandidateConcept;
		}
		
		if (predicate.getId().equals(candidate.getId()))
			return true;
		
		long predicateId = Long.parseLong(predicate.getId());
		for (long parentId : candidateConcept.getParentIds()) {
			if (parentId == predicateId) {
				return true;
			}
		}
		
		for (long ancestorId : candidateConcept.getAncestorIds()) {
			if (ancestorId == predicateId) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * The following steps test if a predicate-attribute-group subsumes candidate-attribute-group.
	 * <ol>
	 * <li>Check the predicate-attribute-group for the presence of the attribute: "finding context" (408729009).</li>
	 * 		<ul>
	 * 		<li>If the group does not contain this attribute, apply the normal attribute group tests specified in section 6.3.3.1.</li>
	 * 		</ul>
	 * <li>If the predicate-attribute-group contains the "finding context" (408729009) attribute, check whether its value is one of 
	 * 		the following: "known absent" (410516002) or "definitely not present" (410594000).</li>
	 * 		<ul>
	 * 		<li>If the attribute exists and has one of these values, apply the tests for a context attribute group with absent finding, 
	 * 			as specified in section 6.3.3.2.</li>
	 * 		<li>If the attribute exists and has any other value, apply the tests for a normal attribute group, as specified in section 6.3.3.1.</li>
	 * 		</ul>
	 * </ol>
	 * @param predicate	the predicate {@link Group}
	 * @param candidate	the candidate {@link Group}
	 * @return
	 */
	public boolean isSubsumed(Group predicate, Group candidate) {
		for (Attribute predicateAttribute : predicate.getAttributes()) {
			Concept predicateNameConcept = predicateAttribute.getName();
			if (!predicateNameConcept.getId().equals(CONCEPT_ID_FINDING_CONTEXT))
				continue;
			
			AttributeValue predicateValue = predicateAttribute.getValue();
			if (predicateValue instanceof Concept) {
				Concept predicateValueConcept = (Concept) predicateValue;
				if (predicateValueConcept.getId().equals(CONCEPT_ID_KNOWN_ABSENT) || predicateValueConcept.getId().equals(CONCEPT_ID_DEFINITELY_NOT_PRESENT))
					return contextGroupWithAbsentFindingSubsumptionTest(predicate, candidate);
			}
		}
		
		return normalGroupSubsumptionTest(predicate, candidate);
	}
	
	/**
	 * The following step tests most attribute groups. However, a modified approach (see 6.3.3.2) is required 
	 * in the case of attribute groups that indicate the absence of a finding.
	 * <ol>
	 * <li>Test that each attribute in the predicate-attribute-group subsumes at least one attribute in the candidate-attribute-group.</li>
	 * 		<ul>
	 * 		<li>If not, the predicate-attribute-group does not subsume the candidate-attribute-group.</li>
	 *		Exit with result false.
	 * 		<li>The approach to testing attribute subsumption is described in section 6.3.4 2. 
	 *		</ul>
	 * <li>If all attributes in the group pass this test then the predicate-attribute-group subsumes
	 * 		the candidate-attribute-group.</li>
	 * Exit with result true.
	 * </ol>
	 */
	private boolean normalGroupSubsumptionTest(Group predicate, Group candidate) {
		List<Attribute> predicateAttributes = predicate.getAttributes();
		List<Attribute> candidateAttributes = candidate.getAttributes();
		
		for (Attribute predicateAttribute : predicateAttributes) {
			boolean subsumed = false;
			for (Attribute candidateAttribute : candidateAttributes) {
				subsumed = isSubsumed(predicateAttribute, candidateAttribute);
				if (subsumed)
					break;
			}
			if (!subsumed)
				return false;
		}
		
		return true;
	}
	
	/**
	 * The following steps test most attribute groups that indicate the absence of a finding. 
	 * This approach differs from the general tests applicable to other attribute groups because of 
	 * the way in which assertions of absence affect the direction of subsumption. 
	 * This is discussed in detail in Annex A.
	 * <ol>
	 * <li>Attempt to match each attribute in the predicate-attribute-group with an attribute which has 
	 * 		the same name in the candidate-attribute-group.
	 * 		<ul>
	 * 		<li>If any attribute in the predicate-attribute-group is not matched by an attribute 
	 * 			with same name in the candidate-attribute-group, the predicate-attribute-group 
	 * 			does not subsume the candidate-attribute-group.</li>
	 * 		Exit with result false.
	 * 		</ul>
	 * </li>
	 * <li>For each of the matched attributes identified in the previous step, compare the value 
	 * 		of the attribute in the predicate-attribute-group with the value of the same attribute 
	 * 		in the candidate-attribute-group.
	 * 		<ul>
	 * 		<li>If the attribute name is "finding context" (408729009) or "temporal context" (408731000), 
	 * 			the candidate-value must be equivalent to or subsumed by the predicate-value.</li>
	 * 		<li>However, if the attribute name is "associated finding" (246090004) or 
	 * 			"subject relationship context" (408732007), the direction of the test is inverted. 
	 * 			In these cases, the predicate-value must be equivalent to or subsumed by the candidate-value.</li>
	 * 		<li>If any of these tests fail, the predicate-attribute-group does not subsume the candidate-attribute-group.</li>
	 * 			Exit with result false.
	 * 		<li>Attribute values are expressions and are tested in the same way as any other 
	 * 			expression (see 6.2 and 6.3).</li>
	 * 			Expression subsumption testing is recursive where expressions include nested qualifiers.
	 * 		</ul>
	 * </li>
	 * <li>If all the tests above are successful, the predicate-attribute-group subsumes the candidate-attribute-group.</li>
	 * 		Exit with result true.
	 * 
	 * @param predicate
	 * @param candidate
	 * @return
	 */
	private boolean contextGroupWithAbsentFindingSubsumptionTest(Group predicate, Group candidate) {
		List<Attribute> predicateAttributes = predicate.getAttributes();
		List<Attribute> candidateAttributes = candidate.getAttributes();
		boolean subsumed = false;
		
		for (Attribute predicateAttribute : predicateAttributes) {
			Concept predicateNameConcept = predicateAttribute.getName();
			
			for (Attribute candidateAttribute : candidateAttributes) {
				Concept candidateNameConcept = candidateAttribute.getName();
				if (candidateNameConcept.getId().equals(predicateNameConcept.getId())) {
					if (candidateNameConcept.getId().equals(CONCEPT_ID_FINDING_CONTEXT) 
							|| candidateNameConcept.getId().equals(CONCEPT_ID_TEMPORAL_CONTEXT)) {
						subsumed = isSubsumed(SemanticUtils.getAttributeValueExpression(predicateAttribute), 
								SemanticUtils.getAttributeValueExpression(candidateAttribute));
					} else if (candidateNameConcept.getId().equals(CONCEPT_ID_ASSOCIATED_FINDING) 
							|| candidateNameConcept.getId().equals(CONCEPT_ID_SUBJECT_RELATIONSHIP_CONTEXT)) {
						subsumed = isSubsumed(SemanticUtils.getAttributeValueExpression(candidateAttribute), 
								SemanticUtils.getAttributeValueExpression(predicateAttribute));
					}
					
					if (subsumed)
						break;
				}
				
			}
			
			if (!subsumed)
				return false;
		}
		
		return true;
	}

	/**
	 * The following steps test if a predicate-attribute subsumes a candidate-attribute.
	 * <ol> 
	 * <li>Test that the candidate attribute name is either the same as or subsumed by the
	 *		predicate attribute name.</li>
	 *		<ul>
	 *		<li>If not, the predicate-attribute does not subsume the candidate-attribute</li>
	 * 			Exit with result false.
	 * 		<li>The approach to testing concept subsumption is described in section 6.3.5</li>
	 * 		</ul>
	 * <li>Test that the candidate-attribute value is equivalent to or subsumed by the 
	 * 		predicate-attribute value.
	 * 		<ul>
	 * 		<li>If not, the predicate-attribute does not subsume the candidate-attribute</li>
	 * 			Exit with result false.
	 * 		<li>Attribute values are expressions and are tested in the same way as any 
	 * 			other expression (see 6.2 and 6.3).</li>
	 * 			Expression subsumption testing is recursive where expressions include nested qualifiers.
	 * 		</ul>
	 * <li>If both the above tests are successful, the predicate-attribute subsumes the 
	 * 		candidate- attribute.</li>
	 * 		Exit with result true.
	 * </ol>
	 * 
	 * @param predicate	the predicate {@link Attribute}
	 * @param candidate	the candidate {@link Attribute}
	 * @return
	 */
	public boolean isSubsumed(Attribute predicate, Attribute candidate) {
		// attribute names
		Concept predicateNameConcept = predicate.getName();
		Concept candidateNameConcept = candidate.getName();
		if (!isSubsumed(predicateNameConcept, candidateNameConcept))
			return false;
		
		// attribute values
		Expression predicateValueExpression = SemanticUtils.getAttributeValueExpression(predicate);
		Expression candidateValueExpression = SemanticUtils.getAttributeValueExpression(candidate);

		if (!isSubsumed(predicateValueExpression, candidateValueExpression))
			return false;
		
		return true;
	}
}