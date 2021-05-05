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
package com.b2international.snowowl.test.commons.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.junit.Before;
import org.junit.Rule;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.Index;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.config.IndexConfiguration;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.internal.validation.ValidationThreadPool;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.b2international.snowowl.core.request.RevisionIndexReadRequest;
import com.b2international.snowowl.core.scripts.ScriptEngine;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.b2international.snowowl.core.validation.ValidateRequestBuilder;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.issue.ValidationIssueDetailExtensionProvider;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteList;
import com.b2international.snowowl.test.commons.TestMethodNameRule;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext.Builder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 7.12
 */
public abstract class BaseValidationTest extends BaseRevisionIndexTest {

	@Rule
	public final TestMethodNameRule nameRule = new TestMethodNameRule();
	
	private final String rulesJsonFile;
	
	private BranchContext context;

	public BaseValidationTest() {
		this("");
	}
	
	public BaseValidationTest(String rulesJsonFile) {
		this.rulesJsonFile = rulesJsonFile;
	}
	
	@Before
	public final void setup() {
		final ClassPathScanner scanner = new ClassPathScanner("com.b2international");
		Builder context = TestBranchContext.on(MAIN)
				.with(ClassLoader.class, getClass().getClassLoader())
				.with(ClassPathScanner.class, scanner)
				.with(Index.class, rawIndex())
				.with(RevisionIndex.class, index()).with(ObjectMapper.class, getMapper())
				.with(ValidationRepository.class, new ValidationRepository(rawIndex()))
				.with(ValidationThreadPool.class, new ValidationThreadPool(1, 1, 1))
				.with(ValidationIssueDetailExtensionProvider.class, new ValidationIssueDetailExtensionProvider(scanner))
				.with(TerminologyRegistry.class, TerminologyRegistry.INSTANCE)
				.with(ResourceURIPathResolver.class, ResourceURIPathResolver.fromMap(getTestCodeSystemPathMap()))
				.with(ScriptEngine.Registry.class, new ScriptEngine.Registry(scanner));
		configureContext(context);
		this.context = context.build();
		initializeData();
	}
	
	protected void initializeData() {
	}

	protected final BranchContext context() {
		return context;
	}
	
	protected void configureContext(Builder context) {
	}

	protected abstract Map<String, String> getTestCodeSystemPathMap();
	
	protected final void assertAffectedComponents(ValidationIssues issues, ComponentIdentifier... expectedComponentIdentifiers) {
		assertThat(issues).hasSize(expectedComponentIdentifiers.length);
		assertThat(issues.stream().map(ValidationIssue::getAffectedComponent).collect(Collectors.toSet())).containsOnly(expectedComponentIdentifiers);
	}
	
	protected final void assertAffectedComponents(ValidationIssues issues, Iterable<ComponentIdentifier> expectedAffectedComponentIdentifiers) {
		assertThat(issues).hasSize(Iterables.size(expectedAffectedComponentIdentifiers));
		assertThat(issues.stream().map(ValidationIssue::getAffectedComponent).collect(Collectors.toSet())).containsOnlyElementsOf(expectedAffectedComponentIdentifiers);
	}

	protected final ValidationIssues validate(String ruleId) {
		final ValidateRequestBuilder req = ValidationRequests.prepareValidate();
		configureValidationRequest(req);
		new RevisionIndexReadRequest<>(req.build()).execute(context);
		return ValidationRequests.issues().prepareSearch().all().filterByRule(ruleId).build().execute(context);
	}
	
	protected void configureValidationRequest(ValidateRequestBuilder req) {
	}

	/* Looks up the rule in the validation-rules.json file and indexes it */
	protected final void indexRule(String ruleId) throws Exception {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(rulesJsonFile), "validation-rules.json file path must be specified to use this method");
		URL rulesJson = getClass().getClassLoader().getResource(rulesJsonFile);
		try (InputStream in = rulesJson.openStream()) {
			MappingIterator<ValidationRule> it = context.service(ObjectMapper.class).readerFor(ValidationRule.class).readValues(in);
			while (it.hasNext()) {
				final ValidationRule rule = it.next();
				if (ruleId.equals(rule.getId())) {
					indexDocument(rule);
					return;
				}
			}
		}
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new PrimitiveCollectionModule());
	}

	@Override
	protected final Map<String, Object> getIndexSettings() {
		return Map.<String, Object>of(
			IndexClientFactory.RESULT_WINDOW_KEY, ""+IndexConfiguration.DEFAULT_RESULT_WINDOW
		);
	}
	
	@Override
	protected final Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>builder()
				.add(ValidationRule.class, ValidationIssue.class, ValidationWhiteList.class)
				.addAll(getAdditionalTypes())
				.build();
	}

	/**
	 * Subclasses may override to provide additional types on top of the must have validation types.
	 * @return
	 */
	protected Collection<Class<?>> getAdditionalTypes() {
		return Collections.emptyList();
	}
	
}
