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
package com.b2international.snowowl.datastore.tasks;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Singleton for creating and managing {@link ITaskContext task context} instances.
 * <p>This class requires running {@link Platform}.
 * @see ITaskContext
 */
public enum TaskContextManager {

	/**Shared singleton manager.*/
	INSTANCE;

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskContextManager.class);
	private static final String TASK_CONTEXT_EXTENSION_POINT_ID = "com.b2international.snowowl.datastore.taskContext";
	private static final String CLASS_ATTRIBUTE = "class";
	
	private final Supplier<Map<String, ITaskContext>> contextSupplier = Suppliers.memoize(new Supplier<Map<String, ITaskContext>>() {
		@Override public Map<String, ITaskContext> get() {
			
			final Map<String, ITaskContext> $ = Maps.newHashMap();
			for (final IConfigurationElement element : Platform.getExtensionRegistry().getConfigurationElementsFor(TASK_CONTEXT_EXTENSION_POINT_ID)) {
				
				try {
					
					final ITaskContext taskContext = ClassUtils.checkAndCast(element.createExecutableExtension(CLASS_ATTRIBUTE), ITaskContext.class);
					$.put(taskContext.getContextId(), taskContext);
					
				} catch (final CoreException e) {
					LOGGER.error("Failed to load available task contexts.", e);
					throw new SnowowlRuntimeException("Failed to load available task contexts.", e);
				}
				
			}
			
			return Collections.unmodifiableMap($);
		}
	});
	
	private final LoadingCache<String, Iterable<ITaskContext>> toolingIdContextCache = CacheBuilder.newBuilder().build(new CacheLoader<String, Iterable<ITaskContext>>() {
		@Override public Iterable<ITaskContext> load(final String toolingId) throws Exception {
			
			Preconditions.checkNotNull(toolingId, "Tooling ID argument cannot be null.");

			final Set<ITaskContext> $ = Sets.newHashSet();
			for (final Iterator<ITaskContext> itr = contextSupplier.get().values().iterator(); itr.hasNext(); /* */) {
				
				final ITaskContext context = itr.next();
				if (toolingId.equals(context.getToolingId())) {
					$.add(context);
				}
			}
			
			return Collections.unmodifiableSet($);
		}
	});
	
	/**
	 * Returns with all available and registered {@link ITaskContext} instances.
	 */
	public Iterable<ITaskContext> getAllContexts() {
		return contextSupplier.get().values();
	}

	/**Returns with all {@link ITaskContext} instances registered for the given tooling support.*/
	public Iterable<ITaskContext> getContextByToolingId(final String toolingId) {
		
		Preconditions.checkNotNull(toolingId, "Tooling ID argument cannot be null.");
		try {
			return toolingIdContextCache.get(toolingId);
		} catch (final ExecutionException e) {
			final String message = "Error while getting task contexts for tooling ID: " + toolingId;
			LOGGER.error(message);
			throw new SnowowlRuntimeException(message, e);
		}
	}
	
	/**Creates a new {@link ITaskContext} instance based on the unique context ID.*/
	public ITaskContext createNewById(final String contextId) {
		try {
			return ((TaskContext) contextSupplier.get().get(Preconditions.checkNotNull(contextId, "Context ID argument cannot be null."))).clone();
		} catch (final CloneNotSupportedException e) {
			final String message = "Error while creating new task contexts for context ID: " + contextId;
			LOGGER.error(message);
			throw new SnowowlRuntimeException(message, e);		
		}
	}
	
}