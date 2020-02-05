/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.options.Metadata;
import com.b2international.commons.options.MetadataHolder;
import com.b2international.commons.options.MetadataHolderMixin;
import com.b2international.commons.options.MetadataMixin;
import com.b2international.index.decimal.DecimalModule;
import com.b2international.index.es.EsIndexClientFactory;
import com.b2international.index.mapping.Mappings;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @since 4.7
 */
public class Indexes {

	private static final IndexClientFactory FACTORY = new EsIndexClientFactory();
	
	private Indexes() {
	}
	
	public static IndexClient createIndexClient(String name, ObjectMapper mapper, Mappings mappings) {
		return FACTORY.createClient(name, configure(mapper), mappings, Collections.<String, Object>emptyMap());
	}
	
	public static IndexClient createIndexClient(String name, ObjectMapper mapper, Mappings mappings, Map<String, Object> settings) {
		return FACTORY.createClient(name, configure(mapper), mappings, settings);
	}
	
	public static Index createIndex(String name, ObjectMapper mapper, Mappings mappings) {
		return new DefaultIndex(createIndexClient(name, mapper, mappings));
	}
	
	public static Index createIndex(String name, ObjectMapper mapper, Mappings mappings, Map<String, Object> settings) {
		return new DefaultIndex(createIndexClient(name, mapper, mappings, settings));
	}
	
	private static ObjectMapper configure(ObjectMapper mapper) {
		return mapper
				.registerModule(new DecimalModule())
				.addMixIn(Metadata.class, MetadataMixin.class)
				.addMixIn(MetadataHolder.class, MetadataHolderMixin.class)
				.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
	
}
