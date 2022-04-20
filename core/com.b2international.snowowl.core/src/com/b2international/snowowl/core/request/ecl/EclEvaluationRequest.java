/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.ecl;

import static com.b2international.index.revision.Revision.Expressions.id;
import static com.b2international.index.revision.Revision.Expressions.ids;
import static com.b2international.index.revision.Revision.Fields.ID;
import static com.b2international.snomed.ecl.Ecl.isAnyExpression;
import static com.b2international.snomed.ecl.Ecl.isEclConceptReference;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.index.query.*;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snomed.ecl.ecl.*;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.ecl.EclParser;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver.PathWithVersion;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @since 8.2.0
 */
public abstract class EclEvaluationRequest<C extends ServiceProvider> implements Request<C, Promise<Expression>> {

	private static final long serialVersionUID = 1L;

	private final PolymorphicDispatcher<Promise<Expression>> dispatcher = PolymorphicDispatcher.createForSingleTarget("eval", 2, 2, this);
	
	@Nullable
	@JsonProperty
	private String expression;
	
	@Nullable
	private Set<String> ignoredSyntaxErrorCodes;
	
	@Override
	public final Promise<Expression> execute(C context) {
		final ExpressionConstraint expressionConstraint = context.service(EclParser.class).parse(expression, ignoredSyntaxErrorCodes);
		return doEval(context, expressionConstraint);
	}

	public final Promise<Expression> doEval(C context, final ExpressionConstraint expressionConstraint) {
		final ExpressionConstraint rewritten = rewrite(context, expressionConstraint);
		return evaluate(context, rewritten);
	}
	
	/**
	 * Subclasses may optionally override this method to rewrite the parsed ECL query before evaluation.
	 *  
	 * @param context
	 * @param expressionConstraint
	 * @return
	 */
	protected ExpressionConstraint rewrite(C context, ExpressionConstraint expressionConstraint) {
		return expressionConstraint;
	}
	
	public final void setExpression(String expression) {
		this.expression = expression;
	}
	
	public void setIgnoredSyntaxErrorCodes(Set<String> ignoredSyntaxErrorCodes) {
		this.ignoredSyntaxErrorCodes = ignoredSyntaxErrorCodes == null ? null : ImmutableSet.copyOf(ignoredSyntaxErrorCodes);
	}
	
	protected final Promise<Expression> evaluate(C context, EObject expression) {
		return dispatcher.invoke(context, expression);
	}

	protected Promise<Expression> eval(C context, EObject eObject) {
		return throwUnsupported(eObject);
	}
	
	/**
	 * Handles cases when the expression constraint is not available at all. For instance, script is empty.
	 */
	protected Promise<Expression> eval(BranchContext context, final Void empty) {
		return Promise.immediate(MatchNone.INSTANCE);
	}
	
	/**
	 * Handles ANY simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints  
	 */
	protected Promise<Expression> eval(C context, Any any) {
		return Promise.immediate(Expressions.matchAll());
	}
	
	/**
	 * Handles EclConceptReference/Self simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(C context, EclConceptReference concept) {
		return Promise.immediate(id(concept.getId()));
	}
	
	protected Promise<Expression> eval(C context, EclConceptReferenceSet conceptSet) {
		final Set<String> conceptIds = conceptSet.getConcepts()
			.stream()
			.map(EclConceptReference::getId)
			.collect(ImmutableSet.toImmutableSet());
		
		return Promise.immediate(ids(conceptIds));
	}
	
	/**
	 * Handles DescendantsOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(C context, final DescendantOf descendantOf) {
		final ExpressionConstraint inner = descendantOf.getConstraint();
		// <* should eval to * MINUS parents IN (ROOT_ID)
		if (isAnyExpression(inner)) {
			return Promise.immediate(Expressions.bool()
					.mustNot(parentsExpression(Collections.singleton(IComponent.ROOT_ID)))
					.build());
		} else {
			return evaluate(context, inner)
					.then(resolveIds(context))
					.then(ids -> Expressions.bool()
							.should(parentsExpression(ids))
							.should(ancestorsExpression(ids))
							.build());
		}
	}
	
	/**
	 * Handles DescendantOrSelfOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(C context, final DescendantOrSelfOf descendantOrSelfOf) {
		final ExpressionConstraint inner = descendantOrSelfOf.getConstraint();
		// <<* should eval to *
		if (isAnyExpression(inner)) {
			return evaluate(context, inner);
		} else {
			return evaluate(context, inner)
					.then(resolveIds(context))
					.then(ids -> Expressions.bool()
							.should(ids(ids))
							.should(parentsExpression(ids))
							.should(ancestorsExpression(ids))
							.build());
		}
	}
	
	/**
	 * Handles ChildOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(C context, final ChildOf childOf) {
		final ExpressionConstraint innerConstraint = childOf.getConstraint();
		// <!* should eval to * MINUS parents in (ROOT_ID)
		if (isAnyExpression(innerConstraint)) {
			return Promise.immediate(Expressions.bool()
					.mustNot(parentsExpression(Collections.singleton(IComponent.ROOT_ID)))
					.build());
		} else {
			return evaluate(context, innerConstraint)
					.then(resolveIds(context))
					.then(ids -> parentsExpression(ids));
		}
	}
	
	/**
	 * Handles ChildOrSelfOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(C context, final ChildOrSelfOf childOrSelfOf) {
		final ExpressionConstraint innerConstraint = childOrSelfOf.getConstraint();
		// <<!* should eval to * (direct child or self of all concept IDs === all concept IDs)
		if (isAnyExpression(innerConstraint)) {
			return evaluate(context, innerConstraint);
		} else {
			return evaluate(context, innerConstraint)
					.then(resolveIds(context))
					.then(ids -> Expressions.bool()
							.should(ids(ids))
							.should(parentsExpression(ids))
							.build());
		}
	}
	
	/**
	 * Handles conjunction binary operator expressions
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.4+Conjunction+and+Disjunction
	 */
	protected Promise<Expression> eval(C context, final AndExpressionConstraint and) {
		if (isAnyExpression(and.getLeft())) {
			return evaluate(context, and.getRight());
		} else if (isAnyExpression(and.getRight())) {
			return evaluate(context, and.getLeft());
		} else if (isEclConceptReference(and.getRight())) {
			// if the right hand is an ID, then iterate and check if the entire tree is ID1 AND ID2 AND IDN
			final Set<String> ids = newHashSet();
			TreeIterator<EObject> it = and.eAllContents();
			while (it.hasNext()) {
				EObject content = it.next();
				// accept only EclConceptReference/Nested and AND expressions, anything else will break the loop
				if (content instanceof EclConceptReference) {
					ids.add(((EclConceptReference) content).getId());
				} else if (content instanceof NestedExpression || content instanceof AndExpressionConstraint) {
					// continue
				} else {
					// remove any IDs collected
					ids.clear();
					break;
				}
			}
			if (ids.size() == 1) {
				// if only a single ID is part of the AND expression, then match that
				return Promise.immediate(id(Iterables.getOnlyElement(ids)));
			} else if (ids.size() > 1) {
				// if 2 or more the nothing can match the query
				return Promise.immediate(Expressions.matchNone());
			} else {
				// otherwise nothing to do
			}
		}
		return Promise.all(evaluate(context, and.getLeft()), evaluate(context, and.getRight()))
				.then(innerExpressions -> {
					final Expression left = (Expression) innerExpressions.get(0);
					final Expression right = (Expression) innerExpressions.get(1);
					return Expressions.bool()
							.filter(left)
							.filter(right)
							.build();
				});
	}
	
	/**
	 * Handles disjunction binary operator expressions
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.4+Conjunction+and+Disjunction
	 */
	protected Promise<Expression> eval(C context, final OrExpressionConstraint or) {
		if (isAnyExpression(or.getLeft())) {
			return evaluate(context, or.getLeft());
		} else if (isAnyExpression(or.getRight())) {
			return evaluate(context, or.getRight());
		} else if (isEclConceptReference(or.getRight())) {
			// if the right hand is an ID, then iterate and check if the entire tree is ID1 OR ID2 OR IDN
			final Set<String> ids = newHashSet();
			TreeIterator<EObject> it = or.eAllContents();
			while (it.hasNext()) {
				EObject content = it.next();
				// accept only EclConceptReference/Nested and OR expressions, anything else will break the loop
				if (content instanceof EclConceptReference) {
					ids.add(((EclConceptReference) content).getId());
				} else if (content instanceof NestedExpression || content instanceof OrExpressionConstraint) {
					// continue
				} else {
					// remove any IDs collected
					ids.clear();
					break;
				}
			}
			if (!CompareUtils.isEmpty(ids)) {
				return Promise.immediate(ids(ids));
			}
		}
		return Promise.all(evaluate(context, or.getLeft()), evaluate(context, or.getRight()))
				.then(innerExpressions -> {
					final Expression left = (Expression) innerExpressions.get(0);
					final Expression right = (Expression) innerExpressions.get(1);
					return Expressions.bool()
							.should(left)
							.should(right)
							.build();
				});
	}

	/**
	 * Handles exclusion binary operator expressions
	 * 
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.5+Exclusion+and+Not+Equals
	 */
	protected Promise<Expression> eval(C context, final ExclusionExpressionConstraint exclusion) {
		return evaluate(context, exclusion.getRight()).thenWith(right -> {
			if (right.isMatchAll()) {
				// excluding everything should result in no matches
				return Promise.immediate(Expressions.matchNone());
			} else if (right.isMatchNone()) {
				// excluding nothing is just the left query
				return evaluate(context, exclusion.getLeft());
			} else {
				return evaluate(context, exclusion.getLeft()).then(left -> {
					// match left hand side query and not the right hand side query
					return Expressions.bool().filter(left).mustNot(right).build();
				});
			}
		});
	}
	
	/**
	 * Handles nested expression constraints by simple evaluation the nested expression.
	 */
	protected Promise<Expression> eval(C context, final NestedExpression nested) {
		return evaluate(context, nested.getNested());
	}
	
	public static <T> T throwUnsupported(EObject eObject) {
		return throwUnsupported(eObject, "");
	}
	
	public static <T> T throwUnsupported(EObject eObject, String additionalInfo) {
		return throwUnsupported(String.format("%s%s", eObject.eClass().getName(), CompareUtils.isEmpty(additionalInfo) ? "" : ". ".concat(additionalInfo)));
	}
	
	public static <T> T throwUnsupported(String feature) {
		throw new NotImplementedException("Not implemented ECL feature: %s", feature);
	}
	
	protected abstract Expression parentsExpression(Set<String> ids);
	
	protected abstract Expression ancestorsExpression(Set<String> ids);

	public static boolean canExtractIds(Expression expression) {
		return expression instanceof Predicate && ID.equals(((Predicate) expression).getField());
	}
	
	/*Extract IDs from the given expression if it is either a String/Long single/multi-valued predicate and the field is equal to RevisionDocument.Fields.ID*/
	public static Set<String> extractIds(Expression expression) {
		if (!canExtractIds(expression)) {
			throw new UnsupportedOperationException("Cannot extract ID values from: " + expression);
		}
		if (expression instanceof StringSetPredicate) {
			return ((StringSetPredicate) expression).values();
		} else {
			return Collections.singleton(((StringPredicate) expression).getArgument());
		}
	}
	
	/**
	 * @param context
	 * @return a function that when an expression is received, it will either shortcut the execution and returns the IDs or evaluates the returned expression to a set of concept IDs.
	 */
	public Function<Expression, Set<String>> resolveIds(ServiceProvider context) {
		return resolveIds(context, getDocumentType());
	}
	
	/**
	 * @param context
	 * @param documentType
	 * @return a function that when an expression is received, it will either shortcut the execution and returns the IDs or evaluates the returned expression to a set of concept IDs.
	 */
	public static Function<Expression, Set<String>> resolveIds(ServiceProvider context, Class<?> documentType) {
		RevisionSearcher searcher = context.service(RevisionSearcher.class);
		boolean cached = context.optionalService(PathWithVersion.class).isPresent();		
		return expression -> {
			// shortcut to extract IDs from the query itself if possible 
			if (canExtractIds(expression)) {
				/* 
				 * It should always be possible to extract identifiers from an index query expression derived from 
				 * an EclConceptReferenceSet, and occasionally ExpressionConstraints also have this property.
				 */
				return extractIds(expression);
			}
			
			return newHashSet(Query.select(String.class)
					.from(documentType)
					.fields(RevisionDocument.Fields.ID)
					.where(expression)
					.limit(Integer.MAX_VALUE)
					// cache when the current context is executed against a version
					.cached(cached)
					.build()
					.search(searcher));
		};
	}

	protected abstract Class<?> getDocumentType();
	
}
