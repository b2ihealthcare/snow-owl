/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.HashMap;
import java.util.Map;

import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.plugin.ClassPathScanner;

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
			return resourceTypeConverters.get(doc.getResourceType()).toResource(doc);
		}
		
	}
	
	String getResourceType();
	
	Resource toResource(ResourceDocument doc);
	
}
