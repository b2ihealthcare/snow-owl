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

import java.io.Serializable;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Query;

import com.b2international.commons.ClassUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.datastore.index.LongDocValuesCollector;
import com.b2international.snowowl.datastore.server.snomed.SnomedComponentService;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
import com.b2international.snowowl.snomed.datastore.escg.IQueryEvaluator;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.StatementObjectIdCollector;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
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
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Preconditions;

import bak.pcj.LongCollection;
import bak.pcj.LongIterator;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

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
			final BooleanQuery mainQuery = (BooleanQuery) SnomedMappings.newQuery().active().concept().matchAll();
			final SnomedIndexServerService service = getIndexService();
			final LongDocValuesCollector collector = new LongDocValuesCollector(SnomedMappings.id().fieldName());
			
			switch (concept.getQuantifier()) {
				
				case SELF:
					return new LongOpenHashSet(new long[] { Long.parseLong(conceptId) });
					
				case ANY_SUBTYPE:
					final Query descendatQuery = SnomedMappings.newQuery().parent(conceptId).ancestor(conceptId).matchAny();
					mainQuery.add(descendatQuery, Occur.MUST);
					service.search(branchPath, mainQuery, collector);
					return new LongOpenHashSet(collector.getValues());

				case SELF_AND_ANY_SUBTYPE:
					mainQuery.add(SnomedMappings.newQuery().id(conceptId).parent(conceptId).ancestor(conceptId).matchAny(), Occur.MUST);
					service.search(branchPath, mainQuery, collector);
					return new LongOpenHashSet(collector.getValues());
					
				default:
					throw new IllegalArgumentException("Unknown concept quantifier type: " + concept.getQuantifier());
			}
			
		} else if (expression instanceof AttributeClause) {
			
			final AttributeClause clause = (AttributeClause) expression;
			
			final LongSet attributeIdSet = evaluate(clause.getLeft());
			final LongSet valueIdSet = evaluate(clause.getRight());

			final BooleanQuery query = (BooleanQuery) SnomedMappings.newQuery().active().relationship().matchAll();
			
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
			
			final Query query = SnomedMappings.newQuery()
					.active()
					.memberRefSetId(refSetId)
					.and(SnomedMappings.newQuery()
							.memberRefSetType(SnomedRefSetType.SIMPLE)
							.memberRefSetType(SnomedRefSetType.SIMPLE_MAP)
							.memberRefSetType(SnomedRefSetType.ATTRIBUTE_VALUE)
							.matchAny())
					.matchAll();
			final SnomedIndexServerService service = getIndexService();
			final LongDocValuesCollector collector = new LongDocValuesCollector(SnomedMappings.memberReferencedComponentId().fieldName());
			service.search(branchPath, query, collector);
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
			throw new NotImplementedException("Can't start expression with NOT: %s", expression);
		}
	
		throw new IllegalArgumentException("Don't know how to expand: " + expression);
	}

	private LongSet handleAndNot(final RValue notNegated, final NotClause negated) {
		if (notNegated instanceof NotClause) {
			throw new NotImplementedException("Cannot AND two NOT clauses yet");
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
		return SnomedMappings.newQuery().relationshipType(attributeId).matchAll();
	}

}