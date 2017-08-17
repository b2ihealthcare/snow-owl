/*
 * Copyright (c) 2009 International Health Terminology Standards Development
 * Organisation
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.pcj.LongSets;
import com.b2international.commons.pcj.LongSets.InverseLongFunction;
import com.b2international.snowowl.datastore.server.snomed.index.InitialReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.reasoner.server.classification.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeProcessor;
import com.b2international.snowowl.snomed.reasoner.server.diff.relationship.StatementFragmentOrdering;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import bak.pcj.LongIterator;
import bak.pcj.map.ByteKeyMap;
import bak.pcj.map.ByteKeyOpenHashMap;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.map.ObjectKeyByteMap;
import bak.pcj.map.ObjectKeyByteMapIterator;
import bak.pcj.map.ObjectKeyByteOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

/**
 * Transforms a subsumption hierarchy and a set of non-ISA relationships into
 * distribution normal form.
 *
 * @author law223 - initial implementation in Snorocket's SNOMED API
 */
public final class RelationshipNormalFormGenerator extends NormalFormGenerator<StatementFragment> {

	/**
	 * Represents any item in an ontology which can be compared for
	 * expressiveness.
	 *
	 * @param <T>
	 *            the implementing type
	 */
	private static interface SemanticComparable<T> {

		/**
		 * Checks if the specified item can be regarded as redundant when
		 * compared to the current item. An item is redundant with respect to
		 * another if it less specific, i.e. it describes a broader range of
		 * individuals.
		 *
		 * @param other
		 *            the item to compare against
		 *
		 * @return <code>true</code> if this item contains an equal or more
		 *         specific description when compared to the other item,
		 *         <code>false</code> otherwise
		 */
		public boolean isSameOrStrongerThan (T other);
	}

	/**
	 * Represents a relationship group, consisting of a(n optionally preserved)
	 * group number and a list of union groups. The object (source concept) is
	 * not stored with the group; it is assumed to be known in context.
	 *
	 * @author law223
	 */
	private static final class Group implements SemanticComparable<Group> {

		private final List<UnionGroup> unionGroups;

		private byte groupNumber = NUMBER_NOT_PRESERVED;

		/**
		 * Creates a new group instance.
		 *
		 * @param unionGroups
		 *            the relationship union groups to associate with this group
		 *            (may not be <code>null</code>)
		 */
		public Group(final Iterable<UnionGroup> unionGroups) {
			checkArgument(unionGroups != null, "unionGroups is null.");
			this.unionGroups = ImmutableList.copyOf(unionGroups);
		}

		public List<UnionGroup> getUnionGroups() {
			return unionGroups;
		}

		public byte getGroupNumber() {
			return groupNumber;
		}

		public void setGroupNumber(final byte groupNumber) {
			checkArgument(groupNumber > NUMBER_NOT_PRESERVED, "Illegal group number '%s'.", groupNumber);
			this.groupNumber = groupNumber;
		}

		@Override
		public boolean isSameOrStrongerThan(final Group other) {

			/*
			 * Things same or stronger than A AND B AND C:
			 *
			 * - A' AND B AND C, where A' is a subclass of A
			 * - A AND B AND C AND D
			 *
			 * So for each end every union group in "other", we'll have to find
			 * a more expressive union group in this group. Points are awarded
			 * if we have extra union groups not used in the comparison.
			 */
			for (final UnionGroup otherUnionGroup : other.unionGroups) {

				boolean found = false;

				for (final UnionGroup ourUnionGroup : unionGroups) {

					if (ourUnionGroup.isSameOrStrongerThan(otherUnionGroup)) {
						found = true;
						break;
					}
				}

				if (!found) {
					return false;
				}
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 31 + unionGroups.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) { 
				return true; 
			}

			if (!(obj instanceof Group)) { 
				return false; 
			}

			final Group other = (Group) obj;

			if (unionGroups.size() != other.unionGroups.size()) {
				return false;
			}

			// containsAll should be symmetric in this case
			return unionGroups.containsAll(other.unionGroups);
		}

		public void adjustOrder(final Group other) {
			if (unionGroups.isEmpty()) {
				return;
			}

			final ByteKeyMap oldNumberMap = new ByteKeyOpenHashMap(unionGroups.size());
			for (final UnionGroup unionGroup : unionGroups) {
				oldNumberMap.put(unionGroup.getUnionGroupNumber(), unionGroup);
			}

			final ObjectKeyByteMap newNumberMap = new ObjectKeyByteOpenHashMap(unionGroups.size());
			for (final UnionGroup unionGroup : unionGroups) {
				final Optional<UnionGroup> otherUnionGroup = Iterables.tryFind(other.unionGroups, Predicates.equalTo(unionGroup));
				if (otherUnionGroup.isPresent()) {
					final byte oldNumber = unionGroup.getUnionGroupNumber();
					final byte newNumber = otherUnionGroup.get().getUnionGroupNumber();

					// If the current union group number is 0, it has a single relationship only, and should be kept that way
					if (oldNumber != 0 && oldNumber != newNumber) {
						newNumberMap.put(unionGroup, newNumber);
					}
				}
			}

			final ObjectKeyByteMapIterator itr = newNumberMap.entries();
			while (itr.hasNext()) {
				itr.next();

				final UnionGroup unionGroupToAdjust = (UnionGroup) itr.getKey();
				final byte oldNumber = unionGroupToAdjust.getUnionGroupNumber();
				final byte newNumber = itr.getValue();

				final UnionGroup swap = (UnionGroup) oldNumberMap.get(newNumber);
				if (swap != null) {
					swap.setUnionGroupNumber(oldNumber);
					oldNumberMap.put(oldNumber, swap);
				} else {
					oldNumberMap.remove(oldNumber);
				}

				unionGroupToAdjust.setUnionGroupNumber(newNumber);
				oldNumberMap.put(newNumber, unionGroupToAdjust);
			}
		}

		public void fillNumbers() {
			byte unionGroupNumber = 1;

			for (final UnionGroup unionGroup : unionGroups) {
				if (unionGroup.getUnionGroupNumber() == NUMBER_NOT_PRESERVED) {
					unionGroup.setUnionGroupNumber(unionGroupNumber++);
				}
			}
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("Group [unionGroups=");
			builder.append(unionGroups);
			builder.append("]");
			return builder.toString();
		}
	}

	private static final class UnionGroup implements SemanticComparable<UnionGroup> {

		private final List<RelationshipFragment> fragments;

		private byte unionGroupNumber = NUMBER_NOT_PRESERVED;

		/**
		 * Creates a new union group instance with the specified parameters,
		 * preserving the union group number for later reference.
		 * 
		 * @param fragments
		 *            the relationship fragments to associate with this union
		 *            group (may not be <code>null</code>)
		 *            
		 * @param unionGroupNumber the union group number
		 */
		public UnionGroup(final Iterable<RelationshipFragment> fragments) {
			checkArgument(fragments != null, "fragments is null.");
			this.fragments = ImmutableList.copyOf(fragments);
		}

		public List<RelationshipFragment> getRelationshipFragments() {
			return fragments;
		}

		public byte getUnionGroupNumber() {
			return unionGroupNumber;
		}

		public void setUnionGroupNumber(final byte unionGroupNumber) {
			checkArgument(unionGroupNumber > NUMBER_NOT_PRESERVED, "Illegal union group number '%s'.", unionGroupNumber);
			this.unionGroupNumber = unionGroupNumber;
		}

		@Override
		public boolean isSameOrStrongerThan(final UnionGroup other) {

			/*
			 * Things same or stronger than A OR B OR C:
			 *
			 * - A' OR B OR C, where A' is a subclass of A
			 * - B
			 *
			 * So we'll have to check for all of our fragments to see if a less
			 * expressive fragment exists in the "other" union group. Points are
			 * awarded if we manage to get away with less fragments than the
			 * "other" union group.
			 */
			for (final RelationshipFragment ourFragment : fragments) {

				boolean found = false;

				for (final RelationshipFragment otherFragment : other.fragments) {

					if (ourFragment.isSameOrStrongerThan(otherFragment)) {
						found = true;
						break;
					}
				}

				if (!found) {
					return false;
				}
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 31 + fragments.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) { 
				return true; 
			}

			if (!(obj instanceof UnionGroup)) { 
				return false; 
			}

			final UnionGroup other = (UnionGroup) obj;

			if (fragments.size() != other.fragments.size()) {
				return false;
			}

			// containsAll should be symmetric in this case
			return fragments.containsAll(other.fragments);
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("UnionGroup [fragments=");
			builder.append(fragments);
			builder.append("]");
			return builder.toString();
		}
	}

	/**
	 * Represents concept attribute-value pairs, used when relationships
	 * originating from different sources are being processed.
	 *
	 * @author law223
	 */
	private final class RelationshipFragment implements SemanticComparable<RelationshipFragment> {

		private final StatementFragment fragment;

		private final Set<ConcreteDomainFragment> concreteDomainFragments;

		public RelationshipFragment(final StatementFragment fragment, Collection<ConcreteDomainFragment> concreteDomainFragments) {
			this.fragment = checkNotNull(fragment);
			this.concreteDomainFragments = ImmutableSet.copyOf(concreteDomainFragments);
		}

		public boolean isDestinationNegated() {
			return fragment.isDestinationNegated();
		}

		public boolean isUniversal() {
			return fragment.isUniversal();
		}

		public long getTypeId() {
			return fragment.getTypeId();
		}

		public long getDestinationId() {
			return fragment.getDestinationId();
		}

		public long getStatementId() {
			return fragment.getStatementId();
		}

		public long getStorageKey() {
			return fragment.getStorageKey();
		}

		@Override
		public boolean isSameOrStrongerThan(final RelationshipFragment other) {

			if (this.equals(other)) {
				return true;
			}

			if (isUniversal() != other.isUniversal()) {
				return false;
			}
			
			if (!concreteDomainFragments.containsAll(other.concreteDomainFragments)) {
				return false;
			}

			if (!isDestinationNegated() && !other.isDestinationNegated()) {

				/*
				 * Things same or stronger than (some/all) rA:
				 *
				 * - (some/all) r'A, where r' is equal to r or is a descendant of r
				 * - (some/all) rA', where A' is equal to A or is a descendant of A
				 * - (some/all) r'A', where both of the above applies
				 */
				final LongSet attributeClosure = getConceptAndAllSuperTypes(getTypeId());
				final LongSet valueClosure = getConceptAndAllSuperTypes(getDestinationId());

				return attributeClosure.contains(other.getTypeId()) && valueClosure.contains(other.getDestinationId());

			} else if (isDestinationNegated() && !other.isDestinationNegated()) {

				final LongSet otherAttributeClosure = getConceptAndAllSuperTypes(other.getTypeId());
				final LongSet superTypes = reasonerTaxonomy.getAncestors(getDestinationId());

				/*
				 * Note that "other" itself may be exhaustive in this case --
				 * the negation will work entirely within the confines of
				 * "other", so it is still going to be more expressive than
				 * "other".
				 *
				 * Supertypes of the negated value can only appear above the
				 * "layers" of exhaustive concepts, because any other case
				 * should be unsatisfiable.
				 */
				return otherAttributeClosure.contains(getTypeId()) && (hasCommonExhaustiveSuperType(other) || isDestinationExhaustive()) && superTypes.contains(other.getDestinationId());

			} else if (!isDestinationNegated() && other.isDestinationNegated()) {

				final LongSet attributeClosure = getConceptAndAllSuperTypes(getTypeId());

				/*
				 * Any contradictions should be filtered out by the reasoner beforehand, so we just check if the two concepts
				 * have a common exhaustive ancestor.
				 */
				return attributeClosure.contains(other.getTypeId()) && hasCommonExhaustiveSuperType(other);

			} else /* if (destinationNegated && other.destinationNegated) */ {

				/*
				 * Note that the comparison is the exact opposite of the first case - if both fragments are negated,
				 * the one which negates a more loose definition is the one that is more strict in the end.
				 */
				final LongSet otherAttributeClosure = getConceptAndAllSuperTypes(other.getTypeId());
				final LongSet otherValueClosure = getConceptAndAllSuperTypes(other.getDestinationId());

				return otherAttributeClosure.contains(getTypeId()) && otherValueClosure.contains(getDestinationId());
			}
		}

		private boolean isDestinationExhaustive() {
			return isExhaustive(getDestinationId());
		}

		private boolean hasCommonExhaustiveSuperType(final RelationshipFragment other) {

			final LongSet valueAncestors = reasonerTaxonomy.getAncestors(getDestinationId());
			final LongSet otherValueAncestors = reasonerTaxonomy.getAncestors(other.getDestinationId());
			final LongSet commonAncestors = LongSets.intersection(valueAncestors, otherValueAncestors);

			for (final LongIterator itr = commonAncestors.iterator(); itr.hasNext(); /* empty */) {
				final long commonAncestor = itr.next();
				if (isExhaustive(commonAncestor)) {
					return true;
				}
			}

			return false;
		}

		private boolean isExhaustive(final long conceptId) {
			return reasonerTaxonomyBuilder.isExhaustive(conceptId);
		}

		/**
		 * Collects all parent concepts reachable from the specified concept. The
		 * returned set also includes the starting concept.
		 *
		 * @param conceptId
		 *            the concept to start from
		 *
		 * @return a set containing the starting concept and all reachable
		 *         supertypes
		 */
		private LongSet getConceptAndAllSuperTypes(final long conceptId) {
			final LongSet ancestors = reasonerTaxonomy.getAncestors(conceptId);
			final LongSet conceptAndAncestors;
			if (ancestors instanceof LongOpenHashSet) {
				conceptAndAncestors = (LongSet) ((LongOpenHashSet) ancestors).clone();
			} else {
				conceptAndAncestors = new LongOpenHashSet(ancestors);
			}
			conceptAndAncestors.add(conceptId);
			return conceptAndAncestors;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}

			if (!(obj instanceof RelationshipFragment)) {
				return false;
			}

			final RelationshipFragment other = (RelationshipFragment) obj;

			return isUniversal() == other.isUniversal() && 
					isDestinationNegated() == other.isDestinationNegated() &&
					getTypeId() == other.getTypeId() &&
					getDestinationId() == other.getDestinationId() &&
					Objects.equal(concreteDomainFragments, other.concreteDomainFragments);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(isUniversal(), isDestinationNegated(), getTypeId(), getDestinationId());
		}

		@Override
		public String toString() {
			return MessageFormat.format("{0,number,#} : {1}{2,number,#} ({3})", getTypeId(), (isDestinationNegated() ? "NOT" : ""), getDestinationId(), isUniversal());
		}
	}

	/**
	 * Represents a set of groups that do not allow redundant elements.
	 */
	private static final class GroupSet extends AbstractSet<Group> {

		private final List<Group> groups = Lists.newArrayList();

		/**
		 * Adds the specified group to this set if it is not already present.
		 * More formally, adds the specified group e to this set if the set
		 * contains no group e2 such that e2.isSameOrStrongerThan(e). If this
		 * set already contains such group, the call leaves the set unchanged and
		 * returns <code>false</code>. If no group contains the specified group,
		 * the call removes all groups ei from the set where
		 * e.isSameOrStrongerThan(ei) applies, adds the new element, and returns
		 * <code>true</code>.
		 */
		@Override
		public boolean add(final Group e) {
			final List<Group> redundant = Lists.newArrayList();

			for (final Group existingGroup : groups) {
				if (existingGroup.isSameOrStrongerThan(e)) {
					return false;
				} else if (e.isSameOrStrongerThan(existingGroup)) {
					redundant.add(existingGroup);
				}
			}

			groups.removeAll(redundant);
			groups.add(e);

			return true;
		}

		/**
		 * Adds a group to the set, bypassing redundancy checks.
		 * 
		 * @see #add(Group)
		 */
		public boolean addUnique(final Group e) {
			return groups.add(e);
		}

		@Override
		public Iterator<Group> iterator() {
			return groups.iterator();
		}

		@Override
		public int size() {
			return groups.size();
		}

		public void adjustOrder(final GroupSet other) {
			if (isEmpty()) {
				return;
			}

			final ByteKeyMap oldNumberMap = new ByteKeyOpenHashMap(groups.size());
			for (final Group group : groups) {
				oldNumberMap.put(group.getGroupNumber(), group);
			}

			final ObjectKeyByteMap newNumberMap = new ObjectKeyByteOpenHashMap(groups.size());
			for (final Group group : groups) {
				final Optional<Group> otherGroup = Iterables.tryFind(other.groups, Predicates.equalTo(group));
				if (otherGroup.isPresent()) {
					final byte oldNumber = group.getGroupNumber();
					final byte newNumber = otherGroup.get().getGroupNumber();

					// If the current group number is 0, it has a single relationship only, and should be kept that way
					if (oldNumber != 0 && oldNumber != newNumber) {
						newNumberMap.put(group, newNumber);
						group.adjustOrder(otherGroup.get());
					}
				}
			}

			final ObjectKeyByteMapIterator itr = newNumberMap.entries();
			while (itr.hasNext()) {
				itr.next();

				final Group groupToAdjust = (Group) itr.getKey();
				final byte oldNumber = groupToAdjust.getGroupNumber();
				final byte newNumber = itr.getValue();

				final Group swap = (Group) oldNumberMap.get(newNumber);
				if (swap != null) {
					swap.setGroupNumber(oldNumber);
					oldNumberMap.put(oldNumber, swap);
				} else {
					oldNumberMap.remove(oldNumber);
				}

				groupToAdjust.setGroupNumber(newNumber);
				oldNumberMap.put(newNumber, groupToAdjust);
			}
		}

		public void fillNumbers() {
			byte groupNumber = 1;

			for (final Group group : groups) {
				group.fillNumbers();

				/* 
				 * Group numbers will already be set on existing inferred relationship groups and 0 groups.
				 */
				if (group.getGroupNumber() == NUMBER_NOT_PRESERVED) {
					group.setGroupNumber(groupNumber++);
				}
			}
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipNormalFormGenerator.class);

	private static final long IS_A_LONG = Long.valueOf(Concepts.IS_A);

	/**
	 * Special group number indicating that the next free group/union group number
	 * should be used when the fragments in this group/union group are converted into
	 * relationships.
	 */
	private static final byte NUMBER_NOT_PRESERVED = -1;

	private static final byte ZERO_GROUP = 0;

	private final LongKeyMap generatedNonIsACache = new LongKeyOpenHashMap();

	/**
	 * Creates a new distribution normal form generator instance.
	 *
	 * @param reasonerTaxonomy the reasoner to extract results from (may not be {@code null})
	 */
	public RelationshipNormalFormGenerator(final ReasonerTaxonomy reasonerTaxonomy, final InitialReasonerTaxonomyBuilder reasonerTaxonomyBuilder) {
		super(reasonerTaxonomy, reasonerTaxonomyBuilder);
	}

	@Override
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
	@Override
	public Collection<StatementFragment> getGeneratedComponents(final long conceptId) {
		final LongSet directSuperTypes = reasonerTaxonomy.getParents(conceptId);

		// Step 1: create IS-A relationships
		final Iterable<StatementFragment> inferredIsAFragments = getInferredIsAFragments(conceptId, directSuperTypes);

		// Step 2: get all non IS-A relationships from ancestors and remove redundancy, then cache the results for later use
		final LongKeyMap otherNonIsAFragments = new LongKeyOpenHashMap();

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

		final ImmutableMultimap.Builder<StatementFragment, ConcreteDomainFragment> cdFragments = ImmutableMultimap.builder(); 
		
		for (StatementFragment ownStatedFragment : ownStatedNonIsaFragments) {
			Collection<ConcreteDomainFragment> ownStatedCdFragments = reasonerTaxonomyBuilder.getStatedConcreteDomainFragments(ownStatedFragment.getStatementId());
			cdFragments.putAll(ownStatedFragment, ownStatedCdFragments);
		}
		
		for (StatementFragment ownInferredFragment : ownInferredFragments) {
			Collection<ConcreteDomainFragment> ownInferredCdFragments = reasonerTaxonomyBuilder.getInferredConcreteDomainFragments(ownInferredFragment.getStatementId());
			cdFragments.putAll(ownInferredFragment, ownInferredCdFragments);
		}

		for (Object statedFragmentsForParent : otherNonIsAFragments.values()) {
			Collection<StatementFragment> otherStatedFragments = (Collection<StatementFragment>) statedFragmentsForParent;
			for (StatementFragment otherStatedFragment : otherStatedFragments) {
				Collection<ConcreteDomainFragment> otherStatedCdFragments = reasonerTaxonomyBuilder.getStatedConcreteDomainFragments(otherStatedFragment.getStatementId());
				cdFragments.putAll(otherStatedFragment, otherStatedCdFragments);
			}
		}

		final Iterable<StatementFragment> inferredNonIsAFragments = getInferredNonIsAFragments(conceptId, 
				ownInferredNonIsaFragments, 
				ownStatedNonIsaFragments,
				otherNonIsAFragments,
				cdFragments.build());

		// Place results in the cache, so children can re-use it
		generatedNonIsACache.put(conceptId, ImmutableList.copyOf(inferredNonIsAFragments));

		// Step 3: concatenate and return
		return ImmutableList.copyOf(Iterables.concat(inferredIsAFragments, inferredNonIsAFragments));
	}

	private Collection<StatementFragment> getCachedNonIsAFragments(final long directSuperTypeId) {
		return getStatementFragments(generatedNonIsACache, directSuperTypeId);
	}

	@SuppressWarnings("unchecked")
	private Collection<StatementFragment> getStatementFragments(final LongKeyMap statementFragmentMap, final long conceptId) {
		return (Collection<StatementFragment>) statementFragmentMap.get(conceptId);
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
			final LongKeyMap parentStatedNonIsAFragments,
			final Multimap<StatementFragment, ConcreteDomainFragment> cdFragments) {

		// Index existing inferred non-IS A relationship groups into a GroupSet (without redundancy check)
		final GroupSet inferredGroups = new GroupSet();
		final Iterable<Group> ownInferredGroups = toGroups(true, ownInferredNonIsAFragments, cdFragments);
		for (final Group ownInferredGroup : ownInferredGroups) {
			inferredGroups.addUnique(ownInferredGroup);
		}

		// Eliminate redundancy between existing stated non-IS A relationship groups
		final GroupSet groups = new GroupSet();
		final Iterable<Group> ownGroups = toGroups(false, ownStatedNonIsAFragments, cdFragments);
		Iterables.addAll(groups, ownGroups);

		// Continue by adding stated non-IS A relationship groups from parents indicated by the reasoner
		for (final LongIterator itr = parentStatedNonIsAFragments.keySet().iterator(); itr.hasNext(); /* empty */) {
			final long parentId = itr.next();
			final Iterable<Group> otherGroups = toGroups(false, getStatementFragments(parentStatedNonIsAFragments, parentId), cdFragments);
			Iterables.addAll(groups, otherGroups);
		}

		// The remaining non-redundant groups should be numbered from 1
		groups.fillNumbers();

		// Shuffle around the numbers to match existing inferred group numbers as much as possible 
		groups.adjustOrder(inferredGroups);

		// Convert groups back to individual statement fragments
		return fromGroupSet(groups);
	}

	private Iterable<Group> toGroups(final boolean preserveNumbers, 
			final Collection<StatementFragment> nonIsARelationshipFragments,
			final Multimap<StatementFragment, ConcreteDomainFragment> cdFragments) {

		final Map<Byte, Collection<StatementFragment>> relationshipsByGroupId = Multimaps.index(nonIsARelationshipFragments, 
				new Function<StatementFragment, Byte>() {
			@Override
			public Byte apply(final StatementFragment input) {
				return input.getGroup();
			}
		}).asMap();

		final Collection<Collection<Group>> groups = Maps.transformEntries(relationshipsByGroupId, 
				new EntryTransformer<Byte, Collection<StatementFragment>, Collection<Group>>() {
			@Override
			public Collection<Group> transformEntry(final Byte key, final Collection<StatementFragment> values) {
				final Iterable<UnionGroup> unionGroups = toUnionGroups(preserveNumbers, values, cdFragments);
				final Iterable<UnionGroup> disjointUnionGroups = getDisjointComparables(unionGroups);

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

	private Iterable<Group> toZeroGroups(final boolean preserveNumbers, final Iterable<UnionGroup> disjointUnionGroups) {
		return FluentIterable.from(disjointUnionGroups).transform(new Function<UnionGroup, Group>() {
			@Override
			public Group apply(final UnionGroup input) {
				final Group group = new Group(ImmutableList.of(input));
				group.setGroupNumber(ZERO_GROUP);
				return group;
			}
		});
	}

	private Group toNonZeroGroup(final boolean preserveNumbers, final byte groupNumber, final Iterable<UnionGroup> disjointUnionGroups) {
		final Group group = new Group(disjointUnionGroups);
		if (preserveNumbers) {
			group.setGroupNumber(groupNumber);
		}
		return group;
	}

	private Iterable<UnionGroup> toUnionGroups(final boolean preserveNumbers, 
			final Collection<StatementFragment> values,
			final Multimap<StatementFragment, ConcreteDomainFragment> cdFragments) {
		
		final Map<Byte, Collection<StatementFragment>> relationshipsByUnionGroupId = Multimaps.index(values, 
				new Function<StatementFragment, Byte>() {
			@Override
			public Byte apply(final StatementFragment input) {
				return input.getUnionGroup();
			}
		}).asMap();

		final Collection<Collection<UnionGroup>> unionGroups = Maps.transformEntries(relationshipsByUnionGroupId, 
				new EntryTransformer<Byte, Collection<StatementFragment>, Collection<UnionGroup>>() {
			@Override
			public Collection<UnionGroup> transformEntry(final Byte key, final Collection<StatementFragment> values) {
				if (key == 0) {
					// Relationships in union group 0 form separate union groups
					return ImmutableList.copyOf(toZeroUnionGroups(values, cdFragments));
				} else {
					// Other group numbers produce a single union group from all fragments
					return ImmutableList.of(toNonZeroUnionGroup(preserveNumbers, key, values, cdFragments));
				}
			}
		}).values();

		return Iterables.concat(unionGroups);
	}

	private Iterable<UnionGroup> toZeroUnionGroups(final Collection<StatementFragment> values, final Multimap<StatementFragment, ConcreteDomainFragment> cdFragments) {
		return FluentIterable.from(values).transform(new Function<StatementFragment, UnionGroup>() {
			@Override
			public UnionGroup apply(final StatementFragment input) {
				final UnionGroup unionGroup = new UnionGroup(ImmutableList.of(new RelationshipFragment(input, cdFragments.get(input))));
				unionGroup.setUnionGroupNumber(ZERO_GROUP); 
				return unionGroup;
			}
		});
	}

	private UnionGroup toNonZeroUnionGroup(final boolean preserveNumbers, final byte unionGroupNumber, 
			final Collection<StatementFragment> values,
			final Multimap<StatementFragment, ConcreteDomainFragment> cdFragments) {
		
		final Iterable<RelationshipFragment> fragments = FluentIterable.from(values).transform(new Function<StatementFragment, RelationshipFragment>() {
			@Override
			public RelationshipFragment apply(final StatementFragment input) {
				return new RelationshipFragment(input, cdFragments.get(input));
			}
		});

		final UnionGroup unionGroup = new UnionGroup(fragments);
		if (preserveNumbers) {
			unionGroup.setUnionGroupNumber(unionGroupNumber);
		}
		return unionGroup;
	}

	/**
	 * Filters {@link SemanticComparable}s so that the returned Iterable only
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
	private <T extends SemanticComparable<T>> Iterable<T> getDisjointComparables(final Iterable<T> comparables) {
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

	private Iterable<StatementFragment> fromGroupSet(final GroupSet groups) {
		return FluentIterable.from(groups).transformAndConcat(new Function<Group, Iterable<StatementFragment>>() {
			@Override
			public Iterable<StatementFragment> apply(final Group group) {
				return fromGroup(group);
			}
		});
	}

	private Iterable<StatementFragment> fromGroup(final Group group) {
		return FluentIterable.from(group.getUnionGroups()).transformAndConcat(new Function<UnionGroup, Iterable<StatementFragment>>() {
			@Override
			public Iterable<StatementFragment> apply(final UnionGroup unionGroup) {
				return fromUnionGroup(unionGroup, group.getGroupNumber(), unionGroup.getUnionGroupNumber());
			}
		});
	}

	private Iterable<StatementFragment> fromUnionGroup(final UnionGroup unionGroup, final byte groupNumber, final byte unionGroupNumber) {
		return FluentIterable.from(unionGroup.getRelationshipFragments()).transform(new Function<RelationshipFragment, StatementFragment>() {
			@Override
			public StatementFragment apply(final RelationshipFragment input) {
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

	public final int collectNormalFormChanges(final IProgressMonitor monitor, final OntologyChangeProcessor<StatementFragment> processor) {
		LOGGER.info(">>> Relationship normal form generation");
		final Stopwatch stopwatch = Stopwatch.createStarted();
		final int results = collectNormalFormChanges(monitor, processor, StatementFragmentOrdering.INSTANCE);
		LOGGER.info(MessageFormat.format("<<< Relationship normal form generation [{0}]", stopwatch.toString()));
		return results;
	}
}
