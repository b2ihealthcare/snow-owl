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
package com.b2international.snowowl.core.uri;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @since 7.5
 */
public class CodeSystemURITest {

	@Test
	public void latestReleasedImplicit() throws Exception {
		final CodeSystemURI uri = new CodeSystemURI("SNOMEDCT");
		assertEquals("SNOMEDCT", uri.getCodeSystem());
		assertEquals(CodeSystemURI.LATEST, uri.getPath());
	}
	
	@Test
	public void latestReleasedExplicit() throws Exception {
		final CodeSystemURI uri = new CodeSystemURI("SNOMEDCT/LATEST");
		assertEquals("SNOMEDCT", uri.getCodeSystem());
		assertEquals(CodeSystemURI.LATEST, uri.getPath());
	}
	
	@Test
	public void explicitVersion() throws Exception {
		final CodeSystemURI uri = new CodeSystemURI("SNOMEDCT/2019-07-31");
		assertEquals("SNOMEDCT", uri.getCodeSystem());
		assertEquals("2019-07-31", uri.getPath());
	}
	
	@Test
	public void extensionVersion() throws Exception {
		final CodeSystemURI uri = new CodeSystemURI("SNOMEDCT-EXT/2019-10-31");
		assertEquals("SNOMEDCT-EXT", uri.getCodeSystem());
		assertEquals("2019-10-31", uri.getPath());
	}
	
	@Test
	public void explicitBranch() throws Exception {
		final CodeSystemURI uri = new CodeSystemURI("SNOMEDCT-EXT/a/b");
		assertEquals("SNOMEDCT-EXT", uri.getCodeSystem());
		assertEquals("a/b", uri.getPath());
	}
	
}
