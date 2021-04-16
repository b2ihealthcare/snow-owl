/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.index.revision.Revision.Fields.ID;
import static com.b2international.snowowl.core.repository.RevisionDocument.Expressions.id;
import static com.b2international.snowowl.core.repository.RevisionDocument.Expressions.ids;
import static com.b2international.snowowl.snomed.core.ecl.EclExpression.isAnyExpression;
import static com.b2international.snowowl.snomed.core.ecl.EclExpression.isEclConceptReference;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.activeMemberOf;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Fields.ACTIVE_MEMBER_OF;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.index.query.*;
import com.b2international.snomed.ecl.ecl.*;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * Evaluates the given ECL expression {@link String} or parsed {@link ExpressionConstraint} to an executable {@link Expression query expression}.
 * <p>
 * <i>NOTE: This request implementation is currently not working in remote environments, when the request need to be sent over the network, because
 * the {@link Expression expression API} is not serializable.</i>
 * </p>
 * 
 * @since 5.4
 */
final class SnomedEclEvaluationRequest implements Request<BranchContext, Promise<Expression>> {

	private static final long serialVersionUID = 5891665196136989183L;
	
	private final PolymorphicDispatcher<Promise<Expression>> dispatcher = PolymorphicDispatcher.createForSingleTarget("eval", 2, 2, this);

	@Nullable
	@JsonProperty
	private String expression;
	
	@NotNull
	@JsonProperty
	private String expressionForm = Trees.INFERRED_FORM;

	SnomedEclEvaluationRequest() {
	}

	void setExpression(String expression) {
		this.expression = expression;
	}
	
	void setExpressionForm(String expressionForm) {
		this.expressionForm = expressionForm;
	}

	@Override
	public Promise<Expression> execute(BranchContext context) {
		final ExpressionConstraint expressionConstraint = context.service(EclParser.class).parse(expression);
		return doEval(context, expressionConstraint);
	}

	Promise<Expression> doEval(BranchContext context, final ExpressionConstraint expressionConstraint) {
		ExpressionConstraint rewritten = new SnomedEclRewriter().rewrite(expressionConstraint);
		return evaluate(context, rewritten);
	}
	
	private Promise<Expression> evaluate(BranchContext context, EObject expression) {
		return dispatcher.invoke(context, expression);
	}

	protected Promise<Expression> eval(BranchContext context, EObject eObject) {
		return throwUnsupported(eObject);
	}

	/**
	 * Handles ANY simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints  
	 */
	protected Promise<Expression> eval(BranchContext context, Any any) {
		return Promise.immediate(Expressions.matchAll());
	}
	
	/**
	 * Handles EclConceptReference/Self simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, EclConceptReference concept) {
		return Promise.immediate(id(concept.getId()));
	}
	
	/**
	 * Handles MemberOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, MemberOf memberOf) {
		final ExpressionConstraint inner = memberOf.getConstraint();
		if (inner instanceof EclConceptReference) {
			final EclConceptReference concept = (EclConceptReference) inner;
			return Promise.immediate(activeMemberOf(concept.getId()));
		} else if (isAnyExpression(inner)) {
			return Promise.immediate(Expressions.exists(ACTIVE_MEMBER_OF));
		} else if (inner instanceof NestedExpression) {
			return EclExpression.of(inner, expressionForm)
					.resolve(context)
					.then(ids -> activeMemberOf(ids));
		} else {
			return throwUnsupported(inner);
		}
	}
	
	/**
	 * Handles DescendantsOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, final DescendantOf descendantOf) {
		final ExpressionConstraint inner = descendantOf.getConstraint();
		// <* should eval to * MINUS parents IN (ROOT_ID)
		if (isAnyExpression(inner)) {
			return Promise.immediate(Expressions.builder()
					.mustNot(parentsExpression(Collections.singleton(IComponent.ROOT_ID)))
					.build());
		} else {
			return evaluate(context, inner)
					.thenWith(resolveIds(context, inner, expressionForm))
					.then(ids -> Expressions.builder()
							.should(parentsExpression(ids))
							.should(ancestorsExpression(ids))
							.build());
		}
	}
	
	/**
	 * Handles DescendantOrSelfOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, final DescendantOrSelfOf descendantOrSelfOf) {
		final ExpressionConstraint inner = descendantOrSelfOf.getConstraint();
		// <<* should eval to *
		if (isAnyExpression(inner)) {
			return evaluate(context, inner);
		} else {
			return evaluate(context, inner)
					.thenWith(resolveIds(context, inner, expressionForm))
					.then(ids -> Expressions.builder()
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
	protected Promise<Expression> eval(BranchContext context, final ChildOf childOf) {
		final ExpressionConstraint innerConstraint = childOf.getConstraint();
		// <!* should eval to * MINUS parents in (ROOT_ID)
		if (isAnyExpression(innerConstraint)) {
			return Promise.immediate(Expressions.builder()
					.mustNot(parentsExpression(Collections.singleton(IComponent.ROOT_ID)))
					.build());
		} else {
			return evaluate(context, innerConstraint)
					.thenWith(resolveIds(context, innerConstraint, expressionForm))
					.then(ids -> parentsExpression(ids));
		}
	}
	
	/**
	 * Handles ChildOrSelfOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, final ChildOrSelfOf childOrSelfOf) {
		final ExpressionConstraint innerConstraint = childOrSelfOf.getConstraint();
		// <<!* should eval to * (direct child or self of all concept IDs === all concept IDs)
		if (isAnyExpression(innerConstraint)) {
			return evaluate(context, innerConstraint);
		} else {
			return evaluate(context, innerConstraint)
					.thenWith(resolveIds(context, innerConstraint, expressionForm))
					.then(ids -> Expressions.builder()
							.should(ids(ids))
							.should(parentsExpression(ids))
							.build());
		}
	}
	
	/**
	 * Handles ParentOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, final ParentOf parentOf) {
		return EclExpression.of(parentOf.getConstraint(), expressionForm)
				.resolveConcepts(context)
				.then(concepts -> {
					final Set<String> parents = newHashSet();
					for (SnomedConcept concept : concepts) {
						addParentIds(concept, parents);
					}
					return parents;
				})
				.then(matchIdsOrNone());
	}
	
	/**
	 * Handles ParentOrSelfOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, final ParentOrSelfOf parentOrSelfOf) {
		return EclExpression.of(parentOrSelfOf.getConstraint(), expressionForm)
				.resolveConcepts(context)
				.then(concepts -> {
					final Set<String> results = newHashSet();
					for (SnomedConcept concept : concepts) {
						results.add(concept.getId());
						addParentIds(concept, results);
					}
					return results;
				})
				.then(matchIdsOrNone());
	}
	
	/**
	 * Handles AncestorOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, final AncestorOf ancestorOf) {
		return EclExpression.of(ancestorOf.getConstraint(), expressionForm)
				.resolveConcepts(context)
				.then(concepts -> {
					final Set<String> ancestors = newHashSet();
					for (SnomedConcept concept : concepts) {
						addParentIds(concept, ancestors);
						addAncestorIds(concept, ancestors);
					}
					return ancestors;
				})
				.then(matchIdsOrNone());
	}
	
	/**
	 * Handles AncestorOrSelfOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, final AncestorOrSelfOf ancestorOrSelfOf) {
		final ExpressionConstraint innerConstraint = ancestorOrSelfOf.getConstraint();
		// >>* should eval to *
		if (isAnyExpression(innerConstraint)) {
			return evaluate(context, innerConstraint);
		} else {
			return EclExpression.of(innerConstraint, expressionForm)
					.resolveConcepts(context)
					.then(concepts -> {
						final Set<String> ancestors = newHashSet();
						for (SnomedConcept concept : concepts) {
							ancestors.add(concept.getId());
							addParentIds(concept, ancestors);
							addAncestorIds(concept, ancestors);
						}
						return ancestors;
					})
					.then(matchIdsOrNone());
		}
	}
	
	/**
	 * Handles conjunction binary operator expressions
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.4+Conjunction+and+Disjunction
	 */
	protected Promise<Expression> eval(BranchContext context, final AndExpressionConstraint and) {
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
			if (!CompareUtils.isEmpty(ids)) {
				return Promise.immediate(Expressions.matchNone());
			}
		}
		return Promise.all(evaluate(context, and.getLeft()), evaluate(context, and.getRight()))
				.then(innerExpressions -> {
					final Expression left = (Expression) innerExpressions.get(0);
					final Expression right = (Expression) innerExpressions.get(1);
					return Expressions.builder()
							.filter(left)
							.filter(right)
							.build();
				});
	}
	
	/**
	 * Handles disjunction binary operator expressions
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.4+Conjunction+and+Disjunction
	 */
	protected Promise<Expression> eval(BranchContext context, final OrExpressionConstraint or) {
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
					return Expressions.builder()
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
	protected Promise<Expression> eval(BranchContext context, final ExclusionExpressionConstraint exclusion) {
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
					return Expressions.builder().filter(left).mustNot(right).build();
				});
			}
		});
	}
	
	/**
	 * Delegates evaluation of Refinement expression constraints to {@link SnomedEclRefinementEvaluator}.
	 */
	protected Promise<Expression> eval(final BranchContext context, final RefinedExpressionConstraint refined) {
		return new SnomedEclRefinementEvaluator(EclExpression.of(refined.getConstraint(), expressionForm)).evaluate(context, refined.getRefinement());
	}
	
	/**
	 * Handles dotted expression constraints (reversed attribute refinement with dot notation)
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.2+Refinements
	 */
	protected Promise<Expression> eval(BranchContext context, DottedExpressionConstraint dotted) {
		final Promise<Set<String>> focusConceptIds = SnomedEclRefinementEvaluator.evalToConceptIds(context, dotted.getConstraint(), expressionForm);
		final Promise<Set<String>> typeConceptIds = SnomedEclRefinementEvaluator.evalToConceptIds(context, dotted.getAttribute(), expressionForm);
		return Promise.all(focusConceptIds, typeConceptIds)
			.thenWith(responses -> {
				Set<String> focusConcepts = (Set<String>) responses.get(0);
				Set<String> typeConcepts = (Set<String>) responses.get(1);
				return SnomedEclRefinementEvaluator.evalStatements(context, focusConcepts, typeConcepts, null /* ANY */, false, expressionForm);
			})
			.then(new Function<Collection<SnomedEclRefinementEvaluator.Property>, Set<String>>() {
				@Override
				public Set<String> apply(Collection<SnomedEclRefinementEvaluator.Property> input) {
					return FluentIterable.from(input).transform(SnomedEclRefinementEvaluator.Property::getValue).filter(String.class).toSet();
				}
			})
			.then(matchIdsOrNone());
	}
	
	/**
	 * Handles nested expression constraints by simple evaluation the nested expression.
	 */
	protected Promise<Expression> eval(BranchContext context, final NestedExpression nested) {
		return evaluate(context, nested.getNested());
	}
	
	/**
	 * Handles cases when the expression constraint is not available at all. For instance, script is empty.
	 */
	protected Promise<Expression> eval(BranchContext context, final Void empty) {
		return Promise.immediate(MatchNone.INSTANCE);
	}
	
	/*package*/ static <T> Promise<T> throwUnsupported(EObject eObject) {
		throw new NotImplementedException("Not implemented ECL feature: %s", eObject.eClass().getName());
	}
	
	static boolean canExtractIds(Expression expression) {
		return expression instanceof Predicate && ID.equals(((Predicate) expression).getField());
	}
	
	/*Extract SNOMED CT IDs from the given expression if it is either a String/Long single/multi-valued predicate and the field is equal to RevisionDocument.Fields.ID*/
	/*package*/ static Set<String> extractIds(Expression expression) {
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
	 * Extracts SNOMED CT IDs from the given expression if it is either single or multi-valued String predicate and the field is equal to RevisionDocument.Fields.ID.
	 * Otherwise it will try to evaluate the ecl completely without using the first returned expression.
	 * @param ecl - the original ECL which will resolve to the returned function parameter as {@link Expression}
	 */
	private static Function<Expression, Promise<Set<String>>> resolveIds(BranchContext context, ExpressionConstraint ecl, String expressionForm) {
		return expression -> {
			try {
				return Promise.immediate(extractIds(expression));
			} catch (UnsupportedOperationException e) {
				// otherwise always evaluate the expression to ID set and return that
				return EclExpression.of(ecl, expressionForm).resolve(context);
			}
		};
	}
	
	private Expression parentsExpression(Set<String> ids) {
		return Trees.INFERRED_FORM.equals(expressionForm) ? SnomedConceptDocument.Expressions.parents(ids) : SnomedConceptDocument.Expressions.statedParents(ids);
	}

	private Expression ancestorsExpression(Set<String> ids) {
		return Trees.INFERRED_FORM.equals(expressionForm) ? SnomedConceptDocument.Expressions.ancestors(ids) : SnomedConceptDocument.Expressions.statedAncestors(ids);
	}
	
	private void addParentIds(SnomedConcept concept, final Set<String> collection) {
		if (Trees.INFERRED_FORM.equals(expressionForm)) {
			if (concept.getParentIds() != null) {
				for (long parent : concept.getParentIds()) {
					if (IComponent.ROOT_IDL != parent) {
						collection.add(Long.toString(parent));
					}
				}
			}
		} else {
			if (concept.getStatedParentIds() != null) {
				for (long statedParent : concept.getStatedParentIds()) {
					if (IComponent.ROOT_IDL != statedParent) {
						collection.add(Long.toString(statedParent));
					}
				}
			}
		}
	}
	
	private void addAncestorIds(SnomedConcept concept, Set<String> collection) {
		if (Trees.INFERRED_FORM.equals(expressionForm)) {
			if (concept.getAncestorIds() != null) {
				for (long ancestor : concept.getAncestorIds()) {
					if (IComponent.ROOT_IDL != ancestor) {
						collection.add(Long.toString(ancestor));
					}
				}
			}
		} else {
			if (concept.getStatedAncestorIds() != null) {
				for (long statedAncestor : concept.getStatedAncestorIds()) {
					if (IComponent.ROOT_IDL != statedAncestor) {
						collection.add(Long.toString(statedAncestor));
					}
				}
			}
		}		
	}
	
	/*package*/ static Function<Set<String>, Expression> matchIdsOrNone() {
		return ids -> ids.isEmpty() ? Expressions.matchNone() : ids(ids);
	}
	
}
