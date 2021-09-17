/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.core.rest.ResourceApiAssert.assertResourceGet;
import static com.b2international.snowowl.core.rest.ResourceApiAssert.assertResourceSearch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.rest.BundleApiAssert;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 8.0
 */
public class ResourceApiTest {

	private static final String DEFAULT_CODE_SYSTEM_TOOLING_ID = SnomedTerminologyComponentConstants.TOOLING_ID;
	private static final String DEFAULT_CODE_SYSTEM_LANGUAGE = "en";
	private static final String DEFAULT_CODE_SYSTEM_DESCRIPTION = "# Description";
	private static final String DEFAULT_CODE_SYSTEM_SHORT_NAME = "sn";
	private static final String DEFAULT_CODE_SYSTEM_OID = "oid";

	private static IEventBus bus;

	@BeforeClass
	public static void setup() {
		bus = Services.bus();
	}

	@Test
	public void noResourcesPresent() {
		assertResourceSearch().statusCode(200).body("items", iterableWithSize(0));
	}

	@Test
	public void searchAllResources() {
		createDefaultCodeSystem(DEFAULT_CODE_SYSTEM_SHORT_NAME, DEFAULT_CODE_SYSTEM_OID);

		assertResourceSearch().statusCode(200).body("items", iterableWithSize(1));
	}

	@Test
	public void getById() {
		createDefaultCodeSystem(DEFAULT_CODE_SYSTEM_SHORT_NAME, DEFAULT_CODE_SYSTEM_OID);

		assertResourceGet(DEFAULT_CODE_SYSTEM_SHORT_NAME)
			.statusCode(200)
			.body("id", equalTo(DEFAULT_CODE_SYSTEM_SHORT_NAME))
			.body("oid", equalTo(DEFAULT_CODE_SYSTEM_OID));
	}
	
	@Test
	public void filterByOid() {
		final String id1 = IDs.base64UUID();
		final String oid1 = "https://b2i.sg/" + id1;
		final String id2 = IDs.base64UUID();
		final String oid2 = "https://b2i.sg/" + id2;
		createDefaultCodeSystem(id1, oid1);
		createDefaultCodeSystem(id2, oid2);

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
		final String id1 = IDs.base64UUID();
		final String id2 = IDs.base64UUID();
		createCodeSystemWithStatus(id1, "draft");
		createCodeSystemWithStatus(id2, "active");
		
		assertResourceSearch(ImmutableMap.of("status", ImmutableList.of("draft")))
			.statusCode(200)
			.body("total", equalTo(1))
			.body("items[0].id", equalTo(id1))
			.body("items[0].status", equalTo("draft"));
	}
	
	@Test(expected = BadRequestException.class)
	public void searchAfter() throws Exception {
		final String id1 = IDs.base64UUID();
		final String oid1 = "https://b2i.sg/" + id1;
		createDefaultCodeSystem(id1, oid1);

		CodeSystemRequests.prepareSearchCodeSystem()
		.filterById(id1)
		.setSearchAfter("codesystem")
		.buildAsync()
		.execute(bus)
		.getSync();
	}

	private void createCodeSystemWithStatus(final String shortName, final String status) {
		CodeSystemRequests
		.prepareNewCodeSystem()
		.setId(shortName)
		.setTitle(shortName)
		.setUrl(SnomedTerminologyComponentConstants.SNOMED_URI_DEV + "/" + shortName)
		.setDescription(DEFAULT_CODE_SYSTEM_DESCRIPTION)
		.setLanguage(DEFAULT_CODE_SYSTEM_LANGUAGE)
		.setToolingId(DEFAULT_CODE_SYSTEM_TOOLING_ID)
		.setStatus(status)
		.setOid("https://b2i.sg/" + shortName)
		.build(RestExtensions.USER, String.format("New code system %s", shortName))
		.execute(bus).getSync();
		
	}
	
	private void createDefaultCodeSystem(final String shortName, final String oid) {
		CodeSystemRequests
		.prepareNewCodeSystem()
		.setId(shortName)
		.setTitle(String.format("%s - %s", shortName, oid))
		.setUrl(SnomedTerminologyComponentConstants.SNOMED_URI_DEV + "/" + shortName)
		.setDescription(DEFAULT_CODE_SYSTEM_DESCRIPTION)
		.setLanguage(DEFAULT_CODE_SYSTEM_LANGUAGE)
		.setToolingId(DEFAULT_CODE_SYSTEM_TOOLING_ID)
		.setOid(oid).build(RestExtensions.USER, String.format("New code system %s", shortName))
		.execute(bus).getSync();
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
	public void sortByResourceTypeAsc() {
		final String id1 = "A";
		final String id2 = "B";
		final String id3 = "C";
		final String id4 = "D";
		
		
		createCodeSystemWithStatus(id1, "active");
		createCodeSystemWithStatus(id2, "active");
		
		BundleApiAssert.createBundle(BundleApiAssert.prepareBundleCreateRequestBody(id3));
		BundleApiAssert.createBundle(BundleApiAssert.prepareBundleCreateRequestBody(id4));
		
		assertResourceSearch(ImmutableMap.of("sort", ImmutableList.of("typeRank:asc", "title:asc")))
			.statusCode(200)
			.body("items.id", Matchers.contains(id3, id4, id1, id2));
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
		
		assertResourceSearch(ImmutableMap.of("sort", ImmutableList.of("typeRank:desc", "title:asc")))
		.statusCode(200)
		.body("items.id", Matchers.contains(id1, id2, id3, id4));
	}

}
