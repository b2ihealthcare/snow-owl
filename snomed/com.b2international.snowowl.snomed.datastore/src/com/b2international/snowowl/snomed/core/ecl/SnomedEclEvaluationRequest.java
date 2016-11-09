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
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Predicate;
import com.b2international.index.query.StringPredicate;
import com.b2international.index.query.StringSetPredicate;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.ecl.AncestorOf;
import com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.Any;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueNotEquals;
import com.b2international.snowowl.snomed.ecl.ecl.Cardinality;
import com.b2international.snowowl.snomed.ecl.ecl.ChildOf;
import com.b2international.snowowl.snomed.ecl.ecl.Comparison;
import com.b2international.snowowl.snomed.ecl.ecl.ConceptReference;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOf;
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf;
import com.b2international.snowowl.snomed.ecl.ecl.EclFactory;
import com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.MemberOf;
import com.b2international.snowowl.snomed.ecl.ecl.NestedExpression;
import com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.ParentOf;
import com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.Refinement;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Range;
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
	private static final EclFactory ECL_FACTORY = EclFactory.eINSTANCE;
	
	private final PolymorphicDispatcher<Promise<Expression>> dispatcher = PolymorphicDispatcher.createForSingleTarget("eval", 2, 2, this);

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
		return evaluate(context, currentExpression);
	}
	
	private Promise<Expression> evaluate(BranchContext context, EObject expression) {
		return dispatcher.invoke(context, expression);
	}

	protected Promise<Expression> eval(BranchContext context, EObject eObject) {
		return throwUnsupported(eObject);
	}

	protected Promise<Expression> eval(BranchContext context, Any any) {
		return Promise.immediate(Expressions.matchAll());
	}
	
	protected Promise<Expression> eval(BranchContext context, ConceptReference concept) {
		return Promise.immediate(id(concept.getId()));
	}
	
	protected Promise<Expression> eval(BranchContext context, MemberOf memberOf) {
		if (memberOf.getConstraint() instanceof ConceptReference) {
			final ConceptReference concept = (ConceptReference) memberOf.getConstraint();
			return Promise.immediate(referringRefSet(concept.getId()));
		} else if (memberOf.getConstraint() instanceof Any) {
			return Promise.immediate(Expressions.exists(REFERRING_REFSETS));
		} else {
			return throwUnsupported(memberOf.getConstraint());
		}
	}
	
	protected Promise<Expression> eval(BranchContext context, final DescendantOf descendantOf) {
		return evaluate(context, descendantOf.getConstraint())
				.then(EXTRACT_IDS)
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
	
	protected Promise<Expression> eval(BranchContext context, final DescendantOrSelfOf descendantOrSelfOf) {
		return evaluate(context, descendantOrSelfOf.getConstraint())
				.then(EXTRACT_IDS)
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
	
	protected Promise<Expression> eval(BranchContext context, final ChildOf childOf) {
		return evaluate(context, childOf.getConstraint())
				.then(EXTRACT_IDS)
				.then(new Function<Set<String>, Expression>() {
					@Override
					public Expression apply(Set<String> ids) {
						return parents(ids);
					}
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final ParentOf parentOf) {
		return evaluate(context, parentOf.getConstraint())
				.then(EXTRACT_IDS)
				.thenWith(fetchConcepts(context))
				.then(new Function<SnomedConcepts, Expression>() {
					@Override
					public Expression apply(SnomedConcepts concepts) {
						final Set<String> parents = newHashSet();
						for (ISnomedConcept concept : concepts) {
							addParentIds(concept, parents);
						}
						return parents.isEmpty() ? Expressions.matchNone() : ids(parents);
					}
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final AncestorOf ancestorOf) {
		return evaluate(context, ancestorOf.getConstraint())
				.then(EXTRACT_IDS)
				.thenWith(fetchConcepts(context))
				.then(new Function<SnomedConcepts, Expression>() {
					@Override
					public Expression apply(SnomedConcepts concepts) {
						final Set<String> ancestors = newHashSet();
						for (ISnomedConcept concept : concepts) {
							addParentIds(concept, ancestors);
							addAncestorIds(concept, ancestors);
						}
						return ancestors.isEmpty() ? Expressions.matchNone() : ids(ancestors);
					}
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final AncestorOrSelfOf ancestorOrSelfOf) {
		return evaluate(context, ancestorOrSelfOf.getConstraint())
				.then(EXTRACT_IDS)
				.thenWith(fetchConcepts(context))
				.then(new Function<SnomedConcepts, Expression>() {
					@Override
					public Expression apply(SnomedConcepts concepts) {
						final Set<String> ancestors = newHashSet();
						for (ISnomedConcept concept : concepts) {
							ancestors.add(concept.getId());
							addParentIds(concept, ancestors);
							addAncestorIds(concept, ancestors);
						}
						return ancestors.isEmpty() ? Expressions.matchNone() : ids(ancestors);
					}
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final AndExpressionConstraint and) {
		return Promise.all(evaluate(context, and.getLeft()), evaluate(context, and.getRight()))
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
	
	protected Promise<Expression> eval(BranchContext context, final ExclusionExpressionConstraint exclusion) {
		return Promise.all(evaluate(context, exclusion.getLeft()), evaluate(context, exclusion.getRight()))
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
	
	protected Promise<Expression> eval(BranchContext context, final RefinedExpressionConstraint refined) {
		final Refinement refinement = refined.getRefinement();
		final Cardinality cardinality = refinement.getCardinality();
		// filterBySource, filterByType and filterByDestination accepts ECL expressions as well, so serialize them into ECL and pass as String when required
		final EclSerializer serializer = context.service(EclSerializer.class);
		final String sourceConceptExpression = serializer.serialize(refined.getConstraint());
		final String typeConceptExpression = serializer.serialize(refinement.getAttribute());
		final String destinationConceptExpression = serializer.serialize(rewrite(refinement.getComparison()));
		final String focusConceptExpression = refinement.isReversed() ? destinationConceptExpression : sourceConceptExpression;
		final String valueConceptExpression = refinement.isReversed() ? sourceConceptExpression : destinationConceptExpression;
		// if reversed refinement, then we are interested in the destinationIds otherwise we need the sourceIds
		final Function<ISnomedRelationship, String> idFunction = refinement.isReversed() ? ISnomedRelationship::getDestinationId : ISnomedRelationship::getSourceId;
		
		// the default cardinality is [1..*]
		final boolean isUnbounded = cardinality == null ? true : cardinality.getMax() == -1;
		final int min = cardinality == null ? 1 : cardinality.getMin();
		final int max = isUnbounded ? Integer.MAX_VALUE : cardinality.getMax();
		final Range<Integer> cardinalityRange = Range.closed(min, max);
		
		if (min == 0) {
			if (isUnbounded) {
				// zero and unbounded attributes, just match all focus concepts
				return evaluate(context, refined.getConstraint());
			} else {
				// otherwise evaluate to BOOL(MUST(focus) MUST_NOT(max+1))
				// construct the positive side of the query with this allowed attribute range
				final Range<Integer> positiveRange = Range.closed(max + 1, Integer.MAX_VALUE);
				return Promise.all(
						// eval the focusConcept expression
						SnomedRequests.prepareEclEvaluation(focusConceptExpression)
							.build(context.id(), context.branch().path())
							.execute(context.service(IEventBus.class))
							.thenWith(promise -> promise),
						// eval the same expression with positive range
						evalRefinement(context, focusConceptExpression, typeConceptExpression, valueConceptExpression, positiveRange, idFunction))
						.then(new Function<List<Object>, Expression>() {
							@Override
							public Expression apply(List<Object> expressions) {
								final Expression focusConcepts = (Expression) expressions.get(0);
								final Expression positiveConcepts = (Expression) expressions.get(1);
								// if positiveConcepts is match none, then don't add it to the expression and just return the focusConcepts
								return Expressions.matchNone().equals(positiveConcepts) 
										? focusConcepts
										: Expressions.builder().must(focusConcepts).mustNot(positiveConcepts).build();
							}
						});
			}
		} else {
			// if the cardinality either 0 or the min is at least one, then the relationship query is enough
			return evalRefinement(context, focusConceptExpression, typeConceptExpression, valueConceptExpression, cardinalityRange, idFunction); 
		}
		
	}

	private Promise<Expression> evalRefinement(final BranchContext context, 
			final String sourceExpression, 
			final String typeExpression,
			final String destinationExpression,
			final Range<Integer> cardinality,
			final Function<ISnomedRelationship, String> idProvider) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true) 
				.filterBySource(sourceExpression)
				.filterByType(typeExpression)
				.filterByDestination(destinationExpression)
				.setFields(ImmutableSet.of(
					SnomedRelationshipIndexEntry.Fields.ID, 
					SnomedRelationshipIndexEntry.Fields.SOURCE_ID, 
					SnomedRelationshipIndexEntry.Fields.DESTINATION_ID
				))
				.build(context.id(), context.branch().path())
				.execute(context.service(IEventBus.class))
				.then(new Function<SnomedRelationships, Expression>() {
					@Override
					public Expression apply(SnomedRelationships matchingAttributes) {
						final Set<String> ids = newHashSet();
						final Multimap<String, ISnomedRelationship> indexedByMatchingIds = Multimaps.index(matchingAttributes, idProvider);
						
						for (String matchingConceptId : indexedByMatchingIds.keySet()) {
							final Collection<ISnomedRelationship> attributes = indexedByMatchingIds.get(matchingConceptId);
							final int numberOfMatchingAttributes = attributes.size();
							if (cardinality.contains(numberOfMatchingAttributes)) {
								ids.add(matchingConceptId);
							}
						}
						return ids.isEmpty() ? Expressions.matchNone() : ids(ids);
					}
				});
	}
	
	private ExpressionConstraint rewrite(Comparison comparison) {
		if (comparison instanceof AttributeValueEquals) {
			return comparison.getConstraint();
		} else if (comparison instanceof AttributeValueNotEquals) {
			// convert != expression to exclusion constraint
			final ExclusionExpressionConstraint exclusion = ECL_FACTORY.createExclusionExpressionConstraint();
			// set Any as left of exclusion
			exclusion.setLeft(ECL_FACTORY.createAny());
			// set original constraint as right of exclusion
			exclusion.setRight(comparison.getConstraint());
			return exclusion;
		}
		throw new UnsupportedOperationException("Cannot rewrite comparison: " + comparison);
	}

	protected Promise<Expression> eval(BranchContext context, final NestedExpression nested) {
		return evaluate(context, nested.getNested());
	}
	
	private Promise<Expression> throwUnsupported(EObject eObject) {
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
	
	private static Function<Expression, Set<String>> EXTRACT_IDS = new Function<Expression, Set<String>>() {
		@Override
		public Set<String> apply(Expression expression) {
			return extractIds(expression);
		}
	};
	
	private static Function<Set<String>, Promise<SnomedConcepts>> fetchConcepts(final BranchContext context) {
		return new Function<Set<String>, Promise<SnomedConcepts>>() {
			@Override
			public Promise<SnomedConcepts> apply(Set<String> ids) {
				return SnomedRequests.prepareSearchConcept()
						.setLimit(ids.size())
						.setComponentIds(ids)
						.build(context.id(), context.branch().path())
						.execute(context.service(IEventBus.class));
			}
		};
	}
	
	private static void addParentIds(ISnomedConcept concept, final Set<String> collection) {
		if (concept.getParentIds() != null) {
			for (long parent : concept.getParentIds()) {
				if (IComponent.ROOT_IDL != parent) {
					collection.add(Long.toString(parent));
				}
			}
		}
	}
	
	private static void addAncestorIds(ISnomedConcept concept, Set<String> collection) {
		if (concept.getAncestorIds() != null) {
			for (long ancestor : concept.getAncestorIds()) {
				if (IComponent.ROOT_IDL != ancestor) {
					collection.add(Long.toString(ancestor));
				}
			}
		}		
	}

}
