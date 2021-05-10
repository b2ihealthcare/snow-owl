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
package com.b2international.snowowl.core.rest.info;

import static com.b2international.snowowl.core.rest.RepositoryApiAssert.assertAllRepositoryInfo;
import static com.b2international.snowowl.core.rest.RepositoryApiAssert.assertRepositoryInfoForExistingRepository;
import static com.b2international.snowowl.core.rest.RepositoryApiAssert.assertRepositoryInfoForInvalidRepository;

import java.util.UUID;

import org.junit.Test;

import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * @since 5.8
 */
public class RepositoryApiTest {
	
	@Test
	public void getAllRepositoryInfo() {
		assertAllRepositoryInfo();
	}

	@Test
	public void getSingleRepositoryInfo() {
		assertRepositoryInfoForExistingRepository(SnomedTerminologyComponentConstants.TERMINOLOGY_ID, Health.GREEN.name());
	}

	@Test
	public void getSingleNonExistentRepositoryInfo() {
		String nonExistentRepositoryId = UUID.randomUUID().toString();
		assertRepositoryInfoForInvalidRepository(nonExistentRepositoryId);
	}
	
}
