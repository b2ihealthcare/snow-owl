/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.b2international.commons.exceptions.LockedException;
import com.b2international.snowowl.core.locks.request.LockRequests;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 9.0
 */
public class ResourceLockRequestTest {

	@Test
	public void lockResource() {
		
		final String user = RestExtensions.USER;
		final String lockDescription = "locking the working branch for test purposes";
		
		LockRequests.prepareResourceLock()
			.setDescription(lockDescription)
			.build(SnomedContentRule.SNOMEDCT)
			.execute(Services.bus())
			.getSync();
		
		try {
			
			final LockedException lockedException = assertThrows(LockedException.class, () -> {
				SnomedRequests.prepareNewDescription()
					.setIdFromNamespace(Concepts.B2I_NAMESPACE)
					.setActive(true)
					.setModuleId(Concepts.MODULE_SCT_CORE)
					.setConceptId(Concepts.ROOT_CONCEPT)
					.setLanguageCode("en")
					.setTerm("Synonym for root concept")
					.setTypeId(Concepts.SYNONYM)
					.setCaseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
					.build(SnomedContentRule.SNOMEDCT, user, "Add new description to root concept")
					.execute(Services.bus())
					.getSync();
			});
			
			assertThat(lockedException.getMessage())
				.isEqualTo("Could not acquire requested lock(s) while committing changes because %s is %s", user, lockDescription);
			
		} finally {
			
			LockRequests.prepareResourceLock()
				.setDescription(lockDescription)
				.build(SnomedContentRule.SNOMEDCT)
				.execute(Services.bus())
				.getSync();
		}		
	}
}
