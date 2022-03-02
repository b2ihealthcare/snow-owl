/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.io.commit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.elasticsearch.core.Map;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.authorization.AuthorizedEventBus;
import com.b2international.snowowl.core.authorization.AuthorizedRequest;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.JWTGenerator;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.Role;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @since 5.2
 */
public class CommitInfoRequestTest {
	
	private static final String USER_ID = "system";
	private static final String REPOSITORY_ID = SnomedTerminologyComponentConstants.TOOLING_ID;
	private static final String BRANCH = Branch.MAIN_PATH;
	
	private IEventBus bus;
	
	@Before
	public void setup() {
		this.bus = Services.bus();
	}
	
	@Test(expected = NotFoundException.class)
	public void getNonExistentCommitInfo() {
		RepositoryRequests
			.commitInfos()
			.prepareGetCommitInfo(UUID.randomUUID().toString())
			.build(REPOSITORY_ID)
			.execute(bus)
			.getSync();
	}
	
	@Test
	public void searchCommitInfoByBranch() {
		final String oid = UUID.randomUUID().toString();
		final String shortName = "Resource6";
		final String comment = "Code system for commit info 6";
		final String branchName = "Test6";
		final String term = "Test Description 6";
				
		createCodeSystem(shortName, oid, comment);
		
		final String branchPath = createBranch(String.format("%s/%s", BRANCH, shortName), branchName);
		createDescription(ResourceURI.branch(CodeSystem.RESOURCE_TYPE, shortName, branchName), term, comment);
				
		//Search as admin
		assertEquals(1, RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByBranch(branchPath)
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync().getTotal());
		
		final Permission userPermission = Permission.requireAll(Permission.OPERATION_BROWSE, String.format("%s*", shortName));
		final List<Role> roles = List.of(new Role("Editor", List.of(userPermission)));
		final String userName = "User6";
		final User user =  new User(userName, roles);
		final IEventBus authorizedBus = new AuthorizedEventBus(bus,
				ImmutableMap.of(AuthorizedRequest.AUTHORIZATION_HEADER, Services.service(JWTGenerator.class).generate(user))
			);
		
		//Search as user with limited permissions
		assertEquals(1, RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByBranch(branchPath)
				.build(REPOSITORY_ID)
				.execute(authorizedBus)
				.getSync()
				.getTotal());
	}
	
	private void createDescription(final ResourceURI uri, final String term, final String commitComment) {
		SnomedRequests.prepareNewDescription()
			.setAcceptability(Map.of())
			.setActive(true)
			.setCaseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
			.setConceptId(Concepts.ROOT_CONCEPT)
			.setIdFromNamespace("")
			.setLanguageCode("en-US")
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setTerm(term)
			.setTypeId(Concepts.SYNONYM)
			.build(uri, USER_ID, commitComment)
			.execute(bus)
			.getSync();
	}
	
	private String createBranch(final String parent, final String name) {
		return RepositoryRequests.branching()
				.prepareCreate()
				.setParent(parent)
				.setName(name)
				.build(SnomedTerminologyComponentConstants.TOOLING_ID)
				.execute(bus)
				.getSync();
	}
	
	@Test
	public void searchCommitOnSubBranch() {
		//Search with no branch filter, to test security filter for user with limited resources
		final String oid = UUID.randomUUID().toString();
		final String shortName = "Resource7";
		final String comment = "Code system for commit info 7";
		final String branchName = "Test7";
		final String commitComment = "Create Description 7";
		final String term = "Test Description 7";
		
		//Commit on resource branch
		createCodeSystem(shortName, oid, comment);	
		createDescription(ResourceURI.of(CodeSystem.RESOURCE_TYPE, shortName), term, commitComment);
		
		//Commit on version branch
		final String branchPath = createBranch(String.format("%s/%s", BRANCH, shortName), branchName);
		createDescription(ResourceURI.branch(CodeSystem.RESOURCE_TYPE, shortName, branchName), term, commitComment);
		
		//Commit on deeper branch
		final String newBranchName = String.format("%s/%s", branchName, branchName);
		createBranch(branchPath, branchName);
		createDescription(ResourceURI.branch(CodeSystem.RESOURCE_TYPE, shortName, newBranchName), term, commitComment);
				
		final Permission userPermission = Permission.requireAll(Permission.OPERATION_BROWSE, String.format("%s*", shortName));
		final List<Role> roles = List.of(new Role("Editor", List.of(userPermission)));
		final String userName = "User7";
		final User user =  new User(userName, roles);
		final IEventBus authorizedBus = new AuthorizedEventBus(bus,
				ImmutableMap.of(AuthorizedRequest.AUTHORIZATION_HEADER, Services.service(JWTGenerator.class).generate(user))
			);
		
		//Search as user with permission only to access the resource and one sub branch
		assertEquals(2, RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByComment(commitComment)
				.build(REPOSITORY_ID)
				.execute(authorizedBus)
				.getSync()
				.getTotal());
		
		//Search as admin user with permission to access all
		assertEquals(3, RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByComment(commitComment)
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync()
				.getTotal());
	}
	
	@Test
	public void getCommitInfo() {
		final String oid = UUID.randomUUID().toString();
		final String shortName = UUID.randomUUID().toString();
		final String comment = "Code system for commit info 1";
		
		createCodeSystem(oid, shortName, comment);
		final String id = getCommitInfoByComment(comment).getId();
		
		Request<RepositoryContext, CommitInfo> req = RepositoryRequests
				.commitInfos()
				.prepareGetCommitInfo(id)
				.build();
		
		CommitInfo commitInfo = new ResourceRepositoryRequestBuilder<CommitInfo>() {
			@Override
			public Request<RepositoryContext, CommitInfo> build() {
				return req;
			}
		 }.buildAsync().execute(bus).getSync();
		
		assertEquals(id, commitInfo.getId());
		assertEquals(comment, commitInfo.getComment());
		assertEquals(BRANCH, commitInfo.getBranch());
		assertEquals(USER_ID, commitInfo.getAuthor());
	}
	
	@Test
	public void searchCommitInfoByComment() {
		final String oid = UUID.randomUUID().toString();
		final String shortName = UUID.randomUUID().toString();
		final String comment = "Code system for commit info 2";
		
		createCodeSystem(oid, shortName, comment);
		final CommitInfo commitInfo = getCommitInfoByComment(comment);
		
		assertEquals(comment, commitInfo.getComment());
	}
	
	@Test
	public void searchCommitInfoByUserId() {
		final String oid = UUID.randomUUID().toString();
		final String shortName = UUID.randomUUID().toString();
		final String comment = "Code system for commit info 3";
		final String userId = "commitInfo";
		
		createCodeSystem(oid, shortName, comment, userId);
		
		Request<RepositoryContext, CommitInfos> req = RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByAuthor(userId)
				.build();
		
		final CommitInfos commitInfos = new ResourceRepositoryRequestBuilder<CommitInfos>() {
			@Override
			public Request<RepositoryContext, CommitInfos> build() {
				return req;
			}
		 }.buildAsync().execute(bus).getSync();
		
		assertEquals(commitInfos.getTotal(), 1);
		
		final CommitInfo commitInfo = Iterables.getOnlyElement(commitInfos);
		assertEquals(userId, commitInfo.getAuthor());
	}
		
	@Test
	public void searchCommitInfoByTimestamp() {
		final String oid = UUID.randomUUID().toString();
		final String shortName = UUID.randomUUID().toString();
		final String comment = "Code system for commit info 5";
		
		createCodeSystem(shortName, oid, comment);
		final CommitInfo commitInfo = getCommitInfoByComment(comment);
		
		Request<RepositoryContext, CommitInfos> req = RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByTimestamp(commitInfo.getTimestamp())
				.build();
		
		final CommitInfos commitInfos = new ResourceRepositoryRequestBuilder<CommitInfos>() {
				@Override
				public Request<RepositoryContext, CommitInfos> build() {
					return req;
				}
			 }.buildAsync().execute(bus).getSync();
		
		assertTrue(commitInfos.getTotal() == 1);
		assertEquals(commitInfo.getTimestamp(), Iterables.getOnlyElement(commitInfos.getItems()).getTimestamp());
	}
	
	private void createCodeSystem(final String shortName, final String oid, final String comment) {
		createCodeSystem(shortName, oid, comment, USER_ID);
	}
	
	private void createCodeSystem(final String shortName, final String oid, final String comment, final String userId) {
		CodeSystemRequests.prepareNewCodeSystem()
			.setId(shortName)
			.setOid(oid)
			.setUrl(String.format("http://snomed.info/sct/%s", shortName))
			.setTitle(String.format("%s - %s", shortName, oid))
			.setLanguage("en")
			.setDescription("citation")
			.setToolingId(SnomedTerminologyComponentConstants.TOOLING_ID)
			.build(userId, comment)
			.execute(bus)
			.getSync();
	}
	
	private CommitInfo getCommitInfoByComment(final String comment) {
		Request<RepositoryContext, CommitInfos> req = RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByComment(comment)
				.build();
		
		final CommitInfos commitInfos = new ResourceRepositoryRequestBuilder<CommitInfos>() {

			@Override
			public Request<RepositoryContext, CommitInfos> build() {
				return req;
			}
			
		 }
		 .buildAsync()
		 .execute(bus)
		 .getSync();
		 
		assertEquals(commitInfos.getTotal(), 1);
		
		return Iterables.getOnlyElement(commitInfos);
	}

}
