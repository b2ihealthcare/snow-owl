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
package com.b2international.snowowl.datastore.server.jobs;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.quicksearch.IQuickSearchProvider;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.UserBranchPathMap;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;
import com.b2international.snowowl.datastore.quicksearch.QuickSearchContentProviderBroker;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 */
public class ProcessQuickSearchItemsCallable implements Callable<Map<String, QuickSearchContentResult>> {

	/**
	 * The filter expression.
	 */
	private final String filterExpression;

	/**
	 * Maximum element limit.
	 */
	private final int limit;

	private final Cache<String, QuickSearchContentResult> results;

	private final IBranchPathMap branchPathMap;

	private Map<String, Map<String, Object>> providerConfiguration;

	/**
	 * Creates a new job instance for processing quick search elements in a background process.
	 * @param filterExpression the query expression. Can be {@code null}. If {@code null} empty string will be used instead.
	 * @param providerIds the unique IDs of the quick search providers from the client side. Cannot be {@code null}.
	 * @param limit the maximum element limit. Must be a positive integer.
	 * @param bookmarks the bookmarked items. Cannot be {@code null}.
	 * @param previousPicks the previous choices. Cannot be {@code null}.
	 * @param branchPathMap branch path map of the client. Cannot be {@code null}.
	 */
	public ProcessQuickSearchItemsCallable(@Nullable final String filterExpression, Map<String, Map<String, Object>> providerConfiguration, 
			@Nonnegative final int limit, @Nonnull final IBranchPathMap branchPathMap) {
		
		checkNotNull(providerConfiguration, "Quick search providers argument cannot be null.");
		checkNotNull(branchPathMap, "Branch path map argument cannot be null.");
		checkState(limit > 0, "Limit must be a positive integer. Was: %s", limit);
		
		final Set<String> repositoryUuids = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).uuidKeySet();
		final UserBranchPathMap localMap = new UserBranchPathMap(branchPathMap.asMap(repositoryUuids));
		for (final Entry<String, Map<String, Object>> configurationEntry : providerConfiguration.entrySet()) {
			
			final Object branchConfiguration = configurationEntry.getValue().get(IQuickSearchProvider.RESTRICTED_BRANCH_PATH_MAP);
			if (branchConfiguration instanceof IBranchPathMap) {
				
				for (final Entry<String, IBranchPath> branchEntry : ((IBranchPathMap) branchConfiguration).asMap(repositoryUuids).entrySet()) {
					
					localMap.putBranchPath(branchEntry.getKey(), branchEntry.getValue());
				}
				
			}
			
		}
		
		this.branchPathMap = localMap; 
		
		this.filterExpression = Strings.nullToEmpty(filterExpression);
		this.limit = limit;
		this.results = CacheBuilder.newBuilder().build();
		this.providerConfiguration = providerConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Map<String, QuickSearchContentResult> call() throws Exception {
		final Set<String> providerIds = providerConfiguration.keySet();
		final List<Runnable> runnables = Lists.newArrayListWithExpectedSize(providerIds.size());
		
		for (final String providerId : providerIds) {
			runnables.add(createRunnableForProviderId(providerId));
		}
		
		ForkJoinUtils.runInParallel(runnables);
		
		return ImmutableMap.copyOf(results.asMap());
	}
	
	/*creates a runnable based on the quick search provider identified by its unique ID for performing a terminology specific search*/
	private Runnable createRunnableForProviderId(final String quickSearchProviderId) {
		return new Runnable() { @Override public void run() {
			final IQuickSearchContentProvider provider = QuickSearchContentProviderBroker.INSTANCE.getProvider(quickSearchProviderId);
			checkNotNull(provider, "Provider has not been registered for quick search provider. ID: " + quickSearchProviderId);
			results.put(quickSearchProviderId, provider.getComponents(filterExpression, branchPathMap, limit, providerConfiguration.get(quickSearchProviderId)));
		}};
	}
}