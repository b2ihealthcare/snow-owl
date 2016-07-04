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

import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

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
