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
package com.b2international.snowowl.api.japi.codesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class CodeSystemRequestTest {

	private static final String SNOMEDCT = "SNOMEDCT";
	private static final String REPOSITORY_ID = "snomedStore";
	private static final String BRANCH = IBranchPath.MAIN_BRANCH;

	private IEventBus bus;
	private CodeSystemRequests requests;

	@Before
	public void setup() {
		this.bus = ApplicationContext.getInstance().getService(IEventBus.class);
		this.requests = new CodeSystemRequests(REPOSITORY_ID);
	}
	
	@Test
	public void getCodeSystem() {
		final ICodeSystem codeSystem = getCodeSystem(SNOMEDCT);
		assertNotNull(codeSystem);
	}
	
	@Test(expected = CodeSystemNotFoundException.class)
	public void getNonExistentCodeSystem() {
		getCodeSystem("non-existent-code-system-short-name");
	}
	
	@Test
	public void createCodeSystem() {
		final String shortName = "sn1";
		final String oid = "oid1";

		assertCodeSystemCreated(shortName, oid);
		
		final ICodeSystem codeSystem = getCodeSystem(shortName);
		assertEquals(shortName, codeSystem.getShortName());
	}
	
	@Test
	public void updateCodeSystem() {
		final String shortName = "sn2";
		final String oid = "oid2";
		
		createCodeSystem(shortName, oid);
		final ICodeSystem oldCodeSystem = getCodeSystem(shortName);
		assertNotNull(oldCodeSystem);
		
		requests.prepareUpdateCodeSystem(shortName)
			.setName("updated name")
			.build("system", BRANCH, String.format("Updated code system %s.", shortName))
			.executeSync(bus);
		
		final ICodeSystem updatedCodeSystem = getCodeSystem(shortName);
		assertNotNull(updatedCodeSystem);
		assertEquals("updated name", updatedCodeSystem.getName());
	}
	
	@Test(expected = NotFoundException.class)
	public void updateCodeSystemWithInvalidBranchPath() {
		final String shortName = "sn3";
		final String oid = "oid3";
		
		createCodeSystem(shortName, oid);
		final ICodeSystem oldCodeSystem = getCodeSystem(shortName);
		assertNotNull(oldCodeSystem);
		
		requests.prepareUpdateCodeSystem(shortName)
			.setBranchPath("non-existent-branch-path")
			.build("system", BRANCH, String.format("Updated code system %s.", shortName))
			.executeSync(bus);
	}
	
	@Test
	public void searchCodeSystem() {
		final CodeSystems codeSystems = requests.prepareSearchCodeSystem()
			.setShortName(SNOMEDCT)
			.build(BRANCH)
			.executeSync(bus);
		
		assertEquals(1, codeSystems.getItems().size());
		assertEquals(SNOMEDCT, Iterables.getOnlyElement(codeSystems.getItems()).getShortName());
	}
	
	private void createCodeSystem(final String shortName, final String oid) {
		requests.prepareNewCodeSystem()
			.setShortName(shortName)
			.setOid(oid)
			.setName(String.format("%s - %s", shortName, oid))
			.setLanguage("en")
			.setBranchPath(BRANCH)
			.setCitation("citation")
			.setIconPath("snomed.png")
			.setRepositoryUuid(REPOSITORY_ID)
			.setTerminologyId("concept")
			.setLink("www.ihtsdo.org")
			.build("system", BRANCH, String.format("New code system %s", shortName))
			.executeSync(bus);
	}
	
	private ICodeSystem getCodeSystem(final String shortName) {
		return requests.prepareGetCodeSystem()
				.setUniqueId(shortName)
				.build(BRANCH)
				.executeSync(bus);
	}
	
	private void assertCodeSystemCreated(final String shortName, final String oid) {
		createCodeSystem(shortName, oid);
		final ICodeSystem codeSystem = getCodeSystem(shortName);
		assertNotNull(codeSystem);
	}

}
