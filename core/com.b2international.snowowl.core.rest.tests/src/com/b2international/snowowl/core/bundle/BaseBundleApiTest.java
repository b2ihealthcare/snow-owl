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
package com.b2international.snowowl.core.bundle;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;

import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.TestMethodNameRule;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 8.0
 */
abstract class BaseBundleApiTest {
	
	static final String USER = RestExtensions.USER;
	static final String ROOT = "-1";
	static final String URL = "https://b2i.sg";
	static final String TITLE = "Bundle title";
	static final String LANGUAGE = "en";
	static final String DESCRIPTION = "Bundle to group resources";
	static final String STATUS = "draft";
	static final String COPYRIGHT = "copyright";
	static final String OWNER = "b2i";
	static final String CONTACT = "info@b2international.com";
	static final String USAGE = "Bundle testing sources";
	static final String PURPOSE = "Testing purpose";
	
	@Rule 
	public final TestMethodNameRule testName = new TestMethodNameRule();
	
	String id;
	
	@Before
	public void setup() {
		this.id = testName.get();
	}

	Bundle getBundle() {
		return getBundle(id);
	}
	
	Bundle getBundle(final String id) {
		return BundleRequests.prepareGetBundle(id)
				.buildAsync()
				.getRequest()
				.execute(Services.context());
	}
	
	String createBundle(final String id, final String bundleId) {
		return BundleRequests.prepareNewBundle()
				.setId(id)
				.setUrl(URL)
				.setTitle(TITLE)
				.setLanguage(LANGUAGE)
				.setDescription(DESCRIPTION)
				.setStatus(STATUS)
				.setCopyright(COPYRIGHT)
				.setOwner(OWNER)
				.setContact(CONTACT)
				.setUsage(USAGE)
				.setPurpose(PURPOSE)
				.bundleId(bundleId)
				.build(USER, String.format("Create bundle: %s", id))
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES)
				.getResultAs(String.class);
	}
}
