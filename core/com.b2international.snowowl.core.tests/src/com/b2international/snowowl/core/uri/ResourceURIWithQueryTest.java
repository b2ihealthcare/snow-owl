/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ResourceURIWithQuery;

/**
 * @since 8.5
 */
public class ResourceURIWithQueryTest {

	@Test(expected = BadRequestException.class)
	public void nullUri() throws Exception {
		new ResourceURIWithQuery(null);
	}
	
	@Test(expected = BadRequestException.class)
	public void emptyUri() throws Exception {
		new ResourceURIWithQuery("");
	}
	
	@Test
	public void basic_noQueryPart() throws Exception {
		ResourceURIWithQuery uri = new ResourceURIWithQuery("codesystems/SNOMEDCT");
		assertThat(uri.getResourceUri()).isEqualTo(new ResourceURI("codesystems/SNOMEDCT"));
		assertThat(uri.getQuery()).isEmpty();
	}
	
	@Test
	public void versioned_noQueryPart() throws Exception {
		ResourceURIWithQuery uri = new ResourceURIWithQuery("codesystems/SNOMEDCT/2022-07-31");
		assertThat(uri.getResourceUri()).isEqualTo(new ResourceURI("codesystems/SNOMEDCT/2022-07-31"));
		assertThat(uri.getQuery()).isEmpty();
	}
	
	@Test
	public void basic_query() throws Exception {
		ResourceURIWithQuery uri = new ResourceURIWithQuery("codesystems/SNOMEDCT?ecl=123123123|TERM|");
		assertThat(uri.getResourceUri()).isEqualTo(new ResourceURI("codesystems/SNOMEDCT"));
		assertThat(uri.getQuery()).isEqualTo("ecl=123123123|TERM|");
	}
	
	@Test
	public void versioned_query() throws Exception {
		ResourceURIWithQuery uri = new ResourceURIWithQuery("codesystems/SNOMEDCT/2022-07-31?ecl=<12312313|TERM|");
		assertThat(uri.getResourceUri()).isEqualTo(new ResourceURI("codesystems/SNOMEDCT/2022-07-31"));
		assertThat(uri.getQuery()).isEqualTo("ecl=<12312313|TERM|");
	}
	
}
