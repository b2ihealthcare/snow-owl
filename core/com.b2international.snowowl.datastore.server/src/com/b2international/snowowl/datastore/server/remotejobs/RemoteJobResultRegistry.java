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
package com.b2international.snowowl.datastore.server.remotejobs;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.b2international.snowowl.core.AbstractDisposableService;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.datastore.remotejobs.AbstractRemoteJobEvent;
import com.b2international.snowowl.datastore.remotejobs.IRemoteJobManager;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobRemovedEvent;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;

/**
 * @param <T> the job result type
 */
public class RemoteJobResultRegistry<T> extends AbstractDisposableService implements IDisposableService {

	private final class ResultCleanupHandler implements IHandler<IMessage> {
		@Override
		public void handle(final IMessage message) {
			final AbstractRemoteJobEvent jobEvent = message.body(AbstractRemoteJobEvent.class);
			if (jobEvent instanceof RemoteJobRemovedEvent) {
				remove(((RemoteJobRemovedEvent) jobEvent).getId());
			}
		}
	}

	private static final class ResultMap<T> extends LinkedHashMap<UUID, T> {

		private static final long serialVersionUID = 1L;
		
		private final int maximumResultsToKeep;

		private ResultMap(final int maximumResultsToKeep) {
			super(maximumResultsToKeep);
			this.maximumResultsToKeep = maximumResultsToKeep;
		}
		
		@Override
		protected boolean removeEldestEntry(final Entry<UUID, T> eldest) {
			return size() > maximumResultsToKeep;
		}
	}

	private final class ResultMapSupplier implements Supplier<Map<UUID, T>> {
		@Override
		public Map<UUID, T> get() {
			return Collections.synchronizedMap(new ResultMap<T>(maximumResultsToKeep));
		}
	}

	private final IHandler<IMessage> cleanupHandler = new ResultCleanupHandler();
	private final Supplier<Map<UUID, T>> resultCacheSupplier = Suppliers.memoize(new ResultMapSupplier());
	private final int maximumResultsToKeep;

	/**
	 * @param maximumResultsToKeep
	 */
	public RemoteJobResultRegistry(final int maximumResultsToKeep) {
		checkArgument(maximumResultsToKeep > 0, "Maximum number of results to keep must be greater than 0.");
		this.maximumResultsToKeep = maximumResultsToKeep;
	}

	public void registerListeners() {
		getEventBus().registerHandler(IRemoteJobManager.ADDRESS_REMOTE_JOB_CHANGED, cleanupHandler);
	}

	@Override
	protected void onDispose() {
		getEventBus().unregisterHandler(IRemoteJobManager.ADDRESS_REMOTE_JOB_CHANGED, cleanupHandler);
		doRemoveAll();
		super.onDispose();
	}

	private Map<UUID, T> getResultCache() {
		return resultCacheSupplier.get();
	}

	public T get(final UUID jobId) {
		throwIfDisposed();
		return getResultCache().get(jobId);
	}

	public Collection<T> getAllResults() {
		throwIfDisposed();
		
		final Map<UUID, T> resultCache = getResultCache();
		synchronized (resultCache) {
			return ImmutableList.copyOf(resultCache.values());
		}
	}

	public void put(final UUID jobId, final T result) {
		throwIfDisposed();
		getResultCache().put(jobId, result);
	}

	public void remove(final UUID jobId) {
		throwIfDisposed();
		getResultCache().remove(jobId);
	}

	public void removeAll() {
		throwIfDisposed();
		doRemoveAll();
	}

	protected void doRemoveAll() {
		getResultCache().clear();
	}

	private static IEventBus getEventBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
}