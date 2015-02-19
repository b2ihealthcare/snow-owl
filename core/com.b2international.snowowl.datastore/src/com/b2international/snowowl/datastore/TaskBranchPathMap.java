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
package com.b2international.snowowl.datastore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Nullable;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.collect.ImmutableMap;

/**
 *
 */
public final class TaskBranchPathMap extends AbstractBranchPathMap {

	private static final long serialVersionUID = 1L;
	
	private IBranchPathMap parent;
	
	private final Map<String, IBranchPath> localMap;

	public TaskBranchPathMap(final Map<String, IBranchPath> localMap) {
		this(localMap, null);
	}
	
	public TaskBranchPathMap(final Map<String, IBranchPath> localMap, final @Nullable IBranchPathMap parent) {
		checkNotNull(localMap, "Local overlay map may not be null.");
		
		this.parent = parent;
		this.localMap = ImmutableMap.copyOf(localMap);
	}
	
	public void setParent(final IBranchPathMap parent) {
		checkNotNull(parent, "Parent branch path map may not be null.");
		
		this.parent = parent;
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.IBranchPathMap#getBranchPath(java.lang.String)
	 */
	@Override
	public IBranchPath getBranchPath(final String repositoryId) {
		final IBranchPath localResult = localMap.get(repositoryId);
		
		if (null != localResult) {
			return localResult;
		} else if (null != parent) {
			return parent.getBranchPath(repositoryId);
		} else {
			return null;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.IBranchPathMap#getBranchPath(org.eclipse.emf.ecore.EPackage)
	 */
	@Override
	public IBranchPath getBranchPath(final EPackage ePackage) {
		final IBranchPath localResult = localMap.get(getRepositoryUuid(ePackage));
		
		if (null != localResult) {
			return localResult;
		} else if (null != parent) {
			return parent.getBranchPath(ePackage);
		} else {
			return null;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.IBranchPathMap#asLocalMap()
	 */
	@Override
	public Map<String, IBranchPath> getLockedEntries() {
		return localMap;
	}
}