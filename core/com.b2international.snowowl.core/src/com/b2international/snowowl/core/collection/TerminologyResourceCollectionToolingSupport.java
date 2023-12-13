/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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
 * @since 9.0.0
 */
public interface TerminologyResourceCollectionToolingSupport {

	/**
	 * @since 9.0.0
	 */
	public static final class Registry {

		private record RegistryKey(String toolingId, String childResourceType) {
			static RegistryKey of(String toolingId, String childResourceType) {
				return new RegistryKey(toolingId, childResourceType);
			}
		}
		
		private final Map<RegistryKey, TerminologyResourceCollectionToolingSupport> collectionToolingSupportImplementations = new HashMap<>();

		public Registry(ClassPathScanner scanner) {
			scanner.getComponentsByInterface(TerminologyResourceCollectionToolingSupport.class).forEach(this::register);
		}
		
		public List<TerminologyResourceCollectionToolingSupport> getAllByToolingId(String toolingId) {
			return collectionToolingSupportImplementations.values().stream().filter(support -> support.getToolingId().equals(toolingId)).toList();
		}

		public void register(TerminologyResourceCollectionToolingSupport toolingSupport) {
			checkArgument(!CompareUtils.isEmpty(toolingSupport.getSupportedChildResourceTypes()), "'%s' resource collection tooling support does not define any valid child resource type.", toolingSupport.getClass().getName());
			toolingSupport.getSupportedChildResourceTypes().forEach(childResourceType -> {
				collectionToolingSupportImplementations.put(RegistryKey.of(toolingSupport.getToolingId(), childResourceType), toolingSupport);
			});
		}

		public void unregister(TerminologyResourceCollectionToolingSupport toolingSupport) {
			toolingSupport.getSupportedChildResourceTypes().forEach(childResourceType -> {
				collectionToolingSupportImplementations.remove(RegistryKey.of(toolingSupport.getToolingId(), childResourceType));
			});
		}

		public TerminologyResourceCollectionToolingSupport getToolingSupport(String toolingId, String childResourceType) {
			RegistryKey key = RegistryKey.of(toolingId, childResourceType);
			if (!collectionToolingSupportImplementations.containsKey(key)) {
				var supportedChildResourceTypes = collectionToolingSupportImplementations.values()
					.stream()
					.filter(support -> support.getToolingId().equals(toolingId))
					.flatMap(support -> support.getSupportedChildResourceTypes().stream())
					.collect(Collectors.toCollection(TreeSet::new));
				throw new BadRequestException("ToolingId '%s' and child resource type '%s' combination is not supported for resource collections. ToolingId '%s' supports the following child resource types: '%s'.", toolingId, childResourceType, toolingId, supportedChildResourceTypes);
			}
			return collectionToolingSupportImplementations.get(key);
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
	 * Certain tooling implementations might require additional validation checks to be performed when creating a new resource under a terminology collection with specific dependencies.
	 * 
	 * @param resourceCollection - the resource parent collection
	 * @param dependencies - the new child resource's explicitly set dependency list
	 * @throws BadRequestException - if any of the child resource's explicitly set dependency entry is forbidden to be used within the given resource collection
	 */
	default void validateChildResourceDependencies(TerminologyResourceCollection resourceCollection, List<Dependency> dependencies) {
		
	}

}
