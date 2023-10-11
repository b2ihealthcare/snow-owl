/*
 * Copyright 2020-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.request;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.ints.IntSet;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyFloatMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.Pair;
import com.b2international.commons.exceptions.SyntaxException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.*;
import com.b2international.snowowl.core.ecl.EclLabelerRequestBuilder;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.request.QueryOptimizer;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.request.SnomedHierarchyStats.ConceptDescendantCountById;
import com.b2international.snowowl.snomed.core.request.SnomedHierarchyStats.ConceptSearchById;
import com.b2international.snowowl.snomed.core.request.SnomedHierarchyStats.EdgeSearchBySourceId;
import com.b2international.snowowl.snomed.core.request.SnomedRelationshipStats.RelationshipSearchBySource;
import com.b2international.snowowl.snomed.core.request.SnomedRelationshipStats.RelationshipSearchByTypeAndDestination;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

/**
 * @since 7.7
 */
public final class SnomedQueryOptimizer implements QueryOptimizer {

	/**
	 * @since 8.12.0
	 */
	public enum OptionKey {
		
		/**
		 * A boolean option that controls whether relationship refinement optimization
		 * should be performed.
		 */
		SKIP_REFINEMENTS;
	}
	
	/**
	 * @since 8.12.0
	 */
	public enum OptimizerStrategy {
		
		/**
		 * Default strategy; does not modify ancestor candidate scores in any way.
		 */
		DEFAULT {
			@Override public boolean needsElevation(int iteration) { return iteration > 100; }
			@Override public OptimizerStrategy nextStrategy() { return SCORE_BOOST_1; }
		},
		
		/** 
		 * Boost scores with the fraction of the precision of direct children, for selected candidates.
		 */
		SCORE_BOOST_1 {
			@Override public boolean needsElevation(int iteration) { return iteration > 200; }
			@Override public OptimizerStrategy nextStrategy() { return SCORE_BOOST_2; }
			
			@Override
			public float adjustScore(final float score, final int totalChildren, final float childPrecision) {
				if (score < 0.4f && totalChildren > 6 && childPrecision > 0.8f) {
					return score + childPrecision / 2.0f;
				} else {
					return super.adjustScore(score, totalChildren, childPrecision);
				}
			}
		},

		/**
		 * Boost scores with the precision of direct children for all candidates.
		 */
		SCORE_BOOST_2 {
			@Override
			public float adjustScore(float score, final int totalChildren, final float childPrecision) {
				return score + childPrecision;
			}
		},
		
		/**
		 * Allow some false positives in lossy optimization mode
		 */
		LOSSY {
			@Override 
			public float getFalsePositiveThreshold(float configuredThreshold) { 
				return configuredThreshold; 
			}
		};

		/**
		 * Controls whether the configured false positive threshold value can take
		 * effect.
		 * <p>
		 * With the exception of {@link #LOSSY} mode, the input value
		 * is discarded and <code>0.0f</code> is returned to indicate that false
		 * positives are disallowed. In lossy mode the input value is passed through.
		 * 
		 * @param configuredThreshold the value initially set for the false positive 
		 * threshold
		 * @return <code>0.0f</code> or <code>configuredThreshold</code>, depending on
		 * the optimizer strategy
		 */
		public float getFalsePositiveThreshold(final float configuredThreshold) {
			return 0.0f;
		}

		/**
		 * Adjusts a candidate ancestor's fitness score based on contextual information.
		 * <p>
		 * The default implementation (used in modes {@link #DEFAULT} and
		 * {@link #LOSSY}) does not change the input score.
		 * <p>
		 * Other strategies may modify the input score based on the total number of
		 * children this parent concept has, as well as its precision (percentage of
		 * children that are also members of the evaluated concept set).
		 * 
		 * @param score the candidate's initial score
		 * @param totalChildren the number of children this candidate has
		 * @param childPrecision the percentage of children that are members of the
		 * evaluated concept set
		 * @return the candidate's adjusted score
		 */
		public float adjustScore(final float score, final int totalChildren, final float childPrecision) {
			return score;
		}
		
		/**
		 * Determines whether the optimization algorithm should move to a more
		 * aggressive strategy given the iteration count.
		 * 
		 * @param iteration the number of iterations so far
		 * @return <code>true</code> if a more aggressive strategy should be tried next,
		 * <code>false</code> otherwise
		 */
		public boolean needsElevation(final int iteration) {
			return false;
		}
		
		/**
		 * Suggests the next strategy in the chain. Should only be called when 
		 * {@link #needsElevation} returns <code>true</code>.
		 * 
		 * @return a more aggressive optimization strategy than the current one
		 * @throws NoSuchElementException if there are no more strategies to try
		 */
		public OptimizerStrategy nextStrategy() {
			throw new NoSuchElementException();
		}
	}

	/**
	 * @since 8.12.0
	 */
	@FunctionalInterface
	public interface EclEvaluator {

		EclEvaluator DEFAULT = (context, eclExpression, pageSize) -> SnomedRequests.prepareSearchConcept()
			.setLimit(pageSize)
			.filterByEcl(eclExpression)
			.setFields(SnomedConceptDocument.Fields.ID)
			.stream(context)
			.flatMap(SnomedConcepts::stream)
			.map(SnomedConcept::getId);

		Stream<String> evaluateEcl(BranchContext context, String eclExpression, int pageSize);
	}
	
	/**
	 * @since 8.12.0
	 */
	@FunctionalInterface
	public interface EclLabeler {
	
		EclLabeler DEFAULT = (context, locales, codeSystemUri, unlabeledExpressions) -> new EclLabelerRequestBuilder(codeSystemUri, unlabeledExpressions)
			.setLocales(locales)
			.build()
			.execute(context)
			.getItems();
	
		List<String> getLabeledExpressions(BranchContext context, List<ExtendedLocale> locales, String codeSystemUri, List<String> unlabeledExpressions);
	}

	/**
	 * @since 8.12.0
	 */
	@FunctionalInterface
	public interface ConceptSetEvaluator {
		
		ConceptSetEvaluator DEFAULT = (context, locales, resourceUri, inclusions, exclusions, pageSize) -> {
			// No inclusions, return early with empty result
			if (inclusions.isEmpty()) {
				return newHashSet();
			}
			
			Set<String> queryExpressions = inclusions.stream()
				.map(QueryExpression::getQuery)
				.collect(Collectors.toSet());
			
			if (queryExpressions.isEmpty()) {
				queryExpressions = null;
			}
			
			Set<String> mustNotQueryExpressions = exclusions.stream()
				.map(QueryExpression::getQuery)
				.collect(Collectors.toSet());

			if (mustNotQueryExpressions.isEmpty()) {
				mustNotQueryExpressions = null;
			}

			return CodeSystemRequests.prepareSearchConcepts()
				.filterByCodeSystemUri(resourceUri)
				.filterByInclusions(queryExpressions)
				.filterByExclusions(mustNotQueryExpressions)
				.setFields(SnomedConceptDocument.Fields.ID)
				.setLimit(pageSize)
				.setLocales(locales)
				.stream(context)
				.flatMap(Concepts::stream)
				.map(Concept::getId)
				.collect(Collectors.toSet());
		};
		
		Set<String> evaluateConceptSet(
			BranchContext context, 
			List<ExtendedLocale> locales, 
			ResourceURI resourceUri, 
			Collection<QueryExpression> inclusions, 
			Collection<QueryExpression> exclusions, 
			int pageSize);
	}

	private final int pageSize;
	private ResourceURI resourceUri;
	private Logger log;
	
	// External dependencies, can be modified for testing purposes
	private Clock clock = Clock.systemUTC();
	private EclEvaluator evaluator = EclEvaluator.DEFAULT;
	private EclLabeler labeler = EclLabeler.DEFAULT;
	private ConceptSetEvaluator conceptSetEvaluator = ConceptSetEvaluator.DEFAULT;

	private RelationshipSearchBySource relationshipSearchBySource = RelationshipSearchBySource.DEFAULT;
	private RelationshipSearchByTypeAndDestination relationshipSearchByTypeAndDestination = RelationshipSearchByTypeAndDestination.DEFAULT;

	private ConceptSearchById conceptSearchById = ConceptSearchById.DEFAULT;
	private ConceptDescendantCountById conceptDescendantCountById = ConceptDescendantCountById.DEFAULT;
	private EdgeSearchBySourceId edgeSearchBySourceId = EdgeSearchBySourceId.DEFAULT;
	
	// Optimizer parameters (some of these are adjusted dynamically)
	private final int minimumClusterSize       = 2;
	private final float falsePositiveThreshold = 0.7f;
	private final double weight                = 1.0d;
	private final float minimumFitThreshold    = 0.5f;
	private float fitThreshold                 = 0.9f;
	private final int idealClauseCount         = 1;
	private final int maxIteration             = 1000;
	private final Duration maxRuntime          = Duration.ofSeconds(580L);
	private boolean skipRefinements            = false;

	private OptimizerStrategy optimizerStrategy = OptimizerStrategy.DEFAULT;
	private int targetClauseCount               = 1;
	private int maxClauseCount                  = 2000;

	// Run-time fields
	private Set<String> conceptSet;
	private Set<String> conceptsToInclude;
	private Set<String> conceptsToExclude;

	private List<QueryExpression> optimizedInclusions;
	private List<QueryExpression> optimizedExclusions;
	

	public SnomedQueryOptimizer(final int pageSize) {
		this.pageSize = pageSize;
	}

	@VisibleForTesting
	void setClock(final Clock clock) {
		this.clock = clock;
	}

	@VisibleForTesting
	void setEvaluator(final EclEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	@VisibleForTesting
	void setLabeler(final EclLabeler labeler) {
		this.labeler = labeler;
	}

	@VisibleForTesting
	void setConceptSetEvaluator(final ConceptSetEvaluator conceptSetEvaluator) {
		this.conceptSetEvaluator = conceptSetEvaluator;
	}
	
	@VisibleForTesting
	void setRelationshipSearchBySource(final RelationshipSearchBySource relationshipSearchBySource) {
		this.relationshipSearchBySource = relationshipSearchBySource;
	}

	@VisibleForTesting
	void setRelationshipSearchByTypeAndDestination(final RelationshipSearchByTypeAndDestination relationshipSearchByTypeAndDestination) {
		this.relationshipSearchByTypeAndDestination = relationshipSearchByTypeAndDestination;
	}

	@VisibleForTesting
	void setConceptSearchById(final ConceptSearchById conceptSearchById) {
		this.conceptSearchById = conceptSearchById;
	}

	@VisibleForTesting
	void setConceptDescendantCountById(final ConceptDescendantCountById conceptDescendantCountById) {
		this.conceptDescendantCountById = conceptDescendantCountById;
	}

	@VisibleForTesting
	void setEdgeSearchBySourceId(final EdgeSearchBySourceId edgeSearchBySourceId) {
		this.edgeSearchBySourceId = edgeSearchBySourceId;
	}
	
	@VisibleForTesting
	void setResourceUri(final ResourceURI resourceUri) {
		this.resourceUri = resourceUri;
	}
	
	@VisibleForTesting
	void setLog(final Logger log) {
		this.log = log;
	}

	@Override
	public QueryExpressionDiffs optimize(final BranchContext context, final Options params) {
		if (resourceUri == null) {
			resourceUri = context.service(ResourceURI.class);
		}
		
		if (log == null) {
			log = context.log();
		}
		
		final Collection<QueryExpression> inclusions = params.getCollection(QueryOptimizer.OptionKey.INCLUSIONS, QueryExpression.class);
		final Collection<QueryExpression> exclusions = params.getCollection(QueryOptimizer.OptionKey.EXCLUSIONS, QueryExpression.class);
		final List<ExtendedLocale> locales = params.getList(QueryOptimizer.OptionKey.LOCALES, ExtendedLocale.class);

		// Don't exceed the maximum clause count that was set initially
		maxClauseCount = Ints.min(maxClauseCount, params.getOptional(QueryOptimizer.OptionKey.LIMIT, Integer.class).orElse(maxClauseCount));
		skipRefinements = params.getBoolean(OptionKey.SKIP_REFINEMENTS);
		
		try {
			conceptSet = evaluateConceptSet(context, locales, inclusions, exclusions);
		} catch (SyntaxException e) {
			log.error("Clause evaluation resulted in syntax error, returning empty diff", e);
			return QueryExpressionDiffs.EMPTY;
		}
		
		log.info("{} inclusion(s) and {} exclusion(s) evaluated to {} concept(s)", inclusions.size(), exclusions.size(), conceptSet.size());

		// Exit early if the value set evaluates to a single concept or is empty
		if (conceptSet.size() < 2) {
			return QueryExpressionDiffs.EMPTY;
		}

		// "conceptsToInclude" will shrink as we find query expressions of acceptable quality for inclusion 
		conceptsToInclude = newHashSet(conceptSet);

		/*
		 * "conceptsToExclude" tracks any exclusions we need to add alongside exclusions
		 * received above, when an optimized query expression evaluates to more concepts
		 * than there were in the original set
		 */
		conceptsToExclude = newHashSet();

		final Set<String> inclusionAncestors = optimizeInclusions(context, inclusions);
		optimizeExclusions(context, exclusions, inclusionAncestors);

		/* 
		 * TODO: some of the final compaction steps are still missing:
		 * 
		 * - merge < and = clauses targeting the same concept to a single << clause
		 * - remove = clause if a corresponding << clause exists  
		 * 
		 * - remove sticky inclusions which have a corresponding sticky exclusion
		 */
		
		final QueryExpressionDiff diff = QueryExpressionDiff.create(
			context, 
			inclusions, 
			exclusions, 
			optimizedInclusions, 
			optimizedExclusions);

		log.info("Optimize resulted in {} addition(s) to inclusion clauses, {} addition(s) to exclusion clauses and {} removal(s)", 
			diff.getAddToInclusion().size(),
			diff.getAddToExclusion().size(),
			diff.getRemove().size());
		
		if (diff.getAddToInclusion().isEmpty() && diff.getAddToExclusion().isEmpty() && diff.getRemove().isEmpty()) {
			return QueryExpressionDiffs.EMPTY;
		}
		
		return addLabelsToDiff(context, locales, diff);
	}

	private Set<String> optimizeInclusions(final BranchContext context, final Collection<QueryExpression> inclusions) {
		optimizedInclusions = newArrayList();

		// Pinned inclusions are checked for redundancy but otherwise should appear unmodified in optimized output 
		final List<QueryExpression> pinnedInclusions = optimizePinned(context, inclusions);
		log.trace("Found {} unique pinned inclusion(s)", pinnedInclusions.size());
		applyInclusions(context, pinnedInclusions);

		optimizeRefinementInclusions(context);

		// Collect information about the entire original concept set (ie. not the remaining set) and their ancestors
		final SnomedHierarchyStats inclusionHierarchyStats = SnomedHierarchyStats.create(
			context,
			pageSize,
			conceptSet,
			conceptSearchById,
			edgeSearchBySourceId,
			conceptDescendantCountById);

		// Save collected ancestors for comparing with exclusion ancestors later
		final Set<String> inclusionAncestors =  inclusionHierarchyStats.conceptsAndAncestors();
		// Exclude candidates that did not meet certain criteria
		filterAncestorsForInclusion(inclusionHierarchyStats);

		// Gather ancestors without any false positives
		final List<QueryExpression> noFalsePositiveInclusions = inclusionHierarchyStats.optimizeNoFalsePositives(
			conceptSet, 
			optimizerStrategy.getFalsePositiveThreshold(falsePositiveThreshold));
		
		log.trace("Found {} inclusion(s) with a '<</<' expression that evaluate to existing members only", noFalsePositiveInclusions.size());
		applyInclusions(context, noFalsePositiveInclusions, false);

		/*
		 * TODO: In lossy optimization mode pinned exclusions should be removed when a
		 * "no false positive" inclusion (that, contrary to its name, _can_ include
		 * false positives) covers them
		 */
		optimizeAncestorInclusions(context, inclusionHierarchyStats);

		// Remaining concepts in "conceptsToInclude" are added as '=' inclusions
		if (!conceptsToInclude.isEmpty()) {
			log.trace("Adding {} remaining concept(s) as a simple inclusion", conceptsToInclude.size());

			conceptsToInclude.removeIf(id -> {
				final QueryExpression singleInclusion = new QueryExpression(IDs.base62UUID(), id, false);
				optimizedInclusions.add(singleInclusion);

				// Remove these on the way out
				return true;
			});
		}
		
		final int removals = compact(optimizedInclusions, inclusionHierarchyStats);
		log.trace("Final inclusion compaction removed {} clause(s)", removals);
		return inclusionAncestors;
	}

	private void optimizeRefinementInclusions(final BranchContext context) {
		if (skipRefinements) {
			log.info("Skipping refinement inclusion optimization on request.");
			return;
		}
		
		// See if concepts still to be processed can be converted to "* : c1 = c2" expressions
		final SnomedRelationshipStats inclusionRelationshipStats = SnomedRelationshipStats.create(
			context, 
			pageSize,
			conceptsToInclude, 
			relationshipSearchBySource, 
			relationshipSearchByTypeAndDestination);

		filterRefinementsForInclusion(inclusionRelationshipStats);
		final List<QueryExpression> refinementInclusions = inclusionRelationshipStats.optimizeRefinements();
		log.trace("Found {} inclusion(s) using a refinement expression", refinementInclusions.size());
		applyInclusions(context, refinementInclusions);
	}

	private void optimizeExclusions(final BranchContext context, final Collection<QueryExpression> exclusions, final Set<String> inclusionAncestors) {
		optimizedExclusions = newArrayList();

		final List<QueryExpression> pinnedExclusions = optimizePinned(context, exclusions);
		log.trace("Found {} unique pinned exclusion(s)", pinnedExclusions.size());
		applyExclusions(context, pinnedExclusions);

		optimizeRefinementExclusions(context);

		final SnomedHierarchyStats exclusionHierarchyStats = SnomedHierarchyStats.create(
			context, 
			pageSize,
			conceptsToExclude,
			conceptSearchById,
			edgeSearchBySourceId,
			conceptDescendantCountById);
		
		// Save the set of leaf concepts before elements are removed
		final Set<String> leafExclusions = conceptsToExclude.stream()
			.filter(id -> exclusionHierarchyStats.totalChildren().count(id) == 0)
			.collect(Collectors.toSet());
		
		filterAncestorsForExclusion(exclusionHierarchyStats, inclusionAncestors);

		// For exclusions, only ancestors without any false positives can be accepted
		final List<QueryExpression> noFalsePositiveExclusions = exclusionHierarchyStats.optimizeNoFalsePositives(conceptsToExclude);
		log.trace("Found {} exclusion(s) with a '<</<' expression that evaluate to concepts selected for exclusion only", noFalsePositiveExclusions.size());
		applyExclusions(context, noFalsePositiveExclusions);

		// Remaining concepts in "conceptsToExclude" are added as '=' exclusions, or '<<' if the concept is a leaf (for future-proofing)
		if (!conceptsToExclude.isEmpty()) {
			log.trace("Adding {} remaining concept(s) as a simple exclusion", conceptsToExclude.size());

			conceptsToExclude.removeIf(id -> {
				final String operator = leafExclusions.contains(id) ? "<< " : "";
				final QueryExpression singleInclusion = new QueryExpression(IDs.base62UUID(), String.format("%s%s", operator, id), false);
				optimizedExclusions.add(singleInclusion);

				// Remove these concepts on the way out
				return true;
			});
		}
		
		final int removals = compact(optimizedExclusions, exclusionHierarchyStats);
		log.trace("Final exclusion compaction removed {} clause(s)", removals);
	}

	private void optimizeRefinementExclusions(final BranchContext context) {
		if (skipRefinements) {
			log.info("Skipping refinement exclusion optimization on request.");
			return;
		}
		
		final SnomedRelationshipStats exclusionRelationshipStats = SnomedRelationshipStats.create(
			context, 
			pageSize,
			conceptsToExclude,
			relationshipSearchBySource,
			relationshipSearchByTypeAndDestination);

		filterRefinementsForExclusion(exclusionRelationshipStats);
		final List<QueryExpression> refinementExclusions = exclusionRelationshipStats.optimizeRefinements();
		log.trace("Found {} exclusion(s) using a refinement expression", refinementExclusions.size());
		applyExclusions(context, refinementExclusions);
	}

	private Set<String> evaluateConceptSet(
		final BranchContext context, 
		final List<ExtendedLocale> locales, 
		final Collection<QueryExpression> inclusions, 
		final Collection<QueryExpression> exclusions
	) {
		return conceptSetEvaluator.evaluateConceptSet(context, locales, resourceUri, inclusions, exclusions, pageSize);
	}

	private Stream<String> evaluateEcl(final BranchContext context, final String eclExpression) {
		return evaluator.evaluateEcl(context, eclExpression, pageSize);
	}

	private Set<String> evaluateEclToSet(final BranchContext context, final String eclExpression) {
		return evaluateEcl(context, eclExpression).collect(Collectors.toSet());
	}

	private List<QueryExpression> optimizePinned(final BranchContext context, final Collection<QueryExpression> expressions) {

		// Find unique pinned queries
		final Map<Set<String>, String> pinnedQueries = expressions.stream()
			.filter(clause -> clause.isPinned())
			.collect(Collectors.toMap(
				clause -> evaluateEclToSet(context, clause.getQuery()), 
				clause -> clause.getQuery(),
				BinaryOperator.minBy(Comparator.comparing(String::length)))); // shorter expression wins

		if (pinnedQueries.isEmpty()) {
			return List.of();
		}

		return pinnedQueries.values()
			.stream()
			.map(query -> new QueryExpression(IDs.base62UUID(), query, true))
			.collect(Collectors.toList());
	}

	private void applyInclusions(final BranchContext context, final List<QueryExpression> newInclusions) {
		applyInclusions(context, newInclusions, true);
	}

	private void applyInclusions(final BranchContext context, final List<QueryExpression> newInclusions, final boolean processExclusions) {

		if (newInclusions.isEmpty()) {
			return;
		}

		// Evaluate queries; remove concepts that are members of the original set from "conceptsToInclude"; track extras in "conceptsToExclude"
		newInclusions.stream()
			.map(clause -> Pair.of(clause, evaluateEclToSet(context, clause.getQuery())))
			.forEachOrdered(pair -> {
				final QueryExpression clause = pair.getA();
				final Set<String> includedIds = pair.getB();
				final Set<String> expected = Sets.intersection(includedIds, conceptSet);
				final boolean modified = conceptsToInclude.removeAll(expected);
				
				if (modified) {
					// Only add the clause to the inclusions list if it had actually removed members from coneptsToInclude  
					optimizedInclusions.add(clause);
					
					if (processExclusions) {
						final Set<String> unexpected = Sets.difference(includedIds, conceptSet);
						conceptsToExclude.addAll(unexpected);
					}
				}
			});

		newInclusions.clear();
	}

	private void filterRefinementsForInclusion(final SnomedRelationshipStats relationshipStats) {
		// a) Less than 95% precision
		relationshipStats.filterByPrecision(0.95f);
		// b) Less than ten true positive matches (a bit redundant as you need at least 19 true positive concepts to reach 95% above)
		relationshipStats.filterByMinTruePositives(10);
		// c) More than two false positive matches
		relationshipStats.filterByMaxFalsePositives(2);
	}
	
	private void filterRefinementsForExclusion(final SnomedRelationshipStats relationshipStats) {
		// a) Less than 100% precision (as we can not re-include things that are thrown out by an exclusion expression)
		relationshipStats.filterByPrecision(1.0f);
		// b) Less than five true positive matches
		relationshipStats.filterByMinTruePositives(5);
	}
	
	private void filterAncestorsForInclusion(final SnomedHierarchyStats hierarchyStats) {
		// a) ancestor does not cover the minimum required members of the set
		hierarchyStats.filterByClusterSize(minimumClusterSize);
		// b) a non-member with a single member as its child -- there is no reason to replace "=child" with "<parent" in this case
		hierarchyStats.filterSingleChildMember();

		if (!OptimizerStrategy.LOSSY.equals(optimizerStrategy) || falsePositiveThreshold < 0.75f) {
			// c) more false positive children than true positive children
			hierarchyStats.filterByChildren();
			// d) 10 or more children but less than 60% precision across children
			hierarchyStats.filterByChildrenAndPrecision(9, 0.6f);
		}

		// e) ancestor has no false positives, but it has an _ancestor_ which has no false positives either (or at most 1 and is a non-member)
		hierarchyStats.filterRedundantNoFalsePositives(conceptSet);
	}

	private void filterAncestorsForExclusion(final SnomedHierarchyStats hierarchyStats, final Set<String> inclusionAncestors) { 
		// No ancestor of an included concept can be used for exclusions -- it would remove at least one concept we want to keep in the set. 
		hierarchyStats.removeCandidates(inclusionAncestors);

		// a) ancestor does not cover the minimum required members of the set
		hierarchyStats.filterByClusterSize(minimumClusterSize);
		// b) a non-member with a single member as its child -- there is no reason to replace "=child" with "<parent" in this case
		hierarchyStats.filterSingleChildMember();
		// c) more false positive children than true positive children
		hierarchyStats.filterByChildren();
		// d) ancestor has no false positives, but it has an _ancestor_ which has no false positives either (or at most 1 and is a non-member)
		hierarchyStats.filterRedundantNoFalsePositives(conceptsToExclude);
	}	

	// Compute scores, then iterate over the best candidates with an acceptable fit in decreasing order
	private void optimizeAncestorInclusions(final BranchContext context, final SnomedHierarchyStats inclusionHierarchyStats) {

		LongKeyFloatMap ancestorScores = computeAncestorScores(inclusionHierarchyStats, null);
		if (ancestorScores.isEmpty()) {
			return;
		}
		
		final Instant startTime = Instant.now(clock);
		final Instant endTime = startTime.plus(maxRuntime);

		int iteration = 1;
		int ancestorExpressionCount = 0;
		boolean ancestorFound = false;
		boolean canceled = false;

		while (!canceled) {

			final String bestFitAncestor = getBestAncestor(context, ancestorScores);

			if (bestFitAncestor != null) {
				ancestorFound = true;
				ancestorExpressionCount++;

				// Convert ancestor to an inclusion expression
				final boolean member = conceptSet.contains(bestFitAncestor);
				final String operator = member ? "<<" : "<";
				final QueryExpression bestFitInclusion = new QueryExpression(IDs.base62UUID(), String.format("%s %s", operator, bestFitAncestor), false);

				// "applyInclusions" is effectively inlined here because we need access to the number of concepts included
				final Set<String> includedIds = evaluateEclToSet(context, bestFitInclusion.getQuery());
				final Set<String> expected = Sets.intersection(includedIds, conceptSet);
				final boolean modified = conceptsToInclude.removeAll(expected);

				if (modified) {
					// Add inclusion to our optimized list if it actually removed some elements
					optimizedInclusions.add(bestFitInclusion);

					final Set<String> unexpected = Sets.difference(includedIds, conceptSet);
					conceptsToExclude.addAll(unexpected);
				}

				// Check if a high fit threshold results in lots of small inclusions
				final float clauseCountFromInclusion = ((float) conceptSet.size()) / (includedIds.size() + 1);
				if (clauseCountFromInclusion > 1000.0f) {
					
					// Lower fit threshold to 90% of the original if possible
					final float newFitThreshold = fitThreshold * 0.9f;
					if (newFitThreshold > minimumFitThreshold) {
						fitThreshold = newFitThreshold;
						targetClauseCount = idealClauseCount;
						
						// Recompute scores as targetClauseCount was set back to the initial value
						ancestorScores = computeAncestorScores(inclusionHierarchyStats, ancestorScores.keySet());
						log.trace("Fit threshold changed to {}, target clause count is {}", fitThreshold, targetClauseCount);
					}
				}

				// Elevate strategy after using the same one for 100 iterations (but don't promote to LOSSY from non-lossy strategies)
				if (optimizerStrategy.needsElevation(iteration)) {
					optimizerStrategy = optimizerStrategy.nextStrategy();
					
					// Recompute scores as optimizer strategy has changed
					ancestorScores = computeAncestorScores(inclusionHierarchyStats, ancestorScores.keySet());
					log.trace("Optimizer strategy changed to {} after {} iterations", optimizerStrategy, iteration);
				}

			} else {
				// No acceptable ancestor in this round

				if (targetClauseCount <= maxClauseCount && targetClauseCount <= conceptSet.size() && targetClauseCount != Math.round(targetClauseCount * 1.1f)) {
					// Try increasing target clause count by 10% first
					targetClauseCount = Math.round(targetClauseCount * 1.1f);
					
					// Recompute scores as targetClauseCount has increased					
					ancestorScores = computeAncestorScores(inclusionHierarchyStats, ancestorScores.keySet());
					log.trace("Target clause count changed to {}", targetClauseCount);
				} else {
					// With the clause count at its limits, see if lowering the fitness threshold might work
					final float newFitThreshold;

					if (ancestorFound) {
						// Lower it moderately
						newFitThreshold = fitThreshold * 0.9f;
					} else {
						// Lower it enough so that the current maximum score will pass
						if (!ancestorScores.isEmpty()) {
							newFitThreshold = Floats.max(ancestorScores.values().toArray());
						} else {
							newFitThreshold = 0.0f;
						}
					}

					if (newFitThreshold > minimumFitThreshold) {
						fitThreshold = newFitThreshold;
						targetClauseCount = idealClauseCount;
						
						// Recompute scores as targetClauseCount was set back to the initial value 
						ancestorScores = computeAncestorScores(inclusionHierarchyStats, ancestorScores.keySet());
						ancestorFound = false;
						log.trace("Fit threshold changed to {}, target clause count is {}", fitThreshold, targetClauseCount);
					} else {
						canceled = true;
					}
				}
				
			}

			iteration++;

			// Do we need to compact clauses?
			if (optimizedInclusions.size() > maxClauseCount) {
				compact(optimizedInclusions, inclusionHierarchyStats);
				
				// Did we manage to get under the limit?
				if (optimizedInclusions.size() > maxClauseCount) {
					log.trace("Compaction could not decrease inclusion count {} below the maximum allowed {}", optimizedInclusions.size(), maxClauseCount);
					canceled = true;
				} else {
					log.trace("Compaction changed inclusion count from {} to {}", optimizedInclusions.size(), maxClauseCount);
				}
			}

			if (iteration > maxIteration) {
				log.trace("Iteration limit {} reached", maxIteration);
				canceled = true;
			}

			final Instant currentTime = Instant.now(clock);
			if (Duration.between(currentTime, endTime).isNegative()) {
				log.trace("{} s runtime limit reached", maxRuntime.toSeconds());
				canceled = true;
			}
		}

		log.trace("Found {} optimized inclusion(s) with a '<</<' expression", ancestorExpressionCount);
	}

	private LongKeyFloatMap computeAncestorScores(final SnomedHierarchyStats inclusionHierarchyStats, final LongSet keysToRetain) {
		final LongKeyFloatMap ancestorScores = inclusionHierarchyStats.computeAncestorScores(
			conceptSet, 
			optimizerStrategy, 
			targetClauseCount, 
			weight);
		
		// If some values were discarded already, they should be discarded again
		if (keysToRetain != null) {
			ancestorScores.keySet().retainAll(keysToRetain);
		}
		
		return ancestorScores;
	}

	private int compact(final List<QueryExpression> expressions, final SnomedHierarchyStats hierarchyStats) {
		final IntSet removeIdx = PrimitiveSets.newIntOpenHashSet();

		// Eliminate exact duplicates
		for (int i = 0; i < expressions.size() - 1; i++) {
			for (int j = i + 1; j < expressions.size(); j++) {
				final String queryA = expressions.get(i).getQuery();
				final String queryB = expressions.get(j).getQuery();
				if (queryA.equals(queryB)) {
					removeIdx.add(i);
				}
			}
		}

		// Favor << over < if the same concept ID is mentioned
		for (int i = 0; i < expressions.size(); i++) {
			if (removeIdx.contains(i)) { continue; }

			for (int j = 0; j < expressions.size(); j++) {
				if (i == j) { continue; }
				if (removeIdx.contains(j)) { continue; }

				final String queryA = expressions.get(i).getQuery();
				final String queryB = expressions.get(j).getQuery();

				/*
				 * XXX: We are using string manipulation here instead of parsing the expression - this relies 
				 * on having a space character as a separator between the concept ID and the ECL operator.
				 */
				if (!queryA.startsWith("< ") || !queryB.startsWith("<< ")) {
					continue;
				}

				final String conceptA = getConceptId(queryA);
				final String conceptB = getConceptId(queryB);

				if (conceptA.equals(conceptB)) {
					removeIdx.add(i);
				}
			}
		}

		// Favor (< X or << X) over (< Y or << Y) if X is an ancestor of Y
		for (int i = 0; i < expressions.size(); i++) {
			if (removeIdx.contains(i)) { continue; }

			for (int j = 0; j < expressions.size(); j++) {
				if (i == j) { continue; }
				if (removeIdx.contains(j)) { continue; }

				final String queryA = expressions.get(i).getQuery();
				final String queryB = expressions.get(j).getQuery();

				if ((!queryA.startsWith("< ") && !queryA.startsWith("<< ")) || (!queryB.startsWith("< ") && !queryB.startsWith("<< "))) {
					continue;
				}

				final String conceptA = getConceptId(queryA);
				final String conceptB = getConceptId(queryB);

				if (hierarchyStats.subsumes(conceptA, conceptB)) {
					removeIdx.add(i);
				}
			}
		}				

		if (!removeIdx.isEmpty()) {
			final int[] sortedRemoveIdx = removeIdx.toArray();
			Ints.sortDescending(sortedRemoveIdx);

			for (final int idx : sortedRemoveIdx) {
				final QueryExpression expressionToRemove = expressions.get(idx);
				if (!expressionToRemove.isPinned()) {
					expressions.remove(idx);
				}
			}
		}
		
		return removeIdx.size();
	}

	private String getBestAncestor(final BranchContext context, final LongKeyFloatMap ancestorScore) {

		if (ancestorScore.isEmpty()) {
			return null;
		}

		final float maxScore = Floats.max(ancestorScore.values().toArray());
		if (maxScore < fitThreshold) {
			return null;
		}

		// Otherwise at least one candidate exists with an acceptable fit...
		for (final LongIterator itr = ancestorScore.keySet().iterator(); itr.hasNext(); /* empty */) {
			final long candidateId = itr.next();
			final float candidateScore = ancestorScore.get(candidateId);

			if (candidateScore == maxScore) {
				// Remove ancestor from the score map as it was already used
				itr.remove();
				return Long.toString(candidateId);
			}
		}

		// ...so this part should not be reached.
		throw new IllegalStateException();
	}

	private String getConceptId(final String query) {
		if (query.startsWith("<< ")) {
			return query.substring(3);
		} else if (query.startsWith("< ")) {
			return query.substring(2);
		} else {
			throw new IllegalStateException("Unexpected query expression '" + query + "'.");
		}
	}

	private void applyExclusions(final BranchContext context, final List<QueryExpression> newExclusions) {

		if (newExclusions.isEmpty()) {
			return;
		}

		// Evaluate queries; remove concepts from "conceptsToExclude"
		newExclusions.stream()
			.map(clause -> Pair.of(clause, evaluateEclToSet(context, clause.getQuery())))
			.forEachOrdered(pair -> {
				final QueryExpression clause = pair.getA();
				final Set<String> excludedIds = pair.getB();
				final boolean modified = conceptsToExclude.removeAll(excludedIds);
				
				if (modified) {
					// Add exclusion to our optimized list if it actually removed some elements
					optimizedExclusions.add(clause);
				}
			});
		
		newExclusions.clear();
	}

	private QueryExpressionDiffs addLabelsToDiff(final BranchContext context, final List<ExtendedLocale> locales, final QueryExpressionDiff diff) {

		final List<String> unlabeledExpressions = Streams.concat(diff.getAddToInclusion().stream(), diff.getAddToExclusion().stream())
			.map(QueryExpression::getQuery)
			.toList();

		final String codeSystemUri = resourceUri.getUri();
		final List<String> labeledExpressions = labeler.getLabeledExpressions(context, locales, codeSystemUri, unlabeledExpressions); 
		final Iterator<String> labeledIterator = labeledExpressions.iterator();

		final List<QueryExpression> addToInclusionLabeled = newArrayList(); 
		for (final QueryExpression include : diff.getAddToInclusion()) {
			addToInclusionLabeled.add(new QueryExpression(include.getId(), labeledIterator.next(), include.isPinned()));
		}

		final List<QueryExpression> addToExclusionLabeled = newArrayList(); 
		for (final QueryExpression exclude : diff.getAddToExclusion()) {
			addToExclusionLabeled.add(new QueryExpression(exclude.getId(), labeledIterator.next(), exclude.isPinned()));
		}		

		final QueryExpressionDiff labeledDiff = new QueryExpressionDiff(addToInclusionLabeled, addToExclusionLabeled, diff.getRemove());
		return new QueryExpressionDiffs(List.of(labeledDiff), false);
	}
}
