/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.index.IndexException;

/**
 * Hooks API
 * <p>
 * Allows {@link Hook}s to be added/removed to a {@link RevisionIndex}. The currently supported hooks are:
 * <ul>
 * <li>{@link PreCommitHook pre-commit}</li>
 * <li>{@link PostCommitHook post-commit}</li>
 * </ul>
 * </p>
 * Exceptions thrown by {@link Hook}s are propagated to clients via the {@link IndexException} runtime exception wrapper.
 * 
 * @since 7.0
 */
public interface Hooks {

	/**
	 * Adds a hook to the underlying repository. If the hook is already added to the repository, then this method is no-op.
	 * 
	 * @param hook
	 *            - the hook to add
	 */
	void addHook(Hook hook);

	/**
	 * Removes a hook from the underlying repository. If the hook is already removed from the repository, then this method is no-op.
	 * 
	 * @param hook
	 *            - the hook to remove
	 */
	void removeHook(Hook hook);

	/**
	 * @since 7.0
	 */
	interface Hook {
	}

	/**
	 * Invoked by {@link StagingArea#commit(String, long, String, String)} before actually committing the changes. The hook receives the current
	 * {@link StagingArea} so it is possible to modify the changes before commit.
	 * 
	 * @since 7.0
	 */
	@FunctionalInterface
	interface PreCommitHook extends Hook {

		void run(StagingArea staging);

	}

	/**
	 * Invoked by {@link StagingArea#commit(String, long, String, String)} after successfully committed the changes. The hook receives the
	 * {@link Commit} document representing the successful commit.
	 * 
	 * @since 7.0
	 */
	@FunctionalInterface
	interface PostCommitHook extends Hook {

		void run(Commit commit);

	}

}
