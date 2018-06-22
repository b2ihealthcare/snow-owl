/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.classification;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.b2international.snowowl.core.AbstractDisposableService;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;

/**
 * Represents an abstract service manager which:
 * <ul>
 * <li>Keeps frequently used instances keyed by {@link IBranchPath} for a certain amount of time
 * <li>Limits the total number of available instances
 * <li>Provides a service instance pool where instances can be borrowed and returned
 * </ul>
 * @param <S> the service instance type
 * @param <P> the initialization parameter type
 */
public abstract class CollectingService<S, P> extends AbstractDisposableService {

	private class CollectingTimerTask extends TimerTask {
		
		private final IBranchPath branchPath;
		
		public CollectingTimerTask(final IBranchPath branchPath) {
			this.branchPath = branchPath;
		}

		@Override
		public void run() {
			if (isDisposed()) {
				cancel();
			} else {
				evict(branchPath);
			}
		}
	}
	
	// Keep unreferenced services around for 15 minutes
	private static final long KEEPALIVE_MILLIS = TimeUnit.MINUTES.toMillis(15L);

	private static final Supplier<Timer> CLEANUP_TIMER = Suppliers.memoize(new Supplier<Timer>() { @Override public Timer get() {
		return new Timer("Service cleanup timer", true);
	}});
	
	private final ConcurrentMap<IBranchPath, CollectingServiceReference<S>> sharedServiceCache;
	
	private final BlockingQueue<CollectingServiceReference<S>> serviceReferenceQueue;
	
	protected CollectingService(final int maximumServiceCount) {
		this(maximumServiceCount, KEEPALIVE_MILLIS);
	}
	
	protected CollectingService(final int maximumServiceCount, final long keepaliveMillis) {
		this.serviceReferenceQueue = initQueue(maximumServiceCount);
		this.sharedServiceCache = new MapMaker().makeMap();
	}
	
	private Timer getCleanupTimer() {
		return CLEANUP_TIMER.get();
	}

	protected abstract Logger getLogger();

	protected abstract S createService(final IBranchPath branchPath, final boolean shared, final P serviceParams) throws Exception;

	protected abstract boolean matchesParams(final S service, final P serviceParams);

	protected abstract void retireService(final S service);

	private ArrayBlockingQueue<CollectingServiceReference<S>> initQueue(final int maximumServiceCount) {
		final ArrayBlockingQueue<CollectingServiceReference<S>> result = new ArrayBlockingQueue<CollectingServiceReference<S>>(maximumServiceCount, true);

		for (int i = 0; i < maximumServiceCount; i++) {
			try {
				result.put(new CollectingServiceReference<S>());
			} catch (final InterruptedException e) {
				throw new SnowowlRuntimeException("Interrupted while trying to populate service reference queue.", e);
			}
		}
		
		return result;
	}
	
	protected final CollectingServiceReference<S> getSharedServiceReferenceIfExists(final IBranchPath branchPath) {
		return sharedServiceCache.get(branchPath);
	}
	
	public final CollectingServiceReference<S> takeServiceReference(final IBranchPath branchPath, final boolean shared, final P serviceParams) throws Exception {
		
		CollectingServiceReference<S> sharedServiceReference = null;
		
		if (shared) {
			sharedServiceReference = takeSharedServiceReference(branchPath, serviceParams);
		}
			
		if (null != sharedServiceReference) {
			return sharedServiceReference;
		} else {
			return takeRegularServiceReference(branchPath, shared, serviceParams);
		}
	}
	
	private CollectingServiceReference<S> takeSharedServiceReferenceIfExists(final IBranchPath branchPath) {
		return sharedServiceCache.remove(branchPath);
	}
	
	private CollectingServiceReference<S> takeSharedServiceReference(final IBranchPath branchPath, final P serviceParams) throws Exception {
		final CollectingServiceReference<S> removedReference = takeSharedServiceReferenceIfExists(branchPath);
		if (null != removedReference) {
			removedReference.cancelCollectingTask();
			
			if (!matchesParams(removedReference.getService(), serviceParams)) {
				retireService(removedReference.getService());
				removedReference.init(branchPath, true, createService(branchPath, true, serviceParams));
			}
		}
		
		return removedReference;
	}

	private CollectingServiceReference<S> takeRegularServiceReference(final IBranchPath branchPath, final boolean shared, final P serviceParams) throws Exception {
		final CollectingServiceReference<S> serviceReference = getUnusedServiceReference();
		serviceReference.init(branchPath, shared, createService(branchPath, shared, serviceParams));
		return serviceReference;
	}

	private CollectingServiceReference<S> getUnusedServiceReference() throws InterruptedException {

		// TODO: after some tries, we may want to give up
		while (true) {
			
			final CollectingServiceReference<S> serviceReference = serviceReferenceQueue.poll(1, TimeUnit.SECONDS);
			
			if (null != serviceReference) {
				return serviceReference;
			}
			
			if (!sharedServiceCache.isEmpty()) {
				final IBranchPath evictedBranchPath = Iterables.getFirst(sharedServiceCache.keySet(), null);
				evict(evictedBranchPath);
			}
		}
	}

	public final void retireServiceReference(final CollectingServiceReference<S> serviceReference) throws InterruptedException {

		if (!serviceReference.isShared()) {
			retireRegularServiceReference(serviceReference);
		} else {
			retireSharedServiceReference(serviceReference);
		}
	}

	private void retireSharedServiceReference(final CollectingServiceReference<S> serviceReference) {
		sharedServiceCache.put(serviceReference.getBranchPath(), serviceReference);
		final TimerTask collectingTask = createSharedCollectingTimerTask(serviceReference.getBranchPath());
		serviceReference.scheduleTaskOnTimer(getCleanupTimer(), collectingTask, KEEPALIVE_MILLIS);
	}
	
	private TimerTask createSharedCollectingTimerTask(final IBranchPath branchPath) {
		return new CollectingTimerTask(branchPath);
	}

	private void retireRegularServiceReference(final CollectingServiceReference<S> serviceReference) throws InterruptedException {
		retireService(serviceReference.getService());
		serviceReference.init(null, false, null);
		serviceReferenceQueue.put(serviceReference);
	}

	protected final boolean hasAvailableServiceReferences() {
		return !serviceReferenceQueue.isEmpty();
	}

	protected void evictAll() {
		for (final IBranchPath branchPath : sharedServiceCache.keySet()) {
			evict(branchPath);
		}
	}

	private void evict(final IBranchPath branchPath) {
		final CollectingServiceReference<S> referenceToRetire = takeSharedServiceReferenceIfExists(branchPath);

		if (null != referenceToRetire) {
			try {
				retireRegularServiceReference(referenceToRetire);
			} catch (final InterruptedException e) {
				getLogger().error("Interrupted while trying to retire an unused shared service reference.", e);
			}
		}
	}

	@Override
	protected void onDispose() {
		getCleanupTimer().cancel();
		evictAll();
		super.onDispose();
	}
}