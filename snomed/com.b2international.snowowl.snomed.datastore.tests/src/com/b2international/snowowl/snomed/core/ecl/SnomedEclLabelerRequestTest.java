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
package com.b2international.snowowl.snomed.core.ecl;

import static com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator.generateDescriptionId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.Before;
import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.index.Index;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.snomed.DocumentBuilders;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.inject.Injector;

/**
 * @since 7.6
 */
public class SnomedEclLabelerRequestTest extends BaseRevisionIndexTest {

	private static final Injector ECL_INJECTOR = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
	
	private static final ExtendedLocale US_LOCALE = new ExtendedLocale("en", "", Concepts.REFSET_LANGUAGE_TYPE_US);
	private static final ExtendedLocale GB_LOCALE = new ExtendedLocale("en", "", Concepts.REFSET_LANGUAGE_TYPE_UK);
	private static final ExtendedLocale SG_LOCALE = new ExtendedLocale("en", "", Concepts.REFSET_LANGUAGE_TYPE_SG);
	
	private static ListMultimap<String, String> languageMap;
	
	private BranchContext context;

	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.singleton(SnomedConceptDocument.class);
	}

	@Before
	public void setup() {
		final IParser parser = ECL_INJECTOR.getInstance(IParser.class);
		final IResourceValidator resourceValidator = ECL_INJECTOR.getInstance(IResourceValidator.class);
		final ISerializer serializer = ECL_INJECTOR.getInstance(ISerializer.class);
		
		languageMap = ArrayListMultimap.create();
		
		languageMap.put(US_LOCALE.getLanguageTag(), Concepts.REFSET_LANGUAGE_TYPE_US);
		languageMap.put(GB_LOCALE.getLanguageTag(), Concepts.REFSET_LANGUAGE_TYPE_UK);
		languageMap.put(SG_LOCALE.getLanguageTag(), Concepts.REFSET_LANGUAGE_TYPE_SG);
		
		final CodeSystem cs = new CodeSystem();
		cs.setBranchPath(MAIN);
		cs.setId(SnomedContentRule.SNOMEDCT_ID);
		cs.setSettings(Map.of(SnomedTerminologyComponentConstants.CODESYSTEM_LANGUAGE_CONFIG_KEY, languageMap));
		
		context = TestBranchContext.on(MAIN)
			.with(EclParser.class, new DefaultEclParser(parser, resourceValidator))
			.with(EclSerializer.class, new DefaultEclSerializer(serializer))
			.with(Index.class, rawIndex()).with(RevisionIndex.class, index())
			.with(TerminologyResource.class, cs).build();
	}

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
		return new RevisionIndexReadRequest<>(SnomedRequests.prepareEclLabeler(expressions)
				.setLocales(locales)
				.setDescriptionType(descriptionType)
				.build())
				.execute(context);
	}

	@Test
	public void emptyExpression() throws Exception {
		label("");
	}

	@Test
	public void conceptReferenceNoLabelFound() throws Exception {
		String result = label(Concepts.ROOT_CONCEPT);
		assertEquals(Concepts.ROOT_CONCEPT, result);
	}

	@Test
	public void conceptReferenceMissingConcept() throws Exception {
		// no indexed concept document can be found for the given ID, labeler just returns the ID
		String result = label(Concepts.ROOT_CONCEPT);
		assertEquals(Concepts.ROOT_CONCEPT, result);
	}

	@Test
	public void conceptReferenceNoDescriptions() throws Exception {
		// no indexed preferredDescriptions can be found for the given ID, labeler just returns the ID
		indexRevision(MAIN, DocumentBuilders.concept(Concepts.ROOT_CONCEPT).build());
		String result = label(Concepts.ROOT_CONCEPT);
		assertEquals(Concepts.ROOT_CONCEPT, result);
	}

	@Test
	public void conceptReferenceDefaultLabel() throws Exception {
		// there is a single FSN indexed for the concept and no locales specified in the request, returns the first FSN in the preferredDescriptions
		// list
		String fsn = "SNOMED CT Concept (SNOMED RT+CTV3)";
		indexRevision(MAIN,
				DocumentBuilders.concept(Concepts.ROOT_CONCEPT).preferredDescriptions(List.of(
						new SnomedDescriptionFragment(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, fsn, Concepts.REFSET_LANGUAGE_TYPE_UK)))
						.build());
		String result = label(Concepts.ROOT_CONCEPT);
		assertEquals(Concepts.ROOT_CONCEPT + " |" + fsn + "|", result);
	}
	
	@Test
	public void conceptReferenceDefaultLabelWithLocale() throws Exception {
		String fsnUk = "SNOMED CT Concept (uk)";
		String fsnUs = "SNOMED CT Concept (us)";
		indexRevision(MAIN,
				DocumentBuilders.concept(Concepts.ROOT_CONCEPT).preferredDescriptions(List.of(
						new SnomedDescriptionFragment(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, fsnUk, Concepts.REFSET_LANGUAGE_TYPE_UK),
						new SnomedDescriptionFragment(generateDescriptionId(), Concepts.FULLY_SPECIFIED_NAME, fsnUs, Concepts.REFSET_LANGUAGE_TYPE_US)))
						.build());
		String result = label(Concepts.ROOT_CONCEPT, List.of(ExtendedLocale.valueOf("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US)));
		assertEquals(Concepts.ROOT_CONCEPT + " |" + fsnUs + "|", result);
	}
	
	@Test
	public void conceptReferencePtWithLocale() throws Exception {
		String ptUk = "SNOMED CT Concept UK";
		String ptUs = "SNOMED CT Concept US";
		indexRevision(MAIN,
				DocumentBuilders.concept(Concepts.ROOT_CONCEPT).preferredDescriptions(List.of(
						new SnomedDescriptionFragment(generateDescriptionId(), Concepts.SYNONYM, ptUk, Concepts.REFSET_LANGUAGE_TYPE_UK),
						new SnomedDescriptionFragment(generateDescriptionId(), Concepts.SYNONYM, ptUs, Concepts.REFSET_LANGUAGE_TYPE_US)))
						.build());
		String result = label(Concepts.ROOT_CONCEPT, SnomedConcept.Expand.PREFERRED_TERM, List.of(ExtendedLocale.valueOf("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US)));
		assertEquals(Concepts.ROOT_CONCEPT + " |" + ptUs + "|", result);
	}
	
	@Test
	public void bulkLabelExpressions() throws Exception {
		String rootPtUk = "SNOMED CT Concept UK";
		String rootPtUs = "SNOMED CT Concept US";
		String isaPtUk = "Is a UK";
		String isaPtUs = "Is a US";
		indexRevision(MAIN,
				DocumentBuilders.concept(Concepts.ROOT_CONCEPT).preferredDescriptions(List.of(
						new SnomedDescriptionFragment(generateDescriptionId(), Concepts.SYNONYM, rootPtUk, Concepts.REFSET_LANGUAGE_TYPE_UK),
						new SnomedDescriptionFragment(generateDescriptionId(), Concepts.SYNONYM, rootPtUs, Concepts.REFSET_LANGUAGE_TYPE_US)))
						.build());
		indexRevision(MAIN,
				DocumentBuilders.concept(Concepts.IS_A).preferredDescriptions(List.of(
						new SnomedDescriptionFragment(generateDescriptionId(), Concepts.SYNONYM, isaPtUk, Concepts.REFSET_LANGUAGE_TYPE_UK),
						new SnomedDescriptionFragment(generateDescriptionId(), Concepts.SYNONYM, isaPtUs, Concepts.REFSET_LANGUAGE_TYPE_US)))
						.build());
		
		LabeledEclExpressions result = bulkLabel(List.of(Concepts.ROOT_CONCEPT, Concepts.IS_A), SnomedConcept.Expand.PREFERRED_TERM, List.of(ExtendedLocale.valueOf("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US)));
		assertThat(result).containsSequence(
			Concepts.ROOT_CONCEPT + " |" + rootPtUs + "|",
			Concepts.IS_A + " |" + isaPtUs + "|"
		);
	}
	
	@Test
	public void wrongExpression() throws Exception {
		String ptUk = "SNOMED CT Concept UK";
		indexRevision(MAIN,
				DocumentBuilders.concept(Concepts.ROOT_CONCEPT).preferredDescriptions(List.of(
						new SnomedDescriptionFragment(generateDescriptionId(), Concepts.SYNONYM, ptUk, Concepts.REFSET_LANGUAGE_TYPE_UK)))
						.build());

		Throwable exception = Assertions.catchThrowable(() -> bulkLabel(List.of("A", "B", Concepts.ROOT_CONCEPT), SnomedConcept.Expand.PREFERRED_TERM, List.of(ExtendedLocale.valueOf("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US))));

		Assertions.assertThat(exception)
			.isExactlyInstanceOf(BadRequestException.class)
			.hasFieldOrPropertyWithValue("additionalInfo", Map.of(
				"erroneousExpressions", Map.of(
					"A", List.of("Invalid character 'A' at [1:1]"),
					"B", List.of("Invalid character 'B' at [1:1]")
				)				
			));
	}

}
