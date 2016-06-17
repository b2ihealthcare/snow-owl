/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.mapping;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.7
 */
public final class Mappings {

	private final Map<Class<?>, DocumentMapping> mappingsByType = newHashMap(); 
	
	public Mappings(Class<?>...types) {
		this(ImmutableSet.copyOf(types));
	}
	
	public Mappings(Collection<Class<?>> types) {
		checkArgument(!types.isEmpty(), "At least one document type should be specified");
		final Builder<Class<?>, DocumentMapping> builder = ImmutableMap.builder();
		for (Class<?> type : ImmutableSet.copyOf(types)) {
			// XXX register only root mappings, nested mappings should be looked up via the parent/ancestor mapping
			getMapping(type);
		}
	}
	
	public Collection<DocumentMapping> getMappings() {
		return ImmutableList.copyOf(mappingsByType.values());
	}
	
	public DocumentMapping getMapping(Class<?> type) {
		if (!mappingsByType.containsKey(type)) {
			mappingsByType.put(type, new DocumentMapping(type));
		}
		return mappingsByType.get(type);
	}

}
