/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.commons.CompareUtils;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.internal.validation.ValidationThreadPool;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.issue.ValidationIssueDetailExtensionProvider;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.core.validation.rule.ValidationRule.Severity;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteList;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclParser;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclSerializer;
import com.b2international.snowowl.snomed.core.ecl.EclParser;
import com.b2international.snowowl.snomed.core.ecl.EclSerializer;
import com.b2international.snowowl.snomed.core.ecl.TestBranchContext;
import com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.snomed.validation.detail.SnomedValidationIssueDetailExtension;
import com.b2international.snowowl.snomed.validation.detail.SnomedValidationIssueDetailExtension.SnomedIssueDetailFilterFields;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;

/**
 * @since 6.4
 */
public class SnomedValidationIssueDetailTest extends BaseRevisionIndexTest {

	private BranchContext context;
	private SnomedQueryValidationRuleEvaluator evaluator;
	private ValidationRepository repository;
	
	private static final String TEST_RULE_ID = "testRuleId";
	
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
		final Index index = Indexes.createIndex(UUID.randomUUID().toString(), getMapper(), new Mappings(ValidationRule.class, ValidationIssue.class, ValidationWhiteList.class));
		repository = new ValidationRepository(index);
		final Injector injector = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
		context = TestBranchContext.on(MAIN)
				.with(ObjectMapper.class, getMapper())
				.with(EclParser.class, new DefaultEclParser(injector.getInstance(IParser.class), injector.getInstance(IResourceValidator.class)))
				.with(EclSerializer.class, new DefaultEclSerializer(injector.getInstance(ISerializer.class)))
				.with(Index.class, rawIndex())
				.with(RevisionIndex.class, index())
				.with(ValidationThreadPool.class, new ValidationThreadPool(1, 1, 1))
				.with(ValidationRepository.class, repository)
				.build();
		evaluator = new SnomedQueryValidationRuleEvaluator();
		if (!ValidationRuleEvaluator.Registry.types().contains(evaluator.type())) {
			ValidationRuleEvaluator.Registry.register(evaluator);
		}
		
		ValidationIssueDetailExtensionProvider extensionProvider = ValidationIssueDetailExtensionProvider.INSTANCE;
		extensionProvider.addExtension(new SnomedValidationIssueDetailExtension());
		
	}
	
	@After
	public void teardown() {
		super.teardown();
		repository.dispose();
	}
	
	@Test
	public void filterByModuleId() {
		final Map<String, Object> details = ImmutableMap.of(SnomedIssueDetailFilterFields.COMPONENT_MODULE_ID, "1010101010101010");

		final ValidationIssue issueWithDetails = createIssue("1122334455",details);
		final ValidationIssue issueWithoutDetails = createIssue("5544332211", Collections.emptyMap());
		
		save(issueWithDetails);
		save(issueWithoutDetails);
		
		ValidationIssues issues = ValidationRequests.issues().prepareSearch()
			.all()
			.filterByDetails(details)
			.buildAsync()
			.getRequest()
			.execute(context);

		assertComponents(issues, issueWithDetails.getAffectedComponent());
	}

	@Test
	public void filterByModuleIds() {
		final Map<String, Object> details = ImmutableMap.of(SnomedIssueDetailFilterFields.COMPONENT_MODULE_ID, newArrayList("1111"));
		final Map<String, Object> details2 = ImmutableMap.of(SnomedIssueDetailFilterFields.COMPONENT_MODULE_ID, "2222");
		
		final ValidationIssue issueWithModuleId = createIssue("111111111", details);
		final ValidationIssue issueWithModuleId2 = createIssue("222222222", details2);
		final ValidationIssue issueWithoutModuleId = createIssue("333333333", Collections.emptyMap());
		
		save(issueWithModuleId);
		save(issueWithModuleId2);
		save(issueWithoutModuleId);
		
		final Map<String, Object> detailsToSearch = ImmutableMap.of(SnomedIssueDetailFilterFields.COMPONENT_MODULE_ID, newArrayList("1111", "2222"));
		
		ValidationIssues issues = ValidationRequests.issues().prepareSearch()
				.all()
				.filterByDetails(detailsToSearch)
				.buildAsync()
				.getRequest()
				.execute(context);
		
		assertComponents(issues, issueWithModuleId.getAffectedComponent(), issueWithModuleId2.getAffectedComponent());
	}

	@Test
	public void filterByAffectedComponentStatus() {
		final Map<String, Object> details = ImmutableMap.of(SnomedIssueDetailFilterFields.COMPONENT_STATUS, true);
		final Map<String, Object> details2 = ImmutableMap.of(SnomedIssueDetailFilterFields.COMPONENT_STATUS, false);
		
		final ValidationIssue issueWithActiveComponent = createIssue("444444444", details);
		final ValidationIssue issueWithInactiveComponent = createIssue("555555555", details2);
		
		save(issueWithActiveComponent);
		save(issueWithInactiveComponent);
		
		ValidationIssues issues = ValidationRequests.issues().prepareSearch()
				.all()
				.filterByDetails(details)
				.buildAsync()
				.getRequest()
				.execute(context);
		
		assertComponents(issues, issueWithActiveComponent.getAffectedComponent());
	}
	
	private ValidationIssue createIssue(String componentId, Map<String, Object> details) {
		final short terminologyShort = 0;
		final ValidationIssue issue = new ValidationIssue(
			UUID.randomUUID().toString(),
			TEST_RULE_ID,
			MAIN,
			ComponentIdentifier.of(terminologyShort, componentId),
			false);
		
		if (!CompareUtils.isEmpty(details)) {
			issue.setDetails(details);
		}
		
		return issue;
	}
	
	private void save(ValidationIssue issue) {
		context.service(ValidationRepository.class).write(index -> {
			index.put(issue.getId(), issue);
			index.commit();
			return null;
		});
	}
	
	private void assertComponents(ValidationIssues issues, ComponentIdentifier... expectedComponents) {
		assertThat(issues).hasSize(expectedComponents.length);
		assertThat(
			issues.stream()
				.map(ValidationIssue::getAffectedComponent)
				.collect(Collectors.toSet())
		).containsOnly(expectedComponents);
	}
	
	@Test
	public void conceptAttributeChange() throws Exception {
		final String conceptId = RandomSnomedIdentiferGenerator.generateConceptId();
		
		indexRevision(MAIN, STORAGE_KEY1, concept(conceptId).effectiveTime(-1).build());
		
		createSnomedQueryRule(
			ImmutableMap.<String, Object>builder()
				.put("componentType", "concept")
				.put("ecl", conceptId)
				.build()
		);
		
		final ValidationIssues firstValidation = validate();
		
		indexRevision(MAIN, STORAGE_KEY1, concept(conceptId).effectiveTime(Long.MAX_VALUE).build());
		
		ValidationIssues afterConceptEffectiveTimeChangeValidation = validate();
		
		assertThat(firstValidation).hasSize(1);
		assertThat(firstValidation.first().get().getDetails().get(SnomedDocument.Fields.EFFECTIVE_TIME))
			.isEqualTo(-1);
		
		assertThat(afterConceptEffectiveTimeChangeValidation).hasSize(1);
		assertThat(afterConceptEffectiveTimeChangeValidation.first().get().getDetails().get(SnomedDocument.Fields.EFFECTIVE_TIME))
			.isEqualTo(Long.MAX_VALUE);
		
	}
	
	private ValidationIssues validate() {
		new RevisionIndexReadRequest<>(ValidationRequests.prepareValidate().build()).execute(context);
		return ValidationRequests.issues().prepareSearch()
			.all()
			.filterByRule(TEST_RULE_ID)
			.build()
			.execute(context);
	}
	
	private void createSnomedQueryRule(final Map<String, Object> ruleQuery) throws JsonProcessingException {
		ValidationRequests.rules().prepareCreate()
			.setId(TEST_RULE_ID)
			.setType(evaluator.type())
			.setMessageTemplate("Error")
			.setSeverity(Severity.ERROR)
			.setImplementation(context.service(ObjectMapper.class).writeValueAsString(ruleQuery))
			.setToolingId(SnomedTerminologyComponentConstants.TERMINOLOGY_ID)
			.build()
			.execute(context);
	}

}
