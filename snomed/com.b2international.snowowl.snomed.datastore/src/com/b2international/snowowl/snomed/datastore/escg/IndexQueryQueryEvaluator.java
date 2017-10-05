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
package com.b2international.snowowl.snomed.datastore.escg;

import java.io.Serializable;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.dsl.query.ast.AndClause;
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
 * Query evaluator transforming a parsed {@link com.b2international.snowowl.snomed.dsl.query.RValue right value} 
 * into a {@link BooleanQuery boolean index query}.
 * @see Serializable
 * @see IQueryEvaluator
 */
public class IndexQueryQueryEvaluator implements Serializable, IQueryEvaluator<BooleanQuery, com.b2international.snowowl.snomed.dsl.query.RValue> {

	private static final long serialVersionUID = 1976491781836431852L;

	@Override
	public BooleanQuery evaluate(final com.b2international.snowowl.snomed.dsl.query.RValue expression) {
		
		final BooleanQuery mainQuery = new BooleanQuery();
		
		if (expression instanceof ConceptRef) {

			final ConceptRef concept = (ConceptRef) expression;
			
			final StringBuilder sb = new StringBuilder();
			sb.append("Concept ID was null for ");
			sb.append(concept);
			sb.append(".");
			
			final String conceptId = Preconditions.checkNotNull(concept.getConceptId(), sb.toString());
	
			switch (concept.getQuantifier()) {
				case SELF:
					
					mainQuery.add(SnomedMappings.newQuery().id(conceptId).matchAll(), Occur.MUST);
					return mainQuery;
					
				case ANY_SUBTYPE:
					mainQuery.add(SnomedMappings.newQuery().parent(conceptId).ancestor(conceptId).matchAny(), Occur.MUST);
					return mainQuery;
					
				case SELF_AND_ANY_SUBTYPE:
					
					mainQuery.add(SnomedMappings.newQuery().id(conceptId).parent(conceptId).ancestor(conceptId).matchAny(), Occur.MUST);
					return mainQuery;
					
				default:
					throw new EscgParseFailedException("Unknown concept quantifier type: " + concept.getQuantifier());
			}
			
			
		} else if (expression instanceof RefSet) {
			
			final RefSet refSet = (RefSet) expression;
			final String refSetId = refSet.getId();
			throw new EscgParseFailedException("Refset search not handled: " +  refSetId);
			
		} else if (expression instanceof SubExpression) {
			
			final SubExpression subExpression = (SubExpression) expression;
			return evaluate(subExpression.getValue());
			
		} else if (expression instanceof OrClause) {
			
			final OrClause orClause = (OrClause) expression;
			
			final BooleanQuery orQuery = new BooleanQuery();
			
			orQuery.add(evaluate(orClause.getLeft()), Occur.SHOULD);
			orQuery.add(evaluate(orClause.getRight()), Occur.SHOULD);
			
			mainQuery.add(orQuery, Occur.MUST);
			return mainQuery;
			
		} else if (expression instanceof AndClause) {

			final AndClause clause = (AndClause) expression;
			
			if (clause.getRight() instanceof NotClause) {
				
				final NotClause notClause = (NotClause) clause.getRight();
				
				return handleAndNot(mainQuery, clause.getLeft(), notClause);
				
			} else if (clause.getLeft() instanceof NotClause) {
				
				final NotClause notClause = (NotClause) clause.getLeft();
				
				return handleAndNot(mainQuery, clause.getRight(), notClause);
				
			} else {

				final BooleanQuery leftQuery = evaluate(clause.getLeft());
				final BooleanQuery rightQuery = evaluate(clause.getRight());
				
				mainQuery.add(leftQuery, Occur.MUST);
				mainQuery.add(rightQuery, Occur.MUST);
				
				return mainQuery;
				
			}
			
		} else if (expression instanceof NumericDataClause) {
			throw new EscgParseFailedException();
		} else if (expression instanceof NumericDataGroupClause) {
			throw new EscgParseFailedException();
		} else if (expression instanceof NotClause) {
			throw new EscgParseFailedException();
		}
		
		throw new EscgParseFailedException("Do not know how to evaluate " + expression);
	}

	private BooleanQuery handleAndNot(final BooleanQuery mainQuery, final RValue notNegated, final NotClause negated) {
		if (notNegated instanceof NotClause) {
			throw new EscgParseFailedException("Cannot AND two NOT clauses yet");
		}
		
		final BooleanQuery notNegatedQuery = evaluate(notNegated);
		final BooleanQuery negatedConceptsQuery = evaluate(negated.getValue());
		
		mainQuery.add(notNegatedQuery, Occur.MUST);
		mainQuery.add(negatedConceptsQuery, Occur.MUST_NOT);
		
		return mainQuery;
	}
	
}