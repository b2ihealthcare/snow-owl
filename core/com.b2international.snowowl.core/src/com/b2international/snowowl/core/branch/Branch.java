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
package com.b2international.snowowl.core.branch;

import java.io.Serializable;
import java.util.Collection;
import java.util.regex.Pattern;

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.MetadataHolder;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

/**
 * Represents a {@link Branch} in a terminology repository. A {@link Branch} can be uniquely identified by using its {@link #path()} and
 * {@link #baseTimestamp()} values.
 * 
 * @since 4.1
 */
public interface Branch extends Deletable, MetadataHolder, Serializable {

	/**
	 * Allowed set of characters for a branch name.
	 */
	String DEFAULT_ALLOWED_BRANCH_NAME_CHARACTER_SET = "a-zA-Z0-9_-";

	/**
	 * The maximum length of a branch.
	 */
	int DEFAULT_MAXIMUM_BRANCH_NAME_LENGTH = 50;

	/**
	 * Branch name prefix used for temporary branches during rebase.
	 */
	String TEMP_PREFIX = "$";
	
	/**
	 * Temporary branch name format. Values are prefix, name, current time. 
	 */
	String TEMP_BRANCH_NAME_FORMAT = "%s%s_%s";
	
	/**
	 * @since 4.2
	 */
	interface BranchNameValidator {

		BranchNameValidator DEFAULT = new BranchNameValidatorImpl();

		/**
		 * Validates a branch name and throws {@link BadRequestException} if not valid.
		 * 
		 * @param name
		 * @throws BadRequestException
		 */
		void checkName(String name) throws BadRequestException;

		/**
		 * @since 4.2
		 */
		class BranchNameValidatorImpl implements BranchNameValidator {

			private Pattern pattern;
			private String allowedCharacterSet;
			private int maximumLength;

			public BranchNameValidatorImpl() {
				this(DEFAULT_ALLOWED_BRANCH_NAME_CHARACTER_SET, DEFAULT_MAXIMUM_BRANCH_NAME_LENGTH);
			}

			public BranchNameValidatorImpl(String allowedCharacterSet, int maximumLength) {
				this.allowedCharacterSet = allowedCharacterSet;
				this.maximumLength = maximumLength;
				pattern = Pattern.compile(String.format("^(%s)?[%s]{1,%s}(_[0-9]{1,19})?$", Pattern.quote(TEMP_PREFIX), allowedCharacterSet, maximumLength));
			}

			@Override
			public void checkName(String name) {
				if (Strings.isNullOrEmpty(name)) {
					throw new BadRequestException("Name cannot be empty");
				}
				if (!pattern.matcher(name).matches()) {
					throw new BadRequestException(
							"'%s' is either too long (max %s characters) or it contains invalid characters (only '%s' characters are allowed).", name,
							maximumLength, allowedCharacterSet);
				}
			}

		}

	}

	/**
	 * The path of the main branch.
	 */
	static final String MAIN_PATH = "MAIN";

	/**
	 * @since 4.1
	 */
	enum BranchState {
		UP_TO_DATE, FORWARD, BEHIND, DIVERGED, STALE
	}

	/**
	 * Segment separator in {@link Branch#path()} values.
	 */
	String SEPARATOR = "/";
	
	/**
	 * A singleton {@link Joiner} that can be used to concatenate branch path segments into a fully usable branch path.
	 * @since 6.4
	 * @see #get(String...)
	 */
	Joiner BRANCH_PATH_JOINER = Joiner.on(SEPARATOR);

	/**
	 * Returns the unique path of this {@link Branch}.
	 * 
	 * @return
	 */
	String path();

	/**
	 * Returns the unique path of the parent of this {@link Branch}.
	 * 
	 * @return
	 */
	String parentPath();

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

	boolean canRebase();

	boolean canRebase(Branch onTopOf);

	/**
	 * Rebases this branch {@link Branch} on top of the specified {@link Branch}.
	 * <p>
	 * Rebasing this branch does not actually modify this {@link Branch} state, instead it will create a new {@link Branch} representing the rebased
	 * form of this {@link Branch} and returns it. Commits available on the target {@link Branch} will be available on the resulting {@link Branch}
	 * after successful rebase.
	 * 
	 * @param onTopOf
	 *            - the branch on top of which this branch should be lifted
	 * @param commitMessage
	 *            - the commit message
	 * @return
	 */
	Branch rebase(Branch onTopOf, String userId, String commitMessage);

	Branch rebase(Branch onTopOf, String userId, String commitMessage, Runnable postReopen);

	/**
	 * Merges changes to this {@link Branch} by squashing the change set of the specified {@link Branch} into a single commit.
	 * 
	 * @param changesFrom
	 *            - the branch to take changes from
	 * @param commitMessage
	 *            - the commit message
	 * @return
	 * @throws BranchMergeException
	 *             - if the branch cannot be merged for some reason
	 */
	Branch merge(Branch changesFrom, String userId, String commitMessage) throws BranchMergeException;

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

	/**
	 * Creates a new child branch with the given name and metadata.
	 * 
	 * @param name
	 *            - the name of the new child {@link Branch}, may not be <code>null</code>
	 * @param metadata
	 *            - optional metadata map
	 * @return
	 * @throws AlreadyExistsException
	 *             - if the child branch already exists
	 */
	Branch createChild(String name, Metadata metadata);

	/**
	 * Returns all child branches created on this {@link Branch}.
	 * 
	 * @return a {@link Collection} of child {@link Branch} instances or an empty collection, never <code>null</code>.
	 */
	Collection<Branch> children();

	/**
	 * Reopens the branch with the same name and parent, on the parent head.
	 * 
	 * @return the reopened branch
	 */
	Branch reopen();

	@Override
	Branch delete();

	/**
	 * @return
	 * @deprecated - use the new {@link Branch} interface instead
	 */
	IBranchPath branchPath();

	/**
	 * Returns a new version of this branch with updated {@link Metadata}.
	 * 
	 * @param metadata
	 * @return
	 */
	@Override
	Branch withMetadata(Metadata metadata);

	/**
	 * Updates the branch with the specified properties. Currently {@link Metadata} supported only.
	 * 
	 * @param metadata
	 */
	void update(Metadata metadata);

	/**
	 * @param segments - segments to join into a usable branch path string 
	 * @return a full absolute branch path that can be used in {@link Request}s and other services  
	 * @since 6.4
	 */
	static String get(String...segments) {
		return BRANCH_PATH_JOINER.join(segments);
	}
}
