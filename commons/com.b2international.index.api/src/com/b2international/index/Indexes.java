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
package com.b2international.index;

import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;

import com.b2international.index.decimal.DecimalModule;
import com.b2international.index.mapping.Mappings;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class Indexes {

	private static ServiceLoader<IndexClientFactory> FACTORIES;
	
	static {
		FACTORIES = ServiceLoader.load(IndexClientFactory.class, Indexes.class.getClassLoader());
	}
	
	private Indexes() {
	}
	
	public static IndexClient createIndexClient(String name, ObjectMapper mapper, Mappings mappings) {
		return FACTORIES.iterator().next().createClient(name, configure(mapper), mappings, Collections.<String, Object>emptyMap());
	}
	
	public static IndexClient createIndexClient(String name, ObjectMapper mapper, Mappings mappings, Map<String, Object> settings) {
		return FACTORIES.iterator().next().createClient(name, configure(mapper), mappings, settings);
	}
	
	public static Index createIndex(String name, ObjectMapper mapper, Mappings mappings) {
		return new DefaultIndex(createIndexClient(name, mapper, mappings));
	}
	
	public static Index createIndex(String name, ObjectMapper mapper, Mappings mappings, Map<String, Object> settings) {
		return new DefaultIndex(createIndexClient(name, mapper, mappings, settings));
	}
	
	private static ObjectMapper configure(ObjectMapper mapper) {
		return mapper.copy().registerModule(new DecimalModule());
	}
	
}
