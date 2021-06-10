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

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.request.TermFilter;
import com.b2international.snowowl.test.commons.Services;

/**
 * @since 8.0
 */
public final class BundleApiTest extends BaseBundleApiTest {
	
	@Test
	public void createWithoutId() {
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
	public void createWithoutNullId() {
		BundleRequests.prepareNewBundle()
			.setId(null)
			.setUrl(URL)
			.setTitle(TITLE)
			.build(USER, String.format("Create bundle"));
	}
	
	@Test(expected = BadRequestException.class)
	public void createWithoutTitle() {
		BundleRequests.prepareNewBundle()
			.setId(id)
			.setUrl(URL)
			.build(USER, String.format("Create bundle"));
	}
	
	@Test(expected = BadRequestException.class)
	public void createWithoutUrl() {
		BundleRequests.prepareNewBundle()
			.setId(id)
			.setTitle(TITLE)
			.build(USER, String.format("Create bundle"));
	}
	
	@Test
	public void createNewBundle() {
		final String bundleId = "123";
		createBundle(id, bundleId, TITLE);
		
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
	public void createWithoutBundleId() {
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

	@Test(expected = NotFoundException.class)
	public void getNotExistingBundle() {
		getBundle("not_existing_id");
	}

	@Test
	public void searchById() {
		final String id2 = "id_2";
		
		createBundle();
		createBundle(id2);
		
		final List<String> bundleIds = build(BundleRequests.prepareSearchBundle().filterById(id))
				.stream()
				.map(Bundle::getId)
				.collect(Collectors.toList());
		
		assertThat(bundleIds).hasSameElementsAs(List.of(id));
	}

	@Test
	public void searchByIds() {
		final String id1 = "id_1";
		final String id2 = "id_2";
		final String id3 = "id_3";
		
		createBundle(id1);
		createBundle(id2);
		createBundle(id3);
		
		final List<String> bundleIds = build(BundleRequests.prepareSearchBundle().filterByIds(Set.of(id1, id2)))
				.stream()
				.map(Bundle::getId)
				.collect(Collectors.toList());
		
		assertThat(bundleIds).hasSameElementsAs(List.of(id1, id2));
	}
	
	@Test
	public void searchByExactTitle() {
		final String title = "Exact bundle title";
		
		final String bundleId1 = createBundle("exactId1", ROOT, title);
		final String bundleId2 = createBundle("exactId1", ROOT, title.toUpperCase());
		
		final List<String> bundleNames = build(
				BundleRequests.prepareSearchBundle().filterByExactTerm(title)).stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(bundleNames).contains(bundleId1);
		assertThat(bundleNames).doesNotContain(bundleId2);
	}

	@Test
	public void searchByExactCaseInsensitiveTitle() {
		final String title = "Exact Case Insensitive Bundle Title";
		
		final String bundleId1 = createBundle("exactId1", ROOT, title.toUpperCase());
		final String bundleId2 = createBundle("exactId1", ROOT, title.toLowerCase());
		
		final List<String> bundleNames = build(
				BundleRequests.prepareSearchBundle().filterByExactTermIgnoreCase(title)).stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(bundleNames).hasSameElementsAs(List.of(bundleId1, bundleId2));
	}
	
	@Test
	public void searchByParsedTitle() {
		final String title1 = "BundleTitle";
		final String title2 = "BundleTerm";
		
		final String bundleId1 = createBundle("exactId1", ROOT, title1);
		final String bundleId2 = createBundle("exactId1", ROOT, title2);
		
		final List<String> bundleNames = build(
				BundleRequests.prepareSearchBundle().filterByTerm(TermFilter.parsedTermMatch("undlet"))).stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(bundleNames).hasSameElementsAs(List.of(bundleId1, bundleId2));
	}
}
