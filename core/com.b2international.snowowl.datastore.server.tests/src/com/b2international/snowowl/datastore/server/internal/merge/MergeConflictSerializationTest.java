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
package com.b2international.snowowl.datastore.server.internal.merge;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.merge.ConflictingAttribute;
import com.b2international.snowowl.core.merge.ConflictingAttributeImpl;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class MergeConflictSerializationTest {

	private MergeConflict conflict;
	private ObjectMapper mapper;
	private ConflictingAttributeImpl attribute;
	
	@Before
	public void before() {
		this.mapper = new ObjectMapper();
		
		this.conflict = MergeConflict.builder()
				.componentId("id")
				.componentType("type")
				.type(ConflictType.CONFLICTING_CHANGE)
				.build();
		
		this.attribute = ConflictingAttributeImpl.builder()
							.property("property")
							.oldValue("oldValue")
							.value("value")
							.build();
	}
	
	@Test
	public void serializeConflictWithOutAttributes() throws Exception {
		String json = mapper.writeValueAsString(conflict);
		String result = 
				"{\"componentId\":\"id\","
				+ "\"componentType\":\"type\","
				+ "\"conflictingAttributes\":[],"
				+ "\"type\":\"CONFLICTING_CHANGE\","
				+ "\"message\":\"type with ID 'id' has a conflict of type 'CONFLICTING_CHANGE' on target branch.\"}";
		assertEquals(json, result);
	}

	@Test
	public void serializeConflictWithOnlyPropertyAttribute() throws Exception {
		MergeConflict conflictWithAttribute = MergeConflict.builder(this.conflict)
				.conflictingAttribute(ConflictingAttributeImpl.builder().property("property").build()).build();
		String json = mapper.writeValueAsString(conflictWithAttribute);
		String result = 
				"{\"componentId\":\"id\","
				+ "\"componentType\":\"type\","
				+ "\"conflictingAttributes\":[{\"property\":\"property\"}],"
				+ "\"type\":\"CONFLICTING_CHANGE\","
				+ "\"message\":\"type with ID 'id' has a conflict of type 'CONFLICTING_CHANGE' on target branch, conflicting attributes are: [property].\"}";
		assertEquals(json, result);
	}
	
	@Test
	public void serializeConflictWithPropertyAndOldValueAttribute() throws Exception {
		MergeConflict conflictWithAttribute = MergeConflict.builder(this.conflict)
				.conflictingAttribute(ConflictingAttributeImpl.builder().property("property").oldValue("oldValue").build()).build();
		String json = mapper.writeValueAsString(conflictWithAttribute);
		String result = 
				"{\"componentId\":\"id\","
				+ "\"componentType\":\"type\","
				+ "\"conflictingAttributes\":[{\"property\":\"property\",\"oldValue\":\"oldValue\"}],"
				+ "\"type\":\"CONFLICTING_CHANGE\","
				+ "\"message\":\"type with ID 'id' has a conflict of type 'CONFLICTING_CHANGE' on target branch, conflicting attributes are: [property -> oldValue].\"}";
		assertEquals(json, result);
	}
	
	@Test
	public void serializeConflictWithPropertyAndValueAttribute() throws Exception {
		MergeConflict conflictWithAttribute = MergeConflict.builder(this.conflict)
				.conflictingAttribute(ConflictingAttributeImpl.builder().property("property").value("value").build()).build();
		String json = mapper.writeValueAsString(conflictWithAttribute);
		String result = 
				"{\"componentId\":\"id\","
				+ "\"componentType\":\"type\","
				+ "\"conflictingAttributes\":[{\"property\":\"property\",\"value\":\"value\"}],"
				+ "\"type\":\"CONFLICTING_CHANGE\","
				+ "\"message\":\"type with ID 'id' has a conflict of type 'CONFLICTING_CHANGE' on target branch, conflicting attributes are: [property -> value].\"}";
		assertEquals(json, result);
	}
	
	@Test
	public void serializeConflictWithFullAttribute() throws Exception {
		MergeConflict conflictWithAttribute = MergeConflict.builder(this.conflict).conflictingAttribute(attribute).build();
		String json = mapper.writeValueAsString(conflictWithAttribute);
		String result = 
				"{\"componentId\":\"id\","
				+ "\"componentType\":\"type\","
				+ "\"conflictingAttributes\":[{\"property\":\"property\",\"value\":\"value\",\"oldValue\":\"oldValue\"}],"
				+ "\"type\":\"CONFLICTING_CHANGE\","
				+ "\"message\":\"type with ID 'id' has a conflict of type 'CONFLICTING_CHANGE' on target branch, conflicting attributes are: [property -> old value: oldValue, value: value].\"}";
		assertEquals(json, result);
	}

	@Test
	public void serializeConflictWithMultipleFullAttributes() throws Exception {
		MergeConflict conflictWithAttribute = MergeConflict
				.builder(this.conflict)
				.conflictingAttribute(attribute)
				.conflictingAttribute(ConflictingAttributeImpl.builder().property("property2").oldValue("oldValue2").value("value2").build())
				.build();
		String json = mapper.writeValueAsString(conflictWithAttribute);
		String result = 
				"{\"componentId\":\"id\","
				+ "\"componentType\":\"type\","
				+ "\"conflictingAttributes\":[{\"property\":\"property\",\"value\":\"value\",\"oldValue\":\"oldValue\"},{\"property\":\"property2\",\"value\":\"value2\",\"oldValue\":\"oldValue2\"}],"
				+ "\"type\":\"CONFLICTING_CHANGE\","
				+ "\"message\":\"type with ID 'id' has a conflict of type 'CONFLICTING_CHANGE' on target branch, conflicting attributes are: [property -> old value: oldValue, value: value; property2 -> old value: oldValue2, value: value2].\"}";
		assertEquals(json, result);
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
		assertEquals("value", attr.getValue());
		
		assertEquals(MergeConflict.buildDefaultMessage("id", "type", conflict.getConflictingAttributes(), ConflictType.CONFLICTING_CHANGE), conflict.getMessage());
	}
}
