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
package com.b2international.snowowl.snomed.core.ecl;

import java.util.*;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.Index;
import com.b2international.index.query.Expression;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;

/**
 * @since 8.0
 */
@RunWith(Parameterized.class)
public abstract class BaseSnomedEclEvaluationRequestTest extends BaseRevisionIndexTest {

	private static final Injector INJECTOR = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
	
	protected static final String ROOT_ID = Concepts.ROOT_CONCEPT;
	protected static final String OTHER_ID = Concepts.ABBREVIATION;
	protected static final String HAS_ACTIVE_INGREDIENT = Concepts.HAS_ACTIVE_INGREDIENT;
	protected static final String SUBSTANCE = Concepts.SUBSTANCE;
	
	protected static final String AXIOM = "axiom";
	
	private BranchContext context;
	
	private final String expressionForm;
	private final boolean statementsWithValue;
	
	public BaseSnomedEclEvaluationRequestTest(String expressionForm, boolean statementsWithValue) {
		this.expressionForm = expressionForm;
		this.statementsWithValue = statementsWithValue;
	}
	
	@Parameters(name = "{0} {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			// Test CD members in all three forms
			{ Trees.INFERRED_FORM, false },
			{ Trees.STATED_FORM,   false },
			{ AXIOM,               false }, // special test parameter to indicate stated form on axiom members
			
			// New statements with value are expected to 
			// appear in axiom and inferred form only
			{ Trees.INFERRED_FORM, true  },
			{ AXIOM,               true  }, 
		});
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return Set.of(SnomedConceptDocument.class, SnomedDescriptionIndexEntry.class, SnomedRelationshipIndexEntry.class, SnomedRefSetMemberIndexEntry.class);
	}

	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	@Before
	public void setup() {		
		context = TestBranchContext.on(MAIN)
				.with(EclParser.class, new DefaultEclParser(INJECTOR.getInstance(IParser.class), INJECTOR.getInstance(IResourceValidator.class)))
				.with(EclSerializer.class, new DefaultEclSerializer(INJECTOR.getInstance(ISerializer.class)))
				.with(Index.class, rawIndex())
				.with(RevisionIndex.class, index())
				.with(ObjectMapper.class, getMapper())
				.with(TerminologyResource.class, createCodeSystem(MAIN))
				.build();
	}
	
	private CodeSystem createCodeSystem(String main) {
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setId("SNOMEDCT");
		codeSystem.setBranchPath(main);
		codeSystem.setSettings(Map.of(
			SnomedTerminologyComponentConstants.CODESYSTEM_LANGUAGE_CONFIG_KEY, List.of(
				Map.of(
					"languageTag", "en",
					"languageRefSetIds", List.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US)
				),
				Map.of(
					"languageTag", "en-us",
					"languageRefSetIds", List.of(Concepts.REFSET_LANGUAGE_TYPE_US)
				),
				Map.of(
					"languageTag", "en-gb",
					"languageRefSetIds", List.of(Concepts.REFSET_LANGUAGE_TYPE_UK)
				)
			),
			SnomedCoreConfiguration.CONCRETE_DOMAIN_SUPPORT, true
		));
		return codeSystem;
	}

	protected final Expression eval(String expression) {
		return new RevisionIndexReadRequest<>(SnomedRequests.prepareEclEvaluation(expression)
			// use the isInferred method decide on inferred vs stated form (this will provide support for axioms as well)
			.setExpressionForm(isInferred() ? Trees.INFERRED_FORM : Trees.STATED_FORM) 
			.build())
			.execute(context)
			.getSync();
	}
	
	protected final boolean isAxiom() {
		return AXIOM.equals(expressionForm);
	}

	protected final boolean isInferred() {
		return Trees.INFERRED_FORM.equals(expressionForm);
	}
	
	protected final String getCharacteristicType() {
		return isInferred() ? Concepts.INFERRED_RELATIONSHIP : Concepts.STATED_RELATIONSHIP;
	}
	
	protected final boolean isStatementsWithValue() {
		return statementsWithValue;
	}
	
}
