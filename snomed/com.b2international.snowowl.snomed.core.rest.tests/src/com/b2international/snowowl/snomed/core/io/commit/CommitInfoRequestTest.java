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

import java.util.UUID;

import org.elasticsearch.core.Map;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
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
				
		createCodeSystem(shortName, oid, comment);
		
		final String branchPath = RepositoryRequests.branching()
				.prepareCreate()
				.setParent(String.format("%s/%s", BRANCH, shortName))
				.setName(branchName)
				.build(SnomedTerminologyComponentConstants.TOOLING_ID)
				.execute(bus)
				.getSync();
		
		SnomedRequests.prepareNewDescription()
			.setAcceptability(Map.of())
			.setActive(true)
			.setCaseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
			.setConceptId(Concepts.ROOT_CONCEPT)
			.setIdFromNamespace("")
			.setLanguageCode("en-US")
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setTerm("Test Description 6")
			.setTypeId(Concepts.SYNONYM)
			.build(ResourceURI.branch(CodeSystem.RESOURCE_TYPE, shortName, branchName), USER_ID, "Create Description 6")
			.execute(bus)
			.getSync();
		
		assertEquals(1, RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByBranch(branchPath)
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync().getTotal());
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
