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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.exceptions.BadRequestException;
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
			collectionToolingSupportImplementations.put(toolingSupport.getToolingId(), toolingSupport);
		}

		public void unregister(TerminologyResourceCollectionToolingSupport toolingSupport) {
			collectionToolingSupportImplementations.remove(toolingSupport.getToolingId());
		}

		public TerminologyResourceCollectionToolingSupport getToolingSupport(String toolingId) {
			if (!collectionToolingSupportImplementations.containsKey(toolingId)) {
				throw new BadRequestException("ToolingId '%s' is not supported to be used for resource collections.", toolingId);
			}
			return collectionToolingSupportImplementations.get(toolingId);
		}

	}

	/**
	 * @return the toolingId to which this support class is registered
	 */
	String getToolingId();

	/**
	 * Returns a Set of supported child terminology resource types.
	 * 
	 * @return a {@link Set} instance with at least one supported child resource type.
	 */
	Set<String> getSupportedChildResourceTypes();

	/**
	 * @return a {@link Map} of dependency scope key-value pairs where keys are required to be present in the parent collection dependency array when
	 *         creating a child resource of any of the matching types. These dependencies are considered inherited and will inherit with the scope
	 *         mapped to the parent colleciton's dependency scope. These scope values cannot be changed on child resource level.
	 * 
	 */
	default Map<String, String> getInheritedDependencyScopeMapping() {
		return Map.of();
	}

	/**
	 * @return a {@link Set} of setting key values that are required to be present in the parent collection settings object when creating a child
	 *         resource of any of the matching types. These settings are considered inherited and cannot be changed on child resource level directly.
	 */
	default Set<String> getInheritedSettingKeys() {
		return Set.of();
	}

}
