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
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;

import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.commons.options.Options;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.request.RevisionSearchRequest;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.ecl.AndRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeComparison;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueNotEquals;
import com.b2international.snowowl.snomed.ecl.ecl.Cardinality;
import com.b2international.snowowl.snomed.ecl.ecl.Comparison;
import com.b2international.snowowl.snomed.ecl.ecl.DataTypeComparison;
import com.b2international.snowowl.snomed.ecl.ecl.EclFactory;
import com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.NestedRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.OrRefinement;
import com.b2international.snowowl.snomed.ecl.ecl.Refinement;
import com.b2international.snowowl.snomed.ecl.ecl.StringValueEquals;
import com.b2international.snowowl.snomed.ecl.ecl.StringValueNotEquals;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
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

	private static final Set<String> ALLOWED_CHARACTERISTIC_TYPES = ImmutableSet.of(Concepts.INFERRED_RELATIONSHIP, Concepts.ADDITIONAL_RELATIONSHIP);
	private static final int UNBOUNDED_CARDINALITY = -1;
	private static final Range<Long> ANY_GROUP = Range.closed(0L, Long.MAX_VALUE);
	private static final EclFactory ECL_FACTORY = EclFactory.eINSTANCE;
	
	private final PolymorphicDispatcher<Promise<Expression>> refinementDispatcher = PolymorphicDispatcher.createForSingleTarget("eval", 2, 2, this);
	private final PolymorphicDispatcher<Promise<Collection<Property>>> groupRefinementDispatcher = PolymorphicDispatcher.createForSingleTarget("evalGroup", 3, 3, this);
	
	private final EclExpression focusConcepts;
	
	public SnomedEclRefinementEvaluator(EclExpression focusConcepts) {
		this.focusConcepts = focusConcepts;
	}
	
	public Promise<Expression> evaluate(BranchContext context, Refinement refinement) {
		// start evaluating the focusConcepts expression
		this.focusConcepts.resolve(context);
		return refinementDispatcher.invoke(context, refinement);
	}
	
	protected Promise<Expression> eval(BranchContext context, Refinement refinement) {
		return SnomedEclEvaluationRequest.throwUnsupported(refinement); 
	}
	
	protected Promise<Expression> eval(final BranchContext context, final AttributeConstraint refinement) {
		return evalRefinement(context, refinement, false, ANY_GROUP)
				.thenWith(new Function<Collection<Property>, Promise<Expression>>() {
					@Override
					public Promise<Expression> apply(Collection<Property> input) {
						final Function<Property, Object> idProvider = refinement.isReversed() ? Property::getValue : Property::getObjectId;
						final Set<String> matchingIds = FluentIterable.from(input).transform(idProvider).filter(String.class).toSet();
						// two cases here, one is the [1..x] the other is [0..x]
						final Cardinality cardinality = refinement.getCardinality();
						if (cardinality != null && cardinality.getMin() == 0 && cardinality.getMax() != UNBOUNDED_CARDINALITY) {
							// XXX internal evaluation returns negative matches, that should be excluded from the focusConcept set
							return focusConcepts.resolveToExclusionExpression(context, matchingIds);
						} else {
							return Promise.immediate(SnomedEclEvaluationRequest.matchIdsOrNone().apply(matchingIds));
						}
					}
				})
				.failWith(new Function<Throwable, Promise<Expression>>() {
					@Override
					public Promise<Expression> apply(Throwable throwable) {
						if (throwable instanceof MatchAll) {
							return focusConcepts.resolveToExpression(context);
						}
						throw new SnowowlRuntimeException(throwable);
					}
				});
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
				return focusConcepts.resolveToExpression(context);
			} else {
				final Range<Long> exclusionRange = Range.closed(max + 1, Long.MAX_VALUE);
				return evaluateGroup(context, exclusionRange, group.getRefinement())
						.thenWith(new Function<Collection<Property>, Promise<Expression>>() {
							@Override
							public Promise<Expression> apply(Collection<Property> input) {
								final Set<String> excludedMatches = FluentIterable.from(input).transform(Property::getObjectId).toSet();
								return focusConcepts.resolveToExclusionExpression(context, excludedMatches);
							}
						});
			}
		} else {
			return evaluateGroup(context, groupCardinality, group.getRefinement())
					.then(new Function<Collection<Property>, Set<String>>() {
						@Override
						public Set<String> apply(Collection<Property> input) {
							return FluentIterable.from(input).transform(Property::getObjectId).toSet();
						}
					})
					.then(SnomedEclEvaluationRequest.matchIdsOrNone());
		}
		
	}
	
	protected Promise<Collection<Property>> evaluateGroup(BranchContext context, Range<Long> groupCardinality, Refinement refinement) {
		return groupRefinementDispatcher.invoke(context, groupCardinality, refinement);
	}
	
	protected Promise<Collection<Property>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final Refinement refinement) {
		return SnomedEclEvaluationRequest.throwUnsupported(refinement);
	}
	
	protected Promise<Collection<Property>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final AttributeConstraint refinement) {
		if (refinement.isReversed()) {
			throw new BadRequestException("Reversed attributes are not supported in group refinements");
		} else {
			return evalRefinement(context, refinement, true, groupCardinality)
					.failWith(new Function<Throwable, Promise<Collection<Property>>>() {
						@Override
						public Promise<Collection<Property>> apply(Throwable throwable) {
							if (throwable instanceof MatchAll) {
								return focusConcepts.resolve(context)
										.then(new Function<Set<String>, Collection<Property>>() {
											@Override
											public Collection<Property> apply(Set<String> focusConceptMatches) {
												final Collection<Property> matchingProperties = newHashSetWithExpectedSize(focusConceptMatches.size());
												for (String focusConceptId : focusConceptMatches) {
													matchingProperties.add(new Property(focusConceptId));
												}
												return matchingProperties;
											}
										});
							}
							throw new SnowowlRuntimeException(throwable);
						}
					});
		}
	}
	
	protected Promise<Collection<Property>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final AndRefinement and) {
		return Promise.all(evaluateGroup(context, groupCardinality, and.getLeft()), evaluateGroup(context, groupCardinality, and.getRight()))
				.then(evalParts(groupCardinality, Sets::intersection));
	}
	
	protected Promise<Collection<Property>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final OrRefinement or) {
		return Promise.all(evaluateGroup(context, groupCardinality, or.getLeft()), evaluateGroup(context, groupCardinality, or.getRight()))
				.then(evalParts(groupCardinality, Sets::union));
	}
	
	protected Promise<Collection<Property>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final NestedRefinement nested) {
		return evaluateGroup(context, groupCardinality, nested.getNested());
	}
	
	private Function<List<Object>, Collection<Property>> evalParts(final Range<Long> groupCardinality, BinaryOperator<Set<Integer>> groupOperator) {
		return new Function<List<Object>, Collection<Property>>() {
			@Override
			public Collection<Property> apply(List<Object> input) {
				final Collection<Property> left = (Collection<Property>) input.get(0);
				final Collection<Property> right = (Collection<Property>) input.get(1);
				
				final Collection<Property> matchingAttributes = newHashSet();
				
				// group left and right side by source ID
				final Multimap<String, Property> leftRelationshipsBySource = Multimaps.index(left, Property::getObjectId);
				final Multimap<String, Property> rightRelationshipsBySource = Multimaps.index(right, Property::getObjectId);
				
				// check that each ID has the required number of groups with left and right relationships
				for (String sourceConcept : Iterables.concat(leftRelationshipsBySource.keySet(), rightRelationshipsBySource.keySet())) {
					final Multimap<Integer, Property> validGroups = ArrayListMultimap.create();
					
					final Collection<Property> leftSourceRelationships = leftRelationshipsBySource.get(sourceConcept);
					final Collection<Property> rightSourceRelationships = rightRelationshipsBySource.get(sourceConcept);
				
					final Multimap<Integer, Property> leftRelationshipsByGroup = Multimaps.index(leftSourceRelationships, Property::getGroup);
					final Multimap<Integer, Property> rightRelationshipsByGroup = Multimaps.index(rightSourceRelationships, Property::getGroup);
					
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

	private Promise<Collection<Property>> evalRefinement(final BranchContext context, final AttributeConstraint refinement, final boolean grouped, final Range<Long> groupCardinality) {
		final Cardinality cardinality = refinement.getCardinality();
		// the default cardinality is [1..*]
		final boolean isUnbounded = cardinality == null ? true : cardinality.getMax() == UNBOUNDED_CARDINALITY;
		final long min = cardinality == null ? 1 : cardinality.getMin();
		final long max = isUnbounded ? Long.MAX_VALUE : cardinality.getMax();

		final Range<Long> propertyCardinality;
		if (min == 0) {
			if (isUnbounded) {
				// zero and unbounded attributes, just match all focus concepts using the focusConcept IDs
				return Promise.fail(new MatchAll());
			} else {
				// zero bounded attributes should eval to BOOL(MUST(focus) MUST_NOT(max+1))
				propertyCardinality = Range.closed(max + 1, Long.MAX_VALUE);
			}
		} else {
			// use cardinality range specified in the syntax
			propertyCardinality = Range.closed(min, max);
		}
		final Function<Property, Object> idProvider = refinement.isReversed() ? Property::getValue : Property::getObjectId;
		final Promise<Set<String>> focusConceptIds = focusConcepts.isAnyExpression() ? Promise.immediate(Collections.emptySet()) : focusConcepts.resolve(context);
		return focusConceptIds
				.thenWith(new Function<Set<String>, Promise<Collection<Property>>>() {
					@Override
					public Promise<Collection<Property>> apply(Set<String> focusConceptIds) {
						// if resolved IDs are empty in case of non-* expression return empty props
						if (focusConceptIds.isEmpty() && !focusConcepts.isAnyExpression()) {
							return Promise.immediate(Collections.emptySet());
						}
						return evalRefinement(context, focusConceptIds, refinement, grouped)
								.then(filterByCardinality(grouped, groupCardinality, propertyCardinality, idProvider));
					}
				});
	}
	
	private Promise<Collection<Property>> evalRefinement(final BranchContext context, final Collection<String> focusConceptIds, final AttributeConstraint refinement, final boolean grouped) {
		final Comparison comparison = refinement.getComparison();
		final EclSerializer serializer = context.service(EclSerializer.class);
		final Collection<String> typeConceptFilter = Collections.singleton(serializer.serialize(refinement.getAttribute()));
		
		if (comparison instanceof AttributeComparison) {
			// resolve non-* focusConcept ECLs to IDs, so we can filter relationships by source/destination
			// filterByType and filterByDestination accepts ECL expressions as well, so serialize them into ECL and pass as String when required
			// if reversed refinement, then we are interested in the destinationIds otherwise we need the sourceIds
			final Collection<String> destinationConceptFilter = Collections.singleton(serializer.serialize(rewrite(comparison)));
			final Collection<String> focusConceptFilter = refinement.isReversed() ? destinationConceptFilter : focusConceptIds;
			final Collection<String> valueConceptFilter = refinement.isReversed() ? focusConceptIds : destinationConceptFilter;
			return evalRelationships(context, focusConceptFilter, typeConceptFilter, valueConceptFilter, grouped);
		} else if (comparison instanceof DataTypeComparison) {
			if (grouped) {
				throw new BadRequestException("Group refinement is not supported in data type based comparison (string/numeric)");
			} else if (refinement.isReversed()) {
				throw new BadRequestException("Reversed flag is not supported in data type based comparison (string/numeric)");
			} else {
				return evalMembers(context, focusConceptIds, typeConceptFilter, comparison);
			}
		} else {
			return SnomedEclEvaluationRequest.throwUnsupported(comparison);
		}
	}
		
	private Promise<Collection<Property>> evalMembers(BranchContext context, final Collection<String> focusConceptIds, Collection<String> attributeNames, Comparison comparison) {
		final Object value;
		final DataType type;
		final RevisionSearchRequest.Operator operator;
		if (comparison instanceof StringValueEquals) {
			value = ((StringValueEquals) comparison).getValue();
			type = DataType.STRING;
			operator = RevisionSearchRequest.Operator.EQUALS;
		} else if (comparison instanceof StringValueNotEquals) {
			value = ((StringValueNotEquals) comparison).getValue();
			type = DataType.STRING;
			operator = RevisionSearchRequest.Operator.NOT_EQUALS;
		} else {
			return SnomedEclEvaluationRequest.throwUnsupported(comparison);
		}
		return evalMembers(context, focusConceptIds, attributeNames, type, value, operator)
				.then(new Function<SnomedReferenceSetMembers, Collection<Property>>() {
					@Override
					public Collection<Property> apply(SnomedReferenceSetMembers matchingMembers) {
						return FluentIterable.from(matchingMembers).transform(new Function<SnomedReferenceSetMember, Property>() {
							@Override
							public Property apply(SnomedReferenceSetMember input) {
								return new Property(input.getId(), 
										input.getReferencedComponent().getId(), 
										(String) input.getProperties().get(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME),
										// TODO deserialize value???
										(String) input.getProperties().get(SnomedRf2Headers.FIELD_VALUE), 
										0 /*groups are not supported, all members considered ungrouped*/);
							}
						}).toSet();
					}
				});
	}

	private Promise<SnomedReferenceSetMembers> evalMembers(final BranchContext context, final Collection<String> referencedComponents, 
			final Collection<String> attributeNames, final DataType type, final Object value, RevisionSearchRequest.Operator operator) {
		final Options propFilter = Options.builder()
				.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, ALLOWED_CHARACTERISTIC_TYPES)
				.put(SnomedRf2Headers.FIELD_ATTRIBUTE_NAME, attributeNames)
				.put(SnomedRefSetMemberIndexEntry.Fields.DATA_TYPE, type)
				.put(SnomedRf2Headers.FIELD_VALUE, value)
				.put(RevisionSearchRequest.operator(SnomedRf2Headers.FIELD_VALUE), operator)
				.build();
		return SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			.filterByReferencedComponent(referencedComponents)
			.filterByRefSetType(Collections.singleton(SnomedRefSetType.CONCRETE_DATA_TYPE))
			.filterByProps(propFilter)
			.build(context.id(), context.branch().path())
			.execute(context.service(IEventBus.class));
	}

	/*package*/ static Function<Collection<Property>, Collection<Property>> filterByCardinality(final boolean grouped, final Range<Long> groupCardinality, final Range<Long> cardinality, final Function<Property, Object> idProvider) {
		return new Function<Collection<Property>, Collection<Property>>() {
			@Override
			public Collection<Property> apply(Collection<Property> matchingProperties) {
				final Multimap<Object, Property> propertiesByMatchingIds = Multimaps.index(matchingProperties, idProvider);
				final Collection<Property> properties = newHashSet();
				
				final Range<Long> allowedRelationshipCardinality;
				if (grouped) {
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
				
				for (Object matchingConceptId : propertiesByMatchingIds.keySet()) {
					final Collection<Property> propertiesOfConcept = propertiesByMatchingIds.get(matchingConceptId);
					if (allowedRelationshipCardinality.contains((long) propertiesOfConcept.size())) {
						if (grouped) {
							final Multimap<Integer, Property> indexedByGroup = FluentIterable.from(propertiesOfConcept).index(Property::getGroup);
							// if groups should be considered as well, then check group numbers in the matching sets
							// check that the concept has at least the right amount of groups
							final Multimap<Integer, Property> validGroups = ArrayListMultimap.create();
							
							for (Integer group : indexedByGroup.keySet()) {
								final Collection<Property> groupedRelationships = indexedByGroup.get(group);
								if (cardinality.contains((long) groupedRelationships.size())) {
									validGroups.putAll(group, groupedRelationships);
								}
							}
							
							if (groupCardinality.contains((long) validGroups.size())) {
								properties.addAll(validGroups.values());
							}
						} else {
							properties.addAll(propertiesOfConcept);
						}
					}
				}
				return properties;
			}
		};
	}

	/*package*/ static Promise<Collection<Property>> evalRelationships(final BranchContext context, 
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
				.filterByCharacteristicTypes(ALLOWED_CHARACTERISTIC_TYPES)
				.setFields(fieldsToLoad.build());
		
		// if a grouping refinement, then filter relationships with group number gte 1
		if (groupedRelationshipsOnly) {
			req.filterByGroup(1, Integer.MAX_VALUE);
		}
		
		return req
				.build(context.id(), context.branch().path())
				.execute(context.service(IEventBus.class))
				.then(new Function<SnomedRelationships, Collection<Property>>() {
					@Override
					public Collection<Property> apply(SnomedRelationships input) {
						return FluentIterable.from(input).transform(new Function<ISnomedRelationship, Property>() {
							@Override
							public Property apply(ISnomedRelationship input) {
								return new Property(input.getId(), input.getSourceId(), input.getTypeId(), input.getDestinationId(), input.getGroup());
							}
						}).toSet();
					}
				});
	}
	
	private ExpressionConstraint rewrite(Comparison comparison) {
		if (comparison instanceof AttributeValueEquals) {
			return ((AttributeValueEquals) comparison).getConstraint();
		} else if (comparison instanceof AttributeValueNotEquals) {
			// convert != expression to exclusion constraint
			final ExclusionExpressionConstraint exclusion = ECL_FACTORY.createExclusionExpressionConstraint();
			// set Any as left of exclusion
			exclusion.setLeft(ECL_FACTORY.createAny());
			// set original constraint as right of exclusion
			exclusion.setRight(((AttributeValueNotEquals) comparison).getConstraint());
			return exclusion;
		}
		throw new UnsupportedOperationException("Cannot rewrite comparison: " + comparison);
	}
	
	// Helper Throwable class to quickly return from attribute constraint evaluation when all matches are valid
	private static final class MatchAll extends Throwable {}
	
	/*Property data class which can represent both relationships and concrete domain members with all relevant properties required for ECL refinement evaluation*/
	static final class Property {
		
		private final String id;
		private final String objectId;
		private String typeId;
		private Object value;
		private Integer group;
		
		public Property(final String objectId) {
			this.id = objectId;
			this.objectId = objectId;
		}
		
		public Property(final String id, final String objectId, final String typeId, final Object value, final Integer group) {
			this.id = id;
			this.objectId = objectId;
			this.typeId = typeId;
			this.value = value;
			this.group = group;
		}
		
		public String getId() {
			return id;
		}
		
		public Integer getGroup() {
			return group;
		}
		
		public String getObjectId() {
			return objectId;
		}
		
		public String getTypeId() {
			return typeId;
		}
		
		public Object getValue() {
			return value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Property other = (Property) obj;
			return Objects.equals(id, other.id);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(id);
		}
		
	}
	
}
