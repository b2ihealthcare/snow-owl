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
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.collect.ImmutableMap;
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
	
	@Test
	public void createCodeSystem() {
		final String shortName = "sn1";
		final String oid = "oid1";
		
		createCodeSystem(shortName, oid);
		final ICodeSystem codeSystem = getCodeSystem(shortName);
		
		assertNotNull(codeSystem);
		assertEquals(shortName, codeSystem.getShortName());
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
		final ImmutableMap<String, String> additionalProperties = ImmutableMap.<String, String>builder()
			.put("baseCodeSystemOID", "")
			.put("releaseType", "INT")
			.build();
		
		
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
			.setAdditionaProperties(additionalProperties)
			.build("system", BRANCH, String.format("New code system %s", shortName))
			.executeSync(bus);
	}
	
	private ICodeSystem getCodeSystem(final String shortName) {
		try {
			return requests.prepareGetCodeSystem()
					.setUniqueId(shortName)
					.build(BRANCH)
					.executeSync(bus);
		} catch (CodeSystemNotFoundException e) {
			return null;
		}
	}

}
