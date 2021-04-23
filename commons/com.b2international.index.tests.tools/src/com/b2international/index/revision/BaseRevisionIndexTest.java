/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Rule;

import com.b2international.commons.options.MetadataImpl;
import com.b2international.index.*;
import com.b2international.index.query.Query;
import com.b2international.index.util.Reflections;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public abstract class BaseRevisionIndexTest {
	
	protected static final String USER_ID = UUID.randomUUID().toString();
	protected static final String MAIN = RevisionBranch.MAIN_PATH;
	protected static final String STORAGE_KEY1 = "1";
	protected static final String STORAGE_KEY2 = "2";
	
	private AtomicLong storageKeys = new AtomicLong(3);
	
	private final Collection<Hooks.Hook> hooks = newArrayListWithCapacity(2);
	
	@Rule
	public final IndexResource index = IndexResource.create(getTypes(), this::configureMapper, this::getIndexSettings);

	@After
	public void after() {
		hooks.forEach(index().hooks()::removeHook);
	}
	
	/**
	 * Subclasses may override to provide additional mappings for the underlying index.
	 * @return
	 */
	protected Collection<Class<?>> getTypes() {
		return Collections.emptySet();
	}
	
	protected void configureMapper(ObjectMapper mapper) {
	}
	
	protected Map<String, Object> getIndexSettings() {
		// make sure we use the default settings for each tests (including max_result_window)
		// this also changes it back if a subclass has changed it for its tests
		return Map.of(
			IndexClientFactory.RESULT_WINDOW_KEY, ""+IndexClientFactory.DEFAULT_RESULT_WINDOW,
			IndexClientFactory.COMMIT_WATERMARK_LOW_KEY, IndexClientFactory.DEFAULT_COMMIT_WATERMARK_LOW_VALUE,
			IndexClientFactory.COMMIT_WATERMARK_HIGH_KEY, IndexClientFactory.DEFAULT_COMMIT_WATERMARK_HIGH_VALUE
		);
	}
	
	protected final String nextId() {
		return Long.toString(storageKeys.getAndIncrement());
	}
	
	protected String createBranch(String parent, String child) {
		return branching().createBranch(parent, child, new MetadataImpl());
	}
	
	protected long currentTime() {
		return ((DefaultRevisionBranching) branching()).currentTime();
	}

	protected final RevisionIndex index() {
		return index.getRevisionIndex();
	}
	
	protected final Index rawIndex() {
		return index.getIndex();
	}
	
	protected final ObjectMapper getMapper() {
		return index.getMapper();
	}
	
	protected BaseRevisionBranching branching() {
		return index().branching();
	}
	
	protected RevisionBranch getMainBranch() {
		return getBranch(MAIN);
	}
	
	protected final void withHook(Hooks.Hook hook) {
		hooks.add(hook);
		index().hooks().addHook(hook);
	}
	
	protected RevisionBranch getBranch(String branchPath) {
		return branching().getBranch(branchPath);
	}
	
	protected final void indexDocument(final Object doc) {
		rawIndex().write(index -> {
			index.put(doc);
			index.commit();
			return null;
		});
	}
	
	protected final <T extends Revision> T getRevision(final String branch, final Class<T> type, final String key) {
		return index().read(branch, index -> index.get(type, key));
	}
	
	protected final void indexRevision(final String branchPath, final Revision... revisions) {
		commit(branchPath, Arrays.asList(revisions));
	}
	
	protected final <T extends Revision> Commit indexChange(final String branchPath, final T oldRevision, final T newRevision) {
		final long commitTimestamp = currentTime();
		return index().prepareCommit(branchPath)
			.stageChange(oldRevision, newRevision)
			.commit(commitTimestamp, USER_ID, "Commit");
	}
	
	protected final void indexRemove(final String branchPath, final Revision...removedRevisions) {
		final long commitTimestamp = currentTime();
		StagingArea staging = index().prepareCommit(branchPath);
		Arrays.asList(removedRevisions).forEach(staging::stageRemove);
		staging
			.commit(commitTimestamp, USER_ID, "Commit");
	}

	protected final Commit commit(final String branchPath, final Iterable<? extends Revision> newRevisions) {
		final long commitTimestamp = currentTime();
		StagingArea staging = index().prepareCommit(branchPath);
		newRevisions.forEach(rev -> staging.stageNew(rev.getId(), rev));
		return staging.commit(commitTimestamp, USER_ID, "Commit");
	}
	
	protected final void deleteRevision(final String branchPath, final Class<? extends Revision> type, final String key) {
		final long commitTimestamp = currentTime();
		StagingArea staging = index().prepareCommit(branchPath);
		staging.stageRemove(key, getRevision(branchPath, type, key));
		staging.commit(commitTimestamp, USER_ID, "Commit");
	}
	
	protected final <T> Hits<T> search(final String branchPath, final Query<T> query) {
		return index().read(branchPath, index -> index.search(query));
	}
	
	protected final <T> Hits<T> searchRaw(final Query<T> query) {
		return rawIndex().read(index -> index.search(query));
	}
	
	protected void assertDocEquals(Object expected, Object actual) {
		assertNotNull("Actual document is missing from index", actual);
		for (Field f : index.getIndex().admin().mappings().getMapping(expected.getClass()).getFields()) {
			if (Revision.Fields.CREATED.equals(f.getName()) 
					|| Revision.Fields.REVISED.equals(f.getName())
					|| WithScore.SCORE.equals(f.getName())
					) {
				// skip revision fields from equality check
				continue;
			}
			assertEquals(String.format("Field '%s' should be equal", f.getName()), Reflections.getValue(expected, f), Reflections.getValue(actual, f));
		}
	}
	
}
