/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core;

import java.util.Objects;
import java.util.Optional;

import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJob;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
public interface ServiceProvider {

	/**
	 * Returns the given service or throws an exception if none found in the current {@link ApplicationContext}.
	 * 
	 * @param type
	 *            - the type of the service
	 * @return the currently registered service implementation for the given service interface, never <code>null</code>
	 */
	<T> T service(Class<T> type);

	/**
	 * Returns the given service wrapped in an {@link Optional} indicating that the service might not be available. If not available the
	 * {@link Optional#empty()} will be returned. 
	 * NOTE: this method is available in certain ServiceProvider context's in not in all contexts.
	 * 
	 * @param <T>
	 *            - the type of the service
	 * @param type
	 *            - the type of the service
	 * @return an {@link Optional}
	 */
	default <T> Optional<T> optionalService(Class<T> type) {
		throw new UnsupportedOperationException(String.format("Optional Service injection for type '%s' is not available in provider '%s'", type.getClass(), getClass().getSimpleName()));
	}

	/**
	 * Returns a {@link Provider} to provide the given type when needed by using {@link #service(Class)}, so the returned {@link Provider} will never
	 * return <code>null</code> instances, instead it throws exception, which may indicate application bootstrapping/initialization problems.
	 * 
	 * @param type
	 * @return
	 */
	<T> Provider<T> provider(Class<T> type);

	/**
	 * Inject or override services in this context using a service provider builder.
	 * 
	 * @return
	 * @see DelegatingContext.Builder#build()
	 */
	default DelegatingContext.Builder<? extends ServiceProvider> inject() {
		return new DelegatingContext.Builder<>(ServiceProvider.class, this);
	}
	
	/**
	 * Returns <code>true</code> if any job present with the given jobKey in {@link RemoteJobEntry#isRunning()} state, <code>false</code> otherwise.
	 * 
	 * @param jobKey
	 * @return
	 */
	default boolean isJobRunning(String jobKey) {
		// check first if this context is running inside a job with the given jobKey
		Optional<RemoteJob> job = optionalService(RemoteJob.class);
		if (job.isPresent() && Objects.equals(jobKey, job.get().getKey())) {
			return true;
		}

		// if not inside a job context or running in non-job context check the jobs index
		return JobRequests.prepareSearch().one().filterByKey(jobKey).build().execute(this).first().map(RemoteJobEntry::isRunning).orElse(false);
	}

	/**
	 * Empty {@link ServiceProvider} implementation that throws {@link UnsupportedOperationException}s when trying to provide services. Useful when
	 * testing {@link Request} implementations.
	 * 
	 * @since 5.4
	 */
	ServiceProvider EMPTY = new ServiceProvider() {
		@Override
		public <T> T service(Class<T> type) {
			throw new UnsupportedOperationException("Empty service provider can't provide services. Requested: " + type);
		}

		@Override
		public <T> Provider<T> provider(Class<T> type) {
			throw new UnsupportedOperationException("Empty service provider can't provide services. Requested: " + type);
		}
	};

}
