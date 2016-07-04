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
package com.b2international.index.revision;

import static com.b2international.index.query.Expressions.matchAnyInt;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.IndexRead;
import com.b2international.index.IndexWrite;
import com.b2international.index.Searcher;
import com.b2international.index.Writer;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.compare.RevisionCompare;
import com.b2international.index.revision.compare.RevisionCompare.Builder;
import com.google.common.collect.Sets;

/**
 * @since 4.7
 */
public final class DefaultRevisionIndex implements RevisionIndex {

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
		return index.read(new IndexRead<T>() {
			@Override
			public T execute(Searcher index) throws IOException {
				final RevisionBranch branch = getBranch(branchPath);
				return read.execute(new DefaultRevisionSearcher(branch, index));
			}
		});
	}
	
	@Override
	public <T> T write(final String branchPath, final long commitTimestamp, final RevisionIndexWrite<T> write) {
		return index.write(new IndexWrite<T>() {
			@Override
			public T execute(Writer index) throws IOException {
				final RevisionBranch branch = getBranch(branchPath);
				final RevisionWriter writer = new DefaultRevisionWriter(branch, commitTimestamp, index, new DefaultRevisionSearcher(branch, index.searcher()));
				return write.execute(writer);
			}
		});
	}
	
	@Override
	public RevisionCompare compare(final String baseBranch, final String compareBranch) {
		return index.read(new IndexRead<RevisionCompare>() {
			@Override
			public RevisionCompare execute(Searcher searcher) throws IOException {
				final RevisionBranch base = getBranch(baseBranch);
				final RevisionBranch compare = getBranch(compareBranch);
				
				final Set<Integer> commonPath = Sets.intersection(compare.segments(), base.segments());
				final Set<Integer> segmentsToCompare = Sets.difference(compare.segments(), base.segments());
				
				final Set<Class<? extends Revision>> typesToCompare = getRevisionTypes();
				final Builder result = RevisionCompare.builder();
				
				final Map<Class<? extends Revision>, LongSet> newAndChangedComponents = newHashMap();
				final Map<Class<? extends Revision>, LongSet> deletedAndChangedComponents = newHashMap();
				
				// query all registered revision types for new, changed and deleted components
				for (Class<? extends Revision> typeToCompare : typesToCompare) {
					final Query<? extends Revision> newAndChangedQuery = Query.builder(typeToCompare)
							.selectAll()
							.where(Revision.branchSegmentFilter(segmentsToCompare))
							.limit(Integer.MAX_VALUE)
							.build();
					final Hits<? extends Revision> newAndChangedHits = searcher.search(newAndChangedQuery);
					final LongSet newAndChangedKeys = PrimitiveSets.newLongOpenHashSet();
					for (Revision newOrChangedHit : newAndChangedHits) {
						newAndChangedKeys.add(newOrChangedHit.getStorageKey());
					}
					newAndChangedComponents.put(typeToCompare, newAndChangedKeys);
					
					// any revision counts as changed or deleted which has segmentID in the common path, but replaced in the compared path
					final Query<? extends Revision> deletedAndChangedQuery = Query.builder(typeToCompare)
							.selectAll()
							.where(Expressions.builder()
									.must(matchAnyInt(Revision.SEGMENT_ID, commonPath))
									.must(matchAnyInt(Revision.REPLACED_INS, segmentsToCompare))
									.build())
							.limit(Integer.MAX_VALUE)
							.build();
					final Hits<? extends Revision> deletedAndChangedHits = searcher.search(deletedAndChangedQuery);
					final LongSet deletedAndChangedKeys = PrimitiveSets.newLongOpenHashSet();
					for (Revision deletedOrChangedHit : deletedAndChangedHits) {
						deletedAndChangedKeys.add(deletedOrChangedHit.getStorageKey());
					}
					deletedAndChangedComponents.put(typeToCompare, deletedAndChangedKeys);
				}
				
				for (Class<? extends Revision> typeToCompare : typesToCompare) {
					final LongSet newAndChangedKeys = newAndChangedComponents.get(typeToCompare);
					final LongSet deletedAndChangedKeys = deletedAndChangedComponents.get(typeToCompare);
					LongIterator newAndChangedIterator = newAndChangedKeys.iterator();
					while (newAndChangedIterator.hasNext()) {
						final long newOrChangedKey = newAndChangedIterator.next();
						if (deletedAndChangedKeys.contains(newOrChangedKey)) {
							// CHANGED
							result.changedRevision(typeToCompare, newOrChangedKey);
						} else {
							// NEW
							result.newRevision(typeToCompare, newOrChangedKey);
						}
					}
					
					LongIterator deletedAndChangedIterator = deletedAndChangedKeys.iterator();
					while (deletedAndChangedIterator.hasNext()) {
						final long deletedOrChangedKey = deletedAndChangedIterator.next();
						if (!newAndChangedKeys.contains(deletedOrChangedKey)) {
							// DELETED
							result.deletedRevision(typeToCompare, deletedOrChangedKey);
						}
					}
				}
				
				return result.build();
			}
		});
	}
	
	@Override
	public void purge(final String branchPath, final Purge purge) {
		final RevisionBranch branch = getBranch(branchPath);
		index.write(new IndexWrite<Void>() {
			@Override
			public Void execute(Writer index) throws IOException {
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
			}

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
		purgeQuery.must(Expressions.exactMatch(Revision.BRANCH_PATH, branchToPurge));
		for (Integer segmentToPurge : segmentsToPurge) {
			purgeQuery.should(Expressions.builder()
				.must(Expressions.match(Revision.SEGMENT_ID, segmentToPurge))
				.must(Expressions.match(Revision.REPLACED_INS, segmentToPurge))
				.build());
		}
		for (Class<? extends Revision> revisionType : typesToPurge) {
			// execute hit count query first
			final int totalRevisionsToPurge = searcher.search(Query.builder(revisionType)
					.selectAll().where(purgeQuery.build()).limit(0).build()).getTotal();
			if (totalRevisionsToPurge > 0) {
				admin().log().info("Purging {} '{}' documents...", totalRevisionsToPurge, DocumentMapping.getType(revisionType));
				// partition the total hit number by the current threshold
				final int limit = 10000;
				int offset = 0;
				do {
					final Hits<? extends Revision> revisionsToPurge = searcher.search(Query.builder(revisionType)
							.selectAll().where(purgeQuery.build()).offset(offset).limit(limit).build());
					
					for (Revision hit : revisionsToPurge) {
						writer.remove(revisionType, hit._id());
					}
					
					// register processed items in the offset, and check if we reached the limit, if yes break
					offset += limit;
					// commit the batch
					writer.commit();
				} while (offset <= totalRevisionsToPurge);
			}
		}
	}

	private RevisionBranch getBranch(final String branchPath) {
		return branchProvider.getBranch(branchPath);
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
