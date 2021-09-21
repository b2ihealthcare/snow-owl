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
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument.Expressions.active;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.*;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Fields.*;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.options.Options;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snomed.ecl.ecl.*;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.*;

/**
 * Handles refined expression constraint evaluation.
 * @since 5.4
 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.2+Refinements
 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.4+Conjunction+and+Disjunction
 */
final class SnomedEclRefinementEvaluator {

	private static final int SCROLL_LIMIT = 10_000;
	
	static final Set<String> STATED_CHARACTERISTIC_TYPES = ImmutableSet.of(Concepts.STATED_RELATIONSHIP, Concepts.ADDITIONAL_RELATIONSHIP);
	static final Set<String> INFERRED_CHARACTERISTIC_TYPES = ImmutableSet.of(Concepts.INFERRED_RELATIONSHIP, Concepts.ADDITIONAL_RELATIONSHIP);
	private static final int UNBOUNDED_CARDINALITY = -1;
	private static final Range<Long> ANY_GROUP = Range.closed(0L, Long.MAX_VALUE);
	
	private final PolymorphicDispatcher<Promise<Expression>> refinementDispatcher = PolymorphicDispatcher.createForSingleTarget("eval", 2, 2, this);
	private final PolymorphicDispatcher<Promise<Collection<Property>>> groupRefinementDispatcher = PolymorphicDispatcher.createForSingleTarget("evalGroup", 3, 3, this);
	
	private final EclExpression focusConcepts;
	private final String expressionForm;
	
	public SnomedEclRefinementEvaluator(EclExpression focusConcepts) {
		this.focusConcepts = focusConcepts;
		this.expressionForm = focusConcepts.getExpressionForm();
	}
	
	public Promise<Expression> evaluate(BranchContext context, EclRefinement refinement) {
		return refinementDispatcher.invoke(context, refinement);
	}
	
	protected Promise<Expression> eval(BranchContext context, EclRefinement refinement) {
		return SnomedEclEvaluationRequest.throwUnsupported(refinement); 
	}
	
	/**
	 * Handles eclAttribute part of refined expression constraints.
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.3+Cardinality
	 */
	protected Promise<Expression> eval(final BranchContext context, final AttributeConstraint refinement) {
		return evalRefinement(context, refinement, false, ANY_GROUP)
				.thenWith(input -> {
					final Function<Property, Object> idProvider = refinement.isReversed() ? Property::getValue : Property::getObjectId;
					final Set<String> matchingIds = FluentIterable.from(input).transform(idProvider).filter(String.class).toSet();
					// two cases here, one is the [1..x] the other is [0..x]
					final Cardinality cardinality = refinement.getCardinality();
					if (cardinality != null && cardinality.getMin() == 0 && cardinality.getMax() != UNBOUNDED_CARDINALITY) {
						// XXX internal evaluation returns negative matches, that should be excluded from the focusConcept set
						return focusConcepts.resolveToExclusionExpression(context, matchingIds);
					} else {
						return focusConcepts.resolveToAndExpression(context, matchingIds);
					}
				})
				.failWith(throwable -> {
					if (throwable instanceof MatchAll) {
						return focusConcepts.resolveToExpression(context);
					}
					if (throwable instanceof RuntimeException) {
						throw (RuntimeException) throwable;
					} else {
						throw new SnowowlRuntimeException(throwable);
					}
				});
	}
	
	/**
	 * Handles conjunctions in refinement part of refined expression constraints.
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.4+Conjunction+and+Disjunction
	 */
	protected Promise<Expression> eval(final BranchContext context, AndRefinement and) {
		return Promise.all(evaluate(context, and.getLeft()), evaluate(context, and.getRight()))
				.then(input -> {
					final Expression left = (Expression) input.get(0);
					final Expression right = (Expression) input.get(1);
					return Expressions.builder().filter(left).filter(right).build();
				});
	}
	
	/**
	 * Handles disjunctions in refinement part of refined expression constraints.
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.4+Conjunction+and+Disjunction
	 */
	protected Promise<Expression> eval(final BranchContext context, OrRefinement or) {
		return Promise.all(evaluate(context, or.getLeft()), evaluate(context, or.getRight()))
				.then(input -> {
					final Expression left = (Expression) input.get(0);
					final Expression right = (Expression) input.get(1);
					return Expressions.builder().should(left).should(right).build();
				});
	}
	
	/**
	 * Handles nested refinements by delegating the evaluation to the nested refinement constraint.
	 */
	protected Promise<Expression> eval(final BranchContext context, NestedRefinement nested) {
		return evaluate(context, nested.getNested());
	}

	/**
	 * Handles evaluation of attribute refinements with groups
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.2+Refinements
	 */
	protected Promise<Expression> eval(final BranchContext context, EclAttributeGroup group) {
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
						.thenWith(input -> {
							final Set<String> excludedMatches = FluentIterable.from(input).transform(Property::getObjectId).toSet();
							return focusConcepts.resolveToExclusionExpression(context, excludedMatches);
						});
			}
		} else {
			return evaluateGroup(context, groupCardinality, group.getRefinement())
					.thenWith(input -> {
						final Set<String> matchingIds = FluentIterable.from(input).transform(Property::getObjectId).toSet();
						return focusConcepts.resolveToAndExpression(context, matchingIds);
					});
		}
	}
	
	/**
	 * Evaluates refinement parts inside attribute group based refinements.
	 */
	protected Promise<Collection<Property>> evaluateGroup(BranchContext context, Range<Long> groupCardinality, EclRefinement refinement) {
		return groupRefinementDispatcher.invoke(context, groupCardinality, refinement);
	}
	
	protected Promise<Collection<Property>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final EclRefinement refinement) {
		return SnomedEclEvaluationRequest.throwUnsupported(refinement);
	}

	/**
	 * Handles attribute refinements inside attribute group refinements.
	 */
	protected Promise<Collection<Property>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final AttributeConstraint refinement) {
		if (refinement.isReversed()) {
			throw new BadRequestException("Reversed attributes are not supported in group refinements");
		} else {
			return evalRefinement(context, refinement, true, groupCardinality)
					.thenWith(input -> {
						final Cardinality cardinality = refinement.getCardinality();
						// two cases here, one is the [1..x] the other is [0..x]
						if (cardinality != null && cardinality.getMin() == 0 && cardinality.getMax() != UNBOUNDED_CARDINALITY) {
							// XXX internal evaluation returns negative matches, that should be excluded from the focusConcept set
							final Function<Property, Object> idProvider = refinement.isReversed() ? Property::getValue : Property::getObjectId;
						
							
							final Set<String> matchingIds = FluentIterable.from(input).transform(idProvider).filter(String.class).toSet();
							return focusConcepts.resolveToConceptsWithGroups(context)
									.then(groupsById -> {
										final Collection<Property> matchingProperties = Sets.newHashSetWithExpectedSize(groupsById.size() - matchingIds.size());
										for (Entry<String, Integer> entry : groupsById.entries()) {
											final String id = entry.getKey();
											if (!matchingIds.contains(id)) {
												matchingProperties.add(new Property(id, entry.getValue()));
											}
										}
										return matchingProperties;
									});
						} else {
							return Promise.immediate(input);
						}
					})
					.failWith(throwable -> {
						if (throwable instanceof MatchAll) {
							return focusConcepts.resolveToConceptsWithGroups(context)
									.then(groupsById -> {
										final Collection<Property> matchingProperties = Sets.newHashSetWithExpectedSize(groupsById.size());
										for (Entry<String, Integer> entry : groupsById.entries()) {
											matchingProperties.add(new Property(entry.getKey(), entry.getValue()));
										}
										return matchingProperties;
									});
						}
						throw new SnowowlRuntimeException(throwable);
					});
		}
	}
	
	/**
	 * Handles conjunction inside attribute group based refinements.
	 */
	protected Promise<Collection<Property>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final AndRefinement and) {
		return Promise.all(evaluateGroup(context, groupCardinality, and.getLeft()), evaluateGroup(context, groupCardinality, and.getRight()))
				.then(evalParts(groupCardinality, Sets::intersection));
	}
	
	/**
	 * Handles disjunction inside attribute group based refinements.
	 */
	protected Promise<Collection<Property>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final OrRefinement or) {
		return Promise.all(evaluateGroup(context, groupCardinality, or.getLeft()), evaluateGroup(context, groupCardinality, or.getRight()))
				.then(evalParts(groupCardinality, Sets::union));
	}
	
	/**
	 * Handles nested refinements inside attribute group based refinements.
	 */
	protected Promise<Collection<Property>> evalGroup(final BranchContext context, final Range<Long> groupCardinality, final NestedRefinement nested) {
		return evaluateGroup(context, groupCardinality, nested.getNested());
	}
	
	/**
	 * Evaluates partial results coming from a binary operator's left and right side within attribute group based refinements.
	 * @param groupCardinality - the cardinality to check
	 * @param groupOperator - the operator to use (AND or OR, aka {@link Sets#intersection(Set, Set)} or {@link Sets#union(Set, Set)})
	 * @return a function that will can be chained via {@link Promise#then(Function)} to evaluate partial results when they are available
	 */
	private Function<List<Object>, Collection<Property>> evalParts(final Range<Long> groupCardinality, BinaryOperator<Set<Integer>> groupOperator) {
		return input -> {
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
		};
	}

	/**
	 * Evaluates attribute refinements. 
	 * @param context - the branch where the evaluation should happen
	 * @param refinement - the refinement itself
	 * @param grouped - whether the refinement should consider groups
	 * @param groupCardinality - the cardinality to use when grouped parameter is <code>true</code>
	 * @return a {@link Collection} of {@link Property} objects that match the parameters
	 */
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
			// use cardinality range specified in the expression
			propertyCardinality = Range.closed(min, max);
		}
		final Function<Property, Object> idProvider = refinement.isReversed() ? Property::getValue : Property::getObjectId;
		final Promise<Set<String>> focusConceptIdsPromise = focusConcepts.isAnyExpression() 
				? Promise.immediate(null) // XXX send null collection to skip filtering by source concepts/referenced components
				: grouped ? focusConcepts.resolveToConceptsWithGroups(context).then(Multimap::keySet) : focusConcepts.resolve(context);
		return focusConceptIdsPromise
				.thenWith(focusConceptIds -> evalRefinement(context, refinement, grouped, focusConceptIds))
				.then(filterByCardinality(grouped, groupCardinality, propertyCardinality, idProvider));
	}
	
	/**
	 * Evaluates an {@link AttributeConstraint} refinement on the given focusConceptId set on the given {@link BranchContext}.
	 * Grouped parameter can  
	 */
	private Promise<Collection<Property>> evalRefinement(final BranchContext context, final AttributeConstraint refinement, final boolean grouped, final Set<String> focusConceptIds) {
		final Comparison comparison = refinement.getComparison();
		final Collection<String> typeConceptFilter = evalToConceptIds(context, refinement.getAttribute(), expressionForm).getSync(1, TimeUnit.MINUTES);
		
		if (comparison instanceof AttributeComparison) {
			// resolve non-* focusConcept ECLs to IDs, so we can filter relationships by source/destination
			// filterByType and filterByDestination accepts ECL expressions as well, so serialize them into ECL and pass as String when required
			// if reversed refinement, then we are interested in the destinationIds otherwise we need the sourceIds
			final Collection<String> destinationConceptFilter = evalToConceptIds(context, ((AttributeComparison) comparison).getValue(), expressionForm).getSync(1, TimeUnit.MINUTES);
			final Collection<String> focusConceptFilter = refinement.isReversed() ? destinationConceptFilter : focusConceptIds;
			final Collection<String> valueConceptFilter = refinement.isReversed() ? focusConceptIds : destinationConceptFilter;
			return evalStatements(context, focusConceptFilter, typeConceptFilter, valueConceptFilter, grouped, expressionForm);
		} else if (comparison instanceof DataTypeComparison) {
			if (refinement.isReversed()) {
				throw new BadRequestException("Reversed flag is not supported in data type based comparison (string/numeric)");
			} else {
				final Promise<Collection<Property>> statementsWithValue = evalStatementsWithValue(context, focusConceptIds, typeConceptFilter, (DataTypeComparison) comparison);
				final Promise<Collection<Property>> members = evalMembers(context, focusConceptIds, typeConceptFilter, (DataTypeComparison) comparison);
				return Promise.all(statementsWithValue, members).then(results -> {
					final Collection<Property> s = (Collection<Property>) results.get(0);
					final Collection<Property> m = (Collection<Property>) results.get(1);
					return FluentIterable.concat(s, m).toSet();
				});
			}
		} else {
			return SnomedEclEvaluationRequest.throwUnsupported(comparison);
		}
	}
		
	private Promise<Collection<Property>> evalMembers(BranchContext context, Set<String> focusConceptIds, Collection<String> typeIds, DataTypeComparison comparison) {
		final Object value;
		final DataType type;
		if (comparison instanceof BooleanValueComparison) {
			value = ((BooleanValueComparison) comparison).isValue();
			type = DataType.BOOLEAN;
		} else if (comparison instanceof StringValueComparison) {
			value = ((StringValueComparison) comparison).getValue();
			type = DataType.STRING;
		} else if (comparison instanceof IntegerValueComparison) {
			value = ((IntegerValueComparison) comparison).getValue();
			type = DataType.INTEGER;
		} else if (comparison instanceof DecimalValueComparison) {
			value = ((DecimalValueComparison) comparison).getValue();
			type = DataType.DECIMAL;
		} else {
			return SnomedEclEvaluationRequest.throwUnsupported(comparison);
		}
		
		final SearchResourceRequest.Operator operator = toSearchOperator(comparison.getOp());
		return evalMembers(context, focusConceptIds, typeIds, type, value, operator)
				.then(matchingMembers -> FluentIterable.from(matchingMembers)
					.transform(input -> new Property(
							input.getReferencedComponent().getId(), 
							(String) input.getProperties().get(SnomedRf2Headers.FIELD_TYPE_ID),
							input.getProperties().get(SnomedRf2Headers.FIELD_VALUE), 
							(Integer) input.getProperties().get(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP)))
					.toSet()
				);
	}

	private Promise<SnomedReferenceSetMembers> evalMembers(
			final BranchContext context, 
			final Set<String> focusConceptIds,
			final Collection<String> typeIds, 
			final DataType type, 
			final Object value, 
			SearchResourceRequest.Operator operator) {
		
		final Options propFilter = Options.builder()
				.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, getCharacteristicTypes(expressionForm))
				.put(SnomedRf2Headers.FIELD_TYPE_ID, typeIds)
				.put(SnomedRefSetMemberIndexEntry.Fields.DATA_TYPE, type)
				.put(SnomedRf2Headers.FIELD_VALUE, value)
				.put(SearchResourceRequest.operator(SnomedRf2Headers.FIELD_VALUE), operator)
				.build();

		// TODO: does this request need to support filtering by group?
		
		return SnomedRequests.prepareSearchMember()
				.all()
				.filterByActive(true)
				.filterByRefSetType(SnomedRefSetType.CONCRETE_DATA_TYPE)
				.filterByReferencedComponent(focusConceptIds)
				.filterByProps(propFilter)
				.setEclExpressionForm(expressionForm)
				.build(context.path())
				.execute(context.service(IEventBus.class));
	}

	private Promise<Collection<Property>> evalStatementsWithValue(
		BranchContext context, 
		Set<String> focusConceptIds, 
		Collection<String> typeIds, 
		DataTypeComparison comparison) {
		
		final RelationshipValue value;
		final SearchResourceRequest.Operator operator = toSearchOperator(comparison.getOp());

		// XXX: no boolean comparison for relationships with value!
		if (comparison instanceof BooleanValueComparison) {
			return Promise.immediate(List.of());
		} else if (comparison instanceof StringValueComparison) {
			value = new RelationshipValue(((StringValueComparison) comparison).getValue());
		} else if (comparison instanceof IntegerValueComparison) {
			value = new RelationshipValue(((IntegerValueComparison) comparison).getValue());
		} else if (comparison instanceof DecimalValueComparison) {
			value = new RelationshipValue(((DecimalValueComparison) comparison).getValue().doubleValue());
		} else {
			return SnomedEclEvaluationRequest.throwUnsupported(comparison);
		}
		
		return evalStatementsWithValue(context, focusConceptIds, typeIds, value, operator);
	}

	private Promise<Collection<Property>> evalStatementsWithValue(
		final BranchContext context, 
		final Set<String> focusConceptIds,
		final Collection<String> typeIds, 
		final RelationshipValue value, 
		final SearchResourceRequest.Operator operator) {

		// TODO: does this request need to support filtering by group?
		Promise<Collection<Property>> relationships = SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true)
				.filterByCharacteristicTypes(getCharacteristicTypes(expressionForm))
				.filterBySources(focusConceptIds)
				.filterByTypes(typeIds)
				.filterByValueType(value.type()) 
				.filterByValue(operator, value)
				.setEclExpressionForm(expressionForm)
				.setFields(ID, SOURCE_ID, TYPE_ID, RELATIONSHIP_GROUP, VALUE_TYPE, INTEGER_VALUE, DECIMAL_VALUE, STRING_VALUE)
				.build(context.path())
				.execute(context.service(IEventBus.class))
				.then(matchingMembers -> FluentIterable.from(matchingMembers)
						.transform(input -> new Property(
								input.getSourceId(), 
								input.getTypeId(),
								input.getValueAsObject().toObject(),
								input.getRelationshipGroup()))
						.toSet());
		
		if (Trees.STATED_FORM.equals(expressionForm)) {
			final Promise<Collection<Property>> axioms = evalAxiomsWithValue(context, focusConceptIds, typeIds, value, operator);
			return Promise.all(relationships, axioms).then(results -> {
				final Collection<Property> r = (Collection<Property>) results.get(0);
				final Collection<Property> a = (Collection<Property>) results.get(1);
				return FluentIterable.concat(r, a).toSet();
			});
		} else {
			return relationships;
		}
	}

	private Promise<Collection<Property>> evalAxiomsWithValue(
			BranchContext context, 
			Set<String> focusConceptIds,
			Collection<String> typeIds, 
			RelationshipValue value, 
			SearchResourceRequest.Operator operator) {
		
		// search existing axioms defined for the given set of conceptIds
		ExpressionBuilder axiomFilter = Expressions.builder();

		if (typeIds != null) {
			axiomFilter.filter(typeIds(typeIds));
		}
		
		switch (operator) {
			case EQUALS: axiomFilter.filter(values(List.of(value))); break;
			case GREATER_THAN: axiomFilter.filter(valueGreaterThan(value, false)); break;
			case GREATER_THAN_EQUALS: axiomFilter.filter(valueGreaterThan(value, true)); break;
			case LESS_THAN: axiomFilter.filter(valueLessThan(value, false)); break;
			case LESS_THAN_EQUALS: axiomFilter.filter(valueLessThan(value, true)); break;
			case NOT_EQUALS: axiomFilter.mustNot(values(List.of(value))); break;
			default: throw new IllegalStateException("Unexpected operator '" + operator +  "'.");
		}
		
		ExpressionBuilder activeOwlAxiomMemberQuery = Expressions.builder()
			.filter(active())
			.filter(Expressions.nestedMatch(SnomedRefSetMemberIndexEntry.Fields.CLASS_AXIOM_RELATIONSHIP, axiomFilter.build()));
		
		if (focusConceptIds != null) {
			activeOwlAxiomMemberQuery.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(focusConceptIds));
		}
		
		final Query<SnomedRefSetMemberIndexEntry> activeAxiomStatementsQuery = Query.select(SnomedRefSetMemberIndexEntry.class)
			.where(activeOwlAxiomMemberQuery.build())
			.limit(SCROLL_LIMIT)
			.build();
		
		final Set<Property> axiomProperties = newHashSet();
		
		context.service(RevisionSearcher.class)
			.stream(activeAxiomStatementsQuery)
			.forEach(chunk -> {
				chunk.stream()
					.filter(owlMember -> !CompareUtils.isEmpty(owlMember.getClassAxiomRelationships()))
					.forEachOrdered(owlMember -> {
						owlMember.getClassAxiomRelationships().stream()
							.filter(r -> typeIds == null || typeIds.contains(r.getTypeId()))
							// We need to find matching OWL relationships in Java
							.filter(r -> r.getValueAsObject().matches(operator, value))
							.map(r -> new Property(
								owlMember.getReferencedComponentId(), 
								r.getTypeId(), 
								r.getValueAsObject().toObject(), 
								r.getRelationshipGroup()))
							.forEachOrdered(axiomProperties::add);
					});
			});
		
		return Promise.immediate(axiomProperties);
	}

	private SearchResourceRequest.Operator toSearchOperator(final String op) {
		final Operator eclOperator = Operator.fromString(op);
		switch (eclOperator) {
			case EQUALS:
				return SearchResourceRequest.Operator.EQUALS;
			case GT:
				return SearchResourceRequest.Operator.GREATER_THAN;
			case GTE:
				return SearchResourceRequest.Operator.GREATER_THAN_EQUALS;
			case LT:
				return SearchResourceRequest.Operator.LESS_THAN;
			case LTE:
				return SearchResourceRequest.Operator.LESS_THAN_EQUALS;
			case NOT_EQUALS:
				return SearchResourceRequest.Operator.NOT_EQUALS;
			default:
				throw new IllegalStateException("Unhandled ECL search operator '" + eclOperator + "'.");
		}
	}

	/*package*/ static Function<Collection<Property>, Collection<Property>> filterByCardinality(final boolean grouped, final Range<Long> groupCardinality, final Range<Long> cardinality, final Function<Property, Object> idProvider) {
		return matchingProperties -> {
			final Multimap<Object, Property> propertiesByMatchingIds = Multimaps.index(matchingProperties, idProvider);
			final Collection<Property> properties = newHashSet();
			
			final Range<Long> allowedRelationshipCardinality;
			if (grouped) {
				final long minRelationships = groupCardinality.lowerEndpoint() == 0 ? cardinality.lowerEndpoint() : groupCardinality.lowerEndpoint() * cardinality.lowerEndpoint();  
				final long maxRelationships;
				if (groupCardinality.hasUpperBound() && cardinality.hasUpperBound()) {
					if (groupCardinality.upperEndpoint() == Long.MAX_VALUE || cardinality.upperEndpoint() == Long.MAX_VALUE) {
						maxRelationships = Long.MAX_VALUE;
					} else {
						maxRelationships = groupCardinality.upperEndpoint() * cardinality.upperEndpoint();
					}
				} else {
					// group and relationship cardinalities are unbounded
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
						
						if (groupCardinality.contains((long) validGroups.keySet().size())) {
							properties.addAll(validGroups.values());
						}
					} else {
						properties.addAll(propertiesOfConcept);
					}
				}
			}
			return properties;
		};
	}

	/**
	 * Executes a SNOMED CT Relationship search request using the given source, type, destination filters.
	 * If the groupedRelationshipsOnly boolean flag is <code>true</code>, then the search will match relationships that are grouped (their groupId is greater than or equals to <code>1</code>).
	 * @param context - the context where the search should happen
	 * @param sourceFilter - filter for relationship sources
	 * @param typeFilter - filter for relationship types
	 * @param destinationFilter - filter for relationship destinations
	 * @param groupedRelationshipsOnly - whether the search should consider grouped relationships only or not
	 * @return a {@link Promise} of {@link Collection} of {@link Property} objects that match the criteria
	 * @see SnomedRelationshipSearchRequestBuilder
	 */
	/*package*/ static Promise<Collection<Property>> evalStatements(final BranchContext context, 
			final Collection<String> sourceFilter, 
			final Collection<String> typeFilter,
			final Collection<String> destinationFilter,
			final boolean groupedRelationshipsOnly,
			final String expressionForm) {

		final ImmutableList.Builder<String> fieldsToLoad = ImmutableList.builder();
		fieldsToLoad.add(SnomedDocument.Fields.ID, SOURCE_ID, TYPE_ID, DESTINATION_ID);
		if (groupedRelationshipsOnly) {
			fieldsToLoad.add(RELATIONSHIP_GROUP);
		}
		
		final SnomedRelationshipSearchRequestBuilder searchRelationships = SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true) 
				.filterBySources(sourceFilter)
				.filterByTypes(typeFilter)
				.filterByDestinations(destinationFilter)
				.filterByCharacteristicTypes(getCharacteristicTypes(expressionForm))
				.setEclExpressionForm(expressionForm)
				.setFields(fieldsToLoad.build());
		
		// if a grouping refinement, then filter relationships with group >= 1
		if (groupedRelationshipsOnly) {
			searchRelationships.filterByGroup(1, Integer.MAX_VALUE);
		}
		
		Promise<Collection<Property>> relationshipSearch = searchRelationships.build(context.path())
			.execute(context.service(IEventBus.class))
			.then(input -> input.stream().map(r -> new Property(r.getSourceId(), r.getTypeId(), r.getDestinationId(), r.getRelationshipGroup())).collect(Collectors.toSet()));
		
		if (Trees.STATED_FORM.equals(expressionForm)) {
			final Set<Property> axiomStatements = evalAxiomStatements(context, groupedRelationshipsOnly, sourceFilter, typeFilter, destinationFilter);
			return relationshipSearch.then(relationshipStatements -> ImmutableSet.<Property>builder().addAll(relationshipStatements).addAll(axiomStatements).build());
		} else {
			return relationshipSearch;
		}
		
	}

	static Promise<Set<String>> evalToConceptIds(final BranchContext context, final ExpressionConstraint expressionConstraint, final String expressionForm) {
		if (expressionConstraint instanceof EclConceptReference) {
			return Promise.immediate(Set.of(((EclConceptReference) expressionConstraint).getId()));
		} else if (expressionConstraint instanceof Any) {
			return Promise.immediate(null);
		} else {
			return EclExpression.of(expressionConstraint, expressionForm).resolve(context);
		}
	}

	static Set<Property> evalAxiomStatements(final BranchContext context, final boolean groupedRelationshipsOnly, final Collection<String> sourceIds, final Collection<String> typeIds, final Collection<String> destinationIds) {
		try {
			// search existing axioms (no values!) defined for the given set of conceptIds
			ExpressionBuilder axiomFilter = Expressions.builder()
				.filter(hasDestinationId());

			if (typeIds != null) {
				axiomFilter.filter(typeIds(typeIds));
			}
			
			if (destinationIds != null) {
				axiomFilter.filter(destinationIds(destinationIds));
			}
			
			if (groupedRelationshipsOnly) {
				axiomFilter.filter(relationshipGroup(1, Integer.MAX_VALUE));
			}
			
			ExpressionBuilder activeOwlAxiomMemberQuery = Expressions.builder()
					.filter(active())
					.filter(Expressions.nestedMatch(SnomedRefSetMemberIndexEntry.Fields.CLASS_AXIOM_RELATIONSHIP, axiomFilter.build()));
			
			if (sourceIds != null) {
				activeOwlAxiomMemberQuery.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(sourceIds));
			}
			
			final Query<SnomedRefSetMemberIndexEntry> activeAxiomStatementsQuery = Query.select(SnomedRefSetMemberIndexEntry.class)
					.where(activeOwlAxiomMemberQuery.build())
					.limit(Integer.MAX_VALUE)
					.build();
			return context.service(RevisionSearcher.class).search(activeAxiomStatementsQuery)
				.stream()
				.filter(owlMember -> !CompareUtils.isEmpty(owlMember.getClassAxiomRelationships()))
				.flatMap(owlMember -> {
					return owlMember.getClassAxiomRelationships().stream()
							.filter(classAxiom -> {
								return (typeIds == null || typeIds.contains(classAxiom.getTypeId())) 
										&& (destinationIds == null || destinationIds.contains(classAxiom.getDestinationId()))
										&& (!groupedRelationshipsOnly || classAxiom.getRelationshipGroup() >= 1);
							})
							.map(classAxiom -> {
								return new Property(owlMember.getReferencedComponentId(), classAxiom.getTypeId(), classAxiom.getDestinationId(), classAxiom.getRelationshipGroup());
							});
				})
				.collect(Collectors.toSet());
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}
	
	static Set<String> getCharacteristicTypes(String expressionForm) {
		return Trees.INFERRED_FORM.equals(expressionForm) ? INFERRED_CHARACTERISTIC_TYPES : STATED_CHARACTERISTIC_TYPES;
	}
	
	// Helper Throwable class to quickly return from attribute constraint evaluation when all matches are valid
	private static final class MatchAll extends Throwable {}
	
	/*Property data class which can represent both relationships and concrete domain members with all relevant properties required for ECL refinement evaluation*/
	static final class Property {
		
		private final String objectId;
		private String typeId;
		private Object value;
		private Integer group;
		
		public Property(final String objectId, final Integer group) {
			this.objectId = objectId;
			this.group = group;
		}
		
		public Property(final String objectId, final String typeId, final Object value, final Integer group) {
			this.objectId = objectId;
			this.typeId = typeId;
			this.value = value;
			this.group = group;
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
			return Objects.equals(objectId, other.objectId)
					&& Objects.equals(typeId, other.typeId)
					&& Objects.equals(value, other.value)
					&& Objects.equals(group, other.group);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(objectId, typeId, value, group);
		}
		
		@Override
		public String toString() {
			return "Property [objectId=" + objectId + ", typeId=" + typeId + ", value=" + value + ", group=" + group + "]";
		}
	}
}
