/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.assertj.core.api.ObjectAssert;
import org.junit.Test;

import com.b2international.index.revision.RevisionBranch.Builder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 7.7
 */
public class RevisionBranchTest {

	private final AtomicLong branchIds = new AtomicLong(1);
	
	private RevisionBranch main = RevisionBranch.builder()
			.id(0)
			.name(RevisionBranch.MAIN_PATH)
			.parentPath("")
			.segments(ImmutableSortedSet.of(new RevisionSegment(0, 100, 100)))
			.deleted(false)
			.build();
	
	private RevisionBranch base = createBranch("base", main, 150);
	private RevisionBranch compare = createBranch("compare", base, 250);
	
	@Test
	public void lowerCaseAlphabetical() throws Exception {
		String branchName = "abcdefghijklmnopqrstuvwz";
		assertBranchCreate(branchName)
			.extracting(RevisionBranch::getName)
			.isEqualTo(branchName);
	}
	
	@Test
	public void upperCaseAlphabetical() throws Exception {
		String branchName = "ABCDEFGHIJKLMNOPQRSTUVWZ";
		assertBranchCreate(branchName)
			.extracting(RevisionBranch::getName)
			.isEqualTo(branchName);
	}
	
	@Test
	public void digit() throws Exception {
		String branchName = "1234567890";
		assertBranchCreate(branchName)
			.extracting(RevisionBranch::getName)
			.isEqualTo(branchName);
	}
	
	@Test
	public void underscore() throws Exception {
		String branchName = "a_b";
		assertBranchCreate(branchName)
			.extracting(RevisionBranch::getName)
			.isEqualTo(branchName);
	}
	
	@Test
	public void hyphen() throws Exception {
		String branchName = "a-b";
		assertBranchCreate(branchName)
			.extracting(RevisionBranch::getName)
			.isEqualTo(branchName);
	}
	
	@Test
	public void dot() throws Exception {
		String branchName = "v1.0";
		assertBranchCreate(branchName)
			.extracting(RevisionBranch::getName)
			.isEqualTo(branchName);
	}
	
	@Test
	public void tilde() throws Exception {
		String branchName = "~1.0";
		assertBranchCreate(branchName)
			.extracting(RevisionBranch::getName)
			.isEqualTo(branchName);
	}
	
	@Test
	public void intersectionNewBranchForward() throws Exception {
		RevisionBranchRef baseIntersection = base.intersection(compare);
		RevisionBranchRef compareIntersection = compare.intersection(base);
		
		assertEquals(baseIntersection.segments(), compareIntersection.segments());
	}
	
	@Test
	public void differenceBranchUpToDate() throws Exception {
		RevisionBranchRef baseDiff = base.difference(compare);
		RevisionBranchRef compareDiff = compare.difference(base);
		
		assertTrue(baseDiff.isEmpty());
		assertTrue(compareDiff.isEmpty());
	}
	
	@Test
	public void differenceBranchBehind() throws Exception {
		base = commit(base, 300);
		
		RevisionBranchRef baseDiff = base.difference(compare);
		RevisionBranchRef compareDiff = compare.difference(base);
		
		assertEquals(ImmutableSortedSet.of(new RevisionSegment(base.getId(), 151, 300)), baseDiff.segments());
		assertTrue(compareDiff.isEmpty());
	}
	
	@Test
	public void differenceBranchBehindRebased() throws Exception {
		base = commit(base, 300);
		compare = merge(base, compare, 400);
		
		RevisionBranchRef baseDiff = base.difference(compare);
		RevisionBranchRef compareDiff = compare.difference(base);
		
		assertTrue(baseDiff.isEmpty());
		assertEquals(ImmutableSortedSet.of(new RevisionSegment(compare.getId(), 250, 400)), compareDiff.segments());
	}
	
	@Test
	public void differenceBranchForward() throws Exception {
		compare = commit(compare, 300);		
		
		RevisionBranchRef baseDiff = base.difference(compare);
		RevisionBranchRef compareDiff = compare.difference(base);
		
		assertTrue(baseDiff.isEmpty());
		assertEquals(ImmutableSortedSet.of(new RevisionSegment(compare.getId(), 250, 300)), compareDiff.segments());
	}
	
	@Test
	public void differenceBranchForwardMerged() throws Exception {
		compare = commit(compare, 300);
		base = merge(compare, base, 400);
		
		RevisionBranchRef baseDiff = base.difference(compare);
		RevisionBranchRef compareDiff = compare.difference(base);
		
		assertEquals(ImmutableSortedSet.of(new RevisionSegment(base.getId(), 151, 400)), baseDiff.segments());
		assertTrue(compareDiff.isEmpty());
	}
	
	@Test
	public void differenceBranchDiverged() throws Exception {
		base = commit(base, 300);
		compare = commit(compare, 400);
		
		RevisionBranchRef baseDiff = base.difference(compare);
		RevisionBranchRef compareDiff = compare.difference(base);
		
		assertEquals(ImmutableSortedSet.of(new RevisionSegment(base.getId(), 151, 300)), baseDiff.segments());
		assertEquals(ImmutableSortedSet.of(new RevisionSegment(compare.getId(), 250, 400)), compareDiff.segments());
	}
	
	@Test
	public void differenceBranchDivergedRebased() throws Exception {
		base = commit(base, 300);
		compare = commit(compare, 400);
		
		compare = merge(base, compare, 500);
		
		RevisionBranchRef baseDiff = base.difference(compare);
		RevisionBranchRef compareDiff = compare.difference(base);
		
		assertTrue(baseDiff.isEmpty());
		assertEquals(ImmutableSortedSet.of(new RevisionSegment(compare.getId(), 250, 500)), compareDiff.segments());
	}
	
	@Test
	public void differenceBranchDivergedRebasedMerged() throws Exception {
		base = commit(base, 300);
		compare = commit(compare, 400);
		
		compare = merge(base, compare, 500);
		base = merge(compare, base, 600);
		
		RevisionBranchRef baseDiff = base.difference(compare);
		RevisionBranchRef compareDiff = compare.difference(base);
		
		assertTrue(baseDiff.isEmpty());
		assertTrue(compareDiff.isEmpty());
	}
	
	private RevisionBranch createBranch(String branchName, RevisionBranch parent, long headTimestamp) {
		final long branchId = branchIds.getAndIncrement();
		final SortedSet<RevisionSegment> compareSegments = ImmutableSortedSet.<RevisionSegment>naturalOrder()
				.addAll(parent.getSegments())
				.add(new RevisionSegment(branchId, headTimestamp, headTimestamp))
				.build();
		final List<RevisionBranchMergeSource> compareMergeSources = ImmutableList.<RevisionBranchMergeSource>builder()
				.add(new RevisionBranchMergeSource(headTimestamp, parent.getSegments().stream().map(RevisionSegment::getEndPoint).collect(Collectors.toCollection(TreeSet::new)), true))
				.build();
		return RevisionBranch.builder()
				.id(branchId)
				.deleted(false)
				.parentPath(parent.getPath())
				.name(branchName)
				.segments(compareSegments)
				.mergeSources(compareMergeSources)
				.build();
	}

	private RevisionBranch commit(RevisionBranch branch, long timestamp) {
		return commit(branch, null, timestamp);
	}
	
	private RevisionBranch commit(RevisionBranch branch, SortedSet<RevisionBranchPoint> mergeSources, long timestamp) {
		Builder builder = branch.toBuilder();
		// apply segment changes
		builder.segments(branch.getSegments().stream()
				.map(segment -> {
					if (segment.branchId() == branch.getId()) {
						return segment.withEnd(timestamp);
					} else {
						return segment;
					}
				})
				.collect(Collectors.toCollection(TreeSet::new)));
	
		if (mergeSources != null) {
			builder.mergeSources(ImmutableList.<RevisionBranchMergeSource>builder()
					.addAll(branch.getMergeSources() == null ? Collections.emptyList() : branch.getMergeSources())
					.add(new RevisionBranchMergeSource(timestamp, mergeSources, false))
					.build());
		}
		
		return builder.build();
	}
	
	private RevisionBranch merge(RevisionBranch from, RevisionBranch to, long timestamp) {
		SortedSet<RevisionBranchPoint> mergeSources = from.difference(to)
				.segments()
				.stream()
				.filter(segment -> segment.branchId() != to.getId())
				.map(RevisionSegment::getEndPoint)
				.collect(Collectors.toCollection(TreeSet::new));
		return commit(to, mergeSources, timestamp);
	}
	
	private ObjectAssert<RevisionBranch> assertBranchCreate(String branchName) {
		return assertThat(RevisionBranch.builder()
			.id(0)
			.parentPath(RevisionBranch.MAIN_PATH)
			.name(branchName)
			.segments(ImmutableSortedSet.of(new RevisionSegment(0, 0, 1)))
			.build());
	}
	
}
