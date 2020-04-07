/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.perf;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.events.bulk.BulkResponse;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.CommitResult;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.AllSnomedApiTests;
import com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.base.Stopwatch;

/**
 * Bulk Concept create and Merge performance test case.
 * The test creates 1000 concepts with 1 FSN, 1 PT (2000 descriptions), both preferred in the UK language (2000 lang. members) and 1 stated ISA to the ROOT (1000 relationships).
 * This means a total of 6000 components being created in approximately 5-10 seconds.
 * The merge of this amount of components should take no more than 10 seconds.
 * The number heavily depends on whether you run this test case on a warmed ES cache/index, what are the current JVM settings and what hardware you have.
 * 
 * @since 6.5
 */
public class SnomedMergePerformanceTest extends AbstractSnomedApiTest {

	/**
	 * XXX can be used to run this single test over and over on the same dataset reusing the same branch.
	 * Just uncomment the annotation, disable clearResources and dataset import in {@link AllSnomedApiTests} and run this single test
	 */
//	@BeforeClass
	public static void deleteBranch() {
		try {
			RepositoryRequests.branching()
				.prepareDelete("MAIN/SnomedMergePerformanceTest/testPerf")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(Services.bus())
				.getSync();
		} catch (NotFoundException e) {
			// ignore
		}
	}
	
	@Test
	public void testPerf() throws Exception {
		IBranchPath branch = BranchPathUtils.createPath(branchPath, "merge-test");
		branching.createBranch(branch).statusCode(201);
		BulkRequestBuilder<TransactionContext> bulk = BulkRequest.create();
		final int numberOfConceptsToWorkWith = 10_000;
		for (int i = 0; i < numberOfConceptsToWorkWith; i++) {
			bulk.add(SnomedRequests.prepareNewConcept()
						.setIdFromNamespace(null /*INT*/)
						.setActive(true)
						.setModuleId(Concepts.MODULE_SCT_CORE)
						.addDescription(SnomedRequests.prepareNewDescription()
								.setIdFromNamespace(null /*INT*/)
								.setTerm("MergeTest FSN " + i)
								.setTypeId(Concepts.FULLY_SPECIFIED_NAME)
								.setLanguageCode("en")
								.preferredIn(Concepts.REFSET_LANGUAGE_TYPE_UK))
						.addDescription(SnomedRequests.prepareNewDescription()
								.setIdFromNamespace(null /*INT*/)
								.setTerm("MergeTest PT " + i)
								.setTypeId(Concepts.SYNONYM)
								.setLanguageCode("en")
								.preferredIn(Concepts.REFSET_LANGUAGE_TYPE_UK))
						.addRelationship(SnomedRequests.prepareNewRelationship()
								.setIdFromNamespace(null /*INT*/)
								.setCharacteristicTypeId(Concepts.STATED_RELATIONSHIP)
								.setTypeId(Concepts.IS_A)
								.setDestinationId(Concepts.ROOT_CONCEPT)));
		}
		
		Stopwatch w = Stopwatch.createStarted();
		// commit large changeset
		CommitResult createCommitResult = SnomedRequests.prepareCommit()
			.setBody(bulk)
			.setCommitComment("Commit large bulk request")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch.getPath())
			.execute(getBus())
			.getSync();
		System.err.println("Bulk create 10_000 concepts commit took: " + w);
		w.reset().start();

		// extract the new IDs assigned to the concepts
		Set<String> idsOfCreatedConcepts = createCommitResult.getResultAs(BulkResponse.class)
			.stream()
			.filter(String.class::isInstance)
			.map(String.class::cast)
			.collect(Collectors.toSet());
		// retrieve the first 100 created concepts back, check the module
		
		SnomedConcepts first100 = SnomedRequests.prepareSearchConcept()
			.setLimit(100)
			.filterByIds(idsOfCreatedConcepts)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch.getPath())
			.execute(getBus())
			.getSync();
		
		assertThat(first100.getTotal()).isEqualTo(numberOfConceptsToWorkWith);
		first100.forEach(concept -> {
			assertThat(concept.getModuleId()).isEqualTo(Concepts.MODULE_SCT_CORE);
		});
		
		// check that SNOMED CT ROOT has 10_000
		int totalDescendants = SnomedRequests.prepareSearchConcept()
			.setLimit(0)
			.filterByStatedParent(Concepts.ROOT_CONCEPT)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch.getPath())
			.execute(getBus())
			.getSync()
			.getTotal();
		assertThat(totalDescendants).isGreaterThanOrEqualTo(numberOfConceptsToWorkWith);
		
		// find the relationship IDs of the concepts
		final Set<String> conceptRelationshipIds = SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true)
				.filterBySource(idsOfCreatedConcepts)
				.filterByDestination(Concepts.ROOT_CONCEPT)
				.setFields(SnomedRelationshipIndexEntry.Fields.ID)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch.getPath())
				.execute(getBus())
				.getSync()
				.stream()
				.map(SnomedRelationship::getId)
				.collect(Collectors.toSet());
		
		System.err.println("Verify creation of 10_000 concepts took: " + w);
		w.reset().start();
		
		// prepare module bulk update
		
		bulk = BulkRequest.create();
		
		for (String conceptIdToUpdate : idsOfCreatedConcepts) {
			bulk.add(SnomedRequests.prepareUpdateConcept(conceptIdToUpdate)
					.setModuleId(Concepts.MODULE_SCT_MODEL_COMPONENT));
		}
		
		// commit module bulk update
		SnomedRequests.prepareCommit()
			.setBody(bulk)
			.setCommitComment("Commit update bulk request")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch.getPath())
			.execute(getBus())
			.getSync();
		System.err.println("Bulk update 10_000 concepts commit took: " + w);
		w.reset().start();
		
		// prepare move concepts in hierarchy to clinical finding update
		bulk = BulkRequest.create();

		// inactivate all current relationships
		for (String relationshipIdToInactive : conceptRelationshipIds) {
			bulk.add(SnomedRequests.prepareUpdateRelationship(relationshipIdToInactive).setActive(false));
		}
		
		// add new relationships to concepts
		for (String conceptIdToUpdate : idsOfCreatedConcepts) {
			bulk.add(SnomedRequests.prepareNewRelationship()
					.setIdFromNamespace(null /*INT*/)
					.setSourceId(conceptIdToUpdate)
					.setTypeId(Concepts.IS_A)
					.setDestinationId("404684003" /*CLINICAL FINDING*/)
					.setModuleId(Concepts.MODULE_SCT_MODEL_COMPONENT));
		}
		
		// commit move concepts in hierarchy to clinical finding update
		SnomedRequests.prepareCommit()
			.setBody(bulk)
			.setCommitComment("Commit update bulk request")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch.getPath())
			.execute(getBus())
			.getSync();
		System.err.println("Bulk move 10_000 concepts to Clinical Finding took: " + w);
		w.reset().start();
		
		SnomedRestFixtures.merge(branch, branchPath, "Promote changes from task...")
			.body("status", CoreMatchers.equalTo(Merge.Status.COMPLETED.name()));
		System.err.println("Merge took: " + w);
	}
	
}
