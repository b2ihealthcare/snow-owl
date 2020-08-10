/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Test;

import com.b2international.snowowl.core.uri.CodeSystemURI;

/**
 * @since 7.8
 */
public class CodeSystemTest {

	@Test(expected = NullPointerException.class)
	public void codeSystemNullChildBranchToURI() throws Exception {
		CodeSystem cs = CodeSystem.builder()
				.shortName("SNOMEDCT")
				.branchPath("MAIN")
				.build();
		cs.getCodeSystemURI(null);
	}
	
	@Test
	public void codeSystemToURI() throws Exception {
		CodeSystem cs = CodeSystem.builder()
				.shortName("SNOMEDCT")
				.branchPath("MAIN")
				.build();
		CodeSystemURI uri = cs.getCodeSystemURI();
		assertThat(uri).isEqualTo(new CodeSystemURI("SNOMEDCT/HEAD"));
	}
	
	@Test
	public void codeSystemChildBranchToURI() throws Exception {
		CodeSystem cs = CodeSystem.builder()
				.shortName("SNOMEDCT")
				.branchPath("MAIN")
				.build();
		CodeSystemURI uri = cs.getCodeSystemURI("MAIN/child");
		assertThat(uri).isEqualTo(new CodeSystemURI("SNOMEDCT/child"));
	}
	
	@Test
	public void codeSystemMainBranchToURI() throws Exception {
		CodeSystem cs = CodeSystem.builder()
				.shortName("SNOMEDCT")
				.branchPath("MAIN")
				.build();
		CodeSystemURI uri = cs.getCodeSystemURI("MAIN");
		assertThat(uri).isEqualTo(new CodeSystemURI("SNOMEDCT/HEAD"));
	}
	
	@Test
	public void extensionCodeSystemChildBranchToURI() throws Exception {
		CodeSystem cs = CodeSystem.builder()
				.shortName("SNOMEDCT-UK")
				.branchPath("MAIN/SNOMEDCT-UK")
				.build();
		CodeSystemURI uri = cs.getCodeSystemURI("MAIN/SNOMEDCT-UK/child");
		assertThat(uri).isEqualTo(new CodeSystemURI("SNOMEDCT-UK/child"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void extensionCodeSystemUnrelatedChildBranchToURI() throws Exception {
		CodeSystem cs = CodeSystem.builder()
				.shortName("SNOMEDCT-UK")
				.branchPath("MAIN/SNOMEDCT-UK")
				.build();
		cs.getCodeSystemURI("MAIN/SNOMEDCT-US/child");
	}
	
}
