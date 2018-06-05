/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.japi.commitinfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.commit.CommitInfos;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.collect.Iterables;

/**
 * @since 5.2
 */
public class CommitInfoRequestTest {
	
	private static final String USER_ID = "system";
	private static final String REPOSITORY_ID = "snomedStore";
	private static final String BRANCH = IBranchPath.MAIN_BRANCH;
	
	private IEventBus bus;
	
	@Before
	public void setup() {
		this.bus = ApplicationContext.getInstance().getService(IEventBus.class);
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
	public void getCommitInfo() {
		final String oid = UUID.randomUUID().toString();
		final String shortName = UUID.randomUUID().toString();
		final String comment = "Code system for commit info 1";
		
		createCodeSystem(oid, shortName, comment);
		
		final CommitInfos commitInfos = RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByComment(comment)
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync();
		
		assertEquals(commitInfos.getTotal(), 1);
		
		final String id = Iterables.getOnlyElement(commitInfos).getId();
		
		final CommitInfo commitInfo = RepositoryRequests
				.commitInfos()
				.prepareGetCommitInfo(id)
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync();
		
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
		
		final CommitInfos commitInfos = RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByAuthor(userId)
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync();
		
		assertEquals(commitInfos.getTotal(), 1);
		
		final CommitInfo commitInfo = Iterables.getOnlyElement(commitInfos);
		assertEquals(userId, commitInfo.getAuthor());
	}
	
	@Test
	public void searchCommitInfoByBranch() {
		final String oid = UUID.randomUUID().toString();
		final String shortName = UUID.randomUUID().toString();
		final String comment = "Code system for commit info 4";
		
		createCodeSystem(oid, shortName, comment);
		
		final CommitInfos commitInfos = RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByBranch(IBranchPath.MAIN_BRANCH)
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync();
		
		assertTrue(commitInfos.getTotal() >= 1);
	}
	
	@Test
	public void searchCommitInfoByTimestamp() {
		final String oid = UUID.randomUUID().toString();
		final String shortName = UUID.randomUUID().toString();
		final String comment = "Code system for commit info 5";
		
		createCodeSystem(oid, shortName, comment);
		final CommitInfo commitInfo = getCommitInfoByComment(comment);
		
		final CommitInfos commitInfos = RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByTimestamp(commitInfo.getTimestamp())
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync();
		
		assertTrue(commitInfos.getTotal() == 1);
		assertEquals(commitInfo.getTimestamp(), Iterables.getOnlyElement(commitInfos.getItems()).getTimestamp());
	}
	
	private void createCodeSystem(final String shortName, final String oid, final String comment) {
		createCodeSystem(shortName, oid, comment, USER_ID);
	}
	
	private void createCodeSystem(final String shortName, final String oid, final String comment, final String userId) {
		CodeSystemRequests.prepareNewCodeSystem()
			.setShortName(shortName)
			.setOid(oid)
			.setName(String.format("%s - %s", shortName, oid))
			.setLanguage("en")
			.setBranchPath(IBranchPath.MAIN_BRANCH)
			.setCitation("citation")
			.setIconPath("snomed.png")
			.setRepositoryUuid("snomedStore")
			.setTerminologyId("concept")
			.setLink("www.ihtsdo.org")
			.build("snomedStore", IBranchPath.MAIN_BRANCH, userId, comment)
			.execute(bus)
			.getSync();
	}
	
	private CommitInfo getCommitInfoByComment(final String comment) {
		final CommitInfos commitInfos = RepositoryRequests
				.commitInfos()
				.prepareSearchCommitInfo()
				.filterByComment(comment)
				.build(REPOSITORY_ID)
				.execute(bus)
				.getSync();
		
		assertEquals(commitInfos.getTotal(), 1);
		
		return Iterables.getOnlyElement(commitInfos);
	}

}
