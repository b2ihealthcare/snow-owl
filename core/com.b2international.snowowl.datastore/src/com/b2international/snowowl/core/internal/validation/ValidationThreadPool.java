/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.internal.validation;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.events.util.Promise;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * @since 6.0
 */
public final class ValidationThreadPool implements IDisposableService {

	private final AtomicBoolean disposed = new AtomicBoolean(false);
	private final ListeningExecutorService executor;
	
	ValidationThreadPool(int nThreads) {
		this.executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(nThreads));
	}
	
	@Override
	public boolean isDisposed() {
		return disposed.get();
	}
	
	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true)) {
			MoreExecutors.shutdownAndAwaitTermination(executor, 1, TimeUnit.MINUTES);
		}
	}
	
	public Promise<Boolean> submit(Callable<Boolean> callable) {
		return Promise.wrap(executor.submit(callable));
	}
	
}
