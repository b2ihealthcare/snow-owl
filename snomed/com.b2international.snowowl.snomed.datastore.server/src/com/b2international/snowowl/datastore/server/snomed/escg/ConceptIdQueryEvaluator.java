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
package com.b2international.snowowl.datastore.server.snomed.escg;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FieldCacheTermsFilter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import bak.pcj.LongCollection;
import bak.pcj.LongIterator;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexService;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.escg.IQueryEvaluator;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.dsl.query.ast.AndClause;
import com.b2international.snowowl.snomed.dsl.query.ast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.ast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.ast.NotClause;
import com.b2international.snowowl.snomed.dsl.query.ast.NumericDataClause;
import com.b2international.snowowl.snomed.dsl.query.ast.NumericDataGroupClause;
import com.b2international.snowowl.snomed.dsl.query.ast.OrClause;
import com.b2international.snowowl.snomed.dsl.query.ast.RValue;
import com.b2international.snowowl.snomed.dsl.query.ast.RefSet;
import com.b2international.snowowl.snomed.dsl.query.ast.SubExpression;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;

/**
 * 
 * @deprecated use {@link ConceptIdQueryEvaluator2} instead.
 */
public class ConceptIdQueryEvaluator implements Serializable, IQueryEvaluator<LongSet, RValue> {
	
	private static final long serialVersionUID = -8592143402592449211L;
	private final IBranchPath branchPath;

	public ConceptIdQueryEvaluator() {
		this(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE));
	}

	/**
	 * @param branchPath the branch path.
	 */
	public ConceptIdQueryEvaluator(final IBranchPath branchPath) {
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
	}

	public LongSet evaluate(final RValue expression) {
		// TODO: this explicit casting is ugly
		SnomedIndexServerService indexService = (SnomedIndexServerService) ApplicationContext.getInstance().getService(SnomedIndexService.class);
		
		try {
			if (expression instanceof ConceptRef) {
				ConceptRef conceptRef = (ConceptRef) expression;
				LongOpenHashSet conceptIdSet = new LongOpenHashSet();
				switch (conceptRef.getQuantifier()) {
				case SELF:
					conceptIdSet.add(Long.valueOf(conceptRef.getConceptId()));
					return conceptIdSet;
				case SELF_AND_ANY_SUBTYPE:
					LongSet anySubTypeConceptIdSet = getAnySubTypeConceptIds(expression, indexService, conceptRef);
					anySubTypeConceptIdSet.add(Long.valueOf(conceptRef.getConceptId()));
					return anySubTypeConceptIdSet;
				case ANY_SUBTYPE:
					return getAnySubTypeConceptIds(expression, indexService, conceptRef);
				}
			} else if (expression instanceof AttributeClause) {
				AttributeClause clause = (AttributeClause) expression;
				LongSet attributeIdSet = new LongOpenHashSet();
				attributeIdSet.addAll(evaluate(clause.getLeft()));
				LongSet valueIdSet = new LongOpenHashSet();
				valueIdSet.addAll(evaluate(clause.getRight()));
				LongSet objectsByAttributes = getObjectsByAttributes(attributeIdSet, valueIdSet, indexService);
				return objectsByAttributes;
			} else if (expression instanceof RefSet) {
				return getRefSetMemberConceptIds((RefSet)expression, indexService);
			} else if (expression instanceof SubExpression) {
				return evaluate(((SubExpression) expression).getValue());
			} else if (expression instanceof OrClause) {
				final OrClause clause = (OrClause) expression;
				final LongSet result = evaluate(clause.getLeft());
				result.addAll(evaluate(clause.getRight()));
				return result;
			} else if (expression instanceof AndClause) {
				final AndClause clause = (AndClause) expression;
				if (clause.getRight() instanceof NotClause) {
					return handleAndNot(clause.getLeft(), (NotClause) clause.getRight());
				} else if (clause.getLeft() instanceof NotClause) {
					return handleAndNot(clause.getRight(), (NotClause) clause.getLeft());
				} else {
					final LongSet result = new LongOpenHashSet();
					final LongSet leftIds = evaluate(clause.getLeft());
					final LongSet rightIds = evaluate(clause.getRight());
					LongIterator leftIdsIterator = leftIds.iterator();
					while (leftIdsIterator.hasNext()) {
						long left = leftIdsIterator.next();
						if (rightIds.contains(left)) {
							result.add(left);
						}
					}
					return result;
				}
			} else if (expression instanceof NumericDataClause) {
				final NumericDataClause clause = (NumericDataClause) expression;
				return getByNumericalAttributes(evaluate(clause.getConcepts()), clause);
			} else if (expression instanceof NumericDataGroupClause) {
				final NumericDataGroupClause clause = (NumericDataGroupClause) expression;
				return getByNumericalAttributesGroup(evaluate(clause.getConcepts()), clause.getNumericData(), evaluate(clause.getSubstances()));
			} else if (expression instanceof NotClause) {
				throw new UnsupportedOperationException("Cannot NOT yet: " + expression);
			}

		} catch (IOException e) {
			throw new RuntimeException("Error when evaluating expression: " + expression, e);
		}
	
		throw new IllegalArgumentException("Don't know how to expand: " + expression);
	}

	private LongSet getRefSetMemberConceptIds(RefSet refSet, AbstractIndexService<?> indexService) throws IOException {
		LongOpenHashSet set = new LongOpenHashSet();
		String refSetId = refSet.getId();
		TermQuery refSetIdQuery = new TermQuery(new Term(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(refSetId)));
		TermQuery activeQuery = new TermQuery(new Term(SnomedIndexBrowserConstants.COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1)));
		BooleanQuery query = new BooleanQuery();
		query.add(refSetIdQuery, Occur.MUST);
		query.add(activeQuery, Occur.MUST);
		DocIdCollector collector = DocIdCollector.create(indexService.maxDoc(branchPath));
		indexService.search(branchPath, query, collector);
		DocIdsIterator docIdsIterator = collector.getDocIDs().iterator();
		while (docIdsIterator.next()) {
			int docID = docIdsIterator.getDocID();
			Document document = indexService.document(branchPath, docID, 
					ImmutableSet.of(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID));
			set.add(Long.valueOf(document.get(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID)));
		}
		return set;
	}

	private LongSet getAnySubTypeConceptIds(final com.b2international.snowowl.snomed.dsl.query.RValue expression,
			AbstractIndexService indexService, ConceptRef conceptRef) throws IOException {
		LongOpenHashSet conceptIdSet = new LongOpenHashSet();
		final BooleanQuery query = new BooleanQuery();
		final Query parentQuery = new TermQuery(new Term(SnomedIndexBrowserConstants.CONCEPT_PARENT, IndexUtils.longToPrefixCoded(conceptRef.getConceptId())));
		query.add(parentQuery, Occur.SHOULD);
		final Query ancestorQuery = new TermQuery(new Term(SnomedIndexBrowserConstants.CONCEPT_ANCESTOR, IndexUtils.longToPrefixCoded(conceptRef.getConceptId())));
		query.add(ancestorQuery, Occur.SHOULD);
		DocIdCollector collector = DocIdCollector.create(indexService.maxDoc(branchPath));
		indexService.search(branchPath, query, collector);
		DocIdsIterator docIdsIterator = collector.getDocIDs().iterator();
		while (docIdsIterator.next()) {
			int docID = docIdsIterator.getDocID();
			Document doc = indexService.document(branchPath, docID, ImmutableSet.of(SnomedIndexBrowserConstants.COMPONENT_ID));
			conceptIdSet.add(Long.valueOf(doc.get(SnomedIndexBrowserConstants.COMPONENT_ID)));
		}
		return conceptIdSet;
	}

	private LongSet handleAndNot(final RValue notNegated, final NotClause negated) {
		if (notNegated instanceof NotClause) {
			throw new UnsupportedOperationException("Cannot AND two NOT clauses yet");
		}
		LongSet notNegatedIds = evaluate(notNegated);
		if (!(notNegatedIds instanceof HashSet<?>)) {
			notNegatedIds = new LongOpenHashSet(notNegatedIds);
		}
		final LongSet negatedConcepts = evaluate(negated.getValue());
		LongIterator negatedConceptsIterator = negatedConcepts.iterator();
		while (negatedConceptsIterator.hasNext()) {
			notNegatedIds.remove(negatedConceptsIterator.next());
		}
		return notNegatedIds;
	}

	/**
	 * <p>Returns all objects where the following relationship exists {object, attribute[i], value[j]} for
	 * each attributeId, valueId pair from the specified sets.</p>
	 *
	 * <p>For example for the following attributeIds {a1, a2} and valueIds {v1, v2}, an objectId o1 will be
	 * returned if there is a relationship: {o1, a1, v1} OR {o1, a1, v2} OR {o1, a2, v1} OR {o1, a2, v2}</p>
	 * 
	 * @param attributes collection of attributes for the query
	 * @param values collection of values for the query
	 * @param indexService 
	 * @return a set of all objects that match the query
	 * @throws IOException 
	 */
	private LongSet getObjectsByAttributes(final LongCollection attributes, final LongCollection values, AbstractIndexService<?> indexService) throws IOException {
		LongOpenHashSet objectIdSet = new LongOpenHashSet();
		DocIdCollector collector = DocIdCollector.create(indexService.maxDoc(branchPath));
		String[] attributeIdStringArray = Lists.transform(Longs.asList(attributes.toArray()), Functions.toStringFunction()).toArray(new String[0]);
		FieldCacheTermsFilter attributeIdFilter = new FieldCacheTermsFilter(SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID, attributeIdStringArray);
		String[] valueIdStringArray = Lists.transform(Longs.asList(values.toArray()), Functions.toStringFunction()).toArray(new String[0]);
		FieldCacheTermsFilter valueIdFilter = new FieldCacheTermsFilter(SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID, valueIdStringArray);
		BooleanFilter filter = new BooleanFilter();
		filter.add(attributeIdFilter, Occur.MUST);
		filter.add(valueIdFilter, Occur.MUST);
		indexService.search(branchPath, new MatchAllDocsQuery(), filter, collector);
		DocIdsIterator docIdsIterator = collector.getDocIDs().iterator();
		while (docIdsIterator.next()) {
			int docID = docIdsIterator.getDocID();
			Document document = indexService.document(branchPath, docID, ImmutableSet.of(SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID));
			Long objectId = Long.valueOf(document.get(SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID));
			objectIdSet.add(objectId);
		}
		return objectIdSet;
	}

	private LongSet getByNumericalAttributes(final LongCollection conceptIds, final NumericDataClause numericDataClause) {

		final LongSet matchingConceptIds = new LongOpenHashSet();

		//TODO add implementation.
		//		Operator operator = numericDataClause.getOperator();
		//		double value = numericDataClause.getValue();
		//		String unit = numericDataClause.getUnit();
		//		
		//		final SnomedRefSetMembershipLookupService service = new SnomedRefSetMembershipLookupService();
		//		
		//		for(ConceptMini concept : concepts) {
		//			Set<SnomedConcreteDataTypeRefSetMemberIndexEntry> concreteDomainElements = Sets.newHashSet(service.getRelationshipDataTypesForConcept(concept.getId()));
		//			
		//			for(SnomedConcreteDataTypeRefSetMemberIndexEntry numericData : concreteDomainElements){
		//
		//				try {
		//					boolean valueMatch = false;
		//					boolean unitMatch = false;
		//
		//					//check value
		//					double storedValue = Double.valueOf(numericData.getValue());
		//					
		//					switch (operator) {
		//					case EQUALS:
		//						valueMatch = value == storedValue;
		//						break;
		//					case GREATER_THAN:
		//						valueMatch = storedValue > value;
		//						break;
		//					case LESS_THAN:
		//						valueMatch = storedValue < value;
		//						break;
		//					case GREATER_EQUALS_TO:
		//						valueMatch = storedValue >= value;
		//						break;
		//					case LESS_EQUALS_TO:
		//						valueMatch = storedValue <= value;
		//						break;
		//					case NOT_EQUALS:
		//						valueMatch = storedValue != value;
		//						break;						
		//					default:
		//						break;
		//					}
		//					
		//					//check unit
		//					unitMatch = numericData.getUnit().getLiteral().equals(unit);
		//					
		//					if(valueMatch
		//							&& unitMatch){
		//						matchingConcepts.add(concept);
		//						break;
		//					}					
		//					
		//				} catch (NumberFormatException e) {
		//					System.out.println("The numeric data type value "+ numericData.getValue() +" could not be parsed as an number.");
		//					continue;
		//				}
		//				
		//			}			
		//		}

		return matchingConceptIds;
	}

	private LongSet getByNumericalAttributesGroup(final LongCollection concepts, final NumericDataClause numericDataClause, final LongCollection substanceConcepts) {

		final LongSet matchingConceptIds = new LongOpenHashSet();

		//TODO add implementation.
		//		Operator operator = numericDataClause.getOperator();
		//		double numericValue = numericDataClause.getValue();
		//		String unit = numericDataClause.getUnit();
		//		
		//		for(ConceptMini concept : concepts) {
		//			Set<SnomedConcreteDataTypeRefSetMemberIndexEntry> concreteDomainElements = Sets.newHashSet(service.getRelationshipDataTypesForConcept(concept.getId()));
		//			
		//			for(SnomedConcreteDataTypeRefSetMemberIndexEntry numericData : concreteDomainElements){
		//
		//				try {
		//					boolean valueMatch = false;
		//					boolean unitMatch = false;
		//					boolean substanceMatch = false;
		//
		//					//check value
		//					double storedValue = Double.valueOf(numericData.getValue());
		//					
		//					switch (operator) {
		//					case EQUALS:
		//						valueMatch = numericValue == storedValue;
		//						break;
		//					case GREATER_THAN:
		//						valueMatch = storedValue > numericValue;
		//						break;
		//					case LESS_THAN:
		//						valueMatch = storedValue < numericValue;
		//						break;
		//					case GREATER_EQUALS_TO:
		//						valueMatch = storedValue >= numericValue;
		//						break;
		//					case LESS_EQUALS_TO:
		//						valueMatch = storedValue <= numericValue;
		//						break;
		//					case NOT_EQUALS:
		//						valueMatch = storedValue != numericValue;
		//						break;						
		//					default:
		//						break;
		//					}
		//					
		//					//check unit
		//					unitMatch = numericData.getUnit().getLiteral().equals(unit);
		//					
		//					//check substance
		//					RelationshipMini statement = Preconditions.checkNotNull(((SnomedClientStatementBrowser) statementBrowser).getStatement(numericData.getReferencedComponentId()));
		//					ConceptMini substance = statement.getValue();
		//					substanceMatch = substanceConcepts.contains(substance);					
		//					
		//					if(valueMatch
		//							&& unitMatch
		//								&& substanceMatch){
		//						matchingConcepts.add(concept);
		//						break;
		//					}					
		//					
		//				} catch (NumberFormatException e) {
		//					System.out.println("The numeric data type value "+ numericData.getValue() +" could not be parsed as an number.");
		//					continue;
		//				}
		//				
		//			}			
		//		}

		return matchingConceptIds;
	}
}