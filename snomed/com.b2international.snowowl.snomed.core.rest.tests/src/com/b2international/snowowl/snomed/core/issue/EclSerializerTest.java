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
package com.b2international.snowowl.snomed.core.issue;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.google.common.base.Throwables;

/**
 * 
 * @since 6.4
 */
public class EclSerializerTest extends AbstractSnomedApiTest {
	
	@Test
	public void verify() throws Exception {
		for (int rounds = 0; rounds < 100; rounds++) {
		
			AsyncRequest<SnomedConcepts> eclRequest = SnomedRequests.prepareSearchConcept()
					.all()
					.filterByEcl("<<" + Concepts.ROOT_CONCEPT + ":" + Concepts.MORPHOLOGY + " = " + Concepts.ROOT_CONCEPT)
					.build(SnomedContentRule.SNOMEDCT);
			
			List<Promise<SnomedConcepts>> promises = newArrayList();
			
			for (int i = 0; i < 4; i++) {
				promises.add(eclRequest.execute(getBus()));
			}
			
			final String error = Promise.all(promises)
					.then(concepts -> {
						return (String) null;
					})
					.fail(throwable -> {
						return (throwable.getMessage() != null) 
								? throwable.getMessage() 
								: Throwables.getRootCause(throwable).getClass().getSimpleName();
					})
					.getSync(1, TimeUnit.MINUTES);
			
			assertNull(error, error);
		}
	}
}
