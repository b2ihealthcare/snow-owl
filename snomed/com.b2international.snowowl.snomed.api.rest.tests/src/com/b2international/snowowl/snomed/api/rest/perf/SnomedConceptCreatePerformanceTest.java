/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.perf;

import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewConcept;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.databene.contiperf.junit.ContiPerfRuleExt;
import org.junit.Rule;
import org.junit.Test;

import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;

/**
 * @since 4.7
 */
public class SnomedConceptCreatePerformanceTest extends AbstractSnomedApiTest {

	@Rule
	public ContiPerfRule rule = new ContiPerfRuleExt();
	
	@Test
	@PerfTest(invocations = 10)
	public void createConcept() throws Exception {
		createNewConcept(branchPath);
	}
	
}
