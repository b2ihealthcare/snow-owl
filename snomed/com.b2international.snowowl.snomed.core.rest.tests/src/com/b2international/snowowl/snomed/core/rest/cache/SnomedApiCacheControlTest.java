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

import org.elasticsearch.core.Map;
import org.junit.Test;

import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.test.commons.rest.BranchBase;

/**
 * @since 8.10
 */
@BranchBase(isolateTests = false)
public class SnomedApiCacheControlTest extends AbstractSnomedApiTest {

	@Test
	public void cacheControlVersioned() throws Exception {
		assertSearchConcepts(getDefaultSnomedResourceUri().withPath("2002-01-31"), Map.of(), 1)
			.statusCode(200)
			.header("Cache-Control", "s-maxage=0,max-age=0,must-revalidate")
			.header("ETag", "TODO");
	}
	
	@Test
	public void cacheControlUnversioned() throws Exception {
		assertSearchConcepts(getDefaultSnomedResourceUri(), Map.of(), 1)
			.statusCode(200)
			.header("Cache-Control", "s-maxage=0,max-age=0,must-revalidate")
			.header("ETag", "TODO");
	}
	
}
