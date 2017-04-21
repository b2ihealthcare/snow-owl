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

import static com.b2international.snowowl.datastore.index.RevisionDocument.Expressions.id;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.ancestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.parents;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.referringMappingRefSet;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.referringRefSet;

import java.util.Collections;
import java.util.Set;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
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
import com.google.common.collect.ImmutableSortedSet;

/**
 * Query evaluator transforming a parsed {@link com.b2international.snowowl.snomed.dsl.query.RValue right value} 
 * into a {@link Expression expression}.
 * @deprecated - see {@link IEscgQueryEvaluatorService}
 */
public class IndexQueryQueryEvaluator implements IQueryEvaluator<Expression, com.b2international.snowowl.snomed.dsl.query.RValue> {

	/**
	 * A sorted set of the top most relationship type concept IDs.
	 * <p>
	 * <b>NOTE:&nbsp;</b>Just for estimation.
	 */
	public static final Set<String> TOP_MOST_RELATIONSHIP_TYPE_IDS = ImmutableSortedSet.<String>of(
			Concepts.IS_A,
			Concepts.FINDING_SITE,
			Concepts.HAS_ACTIVE_INGREDIENT,
			Concepts.METHOD,
			Concepts.MORPHOLOGY,
			Concepts.PART_OF,
			Concepts.HAS_DOSE_FORM,
			Concepts.PROCEDURE_SITE_DIRECT,
			Concepts.INTERPRETS,
			Concepts.CAUSATIVE_AGENT	
	);
	
	@Override
	public Expression evaluate(final com.b2international.snowowl.snomed.dsl.query.RValue expression) {
		if (expression instanceof ConceptRef) {
			final ConceptRef concept = (ConceptRef) expression;
			final String conceptId = Preconditions.checkNotNull(concept.getConceptId(), "conceptId");
	
			switch (concept.getQuantifier()) {
				case SELF:
					return id(conceptId);
				case ANY_SUBTYPE:
					return Expressions.builder()
								.should(parents(Collections.singleton(conceptId)))
								.should(ancestors(Collections.singleton(conceptId)))
								.build();
					
				case SELF_AND_ANY_SUBTYPE:
					return Expressions.builder()
								.should(id(conceptId))
								.should(parents(Collections.singleton(conceptId)))
								.should(ancestors(Collections.singleton(conceptId)))
								.build();
					
				default:
					throw new EscgParseFailedException("Unknown concept quantifier type: " + concept.getQuantifier());
			}
			
			
		} else if (expression instanceof RefSet) {
			final String refSetId = ((RefSet) expression).getId();
			return Expressions.builder()
						.should(referringRefSet(refSetId))
						.should(referringMappingRefSet(refSetId))
						.build();
		} else if (expression instanceof SubExpression) {
			final SubExpression subExpression = (SubExpression) expression;
			return evaluate(subExpression.getValue());
		} else if (expression instanceof OrClause) {
			final OrClause orClause = (OrClause) expression;
			return Expressions.builder()
						.should(evaluate(orClause.getLeft()))
						.should(evaluate(orClause.getRight()))
						.build();
		} else if (expression instanceof AndClause) {

			final AndClause clause = (AndClause) expression;
			
			if (clause.getRight() instanceof NotClause) {
				final NotClause notClause = (NotClause) clause.getRight();
				return handleAndNot(clause.getLeft(), notClause);
			} else if (clause.getLeft() instanceof NotClause) {
				final NotClause notClause = (NotClause) clause.getLeft();
				return handleAndNot(clause.getRight(), notClause);
			} else {
				return Expressions.builder()
						.filter(evaluate(clause.getLeft()))
						.filter(evaluate(clause.getRight()))
						.build();
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

	private Expression handleAndNot(final RValue notNegated, final NotClause negated) {
		if (notNegated instanceof NotClause) {
			throw new EscgParseFailedException("Cannot AND two NOT clauses yet");
		}
		return Expressions.builder()
				.filter(evaluate(notNegated))
				.mustNot(evaluate(negated.getValue()))
				.build();
	}
	
}