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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.core.validation.rule.ValidationRules;

/**
 * @since 6.0
 */
final class ValidateRequest implements Request<BranchContext, Boolean> {
	
	ValidateRequest() {}
	
	@Override
	public Boolean execute(BranchContext context) {
		return context.service(ValidationRepository.class).write(index -> {
			final String branchPath = context.branchPath();
			final ValidationRules rules = ValidationRequests.rules().prepareSearch()
					.all() // TODO support filtering rules
					.build()
					.execute(context);
			
			// clear all previously reported issues on this branch for each rule
			final Set<String> ruleIds = rules.stream().map(ValidationRule::getId).collect(Collectors.toSet());
			final Set<String> issuesToDelete = ValidationRequests.issues().prepareSearch()
					.all()
					.filterByBranchPath(branchPath)
					.filterByRules(ruleIds)
					.setFields(ValidationIssue.Fields.ID)
					.build()
					.execute(context)
					.stream()
					.map(ValidationIssue::getId)
					.collect(Collectors.toSet());
			
			index.removeAll(Collections.singletonMap(ValidationIssue.class, issuesToDelete));
			
			// evaluate selected rules
			for (ValidationRule rule : rules) {
				ValidationRuleEvaluator evaluator = ValidationRuleEvaluator.Registry.get(rule.getType());
				if (evaluator != null) {
					try {
						List<ComponentIdentifier> affectedComponents = evaluator.eval(context, rule);
						if (!affectedComponents.isEmpty()) {
							for (ComponentIdentifier affectedComponent : affectedComponents) {
								String issueId = UUID.randomUUID().toString();
								index.put(issueId, new ValidationIssue(issueId, rule.getId(), branchPath, affectedComponent));
							}
						}
						// TODO report successfully executed validation rule
					} catch (Exception e) {
						// TODO report failed validation rule
						e.printStackTrace();
					}
				}
			}
			
			index.commit();
			
			// TODO return ValidationResult object with status and new issue IDs as set
			return Boolean.TRUE;
		});
		
	}

}
