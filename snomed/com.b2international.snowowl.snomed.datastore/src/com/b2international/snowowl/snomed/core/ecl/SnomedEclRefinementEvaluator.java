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

import static com.b2international.snowowl.datastore.index.RevisionDocument.Expressions.ids;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.ecl.AndRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueNotEquals;
import com.b2international.snowowl.snomed.ecl.ecl.Cardinality;
import com.b2international.snowowl.snomed.ecl.ecl.Comparison;
import com.b2international.snowowl.snomed.ecl.ecl.EclFactory;
import com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.NestedRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.OrRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.Refinement;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Range;

/**
 * @since 5.4
 */
final class SnomedEclRefinementEvaluator {

	private static final EclFactory ECL_FACTORY = EclFactory.eINSTANCE;
	
	private final PolymorphicDispatcher<Promise<Expression>> dispatcher = PolymorphicDispatcher.createForSingleTarget("eval", 2, 2, this); 
	
	private final Set<String> focusConcepts;

	public SnomedEclRefinementEvaluator(Set<String> focusConcepts) {
		this.focusConcepts = focusConcepts;
	}
	
	public Promise<Expression> evaluate(BranchContext context, Refinement refinement) {
		if (focusConcepts == null) {
			return Promise.immediate(Expressions.matchNone());
		} else {
			return dispatcher.invoke(context, refinement);
		}
	}
	
	protected Promise<Expression> eval(BranchContext context, Refinement refinement) {
		return SnomedEclEvaluationRequest.throwUnsupported(refinement); 
	}
	
	protected Promise<Expression> eval(BranchContext context, AttributeConstraint refinement) {
		return evalRefinement(context, (AttributeConstraint) refinement);
	}
	
	protected Promise<Expression> eval(final BranchContext context, AndRefinement and) {
		return Promise.all(evaluate(context, and.getLeft()), evaluate(context, and.getRight()))
				.then(new Function<List<Object>, Expression>() {
					@Override
					public Expression apply(List<Object> input) {
						final Expression left = (Expression) input.get(0);
						final Expression right = (Expression) input.get(1);
						return Expressions.builder().must(left).must(right).build();
					}
				});
	}
	
	protected Promise<Expression> eval(final BranchContext context, OrRefinement or) {
		return Promise.all(evaluate(context, or.getLeft()), evaluate(context, or.getRight()))
				.then(new Function<List<Object>, Expression>() {
					@Override
					public Expression apply(List<Object> input) {
						final Expression left = (Expression) input.get(0);
						final Expression right = (Expression) input.get(1);
						return Expressions.builder().should(left).should(right).build();
					}
				});
	}
	
	protected Promise<Expression> eval(final BranchContext context, NestedRefinement nested) {
		return evaluate(context, nested.getNested());
	}
	
	private Promise<Expression> evalRefinement(BranchContext context, AttributeConstraint refinement) {
		final Cardinality cardinality = refinement.getCardinality();
		// filterBySource, filterByType and filterByDestination accepts ECL expressions as well, so serialize them into ECL and pass as String when required
		final EclSerializer serializer = context.service(EclSerializer.class);
		final Collection<String> typeConceptFilter = Collections.singleton(serializer.serialize(refinement.getAttribute()));
		final String destinationConceptExpression = serializer.serialize(rewrite(refinement.getComparison()));
		final Collection<String> focusConceptFilter = refinement.isReversed() ? Collections.singleton(destinationConceptExpression) : focusConcepts;
		final Collection<String> valueConceptFilter = refinement.isReversed() ? focusConcepts : Collections.singleton(destinationConceptExpression);
		// if reversed refinement, then we are interested in the destinationIds otherwise we need the sourceIds
		final Function<ISnomedRelationship, String> idFunction = refinement.isReversed() ? ISnomedRelationship::getDestinationId : ISnomedRelationship::getSourceId;
		
		// the default cardinality is [1..*]
		final boolean isUnbounded = cardinality == null ? true : cardinality.getMax() == -1;
		final int min = cardinality == null ? 1 : cardinality.getMin();
		final int max = isUnbounded ? Integer.MAX_VALUE : cardinality.getMax();
		final Range<Integer> cardinalityRange = Range.closed(min, max);

		final Expression focusConceptExpression = ids(focusConcepts);
		
		if (min == 0) {
			if (isUnbounded) {
				// zero and unbounded attributes, just match all focus concepts
				return Promise.immediate(focusConceptExpression);
			} else {
				// otherwise evaluate to BOOL(MUST(focus) MUST_NOT(max+1))
				// construct the positive side of the query with this allowed attribute range
				final Range<Integer> positiveRange = Range.closed(max + 1, Integer.MAX_VALUE);
				return evalRefinement(context, focusConceptFilter, typeConceptFilter, valueConceptFilter, positiveRange, idFunction)
						.then(new Function<Expression, Expression>() {
							@Override
							public Expression apply(Expression positiveConcepts) {
								// if positiveConcepts is match none, then don't add it to the expression and just return the focusConcepts
								return Expressions.matchNone().equals(positiveConcepts) 
										? focusConceptExpression
												: Expressions.builder().must(focusConceptExpression).mustNot(positiveConcepts).build();
							}
						});
			}
		} else {
			// if the cardinality either 0 or the min is at least one, then the relationship query is enough
			return evalRefinement(context, focusConceptFilter, typeConceptFilter, valueConceptFilter, cardinalityRange, idFunction); 
		}
	}

	/*package*/ static Promise<Expression> evalRefinement(final BranchContext context, 
			final Collection<String> sourceFilter, 
			final Collection<String> typeFilter,
			final Collection<String> destinationFilter,
			final Range<Integer> cardinality,
			final Function<ISnomedRelationship, String> idProvider) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true) 
				.filterBySource(sourceFilter)
				.filterByType(typeFilter)
				.filterByDestination(destinationFilter)
				.filterByCharacteristicTypes(ImmutableSet.of(Concepts.INFERRED_RELATIONSHIP, Concepts.ADDITIONAL_RELATIONSHIP))
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
	
}
