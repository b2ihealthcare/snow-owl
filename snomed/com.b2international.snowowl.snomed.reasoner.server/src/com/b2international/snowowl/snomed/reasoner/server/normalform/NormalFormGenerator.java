/*
 * Copyright 2009 International Health Terminology Standards Development Organisation
 * Copyright 2013-2018 B2i Healthcare Pte Ltd, http://b2i.sg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright CSIRO Australian e-Health Research Centre (http://aehrc.com).
 * All rights reserved. Use is subject to license terms and conditions.
 */
package com.b2international.snowowl.snomed.reasoner.server.normalform;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.collect.LongSets.InverseLongFunction;
import com.b2international.snowowl.datastore.server.snomed.index.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.reasoner.model.LongConcepts;
import com.b2international.snowowl.snomed.reasoner.server.classification.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeProcessor;
import com.b2international.snowowl.snomed.reasoner.server.diff.relationship.StatementFragmentOrdering;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * Transforms a subsumption hierarchy and a set of non-ISA relationships into
 * distribution normal form.
 *
 * @author law223 - initial implementation in Snorocket's SNOMED API
 */
public final class NormalFormGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(NormalFormGenerator.class);

	private static final int ZERO_GROUP = 0;

	private final ReasonerTaxonomy reasonerTaxonomy;
	private final ReasonerTaxonomyBuilder reasonerTaxonomyBuilder;
	private final LongKeyMap<Collection<StatementFragment>> generatedNonIsACache = PrimitiveMaps.newLongKeyOpenHashMap();

	/**
	 * Computes and returns all changes as a result of normal form computation.
	 * 
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * to call <code>done()</code> on the given monitor. Accepts <code>null</code>, indicating that no progress should
	 * be reported and that the operation cannot be cancelled.
	 * @param processor the change processor to route changes to
	 * @param ordering an ordering defined over existing and generated components, used for detecting changes
	 * @return the total number of generated components
	 */
	public final int collectNormalFormChanges(final IProgressMonitor monitor, final OntologyChangeProcessor<T> processor, final Ordering<T> ordering) {

		final LongList entries = reasonerTaxonomy.getConceptIds();
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Generating normal form...", entries.size());
		int generatedComponentCount = 0;
		
		try {
		
			for (final LongIterator itr = entries.iterator(); itr.hasNext(); /* empty */) {
				final long conceptId = itr.next();
				final Collection<T> existingComponents = getExistingComponents(conceptId);
				final Collection<T> generatedComponents = getGeneratedComponents(conceptId);
				processor.apply(conceptId, existingComponents, generatedComponents, ordering, subMonitor.newChild(1));
				generatedComponentCount += generatedComponents.size();
			}
			
		} finally {
			subMonitor.done();
		}
		
		return generatedComponentCount; 
	}
	/**
	 * Creates a new distribution normal form generator instance.
	 *
	 * @param reasonerTaxonomy the reasoner to extract results from (may not be {@code null})
	 */
	public NormalFormGenerator(final ReasonerTaxonomy reasonerTaxonomy, final ReasonerTaxonomyBuilder reasonerTaxonomyBuilder) {
		this.reasonerTaxonomy = reasonerTaxonomy;
		this.reasonerTaxonomyBuilder = reasonerTaxonomyBuilder;
	}

	public Collection<StatementFragment> getExistingComponents(final long conceptId) {
		return reasonerTaxonomyBuilder.getInferredStatementFragments(conceptId);
	}

	/**
	 * Outbound relationships are calculated in the following fashion:
	 * 
	 * <ol>
	 * <li>
	 * The given concept's <i>direct supertypes</i> are collected from the
	 * inferred taxonomy, and IS_A relationships are created for all of 
	 * them;
	 * </li>
	 * <li>
	 * The <i>given concept and all of its ancestors</i> are gathered from
	 * the taxonomy; the outbound non-IS_A relationship set reachable from
	 * these concepts is extracted; this set is further reduced to contain 
	 * only non-redundant relationships; the resulting relationship groups 
	 * are numbered continuously from 1;
	 * </li>
	 * <li>
	 * Existing inferred non-IS_A relationships are collected for the 
	 * concept, forming relationship groups;
	 * </li>
	 * <li>
	 * Where applicable, new inferred relationship group and union group 
	 * numbers are shuffled to preserve existing values.
	 * </li>
	 * </ol>
	 *
	 * @return a collection of outbound relationships for the specified concept in distribution normal form
	 */
	public Collection<StatementFragment> getGeneratedComponents(final long conceptId) {
		final LongSet directSuperTypes = reasonerTaxonomy.getParents(conceptId);

		// Step 1: create IS-A relationships
		final Iterable<StatementFragment> inferredIsAFragments = getInferredIsAFragments(conceptId, directSuperTypes);

		// Step 2: get all non IS-A relationships from ancestors and remove redundancy, then cache the results for later use
		final LongKeyMap<Collection<StatementFragment>> otherNonIsAFragments = PrimitiveMaps.newLongKeyOpenHashMap();

		/* 
		 * We can rely on the fact that the tree is processed in breadth-first order, so the parents' non-IS A relationships
		 * will already be present in the cache
		 */
		for (final LongIterator itr = directSuperTypes.iterator(); itr.hasNext(); /* empty */) {
			final long directSuperTypeId = itr.next();
			otherNonIsAFragments.put(directSuperTypeId, getCachedNonIsAFragments(directSuperTypeId));
		}

		final Collection<StatementFragment> ownStatedNonIsaFragments = reasonerTaxonomyBuilder.getStatedNonIsAFragments(conceptId);
		final Collection<StatementFragment> ownInferredFragments = reasonerTaxonomyBuilder.getInferredStatementFragments(conceptId);
		final Collection<StatementFragment> ownInferredNonIsaFragments = Collections2.filter(ownInferredFragments, new Predicate<StatementFragment>() {
			@Override
			public boolean apply(final StatementFragment input) {
				return input.getTypeId() != IS_A_LONG;
			}
		});

		final Iterable<StatementFragment> inferredNonIsAFragments = getInferredNonIsAFragments(conceptId, 
				ownInferredNonIsaFragments, 
				ownStatedNonIsaFragments,
				otherNonIsAFragments);

		// Place results in the cache, so children can re-use it
		generatedNonIsACache.put(conceptId, ImmutableList.copyOf(inferredNonIsAFragments));

		// Step 3: concatenate and return
		return ImmutableList.copyOf(Iterables.concat(inferredIsAFragments, inferredNonIsAFragments));
	}

	private Collection<StatementFragment> getCachedNonIsAFragments(final long directSuperTypeId) {
		return generatedNonIsACache.get(directSuperTypeId);
	}

	private Iterable<StatementFragment> getInferredIsAFragments(final long conceptId, final LongSet parentIds) {
		return LongSets.transform(parentIds, new InverseLongFunction<StatementFragment>() {
			@Override
			public StatementFragment apply(final long parentId) {
				return new StatementFragment(IS_A_LONG, parentId);
			}
		});
	}

	private Iterable<StatementFragment> getInferredNonIsAFragments(final long sourceId,
			final Collection<StatementFragment> ownInferredNonIsAFragments,
			final Collection<StatementFragment> ownStatedNonIsAFragments,
			final LongKeyMap<Collection<StatementFragment>> parentStatedNonIsAFragments) {

		// Index existing inferred non-IS A relationship groups into a GroupSet (without redundancy check)
		final NormalFormGroupSet inferredGroups = new NormalFormGroupSet();
		final Iterable<NormalFormGroup> ownInferredGroups = toGroups(true, ownInferredNonIsAFragments);
		for (final NormalFormGroup ownInferredGroup : ownInferredGroups) {
			inferredGroups.addUnique(ownInferredGroup);
		}

		// Eliminate redundancy between existing stated non-IS A relationship groups
		final NormalFormGroupSet groups = new NormalFormGroupSet();
		final Iterable<NormalFormGroup> ownGroups = toGroups(false, ownStatedNonIsAFragments);
		Iterables.addAll(groups, ownGroups);

		// Continue by adding stated non-IS A relationship groups from parents indicated by the reasoner
		for (final LongIterator itr = parentStatedNonIsAFragments.keySet().iterator(); itr.hasNext(); /* empty */) {
			final long parentId = itr.next();
			final Iterable<NormalFormGroup> otherGroups = toGroups(false, parentStatedNonIsAFragments.get(parentId));
			Iterables.addAll(groups, otherGroups);
		}

		// The remaining non-redundant groups should be numbered from 1
		groups.fillNumbers();

		// Shuffle around the numbers to match existing inferred group numbers as much as possible 
		groups.adjustOrder(inferredGroups);

		// Convert groups back to individual statement fragments
		return fromGroupSet(groups);
	}

	private Iterable<NormalFormGroup> toGroups(final boolean preserveNumbers, final Collection<StatementFragment> nonIsARelationshipFragments) {

		final Map<Integer, Collection<StatementFragment>> relationshipsByGroupId = Multimaps.index(nonIsARelationshipFragments, new Function<StatementFragment, Integer>() {
			@Override
			public Integer apply(final StatementFragment input) {
				return input.getGroup();
			}
		}).asMap();

		final Collection<Collection<NormalFormGroup>> groups = Maps.transformEntries(relationshipsByGroupId, 
				new EntryTransformer<Integer, Collection<StatementFragment>, Collection<NormalFormGroup>>() {
			@Override
			public Collection<NormalFormGroup> transformEntry(final Integer key, final Collection<StatementFragment> values) {
				final Iterable<NormalFormUnionGroup> unionGroups = toUnionGroups(preserveNumbers, values);
				final Iterable<NormalFormUnionGroup> disjointUnionGroups = getDisjointComparables(unionGroups);

				if (key == 0) {
					// Relationships in group 0 form separate groups
					return ImmutableList.copyOf(toZeroGroups(preserveNumbers, disjointUnionGroups));
				} else {
					// Other group numbers produce a single group from all fragments
					return ImmutableList.of(toNonZeroGroup(preserveNumbers, key, disjointUnionGroups));
				}
			}
		}).values();

		return Iterables.concat(groups);
	}

	private Iterable<NormalFormGroup> toZeroGroups(final boolean preserveNumbers, final Iterable<NormalFormUnionGroup> disjointUnionGroups) {
		return FluentIterable.from(disjointUnionGroups).transform(new Function<NormalFormUnionGroup, NormalFormGroup>() {
			@Override
			public NormalFormGroup apply(final NormalFormUnionGroup input) {
				final NormalFormGroup group = new NormalFormGroup(ImmutableList.of(input));
				group.setGroupNumber(ZERO_GROUP);
				return group;
			}
		});
	}

	private NormalFormGroup toNonZeroGroup(final boolean preserveNumbers, final int groupNumber, final Iterable<NormalFormUnionGroup> disjointUnionGroups) {
		final NormalFormGroup group = new NormalFormGroup(disjointUnionGroups);
		if (preserveNumbers) {
			group.setGroupNumber(groupNumber);
		}
		return group;
	}

	private Iterable<NormalFormUnionGroup> toUnionGroups(final boolean preserveNumbers, final Collection<StatementFragment> values) {
		final Map<Integer, Collection<StatementFragment>> relationshipsByUnionGroupId = Multimaps.index(values, new Function<StatementFragment, Integer>() {
			@Override
			public Integer apply(final StatementFragment input) {
				return input.getUnionGroup();
			}
		}).asMap();

		final Collection<Collection<NormalFormUnionGroup>> unionGroups = Maps.transformEntries(relationshipsByUnionGroupId, 
				new EntryTransformer<Integer, Collection<StatementFragment>, Collection<NormalFormUnionGroup>>() {
			@Override
			public Collection<NormalFormUnionGroup> transformEntry(final Integer key, final Collection<StatementFragment> values) {
				if (key == 0) {
					// Relationships in union group 0 form separate union groups
					return ImmutableList.copyOf(toZeroUnionGroups(values));
				} else {
					// Other group numbers produce a single union group from all fragments
					return ImmutableList.of(toNonZeroUnionGroup(preserveNumbers, key, values));
				}
			}
		}).values();

		return Iterables.concat(unionGroups);
	}

	private Iterable<NormalFormUnionGroup> toZeroUnionGroups(final Collection<StatementFragment> values) {
		return FluentIterable.from(values).transform(new Function<StatementFragment, NormalFormUnionGroup>() {
			@Override
			public NormalFormUnionGroup apply(final StatementFragment input) {
				final NormalFormUnionGroup unionGroup = new NormalFormUnionGroup(ImmutableList.of(new NormalFormRelationship(input)));
				unionGroup.setUnionGroupNumber(ZERO_GROUP); 
				return unionGroup;
			}
		});
	}

	private NormalFormUnionGroup toNonZeroUnionGroup(final boolean preserveNumbers, final int unionGroupNumber, final Collection<StatementFragment> values) {
		final Iterable<NormalFormRelationship> fragments = FluentIterable.from(values).transform(new Function<StatementFragment, NormalFormRelationship>() {
			@Override
			public NormalFormRelationship apply(final StatementFragment input) {
				return new NormalFormRelationship(input);
			}
		});

		final NormalFormUnionGroup unionGroup = new NormalFormUnionGroup(fragments);
		if (preserveNumbers) {
			unionGroup.setUnionGroupNumber(unionGroupNumber);
		}
		return unionGroup;
	}

	/**
	 * Filters {@link NormalFormProperty}s so that the returned Iterable only
	 * includes elements that are not redundant with respect to each other. The
	 * following steps are taken to ensure that no redundant SemanticComparables
	 * remain in the output Iterable:
	 * <p>
	 * <ol>
	 * <li>
	 * a candidate set is maintained for possible results;
	 * </li>
	 * <li>
	 * each incoming item is checked against all existing candidates to see
	 * if they are redundant (in which case the incoming item is skipped), or if
	 * it makes any of the candidates redundant (in which case the redundant
	 * candidates are removed from the set, and the incoming item gets added);
	 * </li>
	 * <li>
	 * all surviving items are returned.
	 * </li>
	 * </ol>
	 * <p>
	 * The returned Iterable is backed by a locally created Set, and supports
	 * <code>remove()</code>.
	 *
	 * @param comparables
	 *            the comparables to filter
	 *
	 * @return an {@link Iterable} that only includes the reduced comparables
	 */
	private <T extends NormalFormProperty<T>> Iterable<T> getDisjointComparables(final Iterable<T> comparables) {
		final Set<T> candidates = Sets.newHashSet();
		final Set<T> redundant = Sets.newHashSet();

		for (final T comparable : comparables) {

			redundant.clear();
			boolean found = false;

			for (final T candidate : candidates) {

				if (candidate.isSameOrStrongerThan(comparable)) {
					found = true;
					break;
				} else if (comparable.isSameOrStrongerThan(candidate)) {
					redundant.add(candidate);
				}
			}

			if (!found) {
				candidates.removeAll(redundant);
				candidates.add(comparable);
			}
		}

		return candidates;
	}

	private Iterable<StatementFragment> fromGroupSet(final NormalFormGroupSet groups) {
		return FluentIterable.from(groups).transformAndConcat(new Function<NormalFormGroup, Iterable<StatementFragment>>() {
			@Override
			public Iterable<StatementFragment> apply(final NormalFormGroup group) {
				return fromGroup(group);
			}
		});
	}

	private Iterable<StatementFragment> fromGroup(final NormalFormGroup group) {
		return FluentIterable.from(group.getUnionGroups()).transformAndConcat(new Function<NormalFormUnionGroup, Iterable<StatementFragment>>() {
			@Override
			public Iterable<StatementFragment> apply(final NormalFormUnionGroup unionGroup) {
				return fromUnionGroup(unionGroup, group.getGroupNumber(), unionGroup.getUnionGroupNumber());
			}
		});
	}

	private Iterable<StatementFragment> fromUnionGroup(final NormalFormUnionGroup unionGroup, final int groupNumber, final int unionGroupNumber) {
		return FluentIterable.from(unionGroup.getProperties()).transform(new Function<NormalFormRelationship, StatementFragment>() {
			@Override
			public StatementFragment apply(final NormalFormRelationship input) {
				return new StatementFragment(
						input.getTypeId(),
						input.getDestinationId(),
						input.isDestinationNegated(),
						groupNumber,
						unionGroupNumber,
						input.isUniversal(),
						input.getStatementId(),
						input.getStorageKey());
			}
		});
	}

	public final int collectNormalFormChanges(final IProgressMonitor monitor, 
			final OntologyChangeProcessor<StatementFragment> statementProcessor,
			final OntologyChangeProcessor<ConcreteDomainFragment> fragmentProcessor) {

		LOGGER.info(">>> Relationship normal form generation");
		final Stopwatch stopwatch = Stopwatch.createStarted();
		final int results = collectNormalFormChanges(monitor, statementProcessor, fragmentProcessor, StatementFragmentOrdering.INSTANCE);
		LOGGER.info(MessageFormat.format("<<< Relationship normal form generation [{0}]", stopwatch.toString()));

		return results;
	}
}
