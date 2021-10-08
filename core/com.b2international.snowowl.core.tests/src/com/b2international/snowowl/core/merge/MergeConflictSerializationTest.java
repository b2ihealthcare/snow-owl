/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.merge;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class MergeConflictSerializationTest {

	private MergeConflict conflict;
	private ObjectMapper mapper;
	private ConflictingAttribute attribute;
	
	@Before
	public void before() {
		this.mapper = new ObjectMapper();
		
		this.conflict = MergeConflict.builder()
				.componentId("id")
				.componentType("type")
				.type(ConflictType.CONFLICTING_CHANGE)
				.build();
		
		this.attribute = ConflictingAttribute.builder()
							.property("property")
							.oldValue("oldValue")
							.sourceValue("value")
							.build();
	}
	
	@Test
	public void serializeConflictWithOutAttributes() throws Exception {
		String json = mapper.writeValueAsString(conflict);
		String expected = 
				"{\"componentId\":\"id\","
				+ "\"componentType\":\"type\","
				+ "\"conflictingAttributes\":[],"
				+ "\"type\":\"CONFLICTING_CHANGE\","
				+ "\"message\":\"type with ID 'id' has a conflict of type 'CONFLICTING_CHANGE' on target branch.\"}";
		assertEquals(expected, json);
	}

	@Test
	public void serializeConflictWithOnlyPropertyAttribute() throws Exception {
		MergeConflict conflictWithAttribute = MergeConflict.builder(this.conflict)
				.conflictingAttribute(ConflictingAttribute.builder().property("property").build()).build();
		String json = mapper.writeValueAsString(conflictWithAttribute);
		String expected = 
				"{\"componentId\":\"id\","
				+ "\"componentType\":\"type\","
				+ "\"conflictingAttributes\":[{\"property\":\"property\"}],"
				+ "\"type\":\"CONFLICTING_CHANGE\","
				+ "\"message\":\"type with ID 'id' has a conflict of type 'CONFLICTING_CHANGE' on target branch, conflicting attributes are: [property].\"}";
		assertEquals(expected, json);
	}
	
	@Test
	public void serializeConflictingAttributeWithOldValue() throws Exception {
		MergeConflict conflictWithAttribute = MergeConflict.builder(this.conflict)
				.conflictingAttribute(ConflictingAttribute.builder().property("property").sourceValue("value").oldValue("oldValue").build()).build();
		String json = mapper.writeValueAsString(conflictWithAttribute);
		String expected = 
				"{\"componentId\":\"id\","
				+ "\"componentType\":\"type\","
				+ "\"conflictingAttributes\":[{\"property\":\"property\",\"sourceValue\":\"value\",\"oldValue\":\"oldValue\"}],"
				+ "\"type\":\"CONFLICTING_CHANGE\","
				+ "\"message\":\"type with ID 'id' has a conflict of type 'CONFLICTING_CHANGE' on target branch, conflicting attributes are: [property -> value (old value: oldValue)].\"}";
		assertEquals(expected, json);
	}
	
	@Test
	public void serializeConflictingAttributeWithoutOldValue() throws Exception {
		MergeConflict conflictWithAttribute = MergeConflict.builder(this.conflict)
				.conflictingAttribute(ConflictingAttribute.builder().property("property").sourceValue("value").build()).build();
		String json = mapper.writeValueAsString(conflictWithAttribute);
		String expected = 
				"{\"componentId\":\"id\","
				+ "\"componentType\":\"type\","
				+ "\"conflictingAttributes\":[{\"property\":\"property\",\"sourceValue\":\"value\"}],"
				+ "\"type\":\"CONFLICTING_CHANGE\","
				+ "\"message\":\"type with ID 'id' has a conflict of type 'CONFLICTING_CHANGE' on target branch, conflicting attributes are: [property -> value (old value: n/a)].\"}";
		assertEquals(expected, json);
	}
	
	@Test
	public void serializeConflictWithFullAttribute() throws Exception {
		MergeConflict conflictWithAttribute = MergeConflict.builder(this.conflict).conflictingAttribute(attribute).build();
		String json = mapper.writeValueAsString(conflictWithAttribute);
		String expected = 
				"{\"componentId\":\"id\","
				+ "\"componentType\":\"type\","
				+ "\"conflictingAttributes\":[{\"property\":\"property\",\"sourceValue\":\"value\",\"oldValue\":\"oldValue\"}],"
				+ "\"type\":\"CONFLICTING_CHANGE\","
				+ "\"message\":\"type with ID 'id' has a conflict of type 'CONFLICTING_CHANGE' on target branch, conflicting attributes are: [property -> value (old value: oldValue)].\"}";
		assertEquals(expected, json);
	}

	@Test
	public void serializeConflictWithMultipleFullAttributes() throws Exception {
		MergeConflict conflictWithAttribute = MergeConflict
				.builder(this.conflict)
				.conflictingAttribute(attribute)
				.conflictingAttribute(ConflictingAttribute.builder().property("property2").oldValue("oldValue2").sourceValue("value2").build())
				.build();
		String json = mapper.writeValueAsString(conflictWithAttribute);
		String expected = 
				"{\"componentId\":\"id\","
				+ "\"componentType\":\"type\","
				+ "\"conflictingAttributes\":[{\"property\":\"property\",\"sourceValue\":\"value\",\"oldValue\":\"oldValue\"},{\"property\":\"property2\",\"sourceValue\":\"value2\",\"oldValue\":\"oldValue2\"}],"
				+ "\"type\":\"CONFLICTING_CHANGE\","
				+ "\"message\":\"type with ID 'id' has a conflict of type 'CONFLICTING_CHANGE' on target branch, conflicting attributes are: [property -> value (old value: oldValue); property2 -> value2 (old value: oldValue2)].\"}";
		assertEquals(expected, json);
	}
	
	@Test
	public void deserializeConflictWithOutAttributes() throws Exception {
		String json = mapper.writeValueAsString(conflict);
		MergeConflict conflict = mapper.readValue(json, MergeConflict.class);
		assertEquals("id", conflict.getComponentId());
		assertEquals("type", conflict.getComponentType());
		assertEquals(ConflictType.CONFLICTING_CHANGE.name(), conflict.getType().name());
		assertEquals(MergeConflict.buildDefaultMessage("id", "type", Collections.<ConflictingAttribute>emptyList(), ConflictType.CONFLICTING_CHANGE), conflict.getMessage());
	}
	
	@Test
	public void deserializeConflictWithFullAttributes() throws Exception {
		MergeConflict conflictWithAttribute = MergeConflict.builder(this.conflict).conflictingAttribute(attribute).build();
		String json = mapper.writeValueAsString(conflictWithAttribute);
		
		MergeConflict conflict = mapper.readValue(json, MergeConflict.class);
		assertEquals("id", conflict.getComponentId());
		assertEquals("type", conflict.getComponentType());
		assertEquals(ConflictType.CONFLICTING_CHANGE.name(), conflict.getType().name());
		
		assertEquals(1, conflict.getConflictingAttributes().size());
		ConflictingAttribute attr = conflict.getConflictingAttributes().get(0);
		assertEquals("property", attr.getProperty());
		assertEquals("oldValue", attr.getOldValue());
		assertEquals("value", attr.getSourceValue());
		
		assertEquals(MergeConflict.buildDefaultMessage("id", "type", conflict.getConflictingAttributes(), ConflictType.CONFLICTING_CHANGE), conflict.getMessage());
	}
}
