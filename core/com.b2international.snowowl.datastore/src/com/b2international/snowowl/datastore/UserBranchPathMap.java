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

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

/**
 * A data structure which maps Snow Owl repository UUIDs to branch paths, for use with versioning.
 */
public class UserBranchPathMap extends AbstractBranchPathMap {

	private static final long serialVersionUID = 1L;

	private final LoadingCache<String, IBranchPath> delegate = CacheBuilder.newBuilder().build(new CacheLoader<String, IBranchPath>() { 
		/*
		 * (non-Javadoc)
		 * @see com.google.common.cache.CacheLoader#load(java.lang.Object)
		 */
		@Override 
		public IBranchPath load(final String key) throws Exception {
			return BranchPathUtils.createMainPath();
		}
	});
	
	public UserBranchPathMap() { }

	public UserBranchPathMap(final Map<String, IBranchPath> sourceMap) {
		checkNotNull(sourceMap, "Source map may not be null.");
		
		delegate.putAll(sourceMap);
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.IBranchPathMap#getBranchPath(java.lang.String)
	 */
	@Override
	public IBranchPath getBranchPath(final String repositoryId) {
		checkNotNull(repositoryId, "Repository UUID may not be null.");
		
		return delegate.getUnchecked(repositoryId);
	}

	/* 
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.IBranchPathMap#getBranchPath(org.eclipse.emf.ecore.EPackage)
	 */
	@Override
	public IBranchPath getBranchPath(final EPackage ePackage) {
		checkNotNull(ePackage, "Package argument cannot be null.");
		
		return getBranchPath(getRepositoryUuid(ePackage));
	}

	/**
	 * Associates a {@link IBranchPath branch path} with a repository UUID.
	 * @param repositoryId the repository UUID key
	 * @param branchPath the branch path value
	 */
	public void putBranchPath(final String repositoryId, final IBranchPath branchPath) {
		checkNotNull(repositoryId, "Repository UUID may not be null.");
		checkNotNull(branchPath, "Branch path value may not be null.");
		
		delegate.put(repositoryId, branchPath);
	}

	/**
	 * Associates a {@link IBranchPath branch path} with a package.
	 * @param ePackage the package.
	 * @param branchPath the branch path value.
	 */
	public void putBranchPath(final EPackage ePackage, final IBranchPath branchPath) {
		checkNotNull(ePackage, "Package argument cannot be null.");
		checkNotNull(branchPath, "Branch path value may not be null.");
		
		putBranchPath(getRepositoryUuid(ePackage), branchPath);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return MessageFormat.format("BranchPathMap[delegate={0}]", delegate.asMap());
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.IBranchPathMap#asLocalMap()
	 */
	@Override
	public Map<String, IBranchPath> getLockedEntries() {
		return ImmutableMap.of();
	}
	
	private static class SerializationProxy implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private final Map<String, IBranchPath> backingMap;
		
		public SerializationProxy(final UserBranchPathMap source) {
			backingMap = ImmutableMap.copyOf(source.delegate.asMap());
		}
		
		private Object readResolve() {
			return new UserBranchPathMap(backingMap);
		}
	}
	
	private Object writeReplace() {
		return new SerializationProxy(this);
	}
}