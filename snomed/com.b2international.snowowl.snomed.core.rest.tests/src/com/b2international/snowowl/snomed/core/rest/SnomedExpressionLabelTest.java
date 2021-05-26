/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import com.b2international.snowowl.snomed.core.domain.SnomedConcept;

/**
 * @since 7.17.0
 */
public class SnomedExpressionLabelTest extends SnomedExpressionLabelRequests{
	@Test
	public void getEcl() throws Exception {
		String conceptId = createNewConcept(branchPath);

		String shortName = "SNOMEDCT-Test";
		createCodeSystem(branchPath, shortName).statusCode(201);
		
		List<String> expressionLabels = getExpressionLabels(branchPath.getPath(), List.of(conceptId));
		
		SnomedConcept concept = getConcept(conceptId, "fsn()");
		
		String validExpression = conceptId + " |" + concept.getFsn().getTerm() + "|";
		
		assertThat(expressionLabels.stream().findFirst().get(), equalTo(validExpression));
	}

}
