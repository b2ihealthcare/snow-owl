/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.core.tests.util.DocumentBuilders.concept;
import static com.b2international.snowowl.snomed.core.tests.util.DocumentBuilders.description;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.core.validation.rule.ValidationRule.Severity;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclParser;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclSerializer;
import com.b2international.snowowl.snomed.core.ecl.EclParser;
import com.b2international.snowowl.snomed.core.ecl.EclSerializer;
import com.b2international.snowowl.snomed.core.ecl.TestBranchContext;
import com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.ecl.EclStandaloneSetup;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @since 6.0
 */
public class SnomedQueryValidationRuleEvaluatorTest extends BaseRevisionIndexTest {

	private BranchContext context;
	private SnomedQueryValidationRuleEvaluator evaluator;
	private ValidationRepository repository;

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.of(SnomedConceptDocument.class, SnomedDescriptionIndexEntry.class);
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	@Before
	public void setup() {
		super.setup();
		final Index index = Indexes.createIndex(UUID.randomUUID().toString(), getMapper(), new Mappings(ValidationRule.class, ValidationIssue.class));
		repository = new ValidationRepository(index);
		final Injector injector = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
		context = TestBranchContext.on(MAIN)
				.with(ObjectMapper.class, getMapper())
				.with(EclParser.class, new DefaultEclParser(injector.getProvider(IParser.class), injector.getProvider(IResourceValidator.class)))
				.with(EclSerializer.class, new DefaultEclSerializer(injector.getProvider(ISerializer.class)))
				.with(Index.class, rawIndex())
				.with(RevisionIndex.class, index())
				.with(ValidationRepository.class, repository)
				.build();
		evaluator = new SnomedQueryValidationRuleEvaluator();
		if (!ValidationRuleEvaluator.Registry.types().contains(evaluator.type())) {
			ValidationRuleEvaluator.Registry.register(evaluator);
		}
	}
	
	@After
	public void teardown() {
		super.teardown();
		repository.dispose();
	}
	
	@Test
	public void conceptRuleEclSingleConcept() throws Exception {
		final String concept1 = RandomSnomedIdentiferGenerator.generateConceptId();
		final String concept2 = RandomSnomedIdentiferGenerator.generateConceptId();
		indexRevision(MAIN, STORAGE_KEY1, concept(concept1).build());
		indexRevision(MAIN, STORAGE_KEY2, concept(concept2).build());
		
		final Map<String, Object> ruleQuery = ImmutableMap.<String, Object>builder()
				.put("componentType", "concept")
				.put("ecl", concept1) 
				.build();
		
		final String ruleId = createSnomedQueryRule(ruleQuery);
		
		final ValidationIssues issues = validate(ruleId);

		assertThat(issues.getTotal()).isEqualTo(1);
		assertThat(issues.getItems().get(0).getAffectedComponent()).isEqualTo(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, concept1));
	}

	@Test
	public void conceptRuleActiveAndModuleFilter() throws Exception {
		final String concept1 = RandomSnomedIdentiferGenerator.generateConceptId();
		final String concept2 = RandomSnomedIdentiferGenerator.generateConceptId();
		final String concept3 = RandomSnomedIdentiferGenerator.generateConceptId();
		indexRevision(MAIN, STORAGE_KEY1, concept(concept1).moduleId(Concepts.MODULE_B2I_EXTENSION).build());
		indexRevision(MAIN, STORAGE_KEY2, concept(concept2).active(false).moduleId(Concepts.MODULE_B2I_EXTENSION).build());
		indexRevision(MAIN, nextStorageKey(), concept(concept3).active(false).moduleId(Concepts.MODULE_SCT_CORE).build());
		
		final Map<String, Object> ruleQuery = ImmutableMap.<String, Object>builder()
				.put("componentType", "concept")
				.put("active", true)
				.put("module", Concepts.MODULE_B2I_EXTENSION)
				.build();
		
		final String ruleId = createSnomedQueryRule(ruleQuery);
		final ValidationIssues issues = validate(ruleId);

		assertThat(issues.getTotal()).isEqualTo(1);
		assertThat(issues.getItems().get(0).getAffectedComponent()).isEqualTo(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, concept1));
		
	}
	
	@Test
	public void descriptionRuleRegex() throws Exception {
		final String description1 = RandomSnomedIdentiferGenerator.generateDescriptionId();
		final String description2 = RandomSnomedIdentiferGenerator.generateDescriptionId();
		
		indexRevision(MAIN, STORAGE_KEY1, description(description1, Concepts.SYNONYM, "Minor heart attack").build());
		indexRevision(MAIN, STORAGE_KEY2, description(description2, Concepts.SYNONYM, "Clinical finding").build());
		
		final Map<String, Object> ruleQuery = ImmutableMap.<String, Object>builder()
				.put("componentType", "description")
				.put("term", "regex(.*heart.*)")
				.build();
		
		final String ruleId = createSnomedQueryRule(ruleQuery);
		final ValidationIssues issues = validate(ruleId);
		
		assertThat(issues.getTotal()).isEqualTo(1);
		assertThat(issues.getItems().get(0).getAffectedComponent()).isEqualTo(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, description1));
	}
	
	@Test
	public void descriptionRuleEmptySemanticTag() throws Exception {
		final String description1 = RandomSnomedIdentiferGenerator.generateDescriptionId();
		final String description2 = RandomSnomedIdentiferGenerator.generateDescriptionId();
		
		indexRevision(MAIN, STORAGE_KEY1, description(description1, Concepts.SYNONYM, "Minor heart attack").build());
		indexRevision(MAIN, STORAGE_KEY2, description(description2, Concepts.SYNONYM, "Clinical finding (finding)").build());
		
		final Map<String, Object> ruleQuery = ImmutableMap.<String, Object>builder()
				.put("componentType", "description")
				.put("semanticTag", "")
				.build();
		
		final String ruleId = createSnomedQueryRule(ruleQuery);
		final ValidationIssues issues = validate(ruleId);
		
		assertThat(issues.getTotal()).isEqualTo(1);
		assertThat(issues.getItems().get(0).getAffectedComponent()).isEqualTo(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, description1));
	}
	
	@Test
	public void descriptionAcceptableInAndPreferredIn() throws Exception {
		final String description1 = RandomSnomedIdentiferGenerator.generateDescriptionId();
		final String description2 = RandomSnomedIdentiferGenerator.generateDescriptionId();
		final String langRefSet1 = RandomSnomedIdentiferGenerator.generateConceptId();
		final String langRefSet2 = RandomSnomedIdentiferGenerator.generateConceptId();
		
		indexRevision(MAIN, STORAGE_KEY1, description(description1, Concepts.SYNONYM, "Minor heart attack")
				.acceptableIn(ImmutableSet.of(langRefSet1))
				.preferredIn(ImmutableSet.of(langRefSet2))
				.build());
		indexRevision(MAIN, STORAGE_KEY2, description(description2, Concepts.SYNONYM, "Clinical finding (finding)")
				.acceptableIn(ImmutableSet.of(langRefSet1))
				.build());
		
		final Map<String, Object> ruleQuery = ImmutableMap.<String, Object>builder()
				.put("componentType", "description")
				.put("acceptableIn", ImmutableList.of(langRefSet1))
				.put("preferredIn", ImmutableList.of(langRefSet2))
				.build();
		
		final String ruleId = createSnomedQueryRule(ruleQuery);
		final ValidationIssues issues = validate(ruleId);
		
		assertThat(issues.getTotal()).isEqualTo(1);
		assertThat(issues.getItems().get(0).getAffectedComponent()).isEqualTo(ComponentIdentifier.of(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, description1));
	}
	
	private ValidationIssues validate(final String ruleId) {
		new RevisionIndexReadRequest<>(ValidationRequests.prepareValidate().build()).execute(context);
		return ValidationRequests.issues().prepareSearch()
			.all()
			.filterByRule(ruleId)
			.build()
			.execute(context);
	}

	private String createSnomedQueryRule(final Map<String, Object> ruleQuery) throws JsonProcessingException {
		return ValidationRequests.rules().prepareCreate()
			.setType(evaluator.type())
			.setMessageTemplate("Error")
			.setSeverity(Severity.ERROR)
			.setImplementation(context.service(ObjectMapper.class).writeValueAsString(ruleQuery))
			.setToolingId("toolingId")
			.build()
			.execute(context);
	}
	
}
