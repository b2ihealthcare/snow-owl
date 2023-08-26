/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.collection;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.Dependency;
import com.b2international.snowowl.core.plugin.ClassPathScanner;

/**
 * @since 9.0
 */
public interface TerminologyResourceCollectionToolingSupport {

	/**
	 * @since 9.0
	 */
	public static final class Registry {

		private final Map<String, TerminologyResourceCollectionToolingSupport> collectionToolingSupportImplementations = new HashMap<>();

		public Registry(ClassPathScanner scanner) {
			scanner.getComponentsByInterface(TerminologyResourceCollectionToolingSupport.class).forEach(this::register);
		}

		public void register(TerminologyResourceCollectionToolingSupport toolingSupport) {
			checkArgument(!CompareUtils.isEmpty(toolingSupport.getSupportedChildResourceTypes()), "'%s' resource collection tooling support does not define any valid child resource type.", toolingSupport.getClass().getName());
			toolingSupport.getSupportedChildResourceTypes().forEach(childResourceType -> {
				collectionToolingSupportImplementations.put(asKey(toolingSupport.getToolingId(), childResourceType), toolingSupport);
			});
		}

		public void unregister(TerminologyResourceCollectionToolingSupport toolingSupport) {
			toolingSupport.getSupportedChildResourceTypes().forEach(childResourceType -> {
				collectionToolingSupportImplementations.remove(asKey(toolingSupport.getToolingId(), childResourceType));
			});
		}

		public TerminologyResourceCollectionToolingSupport getToolingSupport(String toolingId, String childResourceType) {
			String key = asKey(toolingId, childResourceType);
			if (!collectionToolingSupportImplementations.containsKey(key)) {
				var supportedChildResourceTypes = collectionToolingSupportImplementations.values()
					.stream()
					.filter(support -> support.getToolingId().equals(toolingId))
					.flatMap(support -> support.getSupportedChildResourceTypes().stream())
					.collect(Collectors.toCollection(TreeSet::new));
				if (supportedChildResourceTypes.isEmpty()) {
					throw new BadRequestException("ToolingId '%s' is not supported for resource collections.", toolingId);
				} else {
					throw new BadRequestException("ToolingId '%s' and child resource type '%s' combination is not supported for resource collections. ToolingId '%s' supports the following child resource types: '%s'.", toolingId, childResourceType, toolingId, supportedChildResourceTypes);
				}
			}
			return collectionToolingSupportImplementations.get(key);
		}

		private String asKey(String toolingId, String childResourceType) {
			return String.join("#", toolingId, childResourceType);
		}

	}

	/**
	 * @return the toolingId to which this support class belongs to
	 */
	String getToolingId();

	/**
	 * Returns a Set of supported child terminology resource types.
	 * 
	 * @return a {@link Set} instance with at least one supported child resource type.
	 */
	Set<String> getSupportedChildResourceTypes();

	/**
	 * @return a {@link Set} of setting key values that are required to be present in the parent collection settings object when creating a child
	 *         resource of any of the matching types. These settings are considered inherited and cannot be changed on child resource level directly.
	 */
	default Set<String> getInheritedSettingKeys() {
		return Set.of();
	}

	/**
	 * Certain tooling implementations might require certain scoped dependencies to be present when creating a terminology resource.
	 * @param dependencies
	 * @throws BadRequestException - if a scoped dependency is required 
	 */
	default void validateRequiredDependencies(List<Dependency> dependencies) {
		
	}

}
