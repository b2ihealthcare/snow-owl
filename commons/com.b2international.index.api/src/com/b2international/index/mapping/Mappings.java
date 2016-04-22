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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/**
 * @since 4.7
 */
public class Mappings {

	private ObjectMapper mapper;
	private Map<Class<?>, MappingStrategy<?>> mappings;
	
	public Mappings(ObjectMapper mapper) {
		this(mapper, Maps.<Class<?>, MappingStrategy<?>>newHashMap());
	}
	
	Mappings(Mappings mappings) {
		this(mappings.mapper, newHashMap(mappings.mappings));
	}
	
	Mappings(ObjectMapper mapper, Map<Class<?>, MappingStrategy<?>> mappings) {
		this.mapper = mapper;
		this.mappings = mappings;
	}
	
	public void addMapping(Class<?> type) {
		// TODO provider of mapping strategy???
		final MappingStrategy<?> strategy = new DefaultMappingStrategy<>(mapper, type);
		mappings.put(type, strategy);
	}

	public ObjectMapper mapper() {
		return mapper;
	}
	
	@SuppressWarnings("unchecked")
	public <T> MappingStrategy<T> getMapping(Class<T> type) {
		MappingStrategy<?> strategy = mappings.get(type);
		if (strategy == null) {
			strategy = tryFindStrategy(type);
		}
		return (MappingStrategy<T>) checkNotNull(strategy, "Mapping may not be null for %s", type) ;
	}
	
	private MappingStrategy<?> tryFindStrategy(Class<?> type) {
		MappingStrategy<?> strategy = mappings.get(type);
		if (type.getSuperclass() != null) {
			strategy = tryFindStrategy(type.getSuperclass());
		}
		if (strategy == null) {
			for (Class<?> iface : type.getInterfaces()) {
				strategy = tryFindStrategy(iface);
				if (strategy != null) {
					break;
				}
			}
		}
		return strategy;
	}
	
	public Collection<MappingStrategy<?>> getMappings() {
		return ImmutableList.copyOf(mappings.values());
	}

	@SafeVarargs
	public static Mappings of(ObjectMapper mapper, Class<?>...types) {
		final Mappings mappings = new Mappings(mapper);
		for (Class<?> clazz : types) {
			mappings.addMapping(clazz);
		}
		return mappings;
	}

	@SafeVarargs
	public static Mappings of(Mappings mappings, Class<?>...types) {
		final Mappings newMappings = new Mappings(mappings);
		for (Class<?> clazz : types) {
			newMappings.addMapping(clazz);
		}
		return newMappings;
	}

}