/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.commit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.elasticsearch.core.Map;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.authorization.AuthorizedEventBus;
import com.b2international.snowowl.core.authorization.AuthorizedRequest;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.core.identity.JWTSupport;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;

/**
 * @since 8.2
 */
public class SnomedCommitApiAuthorizationTest {

	private static final String USER_ID = "test-user";
	private static final String TOOLING_ID = SnomedTerminologyComponentConstants.TOOLING_ID;
	private static final String CODESYSTEM_ID = "CodeSystemID";
	
	// gives access to the MAIN branch of the CodeSystemID resource
	private String mainBranchAccessToken;
	private String singleChildBranchAccessToken;
	private String mainAndAllSubbranchesAccessToken;
	
	private static long mainCommitTimestamp;
	private static long child1CommitTimestamp;
	private static long child2CommitTimestamp;
	
	@BeforeClass
	public static void setupCommits() {
		// this generates one commit that sits in the resource repository
		var resourceUri = createCodeSystem(CODESYSTEM_ID);
		
		// generate one content commit on the resource directly
		mainCommitTimestamp = createDescription(resourceUri);
		
		createBranch(Branch.get(Branch.MAIN_PATH, resourceUri.getResourceId()), "childBranch1");
		createBranch(Branch.get(Branch.MAIN_PATH, resourceUri.getResourceId()), "childBranch2");
		// generate one commit on one of the childBranches
		child1CommitTimestamp = createDescription(resourceUri.withPath("childBranch1"));
		child2CommitTimestamp = createDescription(resourceUri.withPath("childBranch2"));
	} 
	
	@Before
	public void setup() {
		this.mainBranchAccessToken = generateAccessTokenForResourceAccess(CODESYSTEM_ID);
		this.mainAndAllSubbranchesAccessToken = generateAccessTokenForResourceAccess(CODESYSTEM_ID, String.format("%s/*", CODESYSTEM_ID));
		this.singleChildBranchAccessToken = generateAccessTokenForResourceAccess(CODESYSTEM_ID, String.format("%s/%s", CODESYSTEM_ID, "childBranch1"));
		
	}
	
	@Test
	public void searchCommitsWithUserAccessToResourceMainBranch() throws Exception {
		assertThat(searchCommits(mainBranchAccessToken))
			.extracting(CommitInfo::getTimestamp)
			.containsOnly(mainCommitTimestamp);
	}
	
	@Test
	public void searchCommitsWithUserAccessToSingleChildBranch() throws Exception {
		assertThat(searchCommits(singleChildBranchAccessToken))
			.extracting(CommitInfo::getTimestamp)
			.containsOnly(mainCommitTimestamp, child1CommitTimestamp);
	}
	
	@Test
	public void searchCommitsWithUserAccessToResourceMainAndChildBranches() throws Exception {
		assertThat(searchCommits(mainAndAllSubbranchesAccessToken))
			.extracting(CommitInfo::getTimestamp)
			.containsOnly(mainCommitTimestamp, child1CommitTimestamp, child2CommitTimestamp);
	}
	
	private CommitInfos searchCommits(String accessToken) {
		return RepositoryRequests
			.commitInfos()
			.prepareSearchCommitInfo()
			.all()
			.sortBy("timestamp:desc")
			.build(TOOLING_ID)
			.execute(new AuthorizedEventBus(Services.bus(), Map.of(AuthorizedRequest.AUTHORIZATION_HEADER, accessToken)))
			.getSync();		
	}

	private static long createDescription(final ResourceURI uri) {
		return SnomedRequests.prepareNewDescription()
			.setAcceptability(Map.of())
			.setActive(true)
			.setCaseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
			.setConceptId(Concepts.ROOT_CONCEPT)
			.setIdFromNamespace("")
			.setLanguageCode("en-US")
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setTerm(UUID.randomUUID().toString())
			.setTypeId(Concepts.SYNONYM)
			.build(uri, USER_ID, "Generate new description commit")
			.execute(Services.bus())
			.getSync()
			.getCommitTimestamp();
	}
	
	private static String createBranch(final String parent, final String name) {
		return RepositoryRequests.branching()
				.prepareCreate()
				.setParent(parent)
				.setName(name)
				.build(SnomedTerminologyComponentConstants.TOOLING_ID)
				.execute(Services.bus())
				.getSync();
	}
	
	private static ResourceURI createCodeSystem(final String codeSystemId) {
		final String oid = UUID.randomUUID().toString();
		CodeSystemRequests.prepareNewCodeSystem()
			.setId(codeSystemId)
			.setOid(oid)
			.setUrl(String.format("http://snomed.info/sct/%s", codeSystemId))
			.setTitle(String.format("%s - %s", codeSystemId, oid))
			.setLanguage("en")
			.setDescription("citation")
			.setToolingId(TOOLING_ID)
			.build(USER_ID, "Create Code System " + codeSystemId)
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES);
		return CodeSystem.uri(codeSystemId);
	}
	
	private String generateAccessTokenForResourceAccess(String...resourcesToGrantAccess) {
		final List<Permission> permissions = List.of(resourcesToGrantAccess).stream().map(res -> Permission.requireAll(Permission.OPERATION_BROWSE, res)).collect(Collectors.toList());
		final User user =  new User("test_user", permissions);
		return Services.service(JWTSupport.class).generate(user);
	}
	
}
