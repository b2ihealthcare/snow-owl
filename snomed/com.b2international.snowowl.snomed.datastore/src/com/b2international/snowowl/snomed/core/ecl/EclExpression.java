/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.b2international.commons.options.Options;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.Ecl;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 5.4
 */
public final class EclExpression {

	private final String ecl;
	private final String expressionForm;
	
	private Promise<Set<String>> promise;
	private Promise<Expression> expressionPromise;
	private Promise<SnomedConcepts> conceptPromise;
	private Promise<Multimap<String, Integer>> conceptsWithGroups;

	private EclExpression(String ecl, String expressionForm) {
		this.ecl = ecl.trim();
		this.expressionForm = expressionForm;
		Preconditions.checkArgument(isInferred() || isStated(), "Allowed expression forms are 'inferred', 'stated' but was '%s'", expressionForm);
	}
	
	public String getEcl() {
		return ecl;
	}
	
	public String getExpressionForm() {
		return expressionForm;
	}
	
	public boolean isInferred() {
		return Trees.INFERRED_FORM.equals(expressionForm);
	}

	public boolean isStated() {
		return Trees.STATED_FORM.equals(expressionForm);
	}
	
	public boolean isAnyExpression() {
		return Ecl.ANY.equals(ecl);
	}
	
	public Promise<Set<String>> resolve(final BranchContext context) {
		if (promise == null) {
			RevisionSearcher searcher = context.service(RevisionSearcher.class);
			promise = resolveToExpression(context)
				.then(expression -> {
					// shortcut to extract IDs from the query itself if possible 
					if (SnomedEclEvaluationRequest.canExtractIds(expression)) {
						return SnomedEclEvaluationRequest.extractIds(expression);
					}
					try {
						return newHashSet(searcher.search(Query.select(String.class)
								.from(SnomedConceptDocument.class)
								.fields(SnomedConceptDocument.Fields.ID)
								.where(expression)
								.limit(Integer.MAX_VALUE)
								.build()));
						
					} catch (IOException e) {
						throw new SnowowlRuntimeException(e);
					}
				});
		}
		return promise;
	}
	
	public Promise<SnomedConcepts> resolveConcepts(final BranchContext context) {
		if (conceptPromise == null) {
			conceptPromise = SnomedRequests.prepareSearchConcept()
					.all()
					.filterByEcl(ecl)
					.build(context.id(), context.path())
					.execute(context.service(IEventBus.class));
		}
		return conceptPromise;
	}

	public Promise<Expression> resolveToExpression(final BranchContext context) {
		if (expressionPromise == null) {
			expressionPromise = SnomedRequests.prepareEclEvaluation(ecl)
					.setExpressionForm(expressionForm)
					.build()
					.execute(context);
		}
		return expressionPromise;
	}
	
	public static EclExpression of(String ecl, String expressionForm) {
		return new EclExpression(ecl, expressionForm);
	}

	public Promise<Expression> resolveToExclusionExpression(final BranchContext context, final Set<String> excludedMatches) {
		return resolveToExpression(context)
				.then(it -> {
					if (!excludedMatches.isEmpty()) {
						return Expressions.builder().filter(it).mustNot(RevisionDocument.Expressions.ids(excludedMatches)).build();
					} else {
						return it;
					}
				});
	}
	
	public Promise<Multimap<String, Integer>> resolveToConceptsWithGroups(final BranchContext context) {
		if (conceptsWithGroups == null) {
			conceptsWithGroups = resolve(context)
					.thenWith(sourceIds -> resolveToGroupedOnly(context, sourceIds));
		}
		return conceptsWithGroups;
	}

	private Promise<Multimap<String, Integer>> resolveToGroupedOnly(BranchContext context, Set<String> sourceIds) {
		final Set<String> characteristicTypes = isInferred()
				? SnomedEclRefinementEvaluator.INFERRED_CHARACTERISTIC_TYPES
						: SnomedEclRefinementEvaluator.STATED_CHARACTERISTIC_TYPES;
		List<Promise<Multimap<String, Integer>>> promises = newArrayListWithCapacity(3);
		
		// search relationships
		promises.add(SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true)
				.filterByCharacteristicTypes(characteristicTypes)
				.filterBySource(sourceIds)
				.filterByGroup(1, Integer.MAX_VALUE)
				.setEclExpressionForm(expressionForm)
				.setFields(SnomedRelationshipIndexEntry.Fields.ID, SnomedRelationshipIndexEntry.Fields.SOURCE_ID, SnomedRelationshipIndexEntry.Fields.GROUP)
				.build(context.id(), context.path())
				.execute(context.service(IEventBus.class))
				.then(new Function<SnomedRelationships, Multimap<String, Integer>>() {
					@Override
					public Multimap<String, Integer> apply(SnomedRelationships input) {
						final Multimap<String, SnomedRelationship> relationshipsBySource = Multimaps.index(input, SnomedRelationship::getSourceId);
						final Multimap<String, Integer> groupsByRelationshipId = Multimaps.transformValues(relationshipsBySource, SnomedRelationship::getGroup);
						return ImmutableSetMultimap.copyOf(groupsByRelationshipId);
					}
				}));
		
		// search concrete domain members
		if (context.service(SnomedCoreConfiguration.class).isConcreteDomainSupported()) {
			final Options propFilter = Options.builder()
					.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, characteristicTypes)
					// any group that is not in zero group
					.put(SearchResourceRequest.operator(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP), SearchResourceRequest.Operator.NOT_EQUALS)
					.put(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, 0)
					.build();
			
			promises.add(
				SnomedRequests.prepareSearchMember()
					.all()
					.filterByActive(true)
					.filterByReferencedComponent(sourceIds)
					.filterByRefSetType(SnomedRefSetType.CONCRETE_DATA_TYPE)
					.filterByProps(propFilter)
					.setEclExpressionForm(expressionForm)
					.build(context.id(), context.path())
					.execute(context.service(IEventBus.class))
					.then(members -> {
						final Multimap<String, SnomedReferenceSetMember> relationshipsBySource = Multimaps.index(members, m -> m.getReferencedComponent().getId());
						return Multimaps.transformValues(relationshipsBySource, m -> (Integer) m.getProperties().get(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP));
					})
			);
		} else {
			promises.add(Promise.immediate(ImmutableSetMultimap.of()));
		}
		
		// search owl axiom members
		if (isStated()) {
			ImmutableSetMultimap.Builder<String, Integer> groupedAxioms = ImmutableSetMultimap.builder();
			SnomedEclRefinementEvaluator.evalAxiomStatements(context, true, sourceIds, null, null)
				.forEach(property -> groupedAxioms.put(property.getObjectId(), property.getGroup()));
			promises.add(Promise.immediate(groupedAxioms.build()));
		} else {
			promises.add(Promise.immediate(ImmutableSetMultimap.of()));
		}
		
		return Promise.all(promises).then(statements -> {
			Multimap<String, Integer> relationshipStatements = (Multimap<String, Integer>) statements.get(0);
			Multimap<String, Integer> concreteDomainStatements = (Multimap<String, Integer>) statements.get(1);
			Multimap<String, Integer> axiomStatements = (Multimap<String, Integer>) statements.get(2);
			return ImmutableSetMultimap.<String, Integer>builder()
					.putAll(relationshipStatements)
					.putAll(concreteDomainStatements)
					.putAll(axiomStatements)
					.build();
		});
	}

	public Promise<Expression> resolveToAndExpression(BranchContext context, Set<String> matchingIds) {
		if (matchingIds.isEmpty()) {
			return Promise.immediate(Expressions.matchNone());
		} else if (isAnyExpression()) {
			return Promise.immediate(SnomedEclEvaluationRequest.matchIdsOrNone().apply(matchingIds));
		} else {
			return resolveToExpression(context)
					.then(left -> {
						return Expressions.builder()
								.filter(left)
								.filter(RevisionDocument.Expressions.ids(matchingIds))
								.build();
					});
		}
	}

}
