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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.Searcher;
import com.b2international.index.Writer;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.b2international.index.revision.RevisionCompare.Builder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.7
 */
public final class DefaultRevisionIndex implements InternalRevisionIndex, Hooks {

	private static final String SCROLL_KEEP_ALIVE = "2m";
	
	private static final int PURGE_LIMIT = 100_000;
	
	private static final int COMPARE_DEFAULT_LIMIT = 100_000;
	
	private final Index index;
	private final BaseRevisionBranching branching;
	private final RevisionIndexAdmin admin;
	private final ObjectMapper mapper;
	private final List<Hooks.Hook> hooks = newArrayList();

	public DefaultRevisionIndex(Index index, TimestampProvider timestampProvider, ObjectMapper mapper) {
		this.index = index;
		this.mapper = mapper;
		this.admin = new RevisionIndexAdmin(this, index.admin());
		this.branching = new DefaultRevisionBranching(this, timestampProvider, mapper);
	}
	
	@Override
	public RevisionIndexAdmin admin() {
		return admin;
	}
	
	@Override
	public String name() {
		return index.name();
	}
	
	@Override
	public Index index() {
		return index;
	}
	
	@Override
	public <T> T read(final String branchPath, final RevisionIndexRead<T> read) {
		if (RevisionIndex.isBranchAtPath(branchPath)) {
			String[] branchAndTimestamp = branchPath.split(RevisionIndex.AT_CHAR);
			checkArgument(branchAndTimestamp.length == 2, "Invalid <branch>@<timestamp> expression. Got: %s.", branchPath);
			String branch = branchAndTimestamp[0];
			long timestamp = Long.parseLong(branchAndTimestamp[1]);
			checkArgument(timestamp >= 0, "Timestamp argument of <branch>@<timestamp> expression must be greater than or equal to zero.");
			// create an alternative ref that only contains segments up until the specified timestamp
			final RevisionBranchRef ref = getBranchRef(branch).restrictTo(timestamp);
			return read(ref, read);
		} else if (RevisionIndex.isBaseRefPath(branchPath)) {
			final String branchPathWithoutBaseRef = branchPath.substring(0, branchPath.length() - 1);
			if (RevisionBranch.MAIN_PATH.equals(branchPathWithoutBaseRef)) {
				throw new IllegalArgumentException("Cannot query base of MAIN branch");
			}
			return read(getBaseRef(branchPathWithoutBaseRef), read);
		} else if (RevisionIndex.isRevRangePath(branchPath)) {
			final String[] branches = RevisionIndex.getRevisionRangePaths(branchPath);
			final String basePath = branches[0];
			final String comparePath = branches[1];
			final RevisionBranchRef base = getBranchRef(basePath);
			final RevisionBranchRef compare = getBranchRef(comparePath);
			return read(compare.difference(base), read);
		} else {
			return read(getBranchRef(branchPath), read);
		}
	}
	
	@Override
	public <T> T read(final RevisionBranchRef branch, final RevisionIndexRead<T> read) {
		return index.read(index -> read.execute(new DefaultRevisionSearcher(branch, index)));
	}
	
	/**
	 * Writes to this index via an {@link RevisionIndexWrite write transaction}.
	 * 
	 * @param branchPath
	 *            - put all modifications to this branch
	 * @param commitTimestamp
	 *            - all modifications should appear with this timestamp
	 * @param write
	 *            - transactional write operation
	 * @return
	 */
	<T> T write(final String branchPath, final long commitTimestamp, final RevisionIndexWrite<T> write) {
		if (branchPath.endsWith(BASE_REF_CHAR)) {
			throw new IllegalArgumentException(String.format("It is illegal to modify a branch's base point (%s).", branchPath));
		}
		return index.write(index -> {
			final RevisionBranchRef branch = getBranchRef(branchPath);
			final RevisionWriter writer = new DefaultRevisionWriter(branching, branch, commitTimestamp, index, new DefaultRevisionSearcher(branch, index.searcher()));
			return write.execute(writer);
		});
	}
	
	@Override
	public RevisionCompare compare(final String branch) {
		return compare(branch, COMPARE_DEFAULT_LIMIT);
	}
	
	@Override
	public RevisionCompare compare(final String branch, final int limit) {
		return compare(getBaseRef(branch), getBranchRef(branch), limit);
	}
	
	@Override
	public RevisionCompare compare(final String baseBranch, final String compareBranch) {
		return compare(baseBranch, compareBranch, COMPARE_DEFAULT_LIMIT);
	}
	
	@Override
	public RevisionCompare compare(final String baseBranch, final String compareBranch, final int limit) {
		return compare(getBranchRef(baseBranch), getBranchRef(compareBranch), limit);
	}
	
	@Override
	public RevisionCompare compare(final RevisionBranchRef base, final RevisionBranchRef compare, final int limit) {
		return index.read(searcher -> {
			
			final RevisionBranchRef baseOfCompareRef = base.intersection(compare);
			final RevisionBranchRef compareRef = compare.difference(base);

			final Builder result = RevisionCompare.builder(baseOfCompareRef, compareRef);
			
			if (base.branchId() != compare.branchId()) {
				doRevisionCompare(searcher, compareRef, result, limit);
			}

			return result.build();
		});
	}
	
	private void doRevisionCompare(Searcher searcher, RevisionBranchRef compareRef, RevisionCompare.Builder result, int limit) throws IOException {
		if (compareRef.segments().isEmpty()) {
			return;
		}
		ExpressionBuilder compareCommitsQuery = Expressions.builder();
		
		for (RevisionSegment segment : compareRef.segments()) {
			String segmentBranch = getBranchPath(searcher, segment.branchId());
			compareCommitsQuery.should(Expressions.builder()
					.filter(Commit.Expressions.timestampRange(segment.start(), segment.end()))
					.filter(Commit.Expressions.branches(Collections.singleton(segmentBranch)))
					.build());
		}
		
		// apply commits happened on the compareRef segments in chronological order 
		searcher.search(Query.select(Commit.class)
				.where(compareCommitsQuery.build())
				.limit(Integer.MAX_VALUE)
				.sortBy(SortBy.field(Commit.Fields.TIMESTAMP, Order.ASC))
				.build())
				.forEach(result::apply);
	}

	private String getBranchPath(Searcher searcher, long branchId) throws IOException {
		return searcher.search(Query.select(String.class)
				.from(RevisionBranch.class)
				.fields(RevisionBranch.Fields.PATH)
				.where(Expressions.exactMatch(RevisionBranch.Fields.ID, branchId))
				.limit(1)
				.build())
				.stream()
				.findFirst()
				.get();
	}

	@Override
	public void purge(final String branchPath, final Purge purge) {
		final RevisionBranchRef branch = getBranchRef(branchPath);
		index.write(index -> {
			// TODO support selective type purging
			final Set<Class<? extends Revision>> typesToPurge = getRevisionTypes();
			
			switch (purge) {
			case ALL: 
				purge(index, branch, typesToPurge);
				break;
			case HISTORY:
				purge(index, branch.historyRef(), typesToPurge);
				break;
			case LATEST:
				purge(index, branch.lastRef(), typesToPurge);
				break;
			default: throw new UnsupportedOperationException("Unsupported purge: " + purge);
			}
			return null;
		});
	}
	
	private void purge(Writer writer, final RevisionBranchRef refToPurge, Set<Class<? extends Revision>> typesToPurge) throws IOException {
		// if nothing to purge return
		if (typesToPurge.isEmpty() || refToPurge.isEmpty()) {
			return;
		}
		
		final Searcher searcher = writer.searcher();
		
		final ExpressionBuilder purgeQuery = Expressions.builder();
		// purge only documents added to the selected branch
		for (RevisionSegment segmentToPurge : refToPurge.segments()) {
			purgeQuery.should(Expressions.builder()
				.filter(segmentToPurge.toRangeExpression(Revision.Fields.CREATED))
				.filter(segmentToPurge.toRangeExpression(Revision.Fields.REVISED))
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
	
	@Override
	public BaseRevisionBranching branching() {
		return branching;
	}
	
	@Override
	public StagingArea prepareCommit(String branchPath) {
		return new StagingArea(this, branchPath, mapper);
	}
	
	@Override
	public List<Commit> history(String id) {
		return index.read(searcher -> {
			return searcher.search(Query.select(Commit.class)
					.where(Commit.Expressions.affectedObject(id))
					.sortBy(SortBy.field(Commit.Fields.TIMESTAMP, Order.DESC))
					.limit(Integer.MAX_VALUE)
					.build())
					.getHits();
		});
	}
	
	@Override
	public Hooks hooks() {
		return this;
	}
	
	@Override
	public void addHook(Hook hook) {
		if (!this.hooks.contains(hook)) {
			this.hooks.add(hook);
		}
	}
	
	@Override
	public void removeHook(Hook hook) {
		this.hooks.remove(hook);
	}
	
	/**
	 * Returns the currently registered {@link List} of {@link Hook}s.
	 * @return
	 */
	List<Hooks.Hook> getHooks() {
		return ImmutableList.copyOf(hooks);
	}

	private RevisionBranchRef getBranchRef(final String branchPath) {
		return getBranch(branchPath).ref();
	}

	private RevisionBranchRef getBaseRef(final String branchPath) {
		return getBranch(branchPath).baseRef();
	}
	
	private RevisionBranch getBranch(final String branchPath) {
		return branching.getBranch(branchPath);
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
