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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;
import static com.b2international.snowowl.datastore.cdo.CDOCommitInfoUtils.check;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.REPOSITORY_UUID;
import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.util.concurrent.TimeUnit.MINUTES;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.IPostStoreUpdateListener2;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Server-side caching component label provider service.
 *
 */
public abstract class SnomedCachingComponentLabelProviderService<C extends SnomedCachingComponentLabelProvider> implements IPostStoreUpdateListener2 {

	private static final long FIVE = 5L;
	
	private final LoadingCache<IBranchPath, C> labelCache = // 
			newBuilder()
			.expireAfterAccess(FIVE, MINUTES)
			.build(new CacheLoader<IBranchPath, C>() {
				public C load(final IBranchPath branchPath) throws Exception {
					return createCachingLabelProvider(branchPath);
				}
			});
	
	public String getLabel(final IBranchPath branchPath, final String conceptId) {
		return labelCache.getUnchecked(branchPath).getLabel(conceptId);
	}

	@Override
	public void storeUpdated(final CDOCommitInfo commitInfo) {
		if (check(commitInfo)) {
			labelCache.invalidate(createPath(commitInfo.getBranch()));
		}
	}

	@Override
	public String getRepositoryUuid() {
		return REPOSITORY_UUID;
	}
	
	/**
	 * Creates and return with the concrete caching component label provider service. 
	 * @param branchPath the branch path for the label provider.
	 * @return the new caching label provider instance.
	 */
	protected abstract C createCachingLabelProvider(final IBranchPath branchPath);
	
}