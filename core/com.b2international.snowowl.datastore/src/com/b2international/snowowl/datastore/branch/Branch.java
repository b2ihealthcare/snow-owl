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
package com.b2international.snowowl.datastore.branch;

import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.datastore.branch.BranchImpl.BranchState;

/**
 * Represents a {@link Branch} in a terminology repository. A {@link Branch} can be uniquely identified by using its {@link #path()} and
 * {@link #baseTimestamp()} values.
 * 
 * @since 4.1
 */
public interface Branch {

	/**
	 * Segment separator in {@link Branch#path()} values.
	 */
	String SEPARATOR = "/";

	/**
	 * Returns the unique path of this {@link Branch}.
	 * 
	 * @return
	 */
	String path();

	/**
	 * Returns the name of the {@link Branch}, which is often the same value as the last segment of the {@link #path()}.
	 * 
	 * @return
	 */
	String name();

	/**
	 * Returns the parent {@link Branch} instance.
	 * 
	 * @return
	 */
	Branch parent();

	/**
	 * Returns the base timestamp value of this {@link Branch}. The base timestamp represents the time when this branch has been created, or branched
	 * of from its {@link #parent()}.
	 * 
	 * @return
	 */
	long baseTimestamp();

	/**
	 * Returns the head timestamp value for this {@link Branch}. The head timestamp represents the time when the last commit arrived on this
	 * {@link Branch}.
	 * 
	 * @return
	 */
	long headTimestamp();

	/**
	 * Returns the {@link BranchState} of this {@link Branch} compared to its {@link #parent()}. TODO document how BranchState calculation works
	 * 
	 * @return
	 * @see #state(Branch)
	 */
	BranchState state();

	/**
	 * Returns the {@link BranchState} of this {@link Branch} compared to the given target {@link Branch}.
	 * 
	 * @param target
	 * @return
	 */
	BranchState state(Branch target);

	/**
	 * Rebases the {@link Branch} with its {@link #parent()}. Rebasing this branch does not actually modify this {@link Branch} state, instead it will
	 * create a new {@link Branch} representing the rebased form of this {@link Branch} and returns it. Commits available on the {@link #parent()}
	 * will be available on the resulting rebased {@link Branch} after successful rebase.
	 * 
	 * @return
	 * @see #rebase(Branch)
	 */
	Branch rebase();

	/**
	 * Rebases the {@link Branch} with the given target {@link Branch}. Rebasing this branch does not actually modify this {@link Branch} state,
	 * instead it will create a new {@link Branch} representing the rebased form of this {@link Branch} and returns it. Commits available on the
	 * target {@link Branch} will be available on the resulting {@link Branch} after successful rebase.
	 * 
	 * @param target
	 * @return
	 */
	Branch rebase(Branch target);

	/**
	 * @param source
	 *            - the branch to merge onto this branch
	 * @throws BranchMergeException
	 *             - if source cannot be merged
	 */
	void merge(Branch source) throws BranchMergeException;

	/**
	 * Creates a new child branch.
	 * 
	 * @param name
	 *            - the name of the new child {@link Branch}
	 * @return
	 * @throws AlreadyExistsException
	 *             - if the child branch already exists
	 */
	Branch createChild(String name) throws AlreadyExistsException;
	
	/*
	 * TODO: move this to internal class/interface
	 */
	void handleCommit(long commitTimestamp);

}