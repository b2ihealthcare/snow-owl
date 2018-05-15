/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.activeMemberOf;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Fields.ACTIVE_MEMBER_OF;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.ancestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.parents;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.MatchNone;
import com.b2international.index.query.Predicate;
import com.b2international.index.query.StringPredicate;
import com.b2international.index.query.StringSetPredicate;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.ecl.ecl.AncestorOf;
import com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.Any;
import com.b2international.snowowl.snomed.ecl.ecl.ChildOf;
import com.b2international.snowowl.snomed.ecl.ecl.ConceptReference;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOf;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.MemberOf;
import com.b2international.snowowl.snomed.ecl.ecl.NestedExpression;
import com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.ParentOf;
import com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint;
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

	SnomedEclEvaluationRequest() {
	}

	void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public Promise<Expression> execute(BranchContext context) {
		// parse and rewrite the ECL expression before processing
		final ExpressionConstraint exp = context.service(EclParser.class).parse(expression);
		return evaluate(context, new SnomedEclRewriter().rewrite(exp));
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
	 * Handles ConceptReference/Self simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, ConceptReference concept) {
		return Promise.immediate(id(concept.getId()));
	}
	
	/**
	 * Handles MemberOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, MemberOf memberOf) {
		final ExpressionConstraint inner = memberOf.getConstraint();
		if (inner instanceof ConceptReference) {
			final ConceptReference concept = (ConceptReference) inner;
			return Promise.immediate(activeMemberOf(concept.getId()));
		} else if (inner instanceof Any) {
			return Promise.immediate(Expressions.exists(ACTIVE_MEMBER_OF));
		} else if (inner instanceof NestedExpression) {
			final String focusConceptExpression = context.service(EclSerializer.class).serializeWithoutTerms(inner);
			return EclExpression.of(focusConceptExpression)
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
		if (inner instanceof Any) {
			return Promise.immediate(Expressions.builder()
					.mustNot(parents(Collections.singleton(IComponent.ROOT_ID)))
					.build());
		} else {
			return evaluate(context, inner)
					.thenWith(resolveIds(context, inner))
					.then(new Function<Set<String>, Expression>() {
						@Override
						public Expression apply(Set<String> ids) {
							return Expressions.builder()
									.should(parents(ids))
									.should(ancestors(ids))
									.build();
						}
					});
		}
	}
	
	/**
	 * Handles DescendantOrSelfOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, final DescendantOrSelfOf descendantOrSelfOf) {
		final ExpressionConstraint inner = descendantOrSelfOf.getConstraint();
		// <<* should eval to *
		if (inner instanceof Any) {
			return evaluate(context, inner);
		} else {
			return evaluate(context, inner)
					.thenWith(resolveIds(context, inner))
					.then(new Function<Set<String>, Expression>() {
						@Override
						public Expression apply(Set<String> ids) {
							return Expressions.builder()
									.should(ids(ids))
									.should(parents(ids))
									.should(ancestors(ids))
									.build();
						}
					});
		}
	}
	
	/**
	 * Handles ChildOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, final ChildOf childOf) {
		final ExpressionConstraint innerConstraint = childOf.getConstraint();
		// <!* should eval to * MINUS parents in (ROOT_ID)
		if (innerConstraint instanceof Any) {
			return Promise.immediate(Expressions.builder()
					.mustNot(parents(Collections.singleton(IComponent.ROOT_ID)))
					.build());
		} else {
			return evaluate(context, innerConstraint)
					.thenWith(resolveIds(context, innerConstraint))
					.then(new Function<Set<String>, Expression>() {
						@Override
						public Expression apply(Set<String> ids) {
							return parents(ids);
						}
					});
		}
	}
	
	/**
	 * Handles ParentOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, final ParentOf parentOf) {
		final String inner = context.service(EclSerializer.class).serializeWithoutTerms(parentOf.getConstraint());
		return EclExpression.of(inner)
				.resolveConcepts(context)
				.then(new Function<SnomedConcepts, Set<String>>() {
					@Override
					public Set<String> apply(SnomedConcepts concepts) {
						final Set<String> parents = newHashSet();
						for (SnomedConcept concept : concepts) {
							addParentIds(concept, parents);
						}
						return parents;
					}
				})
				.then(matchIdsOrNone());
	}
	
	/**
	 * Handles AncestorOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, final AncestorOf ancestorOf) {
		final String inner = context.service(EclSerializer.class).serializeWithoutTerms(ancestorOf.getConstraint());
		return EclExpression.of(inner)
				.resolveConcepts(context)
				.then(new Function<SnomedConcepts, Set<String>>() {
					@Override
					public Set<String> apply(SnomedConcepts concepts) {
						final Set<String> ancestors = newHashSet();
						for (SnomedConcept concept : concepts) {
							addParentIds(concept, ancestors);
							addAncestorIds(concept, ancestors);
						}
						return ancestors;
					}
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
		if (innerConstraint instanceof Any) {
			return evaluate(context, innerConstraint);
		} else {
			final String inner = context.service(EclSerializer.class).serializeWithoutTerms(innerConstraint);
			return EclExpression.of(inner)
					.resolveConcepts(context)
					.then(new Function<SnomedConcepts, Set<String>>() {
						@Override
						public Set<String> apply(SnomedConcepts concepts) {
							final Set<String> ancestors = newHashSet();
							for (SnomedConcept concept : concepts) {
								ancestors.add(concept.getId());
								addParentIds(concept, ancestors);
								addAncestorIds(concept, ancestors);
							}
							return ancestors;
						}
					})
					.then(matchIdsOrNone());
		}
	}
	
	/**
	 * Handles conjunction binary operator expressions
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.4+Conjunction+and+Disjunction
	 */
	protected Promise<Expression> eval(BranchContext context, final AndExpressionConstraint and) {
		return Promise.all(evaluate(context, and.getLeft()), evaluate(context, and.getRight()))
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
	
	/**
	 * Handles disjunction binary operator expressions
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.4+Conjunction+and+Disjunction
	 */
	protected Promise<Expression> eval(BranchContext context, final OrExpressionConstraint or) {
		return Promise.all(evaluate(context, or.getLeft()), evaluate(context, or.getRight()))
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
	
	/**
	 * Handles exclusion binary operator expressions
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.5+Exclusion+and+Not+Equals
	 */
	protected Promise<Expression> eval(BranchContext context, final ExclusionExpressionConstraint exclusion) {
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
	
	/**
	 * Delegates evaluation of Refinement expression constraints to {@link SnomedEclRefinementEvaluator}.
	 */
	protected Promise<Expression> eval(final BranchContext context, final RefinedExpressionConstraint refined) {
		final String focusConceptExpression = context.service(EclSerializer.class).serializeWithoutTerms(refined.getConstraint());
		return new SnomedEclRefinementEvaluator(EclExpression.of(focusConceptExpression)).evaluate(context, refined.getRefinement());
	}
	
	/**
	 * Handles dotted expression constraints (reversed attribute refinement with dot notation)
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.2+Refinements
	 */
	protected Promise<Expression> eval(BranchContext context, DottedExpressionConstraint dotted) {
		final EclSerializer serializer = context.service(EclSerializer.class);
		final Collection<String> sourceFilter = Collections.singleton(serializer.serializeWithoutTerms(dotted.getConstraint()));
		final Collection<String> typeFilter = Collections.singleton(serializer.serializeWithoutTerms(dotted.getAttribute()));
		return SnomedEclRefinementEvaluator.evalRelationships(context, sourceFilter, typeFilter, Collections.emptySet(), false)
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
	
	/*Extract SNOMED CT IDs from the given expression if it is either a String/Long single/multi-valued predicate and the field is equal to RevisionDocument.Fields.ID*/
	private static Set<String> extractIds(Expression expression) {
		if (expression instanceof Predicate) {
			final Predicate predicate = (Predicate) expression;
			if (ID.equals(predicate.getField())) {
				if (predicate instanceof StringSetPredicate) {
					return ((StringSetPredicate) predicate).values();
				} else if (predicate instanceof StringPredicate) {
					return Collections.singleton(((StringPredicate) expression).getArgument());
				}
			}
		}
		throw new UnsupportedOperationException("Cannot extract ID values from: " + expression);
	}
	
	/**
	 * Extracts SNOMED CT IDs from the given expression if it is either single or multi-valued String predicate and the field is equal to RevisionDocument.Fields.ID.
	 * Otherwise it will try to evaluate the ecl completely without using the first returned expression.
	 * @param ecl - the original ECL which will resolve to the returned function parameter as {@link Expression}
	 */
	private static Function<Expression, Promise<Set<String>>> resolveIds(BranchContext context, ExpressionConstraint ecl) {
		return new Function<Expression, Promise<Set<String>>>() {
			@Override
			public Promise<Set<String>> apply(Expression expression) {
				try {
					return Promise.immediate(extractIds(expression));
				} catch (UnsupportedOperationException e) {
					final String eclExpression = context.service(EclSerializer.class).serializeWithoutTerms(ecl);
					// otherwise always evaluate the expression to ID set and return that
					return EclExpression.of(eclExpression).resolve(context);
				}
			}
		};
	}
	
	private static void addParentIds(SnomedConcept concept, final Set<String> collection) {
		if (concept.getParentIds() != null) {
			for (long parent : concept.getParentIds()) {
				if (IComponent.ROOT_IDL != parent) {
					collection.add(Long.toString(parent));
				}
			}
		}
	}
	
	private static void addAncestorIds(SnomedConcept concept, Set<String> collection) {
		if (concept.getAncestorIds() != null) {
			for (long ancestor : concept.getAncestorIds()) {
				if (IComponent.ROOT_IDL != ancestor) {
					collection.add(Long.toString(ancestor));
				}
			}
		}		
	}
	
	/*package*/ static Function<Set<String>, Expression> matchIdsOrNone() {
		return new Function<Set<String>, Expression>() {
			@Override
			public Expression apply(Set<String> ids) {
				return ids.isEmpty() ? Expressions.matchNone() : ids(ids);
			}
		};
	}

}
