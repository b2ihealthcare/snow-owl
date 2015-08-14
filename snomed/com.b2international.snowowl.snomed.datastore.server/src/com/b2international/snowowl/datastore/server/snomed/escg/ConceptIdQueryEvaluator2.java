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

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_ANCESTOR;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID;

import java.io.Serializable;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import bak.pcj.LongCollection;
import bak.pcj.LongIterator;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.commons.ClassUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.ComponentIdLongField;
import com.b2international.snowowl.datastore.index.LongDocValuesCollector;
import com.b2international.snowowl.datastore.server.snomed.SnomedComponentService;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexQueries;
import com.b2international.snowowl.snomed.datastore.escg.IQueryEvaluator;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.StatementObjectIdCollector;
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
import com.google.common.base.Preconditions;

/**
 * Evaluator service for getting SNOMED&nbsp;CT concept IDs.
 */
public class ConceptIdQueryEvaluator2 implements Serializable, IQueryEvaluator<LongSet, com.b2international.snowowl.snomed.dsl.query.RValue> {
	
	private static final long serialVersionUID = -8592143402592449211L;
	
	private final IBranchPath branchPath;

	/**
	 * Creates a query evaluator service that works on the specified branch.
	 * @param branchPath the branch path.
	 */
	public ConceptIdQueryEvaluator2(final IBranchPath branchPath) {
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
	}

	/**
	 * Evaluates the specified expression and returns with a set of SNOMED&nbsp;CT concept IDs representing the matching results.
	 */
	@Override
	public LongSet evaluate(final com.b2international.snowowl.snomed.dsl.query.RValue expression) {
		
		if (expression instanceof ConceptRef) {
			
			final ConceptRef concept = (ConceptRef) expression;
			final String conceptId = Preconditions.checkNotNull(concept.getConceptId());
			final BooleanQuery mainQuery = new BooleanQuery(true);
			final SnomedIndexServerService service = getIndexService();
			final LongDocValuesCollector collector = new LongDocValuesCollector(ComponentIdLongField.COMPONENT_ID);
			
			switch (concept.getQuantifier()) {
				
				case SELF:
					
					return new LongOpenHashSet(new long[] { Long.valueOf(conceptId) });
					
				case ANY_SUBTYPE:
					
					
					final BooleanQuery descendatQuery = new BooleanQuery(true);
					
					descendatQuery.add(createAncestorQuery(conceptId), Occur.SHOULD);
					descendatQuery.add(createParentQuery(conceptId), Occur.SHOULD);
				
					mainQuery.add(createActiveQuery(), Occur.MUST);
					mainQuery.add(createConceptTypeQuery(), Occur.MUST);
					mainQuery.add(descendatQuery, Occur.MUST);
					
					service.search(branchPath, mainQuery, collector);
					
					return new LongOpenHashSet(collector.getValues());

				case SELF_AND_ANY_SUBTYPE:

					final BooleanQuery descendatOrSelfQuery = new BooleanQuery(true);
					
					descendatOrSelfQuery.add(createAncestorQuery(conceptId), Occur.SHOULD);
					descendatOrSelfQuery.add(createParentQuery(conceptId), Occur.SHOULD);
					descendatOrSelfQuery.add(createIdQuery(conceptId), Occur.SHOULD);
				
					mainQuery.add(createActiveQuery(), Occur.MUST);
					mainQuery.add(createConceptTypeQuery(), Occur.MUST);
					mainQuery.add(descendatOrSelfQuery, Occur.MUST);
					
					service.search(branchPath, mainQuery, collector);
					
					return new LongOpenHashSet(collector.getValues());
					
				default:
					throw new IllegalArgumentException("Unknown concept quantifier type: " + concept.getQuantifier());
			}
			
		} else if (expression instanceof AttributeClause) {
			
			final AttributeClause clause = (AttributeClause) expression;
			
			final LongSet attributeIdSet = evaluate(clause.getLeft());
			final LongSet valueIdSet = evaluate(clause.getRight());

			final BooleanQuery query = new BooleanQuery(true);
			
			query.add(createActiveQuery(), Occur.MUST);
			query.add(createRelationshipTypeQuery(), Occur.MUST);
			
			final BooleanQuery negatedQuery = new BooleanQuery(true);
			//filter out the top most relationship types with index query if possible to better performance
			for (final LongIterator itr = SnomedComponentService.TOP_MOST_RELATIONSHIP_TYPE_IDS_AS_LONG.iterator(); itr.hasNext(); /*not much*/) {
				
				final long attributeId = itr.next();
				if (!attributeIdSet.contains(attributeId)) { //queries must not contain the following relationship types 
					
					negatedQuery.add(createStatementAttributeIdQuery(attributeId), Occur.MUST);
					
				}
				
			}
			
			query.add(negatedQuery, Occur.MUST_NOT);

			final StatementObjectIdCollector collector = new StatementObjectIdCollector(attributeIdSet, valueIdSet);
			
			//workaround to disable scoring on MUST_NOT boolean query. See: https://issues.apache.org/jira/browse/LUCENE-4395
			getIndexService().search(branchPath, new ConstantScoreQuery(query), collector);

			return new LongOpenHashSet(collector.getObjectIds());
			
		} else if (expression instanceof RefSet) {
			
			final RefSet refSet = (RefSet) expression;
			final String refSetId = refSet.getId();
			
			final BooleanQuery mainQuery = new BooleanQuery(true);
			final SnomedIndexServerService service = getIndexService();
			final LongDocValuesCollector collector = new LongDocValuesCollector(ComponentIdLongField.COMPONENT_ID);
			
			mainQuery.add(createActiveQuery(), Occur.MUST);
			mainQuery.add(createConceptTypeQuery(), Occur.MUST);
			mainQuery.add(createRefSetQuery(refSetId), Occur.MUST);
			
			service.search(branchPath, mainQuery, collector);
			
			return new LongOpenHashSet(collector.getValues());
			
		} else if (expression instanceof SubExpression) {
			
			return evaluate(((SubExpression) expression).getValue());
			
		} else if (expression instanceof OrClause) {
			
			final OrClause orClause = (OrClause) expression;
			final LongSet result = evaluate(orClause.getLeft());
			result.addAll(evaluate(orClause.getRight()));
			
			return result;
		
		} else if (expression instanceof AndClause) {
			
			final AndClause clause = (AndClause) expression;
			
			if (clause.getRight() instanceof NotClause) {
				
				return handleAndNot(clause.getLeft(), (NotClause) clause.getRight());
				
			} else if (clause.getLeft() instanceof NotClause) {
				
				return handleAndNot(clause.getRight(), (NotClause) clause.getLeft());
				
			} else {

				final LongSet leftIds = evaluate(clause.getLeft());
				final LongSet rightIds = evaluate(clause.getRight());
				
				return leftIds.size() < rightIds.size() 
						? new LongOpenHashSet(LongSets.intersection(leftIds, rightIds)) 
						: new LongOpenHashSet(LongSets.intersection(rightIds, leftIds));
				
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
	
		throw new IllegalArgumentException("Don't know how to expand: " + expression);
	}

	private LongSet handleAndNot(final RValue notNegated, final NotClause negated) {
		if (notNegated instanceof NotClause) {
			throw new UnsupportedOperationException("Cannot AND two NOT clauses yet");
		}
		
		final LongSet notNegatedIds = evaluate(notNegated);
		final LongSet negatedConcepts = evaluate(negated.getValue());
		
		notNegatedIds.removeAll(negatedConcepts);

		return notNegatedIds;
	}

	private LongSet getByNumericalAttributes(final LongCollection conceptIds, final NumericDataClause numericDataClause) {
		return new LongOpenHashSet();
	}

	private LongSet getByNumericalAttributesGroup(final LongCollection concepts, final NumericDataClause numericDataClause, final LongCollection substanceConcepts) {
		return new LongOpenHashSet();
	}
	
	/*returns with the server side index service*/
	private SnomedIndexServerService getIndexService() {
		return ClassUtils.checkAndCast(ApplicationContext.getInstance().getService(SnomedIndexService.class), SnomedIndexServerService.class);
	}
	
	private Query createStatementAttributeIdQuery(final long attributeId) {
		return new TermQuery(createStatementAttributeIdTerm(attributeId));
	}

	private Term createStatementAttributeIdTerm(final long attributeId) {
		return new Term(RELATIONSHIP_ATTRIBUTE_ID, IndexUtils.longToPrefixCoded(attributeId));
	}
	
	private Query createParentQuery(final String conceptId) {
		return new TermQuery(createConceptParentTerm(conceptId));
	}

	private Term createConceptParentTerm(final String conceptId) {
		return new Term(CommonIndexConstants.COMPONENT_PARENT, IndexUtils.longToPrefixCoded(conceptId));
	}

	private Query createAncestorQuery(final String conceptId) {
		return new TermQuery(createConceptAncestorTerm(conceptId));
	}

	private Term createConceptAncestorTerm(final String conceptId) {
		return new Term(CONCEPT_ANCESTOR, IndexUtils.longToPrefixCoded(conceptId));
	}

	private Query createIdQuery(final String conceptId) {
		return new ComponentIdLongField(conceptId).toQuery();
	}
	
	private Term createRefSetTerm(String refSetId) {
		return new Term(CONCEPT_REFERRING_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(refSetId));
	}
	
	private Term createMappingRefSetTerm(String refSetId) {
		return new Term(CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(refSetId));
	}
	
	private Query createRefSetQuery(String refSetId) {
		final BooleanQuery refSetQuery = new BooleanQuery(true);
		refSetQuery.add(new TermQuery(createRefSetTerm(refSetId)), Occur.SHOULD);
		refSetQuery.add(new TermQuery(createMappingRefSetTerm(refSetId)), Occur.SHOULD);
		final BooleanQuery query = new BooleanQuery(true);
		query.add(refSetQuery, Occur.MUST);
		return query;
	}
	
	private Query createConceptTypeQuery() {
		return new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(CONCEPT_NUMBER)));
	}
	
	private Query createRelationshipTypeQuery() {
		return new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(RELATIONSHIP_NUMBER)));
	}

	private Query createActiveQuery() {
		return SnomedIndexQueries.ACTIVE_COMPONENT_QUERY;
	}
	
}