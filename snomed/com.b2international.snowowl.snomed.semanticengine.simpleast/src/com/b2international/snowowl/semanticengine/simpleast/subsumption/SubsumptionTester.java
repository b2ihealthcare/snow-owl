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
package com.b2international.snowowl.semanticengine.simpleast.subsumption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeClauseList;
import com.b2international.snowowl.semanticengine.simpleast.utils.QueryAstUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.SnomedHierarchy;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

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
	
	private final String branch;
	
	public SubsumptionTester(String branch) {
		this.branch = branch;
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
	 * @param predicateRoot	the predicate Expression in short normal form
	 * @param candidateRoot	the candidate Expression in long normal form
	 * @return
	 */
	public boolean isSubsumed(RValue predicateRoot, RValue candidateRoot) {
		// focus concepts
		Collection<ConceptRef> predicateFocusConcepts = QueryAstUtils.getFocusConcepts(predicateRoot);
		Collection<ConceptRef> candidateFocusConcepts = QueryAstUtils.getFocusConcepts(candidateRoot);
		for (ConceptRef predicateFocusConcept : predicateFocusConcepts) {
			// check if it subsumes at least one candidate focus concept
			boolean subsumed = false;
			for (ConceptRef candidateFocusConcept : candidateFocusConcepts) {
				subsumed = isSubsumed(predicateFocusConcept, candidateFocusConcept);
				if (subsumed)
					break;
			}
			if (!subsumed)
				return false;
		}

		// attribute groups
		List<AttributeClause> predicateUngroupedAttributes = QueryAstUtils.getUngroupedAttributes(predicateRoot);
		List<AttributeClauseList> predicateAttributeLists = getAttributeLists(predicateRoot);
		List<AttributeClause> candidateUngroupedAttributes = QueryAstUtils.getUngroupedAttributes(candidateRoot);
		List<AttributeClauseList> candidateAttributeLists = getAttributeLists(candidateRoot);
		
		for (AttributeClauseList predicateAttributeGroup : predicateAttributeLists) {
			// check if it subsumes at least one candidate attribute group
			boolean subsumed = false;
			for (AttributeClauseList candidateAttributeGroup : candidateAttributeLists) {
				subsumed = isSubsumed(predicateAttributeGroup, candidateAttributeGroup);
				if (subsumed)
					break;
			}
			if (!subsumed)
				return false;
		}
		
		// ungrouped attributes
		List<AttributeClause> groupedCandidateAttributes = new ArrayList<AttributeClause>();
		for (AttributeClauseList candidateAttributeGroup : candidateAttributeLists) {
			List<AttributeClause> attributes = candidateAttributeGroup.getAttributeClauses();
			groupedCandidateAttributes.addAll(attributes);
		}
		
		List<AttributeClause> allCandidateAttributes = new ArrayList<AttributeClause>(candidateUngroupedAttributes);
		allCandidateAttributes.addAll(groupedCandidateAttributes);
		
		for (AttributeClause ungroupedPredicateAttribute : predicateUngroupedAttributes) {
			// Test that each ungrouped attribute in the normalized-predicate subsumes at least one
			// attribute (either grouped or ungrouped) in the normalized-candidate.
			boolean subsumed = false;
			for (AttributeClause candidateAttribute : allCandidateAttributes) {
				subsumed = isSubsumed(ungroupedPredicateAttribute, candidateAttribute);
				if (subsumed)
					break;
			}
			if (!subsumed)
				return false;
		}
		
		return true;
	}

	private List<AttributeClauseList> getAttributeLists(RValue predicateRoot) {
		List<AttributeClauseGroup> predicateAttributeGroups = QueryAstUtils.getAttributeGroups(predicateRoot);
		List<AttributeClauseList> predicateAttributeLists = Lists.newArrayList();
		for (AttributeClauseGroup attributeClauseGroup : predicateAttributeGroups) {
			AttributeClauseList attributeClauseList = new AttributeClauseList();
			List<AttributeClause> attributes = QueryAstUtils.getUngroupedAttributes(attributeClauseGroup.getValue());
			attributeClauseList.getAttributeClauses().addAll(attributes);
			predicateAttributeLists.add(attributeClauseList);
		}
		return predicateAttributeLists;
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
	public boolean isSubsumed(ConceptRef predicate, ConceptRef candidate) {
		return isSubsumed(predicate.getConceptId(), candidate.getConceptId());
	}
		
	public boolean isSubsumed(String predicate, String candidate) {
		final SnomedConcept predicateConceptMini = getConcept(predicate);
		final SnomedConcept candidateConceptMini = getConcept(candidate);
		return isSubsumed(predicateConceptMini, candidateConceptMini);
	}
	
	private SnomedConcept getConcept(String conceptId) {
		return SnomedRequests.prepareGetConcept(conceptId).build(SnomedDatastoreActivator.REPOSITORY_UUID, branch).execute(getBus()).getSync();
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
	 * @param predicate	the predicate {@link SnomedConcept}
	 * @param candidate	the candidate {@link SnomedConcept}
	 * @return
	 */
	public boolean isSubsumed(SnomedConcept predicate, SnomedConcept candidate) {
		final String candidateId;
		if (candidate.isActive()) {
			candidateId = candidate.getId();
		} else {
			String replacementConceptId = null;
			
			final SnomedRelationships outboundRelationships = SnomedRequests.prepareSearchRelationship()
					.all()
					.filterByActive(true)
					.filterBySource(candidate.getId())
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
					.execute(getBus())
					.getSync();

			for (SnomedRelationship relationship : outboundRelationships) {
				if (relationship.getTypeId().equals(CONCEPT_ID_SAME_AS) || relationship.getTypeId().equals(CONCEPT_ID_REPLACED_BY)) {
					replacementConceptId = relationship.getDestinationId();
					break;
				}
			}
			
			candidateId = replacementConceptId;
		}
		
		// if no replacement concept found, the candidate is not considered subsumed
		if (Strings.isNullOrEmpty(candidateId)) {
			return false;
		}
		
		if (predicate.getId().equals(candidateId)) {
			return true;
		}
		
		return SnomedRequests.prepareSearchConcept()
			.setLimit(0)
			.filterByAncestor(predicate.getId())
			.filterById(candidateId)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
			.execute(getBus())
			.getSync().getTotal() > 0;
	}

	private IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
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
	 * @param predicateId	the predicate {@link SnomedConceptDocument}
	 * @param candidateId	the candidate {@link SnomedConceptDocument}
	 * @param hierarchy represents the hierarchy between the active concepts
	 * @return
	 */
	public boolean isSubsumed(final long predicateId, final long candidateId, final SnomedHierarchy hierarchy) {
		
		long candidateIdCopy = candidateId; 
		
		if (!hierarchy.isActive(candidateId)) {
			
			long replacementConceptId = -1L;
			
			// FIXME follow historical association members if the concept is inactive
//			final SnomedRelationshipReplacedByOrSameAsQueryAdapter adapter = new SnomedRelationshipReplacedByOrSameAsQueryAdapter(candidateId);			
//			
//			final Collection<SnomedRelationshipIndexEntry> entires = ApplicationContext.getInstance().getService(SnomedClientIndexService.class).search(adapter, 1);
//			
//			if (!CompareUtils.isEmpty(entires)) {
//				
//				final SnomedRelationshipIndexEntry entry = entires.iterator().next();
//				replacementConceptId = Long.parseLong(entry.getValueId());
//				candidateIdCopy = replacementConceptId;
//				
//			}
			
			// if no replacement concept found, the candidate is not considered subsumed
			if (-1L == replacementConceptId) {
				
				return false;
				
			}
			
		} 
		
		if (predicateId == candidateIdCopy) {
			
			return true;
			
		}

		return hierarchy.getAllSuperTypeIds(candidateIdCopy).contains(predicateId);
		
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
	 * @param predicate	the predicate {@link AttributeGroup}
	 * @param candidate	the candidate {@link AttributeGroup}
	 * @return
	 */
	public boolean isSubsumed(AttributeClauseList predicate, AttributeClauseList candidate) {
		for (AttributeClause predicateAttribute : predicate.getAttributeClauses()) {
			ConceptRef predicateNameConcept = (ConceptRef) predicateAttribute.getLeft();
			if (!predicateNameConcept.getConceptId().equals(CONCEPT_ID_FINDING_CONTEXT))
				continue;
			
			RValue predicateValue = predicateAttribute.getRight();
			if (predicateValue instanceof ConceptRef) {
				ConceptRef predicateValueConceptGroup = (ConceptRef) predicateValue;
				if (predicateValueConceptGroup.getConceptId().equals(CONCEPT_ID_KNOWN_ABSENT) || predicateValueConceptGroup.getConceptId().equals(CONCEPT_ID_DEFINITELY_NOT_PRESENT))
					return contextAttributeGroupWithAbsentFindingSubsumptionTest(predicate, candidate);
			}
		}
		
		return normalAttributeGroupSubsumptionTest(predicate, candidate);
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
	private boolean normalAttributeGroupSubsumptionTest(AttributeClauseList predicate, AttributeClauseList candidate) {
		List<AttributeClause> predicateAttributes = predicate.getAttributeClauses();
		List<AttributeClause> candidateAttributes = candidate.getAttributeClauses();
		
		for (AttributeClause predicateAttribute : predicateAttributes) {
			boolean subsumed = false;
			for (AttributeClause candidateAttribute : candidateAttributes) {
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
	private boolean contextAttributeGroupWithAbsentFindingSubsumptionTest(AttributeClauseList predicate, AttributeClauseList candidate) {
		List<AttributeClause> predicateAttributes = predicate.getAttributeClauses();
		List<AttributeClause> candidateAttributes = candidate.getAttributeClauses();
		
		for (AttributeClause predicateAttribute : predicateAttributes) {
			boolean subsumed = false;
			ConceptRef predicateNameConcept = QueryAstUtils.getAttributeNameConcept(predicateAttribute);
			
			for (AttributeClause candidateAttribute : candidateAttributes) {
				ConceptRef candidateNameConcept = QueryAstUtils.getAttributeNameConcept(candidateAttribute);
				if (candidateNameConcept.getConceptId().equals(predicateNameConcept.getConceptId())) {
					if (candidateNameConcept.getConceptId().equals(CONCEPT_ID_FINDING_CONTEXT) 
							|| candidateNameConcept.getConceptId().equals(CONCEPT_ID_TEMPORAL_CONTEXT)) {
						subsumed = isSubsumed(QueryAstUtils.getAttributeValueExpression(predicateAttribute), 
								QueryAstUtils.getAttributeValueExpression(candidateAttribute));
					} else if (candidateNameConcept.getConceptId().equals(CONCEPT_ID_ASSOCIATED_FINDING) 
							|| candidateNameConcept.getConceptId().equals(CONCEPT_ID_SUBJECT_RELATIONSHIP_CONTEXT)) {
						subsumed = isSubsumed(QueryAstUtils.getAttributeValueExpression(candidateAttribute), 
								QueryAstUtils.getAttributeValueExpression(predicateAttribute));
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
	 * @param predicate	the predicate {@link AttributeClause}
	 * @param candidate	the candidate {@link AttributeClause}
	 * @return
	 */
	public boolean isSubsumed(AttributeClause predicate, AttributeClause candidate) {
		// attribute names
		com.b2international.snowowl.snomed.dsl.query.queryast.RValue predicateNameLValue = predicate.getLeft();
		if (!(predicateNameLValue instanceof ConceptRef))
			throw new UnsupportedOperationException("Attribute name '" + predicateNameLValue + "' is not supported.");
		com.b2international.snowowl.snomed.dsl.query.queryast.RValue candidateNameLValue = candidate.getLeft();
		if (!(candidateNameLValue instanceof ConceptRef))
			throw new UnsupportedOperationException("Attribute name '" + candidateNameLValue + "' is not supported.");
		
		ConceptRef predicateNameConceptRef = (ConceptRef) predicateNameLValue;
		ConceptRef candidateNameConceptRef = (ConceptRef) candidateNameLValue;
		
		
		if (!isSubsumed(predicateNameConceptRef, candidateNameConceptRef))
			return false;
		
		// attribute values
		RValue predicateValueExpressionRoot = QueryAstUtils.getAttributeValueExpression(predicate);
		RValue candidateValueExpressionRoot = QueryAstUtils.getAttributeValueExpression(candidate);

		if (!isSubsumed(predicateValueExpressionRoot, candidateValueExpressionRoot))
			return false;
		
		return true;
	}
	
}