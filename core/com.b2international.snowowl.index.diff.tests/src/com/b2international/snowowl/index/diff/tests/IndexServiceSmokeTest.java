/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.index.diff.tests;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.lucene.index.IndexCommit;
import org.junit.Before;
import org.junit.Test;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOBranchPath;
import com.b2international.snowowl.datastore.server.index.IndexBranchService;
import com.b2international.snowowl.index.diff.IndexDiff;
import com.b2international.snowowl.index.diff.IndexDifferFactory;
import com.b2international.snowowl.index.diff.tests.mock.DiffConcept;
import com.b2international.snowowl.index.diff.tests.mock.DiffIndexServerService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;

/**
 * @since 4.3
 */
public class IndexServiceSmokeTest {

	private static final IBranchPath MAIN = BranchPathUtils.createMainPath();
	private DiffIndexServerService service;

	@Before
	public void givenIndexService() {
		service = new DiffIndexServerService();
	}

	@Test
	public void indexSingleDoc() throws Exception {
		service.indexRelevantDocs(MAIN, "1");
		assertConceptIdsExactly(MAIN, "1");
	}

	@Test
	public void deleteSingleDoc() throws Exception {
		indexSingleDoc();
		service.deleteDocs(MAIN, 1L);
		assertConceptIdsExactly(MAIN);
	}

	@Test
	public void deleteOneDocOfTwo() throws Exception {
		service.indexRelevantDocs(MAIN, "1", "2");
		assertEquals("Index should have two documents after adding concepts.", 2, service.getAllDocsCount(MAIN));
		service.deleteDocs(MAIN, 1L);
		assertConceptIdsExactly(MAIN, "2");
	}

	@Test
	public void updateDoc() throws Exception {
		service.indexRelevantDocs(MAIN, "1", "2");
		assertConceptIdsExactly(MAIN, "1", "2");

		Map<String, DiffConcept> allDocsAsMap = service.getAllDocsAsMap(MAIN);
		assertEquals("Concept '2' should have the old label.", "2_default_label", allDocsAsMap.get("2").getLabel());

		service.deleteDocs(MAIN, 1L);
		assertConceptIdsExactly(MAIN, "2");

		service.indexRelevantDocs(MAIN, ImmutableList.of(new DiffConcept("2", "2_new_label")));
		assertConceptIdsExactly(MAIN, "2");

		allDocsAsMap = service.getAllDocsAsMap(MAIN);
		assertEquals("Concept '2' should have the updated label.", "2_new_label", allDocsAsMap.get("2").getLabel());
	}

	@Test
	public void tag() throws Exception {
		indexSingleDoc();
		service.tag("v1");
		assertConceptIdsExactly(MAIN, "1");
		assertConceptIdsExactly(BranchPathUtils.createVersionPath("v1"), "1");
	}

	@Test
	public void testTagVisibility() throws Exception {
		service.tag("v1");
		assertConceptIdsExactly(BranchPathUtils.createVersionPath("v1"));

		service.indexRelevantDocs(MAIN, "1", "2", "3", "4");
		service.tag("v2");
		assertConceptIdsExactly(BranchPathUtils.createVersionPath("v2"), "1", "2", "3", "4");

		service.indexRelevantDocs(MAIN, "3", "4", "5", "6");
		service.tag("v3");
		assertConceptIdsExactly(MAIN, "1", "2", "3", "4", "5", "6");
		assertConceptIdsExactly(BranchPathUtils.createVersionPath("v3"), "1", "2", "3", "4", "5", "6");
	}

	@Test
	public void testCompare() throws Exception {
		// v1: initially contains 10, 1, 2
		service.indexRelevantDocs(MAIN, ImmutableMap.<String, String>builder()
				.put("10", "_")
				.put("1", "a")
				.put("2", "b")
				.build());

		service.tag("v1");

		// v2: add 3, 4, 6, 7 and modify 1, 2. index 10 as an irrelevant change
		service.indexRelevantDocs(MAIN, ImmutableMap.<String, String>builder()
				.put("3", "c")
				.put("4", "d")
				.put("6", "x")
				.put("7", "y")
				.put("1", "f")
				.put("2", "h")
				.build());

		service.indexIrrelevantDocs(MAIN, "10");
		service.tag("v2");

		// v3: delete 6, 7 and modify 4 as an irrelevant change in respect of compare
		service.deleteDocs(MAIN, 6L, 7L);
		service.indexIrrelevantDocs(MAIN, "4");
		service.tag("v3");

		// v4: add 5, delete 2 and modify 1
		service.indexRelevantDocs(MAIN, ImmutableMap.<String, String>builder()
				.put("5", "e")
				.put("1", "g")
				.build());

		service.deleteDocs(MAIN, 2L);
		service.tag("v4");

		// v5: modified 3 and change 1 and 5 as an irrelevant one.
		service.indexRelevantDocs(MAIN, ImmutableMap.<String, String>builder()
				.put("3", "j")
				.build());

		service.indexIrrelevantDocs(MAIN, "1");
		service.indexIrrelevantDocs(MAIN, "5");
		service.tag("v5");

		// v6: modified 3 and changed 10 as an irrelevant change
		service.indexRelevantDocs(MAIN, ImmutableMap.<String, String>builder()
				.put("3", "k")
				.build());

		service.indexIrrelevantDocs(MAIN, "10");
		service.tag("v6");

		// MAIN: add 8, delete 1, 5 and modify 10
		service.indexRelevantDocs(MAIN, ImmutableMap.<String, String>builder()
				.put("8", "L")
				.put("10", "z")
				.build());

		service.deleteDocs(MAIN, 1L, 5L);

		// MAIN: 3, 4, 8 and 10 remain live documents, the others should be deleted
		assertConceptIdsExactly(MAIN, "3", "4", "8", "10");

		compareV1();
		compareV2();
		compareV3();
		compareV4();
		compareV5();
		compareV6();
	}

	private void compareV1() {
		IndexDiff diff;

		// v1 and v2: new: 3, 4, 6 and 7. modified 1 and 2.
		diff = createDiff(BranchPathUtils.createVersionPath("v1"), BranchPathUtils.createVersionPath("v2"));
		assertIdsExactly(diff.getNewIds(), 3L, 4L, 6L, 7L);
		assertIdsExactly(diff.getChangedIds(), 1L, 2L);
		assertIdsExactly(diff.getDetachedIds());

		// v1 and v3: new 3 and 4. modified 1 and 2.
		diff = createDiff(BranchPathUtils.createVersionPath("v1"), BranchPathUtils.createVersionPath("v3"));
		assertIdsExactly(diff.getNewIds(), 3L, 4L);
		assertIdsExactly(diff.getChangedIds(), 1L, 2L);
		assertIdsExactly(diff.getDetachedIds());

		// v1 and v4: new 3, 4 and 5. modified 1. detached 2.
		diff = createDiff(BranchPathUtils.createVersionPath("v1"), BranchPathUtils.createVersionPath("v4"));
		assertIdsExactly(diff.getNewIds(), 3L, 4L, 5L);
		assertIdsExactly(diff.getChangedIds(), 1L);
		assertIdsExactly(diff.getDetachedIds(), 2L);

		// v1 and v5: new 3, 4 and 5. modified 1. detached 2.
		diff = createDiff(BranchPathUtils.createVersionPath("v1"), BranchPathUtils.createVersionPath("v5"));
		assertIdsExactly(diff.getNewIds(), 3L, 4L, 5L);
		assertIdsExactly(diff.getChangedIds(), 1L);
		assertIdsExactly(diff.getDetachedIds(), 2L);

		// v1 and v6: new 3, 4 and 5. modified 1. detached 2.
		diff = createDiff(BranchPathUtils.createVersionPath("v1"), BranchPathUtils.createVersionPath("v6"));
		assertIdsExactly(diff.getNewIds(), 3L, 4L, 5L);
		assertIdsExactly(diff.getChangedIds(), 1L);
		assertIdsExactly(diff.getDetachedIds(), 2L);

		// v1 and HEAD: new 3, 4, 8. modified 10 and detached 1, 2
		diff = createDiffAgainstHead(BranchPathUtils.createVersionPath("v1"), BranchPathUtils.createMainPath());
		assertIdsExactly(diff.getNewIds(), 3L, 4L, 8L);
		assertIdsExactly(diff.getChangedIds(), 10L);
		assertIdsExactly(diff.getDetachedIds(), 1L, 2L);
	}

	private void compareV2() {
		IndexDiff diff;

		// v2 and v3: detached 6 and 7.
		diff = createDiff(BranchPathUtils.createVersionPath("v2"), BranchPathUtils.createVersionPath("v3"));
		assertIdsExactly(diff.getNewIds());
		assertIdsExactly(diff.getChangedIds());
		assertIdsExactly(diff.getDetachedIds(), 6L, 7L);

		// v2 and v4: new 5. modified 1. detached 2, 6 and 7.
		diff = createDiff(BranchPathUtils.createVersionPath("v2"), BranchPathUtils.createVersionPath("v4"));
		assertIdsExactly(diff.getNewIds(), 5L);
		assertIdsExactly(diff.getChangedIds(), 1L);
		assertIdsExactly(diff.getDetachedIds(), 2L, 6L, 7L);

		// v2 and v5: new 5. modified 1, 3. detached 2, 6 and 7.
		diff = createDiff(BranchPathUtils.createVersionPath("v2"), BranchPathUtils.createVersionPath("v5"));
		assertIdsExactly(diff.getNewIds(), 5L);
		assertIdsExactly(diff.getChangedIds(), 1L, 3L);
		assertIdsExactly(diff.getDetachedIds(), 2L, 6L, 7L);

		// v2 and v6: new 5. modified 1, 3. detached 2, 6 and 7.
		diff = createDiff(BranchPathUtils.createVersionPath("v2"), BranchPathUtils.createVersionPath("v6"));
		assertIdsExactly(diff.getNewIds(), 5L);
		assertIdsExactly(diff.getChangedIds(), 1L, 3L);
		assertIdsExactly(diff.getDetachedIds(), 2L, 6L, 7L);

		// v2 and HEAD: new 8. modified 3, 10 and detached 1, 2, 6, 7
		diff = createDiffAgainstHead(BranchPathUtils.createVersionPath("v2"), BranchPathUtils.createMainPath());
		assertIdsExactly(diff.getNewIds(), 8L);
		assertIdsExactly(diff.getChangedIds(), 3L, 10L);
		assertIdsExactly(diff.getDetachedIds(), 1L, 2L, 6L, 7L);
	}

	private void compareV3() {
		IndexDiff diff;

		// v3 and v4: new 5, modified 1 and detached 2.
		diff = createDiff(BranchPathUtils.createVersionPath("v3"), BranchPathUtils.createVersionPath("v4"));
		assertIdsExactly(diff.getNewIds(), 5L);
		assertIdsExactly(diff.getChangedIds(), 1L);
		assertIdsExactly(diff.getDetachedIds(), 2L);

		// v3 and v5: new 5, modified 1, 3. detached 2.
		diff = createDiff(BranchPathUtils.createVersionPath("v3"), BranchPathUtils.createVersionPath("v5"));
		assertIdsExactly(diff.getNewIds(), 5L);
		assertIdsExactly(diff.getChangedIds(), 1L, 3L);
		assertIdsExactly(diff.getDetachedIds(), 2L);

		// v3 and v6: new 5, modified 1, 3. detached 2.
		diff = createDiff(BranchPathUtils.createVersionPath("v3"), BranchPathUtils.createVersionPath("v6"));
		assertIdsExactly(diff.getNewIds(), 5L);
		assertIdsExactly(diff.getChangedIds(), 1L, 3L);
		assertIdsExactly(diff.getDetachedIds(), 2L);

		// v3 and HEAD: new 8, modified 3 and 10. detached 1 and 2.
		diff = createDiffAgainstHead(BranchPathUtils.createVersionPath("v3"), BranchPathUtils.createMainPath());
		assertIdsExactly(diff.getNewIds(), 8L);
		assertIdsExactly(diff.getChangedIds(), 3L, 10L);
		assertIdsExactly(diff.getDetachedIds(), 1L, 2L);
	}

	private void compareV4() {
		IndexDiff diff;

		// v4 and v5: modified 3.
		diff = createDiff(BranchPathUtils.createVersionPath("v4"), BranchPathUtils.createVersionPath("v5"));
		assertIdsExactly(diff.getNewIds());
		assertIdsExactly(diff.getChangedIds(), 3L);
		assertIdsExactly(diff.getDetachedIds());

		// v4 and v6: modified 3.
		diff = createDiff(BranchPathUtils.createVersionPath("v4"), BranchPathUtils.createVersionPath("v6"));
		assertIdsExactly(diff.getNewIds());
		assertIdsExactly(diff.getChangedIds(), 3L);
		assertIdsExactly(diff.getDetachedIds());

		// v4 and HEAD: new 8. modified 3 and 10. detached 1 and 5.
		diff = createDiffAgainstHead(BranchPathUtils.createVersionPath("v4"), BranchPathUtils.createMainPath());
		assertIdsExactly(diff.getNewIds(), 8L);
		assertIdsExactly(diff.getChangedIds(), 3L, 10L);
		assertIdsExactly(diff.getDetachedIds(), 1L, 5L);
	}

	private void compareV5() {
		IndexDiff diff;

		// v5 and v6: modified 3.
		diff = createDiff(BranchPathUtils.createVersionPath("v5"), BranchPathUtils.createVersionPath("v6"));
		assertIdsExactly(diff.getNewIds());
		assertIdsExactly(diff.getChangedIds(), 3L);
		assertIdsExactly(diff.getDetachedIds());

		// v5 and HEAD: new 8. modified 10, 3. detached 1 and 5.
		diff = createDiffAgainstHead(BranchPathUtils.createVersionPath("v5"), BranchPathUtils.createMainPath());
		assertIdsExactly(diff.getNewIds(), 8L);
		assertIdsExactly(diff.getChangedIds(), 3L, 10L);
		assertIdsExactly(diff.getDetachedIds(), 1L, 5L);
	}

	private void compareV6() {
		IndexDiff diff;

		// v6 and HEAD: new 8. modified 10. detached 1 and 5.
		diff = createDiffAgainstHead(BranchPathUtils.createVersionPath("v6"), BranchPathUtils.createMainPath());
		assertIdsExactly(diff.getNewIds(), 8L);
		assertIdsExactly(diff.getChangedIds(), 10L);
		assertIdsExactly(diff.getDetachedIds(), 1L, 5L);
	}

	@Test
	public void testRelevancyChange() throws Exception {
		// v1: initially contains 1, 2
		service.indexRelevantDocs(MAIN, ImmutableMap.<String, String>builder()
				.put("1", "a")
				.put("2", "b")
				.build());

		service.tag("v1");

		// v2: modify 2. re-index 1 and 2 as irrelevant
		service.indexRelevantDocs(MAIN, ImmutableMap.<String, String>builder()
				.put("2", "c")
				.build());

		service.indexIrrelevantDocs(MAIN, "1", "2");
		service.tag("v2");
		assertConceptIdsExactly(MAIN, "1", "2");

		// v1 and v2: modified 2, even though 1 and 2 have changed "irrelevantly" in the meantime.
		final IndexDiff diff = createDiff(BranchPathUtils.createVersionPath("v1"), BranchPathUtils.createVersionPath("v2"));
		assertIdsExactly(diff.getNewIds());
		assertIdsExactly(diff.getChangedIds(), 2L);
		assertIdsExactly(diff.getDetachedIds());
	}

	@Test
	public void testThreeWayCompare() throws Exception {
		// v1: initially contains 1, 2, 3, 6
		service.indexRelevantDocs(MAIN, ImmutableMap.<String, String>builder()
				.put("1", "a")
				.put("2", "b")
				.put("3", "c")
				.put("6", "f")
				.build());

		service.tag("v1");
		service.tag("v2");

		// v1*: update 1 and 3, add 4, delete 2 and 6
		service.indexRelevantDocs(BranchPathUtils.createVersionPath("v1"), ImmutableMap.<String, String>builder()
				.put("1", "A")
				.put("3", "C")
				.put("4", "D")
				.build());

		service.deleteDocs(BranchPathUtils.createVersionPath("v1"), 2L, 6L);

		// v2*: update 1 and 2, add 5, delete 3 and 6
		service.indexRelevantDocs(BranchPathUtils.createVersionPath("v2"), ImmutableMap.<String, String>builder()
				.put("1", "AA")
				.put("2", "B")
				.put("5", "E")
				.build());

		service.deleteDocs(BranchPathUtils.createVersionPath("v2"), 3L, 6L);

		assertConceptIdsExactly(MAIN, "1", "2", "3", "6");
		assertConceptIdsExactly(BranchPathUtils.createVersionPath("v1"), "1", "3", "4");
		assertConceptIdsExactly(BranchPathUtils.createVersionPath("v2"), "1", "2", "5");

		/* 
		 * v1* to v2*: 
		 * - added 2 (had to revert the deletion on v1*) and 5 (actually added on v2*)
		 * - changed 1 (both branches)
		 * - deleted 3 (deleted on v2*) and 4 (had to revert the addition on v1*) 
		 */
		final IndexDiff diff = createThreeWayDiff(BranchPathUtils.createVersionPath("v1"), BranchPathUtils.createVersionPath("v2"));
		assertIdsExactly(diff.getNewIds(), 2L, 5L);
		assertIdsExactly(diff.getChangedIds(), 1L);
		assertIdsExactly(diff.getDetachedIds(), 3L, 4L);
	}

	@Test
	public void testDivergedCompare() throws Exception {
		// MAIN: initially contains 1, 2, 3, 6
		service.indexRelevantDocs(MAIN, ImmutableMap.<String, String>builder()
				.put("1", "a")
				.put("2", "b")
				.put("3", "c")
				.put("6", "f")
				.build());

		final IBranchPath main_A = BranchPathUtils.createPath(MAIN, "a");
		service.reopen(main_A, new CDOBranchPath(new int[] { 0, 1 })); // XXX: we know that 1 will be allocated to this branch in DiffIndexServerService

		// MAIN/A (1st): add 4, change 1
		service.indexRelevantDocs(main_A, ImmutableMap.<String, String>builder()
				.put("1", "d")
				.put("4", "e")
				.build());

		final IBranchPath main_A_B = BranchPathUtils.createPath(main_A, "b");
		service.reopen(main_A_B, new CDOBranchPath(new int[] { 0, 1, 2 }));

		// MAIN/A/B: change 2, delete 1
		service.indexRelevantDocs(main_A_B, ImmutableMap.<String, String>builder()
				.put("2", "q")
				.build());

		service.deleteDocs(main_A_B, 1L);

		// MAIN: delete 6, add 7, change 3 and 1
		service.indexRelevantDocs(MAIN, ImmutableMap.<String, String>builder()
				.put("1", "d")
				.put("3", "w")
				.put("7", "z")
				.build());

		service.deleteDocs(MAIN, 6L);

		// MAIN/A (2nd): adds 4, but doesn't change 1 again (already done on MAIN)
		service.invalidateCdoId(main_A);
		service.reopen(main_A, new CDOBranchPath(new int[] { 0, 3 }));
		service.indexRelevantDocs(main_A, ImmutableMap.<String, String>builder()
				.put("4", "e")
				.put("8", "s")
				.build());

		final IndexDiff indexDiff = createDiffAgainstHeadInContext(main_A_B, main_A, main_A);
		assertIdsExactly(indexDiff.getNewIds(), 4L, 7L, 8L);
		assertIdsExactly(indexDiff.getChangedIds(), 1L, 3L);
		assertIdsExactly(indexDiff.getDetachedIds(), 6L);
	}

	private void assertIdsExactly(final LongSet actual, final long... expected) {
		assertEquals("Number of expected IDs did not match.", expected.length, actual.size());
		assertTrue("Expected concept identifiers did not match.", actual.containsAll(PrimitiveSets.newLongOpenHashSet(expected)));
	}

	private void assertConceptIdsExactly(final IBranchPath branchPath, final String... conceptIds) {
		assertEquals("Document count mismatch.", conceptIds.length, service.getAllDocsCount(branchPath));
		assertThat("Expected concept identifiers did not match.", service.getAllDocsAsMap(branchPath).keySet(), hasItems(conceptIds));
	}

	private IndexDiff createDiff(final IBranchPath sourceBranchPath, final IBranchPath targetBranchPath) {
		final IndexBranchService sourceService = service.getBranchService(sourceBranchPath);
		final IndexBranchService targetService = service.getBranchService(targetBranchPath);

		final IndexCommit sourceCommit = sourceService.getIndexCommit(sourceBranchPath);
		final IndexCommit targetCommit = targetService.getIndexCommit(targetBranchPath);

		return IndexDifferFactory.INSTANCE.createDiffer().calculateDiff(sourceCommit, targetCommit);
	}

	private IndexDiff createDiffAgainstHead(final IBranchPath sourceBranchPath, final IBranchPath targetBranchPath) {
		final IndexBranchService sourceService = service.getBranchService(sourceBranchPath);
		final IndexBranchService targetService = service.getBranchService(targetBranchPath);

		final IndexCommit sourceCommit = sourceService.getIndexCommit(sourceBranchPath);
		final IndexCommit targetCommit = targetService.getLastIndexCommit();

		return IndexDifferFactory.INSTANCE.createDiffer().calculateDiff(sourceCommit, targetCommit);
	}
	
	private IndexDiff createDiffAgainstHeadInContext(final IBranchPath contextBranchPath, final IBranchPath sourceBranchPath, final IBranchPath targetBranchPath) {
		if (!Iterators.contains(BranchPathUtils.topToBottomIterator(contextBranchPath), sourceBranchPath)) {
			throw new IllegalStateException(String.format("Context %s must be a descendant of source %s.", contextBranchPath, sourceBranchPath));
		}
		
		final IndexBranchService sourceService = service.getBranchService(contextBranchPath);
		final IndexBranchService targetService = service.getBranchService(targetBranchPath);
		
		final IndexCommit sourceCommit = sourceService.getIndexCommit(sourceBranchPath);
		final IndexCommit targetCommit = targetService.getLastIndexCommit();
		
		return IndexDifferFactory.INSTANCE.createDiffer().calculateDiff(sourceCommit, targetCommit);
	}

	private IndexDiff createThreeWayDiff(final IBranchPath sourceBranchPath, final IBranchPath targetBranchPath) {
		final IndexCommit ancestorCommit = service.getBranchService(sourceBranchPath).getIndexCommit(sourceBranchPath);
		final IndexCommit sourceCommit = service.getBranchService(sourceBranchPath).getLastIndexCommit();
		final IndexCommit targetCommit = service.getBranchService(targetBranchPath).getLastIndexCommit();

		return IndexDifferFactory.INSTANCE.createDiffer().calculateDiff(ancestorCommit, sourceCommit, targetCommit);
	}
}
