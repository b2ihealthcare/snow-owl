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
package com.b2international.snowowl.core.rest.resource;

import static com.b2international.snowowl.core.rest.ResourceApiAssert.assertResourceGet;
import static com.b2international.snowowl.core.rest.ResourceApiAssert.assertResourceSearch;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 8.0
 */
public class ResourceApiTest {

	private static final String DEFAULT_CODE_SYSTEM_TOOLING_ID = "snomed";
	private static final String DEFAULT_CODE_SYSTEM_LANGUAGE = "en";
	private static final String DEFAULT_CODE_SYSTEM_DESCRIPTION = "# Description";
	private static final String DEFAULT_CODE_SYSTEM_URL = "www.ihtsdo.org";
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

	private void createDefaultCodeSystem(final String shortName, final String oid) {
		CodeSystemRequests
		.prepareNewCodeSystem()
		.setId(shortName)
		.setTitle(String.format("%s - %s", shortName, oid))
		.setUrl(DEFAULT_CODE_SYSTEM_URL)
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

}
