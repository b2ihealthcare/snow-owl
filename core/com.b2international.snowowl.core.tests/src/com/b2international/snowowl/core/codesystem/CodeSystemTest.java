/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ResourceURI;

/**
 * @since 7.8
 */
public class CodeSystemTest {

	private CodeSystem codeSystem = new CodeSystem();
	
	@Before
	public void setup() {
		codeSystem.setId("SNOMEDCT");
		codeSystem.setBranchPath("MAIN");		
	}
	
	@Test(expected = NullPointerException.class)
	public void codeSystemNullChildBranchToURI() throws Exception {
		codeSystem.getResourceURI(null);
	}
	
	@Test
	public void codeSystemChildBranchToURI() throws Exception {
		ResourceURI uri = codeSystem.getResourceURI("MAIN/child");
		assertThat(uri).isEqualTo(CodeSystem.uri("SNOMEDCT/child"));
	}
	
	@Test
	public void codeSystemMainBranchToURI() throws Exception {
		ResourceURI uri = codeSystem.getResourceURI("MAIN");
		assertThat(uri).isEqualTo(CodeSystem.uri("SNOMEDCT"));
	}
	
	@Test
	public void extensionCodeSystemChildBranchToURI() throws Exception {
		codeSystem.setId("SNOMEDCT-UK");
		codeSystem.setBranchPath("MAIN/SNOMEDCT-UK");
				
		ResourceURI uri = codeSystem.getResourceURI("MAIN/SNOMEDCT-UK/child");
		assertThat(uri).isEqualTo(CodeSystem.uri("SNOMEDCT-UK/child"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void extensionCodeSystemUnrelatedChildBranchToURI() throws Exception {
		codeSystem.setId("SNOMEDCT-UK");
		codeSystem.setBranchPath("MAIN/SNOMEDCT-UK");
		codeSystem.getResourceURI("MAIN/SNOMEDCT-US/child");
	}
	
}
