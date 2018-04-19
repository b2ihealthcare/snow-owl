/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import static com.b2international.index.query.Expressions.matchAnyInt;
import static com.b2international.index.query.Expressions.matchAnyLong;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.Searcher;
import com.b2international.index.Writer;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionCompare.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * @since 4.7
 */
public final class DefaultRevisionIndex implements InternalRevisionIndex {

	private static final String SCROLL_KEEP_ALIVE = "2m";
	
	private static final int SCROLL_LIMIT = 10_000;
	
	private static final int PURGE_LIMIT = 100_000;
	
	private static final int COMPARE_DEFAULT_LIMIT = 100_000;
	
	private final Index index;
	private final RevisionBranchProvider branchProvider;

	public DefaultRevisionIndex(Index index, RevisionBranchProvider branchProvider) {
		this.index = index;
		this.branchProvider = branchProvider;
	}
	
	@Override
	public IndexAdmin admin() {
		return index.admin();
	}
	
	@Override
	public String name() {
		return index.name();
	}
	
	@Override
	public <T> T read(final String branchPath, final RevisionIndexRead<T> read) {
		if (RevisionIndex.isBaseRefPath(branchPath)) {
			final String branchPathWithoutBaseRef = branchPath.substring(0, branchPath.length() - 1);
			if (RevisionBranch.MAIN_PATH.equals(branchPathWithoutBaseRef)) {
				throw new IllegalArgumentException("Cannot query base of MAIN branch");
			}
			final RevisionBranch parent = getParentBranch(branchPathWithoutBaseRef);
			final RevisionBranch branch = getBranch(branchPathWithoutBaseRef);
			final Set<Integer> commonPath = Sets.intersection(branch.segments(), parent.segments());
			final RevisionBranch baseOfBranch = new RevisionBranch(parent.path(), Ordering.natural().max(commonPath), commonPath);
			return read(baseOfBranch, read);
		} else if (RevisionIndex.isRevRangePath(branchPath)) {
			final String[] branches = RevisionIndex.getRevisionRangePaths(branchPath);
			final String basePath = branches[0];
			final String comparePath = branches[1];
			final RevisionBranch base = getBranch(basePath);
			final RevisionBranch compare = getBranch(comparePath);
			final Set<Integer> compareSegments = Sets.difference(compare.segments(), base.segments());
			return read(new RevisionBranch(comparePath, compare.segmentId(), compareSegments), read);
		} else {
			return read(getBranch(branchPath), read);
		}
	}
	
	@Override
	public <T> T read(final RevisionBranch branch, final RevisionIndexRead<T> read) {
		return index.read(index -> read.execute(new DefaultRevisionSearcher(branch, index)));
	}
	
	@Override
	public <T> T write(final String branchPath, final long commitTimestamp, final RevisionIndexWrite<T> write) {
		if (branchPath.endsWith(BASE_REF_CHAR)) {
			throw new IllegalArgumentException(String.format("It is illegal to modify a branch's base point (%s).", branchPath));
		}
		return index.write(index -> {
			final RevisionBranch branch = getBranch(branchPath);
			final RevisionWriter writer = new DefaultRevisionWriter(branch, commitTimestamp, index, new DefaultRevisionSearcher(branch, index.searcher()));
			return write.execute(writer);
		});
	}
	
	@Override
	public RevisionCompare compare(final String branch) {
		return compare(getParentBranch(branch), getBranch(branch), COMPARE_DEFAULT_LIMIT);
	}
	
	@Override
	public RevisionCompare compare(final String branch, final int limit) {
		return compare(getParentBranch(branch), getBranch(branch), limit);
	}
	
	@Override
	public RevisionCompare compare(final String baseBranch, final String compareBranch) {
		return compare(getBranch(baseBranch), getBranch(compareBranch), COMPARE_DEFAULT_LIMIT);
	}
	
	@Override
	public RevisionCompare compare(final String baseBranch, final String compareBranch, final int limit) {
		return compare(getBranch(baseBranch), getBranch(compareBranch), limit);
	}
	
	private RevisionCompare compare(final RevisionBranch base, final RevisionBranch compare, final int limit) {
		return index.read(searcher -> {
			final Set<Integer> commonPath = Sets.intersection(compare.segments(), base.segments());
			final Set<Integer> segmentsToCompare = Sets.difference(compare.segments(), base.segments());
			final RevisionBranch baseOfCompareBranch = new RevisionBranch(base.path(), Ordering.natural().max(commonPath), commonPath);

			final Set<Class<? extends Revision>> typesToCompare = getRevisionTypes();
			final Builder result = RevisionCompare.builder(DefaultRevisionIndex.this, 
					baseOfCompareBranch, 
					compare,
					limit);
			
			int added = 0;
			int changed = 0;
			int deleted = 0;

			LongSet newOrChangedKeys = PrimitiveSets.newLongOpenHashSet();
			LongKeyMap<String> newOrChangedHashes = PrimitiveMaps.newLongKeyOpenHashMap();
			LongSet deletedOrChangedKeys = PrimitiveSets.newLongOpenHashSet();
			// Don't need to keep track of deleted-or-changed hashes
			
			// query all registered revision types for new, changed and deleted components
			for (Class<? extends Revision> type : typesToCompare) {

				// The current storage key-hash pairs from the "compare" segments
				final Query<String[]> newOrChangedQuery = Query
						.select(String[].class)
						.from(type)
						.fields(Revision.STORAGE_KEY, DocumentMapping._HASH)
						.where(Revision.branchSegmentFilter(segmentsToCompare))
						.scroll(SCROLL_KEEP_ALIVE)
						.limit(SCROLL_LIMIT)
						.build();
				
				for (final Hits<String[]> newOrChangedHits : searcher.scroll(newOrChangedQuery)) {
					
					newOrChangedKeys.clear();
					newOrChangedHashes.clear();

					for (final String[] newOrChangedHit : newOrChangedHits) {
						final long storageKey = Long.parseLong(newOrChangedHit[0]);
						final String hash = newOrChangedHit[1];
						newOrChangedKeys.add(storageKey);
						if (hash != null) {
							newOrChangedHashes.put(storageKey, hash);
						}
					}
					
					/* 
					 * Create "dependent sub-query": try to find the same IDs in the "base" segments, which 
					 * will be either changed or "same" revisions from a compare point of view 
					 * (in case of a matching content hash value)
					 */
					final Query<String[]> changedOrSameQuery = Query
							.select(String[].class)
							.from(type)
							.fields(Revision.STORAGE_KEY, DocumentMapping._HASH)
							.where(Expressions.builder()
									.filter(matchAnyLong(Revision.STORAGE_KEY, LongSets.toList(newOrChangedKeys)))
									.filter(matchAnyInt(Revision.SEGMENT_ID, commonPath))
									.filter(matchAnyInt(Revision.REPLACED_INS, segmentsToCompare))
									.build())
							.scroll(SCROLL_KEEP_ALIVE)
							.limit(SCROLL_LIMIT)
							.build();
					
					for (Hits<String[]> changedOrSameHits : searcher.scroll(changedOrSameQuery)) {
						for (final String[] changedOrSameHit : changedOrSameHits) {
							final long storageKey = Long.parseLong(changedOrSameHit[0]);
							final String hash = changedOrSameHit[1];

							// CHANGED, unless the hashes tell us otherwise
							if (hash == null 
									|| !newOrChangedHashes.containsKey(storageKey)
									|| !Objects.equals(newOrChangedHashes.get(storageKey), hash)) {
								
								result.changedRevision(type, storageKey);
								changed++;
							}
							
							// Remove this storage key from newOrChanged, it is decidedly changed-or-same
							newOrChangedKeys.remove(storageKey);
							newOrChangedHashes.remove(storageKey);
						}
						
					} // changedOrSameHits
					
					// Everything remaining in newOrChanged is NEW, as it had no previous revision in the common segments
					for (LongIterator itr = newOrChangedKeys.iterator(); itr.hasNext(); /* empty */) {
						result.newRevision(type, itr.next());
						added++;
					}
					
					if (added > limit || changed > limit) {
						break;
					}
				
				} // newOrChangedHits

				// Revisions which existed on "base", but where replaced by another revision on "compare" segments
				final Query<String[]> deletedOrChangedQuery = Query
						.select(String[].class)
						.from(type)
						.fields(Revision.STORAGE_KEY)
						.where(Expressions.builder()
								.filter(matchAnyInt(Revision.SEGMENT_ID, commonPath))
								.filter(matchAnyInt(Revision.REPLACED_INS, segmentsToCompare))
								.build())
						.scroll(SCROLL_KEEP_ALIVE)
						.limit(SCROLL_LIMIT)
						.build();
				
				for (Hits<String[]> deletedOrChangedHits : searcher.scroll(deletedOrChangedQuery)) {
					
					deletedOrChangedKeys.clear();

					for (String[] deletedOrChanged : deletedOrChangedHits) {
						final long storageKey = Long.parseLong(deletedOrChanged[0]);
						deletedOrChangedKeys.add(storageKey);
					}
					
					/* 
					 * Create "dependent sub-query": try to find the same IDs in the "compare" segments,
					 * if they are present, the revision is definitely not deleted
					 */
					final Query<String[]> changedOrSameQuery = Query
							.select(String[].class)
							.from(type)
							.fields(Revision.STORAGE_KEY)
							.where(Expressions.builder()
									.filter(matchAnyLong(Revision.STORAGE_KEY, LongSets.toList(deletedOrChangedKeys)))
									.filter(Revision.branchSegmentFilter(segmentsToCompare))
									.build())
							.scroll(SCROLL_KEEP_ALIVE)
							.limit(SCROLL_LIMIT)
							.build();
					
					for (Hits<String[]> changedOrSameHits : searcher.scroll(changedOrSameQuery)) {
						for (final String[] changedOrSameHit : changedOrSameHits) {
							final long storageKey = Long.parseLong(changedOrSameHit[0]);
							
							// Remove this storage key from deletedOrChanged, it is decidedly still existing
							deletedOrChangedKeys.remove(storageKey);
						}
					}
					
					// Everything remaining in deletedOrChanged is DELETED, as it had successor in the "compare" segments
					for (LongIterator itr = deletedOrChangedKeys.iterator(); itr.hasNext(); /* empty */) {
						result.deletedRevision(type, itr.next());
						deleted++;
					}
					
					if (deleted > limit) {
						break;
					}
					
				} // deletedOrChangedHits
				
				if (added > limit || changed > limit || deleted > limit) {
					break;
				}
				
			} // type
			
			return result.build();
		});
	}
	
	@Override
	public void purge(final String branchPath, final Purge purge) {
		final RevisionBranch branch = getBranch(branchPath);
		index.write(index -> {
			// TODO support selective type purging
			final Set<Class<? extends Revision>> typesToPurge = getRevisionTypes();
			
			switch (purge) {
			case ALL: 
				purge(branch.path(), index, typesToPurge, branch.segments());
				break;
			case HISTORY:
				final Set<Integer> segmentsToPurge = newHashSet(branch.segments());
				segmentsToPurge.remove(branch.segmentId());
				purge(branch.path(), index, typesToPurge, segmentsToPurge);
				break;
			case LATEST:
				purge(branch.path(), index, typesToPurge, Collections.singleton(branch.segmentId()));
				break;
			default: throw new UnsupportedOperationException("Unsupported purge: " + purge);
			}
			return null;
		});
	}
	
	private void purge(final String branchToPurge, Writer writer, Set<Class<? extends Revision>> typesToPurge, Set<Integer> segmentsToPurge) throws IOException {
		// if nothing to purge return
		if (typesToPurge.isEmpty() || segmentsToPurge.isEmpty()) {
			return;
		}
		
		final Searcher searcher = writer.searcher();
		final ExpressionBuilder purgeQuery = Expressions.builder();
		// purge only documents added to the selected branch
		purgeQuery.filter(Expressions.exactMatch(Revision.BRANCH_PATH, branchToPurge));
		for (Integer segmentToPurge : segmentsToPurge) {
			purgeQuery.should(Expressions.builder()
				.filter(Expressions.match(Revision.SEGMENT_ID, segmentToPurge))
				.filter(Expressions.match(Revision.REPLACED_INS, segmentToPurge))
				.build());
		}
		for (Class<? extends Revision> revisionType : typesToPurge) {
			final String type = DocumentMapping.getType(revisionType);
			
			final Query<String> query = Query.select(String.class)
				.from(revisionType)
				.fields(DocumentMapping._ID)
				.where(purgeQuery.build())
				.scroll(SCROLL_KEEP_ALIVE) 
				.limit(PURGE_LIMIT)
				.build();
			
			int purged = 0;
			for (Hits<String> revisionsToPurge : searcher.scroll(query)) {
				purged += revisionsToPurge.getHits().size();
				admin().log().info("Purging {}/{} '{}' documents...", purged, revisionsToPurge.getTotal(), type);
				writer.removeAll(ImmutableMap.of(revisionType, newHashSet(revisionsToPurge)));
				writer.commit();
			}
			
		}
	}

	private RevisionBranch getBranch(final String branchPath) {
		return branchProvider.getBranch(branchPath);
	}
	
	private RevisionBranch getParentBranch(final String branchPath) {
		return branchProvider.getParentBranch(branchPath);
	}
	
	private Set<Class<? extends Revision>> getRevisionTypes() {
		final Set<Class<? extends Revision>> revisionTypes = newHashSet();
		for (DocumentMapping mapping : admin().mappings().getMappings()) {
			if (Revision.class.isAssignableFrom(mapping.type())) {
				revisionTypes.add((Class<? extends Revision>) mapping.type());
			}
		}
		return revisionTypes;
	}
	
}
