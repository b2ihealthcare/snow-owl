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
package com.b2international.snowowl.datastore.version;

import java.io.Serializable;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.google.common.base.Preconditions;

/**
 * Builder for creating {@link ITagConfiguration tag configuration} instances.
 */
public class TagConfigurationBuilder implements Serializable {

	private static final long serialVersionUID = -610554607005341172L;

	private final String repositoryUuid;
	private final String versionId;
	private String userId;
	private IBranchPath branchPath;
	private String parentContextDescription = DatastoreLockContextDescriptions.CREATE_VERSION;
	
	/**Creates a new tag configuration tag builder for the given repository UUID argument.*/
	public static TagConfigurationBuilder createForRepositoryUuid(final String repositoryUuid, final String versionId) {
		return new TagConfigurationBuilder(repositoryUuid, versionId);
	}
	
	/**Creates a new tag configuration tag builder for the given tooling feature ID.*/
	public static TagConfigurationBuilder createForToolingId(final String toolingId, final String versionId) {
		return new TagConfigurationBuilder(CodeSystemUtils.getRepositoryUuid(toolingId), versionId);
	}
	
	/**Builds a new {@link ITagConfiguration tag configuration} instance.*/
	public ITagConfiguration build() {
		return new ITagConfiguration() {
			private static final long serialVersionUID = 1000036369640450915L;
			@Override public String getUserId() { return userId; }
			@Override public String getVersionId() { return versionId; }
			@Override public String getRepositoryUuid() { return repositoryUuid; }
			@Override public IBranchPath getBranchPath() { return branchPath; }
			@Override public String getParentContextDescription() { return parentContextDescription; }
		};
	}

	/**
	 * Sets the user ID on the current instance based on the user ID argument.
	 * <p>This should be used when trying to impersonate someone else. Or just to avoid user associated with embedded client session.
	 * @return the builder.
	 */
	public TagConfigurationBuilder setUserId(final String userId) {
		this.userId = Preconditions.checkNotNull(userId);
		return this;
	}

	/**
	 * Sets the branch path on the current instance based on the {@link IBranchPath branch path} argument.
	 * <p>This could be invoked whenever tagging the repository content is supported/required 
	 * not only on the {@link IBranchPath#MAIN_BRANCH MAIN} branch but on any arbitrary branches. 
	 * @return the builder.
	 */
	public TagConfigurationBuilder setBranchPath(final IBranchPath branchPath) {
		this.branchPath = Preconditions.checkNotNull(branchPath);
		return this;
	}
	
	/**
	 * Sets the parent lock context description, as a part of which tagging should be done.
	 * <p>This should be used when tagging is not done from the create version wizard.
	 * @return the builder.
	 */
	public TagConfigurationBuilder setParentContextDescription(final String parentContextDescription) {
		this.parentContextDescription = parentContextDescription;
		return this;
	}
	
	/**Private constructor.*/
	private TagConfigurationBuilder(final String repositoryUuid, final String versionId) {
		this.repositoryUuid = Preconditions.checkNotNull(repositoryUuid);
		this.versionId = Preconditions.checkNotNull(versionId);
		this.userId = getUserId();
		this.branchPath = BranchPathUtils.createMainPath();
	}

	/**Returns with the user ID.*/
	private String getUserId() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).getUserId();
	}
}
