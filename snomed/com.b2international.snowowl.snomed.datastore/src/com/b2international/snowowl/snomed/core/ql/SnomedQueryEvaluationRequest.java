/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ql;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.ecl.EclExpression;
import com.b2international.snowowl.snomed.core.ecl.EclSerializer;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.ecl.Script;
import com.b2international.snowowl.snomed.ql.Conjunction;
import com.b2international.snowowl.snomed.ql.Disjunction;
import com.b2international.snowowl.snomed.ql.EclFilter;
import com.b2international.snowowl.snomed.ql.Exclusion;
import com.b2international.snowowl.snomed.ql.TermFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;

/**
 * @since 6.12
 */
final class SnomedQueryEvaluationRequest implements Request<BranchContext, Promise<Expression>> {

	private static final long serialVersionUID = 8932162693072727864L;

	@JsonIgnore
	private transient final PolymorphicDispatcher<Promise<Expression>> dispatcher = PolymorphicDispatcher.createForSingleTarget("eval", 2, 2, this);

	@Nullable
	@JsonProperty
	private String expression;

	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public Promise<Expression> execute(BranchContext context) {
		final Disjunction parseResult = context.service(SnomedQueryParser.class).parse(expression);		
		return evaluate(context, parseResult);
	}
	
	private Promise<Expression> evaluate(BranchContext context, EObject expression) {
		return dispatcher.invoke(context, expression);
	}
	
	protected Promise<Expression> eval(BranchContext context, final TermFilter termFilter) {
		final String term = termFilter.getTerm();
		
		return SnomedRequests.prepareSearchDescription()
				.all()
				.filterByTerm(term)
				.build(context.id(), context.branchPath())
				.execute(context.service(IEventBus.class))
				.then(descriptions -> {
					Set<String> ids = descriptions.stream()
							.map(SnomedDescription::getConceptId)
							.collect(Collectors.toSet());
					
					return Expressions.builder().filter(SnomedConceptDocument.Expressions.ids(ids)).build();
				});	
	}
	
	protected Promise<Expression> eval(BranchContext context, final EclFilter eclFilter) {
		final Script script = eclFilter.getEcl();
		final String eclExpression = context.service(EclSerializer.class).serializeWithoutTerms(script.getConstraint());
		return EclExpression.of(eclExpression)
					.resolve(context)
					.then(ids -> {
						return Expressions.builder().filter(SnomedConceptDocument.Expressions.ids(ids)).build();
					});
	}
	
	protected Promise<Expression> eval(BranchContext context, final Disjunction disjunction) {
		return Promise.all(evaluate(context, disjunction.getLeft()), evaluate(context, disjunction.getRight()))
				.then(innerExpressions -> {
						final Expression left = (Expression) innerExpressions.get(0);
						final Expression right = (Expression) innerExpressions.get(1);
						return Expressions.builder()
								.should(left)
								.should(right)
								.build();
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final Exclusion exclusion) {
		return Promise.all(evaluate(context, exclusion.getLeft()), evaluate(context, exclusion.getRight()))
				.then(new Function<List<Object>, Expression>() {
					@Override
					public Expression apply(List<Object> innerExpressions) {
						final Expression left = (Expression) innerExpressions.get(0);
						final Expression right = (Expression) innerExpressions.get(1);
						return Expressions.builder()
								.filter(left)
								.mustNot(right)
								.build();
					}
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final Conjunction conjunction) {
		return Promise.all(evaluate(context, conjunction.getLeft()), evaluate(context, conjunction.getRight()))
				.then(new Function<List<Object>, Expression>() {
					@Override
					public Expression apply(List<Object> innerExpressions) {
						final Expression left = (Expression) innerExpressions.get(0);
						final Expression right = (Expression) innerExpressions.get(1);
						return Expressions.builder()
								.filter(left)
								.filter(right)
								.build();
					}
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, EObject eObject) {
		throw new UnsupportedOperationException("Cannot handle object of type " + eObject.getClass().getName());
	}

}
