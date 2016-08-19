/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.junit.Assert.fail;

import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.Test;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 * use config rebase-issue.yml
 * @since 4.6
 */
public class SnomedRebaseVsClassificationIssueTest {

	// rebase config
	private static final String BRANCH = "MAIN/DRGPHMAINT/DRGPHMAINT-3";
	
	// classify config
	private static final String CLASSIFY_BRANCH = "MAIN";
	private static final int NUMBER_OF_CLASSIFICATIONS = 5;

	@Test
	public void executeRebaseAndClassificationOperationParallel() throws Exception {
		// execute branch rebase
		Promise.wrap(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				SnomedBranchingApiAssert.assertBranchCanBeRebased(BranchPathUtils.createPath(BRANCH), "Rebased " + BRANCH);
				return null;
			}
		}).fail(new Function<Throwable, Object>() {
			@Override
			public Object apply(Throwable input) {
				fail(input.getMessage());
				return null;
			}
		});
		
		// execute N classify operation on the same branch
		final Map<String, Object> classifyReq = ImmutableMap.<String, Object>of("reasonerId", SnomedCoreConfiguration.ELK_REASONER_ID); 
		for (int i = 0; i < NUMBER_OF_CLASSIFICATIONS; i++) {
			RestExtensions.postJson(SnomedApiTestConstants.SCT_API, classifyReq, CLASSIFY_BRANCH, "classifications").then().statusCode(201);
		}

		// then wait until all the processes finish
		// TODO refactor wait time, when we have async rebase operations, so we can use Promise.all then and fail combined with a countdownlatch
		System.in.read();
		
	}
	
}
