/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.collections.PrimitiveSets;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
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
import com.b2international.snowowl.snomed.ecl.EclStandaloneSetup;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;

/**
 * @since 6.0
 */
public class SnomedEclValidationRuleTest extends BaseRevisionIndexTest {

	private BranchContext context;
	private SnomedEclValidationRuleEvaluator evaluator;
	private ValidationRepository repository;

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.of(SnomedConceptDocument.class);
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
				.with(EclParser.class, new DefaultEclParser(injector.getInstance(IParser.class), injector.getInstance(IResourceValidator.class)))
				.with(EclSerializer.class, new DefaultEclSerializer(injector.getInstance(ISerializer.class)))
				.with(Index.class, rawIndex())
				.with(RevisionIndex.class, index())
				.with(ValidationRepository.class, repository)
				.build();
		evaluator = new SnomedEclValidationRuleEvaluator();
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
	public void test() throws Exception {
		final String concept1 = RandomSnomedIdentiferGenerator.generateConceptId();
		indexRevision(MAIN, STORAGE_KEY1, concept(concept1).build());
		final String ruleId = ValidationRequests.rules().prepareCreate()
			.setType(evaluator.type())
			.setMessageTemplate("Error")
			.setSeverity(Severity.ERROR)
			.setImplementation("*")
			.setToolingId("toolingId")
			.build()
			.execute(context);

		
		new RevisionIndexReadRequest<>(ValidationRequests.prepareValidate().build()).execute(context);
		
		final ValidationIssues issues = ValidationRequests.issues().prepareSearch()
			.all()
			.filterByRule(ruleId)
			.build()
			.execute(context);

		assertThat(issues.getTotal()).isEqualTo(1);
		assertThat(issues.getItems().get(0).getAffectedComponent()).isEqualTo(ComponentIdentifier.of(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, concept1));
	}
	
	private SnomedConceptDocument.Builder concept(final String id) {
		return SnomedConceptDocument.builder()
				.id(id)
				.iconId(Concepts.ROOT_CONCEPT)
				.active(true)
				.released(true)
				.exhaustive(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.primitive(true)
				.parents(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.ancestors(PrimitiveSets.newLongOpenHashSet())
				.statedParents(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet())
				.referringRefSets(Collections.<String>emptySet())
				.referringMappingRefSets(Collections.<String>emptySet());
	}
	
}
