/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.components;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.context.TerminologyResourceContentRequest;
import com.b2international.snowowl.core.context.TerminologyResourceRequest;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.request.RevisionIndexReadRequest;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.rest.BranchBase;

/**
 * @since 7.11
 */
@BranchBase(isolateTests = false)
public class SnomedPartialLoadingApiTest extends AbstractSnomedApiTest {

	@Test
	public void partialLoadingConceptApi() throws Exception {
		PageableCollectionResource<String[]> hits = new TerminologyResourceRequest<>(SnomedTerminologyComponentConstants.TOOLING_ID, SnomedContentRule.SNOMEDCT.getResourceId(),
			new TerminologyResourceContentRequest<>(
				new RevisionIndexReadRequest<>(
					context -> {
						return SnomedRequests.prepareSearchConcept()
								.all()
								.setFields(SnomedRf2Headers.FIELD_ID, SnomedRf2Headers.FIELD_EFFECTIVE_TIME)
								.toRawSearch(String[].class)
								.build()
								.execute(context);
					}
				)
			)
		).execute(ApplicationContext.getServiceForClass(Environment.class));
		hits.forEach(hit -> {
			// simple assertions to parse the ID as SCT ID and effective time as short date
			assertTrue(SnomedIdentifiers.isValid(hit[0]));
			Long.valueOf(hit[1]);
		});
	}
	
}
