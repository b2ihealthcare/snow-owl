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
package com.b2international.snowowl.snomed.datastore.index;

import static com.b2international.commons.collect.LongSets.toStringSet;
import static com.b2international.snowowl.snomed.datastore.index.SnomedHierarchy.forBranch;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.lang.Long.parseLong;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.browser.SuperTypeIdProvider;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Caching super type ID provider for SNOMED&nbsp;CT.
 * <p>Internally it uses a loading cache to store {@link SnomedHierarchy} instances mapped via the
 * unique {@link IBranchPath branch path}s.
 *
 */
public class SnomedCachingSuperTypeIdProvider implements SuperTypeIdProvider<String> {

	private final LoadingCache<IBranchPath, SnomedHierarchy> hierarchyCache = newBuilder().build(new CacheLoader<IBranchPath, SnomedHierarchy>() {
		@Override public SnomedHierarchy load(final IBranchPath branchPath) throws Exception {
			return forBranch(branchPath);
		}
	});
	
	@Override
	public Collection<String> getSuperTypeIds(final IBranchPath branchPath, final String conceptId) {
		return toStringSet(getSuperTypeIdsAsLong(checkNotNull(branchPath, "branchPath"), checkNotNull(conceptId, "conceptId")));
	}

	private LongSet getSuperTypeIdsAsLong(final IBranchPath branchPath, final String conceptId) {
		return getHierarchy(branchPath).getSuperTypeIds(parseLong(conceptId));
	}

	private SnomedHierarchy getHierarchy(final IBranchPath branchPath) {
		try {
			return hierarchyCache.get(branchPath);
		} catch (final ExecutionException e) {
			throw new SnowowlRuntimeException("Error while initializing SNOMED CT hierarchy for " + branchPath);
		}
	}

}