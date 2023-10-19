/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.b2international.snowowl.core.Dependency;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 9.0.0
 */
public class ResourceSearchRequestTest {

	private static final String CODE_SYSTEM_ID = "SNOMEDCT-COMMITS";
	
	@Test
	public void expandCommits() throws Exception {
		CodeSystemRequests.prepareNewCodeSystem()
			.setId(CODE_SYSTEM_ID)
			.setTitle(CODE_SYSTEM_ID)
			.setUrl(SnomedTerminologyComponentConstants.SNOMED_URI_DEV + "/" + CODE_SYSTEM_ID)
			.setToolingId(SnomedTerminologyComponentConstants.TOOLING_ID)
			.setDependencies(Dependency.of(SnomedContentRule.SNOMEDCT.withPath("2020-01-31"), TerminologyResource.DependencyScope.EXTENSION_OF))
			.build(RestExtensions.USER, String.format("New code system %s", CODE_SYSTEM_ID))
			.execute(Services.bus())
			.getSync();

		final long firstCommit = SnomedRequests.prepareUpdateConcept(Concepts.ROOT_CONCEPT)
			.setModuleId(Concepts.MODULE_SCT_MODEL_COMPONENT)
			.build(CODE_SYSTEM_ID, RestExtensions.USER, "Updated module of root concept")
			.execute(Services.bus())
			.getSync()
			.getCommitTimestamp();
		
		final long secondCommit = SnomedRequests.prepareUpdateConcept(Concepts.ROOT_CONCEPT)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.build(CODE_SYSTEM_ID, RestExtensions.USER, "Restored module of root concept")
			.execute(Services.bus())
			.getSync()
			.getCommitTimestamp();
		
		final Resource resource = ResourceRequests.prepareGet(CODE_SYSTEM_ID)
			.setExpand(String.format("commits(%s:%d, %s:%d)", 
				TerminologyResource.Expand.TIMESTAMP_FROM_OPTION_KEY, firstCommit,
				TerminologyResource.Expand.TIMESTAMP_TO_OPTION_KEY, secondCommit - 1L
			))
			.buildAsync()
			.execute(Services.bus())
			.getSync();
		
		assertThat(resource).isInstanceOf(TerminologyResource.class);

		final TerminologyResource terminologyResource = (TerminologyResource) resource;
		final CommitInfos commits = terminologyResource.getCommits();
		
		assertThat(commits.getItems())
			.hasSize(1)
			.element(0)
			.extracting(CommitInfo::getTimestamp)
			.isEqualTo(firstCommit);
	}
}
