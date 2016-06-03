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
package com.b2international.snowowl.datastore.index.diff;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.ICodeSystemVersion;

/**
 * Represents a version compare configuration with a source and a target {@link IBranchPath branch path} and a repository UUID.
 */
public class VersionCompareConfiguration implements Serializable {

	private static final long serialVersionUID = 6825334028122710935L;

	private final String repositoryUuid;
	private final String toolingName;
	private final boolean threeWay;
	
	private final IBranchPath sourcePath;
	private final boolean sourcePatched;
	
	private final IBranchPath targetPath;
	private final boolean targetPatched;

	public static Builder builder(final String repositoryUuid, final boolean threeWay) {
		return new Builder(repositoryUuid, threeWay);
	}

	public static class Builder {
		private final String repositoryUuid;
		private final String toolingName;
		private final boolean threeWay;
		
		private IBranchPath sourcePath;
		private boolean sourcePatched;

		private IBranchPath targetPath;
		private boolean targetPatched;
		
		private Builder(final String repositoryUuid, final boolean threeWay) {
			this.repositoryUuid = checkNotNull(repositoryUuid, "repositoryUuid");
			this.toolingName = checkNotNull(CodeSystemUtils.getSnowOwlToolingName(repositoryUuid), "Tooling name was null for repository: " + repositoryUuid);
			this.threeWay = threeWay;
		}
		
		public Builder source(final ICodeSystemVersion version) {
			return source(BranchPathUtils.createPath(version.getPath()), version.isPatched());
		}

		public Builder sourcePatched(final boolean patched) {
			this.sourcePatched = patched;
			return this;
		}

		public Builder source(final IBranchPath sourcePath, final boolean sourcePatched) {
			this.sourcePath = sourcePath;
			this.sourcePatched = sourcePatched;
			return this;
		}
		
		public Builder target(final ICodeSystemVersion version) {
			return target(BranchPathUtils.createPath(version.getPath()), version.isPatched());
		}
		
		public Builder targetPatched(final boolean patched) {
			this.targetPatched = patched;
			return this;
		}
		
		public Builder target(final IBranchPath targetPath, final boolean targetPatched) {
			this.targetPath = targetPath;
			this.targetPatched = targetPatched;
			return this;
		}
		
		public VersionCompareConfiguration build() {
			checkNotNull(sourcePath, "sourcePath");
			checkNotNull(targetPath, "targetPath");
			return new VersionCompareConfiguration(this);
		}
	}

	private VersionCompareConfiguration(final Builder builder) {
		this.repositoryUuid = builder.repositoryUuid;
		this.toolingName = builder.toolingName;
		this.threeWay = builder.threeWay;
		
		this.sourcePath = builder.sourcePath;
		this.sourcePatched = builder.sourcePatched;
		
		this.targetPath = builder.targetPath;
		this.targetPatched = builder.targetPatched;
	}

	/**Returns with the repository UUID.*/
	public String getRepositoryUuid() {
		return repositoryUuid;
	}

	/**Returns with the source {@link IBranchPath branch path}.*/
	public IBranchPath getSourcePath() {
		return sourcePath;
	}
	
	/**Returns with the target {@link IBranchPath branch path}.*/
	public IBranchPath getTargetPath() {
		return targetPath;
	}
	
	/**Returns with the tooling name for the current version compare configuration.*/
	public String getToolingName() {
		return toolingName;
	}
	
	/**
	 * Returns with {@code true} if the version compare operation is three way compare.
	 * <br>Otherwise {@code false}.
	 */
	public boolean isThreeWay() {
		return threeWay;
	}
	
	/**
	 * Returns with {@code true} if the source version branch is patched. Otherwise {@code false}.
	 */
	public boolean isSourcePatched() {
		return sourcePatched;
	}
	
	/**
	 * Returns with {@code true} if the target version branch is patched. Otherwise {@code false}.
	 */
	public boolean isTargetPatched() {
		return targetPatched;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((repositoryUuid == null) ? 0 : repositoryUuid.hashCode());
		result = prime * result + ((sourcePath == null) ? 0 : sourcePath.hashCode());
		result = prime * result + ((targetPath == null) ? 0 : targetPath.hashCode());
		result = prime * result + (threeWay ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final VersionCompareConfiguration other = (VersionCompareConfiguration) obj;
		if (repositoryUuid == null) {
			if (other.repositoryUuid != null)
				return false;
		} else if (!repositoryUuid.equals(other.repositoryUuid))
			return false;
		if (sourcePath == null) {
			if (other.sourcePath != null)
				return false;
		} else if (!sourcePath.equals(other.sourcePath))
			return false;
		if (targetPath == null) {
			if (other.targetPath != null)
				return false;
		} else if (!targetPath.equals(other.targetPath))
			return false;
		if (threeWay != other.threeWay)
			return false;
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(toolingName);
		sb.append(": ");
		sb.append(sourcePath.getPath());
		sb.append((sourcePatched ? "*" : ""));
		sb.append(" - ");
		sb.append(targetPath.getPath());
		sb.append((targetPatched ? "*" : ""));
		return sb.toString();  
	}
}
