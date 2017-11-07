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
package com.b2international.snowowl.datastore.server.internal.branch;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.MetadataImpl;
import com.b2international.snowowl.datastore.server.internal.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.1
 */
public class BranchSerializationTest {

	private BranchImpl branch;
	private ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
	private CDOBranchImpl cdoBranch;

	@Before
	public void givenBranch() {
		this.branch = new BranchImpl("name", "parent", 0L, 0L, false, new MetadataImpl());
		this.cdoBranch = new CDOBranchImpl("name", "parent", 0L, 0L, false, new MetadataImpl(), 1, 0, Collections.singleton(0), Collections.singleton(0));
	}
	
	@Test
	public void serializeBranchWithoutMetadata() throws Exception {
		final String json = mapper.writeValueAsString(branch);
		assertEquals("{\"type\":\"BranchImpl\",\"name\":\"name\",\"parentPath\":\"parent\",\"baseTimestamp\":0,\"headTimestamp\":0,\"deleted\":false,\"metadata\":{},\"path\":\"parent/name\"}", json);
	}
	
	@Test
	public void serializeBranchWithMetadata() throws Exception {
		branch.metadata().put("description", "Project A branch");
		final String json = mapper.writeValueAsString(branch);
		assertEquals("{\"type\":\"BranchImpl\",\"name\":\"name\",\"parentPath\":\"parent\",\"baseTimestamp\":0,\"headTimestamp\":0,\"deleted\":false,\"metadata\":{\"description\":\"Project A branch\"},\"path\":\"parent/name\"}", json);
	}
	
	@Test
	public void deserializeBranchWithoutMetadata() throws Exception {
		final String json = mapper.writeValueAsString(branch);
		mapper.readValue(json, BranchImpl.class);
	}
	
	@Test
	public void deserializeBranchWithMetadata() throws Exception {
		branch.metadata().put("description", "Project A branch");
		final String json = mapper.writeValueAsString(branch);
		final BranchImpl value = mapper.readValue(json, BranchImpl.class);
		assertEquals("name", value.name());
		assertEquals("parent", value.parentPath());
		assertEquals(0L, value.baseTimestamp());
		assertEquals(0L, value.headTimestamp());
		assertEquals(false, value.isDeleted());
		assertEquals("Project A branch", value.metadata().get("description"));
	}
	
	@Test
	public void serializeCDOBranchImpl() throws Exception {
		final String json = mapper.writeValueAsString(cdoBranch);
		assertEquals("{\"type\":\"CDOBranchImpl\",\"name\":\"name\",\"parentPath\":\"parent\",\"baseTimestamp\":0,\"headTimestamp\":0,\"deleted\":false,\"metadata\":{},\"segmentId\":0,\"segments\":[0],\"parentSegments\":[0],\"path\":\"parent/name\",\"cdoBranchId\":1}", json);
	}
	
	@Test
	public void deserializeCDOBranchImpl() throws Exception {
		final String json = mapper.writeValueAsString(cdoBranch);
		mapper.readValue(json, CDOBranchImpl.class);
	}
	
	@Test
	public void serializeCDOMainBranchImpl() throws Exception {
		final CDOMainBranchImpl mainCdoBranch = new CDOMainBranchImpl(0L, 2L, new MetadataImpl(), 0, Collections.singleton(0));
		final String json = mapper.writeValueAsString(mainCdoBranch);
		assertEquals("{\"type\":\"CDOMainBranchImpl\",\"baseTimestamp\":0,\"headTimestamp\":2,\"metadata\":{},\"segmentId\":0,\"segments\":[0],\"name\":\"MAIN\",\"parentPath\":\"\",\"deleted\":false,\"path\":\"MAIN\",\"cdoBranchId\":0}", json);
		final CDOMainBranchImpl actual = mapper.readValue(json, CDOMainBranchImpl.class);
		assertEquals(mainCdoBranch.cdoBranchId(), actual.cdoBranchId());
		
	}
	
}
