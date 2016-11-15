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

import static com.b2international.snowowl.datastore.index.RevisionDocument.Fields.ID;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Fields.DESTINATION_ID;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Fields.GROUP;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Fields.SOURCE_ID;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;

import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.ecl.AndRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup;
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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

/**
 * @since 5.4
 */
final class SnomedEclRefinementEvaluator {

	private static final int UNBOUNDED_CARDINALITY = -1;
	private static final Range<Long> ANY_GROUP = Range.closed(0L, Long.MAX_VALUE);
	private static final EclFactory ECL_FACTORY = EclFactory.eINSTANCE;
	
	private final PolymorphicDispatcher<Promise<Expression>> refinementDispatcher = PolymorphicDispatcher.createForSingleTarget("eval", 2, 2, this);
	private final PolymorphicDispatcher<Promise<Collection<ISnomedRelationship>>> groupRefinementDispatcher = PolymorphicDispatcher.createForSingleTarget("evalGroup", 3, 3, this);
	
	private final Set<String> focusConcepts;

	public SnomedEclRefinementEvaluator(Set<String> focusConcepts) {
		this.focusConcepts = focusConcepts;
	}
	
	public Promise<Expression> evaluate(BranchContext context, Refinement refinement) {
		return refinementDispatcher.invoke(context, refinement);
	}
	
	protected Promise<Expression> eval(BranchContext context, Refinement refinement) {
		return SnomedEclEvaluationRequest.throwUnsupported(refinement); 
	}
	
	protected Promise<Expression> eval(final BranchContext context, final AttributeConstraint refinement) {
		return evalRefinement(context, refinement, false, ANY_GROUP)
				.then(new Function<Collection<ISnomedRelationship>, Set<String>>() {
					@Override
					public Set<String> apply(Collection<ISnomedRelationship> input) {
						final Function<ISnomedRelationship, String> idProvider = refinement.isReversed() ? ISnomedRelationship::getDestinationId : ISnomedRelationship::getSourceId;
						final Set<String> matchingIds = FluentIterable.from(input).transform(idProvider).toSet();
						// two cases here, one is the [1..x] the other is [0..x]
						final Cardinality cardinality = refinement.getCardinality();
						if (cardinality != null && cardinality.getMin() == 0 && cardinality.getMax() != UNBOUNDED_CARDINALITY) {
							// XXX internal evaluation returns negative matches, that should be excluded from the focusConcept set
							final Set<String> matches = newHashSet(focusConcepts);
							matches.removeAll(matchingIds);
							return matches;
						} else {
							return matchingIds;
						}
					}
				})
				.fail(new Function<Throwable, Set<String>>() {
					@Override
					public Set<String> apply(Throwable throwable) {
						if (throwable instanceof MatchAll) {
							return focusConcepts;
						}
						throw new SnowowlRuntimeException(throwable);
					}
				})
				.then(SnomedEclEvaluationRequest.matchIdsOrNone());
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
	
	protected Promise<Expression> eval(final BranchContext context, AttributeGroup group) {
		final Cardinality cardinality = group.getCardinality();
		final boolean isUnbounded = cardinality == null ? true : cardinality.getMax() == UNBOUNDED_CARDINALITY;
		final long min = cardinality == null ? 1 : cardinality.getMin();
		final long max = isUnbounded ? Long.MAX_VALUE : cardinality.getMax();
		final Range<Long> groupCardinality = Range.closed(min, max);
		
		if (min == 0) {
			if (isUnbounded) {
				return Promise.immediate(RevisionDocument.Expressions.ids(focusConcepts));
			} else {
				final Range<Long> exclusionRange = Range.closed(max + 1, Long.MAX_VALUE);
				return evaluateGroup(context, exclusionRange, group.getRefinement())
						.then(new Function<Collection<ISnomedRelationship>, Set<String>>() {
							@Override
							public Set<String> apply(Collection<ISnomedRelationship> input) {
								final Set<String> excludedConcepts = FluentIterable.from(input).transform(ISnomedRelationship::getSourceId).toSet();
								final Set<String> matches = newHashSet(focusConcepts);
								matches.removeAll(excludedConcepts);
								return matches;
							}
						})
						.then(SnomedEclEvaluationRequest.matchIdsOrNone());
			}
		} else {
			return evaluateGroup(context, groupCardinality, group.getRefinement())
					.then(new Function<Collection<ISnomedRelationship>, Set<String>>() {
						@Override
						public Set<String> apply(Collection<ISnomedRelationship> input) {
							return FluentIterable.from(input).transform(ISnomedRelationship::getSourceId).toSet();
						}
					})
					.then(SnomedEclEvaluationRequest.matchIdsOrNone());
		}
		
	}
	
	protected Promise<Collection<ISnomedRelationship>> evaluateGroup(BranchContext context, Range<Long> groupCardinality, Refinement refinement) {
		return groupRefinementDispatcher.invoke(context, groupCardinality, refinement);
	}
	
	protected Promise<Collection<ISnomedRelationship>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final Refinement refinement) {
		return SnomedEclEvaluationRequest.throwUnsupported(refinement);
	}
	
	protected Promise<Collection<ISnomedRelationship>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final AttributeConstraint refinement) {
		if (refinement.isReversed()) {
			throw new BadRequestException("Reversed attributes are not supported in group refinements");
		} else {
			return evalRefinement(context, refinement, true, groupCardinality)
					.fail(new Function<Throwable, Collection<ISnomedRelationship>>() {
						@Override
						public Collection<ISnomedRelationship> apply(Throwable throwable) {
							if (throwable instanceof MatchAll) {
								final Collection<ISnomedRelationship> matchingAttributes = newHashSetWithExpectedSize(focusConcepts.size());
								for (String focusConceptId : focusConcepts) {
									final SnomedRelationship relationship = new SnomedRelationship();
									relationship.setSource(new SnomedConcept(focusConceptId));
									matchingAttributes.add(relationship);
								}
								return matchingAttributes;
							}
							throw new SnowowlRuntimeException(throwable);
						}
					});
		}
	}
	
	protected Promise<Collection<ISnomedRelationship>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final AndRefinement and) {
		return Promise.all(evaluateGroup(context, groupCardinality, and.getLeft()), evaluateGroup(context, groupCardinality, and.getRight()))
				.then(evalParts(groupCardinality, Sets::intersection));
	}
	
	protected Promise<Collection<ISnomedRelationship>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final OrRefinement or) {
		return Promise.all(evaluateGroup(context, groupCardinality, or.getLeft()), evaluateGroup(context, groupCardinality, or.getRight()))
				.then(evalParts(groupCardinality, Sets::union));
	}
	
	protected Promise<Collection<ISnomedRelationship>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final NestedRefinement nested) {
		return evaluateGroup(context, groupCardinality, nested.getNested());
	}
	
	private Function<List<Object>, Collection<ISnomedRelationship>> evalParts(final Range<Long> groupCardinality, BinaryOperator<Set<Integer>> groupOperator) {
		return new Function<List<Object>, Collection<ISnomedRelationship>>() {
			@Override
			public Collection<ISnomedRelationship> apply(List<Object> input) {
				final Collection<ISnomedRelationship> left = (Collection<ISnomedRelationship>) input.get(0);
				final Collection<ISnomedRelationship> right = (Collection<ISnomedRelationship>) input.get(1);
				
				final Collection<ISnomedRelationship> matchingAttributes = newHashSet();
				
				// group left and right side by source ID
				final Multimap<String, ISnomedRelationship> leftRelationshipsBySource = Multimaps.index(left, ISnomedRelationship::getSourceId);
				final Multimap<String, ISnomedRelationship> rightRelationshipsBySource = Multimaps.index(right, ISnomedRelationship::getSourceId);
				
				// check that each ID has the required number of groups with left and right relationships
				for (String sourceConcept : Iterables.concat(leftRelationshipsBySource.keySet(), rightRelationshipsBySource.keySet())) {
					final Multimap<Integer, ISnomedRelationship> validGroups = ArrayListMultimap.create();
					
					final Collection<ISnomedRelationship> leftSourceRelationships = leftRelationshipsBySource.get(sourceConcept);
					final Collection<ISnomedRelationship> rightSourceRelationships = rightRelationshipsBySource.get(sourceConcept);
				
					final Multimap<Integer, ISnomedRelationship> leftRelationshipsByGroup = Multimaps.index(leftSourceRelationships, ISnomedRelationship::getGroup);
					final Multimap<Integer, ISnomedRelationship> rightRelationshipsByGroup = Multimaps.index(rightSourceRelationships, ISnomedRelationship::getGroup);
					
					for (Integer group : groupOperator.apply(leftRelationshipsByGroup.keySet(), rightRelationshipsByGroup.keySet())) {
						validGroups.get(group).addAll(leftRelationshipsByGroup.get(group));
						validGroups.get(group).addAll(rightRelationshipsByGroup.get(group));
					}
					
					if (groupCardinality.contains((long) validGroups.keySet().size())) {
						matchingAttributes.addAll(validGroups.values());
					}
				}
				return matchingAttributes;
			}
		};
	}

	private Promise<Collection<ISnomedRelationship>> evalRefinement(BranchContext context, AttributeConstraint refinement, final boolean grouped, final Range<Long> groupCardinality) {
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
		final boolean isUnbounded = cardinality == null ? true : cardinality.getMax() == UNBOUNDED_CARDINALITY;
		final long min = cardinality == null ? 1 : cardinality.getMin();
		final long max = isUnbounded ? Long.MAX_VALUE : cardinality.getMax();
		final Range<Long> cardinalityRange = Range.closed(min, max);

		if (min == 0) {
			if (isUnbounded) {
				// zero and unbounded attributes, just match all focus concepts using the source or destination IDs
				return Promise.fail(new MatchAll());
			} else {
				// otherwise evaluate to BOOL(MUST(focus) MUST_NOT(max+1))
				// construct the positive side of the query with this allowed attribute range
				final Range<Long> positiveRange = Range.closed(max + 1, Long.MAX_VALUE);
				return evalRefinement(context, focusConceptFilter, typeConceptFilter, valueConceptFilter, grouped)
						.then(filterByCardinality(groupCardinality, positiveRange, idFunction));
			}
		} else {
			// if the cardinality either 0 or the min is at least one, then the relationship query is enough
			return evalRefinement(context, focusConceptFilter, typeConceptFilter, valueConceptFilter, grouped)
					.then(filterByCardinality(groupCardinality, cardinalityRange, idFunction)); 
		}
	}
	
	/*package*/ static Function<SnomedRelationships, Collection<ISnomedRelationship>> filterByCardinality(final Range<Long> groupCardinality, final Range<Long> cardinality, final Function<ISnomedRelationship, String> idProvider) {
		return new Function<SnomedRelationships, Collection<ISnomedRelationship>>() {
			@Override
			public Collection<ISnomedRelationship> apply(SnomedRelationships candidateAttributes) {
				final Multimap<String, ISnomedRelationship> indexedByMatchingIds = Multimaps.index(candidateAttributes, idProvider);
				final Collection<ISnomedRelationship> matchingAttributes = newArrayList();
				
				final Range<Long> allowedRelationshipCardinality;
				if (!ANY_GROUP.equals(groupCardinality)) {
					final long minRelationships;
					if (groupCardinality.lowerEndpoint() == 0) {
						minRelationships = cardinality.lowerEndpoint();
					} else {
						minRelationships = groupCardinality.lowerEndpoint() * cardinality.lowerEndpoint();
					}
					final long maxRelationships;
					if (groupCardinality.hasUpperBound() && cardinality.hasUpperBound()) {
						if (groupCardinality.upperEndpoint() == Long.MAX_VALUE || cardinality.upperEndpoint() == Long.MAX_VALUE) {
							maxRelationships = Long.MAX_VALUE;
						} else {
							maxRelationships = groupCardinality.upperEndpoint() * cardinality.upperEndpoint();
						}
					} else {
						// either group or relationship cardinality is unbounded
						maxRelationships = Long.MAX_VALUE;
					}
					allowedRelationshipCardinality = Range.closed(minRelationships, maxRelationships);
				} else {
					allowedRelationshipCardinality = cardinality;
				}
				
				for (String matchingConceptId : indexedByMatchingIds.keySet()) {
					final Collection<ISnomedRelationship> attributes = indexedByMatchingIds.get(matchingConceptId);
					final int numberOfMatchingAttributes = attributes.size();
					if (allowedRelationshipCardinality.contains((long) numberOfMatchingAttributes)) {
						if (!ANY_GROUP.equals(groupCardinality)) {
							// if groups should be considered as well, then check group numbers in the matching sets
							final Multimap<Integer, ISnomedRelationship> indexedByGroup = FluentIterable.from(attributes).index(ISnomedRelationship::getGroup);
							checkState(!indexedByGroup.containsKey(0), "At this point ungrouped relationships should not be part of the matching attributes set");
							// check that the concept has at least the right amount of groups
							if (groupCardinality.contains((long) indexedByGroup.keySet().size())) {
								matchingAttributes.addAll(attributes);
							}
						} else {
							matchingAttributes.addAll(attributes);
						}
					}
				}
				return matchingAttributes;
			}
		};
	}

	/*package*/ static Promise<SnomedRelationships> evalRefinement(final BranchContext context, 
			final Collection<String> sourceFilter, 
			final Collection<String> typeFilter,
			final Collection<String> destinationFilter,
			final boolean groupedRelationshipsOnly) {

		final ImmutableSet.Builder<String> fieldsToLoad = ImmutableSet.builder();
		fieldsToLoad.add(ID, SOURCE_ID,	DESTINATION_ID);
		if (groupedRelationshipsOnly) {
			fieldsToLoad.add(GROUP);
		}
		
		final SnomedRelationshipSearchRequestBuilder req = SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true) 
				.filterBySource(sourceFilter)
				.filterByType(typeFilter)
				.filterByDestination(destinationFilter)
				.filterByCharacteristicTypes(ImmutableSet.of(Concepts.INFERRED_RELATIONSHIP, Concepts.ADDITIONAL_RELATIONSHIP))
				.setFields(fieldsToLoad.build());
		
		// if a grouping refinement, then filter relationships with group number gte 1
		if (groupedRelationshipsOnly) {
			req.filterByGroup(1, Integer.MAX_VALUE);
		}
		
		return req.build(context.id(), context.branch().path()).execute(context.service(IEventBus.class));
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
	
	// Helper Throwable class to quickly return from attribute constraint evaluation when all matches are valid
	private static class MatchAll extends Throwable {}
	
}
