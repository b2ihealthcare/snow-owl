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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.options.Options;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.4
 */
public class SnomedValidationIssueDetailTest {

	private static final String TEST_BRANCH = "testBranch";
	private static final String TEST_RULE_ID = "testRuleId";
	private static final short TEST_TERMINOLOGY_SHORT = 0;
	
	private ServiceProvider context;
	
	@Before
	public void setup() {
		final ObjectMapper mapper = new ObjectMapper();
		final Index index = Indexes.createIndex(UUID.randomUUID().toString(), mapper, new Mappings(ValidationIssue.class));
		index.admin().create();
		final ValidationRepository repository = new ValidationRepository(index);
		context = ServiceProvider.EMPTY.inject()
				.bind(ValidationRepository.class, repository)
				.build();
	}
	
	@After
	public void tearDown() {
		context.service(ValidationRepository.class).admin().delete();
		if(context instanceof IDisposableService) {
			((IDisposableService) context).dispose();
		}
	}
	
	@Test
	public void filterByModuleId() {
		final Map<String, Object> details = ImmutableMap.of(SnomedRf2Headers.FIELD_MODULE_ID, "1010101010101010");

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
		final Map<String, Object> details = ImmutableMap.of(SnomedRf2Headers.FIELD_MODULE_ID, newArrayList("1111"));
		final Map<String, Object> details2 = ImmutableMap.of(SnomedRf2Headers.FIELD_MODULE_ID, "2222");
		
		final ValidationIssue issueWithModuleId = createIssue("111111111", details);
		final ValidationIssue issueWithModuleId2 = createIssue("222222222", details2);
		final ValidationIssue issueWithoutModuleId = createIssue("333333333", Collections.emptyMap());
		
		save(issueWithModuleId);
		save(issueWithModuleId2);
		save(issueWithoutModuleId);
		
		final Map<String, Object> detailsToSearch = ImmutableMap.of(SnomedRf2Headers.FIELD_MODULE_ID, newArrayList("1111", "2222"));
		
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
		final Map<String, Object> details = ImmutableMap.of(SnomedRf2Headers.FIELD_ACTIVE, true);
		final Map<String, Object> details2 = ImmutableMap.of(SnomedRf2Headers.FIELD_ACTIVE, false);
		
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
		final ValidationIssue issue = new ValidationIssue(
			UUID.randomUUID().toString(),
			TEST_RULE_ID,
			TEST_BRANCH,
			ComponentIdentifier.of(TEST_TERMINOLOGY_SHORT, componentId),
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
	
}