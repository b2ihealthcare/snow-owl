/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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

import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.assertGetVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.collections.TerminologyResourceCollectionRestRequests.*;
import static com.b2international.snowowl.test.commons.collections.TerminologyResourceCollectionRestRequests.assertTerminologyResourceCollectionGet;
import static com.b2international.snowowl.test.commons.collections.TerminologyResourceCollectionRestRequests.createTerminologyResourceCollection;
import static com.b2international.snowowl.test.commons.collections.TerminologyResourceCollectionRestRequests.prepareTerminologyResourceCollectionCreateBody;
import static com.b2international.snowowl.test.commons.rest.BundleApiAssert.assertBundleGet;
import static com.b2international.snowowl.test.commons.rest.BundleApiAssert.createBundle;
import static com.b2international.snowowl.test.commons.rest.CodeSystemApiAssert.assertCodeSystemCreate;
import static com.b2international.snowowl.test.commons.rest.CodeSystemApiAssert.assertCodeSystemGet;
import static com.b2international.snowowl.test.commons.rest.CodeSystemApiAssert.createCodeSystem;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.nullValue;

import java.time.LocalDate;
import java.util.Set;

import org.elasticsearch.core.List;
import org.elasticsearch.core.Map;
import org.junit.After;
import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.collection.TerminologyResourceCollection;
import com.b2international.snowowl.core.collection.TerminologyResourceCollectionToolingSupport;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.rest.CodeSystemApiAssert;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 9.0.0
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
		
		@Override
		public Set<String> getInheritedSettingKeys() {
			return Set.of("customSetting");
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

	@After
	public void after() {
		// make sure we always unregister all custom tooling support for terminology collections
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).unregister(SNOMED_CODESYSTEM_CHILD_SUPPORT);
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).unregister(SNOMED_OTHER_CHILD_SUPPORT);
	}
	
	private void registerSnomedCodeSystemChildSupport() {
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).register(SNOMED_CODESYSTEM_CHILD_SUPPORT);
	}
	
	@Test
	public void create_UnsupportedChildResourceType() throws Exception {
		assertTerminologyResourceCollectionCreate()
			.statusCode(400)
			.body("message", equalTo("ToolingId 'snomed' is not supported for resource collections."));
	}
	
	@Test
	public void create() throws Exception {
		registerSnomedCodeSystemChildSupport();
		
		var collectionId = createTerminologyResourceCollection();
		
		assertTerminologyResourceCollectionGet(collectionId)
			.statusCode(200)
			.body("resourceURI", equalTo("collections/" + collectionId));
	}

	@Test
	public void create_BundleChildResource() throws Exception {
		registerSnomedCodeSystemChildSupport();
		
		var collectionId = createTerminologyResourceCollection();
		
		var bundleId = createBundle(IDs.base62UUID(), collectionId);
		
		assertBundleGet(bundleId, "resourcePathLabels()")
			.statusCode(200)
			.body("bundleId", equalTo(collectionId))
			.body("bundleAncestorIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("resourcePathLabels", equalTo(List.of("Root", "Title of " + collectionId)))
			.body("dependencies", nullValue());
		
		assertTerminologyResourceCollectionGet(collectionId, "content()")
			.statusCode(200)
			.body("content.items.id", hasItem(bundleId));
		
		assertTerminologyResourceCollectionSearch(
			Json.object(
				// XXX adding bundleId here as well to simulate that we request a non-collection type resource as well
				"id", Json.array(collectionId, bundleId) 
			)
		)
			.statusCode(200)
			.body("items.id", hasItem(collectionId));
	}
	
	@Test
	public void create_ValidChildResource() throws Exception {
		registerSnomedCodeSystemChildSupport();
		
		var collectionId = createTerminologyResourceCollection();
		
		var codeSystemId = createCodeSystem(IDs.base62UUID(), collectionId);
		
		assertCodeSystemGet(codeSystemId, "resourcePathLabels()")
			.statusCode(200)
			.body("bundleId", equalTo(collectionId))
			.body("bundleAncestorIds", equalTo(List.of(IComponent.ROOT_ID)))
			.body("resourcePathLabels", equalTo(List.of("Root", "Title of " + collectionId)))
			.body("dependencies", equalTo(List.of(Map.of("uri", "collections/" + collectionId))));
		
		assertTerminologyResourceCollectionGet(collectionId, "content()")
			.statusCode(200)
			.body("content.items.id", hasItem(codeSystemId));
	}
	
	@Test
	public void create_InvalidChildResource() throws Exception {
		ApplicationContext.getServiceForClass(TerminologyResourceCollectionToolingSupport.Registry.class).register(SNOMED_OTHER_CHILD_SUPPORT);
		
		var collectionId = createTerminologyResourceCollection();
		
		assertCodeSystemCreate(IDs.base62UUID(), collectionId)
			.statusCode(400)
			.body("message", equalTo("ToolingId 'snomed' and child resource type 'codesystems' combination is not supported for resource collections. ToolingId 'snomed' supports the following child resource types: '[other]'."));
	}
	
	@Test
	public void create_ChildResourceInheritsSettings() throws Exception {
		registerSnomedCodeSystemChildSupport();
		
		var collectionId = createTerminologyResourceCollection(prepareTerminologyResourceCollectionCreateBody(IDs.base62UUID()).with("settings", Map.of("customSetting", "customSettingValue")));
		
		var codeSystemId = createCodeSystem(IDs.base62UUID(), collectionId);
		assertCodeSystemGet(codeSystemId)
			.statusCode(200)
			.body("settings.customSetting", equalTo("customSettingValue"));
	}
	
	@Test
	public void create_ChildResourceInheritsSettingsThroughAncestors() throws Exception {
		registerSnomedCodeSystemChildSupport();
		
		var collectionId = createTerminologyResourceCollection(prepareTerminologyResourceCollectionCreateBody(IDs.base62UUID()).with("settings", Map.of("customSetting", "customSettingValue")));
		
		var bundleId = createBundle(IDs.base62UUID(), collectionId);
		
		var codeSystemId = createCodeSystem(IDs.base62UUID(), bundleId);
		assertCodeSystemGet(codeSystemId)
			.statusCode(200)
			.body("settings.customSetting", equalTo("customSettingValue"));
	}
	
	@Test
	public void create_UnderBundle() throws Exception {
		registerSnomedCodeSystemChildSupport();
		
		var bundleId = createBundle(IDs.base62UUID());
		assertTerminologyResourceCollectionCreate(prepareTerminologyResourceCollectionCreateBody(IDs.base62UUID()).with("bundleId", bundleId))
			.statusCode(201);
	}
	
	@Test
	public void create_UnderAnotherTerminologyResourceCollection() throws Exception {
		registerSnomedCodeSystemChildSupport();
		
		var collectionId = createTerminologyResourceCollection(prepareTerminologyResourceCollectionCreateBody(IDs.base62UUID()).with("settings", Map.of("customSetting", "customSettingValue")));
		
		assertTerminologyResourceCollectionCreate(prepareTerminologyResourceCollectionCreateBody(IDs.base62UUID()).with("bundleId", collectionId))
			.statusCode(400)
			.body("message", equalTo("Nesting terminology collection resources is not supported. Use regular bundles to organize content."));
	}
	
	@Test
	public void version_CollectionResource() throws Exception {
		registerSnomedCodeSystemChildSupport();
		
		var collectionId = createTerminologyResourceCollection(prepareTerminologyResourceCollectionCreateBody(IDs.base62UUID()).with("settings", Map.of("customSetting", "customSettingValue")));
		
		var codeSystemId1 = createCodeSystem(IDs.base62UUID(), collectionId);
		var codeSystemId2 = createCodeSystem(IDs.base62UUID(), collectionId);
		
		createVersion(TerminologyResourceCollection.uri(collectionId), "v1", LocalDate.now(), false)
			.statusCode(201);
		
		// collection versioning should generate versions for each child resource as well
		assertGetVersion(codeSystemId1, "v1").statusCode(200);
		assertGetVersion(codeSystemId2, "v1").statusCode(200);
	}
	
	@Test
	public void versionCollectionResourceWithRetiredChild() throws Exception {
		registerSnomedCodeSystemChildSupport();
		
		var collectionId = createTerminologyResourceCollection(prepareTerminologyResourceCollectionCreateBody(IDs.base62UUID()).with("settings", Map.of("customSetting", "customSettingValue")));
		
		var codeSystemId1 = createCodeSystem(IDs.base62UUID(), collectionId);
		var codeSystemId2 = createCodeSystem(IDs.base62UUID(), collectionId);
		
		// deprecate codeSystem2
		CodeSystemApiAssert.assertCodeSystemUpdated(codeSystemId2, Json.object(
			"status", "retired",
			"commitComment", "Retire " + codeSystemId2
		));
		
		createVersion(TerminologyResourceCollection.uri(collectionId), "v1", LocalDate.now(), false)
			.statusCode(201);
	
		// collection versioning should generate versions for each active child resource
		assertGetVersion(codeSystemId1, "v1").statusCode(200);
		assertGetVersion(codeSystemId2, "v1").statusCode(404);
	}
	
}
