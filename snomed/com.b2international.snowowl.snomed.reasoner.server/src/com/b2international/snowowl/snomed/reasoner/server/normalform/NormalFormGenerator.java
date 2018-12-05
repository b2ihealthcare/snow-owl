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
import java.util.Set;
import java.util.stream.Collectors;

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
import com.b2international.snowowl.datastore.server.snomed.index.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.reasoner.model.LongConcepts;
import com.b2international.snowowl.snomed.reasoner.server.classification.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeProcessor;
import com.b2international.snowowl.snomed.reasoner.server.diff.concretedomain.ConcreteDomainChangeOrdering;
import com.b2international.snowowl.snomed.reasoner.server.diff.relationship.StatementFragmentOrdering;
import com.google.common.base.Stopwatch;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * Transforms a subsumption hierarchy and a set of non-ISA relationships into
 * distribution normal form.
 *
 * @author law223 - initial implementation in Snorocket's SNOMED API
 */
public final class NormalFormGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(NormalFormGenerator.class);

	private final ReasonerTaxonomy reasonerTaxonomy;
	private final ReasonerTaxonomyBuilder reasonerTaxonomyBuilder;
	
	private final LongKeyMap<Collection<StatementFragment>> targetNonIsARelationships = PrimitiveMaps.newLongKeyOpenHashMap();
	private final LongKeyMap<Collection<ConcreteDomainFragment>> targetMembers = PrimitiveMaps.newLongKeyOpenHashMap();

	/**
	 * Creates a new distribution normal form generator instance.
	 *
	 * @param reasonerTaxonomy        used for querying the concept hierarchy
	 *                                inferred by the reasoner (may not be
	 *                                {@code null})
	 * @param reasonerTaxonomyBuilder used for querying the pre-classification
	 *                                terminology content snapshot (may not be
	 *                                {@code null})
	 */
	public NormalFormGenerator(final ReasonerTaxonomy reasonerTaxonomy, final ReasonerTaxonomyBuilder reasonerTaxonomyBuilder) {
		this.reasonerTaxonomy = reasonerTaxonomy;
		this.reasonerTaxonomyBuilder = reasonerTaxonomyBuilder;
	}
	
	public final void computeChanges(final IProgressMonitor monitor, 
			final OntologyChangeProcessor<StatementFragment> relationshipProcessor,
			final OntologyChangeProcessor<ConcreteDomainFragment> memberProcessor) {
	
		final Stopwatch stopwatch = Stopwatch.createStarted();
		LOGGER.info(">>> Distribution normal form generation");

		final LongList entries = reasonerTaxonomy.getConceptIds();
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Generating distribution normal form...", entries.size() * 2);
		
		try {
		
			for (final LongIterator itr = entries.iterator(); itr.hasNext(); /* empty */) {
				final long conceptId = itr.next();

				computeTargetProperties(conceptId);
				
				final Collection<StatementFragment> existingRelationships = reasonerTaxonomyBuilder.getInferredStatementFragments(conceptId);
				final Collection<StatementFragment> targetRelationships = getTargetRelationships(conceptId);
				relationshipProcessor.apply(conceptId, existingRelationships, targetRelationships, StatementFragmentOrdering.INSTANCE, subMonitor.newChild(1));
				
				final Collection<ConcreteDomainFragment> existingMembers = reasonerTaxonomyBuilder.getInferredConcreteDomainFragments(conceptId);
				final Collection<ConcreteDomainFragment> targetMembers = getTargetMembers(conceptId);
				memberProcessor.apply(conceptId, existingMembers, targetMembers, ConcreteDomainChangeOrdering.INSTANCE, subMonitor.newChild(1));
			}
			
		} finally {
			subMonitor.done();
			LOGGER.info(MessageFormat.format("<<< Distribution normal form generation [{0}]", stopwatch.toString()));
		}
	}

	private void computeTargetProperties(final long conceptId) {
		final LongSet parentIds = reasonerTaxonomy.getParents(conceptId);
		
		/*
		 * Non IS-A relationships are fetched from ancestors; redundancy must be removed. Since we are working through the list
		 * of concepts in breadth-first order, we only need to look at cached results from the direct parents, and "distill"
		 * a non-redundant set of components out of them.
		 */
		final LongKeyMap<Collection<StatementFragment>> candidateNonIsARelationships = PrimitiveMaps.newLongKeyOpenHashMap();
		for (final LongIterator itr = parentIds.iterator(); itr.hasNext(); /* empty */) {
			final long parentId = itr.next();
			candidateNonIsARelationships.put(parentId, targetNonIsARelationships.get(parentId));
		}
		
		// Add stated relationships from the concept in question as potential sources
		final Collection<StatementFragment> ownStatedRelationships = reasonerTaxonomyBuilder.getStatedStatementFragments(conceptId);
		final Collection<StatementFragment> ownStatedNonIsaRelationships = ownStatedRelationships.stream()
				.filter(r -> r.getTypeId() != LongConcepts.IS_A_ID)
				.collect(Collectors.toList());
		
		final Collection<StatementFragment> ownAdditionalGroupedRelationships = reasonerTaxonomyBuilder.getAdditionalGroupedStatementFragments(conceptId);
		candidateNonIsARelationships.put(conceptId, ImmutableList.<StatementFragment>builder()
				.addAll(ownStatedNonIsaRelationships)
				.addAll(ownAdditionalGroupedRelationships)
				.build());
		
		// Collect existing inferred relationships for cross-referencing group numbers
		final Collection<StatementFragment> ownInferredRelationships = reasonerTaxonomyBuilder.getInferredStatementFragments(conceptId);
		final Collection<StatementFragment> ownInferredNonIsaRelationships = ownInferredRelationships.stream()
			.filter(r -> r.getTypeId() != LongConcepts.IS_A_ID)
			.collect(Collectors.toList());

		/*
		 * Do the same as the above, but for CD members
		 */
		final LongKeyMap<Collection<ConcreteDomainFragment>> candidateMembers = PrimitiveMaps.newLongKeyOpenHashMap();
		for (final LongIterator itr = parentIds.iterator(); itr.hasNext(); /* empty */) {
			final long parentId = itr.next();
			candidateMembers.put(parentId, targetMembers.get(parentId));
		}
		
		final Collection<ConcreteDomainFragment> ownStatedMembers = reasonerTaxonomyBuilder.getStatedConcreteDomainFragments(conceptId);
		final Collection<ConcreteDomainFragment> ownAdditionalGroupedMembers = reasonerTaxonomyBuilder.getAdditionalGroupedConcreteDomainFragments(conceptId);
		
		candidateMembers.put(conceptId, ImmutableList.<ConcreteDomainFragment>builder()
				.addAll(ownStatedMembers)
				.addAll(ownAdditionalGroupedMembers)
				.build());
		
		final Collection<ConcreteDomainFragment> ownInferredMembers = reasonerTaxonomyBuilder.getInferredConcreteDomainFragments(conceptId);
		
		// Remove redundancy
		final NormalFormGroupSet targetGroupSet = getTargetGroupSet(conceptId, 
				parentIds,
				ownInferredNonIsaRelationships,
				ownInferredMembers,
				candidateNonIsARelationships,
				candidateMembers);
		
		// Extract results; place them in the cache, so following concepts can re-use it
		targetNonIsARelationships.put(conceptId, ImmutableList.copyOf(relationshipsFromGroupSet(targetGroupSet)));
		targetMembers.put(conceptId, ImmutableList.copyOf(membersFromGroupSet(targetGroupSet)));
	}
	
	private NormalFormGroupSet getTargetGroupSet(final long conceptId,
			final LongSet parentIds,
			final Collection<StatementFragment> existingInferredNonIsAFragments,
			final Collection<ConcreteDomainFragment> existingInferredMembers, 
			final LongKeyMap<Collection<StatementFragment>> candidateNonIsAFragments, 
			final LongKeyMap<Collection<ConcreteDomainFragment>> candidateMembers) {

		// Index existing inferred properties into a GroupSet (without redundancy check)
		final NormalFormGroupSet existingGroupSet = new NormalFormGroupSet();
		final Iterable<NormalFormGroup> existingGroups = toGroups(true, 
				existingInferredNonIsAFragments, 
				existingInferredMembers);
		
		for (final NormalFormGroup ownInferredGroup : existingGroups) {
			existingGroupSet.addUnique(ownInferredGroup);
		}

		// Eliminate redundancy between candidate target properties in another GroupSet
		final NormalFormGroupSet targetGroupSet = new NormalFormGroupSet();
		for (final LongIterator itr = parentIds.iterator(); itr.hasNext(); /* empty */) {
			final long parentId = itr.next();
			final Iterable<NormalFormGroup> otherGroups = toGroups(false, 
					candidateNonIsAFragments.get(parentId),
					candidateMembers.get(parentId));
			
			Iterables.addAll(targetGroupSet, otherGroups);
		}
		
		// Finally, add the (stated) information from the concept itself
		final Iterable<NormalFormGroup> ownGroups = toGroups(false,
				candidateNonIsAFragments.get(conceptId),
				candidateMembers.get(conceptId));

		Iterables.addAll(targetGroupSet, ownGroups);
		
		// Shuffle around group numbers to match existing inferred group numbers as much as possible 
		targetGroupSet.adjustOrder(existingGroupSet);

		// Populate the group number for remaining groups
		targetGroupSet.fillNumbers();

		return targetGroupSet;
	}

	private Iterable<NormalFormGroup> toGroups(final boolean preserveNumbers, 
			final Collection<StatementFragment> conceptRelationships, 
			final Collection<ConcreteDomainFragment> conceptMembers) {

		final Multimap<Integer, StatementFragment> relationshipsByGroupId = Multimaps.index(conceptRelationships, StatementFragment::getGroup);
		final Multimap<Integer, ConcreteDomainFragment> membersByGroupId = Multimaps.index(conceptMembers, ConcreteDomainFragment::getGroup);

		final Set<Integer> allKeys = Sets.union(relationshipsByGroupId.keySet(), membersByGroupId.keySet());
		final ImmutableList.Builder<NormalFormGroup> groups = ImmutableList.builder();
		
		for (final Integer key : allKeys) {
			final Collection<StatementFragment> groupRelationships = relationshipsByGroupId.get(key);
			final Collection<ConcreteDomainFragment> groupMembers = membersByGroupId.get(key);
			
			final Iterable<NormalFormUnionGroup> unionGroups = toUnionGroups(preserveNumbers, groupRelationships, groupMembers);
			final Iterable<NormalFormUnionGroup> disjointUnionGroups = getDisjointComparables(unionGroups);

			if (key == 0) {
				// Properties in group 0 form separate groups
				groups.addAll(toZeroGroups(preserveNumbers, disjointUnionGroups));
			} else {
				// Other group numbers produce a single group from all properties
				groups.add(toNonZeroGroup(preserveNumbers, key, disjointUnionGroups));
			}
		}

		return groups.build();
	}

	private Iterable<NormalFormGroup> toZeroGroups(final boolean preserveNumbers, final Iterable<NormalFormUnionGroup> disjointUnionGroups) {
		return FluentIterable
				.from(disjointUnionGroups)
				.transform(ug -> new NormalFormGroup(ug));
	}

	private NormalFormGroup toNonZeroGroup(final boolean preserveNumbers, final int groupNumber, final Iterable<NormalFormUnionGroup> disjointUnionGroups) {
		final NormalFormGroup group = new NormalFormGroup(disjointUnionGroups);
		if (preserveNumbers) {
			group.setGroupNumber(groupNumber);
		}
		return group;
	}

	private Iterable<NormalFormUnionGroup> toUnionGroups(final boolean preserveNumbers, 
			final Collection<StatementFragment> groupRelationships, 
			final Collection<ConcreteDomainFragment> groupMembers) {
	
		final Multimap<Integer, StatementFragment> relationshipsByUnionGroupId = Multimaps.index(groupRelationships, StatementFragment::getUnionGroup);
		final ImmutableList.Builder<NormalFormUnionGroup> unionGroups = ImmutableList.builder();
	
		for (final Integer key : relationshipsByUnionGroupId.keySet()) {
			final Collection<StatementFragment> unionGroupRelationships = relationshipsByUnionGroupId.get(key);
		
			if (key == 0) {
				// Properties in union group 0 form separate union groups
				unionGroups.addAll(toZeroUnionGroups(unionGroupRelationships, groupMembers));
			} else {
				// Other group numbers produce a single union group from all properties
				unionGroups.add(toNonZeroUnionGroup(preserveNumbers, key, unionGroupRelationships));
			}
		}
		
		// If there are no relationships, process the reference set members
		if (relationshipsByUnionGroupId.isEmpty()) {
			unionGroups.addAll(toZeroUnionGroups(ImmutableList.of(), groupMembers));
		}
	
		return unionGroups.build();
	}

	private Iterable<NormalFormUnionGroup> toZeroUnionGroups(
			final Collection<StatementFragment> unionGroupRelationships, 
			final Collection<ConcreteDomainFragment> unionGroupMembers) {
		
		final ImmutableList.Builder<NormalFormUnionGroup> zeroUnionGroups = ImmutableList.builder();
		
		for (final StatementFragment unionGroupRelationship : unionGroupRelationships) {
			final NormalFormRelationship normalFormRelationship = new NormalFormRelationship(unionGroupRelationship, 
					reasonerTaxonomy, 
					reasonerTaxonomyBuilder);
			
			zeroUnionGroups.add(new NormalFormUnionGroup(normalFormRelationship));
		}
		
		for (final ConcreteDomainFragment unionGroupMember : unionGroupMembers) {
			final NormalFormValue normalFormValue = new NormalFormValue(unionGroupMember, reasonerTaxonomy);
			zeroUnionGroups.add(new NormalFormUnionGroup(normalFormValue));
		}
		
		return zeroUnionGroups.build();
	}

	private NormalFormUnionGroup toNonZeroUnionGroup(final boolean preserveNumbers, 
			final int unionGroupNumber, 
			final Collection<StatementFragment> unionGroupRelationships) {
		
		final Iterable<NormalFormProperty> properties = FluentIterable
				.from(unionGroupRelationships)
				.transform(ugr -> new NormalFormRelationship(ugr, reasonerTaxonomy, reasonerTaxonomyBuilder));

		final NormalFormUnionGroup unionGroup = new NormalFormUnionGroup(properties);
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
	 * <li>a candidate set is maintained for possible results;</li>
	 * <li>each incoming item is checked against all existing candidates to see if
	 * they are redundant (in which case the incoming item is skipped), or if it
	 * makes any of the candidates redundant (in which case the redundant candidates
	 * are removed from the set, and the incoming item gets added);</li>
	 * <li>all surviving items are returned.</li>
	 * </ol>
	 * <p>
	 * The returned Iterable is backed by a locally created Set, and supports
	 * <code>remove()</code>.
	 *
	 * @param comparables the comparables to filter
	 *
	 * @return an {@link Iterable} that only includes the reduced comparables
	 */
	private <T extends NormalFormProperty> Iterable<T> getDisjointComparables(final Iterable<T> comparables) {
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

	private Iterable<StatementFragment> relationshipsFromGroupSet(final NormalFormGroupSet targetGroupSet) {
		return FluentIterable.from(targetGroupSet).transformAndConcat(this::relationshipsFromGroup);
	}

	private Iterable<StatementFragment> relationshipsFromGroup(final NormalFormGroup group) {
		return FluentIterable
				.from(group.getUnionGroups())
				.transformAndConcat(unionGroup -> relationshipsFromUnionGroup(unionGroup, 
						group.getGroupNumber(), 
						unionGroup.getUnionGroupNumber()));
	}

	private Iterable<StatementFragment> relationshipsFromUnionGroup(final NormalFormUnionGroup unionGroup, 
			final int groupNumber, 
			final int unionGroupNumber) {
		
		return FluentIterable
				.from(unionGroup.getProperties())
				.filter(NormalFormRelationship.class)
				.transform(property -> new StatementFragment(
						property.getTypeId(),
						property.getDestinationId(),
						property.isDestinationNegated(),
						groupNumber,
						unionGroupNumber,
						property.isUniversal(),
						property.getStatementId(),
						property.getStorageKey()));
	}
	
	private Iterable<ConcreteDomainFragment> membersFromGroupSet(NormalFormGroupSet targetGroupSet) {
		return FluentIterable.from(targetGroupSet).transformAndConcat(this::membersFromGroup);
	}

	private Iterable<ConcreteDomainFragment> membersFromGroup(final NormalFormGroup group) {
		return FluentIterable
				.from(group.getUnionGroups())
				.transformAndConcat(unionGroup -> membersFromUnionGroup(unionGroup, 
						group.getGroupNumber(), 
						unionGroup.getUnionGroupNumber()));
	}
	
	private Iterable<ConcreteDomainFragment> membersFromUnionGroup(final NormalFormUnionGroup unionGroup, final int groupNumber, final int unionGroupNumber) {
		return FluentIterable
				.from(unionGroup.getProperties())
				.filter(NormalFormValue.class)
				.transform(property -> new ConcreteDomainFragment(
						property.getSerializedValue(),
						property.getTypeId(),
						property.getStorageKey(),
						property.getRefSetId(),
						groupNumber));
	}

	private Collection<StatementFragment> getTargetRelationships(final long conceptId) {
		final Iterable<StatementFragment> targetIsARelationships = getTargetIsARelationships(conceptId);
		final Iterable<StatementFragment> targetNonIsARelationships = this.targetNonIsARelationships.get(conceptId);
	
		return ImmutableList.<StatementFragment>builder()
				.addAll(targetIsARelationships)
				.addAll(targetNonIsARelationships)
				.build();
	}

	private Collection<ConcreteDomainFragment> getTargetMembers(long conceptId) {
		return targetMembers.get(conceptId);
	}

	private Iterable<StatementFragment> getTargetIsARelationships(final long conceptId) {
		final LongSet parentIds = reasonerTaxonomy.getParents(conceptId);
		return LongSets.transform(parentIds, parentId -> new StatementFragment(LongConcepts.IS_A_ID, parentId));
	}
}
