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
package com.b2international.snowowl.core.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.core.validation.rule.ValidationRule.Severity;
import com.b2international.snowowl.core.validation.rule.ValidationRules;
import com.b2international.snowowl.datastore.server.internal.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 6.0
 */
public class ValidationRuleApiTest {

	private ServiceProvider context;

	@Before
	public void setup() {
		final ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
		final Index index = Indexes.createIndex(UUID.randomUUID().toString(), mapper, new Mappings(ValidationRule.class));
		index.admin().create();
		final ValidationRepository repository = new ValidationRepository(index);
		context = ServiceProvider.EMPTY.inject()
				.bind(ValidationRepository.class, repository)
				.build();
	}
	
	@After
	public void after() {
		context.service(ValidationRepository.class).admin().delete();
		if (context instanceof IDisposableService) {
			((IDisposableService) context).dispose();
		}
	}

	@Test
	public void getAllValidationRules() throws Exception {
		final ValidationRules rules = ValidationRequests.rules().prepareSearch()
			.all()
			.buildAsync().getRequest()
			.execute(context);
		assertThat(rules).isEmpty();
	}
	
	@Test(expected = NotFoundException.class)
	public void throwNotFoundIfRuleDoesNotExist() throws Exception {
		ValidationRequests.rules().prepareGet("invalid")
			.buildAsync().getRequest()
			.execute(context);
	}
	
	@Test
	public void createRule() throws Exception {
		final String ruleId = ValidationRequests.rules().prepareCreate()
				.setId(UUID.randomUUID().toString())
				.setToolingId("TerminologyToolingId")
				.setMessageTemplate("Error message")
				.setSeverity(Severity.ERROR)
				.setType("snomed-query")
				.setImplementation("*")
				.buildAsync().getRequest()
				.execute(context);
		
		final ValidationRule rule = ValidationRequests.rules().prepareGet(ruleId).buildAsync().getRequest().execute(context);
		assertThat(rule.getToolingId()).isEqualTo("TerminologyToolingId");
		assertThat(rule.getMessageTemplate()).isEqualTo("Error message");
		assertThat(rule.getSeverity()).isEqualTo(Severity.ERROR);
		assertThat(rule.getType()).isEqualTo("snomed-query");
		assertThat(rule.getImplementation()).isEqualTo("*");
	}
	
}
