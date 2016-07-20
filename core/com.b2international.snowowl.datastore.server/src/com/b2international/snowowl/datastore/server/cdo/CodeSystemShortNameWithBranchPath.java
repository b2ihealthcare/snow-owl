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
package com.b2international.snowowl.datastore.server.cdo;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.BranchPathUtils.isMain;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.TaskBranchPathMap;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.datastore.UserBranchPathMap;
import com.b2international.snowowl.datastore.tasks.ITaskStateManager;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Wraps a code system short name as a string and a {@link IBranchPath branch path} associated with the current terminology metadata repository.
 * <p><b>NOTE:&nbsp;</b> Since terminology metadata cannot vary on VD and MS authoring tasks, clients should stick to the parent branch of any actual VD/MS branches.
 * <br><b>NOTE:&nbsp;</b>Equals and has code is calculated by the wrapped code system short name.
 * @deprecated - UNSUPPORTED
 */
public class CodeSystemShortNameWithBranchPath {

	private final IBranchPath path;
	private final String codeSystemShortName;

	public CodeSystemShortNameWithBranchPath(final IBranchPath path, final String codeSystemShortName) {
		this.path = Preconditions.checkNotNull(path, "Branch path argument cannot be null.");
		this.codeSystemShortName = Preconditions.checkNotNull(codeSystemShortName, "Code system short name argument cannot be null.");
	}

	/**
	 * Returns with the code system short name.
	 */
	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}

	/**
	 * Returns with the {@link IBranchPath branch path}.
	 */
	public IBranchPath getBranchPath() {
		return path;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codeSystemShortName == null) ? 0 : codeSystemShortName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CodeSystemShortNameWithBranchPath))
			return false;
		CodeSystemShortNameWithBranchPath other = (CodeSystemShortNameWithBranchPath) obj;
		if (codeSystemShortName == null) {
			if (other.codeSystemShortName != null)
				return false;
		} else if (!codeSystemShortName.equals(other.codeSystemShortName))
			return false;
		return true;
	}

	/**Factory for creating {@link LoadingCache caches} for lazily ensuring mapping between
	 *{@link CodeSystemShortNameWithBranchPath} and terminology component IDs given as a short.*/
	public static final class CodeSystemShortNameCacheFactor {

		/**Returns with a new {@link LoadingCache cache} instance.*/
		public static LoadingCache<CodeSystemShortNameWithBranchPath, Short> createNewCache(final String repositoryUuid) {
			
			return CacheBuilder.newBuilder().build(new CacheLoader<CodeSystemShortNameWithBranchPath, Short>() {
				
				@Override
				public Short load(final CodeSystemShortNameWithBranchPath key) throws Exception {

					final IBranchPathMap branchPathMap;
					if (isMain(key.getBranchPath())) {
						branchPathMap = new UserBranchPathMap();
					} else {
						final TaskBranchPathMap taskBranchPathMap = getServiceForClass(ITaskStateManager.class).getTaskBranchPathMap(key.getBranchPath().lastSegment());
						taskBranchPathMap.setParent(new UserBranchPathMap());
						branchPathMap = taskBranchPathMap;
						
					}
					
					final String terminologyComponentId = getServiceForClass(TerminologyRegistryService.class) //
							.getTerminologyComponentIdByShortName(branchPathMap, key.getCodeSystemShortName());

					return StringUtils.isEmpty(terminologyComponentId) ? CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT //unspecified 
							: CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsShort(terminologyComponentId); //terminology component ID as short 
				};
				
			});
			
		}
	}

}