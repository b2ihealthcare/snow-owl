/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @since 4.1
 */
public class MetadataHolderTest {

	private MetadataHolderImpl holder;
	private ObjectMapper mapper;

	@Before
	public void givenMetadataHolder() {
		this.holder = new MetadataHolderImpl();
		this.mapper = new ObjectMapper();
		this.mapper.addMixInAnnotations(Metadata.class, MetadataMixin.class);
		this.mapper.addMixInAnnotations(MetadataHolder.class, MetadataHolderMixin.class);
	}
	
	@Test
	public void whenSerializingEmptyMetadataToJSON_ThenItShouldSerializeAsEmptyObject() throws Exception {
		assertEquals("{\"metadata\":{}}", serialize());
	}

	@Test
	public void whenSerializingMetadataWithKeyValueToJSON_ThenItShouldBeSerialized() throws Exception {
		holder.metadata().put("key", "value");
		assertEquals("{\"metadata\":{\"key\":\"value\"}}", serialize());
	}
	
	@Test
	public void whenDeserializingMetadataWithKeyValueToJSON_ThenItShouldBeDeserializedAsInternalMetadataObject() throws Exception {
		holder.metadata().put("key", "value");
		final String json = serialize();
		final MetadataHolder holder = deserialize(json);
		assertEquals(holder.metadata().getString("key"), "value");
	}
	
	private MetadataHolder deserialize(String json) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, MetadataHolderImpl.class);
	}

	private String serialize() throws JsonProcessingException {
		return mapper.writeValueAsString(holder);
	}
	
}
