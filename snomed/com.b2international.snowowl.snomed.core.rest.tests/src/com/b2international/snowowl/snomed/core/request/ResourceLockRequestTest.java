/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.b2international.commons.exceptions.LockedException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.locks.request.LockRequests;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.rest.RestExtensions;

/**
 * @since 9.0.0
 */
public class ResourceLockRequestTest {

	@Test
	public void lockResource() throws Exception {
		
		final String user = RestExtensions.USER;
		final String lockDescription = "locking the working branch for test purposes";
		
		LockRequests.prepareResourceLock()
			.setDescription(lockDescription)
			.build(SnomedContentRule.SNOMEDCT)
			.execute(Services.bus())
			.getSync();
		
		try {
			
			final LockedException lockedException = assertThrows(LockedException.class, () -> {
				updateConcept(user, Concepts.FULLY_DEFINED);
			});
			
			assertThat(lockedException.getMessage())
				.isEqualTo("Could not acquire requested lock(s) while committing changes because %s is %s", user, lockDescription);
			
		} finally {
			
			LockRequests.prepareResourceUnlock()
				.setDescription(lockDescription)
				.build(SnomedContentRule.SNOMEDCT)
				.execute(Services.bus())
				.getSync();
		}
		
		updateConcept(user, Concepts.FULLY_DEFINED);
		SnomedConcept updatedConcept = SnomedRequests.prepareGetConcept(Concepts.ROOT_CONCEPT).build(SnomedContentRule.SNOMEDCT).execute(Services.bus()).getSync();
		assertEquals(Concepts.FULLY_DEFINED, updatedConcept.getDefinitionStatusId());
		
		updateConcept(user, Concepts.PRIMITIVE);
		SnomedConcept revertedConcept = SnomedRequests.prepareGetConcept(Concepts.ROOT_CONCEPT).build(SnomedContentRule.SNOMEDCT).execute(Services.bus()).getSync();
		assertEquals(Concepts.PRIMITIVE, revertedConcept.getDefinitionStatusId());
		assertEquals(EffectiveTimes.parse("2002-01-31"), revertedConcept.getEffectiveTime());
		
	}

	private void updateConcept(final String author, String definitionStatus) {
		SnomedRequests.prepareUpdateConcept(Concepts.ROOT_CONCEPT)
			.setDefinitionStatusId(definitionStatus)
			.build(SnomedContentRule.SNOMEDCT, author, "Update definition status of root concept")
			.execute(Services.bus())
			.getSync();
	}
}
