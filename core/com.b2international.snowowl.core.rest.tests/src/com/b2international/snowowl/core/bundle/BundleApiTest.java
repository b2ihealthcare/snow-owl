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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.elasticsearch.common.UUIDs;
import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.CycleDetectedException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.TermFilter;
import com.b2international.snowowl.test.commons.Services;

/**
 * @since 8.0
 */
public final class BundleApiTest extends BaseBundleApiTest {
	
	@Test
	public void createWithoutId() {
		final String id = ResourceRequests.bundles().prepareCreate()
				.setUrl(URL_PREFIX + "random")
				.setTitle(TITLE)
				.build(USER, "Create bundle")
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES)
				.getResultAs(String.class);

		final Bundle bundle = getBundle(id);
		assertThat(bundle.getId()).isEqualTo(id);
	}

	@Test(expected = BadRequestException.class)
	public void createWithoutNullId() {
		ResourceRequests.bundles().prepareCreate()
			.setId(null)
			.setTitle(TITLE)
			.build(USER, "Create bundle");
	}
	
	@Test(expected = BadRequestException.class)
	public void createWithoutTitle() {
		ResourceRequests.bundles().prepareCreate()
			.setId(id)
			.setUrl(URL_PREFIX + id)
			.build(USER, "Create bundle");
	}
	
	@Test(expected = BadRequestException.class)
	public void createWithoutUrl() {
		ResourceRequests.bundles().prepareCreate()
			.setId(id)
			.setTitle(TITLE)
			.build(USER, "Create bundle");
	}
	
	@Test(expected = BadRequestException.class)
	public void createBundleWithNonExistingParentBundle() throws Exception {
		createBundle(id, UUID.randomUUID().toString(), TITLE);
	}
	
	@Test
	public void createNewBundle() {
		final String parentBundleId = createBundle("123");
		createBundle(id, parentBundleId, TITLE);
		
		final Bundle bundle = getBundle();
		
		assertThat(bundle.getId()).isEqualTo(id);
		assertThat(bundle.getUrl()).isEqualTo(URL_PREFIX + id);
		assertThat(bundle.getTitle()).isEqualTo(TITLE);
		assertThat(bundle.getLanguage()).isEqualTo(LANGUAGE);
		assertThat(bundle.getDescription()).isEqualTo(DESCRIPTION);
		assertThat(bundle.getStatus()).isEqualTo(STATUS);
		assertThat(bundle.getCopyright()).isEqualTo(COPYRIGHT);
		assertThat(bundle.getOwner()).isEqualTo(OWNER);
		assertThat(bundle.getUsage()).isEqualTo(USAGE);
		assertThat(bundle.getPurpose()).isEqualTo(PURPOSE);
		assertThat(bundle.getBundleId()).isEqualTo(parentBundleId);
	}
	
	@Test
	public void createWithoutBundleId() {
		ResourceRequests.bundles().prepareCreate()
				.setId(id)
				.setUrl(URL_PREFIX + id)
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
		
		assertThat(executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterById(id)))
			.hasSameElementsAs(List.of(id));
	}

	@Test
	public void searchByIds() {
		final String id1 = "id_1";
		final String id2 = "id_2";
		final String id3 = "id_3";
		
		createBundle(id1);
		createBundle(id2);
		createBundle(id3);
		
		assertThat(executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByIds(Set.of(id1, id2))))
			.hasSameElementsAs(List.of(id1, id2));
	}
	
	@Test
	public void searchByExactTitle() {
		final String title = "Exact bundle title";
		
		final String bundleId1 = createBundle("exactId1", ROOT, title);
		final String bundleId2 = createBundle("exactId2", ROOT, title.toUpperCase());
		
		final List<String> bundleIds = executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByExactTitle(title)).collect(Collectors.toList());
		
		assertThat(bundleIds)
			.contains(bundleId1)
			.doesNotContain(bundleId2);
	}

	@Test
	public void searchByExactCaseInsensitiveTitle() {
		final String title = "Exact Case Insensitive Bundle Title";
		
		final String bundleId1 = createBundle("exactId1", ROOT, title.toUpperCase());
		final String bundleId2 = createBundle("exactId2", ROOT, title.toLowerCase());
		
		assertThat(executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByExactTitleIgnoreCase(title)))
			.contains(bundleId1, bundleId2);
	}
	
	@Test
	public void searchByWildCardTitle() {
		final String title1 = "Bundle title";
		final String title2 = "Bundle term";
		
		final String bundleId1 = createBundle("exactId1", ROOT, title1);
		final String bundleId2 = createBundle("exactId2", ROOT, title2);
		
		assertThat(executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByTitle(TermFilter.parsedTermMatch("Bundle*"))))
			.containsOnlyOnce(bundleId1, bundleId2);
	}

	@Test
	public void searchTitleFuzzy() {
		final String title = "Bundle title";
		
		final String bundleId = createBundle(id, ROOT, title);
		
		// Only 1 Levenshtein distance is allowed
		assertThat(executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByTitle(TermFilter.fuzzyMatch("uncle title"))))
			.isEmpty();

		assertThat( executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByTitle(TermFilter.fuzzyMatch("Buncle title"))))
			.containsOnly(bundleId);
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
		assertThat(executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByTitle("search algorithm")))
			.containsOnlyOnce(id1, id2);

		// Match all word stop words ignored
		assertThat(executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByTitle(TermFilter.defaultTermMatch("the search algorithm of").withIgnoreStopwords())))
			.containsOnlyOnce(id1, id2);

		// Match prefixes
		assertThat(executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByTitle("te algo")))
			.containsOnlyOnce(id1, id3);

		// Match boolean prefixes
		assertThat(executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByTitle("text search alg")))
			.containsOnlyOnce(id1);
		
		// Match exact case insensitive
		assertThat( executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByTitle(title2.toUpperCase())))
			.containsOnlyOnce(id2);
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
		assertThat(executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByTitle(TermFilter.minTermMatch("General clinical state finding", 3))))
			.containsOnlyOnce(id3);

		// 2 word of "General clinical state finding" must present
		assertThat(executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByTitle(TermFilter.minTermMatch("General clinical state finding", 2))))
			.containsOnlyOnce(id1, id2, id3);
		
		// 3 word prefix of "en cli sta fin" must present
		assertThat(executeThenExtractIds(ResourceRequests.bundles().prepareSearch().filterByTitle(TermFilter.minTermMatch("en cli sta fin", 3))))
			.containsOnlyOnce(id3, id2);
	}
	
	@Test
	public void searchByBundleId() {
		final String parentBundleId = createBundle("b1", IComponent.ROOT_ID, "Parent bundle");
		final String middleBundleId = createBundle("b2", parentBundleId, "Middle bundle");
		createBundle("b3", middleBundleId, "Child bundle");
		
		final Bundles childrenOfParent = ResourceRequests.bundles()
			.prepareSearch()
			.all()
			.filterByBundleId(parentBundleId)
			.buildAsync()
			.execute(Services.bus())
			.getSync(1L, TimeUnit.MINUTES);
		
		assertEquals(1, childrenOfParent.getTotal());
		assertEquals(middleBundleId, childrenOfParent.first().get().getId());
	}
	
	@Test
	public void searchByBundleAncestorId() {
		final String parentBundleId = createBundle("e1", IComponent.ROOT_ID, "Parent bundle");
		final String middleBundleId = createBundle("e2", parentBundleId, "Middle bundle");
		final String childBundleId = createBundle("e3", middleBundleId, "Child bundle");
		
		final Bundles descendantsOfParent = ResourceRequests.bundles()
			.prepareSearch()
			.all()
			.filterByBundleAncestorId(parentBundleId)
			.buildAsync()
			.execute(Services.bus())
			.getSync(10L, TimeUnit.MINUTES);
		
		assertEquals(2, descendantsOfParent.getTotal());
		assertThat(descendantsOfParent).extracting(Bundle::getId).containsOnly(middleBundleId, childBundleId);
	}
	
	@Test
	public void expandResources() {
		final String rootBundleId = createBundle();
		
		final String cs1Id = createCodeSystem(rootBundleId, UUIDs.randomBase64UUID());
		final String cs2Id = createCodeSystem(rootBundleId, UUIDs.randomBase64UUID());

		final String subBundleId = createBundle("bundle1", rootBundleId, "subBundle");
		
		final String cs3Id = createCodeSystem(subBundleId, UUIDs.randomBase64UUID());
		
		final Bundles bundles = execute(ResourceRequests.bundles().prepareSearch().filterById(rootBundleId).setExpand("resources()"));

		assertThat(bundles.getItems()).hasSize(1);
		
		final Bundle bundle = bundles.getItems().get(0);
		
		assertThat(bundle.getResources()).isNotNull();
		
		final List<String> resourceIds = bundle.getResources().getItems().stream().map(Resource::getId).collect(Collectors.toList());
		
		assertThat(resourceIds)
			.containsOnlyOnce(cs1Id, cs2Id, subBundleId)
			.doesNotContain(cs3Id);
	}
	
	@Test
	public void updateBundle() {
		createBundle();

		final String newUrl = "https://updated.com";
		final String newTitle = "New title";
		final String newLanguage = "us";
		final String newDescription = "New description for this bundle";
		final String newStatus = "inactive";
		final String newCopyright = "Updated license agreement";
		final String newOwner = "New owner";
		final String newContact = "newcontact@gmail.com";
		final String newUsage = "New usage";
		final String newPurpose = "New purpose";
		final String newBundleId = createBundle(IDs.base64UUID());
		
		ResourceRequests.bundles().prepareUpdate(id)
		 	.setUrl(newUrl)
			.setTitle(newTitle)
			.setLanguage(newLanguage)
			.setDescription(newDescription)
			.setStatus(newStatus)
			.setCopyright(newCopyright)
			.setOwner(newOwner)
			.setContact(newContact)
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
		
		final String cs1Id = createCodeSystem(rootBundleId, UUIDs.randomBase64UUID());
		final String cs2Id = createCodeSystem(rootBundleId, UUIDs.randomBase64UUID());

		final String subBundleId = createBundle("bundle1", rootBundleId, "subBundle");
		
		final String cs3Id = createCodeSystem(subBundleId, UUIDs.randomBase64UUID());
		
		final Boolean isSuccess = ResourceRequests.bundles().prepareDelete(subBundleId)
				.build(USER, String.format("Delete bundle: %s", subBundleId))
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES)
				.getResultAs(Boolean.class);
		
		assertThat(isSuccess);
		
		final Bundles bundles = execute(ResourceRequests.bundles().prepareSearch().filterById(rootBundleId).setExpand("resources()"));

		assertThat(bundles.getItems()).hasSize(1);
		
		final Bundle bundle = bundles.getItems().get(0);
		
		assertThat(bundle.getResources()).isNotNull();
		
		final Stream<String> resourceIds = bundle.getResources().getItems().stream().map(Resource::getId);
		
		assertThat(resourceIds).containsOnlyOnce(cs1Id, cs2Id, cs3Id);
	}
	
	@Test(expected = CycleDetectedException.class)
	public void updateBundleBundleId_DirectCycle() {
		final String parentBundleId = createBundle("p1", IComponent.ROOT_ID, "Parent bundle");
		final String childBundleId = createBundle("c1", parentBundleId, "Child bundle");
		
		/* 
		 * Make "parent" the child of "child" -- but "child" is already a child of "parent",
		 * so that is not possible. 
		 */
		ResourceRequests.bundles()
			.prepareUpdate(parentBundleId)
			.setBundleId(childBundleId)
			.build(USER, String.format("Update bundle: %s", parentBundleId))
			.execute(Services.bus())
			.getSync(1L, TimeUnit.MINUTES);
	}
	
	@Test(expected = CycleDetectedException.class)
	public void updateBundleBundleId_IndirectCycle() {
		final String parentBundleId = createBundle("p2", IComponent.ROOT_ID, "Parent bundle");
		final String middleBundleId = createBundle("m2", parentBundleId, "Middle bundle");
		final String childBundleId = createBundle("c2", middleBundleId, "Child bundle");
		
		/* 
		 * Make "parent" the child of "child" -- but "child" is already an indirect descendant of "parent",
		 * so that is not possible. 
		 */
		ResourceRequests.bundles()
			.prepareUpdate(parentBundleId)
			.setBundleId(childBundleId)
			.build(USER, String.format("Update bundle: %s", parentBundleId))
			.execute(Services.bus())
			.getSync(1L, TimeUnit.MINUTES);
	}
	
	@Test
	public void updateBundleBundleId_Hierarchy() {
		final String parentBundleId = createBundle("p3", IComponent.ROOT_ID, "Parent bundle");
		final String middleBundleId = createBundle("m3", parentBundleId, "Middle bundle");
		final String childBundleId = createBundle("c3", middleBundleId, "Child bundle");
	
		final String otherParentBundleId = createBundle("p4", IComponent.ROOT_ID, "Other parent bundle");
		
		ResourceRequests.bundles()
			.prepareUpdate(parentBundleId)
			.setBundleId(otherParentBundleId)
			.build(USER, String.format("Update bundle: %s", parentBundleId))
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES);
		
		final Resources descendants = ResourceRequests.prepareSearch()
			.all()
			.filterByBundleAncestorId(otherParentBundleId)
			.buildAsync()
			.execute(Services.bus())
			.getSync(100L, TimeUnit.MINUTES);
		
		assertEquals(3, descendants.getTotal());
		assertThat(descendants).extracting(Resource::getId).containsOnly(parentBundleId, middleBundleId, childBundleId);
	}
}
