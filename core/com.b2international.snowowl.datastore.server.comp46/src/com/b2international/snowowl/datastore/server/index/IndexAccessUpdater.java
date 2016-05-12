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
package com.b2international.snowowl.datastore.server.index;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Default index access updater.
 */
public class IndexAccessUpdater implements IIndexAccessUpdater {
	
	private static final Logger LOG = LoggerFactory.getLogger(IndexAccessUpdater.class);

	private static Supplier<Timer> CLEANUP_TIMER = Suppliers.memoize(new Supplier<Timer>() { @Override public Timer get() {
		return new Timer("Index cleanup timer", true);
	}});

	private static final class UsageTimeLoader extends CacheLoader<IBranchPath, AtomicLong> {
		@Override 
		public AtomicLong load(final IBranchPath key) throws Exception {
			return new AtomicLong();
		}
	}

	private final class UsageTimeRemovalListener implements RemovalListener<IBranchPath, AtomicLong> {
		@Override
		public void onRemoval(final RemovalNotification<IBranchPath, AtomicLong> notification) {
			final IBranchPath branchPath = notification.getKey();
			if (!indexService.inactiveClose(branchPath, false)) {
				/* 
				 * Service could not close because it had some work to do; reset its usage timer, so it will
				 * be considered for closing in the next full timeout cycle (but not in the next minute).
				 */
				usageTimeCache.getUnchecked(branchPath).set(0L);
			} else {
				LOG.info("Closing {} branch service for path {} due to inactivity.", indexService.getClass().getSimpleName(), branchPath);
			}
		}
	}

	private final class CleanupTimerTask extends TimerTask {
		@Override 
		public void run() {
			for (final IBranchPath branchPath : usageTimeCache.asMap().keySet()) {
				final AtomicLong usageTime = usageTimeCache.getIfPresent(branchPath);
				if (null == usageTime) {
					continue;
				}

				if (usageTime.incrementAndGet() > timeoutMinutes) {
					usageTimeCache.invalidate(branchPath);
				}
			}
		}
	}

	// Checks every minute if there's something to be done
	private static final long CLEANUP_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(1L);

	private final long timeoutMinutes;

	private LoadingCache<IBranchPath, AtomicLong> usageTimeCache;
	private TimerTask cleanupTask;
	private IndexServerService<?> indexService;

	public IndexAccessUpdater(final IndexServerService<?> indexService, final long timeoutMinutes) {
		this.timeoutMinutes = timeoutMinutes;

		if (timeoutMinutes > 0) {
			this.usageTimeCache = CacheBuilder.newBuilder()
					.removalListener(new UsageTimeRemovalListener())
					.build(new UsageTimeLoader());

			this.indexService = indexService;
			this.cleanupTask = new CleanupTimerTask();
			CLEANUP_TIMER.get().schedule(cleanupTask, CLEANUP_INTERVAL_MILLIS, CLEANUP_INTERVAL_MILLIS);
			LOG.info("Activity timer for {} branch services started.", indexService.getClass().getSimpleName());
		}		
	}

	@Override
	public void registerAccessAndRecordUsage(final IBranchPath branchPath) {
		if (usageTimeCache != null) {
			usageTimeCache.getUnchecked(branchPath).set(0);
		}
	}

	public void close() {
		if (cleanupTask != null) {
			LOG.info("Canceling activity timer for {} branch services.", indexService.getClass().getSimpleName());
			cleanupTask.cancel();
			cleanupTask = null;
		}

		indexService = null;
		usageTimeCache = null;
	}
}
