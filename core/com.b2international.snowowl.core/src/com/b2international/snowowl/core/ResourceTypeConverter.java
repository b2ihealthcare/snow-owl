/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 8.0
 */
public interface ResourceTypeConverter {

	final class Registry {
		
		private final Map<String, ResourceTypeConverter> resourceTypeConverters = new HashMap<>();
		
		public Registry(ClassPathScanner scanner) {
			scanner.getComponentsByInterface(ResourceTypeConverter.class).forEach(converter -> {
				resourceTypeConverters.put(converter.getResourceType(), converter);
			});
		}
		
		public Resource toResource(ResourceDocument doc) {
			checkArgument(resourceTypeConverters.containsKey(doc.getResourceType()), "ResourceTypeConverter implementation is missing for type: %s", doc.getResourceType());
			return resourceTypeConverters.get(doc.getResourceType()).toResource(doc);
		}
		
		public Map<String, ResourceTypeConverter> getResourceTypeConverters() {
			return ImmutableMap.copyOf(resourceTypeConverters);
		}

		public void expand(RepositoryContext context, Options expand, List<ExtendedLocale> locales, List<Resource> results) {
			Multimap<String, Resource> resourcesByIndex = Multimaps.index(results, Resource::getResourceType);
			for (String resourceTypeToExpand : resourcesByIndex.keySet()) {
				checkArgument(resourceTypeConverters.containsKey(resourceTypeToExpand), "ResourceTypeConverter implementation is missing for type: %s", resourceTypeToExpand);
				Stopwatch w = Stopwatch.createStarted();
				Collection<Resource> resourcesToExpand = resourcesByIndex.get(resourceTypeToExpand);
				resourceTypeConverters.get(resourceTypeToExpand).expand(context, expand, locales, resourcesToExpand);
				if (w.elapsed(TimeUnit.MILLISECONDS) > 50) {
					context.log().warn("Expanded {} {} in {}", resourcesToExpand.size(), resourceTypeToExpand, w);
				}
			}
		}
	}
	
	String getResourceType();
	
	Integer getRank();
	
	Resource toResource(ResourceDocument doc);
	
	default void expand(RepositoryContext context, Options expand, List<ExtendedLocale> locales, Collection<Resource> results) {
	}
	
}
