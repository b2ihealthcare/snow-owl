/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.escg;

import java.io.IOException;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
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
import com.google.common.collect.ImmutableSet;

/**
 * Evaluator service for getting SNOMED&nbsp;CT concept IDs.
 * @deprecated - see {@link IEscgQueryEvaluatorService}
 */
public class ConceptIdQueryEvaluator2 implements IQueryEvaluator<LongSet, com.b2international.snowowl.snomed.dsl.query.RValue> {
	
	private final RevisionSearcher searcher;
	private final IndexQueryQueryEvaluator queryEvaluator;

	public ConceptIdQueryEvaluator2(RevisionSearcher searcher) {
		this.searcher = searcher;
		this.queryEvaluator = new IndexQueryQueryEvaluator();
	}

	/**
	 * Evaluates the specified expression and returns with a set of SNOMED&nbsp;CT concept IDs representing the matching results.
	 */
	@Override
	public LongSet evaluate(final com.b2international.snowowl.snomed.dsl.query.RValue expression) {
		
		if (expression instanceof ConceptRef) {
			final Expression conceptRefExpression = queryEvaluator.evaluate(expression);
			return getMatchingConceptIds(conceptRefExpression);
		} else if (expression instanceof AttributeClause) {
			
			final AttributeClause clause = (AttributeClause) expression;
			
			final LongSet typeIds = evaluate(clause.getLeft());
			final LongSet destinationIds = evaluate(clause.getRight());

			final Query<SnomedRelationshipIndexEntry> query = Query.select(SnomedRelationshipIndexEntry.class)
					.where(Expressions.builder()
							.filter(SnomedRelationshipIndexEntry.Expressions.active())
							.filter(SnomedRelationshipIndexEntry.Expressions.typeIds(LongSets.toStringSet(typeIds)))
							.filter(SnomedRelationshipIndexEntry.Expressions.characteristicTypeIds(ImmutableSet.of(Concepts.INFERRED_RELATIONSHIP, Concepts.ADDITIONAL_RELATIONSHIP)))
							.filter(SnomedRelationshipIndexEntry.Expressions.destinationIds(LongSets.toStringSet(destinationIds)))
							.build())
					.limit(Integer.MAX_VALUE)
					.build();
			try {
				final Hits<SnomedRelationshipIndexEntry> hits = searcher.search(query);
				final LongSet sourceIds = PrimitiveSets.newLongOpenHashSet(hits.getHits().size());
				
				for (SnomedRelationshipIndexEntry hit : hits) {
					sourceIds.add(Long.parseLong(hit.getSourceId()));
				}
				
				return sourceIds;
			} catch (IOException e) {
				throw new SnowowlRuntimeException(e);
			}
			
		} else if (expression instanceof RefSet) {
			return getMatchingConceptIds(queryEvaluator.evaluate(expression));
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
						? PrimitiveSets.newLongOpenHashSet(LongSets.intersection(leftIds, rightIds)) 
						: PrimitiveSets.newLongOpenHashSet(LongSets.intersection(rightIds, leftIds));
				
			}
			
		} else if (expression instanceof NumericDataClause) {
			return PrimitiveSets.newLongOpenHashSet();
		} else if (expression instanceof NumericDataGroupClause) {
			return PrimitiveSets.newLongOpenHashSet();
		} else if (expression instanceof NotClause) {
			throw new NotImplementedException("Can't start expression with NOT: %s", expression);
		}
	
		throw new IllegalArgumentException("Don't know how to expand: " + expression);
	}

	private LongSet getMatchingConceptIds(Expression expression) {
		final Query<SnomedConceptDocument> query = Query.select(SnomedConceptDocument.class)
				.where(expression)
				.limit(Integer.MAX_VALUE)
				.build();
		try {
			final Hits<SnomedConceptDocument> hits = searcher.search(query);
			final LongSet ids = PrimitiveSets.newLongOpenHashSet(hits.getHits().size());
			for (SnomedConceptDocument hit : hits) {
				ids.add(Long.parseLong(hit.getId()));
			}
			return ids;
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
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

}