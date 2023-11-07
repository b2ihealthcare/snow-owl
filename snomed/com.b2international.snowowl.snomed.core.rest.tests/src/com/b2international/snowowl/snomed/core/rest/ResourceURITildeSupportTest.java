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
package com.b2international.snowowl.snomed.core.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.elasticsearch.core.Map;
import org.junit.Test;

import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.rest.BranchBase;

/**
 * @since 9.0.0
 */
@BranchBase(isolateTests = false)
public class ResourceURITildeSupportTest extends AbstractSnomedApiTest {

	@Test
	public void accessVersionViaTilde() throws Exception {
		SnomedConcepts conceptsFromPath = searchConcepts(SnomedContentRule.SNOMEDCT.withPath("2002-01-31"), Map.of(), 0);
		SnomedConcepts conceptsFromTilde = searchConcepts(SnomedContentRule.SNOMEDCT.withSpecialResourceIdPart("2002-01-31"), Map.of(), 0);
		assertThat(conceptsFromPath.getTotal()).isEqualTo(1331);
		assertThat(conceptsFromTilde.getTotal()).isEqualTo(1331);
	}
	
	@Test
	public void accessBranchViaTilde() throws Exception {
		String childBranchName = "accessBranchViaTilde";
		branching.createBranch(BranchPathUtils.createMainPath().child(childBranchName)).statusCode(201);
		// search concepts using the same branch and 
		SnomedConcepts conceptsFromPath = searchConcepts(SnomedContentRule.SNOMEDCT.withPath(childBranchName), Map.of(), 0);
		SnomedConcepts conceptsFromTilde = searchConcepts(SnomedContentRule.SNOMEDCT.withSpecialResourceIdPart(childBranchName), Map.of(), 0);
		assertThat(conceptsFromPath.getTotal()).isEqualTo(1999);
		assertThat(conceptsFromTilde.getTotal()).isEqualTo(1999);
	}
	
	@Test
	public void commitShouldRegisterSubjectUsingTheTildeUri() throws Exception {
		String childBranchName = "commitShouldRegisterSubjectUsingTheTildeUri";
		branching.createBranch(BranchPathUtils.createMainPath().child(childBranchName)).statusCode(201);
		
		ResourceURI target = SnomedContentRule.SNOMEDCT.withSpecialResourceIdPart(childBranchName);
		createDescription(target, Json.object(
			"term", "RandomTerm",
			"languageCode", "en",
			"typeId", Concepts.SYNONYM,
			"conceptId", Concepts.ROOT_CONCEPT,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"acceptability", Map.of(
				Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED
			)
		));
		
		// fetch the most recent commit
		CommitInfos commits = RepositoryRequests.commitInfos().prepareSearchCommitInfo()
			.one()
			.filterBySubject(target.toString())
			.sortBy("timestamp:desc") 
			.build(SnomedTerminologyComponentConstants.TOOLING_ID)
			.execute(getBus())
			.getSync();
		
		assertThat(commits)
			.hasSize(1)
			.flatExtracting(CommitInfo::getSubjects)
			.contains(target.toString());
			
	}
	
}
