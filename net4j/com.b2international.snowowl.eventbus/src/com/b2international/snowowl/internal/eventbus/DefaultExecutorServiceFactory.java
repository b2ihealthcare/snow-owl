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
package com.b2international.snowowl.internal.eventbus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @since 4.5.1
 */
public class DefaultExecutorServiceFactory implements ExecutorServiceFactory {
	
	@Override
	public List<ExecutorService> createExecutorServices(String description, int numberOfWorkers) {
		final ThreadGroup group = new ThreadGroup(description);
		final List<ExecutorService> results = new ArrayList<>();

		for (int i = 0; i < numberOfWorkers; i++) {
			final ExecutorService context = Executors.newSingleThreadExecutor(
					new ThreadFactory() {
						@Override
						public Thread newThread(Runnable r) {
							final Thread thread = new Thread(group, r);
							thread.setDaemon(true);
							return thread;
						}
					});

			results.add(context);
		}

		return results;
	}
}
