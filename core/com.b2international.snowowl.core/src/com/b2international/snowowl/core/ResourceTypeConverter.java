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

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
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
			checkArgument(resourceTypeConverters.containsKey(doc.getResourceType()), "ResourceTypeConverter implementation is missing for type: %s",
					doc.getResourceType());
			return resourceTypeConverters.get(doc.getResourceType()).toResource(doc);
		}

		public Map<String, ResourceTypeConverter> getResourceTypeConverters() {
			return ImmutableMap.copyOf(resourceTypeConverters);
		}

		public <T extends Resource> void expand(RepositoryContext context, Options expand, List<ExtendedLocale> locales, List<T> results) {
			Multimap<String, T> resourcesByIndex = Multimaps.index(results, Resource::getResourceType);
			for (String resourceTypeToExpand : resourcesByIndex.keySet()) {
				checkArgument(resourceTypeConverters.containsKey(resourceTypeToExpand),
						"ResourceTypeConverter implementation is missing for type: %s", resourceTypeToExpand);
				resourceTypeConverters.get(resourceTypeToExpand).expand(context, expand, locales, resourcesByIndex.get(resourceTypeToExpand));
			}
		}
	}

	String getResourceType();

	Integer getRank();

	Resource toResource(ResourceDocument doc);

	default <T extends Resource> void expand(RepositoryContext context, Options expand, List<ExtendedLocale> locales, Collection<T> results) {
	}

	/**
	 * Resolves an URI to a CodeSystemURI with ECL part if supported by this resource type converter using the given context.
	 * 
	 * @param context 
	 * @param uriToResolve 
	 * 
	 * @return
	 * @throws BadRequestException - if the uri cannot be resolved to a valid CodeSystem URI with ECL part
	 */
	default ResourceURIWithQuery resolveToCodeSystemUriWithQuery(ServiceProvider context, String uriToResolve) {
		throw new BadRequestException("'%s' represents a resource that cannot be resolved into a CodeSystem URI with ECL query part.", uriToResolve);
	}

}
