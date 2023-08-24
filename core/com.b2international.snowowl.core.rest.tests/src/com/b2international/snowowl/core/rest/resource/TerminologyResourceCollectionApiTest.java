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

import static com.b2international.snowowl.test.commons.collections.TerminologyResourceCollectionRestRequests.assertTerminologyResourceCollectionCreate;
import static com.b2international.snowowl.test.commons.collections.TerminologyResourceCollectionRestRequests.assertTerminologyResourceCollectionGet;
import static com.b2international.snowowl.test.commons.collections.TerminologyResourceCollectionRestRequests.createTerminologyResourceCollection;
import static com.b2international.snowowl.test.commons.rest.BundleApiAssert.assertBundleGet;
import static com.b2international.snowowl.test.commons.rest.BundleApiAssert.createBundle;
import static com.b2international.snowowl.test.commons.rest.CodeSystemApiAssert.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import java.util.Set;

import org.elasticsearch.core.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.collection.TerminologyResourceCollectionToolingSupport;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 9.0
 */
public class TerminologyResourceCollectionApiTest {

	private static final TerminologyResourceCollectionToolingSupport SNOMED_CODESYSTEM_CHILD_SUPPORT = new TerminologyResourceCollectionToolingSupport() {
		
		@Override
		public String getToolingId() {
			return SnomedTerminologyComponentConstants.TOOLING_ID;
		}
		
		@Override
		public Set<String> getSupportedChildResourceTypes() {
			return ImmutableSortedSet.of(CodeSystem.RESOURCE_TYPE);
		}
	};
	
	private static final TerminologyResourceCollectionToolingSupport SNOMED_OTHER_CHILD_SUPPORT = new TerminologyResourceCollectionToolingSupport() {
		
		@Override
		public String getToolingId() {
			return SnomedTerminologyComponentConstants.TOOLING_ID;
		}
		
		@Override
		public Set<String> getSupportedChildResourceTypes() {
			return ImmutableSortedSet.of("other");
		}
	};

	@Before
	public void setup() {
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).register(SNOMED_CODESYSTEM_CHILD_SUPPORT);
	}
	
	@After
	public void after() {
		// make sure we always unregister all custom tooling support for terminology collections
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).unregister(SNOMED_CODESYSTEM_CHILD_SUPPORT);
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).unregister(SNOMED_OTHER_CHILD_SUPPORT);
	}
	
	@Test
	public void create_UnsupportedChildResourceType() throws Exception {
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).unregister(SNOMED_CODESYSTEM_CHILD_SUPPORT);
		
		assertTerminologyResourceCollectionCreate(CodeSystem.RESOURCE_TYPE)
			.statusCode(400)
			.body("message", equalTo("ToolingId 'snomed' is not supported to be used for resource collections."));
	}
	
	@Test
	public void create() throws Exception {
		var collectionId = createTerminologyResourceCollection(CodeSystem.RESOURCE_TYPE);
		
		assertTerminologyResourceCollectionGet(collectionId)
			.statusCode(200)
			.body("childResourceType", equalTo(CodeSystem.RESOURCE_TYPE));
	}
	
	@Test
	public void create_BundleChildResource() throws Exception {
		var collectionId = createTerminologyResourceCollection(CodeSystem.RESOURCE_TYPE);
		
		var bundleId = createBundle(IDs.base62UUID(), collectionId);
		
		assertBundleGet(bundleId, "resourcePathLabels()")
			.statusCode(200)
			.body("bundleId", equalTo(collectionId))
			.body("bundleAncestorIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("resourcePathLabels", equalTo(List.of("Root", "Title of " + collectionId)));
		
		assertTerminologyResourceCollectionGet(collectionId, "content()")
			.statusCode(200)
			.body("content.items.id", hasItem(bundleId));
	}
	
	@Test
	public void create_ValidChildResource() throws Exception {
		var collectionId = createTerminologyResourceCollection(CodeSystem.RESOURCE_TYPE);
		
		var codeSystemId = createCodeSystem(IDs.base62UUID(), collectionId);
		
		assertCodeSystemGet(codeSystemId, "resourcePathLabels()")
			.statusCode(200)
			.body("bundleId", equalTo(collectionId))
			.body("bundleAncestorIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("resourcePathLabels", equalTo(List.of("Root", "Title of " + collectionId)));
		
		assertTerminologyResourceCollectionGet(collectionId, "content()")
			.statusCode(200)
			.body("content.items.id", hasItem(codeSystemId));
	}
	
	@Test
	public void create_InvalidChildResource() throws Exception {
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).register(SNOMED_OTHER_CHILD_SUPPORT);
		
		var collectionId = createTerminologyResourceCollection("other");
		
		assertCodeSystemCreate(IDs.base62UUID(), collectionId)
			.statusCode(400)
			.body("message", equalTo("'codesystems' resources are not allowed to be created under parent collection '"+collectionId+"'. The allowed resource types are: 'other'."));
	}
	
}
