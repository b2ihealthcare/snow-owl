/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.test.commons.Services;

/**
 * @since 8.0
 */
public final class BundleApiTest extends BaseBundleApiTest {
	
	@Test
	public void testCreateBundle() {
		final String bundleId = "123";
		createBundle(id, bundleId);
		
		final Bundle bundle = getBundle();
		
		assertThat(bundle.getId()).isEqualTo(id);
		assertThat(bundle.getUrl()).isEqualTo(URL);
		assertThat(bundle.getTitle()).isEqualTo(TITLE);
		assertThat(bundle.getLanguage()).isEqualTo(LANGUAGE);
		assertThat(bundle.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(bundle.getStatus()).isEqualTo(STATUS);
		assertThat(bundle.getCopyright()).isEqualTo(COPYRIGHT);
		assertThat(bundle.getOwner()).isEqualTo(OWNER);
		assertThat(bundle.getUsage()).isEqualTo(USAGE);
		assertThat(bundle.getPurpose()).isEqualTo(PURPOSE);
		assertThat(bundle.getBundleId()).isEqualTo(bundleId);
	}
	
	@Test
	public void testCreateWithoutBundleId() {
		BundleRequests.prepareNewBundle()
				.setId(id)
				.setUrl(URL)
				.setTitle(TITLE)
				.build(USER, String.format("Create bundle: %s", id))
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);
		
		final Bundle bundle = getBundle();
		assertThat(bundle.getId()).isEqualTo(id);
		assertThat(bundle.getBundleId()).isEqualTo(ROOT);
		
	}

	@Test
	public void testCreateWithoutId() {
		final String id = BundleRequests.prepareNewBundle()
				.setUrl(URL)
				.setTitle(TITLE)
				.build(USER, String.format("Create bundle"))
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES)
				.getResultAs(String.class);
		
		final Bundle bundle = getBundle(id);
		assertThat(bundle.getId()).isEqualTo(id);
	}

	@Test(expected = BadRequestException.class)
	public void testCreateWithoutNullId() {
		BundleRequests.prepareNewBundle()
			.setId(null)
			.setUrl(URL)
			.setTitle(TITLE)
			.build(USER, String.format("Create bundle"))
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES)
			.getResultAs(String.class);
	}
}
