/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.perf;

import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.createBranch;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.merge;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.AllSnomedApiTests;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
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
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
		} catch (NotFoundException e) {
			// ignore
		}
	}
	
	@Test
	public void testPerf() throws Exception {
		IBranchPath branch = BranchPathUtils.createPath(branchPath, "merge-test");
		createBranch(branch).statusCode(201);
		BulkRequestBuilder<TransactionContext> bulk = BulkRequest.create();
		for (int i = 0; i < 1000; i++) {
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
								.setCharacteristicType(CharacteristicType.STATED_RELATIONSHIP)
								.setTypeId(Concepts.IS_A)
								.setDestinationId(Concepts.ROOT_CONCEPT)));
		}
		
		Stopwatch w = Stopwatch.createStarted();
		// commit large changeset
		SnomedRequests.prepareCommit()
			.setBody(bulk)
			.setCommitComment("Commit large bulk request")
			.setUserId(User.SYSTEM.getUsername())
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch.getPath())
			.execute(getBus())
			.getSync();
		System.err.println("Bulk commit took: " + w);
		w.reset().start();
		
		merge(branch, branchPath, "Promote changes from task...")
			.body("status", CoreMatchers.equalTo(Merge.Status.COMPLETED.name()));
		System.err.println("Merge took: " + w);
	}
	
}
