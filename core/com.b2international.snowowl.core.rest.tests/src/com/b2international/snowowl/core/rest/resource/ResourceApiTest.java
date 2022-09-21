/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CODESYSTEM_LANGUAGE_CONFIG_KEY;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CODESYSTEM_NAMESPACE_CONFIG_KEY;
import static com.b2international.snowowl.test.commons.ApiTestConstants.RESOURCES_API;
import static com.b2international.snowowl.test.commons.rest.CodeSystemApiAssert.assertCodeSystemCreated;
import static com.b2international.snowowl.test.commons.rest.CodeSystemApiAssert.prepareCodeSystemCreateRequestBody;
import static com.b2international.snowowl.test.commons.rest.ResourceApiAssert.assertResourceGet;
import static com.b2international.snowowl.test.commons.rest.ResourceApiAssert.assertResourceSearch;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.request.CommitResult;
import com.b2international.snowowl.core.request.ResourceConverter;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.BundleApiAssert;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 8.0
 */
public class ResourceApiTest {

	private static final String DEFAULT_CODE_SYSTEM_TOOLING_ID = SnomedTerminologyComponentConstants.TOOLING_ID;
	private static final String DEFAULT_CODE_SYSTEM_LANGUAGE = "en";
	private static final String DEFAULT_CODE_SYSTEM_DESCRIPTION = "# Description";
	private static final String DEFAULT_CODE_SYSTEM_SHORT_NAME = "sn";
	private static final String DEFAULT_CODE_SYSTEM_OID = "oid";
	private static final String DEFAULT_OWNER = "defaultOwner";

	private static IEventBus bus;

	@BeforeClass
	public static void setup() {
		bus = Services.bus();
	}
	
	@After
	public void deleteAllResources() {
		ResourceRequests
			.prepareSearch()
			.buildAsync()
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES)
			.forEach(resource -> {
				ResourceRequests
				.prepareDelete(resource.getId())
				.build(RestExtensions.USER, "Delete " + resource.getId())
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES); 
			});
	}

	@Test
	public void noResourcesPresent() {
		assertResourceSearch().statusCode(200).body("items", iterableWithSize(0));
	}

	@Test
	public void searchAllResources() {
		createCodeSystemWithOid(DEFAULT_CODE_SYSTEM_SHORT_NAME, DEFAULT_CODE_SYSTEM_OID);

		assertResourceSearch().statusCode(200).body("items", iterableWithSize(1));
	}

	@Test
	public void getById() {
		createCodeSystemWithOid(DEFAULT_CODE_SYSTEM_SHORT_NAME, DEFAULT_CODE_SYSTEM_OID);

		assertResourceGet(DEFAULT_CODE_SYSTEM_SHORT_NAME)
			.statusCode(200)
			.body("id", equalTo(DEFAULT_CODE_SYSTEM_SHORT_NAME))
			.body("oid", equalTo(DEFAULT_CODE_SYSTEM_OID));
	}
	
	@Test
	public void filterByOid() {
		final String id1 = IDs.base62UUID();
		final String oid1 = "https://b2i.sg/" + id1;
		final String id2 = IDs.base62UUID();
		final String oid2 = "https://b2i.sg/" + id2;
		createCodeSystemWithOid(id1, oid1);
		createCodeSystemWithOid(id2, oid2);

		final List<Resource> resources = ResourceRequests.prepareSearch()
				.filterByOid(oid1)
				.buildAsync()
				.execute(bus)
				.getSync(1, TimeUnit.MINUTES)
				.getItems();
		
		assertThat(resources).extracting("id", "oid")
			.contains(tuple(id1, oid1))
			.doesNotContain(tuple(id2, oid2));
	}

	@Test
	public void filterByStatus() {
		final String id1 = IDs.base62UUID();
		final String id2 = IDs.base62UUID();
		createCodeSystemWithStatus(id1, "draft");
		createCodeSystemWithStatus(id2, "active");
		
		assertResourceSearch(Map.of("status", List.of("draft")))
			.statusCode(200)
			.body("total", equalTo(1))
			.body("items[0].id", equalTo(id1))
			.body("items[0].status", equalTo("draft"));
	}
	
	@Test
	public void filterByStatusNegated() {
		final String id1 = IDs.base62UUID();
		final String id2 = IDs.base62UUID();
		createCodeSystemWithStatus(id1, "draft");
		createCodeSystemWithStatus(id2, "active");
		
		assertResourceSearch(Map.of("status", List.of("-draft")))
			.statusCode(200)
			.body("total", equalTo(1))
			.body("items[0].id", equalTo(id2))
			.body("items[0].status", equalTo("active"));
	}
	
	@Test
	public void filterByOwnerNegated() {
		final String id1 = IDs.base62UUID();
		final String id2 = IDs.base62UUID();
		createCodeSystem(id1, "draft", DEFAULT_OWNER);
		createCodeSystem(id2, "active", "otherOwner");
		
		assertResourceSearch(Map.of("owner", List.of("-otherOwner")))
			.statusCode(200)
			.body("total", equalTo(1))
			.body("items[0].id", equalTo(id1))
			.body("items[0].owner", equalTo(DEFAULT_OWNER));
	}
	
	@Test
	public void filterByOwnerNegatedEmptyValue() {
		final String id1 = IDs.base62UUID();
		final String id2 = IDs.base62UUID();
		createCodeSystem(id1, "draft", DEFAULT_OWNER);
		createCodeSystem(id2, "active", "otherOwner");
		
		assertResourceSearch(Map.of("owner", List.of("-")))
			.statusCode(200)
			.body("total", equalTo(0));
	}
	
	@Test
	public void filterBySettings() {
		final String codesystemId1 = IDs.base62UUID();
		final String codesystemId2 = IDs.base62UUID();
		final String oid2 = "https://b2i.sg/" + codesystemId2;

		final String namespace = "1000198";
		final String module1 = "1234567891000198103";
		final String module2 = "9876543211000198107";
		final String languageRefsetId = "450828004";

		createCodeSystemWitSettings(codesystemId1, namespace, module1, module2, languageRefsetId);
		createCodeSystemWithOid(codesystemId2, oid2);
				
		assertResourceSearch(Map.of("settings", String.format("%s#%s", CODESYSTEM_NAMESPACE_CONFIG_KEY, namespace)))
			.statusCode(200).body("total", equalTo(1)).body("items[0].id", equalTo(codesystemId1));
		assertResourceSearch(Map.of("settings", String.format("%s#%s", CODESYSTEM_MODULES_CONFIG_KEY, module1)))
			.statusCode(200).body("total", equalTo(1)).body("items[0].id", equalTo(codesystemId1));
		assertResourceSearch(Map.of("settings", String.format("%s#%s", CODESYSTEM_MODULES_CONFIG_KEY, module2)))
			.statusCode(200).body("total", equalTo(1)).body("items[0].id", equalTo(codesystemId1));
		assertResourceSearch(Map.of("settings", String.format("%s.%s#%s", CODESYSTEM_LANGUAGE_CONFIG_KEY, "languageRefSetIds", languageRefsetId)))
			.statusCode(200).body("total", equalTo(1)).body("items[0].id", equalTo(codesystemId1));
		assertResourceSearch(Map.of("settings", String.format("%s#", CODESYSTEM_LANGUAGE_CONFIG_KEY)))
		.statusCode(400);
	}
	
	@Test(expected = BadRequestException.class)
	public void searchAfter() throws Exception {
		final String id1 = IDs.base62UUID();
		final String oid1 = "https://b2i.sg/" + id1;
		createCodeSystemWithOid(id1, oid1);

		CodeSystemRequests.prepareSearchCodeSystem()
			.filterById(id1)
			.setSearchAfter("codesystem")
			.buildAsync()
			.execute(bus)
			.getSync();
	}

	private CommitResult createCodeSystem(final String codeSystemId, final String status, final String owner) {
		return CodeSystemRequests.prepareNewCodeSystem()
			.setId(codeSystemId)
			.setTitle(codeSystemId)
			.setUrl(SnomedTerminologyComponentConstants.SNOMED_URI_DEV + "/" + codeSystemId)
			.setDescription(DEFAULT_CODE_SYSTEM_DESCRIPTION)
			.setLanguage(DEFAULT_CODE_SYSTEM_LANGUAGE)
			.setToolingId(DEFAULT_CODE_SYSTEM_TOOLING_ID)
			.setStatus(status)
			.setOwner(owner)
			.setOid("https://b2i.sg/" + codeSystemId)
			.build(RestExtensions.USER, String.format("New code system %s", codeSystemId))
			.execute(bus)
			.getSync();
	}
	
	private CommitResult createCodeSystemWithStatus(final String codeSystemId, final String status) {
		return createCodeSystem(codeSystemId, status, DEFAULT_OWNER);
	}
	
	private CommitResult createCodeSystemWithOid(final String codeSystemId, final String oid) {
		return CodeSystemRequests.prepareNewCodeSystem()
			.setId(codeSystemId)
			.setTitle(String.join(" - ", codeSystemId, oid))
			.setUrl(SnomedTerminologyComponentConstants.SNOMED_URI_DEV + "/" + codeSystemId)
			.setDescription(DEFAULT_CODE_SYSTEM_DESCRIPTION)
			.setLanguage(DEFAULT_CODE_SYSTEM_LANGUAGE)
			.setToolingId(DEFAULT_CODE_SYSTEM_TOOLING_ID)
			.setOid(oid)
			.build(RestExtensions.USER, String.format("New code system %s", codeSystemId))
			.execute(bus)
			.getSync();
	}
	
	private CommitResult createCodeSystemWitSettings(final String codeSystemId, final String namespace,
			final String module1, final String module2, final String languageRefsetId) {
		return CodeSystemRequests.prepareNewCodeSystem()
				.setId(codeSystemId)
				.setTitle(codeSystemId)
				.setUrl(SnomedTerminologyComponentConstants.SNOMED_URI_DEV + "/" + codeSystemId)
				.setDescription(DEFAULT_CODE_SYSTEM_DESCRIPTION)
				.setLanguage(DEFAULT_CODE_SYSTEM_LANGUAGE)
				.setToolingId(DEFAULT_CODE_SYSTEM_TOOLING_ID)
				.setOid("https://b2i.sg/" + codeSystemId)
				.setSettings( Map.of(
					CODESYSTEM_NAMESPACE_CONFIG_KEY, namespace,
					CODESYSTEM_MODULES_CONFIG_KEY, List.of(module1, module2),
					CODESYSTEM_LANGUAGE_CONFIG_KEY, 
						List.of(Map.of("languageRefSetIds", List.of(languageRefsetId)))
					))
				.build(RestExtensions.USER, String.format("New code system %s", codeSystemId))
				.execute(bus)
				.getSync();
	}

	@Test
	public void sortByResourceTypeAsc() {
		final String id1 = "A";
		final String id2 = "B";
		final String id3 = "C";
		final String id4 = "D";
		
		createCodeSystemWithStatus(id1, "active");
		createCodeSystemWithStatus(id2, "active");
		
		BundleApiAssert.createBundle(BundleApiAssert.prepareBundleCreateRequestBody(id3));
		BundleApiAssert.createBundle(BundleApiAssert.prepareBundleCreateRequestBody(id4));
		
		assertResourceSearch(Map.of("sort", List.of("typeRank:asc", "title:asc")))
			.statusCode(200)
			.body("items.id", contains(id3, id4, id1, id2));
	}
	
	@Test
	public void sortByResourceTypeDesc() {
		final String id1 = "A";
		final String id2 = "B";
		final String id3 = "C";
		final String id4 = "D";
		
		createCodeSystemWithStatus(id1, "active");
		createCodeSystemWithStatus(id2, "active");
		
		BundleApiAssert.createBundle(BundleApiAssert.prepareBundleCreateRequestBody(id3));
		BundleApiAssert.createBundle(BundleApiAssert.prepareBundleCreateRequestBody(id4));
		
		assertResourceSearch(Map.of("sort", List.of("typeRank:desc", "title:asc")))
			.statusCode(200)
			.body("items.id", contains(id1, id2, id3, id4));
	}
	
	@Test
	public void sortBySnomedFirst() throws Exception {
		final String id1 = "A";
		final String id2 = "B";
		final String id3 = "C";
		final String id4 = "D";
		
		createCodeSystemWithStatus(id1, "active");
		createCodeSystemWithStatus(id2, "active");
		
		BundleApiAssert.createBundle(BundleApiAssert.prepareBundleCreateRequestBody(id3));
		BundleApiAssert.createBundle(BundleApiAssert.prepareBundleCreateRequestBody(id4));
		
		assertResourceSearch(Map.of("sort", List.of("snomedFirst", "title:asc")))
			.statusCode(200)
			.body("items.id", contains(id1, id2, id3, id4));
	}
	
	@Test
	public void expandPathLabels() {
		final String rootBundleId = "rootBundleId";
		final String subBundleId = "subBundleId";
		
		BundleApiAssert.createBundle(BundleApiAssert.prepareBundleCreateRequestBody(rootBundleId));
		BundleApiAssert.createBundle(BundleApiAssert.prepareBundleCreateRequestBody(subBundleId, rootBundleId));
		assertCodeSystemCreated(prepareCodeSystemCreateRequestBody("cs1").with("bundleId", subBundleId));

		givenAuthenticatedRequest(RESOURCES_API)
			.when()
			.get("/cs1?expand=resourcePathLabels()")
			.then()
			.assertThat()
			.statusCode(200)
			.body("resourcePathSegments", contains(IComponent.ROOT_ID, rootBundleId, subBundleId)) 
			.body("resourcePathLabels", contains(ResourceConverter.ROOT_LABEL, "Bundle " + rootBundleId, "Bundle " + subBundleId));		
	}
	
	@Test
	public void createdAtAndUpdatedAt() throws Exception {
		createCodeSystemWithOid(DEFAULT_CODE_SYSTEM_SHORT_NAME, DEFAULT_CODE_SYSTEM_OID);
		
		// assert that createdAt and updatedAt values are the same after create
		CodeSystem createdCodeSystem = assertResourceGet(DEFAULT_CODE_SYSTEM_SHORT_NAME)
			.statusCode(200)
			.extract()
			.as(CodeSystem.class);
		assertEquals(createdCodeSystem.getCreatedAt(), createdCodeSystem.getUpdatedAt());
		
		CommitResult updateResult = CodeSystemRequests.prepareUpdateCodeSystem(DEFAULT_CODE_SYSTEM_SHORT_NAME)
			.setCopyright("Updated copyright")
			.build(RestExtensions.USER, String.format("Updated copyright %s", DEFAULT_CODE_SYSTEM_SHORT_NAME))
			.execute(bus)
			.getSync();
		
		// assert that createdAt and updatedAt values are the same after create
		CodeSystem updatedCodeSystem = assertResourceGet(DEFAULT_CODE_SYSTEM_SHORT_NAME)
			.statusCode(200)
			.extract()
			.as(CodeSystem.class);
		// createdAt stays as is, but updatedAt got updated to a time after the marked value
		assertEquals(createdCodeSystem.getCreatedAt(), updatedCodeSystem.getCreatedAt());
		assertEquals((Long) updateResult.getCommitTimestamp(), updatedCodeSystem.getUpdatedAt());
	}
	
	@Test
	public void getWithTimestamp() throws Exception {
		createCodeSystemWithOid(DEFAULT_CODE_SYSTEM_SHORT_NAME, DEFAULT_CODE_SYSTEM_OID);
		
		CodeSystem createdCodeSystem = assertResourceGet(DEFAULT_CODE_SYSTEM_SHORT_NAME)
			.statusCode(200)
			.extract()
			.as(CodeSystem.class);
		
		CommitResult updateResult = CodeSystemRequests.prepareUpdateCodeSystem(DEFAULT_CODE_SYSTEM_SHORT_NAME)
			.setCopyright("Updated copyright")
			.build(RestExtensions.USER, String.format("Updated copyright %s", DEFAULT_CODE_SYSTEM_SHORT_NAME))
			.execute(bus)
			.getSync();
		
		// try to retrieve code system state before it was created
		assertResourceGet(DEFAULT_CODE_SYSTEM_SHORT_NAME, createdCodeSystem.getCreatedAt() - 1L)
			.statusCode(404);
		
		// retrieve the state at creation time
		CodeSystem codeSystem1 = assertResourceGet(DEFAULT_CODE_SYSTEM_SHORT_NAME, createdCodeSystem.getCreatedAt())
			.statusCode(200)
			.extract()
			.as(CodeSystem.class);
		
		assertEquals(createdCodeSystem.getCopyright(), codeSystem1.getCopyright());
		
		// retrieve the state at the point in time when the update happened
		CodeSystem codeSystem2 = assertResourceGet(DEFAULT_CODE_SYSTEM_SHORT_NAME, updateResult.getCommitTimestamp())
			.statusCode(200)
			.extract()
			.as(CodeSystem.class);
		
		assertEquals("Updated copyright", codeSystem2.getCopyright());
		
		// look into the future a small amount as well
		CodeSystem codeSystem3 = assertResourceGet(DEFAULT_CODE_SYSTEM_SHORT_NAME, updateResult.getCommitTimestamp() + 1L)
				.statusCode(200)
				.extract()
				.as(CodeSystem.class);
		
		assertEquals("Updated copyright", codeSystem3.getCopyright());
	}
	
	@Test
	public void searchWithTimestamp() throws Exception {
		final long timestamp1 = createCodeSystemWithStatus("cs1", "draft").getCommitTimestamp();
		final long timestamp2 = createCodeSystemWithStatus("cs2", "draft").getCommitTimestamp();
		final long timestamp3 = createCodeSystemWithStatus("cs3", "draft").getCommitTimestamp();
		
		assertResourceSearch(Map.of("timestamp", timestamp1 - 1L)).statusCode(200).body("items", empty());
		assertResourceSearch(Map.of("timestamp", timestamp1)).statusCode(200).body("items.id", containsInAnyOrder("cs1"));
		assertResourceSearch(Map.of("timestamp", timestamp2 - 1L)).statusCode(200).body("items.id", containsInAnyOrder("cs1"));
		assertResourceSearch(Map.of("timestamp", timestamp2)).statusCode(200).body("items.id", containsInAnyOrder("cs1", "cs2"));
		assertResourceSearch(Map.of("timestamp", timestamp3 - 1L)).statusCode(200).body("items.id", containsInAnyOrder("cs1", "cs2"));
		assertResourceSearch(Map.of("timestamp", timestamp3)).statusCode(200).body("items.id", containsInAnyOrder("cs1", "cs2", "cs3"));
		assertResourceSearch(Map.of("timestamp", timestamp3 + 1L)).statusCode(200).body("items.id", containsInAnyOrder("cs1", "cs2", "cs3"));
	}
	
	@Test
	public void searchByOwner() throws Exception {
		final String id1 = createCodeSystem("cs1", "draft", "owner1").getResultAs(String.class);
		final String id2 = createCodeSystem("cs2", "draft", "owner2").getResultAs(String.class);
		
		assertResourceSearch(Map.of("owner", "ownerx")).statusCode(200).body("items", empty());
		assertResourceSearch(Map.of("owner", "owner1")).statusCode(200).body("items.id", containsInAnyOrder(id1));
	}
	
	@Test(expected = ConflictException.class)
	public void tryToCreateRootBundle() throws Exception {
		createCodeSystemWithStatus(IComponent.ROOT_ID, "draft");
	}
	
}
