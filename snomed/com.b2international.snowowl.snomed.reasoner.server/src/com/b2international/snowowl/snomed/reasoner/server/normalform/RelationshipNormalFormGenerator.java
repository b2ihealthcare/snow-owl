/**
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

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bak.pcj.LongIterator;
import bak.pcj.adapter.LongSetToSetAdapter;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.datastore.server.snomed.index.InitialReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.reasoner.server.classification.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeProcessor;
import com.b2international.snowowl.snomed.reasoner.server.diff.relationship.StatementFragmentOrdering;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

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

		/**
		 * Special group number indicating that the next free group number
		 * should be used when the fragments in this group are converted into
		 * relationships.
		 */
		private static final byte NUMBER_NOT_PRESERVED = -1;

		private final byte groupNumber;
		private final List<UnionGroup> unionGroups;

		/**
		 * Creates a new group instance where the group number is not preserved.
		 *
		 * @param unionGroups
		 *            the relationship union groups to associate with this group
		 *            (may not be <code>null</code>)
		 */
		public Group(final Iterable<UnionGroup> unionGroups) {
			this(NUMBER_NOT_PRESERVED, unionGroups);
		}

		public Group(final byte groupNumber, final Iterable<UnionGroup> unionGroups) {
			this(groupNumber, unionGroups, new SkippingByteIterator());
		}

		/**
		 * Creates a new group instance with the specified parameters,
		 * preserving the group number for later reference.
		 *
		 * @param groupNumber
		 *            the group identifier (must be non-negative)
		 *
		 * @param unionGroups
		 *            the relationship union groups to associate with this group
		 *            (may not be <code>null</code>)
		 */
		public Group(final byte groupNumber, final Iterable<UnionGroup> unionGroups, final SkippingByteIterator unionGroupIterator) {
			checkArgument(groupNumber >= NUMBER_NOT_PRESERVED, "illegal groupNumber: %s.", groupNumber);
			checkArgument(unionGroups != null, "unionGroups is null.");

			this.groupNumber = groupNumber;
			this.unionGroups = ImmutableList.copyOf(unionGroups);

			for (final UnionGroup unionGroup : unionGroups) {
				if (unionGroup.isGroupNumberPreserved()) {
					unionGroupIterator.skip(unionGroup.getUnionGroupNumber());
				}
			}
		}

		public byte getGroupNumber() {
			return groupNumber;
		}

		public List<UnionGroup> getUnionGroups() {
			return unionGroups;
		}

		public boolean isGroupNumberPreserved() {
			return groupNumber != NUMBER_NOT_PRESERVED;
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
		public String toString() {

			return MessageFormat.format("number: {0}, union groups: {1}",
					isGroupNumberPreserved() ? getGroupNumber() : "?", unionGroups);
		}
	}

	private static final class UnionGroup implements SemanticComparable<UnionGroup> {

		private static final byte NUMBER_NOT_PRESERVED = -1;

		private final byte unionGroupNumber;
		private final List<RelationshipFragment> fragments;

		/**
		 * Creates a new union group instance with the specified parameters,
		 * preserving the union group number for later reference.
		 *
		 * @param unionGroupNumber
		 *            the union group identifier (must be non-negative)
		 *
		 * @param fragments
		 *            the relationship fragments to associate with this union
		 *            group (may not be <code>null</code>)
		 */
		public UnionGroup(final byte unionGroupNumber, final Iterable<RelationshipFragment> fragments) {
			checkArgument(unionGroupNumber >= NUMBER_NOT_PRESERVED, "illegal unionGroupNumber: %s.", unionGroupNumber);
			checkArgument(fragments != null, "fragments is null.");

			this.unionGroupNumber = unionGroupNumber;
			this.fragments = ImmutableList.copyOf(fragments);
		}

		public byte getUnionGroupNumber() {
			return unionGroupNumber;
		}

		public List<RelationshipFragment> getRelationshipFragments() {
			return fragments;
		}

		public boolean isGroupNumberPreserved() {
			return unionGroupNumber != NUMBER_NOT_PRESERVED;
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
		public String toString() {

			return MessageFormat.format("number: {0}, fragments: {1}",
					isGroupNumberPreserved() ? getUnionGroupNumber() : "?", fragments);
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

		/**
		 * Creates a new relationship fragment from the specified relationship.
		 *
		 * @param fragment
		 *            the relationship to extract attribute and value from (may
		 *            not be <code>null</code>)
		 *
		 * @throws NullPointerException
		 *             if the given relationship is <code>null</code>
		 */
		public RelationshipFragment(final StatementFragment fragment) {
			this.fragment = checkNotNull(fragment, "fragment");
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

		@Override
		public boolean equals(final Object obj) {

			if (this == obj) {
				return true;
			}

			if (!(obj instanceof RelationshipFragment)) {
				return false;
			}

			final RelationshipFragment other = (RelationshipFragment) obj;

			return (isUniversal() == other.isUniversal()) &&
					(isDestinationNegated() == other.isDestinationNegated()) &&
					(getTypeId() == other.getTypeId()) &&
					(getDestinationId() == other.getDestinationId());
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(isUniversal(), isDestinationNegated(), getTypeId(), getDestinationId());
		}

		@Override
		public String toString() {
			return MessageFormat.format("{0} : {3} {1} ({2})", getTypeId(), getDestinationId(), isUniversal(), isDestinationNegated() ? "NOT" : "");
		}
	}

	/**
	 * Represents a set of groups that do not allow redundant elements.
	 *
	 */
	private static final class GroupSet extends AbstractSet<Group> {

		private final List<Group> delegate = Lists.newArrayList();

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

			for (final Group existingGroup : delegate) {
				if (existingGroup.isSameOrStrongerThan(e)) {
					return false;
				} else if (e.isSameOrStrongerThan(existingGroup)) {
					redundant.add(existingGroup);
				}
			}

			delegate.removeAll(redundant);
			delegate.add(e);

			return true;
		}

		@Override
		public Iterator<Group> iterator() {
			return delegate.iterator();
		}

		@Override
		public int size() {
			return delegate.size();
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipNormalFormGenerator.class);

	private static final long IS_A_LONG = Long.valueOf(Concepts.IS_A);

	private final LongKeyMap generatedNonIsACache = new LongKeyOpenHashMap();

	/**
	 * Creates a new distribution normal form generator instance.
	 *
	 * @param reasonerTaxonomy
	 *            the reasoner to extract results from (may not be
	 *            <code>null</code>)
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
	 *
	 * <li>The given concept's <i>direct supertypes</i> are collected from the
	 * terminology browser, and IS_A relationships are created for all of them
	 * (assuming the terminology browser only links concepts where the
	 * corresponding relationship, object, attribute and value concepts are
	 * active);
	 *
	 * <li>The <i>given concept and all of its ancestors</i> are gathered from
	 * the terminology browser; the outbound relationship set reachable from
	 * these concepts is reduced to relationships which are active and non-IS_A;
	 * this set is further reduced to contain only non-redundant relationships
	 * with appropriate group numbers;
	 *
	 * <li>The relationships representing IS_A connections to direct supertypes
	 * and the non-redundant relationships are returned after being copied into
	 * an immutable list.
	 *
	 * </ol>
	 *
	 * @return a collection of outbound relationships for the specified
	 *         concept in distribution normal form
	 */
	@Override
	public Collection<StatementFragment> getGeneratedComponents(final long conceptId) {
		final LongSet directSuperTypes = reasonerTaxonomy.getParents(conceptId);

		// Step 1: create IS-A relationships
		final Iterable<StatementFragment> filteredIsARelationships = getFilteredIsARelationships(conceptId, directSuperTypes);

		// Step 2: get all non IS-A relationships from ancestors and remove redundancy, then cache the results for later use
		final LongKeyMap otherNonIsAFragments = new LongKeyOpenHashMap();

		for (final LongIterator itr = directSuperTypes.iterator(); itr.hasNext(); /* empty */) {
			final long directSuperTypeId = itr.next();
			otherNonIsAFragments.put(directSuperTypeId, getCachedNonIsAFragments(directSuperTypeId));
		}

		final Collection<StatementFragment> ownNonIsaFragments = reasonerTaxonomyBuilder.getNonIsAFragments(conceptId);
		final Iterable<StatementFragment> filteredNonIsARelationships = getDisjointGroups(conceptId, ownNonIsaFragments, otherNonIsAFragments);
		generatedNonIsACache.put(conceptId, filteredNonIsARelationships);

		// Step 3: concatenate and return
		return ImmutableList.copyOf(Iterables.concat(filteredIsARelationships, filteredNonIsARelationships));
	}

	@SuppressWarnings("unchecked")
	private Collection<StatementFragment> getCachedNonIsAFragments(final long directSuperTypeId) {
		return (Collection<StatementFragment>) generatedNonIsACache.get(directSuperTypeId);
	}

	private Iterable<StatementFragment> getFilteredIsARelationships(final long conceptId, final LongSet parentIds) {
		final Function<Long, StatementFragment> superTypeRelationshipFactory = new Function<Long, StatementFragment>() {
			@Override
			public StatementFragment apply(final Long parentId) {
				return createStatementFragment(-1L, -1L, IS_A_LONG, parentId.longValue(), (byte)0, (byte)0, false, false);
			}
		};

		return Iterables.transform(new LongSetToSetAdapter(parentIds), superTypeRelationshipFactory);
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

	/**
	 * Filters grouped relationships so that the returned Iterable only contains
	 * relationships for groups that are not redundant with respect to each
	 * other.
	 * <p>
	 * The returned Iterable is backed by an immutable data structure;
	 * <code>remove()</code> is not available.
	 *
	 * @param sourceId
	 *            the concept to build relationships for
	 *
	 * @param nonIsARelationships
	 *            the relationships which have a source concept that is a member
	 *            of the set returned by
	 *            {@link #getConceptAndAllSuperTypes(Long)}
	 *
	 * @return an {@link Iterable} containing the reduced set of relationships
	 */
	private Iterable<StatementFragment> getDisjointGroups(final long sourceId,
			final Collection<StatementFragment> ownNonIsAFragments,
			final LongKeyMap otherNonIsAFragments) {

		final GroupSet groups = new GroupSet();
		final SkippingByteIterator groupIterator = new SkippingByteIterator();
		final SkippingByteIterator unionGroupIterator = new SkippingByteIterator();

		final Iterable<Group> ownGroups = createGroupsForObject(true, groupIterator, unionGroupIterator, ownNonIsAFragments);
		Iterables.addAll(groups, ownGroups);

		for (final LongIterator itr = otherNonIsAFragments.keySet().iterator(); itr.hasNext(); /* empty */) {
			final long parentId = itr.next();
			final Iterable<Group> otherGroups = createGroupsForObject(false, groupIterator, unionGroupIterator, (Collection<StatementFragment>) otherNonIsAFragments.get(parentId));
			Iterables.addAll(groups, otherGroups);
		}

		final Iterable<Iterable<Iterable<StatementFragment>>> results = Iterables.transform(groups, new Function<Group, Iterable<Iterable<StatementFragment>>>() {

			@Override
			public Iterable<Iterable<StatementFragment>> apply(final Group group) {

				final byte groupId = group.isGroupNumberPreserved()
						? group.getGroupNumber()
						: groupIterator.nextByte();

				return Iterables.transform(group.getUnionGroups(), new Function<UnionGroup, Iterable<StatementFragment>>() {

					@Override
					public Iterable<StatementFragment> apply(final UnionGroup unionInput) {

						final byte unionGroupId = unionInput.isGroupNumberPreserved()
								? unionInput.getUnionGroupNumber()
								: unionGroupIterator.nextByte();

						return Iterables.transform(unionInput.getRelationshipFragments(), new Function<RelationshipFragment, StatementFragment>() {

							@Override
							public StatementFragment apply(final RelationshipFragment input) {

								return createStatementFragment(input.getStatementId(),
										input.getStorageKey(),
										input.getTypeId(),
										input.getDestinationId(),
										groupId,
										unionGroupId,
										input.isUniversal(),
										input.isDestinationNegated());
							}
						});
					};
				});
			}
		});

		return ImmutableList.copyOf(Iterables.concat(Iterables.concat(results)));
	}

	/**
	 * Extracts group information for the given relationships. All supplied
	 * relationships must have the same object concept, as relationship group
	 * numbers are only locally unique; the result is otherwise undefined.
	 *
	 * @param sourceOwned
	 *            <code>true</code> if the object of the specified relationships
	 *            is equal to the source concept for which normal form
	 *            relationships are generated (group numbers need to be
	 *            preserved in this case), <code>false</code> otherwise
	 *
	 * @param groupNumberIterator
	 *            the group iterator to configure (used for skipping
	 *            source-owned relationship group numbers)
	 *
	 * @param ownNonIsARelationshipFragments
	 *            the relationships to process
	 *
	 * @return an {@link Iterable} of Groups that store the appropriate
	 *         relationships fragments with an optionally preserved
	 */
	private Iterable<Group> createGroupsForObject(final boolean sourceOwned, final SkippingByteIterator groupNumberIterator,
			final SkippingByteIterator zeroGroupUnionGroupIterator, final Collection<StatementFragment> ownNonIsARelationshipFragments) {

		final Map<Byte, Collection<StatementFragment>> relationshipsByGroupId = getRelationshipsByGroupId(ownNonIsARelationshipFragments).asMap();

		final Map<Byte, Collection<Group>> groupsByGroupId = Maps.transformEntries(relationshipsByGroupId,
				new EntryTransformer<Byte, Collection<StatementFragment>, Collection<Group>>() {

					@Override
					public Collection<Group> transformEntry(final Byte key, final Collection<StatementFragment> value) {

						if (key == 0) {
							return ImmutableList.copyOf(transformZeroGroups(value));
						} else {
							return ImmutableList.of(transformNonZeroGroup(key, value));
						}
					}

					private Iterable<Group> transformZeroGroups(final Collection<StatementFragment> values) {

						// Relationships in group 0 are considered separate groups
						return Iterables.concat(Collections2.transform(values, new Function<StatementFragment, Iterable<Group>>() {
							@Override
							public Iterable<Group> apply(final StatementFragment input) {

								final Iterable<UnionGroup> unionGroups = getUnionGroups(sourceOwned, values);
								final Iterable<UnionGroup> disjointUnionGroups = getDisjointComparables(unionGroups);

								return Iterables.transform(disjointUnionGroups, new Function<UnionGroup, Group>() {
									@Override
									public Group apply(@Nullable final UnionGroup input) {
										return new Group((byte)0, ImmutableList.of(input), zeroGroupUnionGroupIterator);
									}
								});
							}
						}));
					}

					private Group transformNonZeroGroup(final Byte key, final Collection<StatementFragment> values) {

						final Iterable<UnionGroup> unionGroups = getUnionGroups(sourceOwned, values);
						final Iterable<UnionGroup> disjointUnionGroups = getDisjointComparables(unionGroups);

						if (sourceOwned) {
							groupNumberIterator.skip(key);
							return new Group(key, disjointUnionGroups);
						} else {
							return new Group(disjointUnionGroups);
						}
					}
				});

		return Iterables.concat(groupsByGroupId.values());
	}

	private Multimap<Byte, StatementFragment> getRelationshipsByGroupId(final Iterable<StatementFragment> relationshipsForObject) {

		final ImmutableMultimap.Builder<Byte, StatementFragment> relationshipsByGroupId = ImmutableMultimap.builder();

		for (final StatementFragment rmini : relationshipsForObject) {
			relationshipsByGroupId.put(rmini.getGroup(), rmini);
		}

		return relationshipsByGroupId.build();
	}

	private Iterable<UnionGroup> getUnionGroups(final boolean sourceOwned, final Iterable<StatementFragment> relationshipsForGroup) {

		final Multimap<Byte, RelationshipFragment> fragmentsByUnionGroupId = HashMultimap.create();
		final List<UnionGroup> result = Lists.newArrayList();

		for (final StatementFragment rmini : relationshipsForGroup) {

			if (rmini.getUnionGroup() == 0) {
				// Union groups should be in non-zero groups; goes straight into the result list
				result.add(new UnionGroup((byte)0, ImmutableList.of(new RelationshipFragment(rmini))));
			} else {
				fragmentsByUnionGroupId.put(rmini.getUnionGroup(), new RelationshipFragment(rmini));
			}
		}

		for (final byte group : fragmentsByUnionGroupId.keySet()) {

			result.add(new UnionGroup(
						sourceOwned ? group : UnionGroup.NUMBER_NOT_PRESERVED,
						fragmentsByUnionGroupId.get(group)));
		}

		return result;
	}

	/**
	 * Filters {@link SemanticComparable}s so that the returned Iterable only
	 * includes elements that are not redundant with respect to each other. The
	 * following steps are taken to ensure that no redundant SemanticComparables
	 * remain in the output Iterable:
	 *
	 * <ol>
	 *
	 * <li>a candidate set is maintained for possible results;
	 *
	 * <li>each incoming item is checked against all existing candidates to see
	 * if they are redundant (in which case the incoming item is skipped), or if
	 * it makes any of the candidates redundant (in which case the redundant
	 * candidates are removed from the set, and the incoming item gets added);
	 *
	 * <li>all surviving items are returned.
	 *
	 * </ol>
	 *
	 * <p>
	 *
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

	/**
	 * Creates a new {@link StatementFragment} instance with the specified arguments. The created instance has no
	 * identifier, is un-published, active and inferred by default. If there's an existing relationship with the same
	 * triple and group given, it will take precedence during the comparison.
	 *
	 * @param originatingRelationshipId the identifier of the relationship that originally captured the information in
	 * this relationship (may be {@code -1L} to indicate a relationship without a source)
	 * @oaram storageKey
	 * @param objectId the object concept
	 * @param attributeId the attribute concept
	 * @param valueId the value concept
	 * @param l
	 * @param group the group number
	 * @return the created instance
	 */
	private StatementFragment createStatementFragment(final long originatingRelationshipId, final long storageKey, final long attributeId, final long valueId,
			final byte group, final byte unionGroup, final boolean isUniversal, final boolean destinationNegated) {

		return new StatementFragment(originatingRelationshipId, storageKey, valueId, attributeId, destinationNegated, isUniversal, group, unionGroup);
	}

	public final int collectNormalFormChanges(final IProgressMonitor monitor, final OntologyChangeProcessor<StatementFragment> processor) {
		LOGGER.info(">>> Relationship normal form generation");
		final Stopwatch stopwatch = Stopwatch.createStarted();
		final int results = collectNormalFormChanges(monitor, processor, StatementFragmentOrdering.INSTANCE);
		LOGGER.info(MessageFormat.format("<<< Relationship normal form generation [{0}]", stopwatch.toString()));
		return results;
	}
}
