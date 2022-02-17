/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.Before;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.commons.exceptions.SyntaxException;
import com.b2international.index.Index;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.query.Expression;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.config.IndexConfiguration;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
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
public abstract class BaseSnomedEclEvaluationRequestTest extends BaseRevisionIndexTest {

	private static final Injector INJECTOR = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
	
	protected static final String ROOT_ID = Concepts.ROOT_CONCEPT;
	protected static final String OTHER_ID = Concepts.ABBREVIATION;
	protected static final String HAS_ACTIVE_INGREDIENT = Concepts.HAS_ACTIVE_INGREDIENT;
	protected static final String SUBSTANCE = Concepts.SUBSTANCE;
	
	protected static final String AXIOM = "axiom";
	
	private BranchContext context;
	
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
		SnomedCoreConfiguration config = new SnomedCoreConfiguration();
		config.setConcreteDomainSupported(true);
		
		RepositoryConfiguration repositoryConfig = new RepositoryConfiguration();
		IndexConfiguration indexConfiguration = new IndexConfiguration();
		indexConfiguration.setResultWindow(IndexClientFactory.DEFAULT_RESULT_WINDOW);
		repositoryConfig.setIndexConfiguration(indexConfiguration);
		
		context = TestBranchContext.on(MAIN)
				.with(EclParser.class, new DefaultEclParser(INJECTOR.getInstance(IParser.class), INJECTOR.getInstance(IResourceValidator.class)))
				.with(EclSerializer.class, new DefaultEclSerializer(INJECTOR.getInstance(ISerializer.class)))
				.with(Index.class, rawIndex())
				.with(RevisionIndex.class, index())
				.with(SnomedCoreConfiguration.class, config)
				.with(ObjectMapper.class, getMapper())
				.with(TerminologyResource.class, createCodeSystem(MAIN))
				.with(ResourceURI.class, CodeSystem.uri("SNOMEDCT"))
				.with(RepositoryConfiguration.class, repositoryConfig)
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
			)		
		));
		return codeSystem;
	}

	protected Expression eval(String expression) {
		try {
			return new RevisionIndexReadRequest<>(SnomedRequests.prepareEclEvaluation(expression)
					// use the isInferred method decide on inferred vs stated form (this will provide support for axioms as well)
					.setExpressionForm(isInferred() ? Trees.INFERRED_FORM : Trees.STATED_FORM) 
					.build())
					.execute(context)
					.getSync();
		} catch (SyntaxException e) {
			System.err.println(e.getMessage());
			System.err.println(e.getAdditionalInfo());
			throw e;
		}
	}

	/**
	 * Subclasses may configure the expression form if required, but by default it should always execute on the inferred form
	 * @return
	 */
	protected boolean isInferred() {
		return true;
	}
	
}
