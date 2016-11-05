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

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.commons.ClassUtils;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Predicate;
import com.b2international.index.query.StringPredicate;
import com.b2international.index.query.StringSetPredicate;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.Any;
import com.b2international.snowowl.snomed.ecl.ecl.ConceptReference;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOf;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.MemberOf;
import com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * @since 5.4
 */
public class DefaultEclEvaluator implements EclEvaluator {

	private final PolymorphicDispatcher<Promise<Expression>> dispatcher = PolymorphicDispatcher.createForSingleTarget("eval", this);
	private final IParser eclParser;

	public DefaultEclEvaluator(IParser eclParser) {
		this.eclParser = eclParser;
	}
	
	@Override
	public Promise<Expression> evaluate(String expression) {
		if (Strings.isNullOrEmpty(expression)) {
			return Promise.fail(new BadRequestException("Expression should be specified"));
		} else {
			try (final StringReader reader = new StringReader(expression)) {
				final IParseResult parseResult = eclParser.parse(reader);
				if (parseResult.hasSyntaxErrors()) {
					final String message = Joiner.on("\n").join(Iterables.transform(parseResult.getSyntaxErrors(), (node) -> node.getSyntaxErrorMessage().getMessage()));
					return Promise.fail(new BadRequestException(message));
				} else {
					// TODO validate
					return evaluate(parseResult.getRootASTElement());
				}
			}
		}
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
