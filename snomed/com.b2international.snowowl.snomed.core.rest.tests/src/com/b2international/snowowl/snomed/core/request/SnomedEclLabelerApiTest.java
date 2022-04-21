/*
 * Copyright 2020-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewConcept;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.ecl.LabeledEclExpressions;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator;

import io.restassured.http.ContentType;

/**
 * @since 7.6
 */
public class SnomedEclLabelerApiTest extends AbstractSnomedApiTest {

	private String label(String expression) {
		return label(expression, SnomedConcept.Expand.FULLY_SPECIFIED_NAME);
	}
	
	private String label(String expression, String descriptionType) {
		return label(expression, descriptionType, Collections.emptyList());
	}
	
	private String label(String expression, List<ExtendedLocale> locales) {
		return label(expression, SnomedConcept.Expand.FULLY_SPECIFIED_NAME, locales);
	}

	private String label(String expression, String descriptionType, List<ExtendedLocale> locales) {
		return bulkLabel(List.of(expression), descriptionType, locales)
				.first()
				.get();
	}
	
	private LabeledEclExpressions bulkLabel(List<String> expressions, String descriptionType, List<ExtendedLocale> locales) {
		return CodeSystemRequests.prepareEclLabeler(getDefaultSnomedResourceUri().withoutResourceType(), expressions)
				.setLocales(locales)
				.setDescriptionType(descriptionType)
				.buildAsync()
				.execute(Services.bus())
				.getSync();
	}

	@Test
	public void emptyExpression() throws Exception {
		label("");
	}

	@Test
	public void conceptReferenceMissingConcept() throws Exception {
		// no indexed concept document can be found for the given ID, labeler just returns the ID
		String randomId = RandomSnomedIdentiferGenerator.generateConceptId(Concepts.B2I_NAMESPACE);
		String result = label(randomId);
		assertEquals(randomId, result);
	}

	@Test
	public void conceptReferenceDefaultLabel() throws Exception {
		// there is a single FSN indexed for the concept and no locales specified in the request, returns the first FSN in the preferredDescriptions
		// list
		String fsn = "SNOMED CT Concept (SNOMED RT+CTV3)";
		createDescription(getDefaultSnomedResourceUri(), Map.of(
			"term", fsn,
			"languageCode", "en",
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"conceptId", Concepts.ROOT_CONCEPT,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"acceptability", Map.of(
				Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED
			)
		));
		String result = label(Concepts.ROOT_CONCEPT);
		assertEquals(Concepts.ROOT_CONCEPT + " |" + fsn + "|", result);
	}
	
	@Test
	public void conceptReferenceDefaultLabelWithLocale() throws Exception {
		String fsnUk = "SNOMED CT Concept (uk)";
		String fsnUs = "SNOMED CT Concept (us)";
		createDescription(getDefaultSnomedResourceUri(), Map.of(
			"term", fsnUk,
			"languageCode", "en",
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"conceptId", Concepts.ROOT_CONCEPT,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"acceptability", Map.of(
				Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED
			)
		));
		createDescription(getDefaultSnomedResourceUri(), Map.of(
			"term", fsnUs,
			"languageCode", "en",
			"typeId", Concepts.FULLY_SPECIFIED_NAME,
			"conceptId", Concepts.ROOT_CONCEPT,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"acceptability", Map.of(
				Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED
			)
		));
		
		String result = label(Concepts.ROOT_CONCEPT, List.of(ExtendedLocale.valueOf("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US)));
		assertEquals(Concepts.ROOT_CONCEPT + " |" + fsnUs + "|", result);
	}
	
	@Test
	public void conceptReferencePtWithLocale() throws Exception {
		String ptUk = "SNOMED CT Concept UK";
		String ptUs = "SNOMED CT Concept US";
		createDescription(getDefaultSnomedResourceUri(), Map.of(
			"term", ptUk,
			"languageCode", "en",
			"typeId", Concepts.SYNONYM,
			"conceptId", Concepts.ROOT_CONCEPT,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"acceptability", Map.of(
				Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED
			)
		));
		createDescription(getDefaultSnomedResourceUri(), Map.of(
			"term", ptUs,
			"languageCode", "en",
			"typeId", Concepts.SYNONYM,
			"conceptId", Concepts.ROOT_CONCEPT,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"acceptability", Map.of(
				Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED
			)
		));
		
		String result = label(Concepts.ROOT_CONCEPT, SnomedConcept.Expand.PREFERRED_TERM, List.of(ExtendedLocale.valueOf("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US)));
		assertEquals(Concepts.ROOT_CONCEPT + " |" + ptUs + "|", result);
	}
	
	@Test
	public void bulkLabelExpressions() throws Exception {
		String rootPtUk = "SNOMED CT Concept UK";
		String rootPtUs = "SNOMED CT Concept US";
		String isaPtUk = "Is a UK";
		String isaPtUs = "Is a US";
		
		createDescription(getDefaultSnomedResourceUri(), Map.of(
			"term", rootPtUk,
			"languageCode", "en",
			"typeId", Concepts.SYNONYM,
			"conceptId", Concepts.ROOT_CONCEPT,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"acceptability", Map.of(
				Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED
			)
		));
		createDescription(getDefaultSnomedResourceUri(), Map.of(
			"term", rootPtUs,
			"languageCode", "en",
			"typeId", Concepts.SYNONYM,
			"conceptId", Concepts.ROOT_CONCEPT,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"acceptability", Map.of(
				Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED
			)
		));
		
		createDescription(getDefaultSnomedResourceUri(), Map.of(
			"term", isaPtUk,
			"languageCode", "en",
			"typeId", Concepts.SYNONYM,
			"conceptId", Concepts.IS_A,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"acceptability", Map.of(
				Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED
			)
		));
		createDescription(getDefaultSnomedResourceUri(), Map.of(
			"term", isaPtUs,
			"languageCode", "en",
			"typeId", Concepts.SYNONYM,
			"conceptId", Concepts.IS_A,
			"moduleId", Concepts.MODULE_SCT_CORE,
			"acceptability", Map.of(
				Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED
			)
		));
		
		LabeledEclExpressions result = bulkLabel(List.of(Concepts.ROOT_CONCEPT, Concepts.IS_A), SnomedConcept.Expand.PREFERRED_TERM, List.of(ExtendedLocale.valueOf("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US)));
		assertThat(result).containsSequence(
			Concepts.ROOT_CONCEPT + " |" + rootPtUs + "|",
			Concepts.IS_A + " |" + isaPtUs + "|"
		);
	}
	
	@Test
	public void wrongExpression() throws Exception {
		Throwable exception = Assertions.catchThrowable(() -> bulkLabel(List.of("A", "B", Concepts.ROOT_CONCEPT), SnomedConcept.Expand.PREFERRED_TERM, List.of(ExtendedLocale.valueOf("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US))));

		Assertions.assertThat(exception)
			.isExactlyInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("additionalInfo", Map.of(
				"erroneousExpressions", Map.of(
					"A", List.of("SCTID length must be between 6-18 characters. Got: A"),
					"B", List.of("SCTID length must be between 6-18 characters. Got: B")
				)				
			));
	}
	
	@Test
	public void ecl_label_api() throws Exception {
		String conceptId = createNewConcept(branchPath);

		String shortName = "SNOMEDCT-Test";
		createCodeSystem(branchPath, shortName).statusCode(201);
		
		List<String> expressionLabels = givenAuthenticatedRequest("/")
				.contentType(ContentType.JSON)
				.body(Map.of(
					"expressions", List.of(conceptId),
					"descriptionType", "fsn",
					"codeSystemUri", shortName
				))
				.post("/label-expressions")
				.then()
				.extract()
				.jsonPath()
				.getList("items");
		
		SnomedConcept concept = getConcept(conceptId, "fsn()");
		
		String validExpression = conceptId + " |" + concept.getFsn().getTerm() + "|";
		
		Assertions.assertThat(expressionLabels).containsOnly(validExpression);
	}

}
