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
package com.b2international.snowowl.core.codesystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 4.7
 */
public class CodeSystemRequestTest {

	private static final String SNOMEDCT = "SNOMEDCT";
	private static final String BRANCH = Branch.MAIN_PATH;

	private IEventBus bus;

	@Before
	public void setup() {
		this.bus = Services.bus();
	}
	
	@Test
	public void getAllCodeSystems() {
		final String shortName = "newCodeSystemShortName";
		final String oid = "newCodeSystemOid";
		
		assertCodeSystemCreated(shortName, oid);
		
		final CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
				.buildAsync()
				.getRequest()
				.execute(Services.context());
		
		assertThat(codeSystems.getItems()).hasSizeGreaterThanOrEqualTo(2);
	}
	
	@Test
	public void getCodeSystemByShortNameFromAllRepositoreis() {
		final String id = "ShortName";
		final String oid = "Oid";
		
		assertCodeSystemCreated(id, oid);
		
		final CodeSystems existingCodeSystem = CodeSystemRequests.prepareSearchCodeSystem()
				.filterById(id)
				.buildAsync()
				.getRequest()
				.execute(Services.context());

		assertThat(existingCodeSystem.getItems()).hasSize(1);
		assertThat(existingCodeSystem.getItems().get(0).getId()).isEqualTo(id);
	}
	
	@Test
	public void getNonExistentCodeSystemByShortNameFromAllRepositoreis() {
		
		final CodeSystems nonExistentCodeSystem = CodeSystemRequests.prepareSearchCodeSystem()
				.filterById("not a valid code system short name")
				.buildAsync()
				.getRequest()
				.execute(Services.context());
		assertThat(nonExistentCodeSystem.getItems()).hasSize(0);
	}
	
	@Test
	public void getCodeSystem() {
		final CodeSystem codeSystem = getCodeSystem(SNOMEDCT);
		assertNotNull(codeSystem);
	}
	
	@Test(expected = NotFoundException.class)
	public void getNonExistentCodeSystem() {
		getCodeSystem("non-existent-code-system-short-name");
	}
	
	@Test
	public void createCodeSystem() {
		final String shortName = "sn1";
		final String oid = "oid1";

		assertCodeSystemCreated(shortName, oid);
		
		final CodeSystem codeSystem = getCodeSystem(shortName);
		assertThat(codeSystem.getId()).isEqualTo(shortName);
	}
	
	@Test
	public void updateCodeSystem() {
		final String shortName = "sn2";
		final String oid = "oid2";
		
		createCodeSystem(shortName, oid);
		final CodeSystem oldCodeSystem = getCodeSystem(shortName);
		assertNotNull(oldCodeSystem);
		
		CodeSystemRequests.prepareUpdateCodeSystem(shortName)
			.setTitle("updated name")
			.build(RestExtensions.USER, String.format("Updated code system %s.", shortName))
			.execute(bus)
			.getSync();
		
		final CodeSystem updatedCodeSystem = getCodeSystem(shortName);
		assertNotNull(updatedCodeSystem);
		assertThat(updatedCodeSystem.getTitle()).isEqualTo("updated name");
	}
	
	@Test(expected = NotFoundException.class)
	public void updateCodeSystemWithInvalidBranchPath() {
		final String shortName = "sn3";
		final String oid = "oid3";
		
		createCodeSystem(shortName, oid);
		final CodeSystem oldCodeSystem = getCodeSystem(shortName);
		assertNotNull(oldCodeSystem);
		
		CodeSystemRequests.prepareUpdateCodeSystem(shortName)
			.setBranchPath("non-existent-branch-path")
			.build(RestExtensions.USER, String.format("Updated code system %s.", shortName))
			.execute(bus)
			.getSync();
	}
	
	@Test
	public void searchCodeSystem() {
		final CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
			.filterById(SNOMEDCT)
			.buildAsync()
			.execute(bus)
			.getSync();
		
		assertThat(codeSystems.getItems()).hasSize(1);
		assertThat(codeSystems.getItems().get(0).getId()).isEqualTo(SNOMEDCT);
	}
	
	private void createCodeSystem(final String shortName, final String oid) {
		CodeSystemRequests.prepareNewCodeSystem()
			.setId(shortName)
			.setTitle(String.format("%s - %s", shortName, oid))
			.setUrl("www.ihtsdo.org")
			.setDescription("# Description")
			.setLanguage("en")
			.setBranchPath(BRANCH)
			.setToolingId("snomed")
			.setOid(oid)
			.build(RestExtensions.USER, String.format("New code system %s", shortName))
			.execute(bus)
			.getSync();
	}
	
	private CodeSystem getCodeSystem(final String shortName) {
		return CodeSystemRequests.prepareGetCodeSystem(shortName)
				.buildAsync()
				.execute(bus)
				.getSync();
	}
	
	private void assertCodeSystemCreated(final String shortName, final String oid) {
		createCodeSystem(shortName, oid);
		final CodeSystem codeSystem = getCodeSystem(shortName);
		assertNotNull(codeSystem);
	}

}
