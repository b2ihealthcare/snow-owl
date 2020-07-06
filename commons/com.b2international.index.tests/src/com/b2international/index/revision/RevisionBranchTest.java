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

import org.assertj.core.api.ObjectAssert;
import org.junit.Test;

import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 7.7
 */
public class RevisionBranchTest {

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
	
	private ObjectAssert<RevisionBranch> assertBranchCreate(String branchName) {
		return assertThat(RevisionBranch.builder()
			.id(0)
			.parentPath(RevisionBranch.MAIN_PATH)
			.name(branchName)
			.segments(ImmutableSortedSet.of(new RevisionSegment(0, 0, 1)))
			.build());
	}
	
}
