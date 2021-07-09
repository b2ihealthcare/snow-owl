/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.validation;

import static com.b2international.snowowl.test.commons.snomed.DocumentBuilders.concept;
import static com.b2international.snowowl.test.commons.snomed.DocumentBuilders.description;
import static com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator.generateConceptId;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.Test;

import com.b2international.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.core.validation.rule.ValidationRule.Severity;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclParser;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclSerializer;
import com.b2international.snowowl.snomed.core.ecl.EclParser;
import com.b2international.snowowl.snomed.core.ecl.EclSerializer;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext.Builder;
import com.b2international.snowowl.test.commons.validation.BaseValidationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @since 6.0
 */
public class SnomedQueryValidationRuleEvaluatorTest extends BaseValidationTest {

	private static final Injector INJECTOR = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
	
	private SnomedQueryValidationRuleEvaluator evaluator;
	
	@Override
	protected Collection<Class<?>> getAdditionalTypes() {
		return List.of(
			SnomedConceptDocument.class, 
			SnomedDescriptionIndexEntry.class, 
			SnomedRefSetMemberIndexEntry.class
		);
	}
	
	@Override
	protected void configureContext(Builder context) {
		super.configureContext(context);
		
		final CodeSystem cs = new CodeSystem();
		cs.setBranchPath(MAIN);
		cs.setId(SnomedContentRule.SNOMEDCT_ID);

		context
			.with(TerminologyResource.class, cs)
			.with(EclParser.class, new DefaultEclParser(INJECTOR.getInstance(IParser.class), INJECTOR.getInstance(IResourceValidator.class)))
			.with(EclSerializer.class, new DefaultEclSerializer(INJECTOR.getInstance(ISerializer.class)));
	
		evaluator = new SnomedQueryValidationRuleEvaluator();
		if (!ValidationRuleEvaluator.Registry.types().contains(evaluator.type())) {
			ValidationRuleEvaluator.Registry.register(evaluator);
		}
	}
	
	@Override
	protected Map<String, String> getTestCodeSystemPathMap() {
		return Map.of(SnomedContentRule.SNOMEDCT_ID, MAIN);
	}
	
	@Test
	public void conceptRuleEclSingleConcept() throws Exception {
		final String concept1 = RandomSnomedIdentiferGenerator.generateConceptId();
		final String concept2 = RandomSnomedIdentiferGenerator.generateConceptId();
		indexRevision(MAIN, 
			concept(concept1).build(),
			concept(concept2).build()
		);
		
		final Map<String, Object> ruleQuery = ImmutableMap.<String, Object>builder()
				.put("componentType", "concept")
				.put("ecl", concept1) 
				.build();
		
		final String ruleId = createSnomedQueryRule(ruleQuery);
		
		final ValidationIssues issues = validate(ruleId);

		assertThat(issues.getTotal()).isEqualTo(1);
		assertThat(issues.getItems().get(0).getAffectedComponent()).isEqualTo(ComponentIdentifier.of(SnomedConcept.TYPE, concept1));
	}

	@Test
	public void conceptRuleActiveAndModuleFilter() throws Exception {
		final String concept1 = RandomSnomedIdentiferGenerator.generateConceptId();
		final String concept2 = RandomSnomedIdentiferGenerator.generateConceptId();
		final String concept3 = RandomSnomedIdentiferGenerator.generateConceptId();
		
		indexRevision(MAIN, 
			concept(concept1).moduleId(Concepts.MODULE_B2I_EXTENSION).build(),
			concept(concept2).active(false).moduleId(Concepts.MODULE_B2I_EXTENSION).build(),
			concept(concept3).active(false).moduleId(Concepts.MODULE_SCT_CORE).build()
		);
		
		final Map<String, Object> ruleQuery = ImmutableMap.<String, Object>builder()
				.put("componentType", "concept")
				.put("active", true)
				.put("module", Concepts.MODULE_B2I_EXTENSION)
				.build();
		
		final String ruleId = createSnomedQueryRule(ruleQuery);
		final ValidationIssues issues = validate(ruleId);

		assertThat(issues.getTotal()).isEqualTo(1);
		assertThat(issues.getItems().get(0).getAffectedComponent()).isEqualTo(ComponentIdentifier.of(SnomedConcept.TYPE, concept1));
		
	}
	
	@Test
	public void descriptionRuleRegex() throws Exception {
		final String description1 = RandomSnomedIdentiferGenerator.generateDescriptionId();
		final String description2 = RandomSnomedIdentiferGenerator.generateDescriptionId();
		
		indexRevision(MAIN, 
			description(description1, Concepts.SYNONYM, "Minor heart attack")
				.conceptId(generateConceptId())
				.build(),
			description(description2, Concepts.SYNONYM, "Clinical finding")
				.conceptId(generateConceptId())
				.build()
		);
		
		final Map<String, Object> ruleQuery = ImmutableMap.<String, Object>builder()
				.put("componentType", "description")
				.put("term", "regex(.*heart.*)")
				.build();
		
		final String ruleId = createSnomedQueryRule(ruleQuery);
		final ValidationIssues issues = validate(ruleId);
		
		assertThat(issues.getTotal()).isEqualTo(1);
		assertThat(issues.getItems().get(0).getAffectedComponent()).isEqualTo(ComponentIdentifier.of(SnomedDescription.TYPE, description1));
	}
	
	@Test
	public void descriptionRuleEmptySemanticTag() throws Exception {
		final String description1 = RandomSnomedIdentiferGenerator.generateDescriptionId();
		final String description2 = RandomSnomedIdentiferGenerator.generateDescriptionId();
		
		indexRevision(MAIN, 
			description(description1, Concepts.SYNONYM, "Minor heart attack")
				.conceptId(generateConceptId())
				.build(),
			description(description2, Concepts.SYNONYM, "Clinical finding (finding)")
				.conceptId(generateConceptId())
				.build()
		);
		
		final Map<String, Object> ruleQuery = ImmutableMap.<String, Object>builder()
				.put("componentType", "description")
				.put("semanticTag", "")
				.build();
		
		final String ruleId = createSnomedQueryRule(ruleQuery);
		final ValidationIssues issues = validate(ruleId);
		
		assertThat(issues.getTotal()).isEqualTo(1);
		assertThat(issues.getItems().get(0).getAffectedComponent()).isEqualTo(ComponentIdentifier.of(SnomedDescription.TYPE, description1));
	}
	
	@Test
	public void descriptionAcceptableInAndPreferredIn() throws Exception {
		final String description1 = RandomSnomedIdentiferGenerator.generateDescriptionId();
		final String description2 = RandomSnomedIdentiferGenerator.generateDescriptionId();
		final String langRefSet1 = generateConceptId();
		final String langRefSet2 = RandomSnomedIdentiferGenerator.generateConceptId();
		
		indexRevision(MAIN, 
			description(description1, Concepts.SYNONYM, "Minor heart attack")
				.conceptId(generateConceptId())
				.acceptableIn(ImmutableSet.of(langRefSet1))
				.preferredIn(ImmutableSet.of(langRefSet2))
				.build(),
			description(description2, Concepts.SYNONYM, "Clinical finding (finding)")
				.conceptId(generateConceptId())
				.acceptableIn(ImmutableSet.of(langRefSet1))
				.build()
		);
		
		final Map<String, Object> ruleQuery = ImmutableMap.<String, Object>builder()
				.put("componentType", "description")
				.put("acceptableIn", ImmutableList.of(langRefSet1))
				.put("preferredIn", ImmutableList.of(langRefSet2))
				.build();
		
		final String ruleId = createSnomedQueryRule(ruleQuery);
		final ValidationIssues issues = validate(ruleId);
		
		assertThat(issues.getTotal()).isEqualTo(1);
		assertThat(issues.getItems().get(0).getAffectedComponent()).isEqualTo(ComponentIdentifier.of(SnomedDescription.TYPE, description1));
	}
	
	private String createSnomedQueryRule(final Map<String, Object> ruleQuery) throws JsonProcessingException {
		return ValidationRequests.rules().prepareCreate()
			.setType(evaluator.type())
			.setMessageTemplate("Error")
			.setSeverity(Severity.ERROR)
			.setImplementation(context().service(ObjectMapper.class).writeValueAsString(ruleQuery))
			.setToolingId(SnomedTerminologyComponentConstants.TOOLING_ID)
			.build()
			.execute(context());
	}
	
}
