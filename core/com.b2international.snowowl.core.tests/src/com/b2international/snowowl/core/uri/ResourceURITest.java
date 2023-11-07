/*
 * Copyright 2020-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.CodeSystem;

/**
 * @since 7.5
 */
public class ResourceURITest {

	@Test
	public void headImplicit() throws Exception {
		final ResourceURI uri = CodeSystem.uri("SNOMEDCT");
		assertEquals("SNOMEDCT", uri.getResourceId());
		assertEquals(ResourceURI.HEAD, uri.getPath());
	}
	
	@Test
	public void latestReleasedExplicit() throws Exception {
		final ResourceURI uri = CodeSystem.uri("SNOMEDCT/LATEST");
		assertEquals("SNOMEDCT", uri.getResourceId());
		assertEquals(ResourceURI.LATEST, uri.getPath());
	}
	
	@Test
	public void explicitVersion() throws Exception {
		final ResourceURI uri = CodeSystem.uri("SNOMEDCT/2019-07-31");
		assertEquals("SNOMEDCT", uri.getResourceId());
		assertEquals("2019-07-31", uri.getPath());
	}
	
	@Test
	public void extensionVersion() throws Exception {
		final ResourceURI uri = CodeSystem.uri("SNOMEDCT-EXT/2019-10-31");
		assertEquals("SNOMEDCT-EXT", uri.getResourceId());
		assertEquals("2019-10-31", uri.getPath());
	}
	
	@Test
	public void explicitBranch() throws Exception {
		final ResourceURI uri = CodeSystem.uri("SNOMEDCT-EXT/a/b");
		assertEquals("SNOMEDCT-EXT", uri.getResourceId());
		assertEquals("a/b", uri.getPath());
	}
	
	@Test
	public void timestampPart() throws Exception {
		final ResourceURI uri = CodeSystem.uri("SNOMEDCT-EXT/a/b@1234567890");
		assertEquals("SNOMEDCT-EXT", uri.getResourceId());
		assertEquals("a/b", uri.getPath());
		assertEquals("@1234567890", uri.getTimestampPart());
	}
	
	@Test(expected = BadRequestException.class)
	public void timestampPartValidation() throws Exception {
		CodeSystem.uri("SNOMEDCT-EXT/a/b@yesterday");
	}
	
	@Test
	public void specialIdPart() throws Exception {
		ResourceURI uri = CodeSystem.uri("SNOMEDCT-EXT~2022-01-31");
		assertEquals("SNOMEDCT-EXT~2022-01-31", uri.getResourceId());
		assertThat(uri.getPath()).isNull();
	}
	
	@Test(expected = BadRequestException.class)
	public void specialIdPartAndPath() throws Exception {
		CodeSystem.uri("SNOMEDCT-EXT~2022-01-31/child");
	}
	
	@Test(expected = BadRequestException.class)
	public void specialIdPartAndEmptyPath() throws Exception {
		CodeSystem.uri("SNOMEDCT-EXT~2022-01-31/");
	}
	
	@Test
	public void removeSpecialIdPart() throws Exception {
		ResourceURI uri = CodeSystem.uri("SNOMEDCT-EXT~2022-01-31").withoutSpecialResourceIdPart();
		assertThat(uri.toString()).isEqualTo("codesystems/SNOMEDCT-EXT");
		assertThat(uri.getResourceId()).isEqualTo("SNOMEDCT-EXT");
		assertThat(uri.getPath()).isEqualTo(ResourceURI.HEAD);
	}
	
	@Test
	public void appendSpecialIdPart() throws Exception {
		ResourceURI uri = CodeSystem.uri("SNOMEDCT-EXT").withSpecialResourceIdPart("2022-01-31");
		assertThat(uri.toString()).isEqualTo("codesystems/SNOMEDCT-EXT~2022-01-31");
		assertThat(uri.getResourceId()).isEqualTo("SNOMEDCT-EXT~2022-01-31");
		assertThat(uri.getPath()).isNull();
	}
	
	@Test
	public void convertTimestampedSpecialURIToRegular() throws Exception {
		ResourceURI uri = CodeSystem.uri("SNOMEDCT-EXT").withSpecialResourceIdPart("2022-01-31").withTimestampPart("@123123123");
		ResourceURI regularForm = uri.toRegularURI();
		
		assertThat(uri.toString()).isEqualTo("codesystems/SNOMEDCT-EXT~2022-01-31@123123123");
		assertThat(uri.getResourceId()).isEqualTo("SNOMEDCT-EXT~2022-01-31");
		assertThat(uri.getPath()).isNull();
		assertThat(uri.getTimestampPart()).isEqualTo("@123123123");
		
		assertThat(regularForm.toString()).isEqualTo("codesystems/SNOMEDCT-EXT/2022-01-31@123123123");
		assertThat(regularForm.getResourceId()).isEqualTo("SNOMEDCT-EXT");
		assertThat(regularForm.getPath()).isEqualTo("2022-01-31");
		assertThat(regularForm.getTimestampPart()).isEqualTo("@123123123");
	}
	
}
