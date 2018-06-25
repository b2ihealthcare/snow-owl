/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.internal.validation.ValidationThreadPool;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.core.validation.issue.IssueDetail;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.core.validation.rule.ValidationRuleSearchRequestBuilder;
import com.b2international.snowowl.core.validation.rule.ValidationRules;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteListSearchRequestBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 6.0
 */
final class ValidateRequest implements Request<BranchContext, ValidationResult> {
	
	private static final Logger LOG = LoggerFactory.getLogger("validation");
	
	Collection<String> ruleIds;
	
	ValidateRequest() {}
	
	@Override
	public ValidationResult execute(BranchContext context) {
		return context.service(ValidationRepository.class).write(index -> {
			final String branchPath = context.branchPath();
			
			ValidationRuleSearchRequestBuilder req = ValidationRequests.rules().prepareSearch();

			if (!CompareUtils.isEmpty(ruleIds)) {
				req.filterByIds(ruleIds);
			}
			
			final ValidationRules rules = req
					.all()
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
			
			ValidationThreadPool pool = context.service(ValidationThreadPool.class);
			
			final Multimap<String, IssueDetail> newIssuesByRule = HashMultimap.create();
			
			// evaluate selected rules
			for (ValidationRule rule : rules) {
				ValidationRuleEvaluator evaluator = ValidationRuleEvaluator.Registry.get(rule.getType());
				if (evaluator != null) {
					pool.submit(() -> {
						try {
							LOG.info("Executing rule '{}'...", rule.getId());
							List<IssueDetail> issueDetails = evaluator.eval(context, rule);
							newIssuesByRule.putAll(rule.getId(), issueDetails);
							LOG.info("Execution of rule '{}' successfully completed", rule.getId());
							// TODO report successfully executed validation rule
						} catch (Exception e) {
							// TODO report failed validation rule
							LOG.info("Execution of rule '{}' failed", rule.getId(), e);
						}
						return Boolean.TRUE;
					})
					.getSync();
					
				}
			}
			
			// fetch all white list entries to determine whether an issue is whitelisted already or not
			final Multimap<String, ComponentIdentifier> whiteListedEntries = HashMultimap.create();
			ValidationWhiteListSearchRequestBuilder whiteListReq = ValidationRequests.whiteList().prepareSearch();
			
			// fetch whitelist entries associated with the defined rules
			if (!CompareUtils.isEmpty(ruleIds)) {
				whiteListReq.filterByRuleIds(ruleIds);
			}
			
			whiteListReq
				.all()
				.build()
				.execute(context)
				.stream()
				.forEach(whitelist -> whiteListedEntries.put(whitelist.getRuleId(), whitelist.getComponentIdentifier()));
			
			// persist new issues by removing them from the newIssues Multimap to save up memory
			for (String ruleId : newHashSet(newIssuesByRule.keySet())) {
				for (IssueDetail issueDetail : newIssuesByRule.removeAll(ruleId)) {
					String issueId = UUID.randomUUID().toString();
					
					ValidationIssue validationIssue = new ValidationIssue(
						issueId,
						ruleId,
						branchPath,
						issueDetail.getAffectedComponent(),
						whiteListedEntries.get(ruleId).contains(issueDetail.getAffectedComponent()));
				
					validationIssue.setDetails(issueDetail.getDetails());
					
					index.put(issueId, validationIssue);
				}
				// remove all processed whitelist entries 
				whiteListedEntries.removeAll(ruleId);
			}
			
			index.commit();
			
			// TODO return ValidationResult object with status and new issue IDs as set
			return new ValidationResult(context.id(), context.branchPath());
		});
		
	}

	public void setRuleIds(Collection<String> ruleIds) {
		this.ruleIds = ruleIds;
	}
	
}
