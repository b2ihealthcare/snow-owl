/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.core.rest.cache;

import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.childUnderRootWithDefaults;
import static org.assertj.core.api.Assertions.assertThat;

import org.elasticsearch.core.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.test.commons.SnomedContentRule;

/**
 * @since 9.2
 */
public class SnomedApiCacheControlTest extends AbstractSnomedApiTest {

	@Test
	public void cacheControlVersioned() throws Exception {
		assertSearchConcepts(SnomedContentRule.SNOMEDCT.withPath("2002-01-31"), Map.of(), 1)
			.statusCode(200)
			.header("Cache-Control", "s-maxage=0,max-age=0,must-revalidate")
			.header("ETag", CoreMatchers.notNullValue());
	}
	
	@Test
	public void cacheControlUnversioned() throws Exception {
		assertSearchConcepts(getDefaultSnomedResourceUri(), Map.of(), 1)
			.statusCode(200)
			.header("Cache-Control", "s-maxage=0,max-age=0,must-revalidate")
			.header("ETag", CoreMatchers.notNullValue());
	}
	
	@Test
	public void eTagGetsUpdatedAfterCommit() throws Exception {
		String eTagBeforeCommit = assertSearchConcepts(getDefaultSnomedResourceUri(), Map.of(), 1)
			.statusCode(200)
			.header("Cache-Control", "s-maxage=0,max-age=0,must-revalidate")
			.header("ETag", CoreMatchers.notNullValue())
			.extract()
			.header("ETag");
		
		createConcept(branchPath, childUnderRootWithDefaults());
		
		String eTagAfterCommit = assertSearchConcepts(getDefaultSnomedResourceUri(), Map.of(), 1)
				.statusCode(200)
				.header("Cache-Control", "s-maxage=0,max-age=0,must-revalidate")
				.header("ETag", CoreMatchers.notNullValue())
				.extract()
				.header("ETag");
		
		assertThat(eTagAfterCommit).isNotEqualTo(eTagBeforeCommit);
	}
	
}
