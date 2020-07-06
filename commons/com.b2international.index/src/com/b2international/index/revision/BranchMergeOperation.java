/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * @since 7.2
 */
public final class BranchMergeOperation {

	private BaseRevisionBranching branching;
	
	final String fromPath;
	final String toPath;
	Set<String> exclusions;
	String author;
	String commitMessage;
	RevisionConflictProcessor conflictProcessor = new RevisionConflictProcessor.Default();
	boolean squash = false;
	Object context;

	public BranchMergeOperation(BaseRevisionBranching branching, String fromPath, String toPath) {
		this.branching = branching;
		this.fromPath = fromPath;
		this.toPath = toPath;
	}
	
	public BranchMergeOperation author(String author) {
		this.author = author;
		return this;
	}
	
	public BranchMergeOperation commitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
		return this;
	}
	
	public BranchMergeOperation squash(boolean squash) {
		this.squash = squash;
		return this;
	}
	
	public BranchMergeOperation conflictProcessor(RevisionConflictProcessor conflictProcessor) {
		this.conflictProcessor = conflictProcessor;
		return this;
	}
	
	public BranchMergeOperation context(Object context) {
		this.context = context;
		return this;
	}
	
	public Commit merge() {
		return branching.doMerge(this);
	}

	public BranchMergeOperation exclude(String ... ids) {
		return exclude(ImmutableSet.copyOf(ids));
	}
	
	public BranchMergeOperation exclude(Iterable<String> ids) {
		this.exclusions = ImmutableSet.copyOf(ids);
		return this;
	}
	
	public Set<String> getExclusions() {
		return exclusions;
	}
	
}
