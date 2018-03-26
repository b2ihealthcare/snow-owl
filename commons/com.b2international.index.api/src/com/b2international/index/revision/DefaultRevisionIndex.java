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
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
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
	private static final int PURGE_LIMIT = 100_000;
	
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
		if (branchPath.endsWith(BASE_REF_CHAR)) {
			final String branchPathWithoutBaseRef = branchPath.substring(0, branchPath.length() - 1);
			if (RevisionBranch.MAIN_PATH.equals(branchPathWithoutBaseRef)) {
				throw new IllegalArgumentException("Cannot query base of MAIN branch");
			}
			final RevisionBranch parent = getParentBranch(branchPathWithoutBaseRef);
			final RevisionBranch branch = getBranch(branchPathWithoutBaseRef);
			final Set<Integer> commonPath = Sets.intersection(branch.segments(), parent.segments());
			final RevisionBranch baseOfBranch = new RevisionBranch(parent.path(), Ordering.natural().max(commonPath), commonPath);
			return read(baseOfBranch, read);
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
	public RevisionCompare compare(String branch) {
		return compare(getParentBranch(branch), getBranch(branch));
	}
	
	@Override
	public RevisionCompare compare(final String baseBranch, final String compareBranch) {
		return compare(getBranch(baseBranch), getBranch(compareBranch));
	}
	
	private RevisionCompare compare(final RevisionBranch base, final RevisionBranch compare) {
		return index.read(searcher -> {
			final Set<Integer> commonPath = Sets.intersection(compare.segments(), base.segments());
			final Set<Integer> segmentsToCompare = Sets.difference(compare.segments(), base.segments());
			final RevisionBranch baseOfCompareBranch = new RevisionBranch(base.path(), Ordering.natural().max(commonPath), commonPath);
			
			final Set<Class<? extends Revision>> typesToCompare = getRevisionTypes();
			final Builder result = RevisionCompare.builder(DefaultRevisionIndex.this, baseOfCompareBranch, compare);
			
			final Map<Class<? extends Revision>, LongSet> newOrChangedKeys = newHashMap();
			final LongKeyMap<String> newOrChangedHashes = PrimitiveMaps.newLongKeyOpenHashMap();
			final Map<Class<? extends Revision>, LongSet> deletedOrChangedKeys = newHashMap();
			final LongKeyMap<String> deletedOrChangedHashes = PrimitiveMaps.newLongKeyOpenHashMap();
			
			// query all registered revision types for new, changed and deleted components
			for (Class<? extends Revision> type : typesToCompare) {
				final LongSet newOrChangedKeysForType = newOrChangedKeys.computeIfAbsent(
						type, key -> PrimitiveSets.newLongOpenHashSet());
				
				final Query<String[]> newOrChangedQuery = Query
						.select(String[].class)
						.from(type)
						.fields(Revision.STORAGE_KEY, DocumentMapping._HASH)
						.where(Revision.branchSegmentFilter(segmentsToCompare))
						.scroll(SCROLL_KEEP_ALIVE)
						.limit(10000)
						.build();
				
				for (final Hits<String[]> newOrChangedHits : searcher.scroll(newOrChangedQuery)) {
					for (final String[] newOrChangedHit : newOrChangedHits) {
						final long storageKey = Long.parseLong(newOrChangedHit[0]);
						final String hash = newOrChangedHit[1];
						newOrChangedKeysForType.add(storageKey);
						if (hash != null) {
							newOrChangedHashes.put(storageKey, hash);
						}
					}
				}
				
				// any revision counts as changed or deleted which has segmentID in the common path, but replaced in the compared path
				final LongSet deletedOrChangedKeysForType = deletedOrChangedKeys.computeIfAbsent(
						type, key -> PrimitiveSets.newLongOpenHashSet());
				
				final Query<String[]> deletedOrChangedQuery = Query
						.select(String[].class)
						.from(type)
						.fields(Revision.STORAGE_KEY, DocumentMapping._HASH)
						.where(Expressions.builder()
								.filter(matchAnyInt(Revision.SEGMENT_ID, commonPath))
								.filter(matchAnyInt(Revision.REPLACED_INS, segmentsToCompare))
								.build())
						.scroll(SCROLL_KEEP_ALIVE)
						.limit(10000)
						.build();
				
				for (Hits<String[]> deletedOrChangedHits : searcher.scroll(deletedOrChangedQuery)) {
					for (String[] deletedOrChanged : deletedOrChangedHits) {
						final long storageKey = Long.parseLong(deletedOrChanged[0]);
						final String hash = deletedOrChanged[1];
						deletedOrChangedKeysForType.add(storageKey);
						if (hash != null) {
							deletedOrChangedHashes.put(storageKey, hash);
						}
					}
				}
			}
			
			for (Class<? extends Revision> type : typesToCompare) {
				final LongSet newOrChangedKeysForType = newOrChangedKeys.get(type);
				final LongSet deletedOrChangedKeysForType = deletedOrChangedKeys.get(type);
				
				for (LongIterator itr = newOrChangedKeysForType.iterator(); itr.hasNext(); /* empty */) {
					long newOrChangedStorageKey = itr.next();
					
					if (deletedOrChangedKeysForType.contains(newOrChangedStorageKey)) {
						// CHANGED, unless the hashes tell us otherwise
						if (!deletedOrChangedHashes.containsKey(newOrChangedStorageKey) 
								|| !newOrChangedHashes.containsKey(newOrChangedStorageKey) 
								|| !Objects.equals(deletedOrChangedHashes.get(newOrChangedStorageKey), newOrChangedHashes.get(newOrChangedStorageKey))) {
							
							result.changedRevision(type, newOrChangedStorageKey);
						}
					} else {
						// NEW
						result.newRevision(type, newOrChangedStorageKey);
					}
				}
				
				for (LongIterator itr = deletedOrChangedKeysForType.iterator(); itr.hasNext(); /* empty */) {
					long deletedOrChangedStorageKey = itr.next();
					
					if (!newOrChangedKeysForType.contains(deletedOrChangedStorageKey)) {
						// DELETED
						result.deletedRevision(type, deletedOrChangedStorageKey);
					}
				}
			}
			
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
