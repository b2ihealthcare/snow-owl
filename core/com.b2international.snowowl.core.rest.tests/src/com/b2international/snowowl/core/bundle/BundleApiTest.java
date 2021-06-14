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
import com.b2international.snowowl.core.Resource;
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
		final String bundleId2 = createBundle("exactId2", ROOT, title.toUpperCase());
		
		final List<String> bundleIds = build(BundleRequests.prepareSearchBundle().filterByExactTerm(title))
				.stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(bundleIds).contains(bundleId1);
		assertThat(bundleIds).doesNotContain(bundleId2);
	}

	@Test
	public void searchByExactCaseInsensitiveTitle() {
		final String title = "Exact Case Insensitive Bundle Title";
		
		final String bundleId1 = createBundle("exactId1", ROOT, title.toUpperCase());
		final String bundleId2 = createBundle("exactId2", ROOT, title.toLowerCase());
		
		final List<String> bundleIds = build(BundleRequests.prepareSearchBundle().filterByExactTermIgnoreCase(title))
				.stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(bundleIds).containsOnlyOnce(bundleId1, bundleId2);
	}
	
	@Test
	public void searchByWildCardTitle() {
		final String title1 = "Bundle title";
		final String title2 = "Bundle term";
		
		final String bundleId1 = createBundle("exactId1", ROOT, title1);
		final String bundleId2 = createBundle("exactId2", ROOT, title2);
		
		final List<String> bundleIds = build(BundleRequests.prepareSearchBundle().filterByTerm(TermFilter.parsedTermMatch("Bundle*")))
				.stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(bundleIds).containsOnlyOnce(bundleId1, bundleId2);
	}

	@Test
	public void searchTitleFuzzy() {
		final String title = "Bundle title";
		
		final String bundleId = createBundle(id, ROOT, title);
		
		final List<String> bundleIdsDistance2 = build(BundleRequests.prepareSearchBundle().filterByTerm(TermFilter.fuzzyMatch("uncle title")))
				.stream().map(Bundle::getId).collect(Collectors.toList());
		
		// Only 1 Levenshtein distance is allowed
		assertThat(bundleIdsDistance2).isEmpty();

		final List<String> bundleIdsDistance1 = build(BundleRequests.prepareSearchBundle().filterByTerm(TermFilter.fuzzyMatch("Buncle title")))
				.stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(bundleIdsDistance1).containsOnly(bundleId);
	}

	@Test
	public void searchByTitle() {
		final String title = "Text searching algorithms";
		final String title2 = "Search algorithms";
		final String title3 = "Term matching algorithms";

		final String id1 = createBundle("title_id_1", ROOT, title);
		final String id2 = createBundle("title_id_2", ROOT, title2);
		final String id3 = createBundle("title_id_3", ROOT, title3);
		
		// Match all word stop words not ignored
		final List<String> textSearch = build(BundleRequests.prepareSearchBundle().filterByTerm("search algorithm"))
				.stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(textSearch).containsOnlyOnce(id1, id2);

		// Match all word stop words ignored
		final List<String> textSearchStopWrodsIgnored = build(BundleRequests.prepareSearchBundle().filterByTerm(TermFilter.defaultTermMatch("the search algorithm of").withIgnoreStopwords()))
				.stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(textSearchStopWrodsIgnored).containsOnlyOnce(id1, id2);

		// Match prefixes
		final List<String> bundlePrefix = build(BundleRequests.prepareSearchBundle().filterByTerm("te algo"))
				.stream().map(Bundle::getId).collect(Collectors.toList());

		assertThat(bundlePrefix).containsOnlyOnce(id1, id3);

		// Match boolean prefixes
		final List<String> bundleBooleanPrefix = build(BundleRequests.prepareSearchBundle().filterByTerm("text search alg"))
				.stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(bundleBooleanPrefix).containsOnlyOnce(id1);
		
		// Match exact case insensitive
		final List<String> bundleExactCaseInsensitive = build(
				BundleRequests.prepareSearchBundle().filterByTerm(title2.toUpperCase())).stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(bundleExactCaseInsensitive).containsOnlyOnce(id2);
	}
	
	@Test
	public void searchByMinShouldMatch() {
		final String title = "Clinical finding";
		final String title2 = "Clinical stage finding";
		final String title3 = "General clinical state finding";

		final String id1 = createBundle("title_id_1", ROOT, title);
		final String id2 = createBundle("title_id_2", ROOT, title2);
		final String id3 = createBundle("title_id_3", ROOT, title3);
		
		// 3 word of "General clinical state finding" must present
		final List<String> threeTermMatch = build(BundleRequests.prepareSearchBundle().filterByTerm(TermFilter.minTermMatch("General clinical state finding", 3)))
				.stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(threeTermMatch).containsOnlyOnce(id3);

		// 2 word of "General clinical state finding" must present
		final List<String> twoTermMatch = build(BundleRequests.prepareSearchBundle().filterByTerm(TermFilter.minTermMatch("General clinical state finding", 2)))
				.stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(twoTermMatch).containsOnlyOnce(id1, id2, id3);
		
		// 3 word prefix of "en cli sta fin" must present
		final List<String> threePrefixTermMatch = build(BundleRequests.prepareSearchBundle().filterByTerm(TermFilter.minTermMatch("en cli sta fin", 3)))
				.stream().map(Bundle::getId).collect(Collectors.toList());
		
		assertThat(threePrefixTermMatch).containsOnlyOnce(id3, id2);
	}
	
	@Test
	public void expandResources() {
		final String rootBundleId = createBundle();
		
		final String cs1Id = createCodeSystem(rootBundleId, "cs1");
		final String cs2Id = createCodeSystem(rootBundleId, "cs2");

		final String subBundleId = createBundle("bundle1", rootBundleId, "subBundle");
		
		final String cs3Id = createCodeSystem(subBundleId, "cs3");
		
		final Bundles bundles = build(BundleRequests.prepareSearchBundle().filterById(rootBundleId).setExpand("resources()"));

		assertThat(bundles.getItems()).hasSize(1);
		
		final Bundle bundle = bundles.getItems().get(0);
		
		assertThat(bundle.getResources()).isNotNull();
		
		final List<String> resourceIds = bundle.getResources().getItems().stream().map(Resource::getId).collect(Collectors.toList());
		
		assertThat(resourceIds).containsOnlyOnce(cs1Id, cs2Id, subBundleId);
		assertThat(resourceIds).doesNotContain(cs3Id);
	}
	
	@Test
	public void updateBundle() {
		createBundle();

		final String newUrl = "https://updated.hu";
		final String newTitle = "Új név";
		final String newLanguage = "hu";
		final String newDescription = "Erőforrások rendszerezésére";
		final String newStatus = "inactive";
		final String newCopyright = "Szerzői jog";
		final String newOwner = "tulajdonos";
		final String newContract = "info@b2international.hu";
		final String newUsage = "Magyar kódrendszerek csoportosítására";
		final String newPurpose = "Tesztelési célra";
		final String newBundleId = "123";
		
		BundleRequests.prepareUpdateBundle(id)
		 	.setUrl(newUrl)
			.setTitle(newTitle)
			.setLanguage(newLanguage)
			.setDescription(newDescription)
			.setStatus(newStatus)
			.setCopyright(newCopyright)
			.setOwner(newOwner)
			.setContact(newContract)
			.setUsage(newUsage)
			.setPurpose(newPurpose)
			.setBundleId(newBundleId)
			.build(USER, String.format("Update bundle: %s", id))
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES);

		final Bundle bundle = getBundle();
		
		assertThat(bundle.getId()).isEqualTo(id);
		assertThat(bundle.getUrl()).isEqualTo(newUrl);
		assertThat(bundle.getTitle()).isEqualTo(newTitle);
		assertThat(bundle.getLanguage()).isEqualTo(newLanguage);
		assertThat(bundle.getDescription()).isEqualTo(newDescription);
		assertThat(bundle.getStatus()).isEqualTo(newStatus);
		assertThat(bundle.getCopyright()).isEqualTo(newCopyright);
		assertThat(bundle.getOwner()).isEqualTo(newOwner);
		assertThat(bundle.getUsage()).isEqualTo(newUsage);
		assertThat(bundle.getPurpose()).isEqualTo(newPurpose);
		assertThat(bundle.getBundleId()).isEqualTo(newBundleId);
	}

	@Test
	public void deleteBundle() {
		final String rootBundleId = createBundle();
		
		final String cs1Id = createCodeSystem(rootBundleId, "cs1");
		final String cs2Id = createCodeSystem(rootBundleId, "cs2");

		final String subBundleId = createBundle("bundle1", rootBundleId, "subBundle");
		
		final String cs3Id = createCodeSystem(subBundleId, "cs3");
		
		final Boolean isSuccess = BundleRequests.prepareDeleteBundle(subBundleId)
				.build(USER, String.format("Delete bundle: %s", subBundleId))
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES)
				.getResultAs(Boolean.class);
		
		assertThat(isSuccess);
		
		final Bundles bundles = build(BundleRequests.prepareSearchBundle().filterById(rootBundleId).setExpand("resources()"));

		assertThat(bundles.getItems()).hasSize(1);
		
		final Bundle bundle = bundles.getItems().get(0);
		
		assertThat(bundle.getResources()).isNotNull();
		
		final List<String> resourceIds = bundle.getResources().getItems().stream().map(Resource::getId).collect(Collectors.toList());
		
		assertThat(resourceIds).containsOnlyOnce(cs1Id, cs2Id, cs3Id);
	}
}
