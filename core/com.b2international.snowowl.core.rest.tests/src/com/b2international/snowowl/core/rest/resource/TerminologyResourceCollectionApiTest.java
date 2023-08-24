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
package com.b2international.snowowl.core.rest.resource;

import static com.b2international.snowowl.core.rest.resource.TerminologyResourceCollectionRestRequests.assertTerminologyResourceCollectionCreate;
import static com.b2international.snowowl.core.rest.resource.TerminologyResourceCollectionRestRequests.assertTerminologyResourceCollectionGet;
import static com.b2international.snowowl.core.rest.resource.TerminologyResourceCollectionRestRequests.createTerminologyResourceCollection;
import static org.hamcrest.Matchers.equalTo;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.collection.TerminologyResourceCollectionToolingSupport;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 9.0
 */
public class TerminologyResourceCollectionApiTest {

	private static final String CHILD_RESOURCE_TYPE = CodeSystem.RESOURCE_TYPE;

	private static final TerminologyResourceCollectionToolingSupport TOOLING_SUPPORT = new TerminologyResourceCollectionToolingSupport() {
		
		@Override
		public String getToolingId() {
			return SnomedTerminologyComponentConstants.TOOLING_ID;
		}
		
		@Override
		public Set<String> getSupportedChildResourceTypes() {
			return ImmutableSortedSet.of(CHILD_RESOURCE_TYPE);
		}
	}; 

	@Before
	public void setup() {
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).register(TOOLING_SUPPORT);
	}
	
	@After
	public void after() {
		// make sure we always unregister the custom tooling support implementation
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).unregister(TOOLING_SUPPORT);
	}
	
	@Test
	public void create_UnsupportedChildResourceType() throws Exception {
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).unregister(TOOLING_SUPPORT);
		
		assertTerminologyResourceCollectionCreate(CHILD_RESOURCE_TYPE)
			.statusCode(400);
	}
	
	@Test
	public void create() throws Exception {
		var collectionId = createTerminologyResourceCollection(CHILD_RESOURCE_TYPE);
		
		assertTerminologyResourceCollectionGet(collectionId)
			.statusCode(200)
			.body("childResourceType", equalTo(CHILD_RESOURCE_TYPE));
	}
	
}
