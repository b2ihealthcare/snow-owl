/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.issue;

import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.common.Strings;

import com.b2international.index.BulkDelete;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.validation.ValidationDeleteNotification;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.20.0
 */
final class ValidationIssueDeleteRequest implements Request<ServiceProvider, Boolean> {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	private final String codeSystemURI;

	@JsonProperty
	private String toolingId;
	
	ValidationIssueDeleteRequest(String codeSystemURI, String toolingId) {
		this.codeSystemURI = codeSystemURI;
		this.toolingId = toolingId;
	}
	
	@Override
	public Boolean execute(ServiceProvider context) {
		ExpressionBuilder query = Expressions.builder();
		
		if (!Strings.isNullOrEmpty(codeSystemURI)) {
			query.filter(Expressions.exactMatch(ValidationIssue.Fields.CODESYSTEM_URI, codeSystemURI));
		}
		
		if (!Strings.isNullOrEmpty(toolingId)) {
			final Set<String> rulesToDelete = ValidationRequests.rules().prepareSearch()
				.all()
				.filterByTooling(toolingId)
				.build()
				.execute(context)
				.stream()
				.map(ValidationRule::getId)
				.collect(Collectors.toSet());
			query.filter(Expressions.matchAny(ValidationIssue.Fields.RULE_ID, rulesToDelete));
		}
		
		return context.service(ValidationRepository.class).write(writer -> {
			
			writer.bulkDelete(new BulkDelete<>(ValidationIssue.class, query.build()));
			writer.commit();
			
			new ValidationDeleteNotification(codeSystemURI, toolingId).publish(context.service(IEventBus.class));
			
			return Boolean.TRUE;
		});
	}

}
