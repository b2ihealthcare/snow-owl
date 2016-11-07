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
package com.b2international.snowowl.snomed.core.ecl;

import static com.b2international.snowowl.datastore.index.RevisionDocument.Expressions.id;
import static com.b2international.snowowl.datastore.index.RevisionDocument.Expressions.ids;
import static com.b2international.snowowl.datastore.index.RevisionDocument.Fields.ID;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.referringRefSet;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Fields.REFERRING_REFSETS;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.ancestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.parents;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.commons.ClassUtils;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Predicate;
import com.b2international.index.query.StringPredicate;
import com.b2international.index.query.StringSetPredicate;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.Any;
import com.b2international.snowowl.snomed.ecl.ecl.ChildOf;
import com.b2international.snowowl.snomed.ecl.ecl.ConceptReference;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOf;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.MemberOf;
import com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint;
import com.google.common.base.Function;
import com.google.common.reflect.TypeToken;

/**
 * Evaluates the given ECL expression {@link String} or parsed {@link ExpressionConstraint} to an executable {@link Expression query expression}.
 * <p>
 * <i>NOTE: This request implementation is currently not working in remote environments, when the request need to be sent over the network, because
 * the {@link Expression expression API} is not serializable.</i>
 * </p>
 * 
 * @since 5.4
 */
final class SnomedEclEvaluationRequest extends BaseRequest<BranchContext, Promise<Expression>> {

	private static final long serialVersionUID = 5891665196136989183L;
	
	private final PolymorphicDispatcher<Promise<Expression>> dispatcher = PolymorphicDispatcher.createForSingleTarget("eval", this);

	@Nullable
	private String expression;

	@Nullable
	private ExpressionConstraint expressionConstraint;

	SnomedEclEvaluationRequest() {
	}

	void setExpression(ExpressionConstraint expressionConstraint) {
		this.expressionConstraint = expressionConstraint;
	}

	void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	protected Class<Promise<Expression>> getReturnType() {
		TypeToken<Promise<Expression>> exp = new TypeToken<Promise<Expression>>(){};
		Class<Promise<Expression>> rawType = (Class<Promise<Expression>>) exp.getRawType();
		return rawType;
	}

	@Override
	public Promise<Expression> execute(BranchContext context) {
		final ExpressionConstraint currentExpression = expressionConstraint != null ? expressionConstraint
				: context.service(EclParser.class).parse(expression);
		return evaluate(currentExpression);
	}
	
	private Promise<Expression> evaluate(EObject expression) {
		return dispatcher.invoke(expression);
	}

	protected Promise<Expression> eval(EObject eObject) {
		return throwUnsupported(eObject);
	}

	protected Promise<Expression> eval(Any any) {
		return Promise.immediate(Expressions.matchAll());
	}
	
	protected Promise<Expression> eval(ConceptReference concept) {
		return Promise.immediate(id(concept.getId()));
	}
	
	protected Promise<Expression> eval(MemberOf memberOf) {
		if (memberOf.getConstraint() instanceof ConceptReference) {
			final ConceptReference concept = (ConceptReference) memberOf.getConstraint();
			return Promise.immediate(referringRefSet(concept.getId()));
		} else if (memberOf.getConstraint() instanceof Any) {
			return Promise.immediate(Expressions.exists(REFERRING_REFSETS));
		} else {
			return throwUnsupported(memberOf.getConstraint());
		}
	}
	
	protected Promise<Expression> eval(final DescendantOf descendantOf) {
		return evaluate(descendantOf.getConstraint())
			.then(new Function<Expression, Expression>() {
				@Override
				public Expression apply(Expression inner) {
					final Set<String> ids = extractIds(inner);
					return Expressions.builder()
							.should(parents(ids))
							.should(ancestors(ids))
							.build();
				}
			});
	}
	
	protected Promise<Expression> eval(final DescendantOrSelfOf descendantOrSelfOf) {
		return evaluate(descendantOrSelfOf.getConstraint())
				.then(new Function<Expression, Expression>() {
					@Override
					public Expression apply(Expression inner) {
						final Set<String> ids = extractIds(inner);
						return Expressions.builder()
								.should(ids(ids))
								.should(parents(ids))
								.should(ancestors(ids))
								.build();
					}
				});
	}
	
	protected Promise<Expression> eval(final ChildOf childOf) {
		return evaluate(childOf.getConstraint())
				.then(new Function<Expression, Expression>() {
					@Override
					public Expression apply(Expression inner) {
						final Set<String> ids = extractIds(inner);
						return parents(ids);
					}
				});
	}
	
	protected Promise<Expression> eval(final AndExpressionConstraint and) {
		return Promise.all(evaluate(and.getLeft()), evaluate(and.getRight()))
				.then(new Function<List<Object>, Expression>() {
					@Override
					public Expression apply(List<Object> innerExpressions) {
						final Expression left = (Expression) innerExpressions.get(0);
						final Expression right = (Expression) innerExpressions.get(1);
						return Expressions.builder()
								.must(left)
								.must(right)
								.build();
					}
				});
	}
	
	protected Promise<Expression> eval(final OrExpressionConstraint or) {
		return Promise.all(evaluate(or.getLeft()), evaluate(or.getRight()))
				.then(new Function<List<Object>, Expression>() {
					@Override
					public Expression apply(List<Object> innerExpressions) {
						final Expression left = (Expression) innerExpressions.get(0);
						final Expression right = (Expression) innerExpressions.get(1);
						return Expressions.builder()
								.should(left)
								.should(right)
								.build();
					}
				});
	}
	
	protected Promise<Expression> eval(final ExclusionExpressionConstraint exclusion) {
		return Promise.all(evaluate(exclusion.getLeft()), evaluate(exclusion.getRight()))
				.then(new Function<List<Object>, Expression>() {
					@Override
					public Expression apply(List<Object> innerExpressions) {
						final Expression left = (Expression) innerExpressions.get(0);
						final Expression right = (Expression) innerExpressions.get(1);
						return Expressions.builder()
								.must(left)
								.mustNot(right)
								.build();
					}
				});
	}
	
	private Promise<Expression> throwUnsupported(EObject eObject) {
		throw new UnsupportedOperationException("Unhandled ECL grammar feature: " + eObject.eClass().getName());
	}
	
	/*Extract SNOMED CT IDs from the given expression if it is either a String/Long single/multi-valued predicate and the field is equal to RevisionDocument.Fields.ID*/
	private static Set<String> extractIds(Expression expression) {
		final Predicate predicate = ClassUtils.checkAndCast(expression, Predicate.class);
		if (ID.equals(predicate.getField())) {
			if (predicate instanceof StringSetPredicate) {
				return ((StringSetPredicate) predicate).values();
			} else if (predicate instanceof StringPredicate) {
				return Collections.singleton(((StringPredicate) expression).getArgument());
			}
		}
		throw new UnsupportedOperationException("Cannot extract ID values from: " + expression);
	}

}
