/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * @since 6.20.0
 */
final class DeleteIssuesRequest implements Request<ServiceProvider, Boolean> {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	private final Set<String> issueIds;
	
	DeleteIssuesRequest(Set<String> issueIds) {
		this.issueIds = issueIds;
	}
	
	@Override
	public Boolean execute(ServiceProvider context) {
		return context.service(ValidationRepository.class).write(writer -> {
			final Set<String> affectedRuleIds = ValidationRequests.issues().prepareSearch()
					.all()
					.filterByIds(issueIds)
					.build()
					.execute(context)
					.stream()
					.map(ValidationIssue::getRuleId)
					.collect(Collectors.toSet());
			
			final ImmutableMap<String, ValidationIssue> issuesUnderAffectedRules =  Maps.uniqueIndex(ValidationRequests.issues().prepareSearch()
					.all()
					.filterByRules(affectedRuleIds)
					.build()
					.execute(context), ValidationIssue::getId);
			
			SetView<String> issuesToKeep = Sets.difference(issuesUnderAffectedRules.keySet(), issueIds);
			Set<String> rulesToKeep = Sets.newHashSet();
			issuesToKeep.forEach(issueId -> {
				rulesToKeep.add(issuesUnderAffectedRules.get(issueId).getRuleId());
			});
			SetView<String> rulesToDelete = Sets.difference(affectedRuleIds, rulesToKeep);
			
			writer.removeAll(ImmutableMap.<Class<?>, Set<String>>of(
					ValidationRule.class, rulesToDelete,
					ValidationIssue.class, issueIds));
			
			writer.commit();
			return Boolean.TRUE;
		});
	}

}
