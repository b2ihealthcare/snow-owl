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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.index.Writer;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.internal.validation.ValidationThreadPool;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.issue.ValidationIssueDetailExtension;
import com.b2international.snowowl.core.validation.issue.ValidationIssueDetailExtensionProvider;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.core.validation.rule.ValidationRuleSearchRequestBuilder;
import com.b2international.snowowl.core.validation.rule.ValidationRules;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteListSearchRequestBuilder;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;

/**
 * @since 6.0
 */
final class ValidateRequest implements Request<BranchContext, ValidationResult> {
	
	private static final Logger LOG = LoggerFactory.getLogger("validation");
	
	Collection<String> ruleIds;

	private Map<String, Object> ruleParameters = Maps.newHashMap();
	
	ValidateRequest() {}
	
	@Override
	public ValidationResult execute(BranchContext context) {
		return context.service(ValidationRepository.class).write(writer -> doValidate(context, writer));
	}
	
	private ValidationResult doValidate(BranchContext context, Writer index) throws IOException {
		final String branchPath = context.branchPath();

		ValidationRuleSearchRequestBuilder req = ValidationRequests.rules().prepareSearch();

		if (!CompareUtils.isEmpty(ruleIds)) {
			req.filterByIds(ruleIds);
		}
		
		final ValidationRules rules = req
				.all()
				.build()
				.execute(context);
		
		final ValidationThreadPool pool = context.service(ValidationThreadPool.class);
		final BlockingQueue<IssuesToPersist> issuesToPersistQueue = Queues.newLinkedBlockingDeque();
		final List<Promise<Object>> validationPromises = Lists.newArrayList();
		// evaluate selected rules
		for (ValidationRule rule : rules) {
			checkArgument(rule.getCheckType() != null, "CheckType is missing for rule " + rule.getId());
			final ValidationRuleEvaluator evaluator = ValidationRuleEvaluator.Registry.get(rule.getType());
			if (evaluator != null) {
				validationPromises.add(pool.submit(rule.getCheckType(), () -> {
					Stopwatch w = Stopwatch.createStarted();
					
					try {
						LOG.info("Executing rule '{}'...", rule.getId());
						final List<ComponentIdentifier> componentIdentifiers = evaluator.eval(context, rule, ruleParameters);
						issuesToPersistQueue.offer(new IssuesToPersist(rule.getId(), componentIdentifiers));
						LOG.info("Execution of rule '{}' successfully completed in '{}'.", rule.getId(), w);
						// TODO report successfully executed validation rule
					} catch (Exception e) {
						// TODO report failed validation rule
						LOG.info("Execution of rule '{}' failed after '{}'.", rule.getId(), w, e);
					}
				}));
			}
		}
		
		final Set<String> ruleIds = rules.stream().map(ValidationRule::getId).collect(Collectors.toSet());
		final Multimap<String, ComponentIdentifier> whiteListedEntries = fetchWhiteListEntries(context, ruleIds);
		
		final Promise<List<Object>> promise = Promise.all(validationPromises);
		
		while (!promise.isDone() || !issuesToPersistQueue.isEmpty()) {
			if (!issuesToPersistQueue.isEmpty()) {
				final Collection<IssuesToPersist> issuesToPersist = newArrayList(); 
				issuesToPersistQueue.drainTo(issuesToPersist);
				if (!issuesToPersist.isEmpty()) {
					final List<String> rulesToPersist = issuesToPersist.stream().map(itp -> itp.ruleId).collect(Collectors.toList());
					LOG.info("Persisting issues generated by rules '{}'...", rulesToPersist);
					// persist new issues generated by rules so far, extending them using the Issue Extension API
					int persistedIssues = 0;
					
					final Multimap<String, ValidationIssue> issuesToExtendWithDetailsByToolingId = HashMultimap.create();
					for (IssuesToPersist ruleIssues : Iterables.consumingIterable(issuesToPersist)) {
						final String ruleId = ruleIssues.ruleId;
						final List<ValidationIssue> existingRuleIssues = ValidationRequests.issues().prepareSearch()
								.all()
								.filterByBranchPath(branchPath)
								.filterByRule(ruleId)
								.build()
								.execute(context)
								.getItems();
						
						
						final Map<ComponentIdentifier, ValidationIssue> existingIsssuesByComponentIdentifier = existingRuleIssues.stream().collect(Collectors.toMap(ValidationIssue::getAffectedComponent, Function.identity()));
						
						// remove all processed whitelist entries 
						final Collection<ComponentIdentifier> ruleWhiteListEntries = whiteListedEntries.removeAll(ruleId);
						final String toolingId = rules.stream().filter(rule -> ruleId.equals(rule.getId())).findFirst().get().getToolingId();
						for (ComponentIdentifier componentIdentifier : ruleIssues.affectedComponentIds) {
							
							if (!existingIsssuesByComponentIdentifier.containsKey(componentIdentifier)) {
								final ValidationIssue validationIssue = new ValidationIssue(
										UUID.randomUUID().toString(),
										ruleId,
										branchPath,
										componentIdentifier,
										ruleWhiteListEntries.contains(componentIdentifier));
								
								issuesToExtendWithDetailsByToolingId.put(toolingId, validationIssue);
								persistedIssues++; 
							} else {
								final ValidationIssue issueToCopy = existingIsssuesByComponentIdentifier.get(componentIdentifier);
								
								final ValidationIssue validationIssue = new ValidationIssue(
									issueToCopy.getId(),
									issueToCopy.getRuleId(),
									issueToCopy.getBranchPath(),
									issueToCopy.getAffectedComponent(),
									ruleWhiteListEntries.contains(issueToCopy.getAffectedComponent()));
								validationIssue.setDetails(Maps.newHashMap());
								
								issuesToExtendWithDetailsByToolingId.put(toolingId, validationIssue);
								persistedIssues++; 
								existingIsssuesByComponentIdentifier.remove(componentIdentifier);
							}
						}
						
						final Set<String> issueIdsToDelete = existingRuleIssues
								.stream()
								.filter(issue -> existingIsssuesByComponentIdentifier.containsKey(issue.getAffectedComponent()))
								.map(ValidationIssue::getId)
								.collect(Collectors.toSet());
						
						if (!issueIdsToDelete.isEmpty()) {
							index.removeAll(Collections.singletonMap(ValidationIssue.class, issueIdsToDelete));
						}
						
					}
					
					for (String toolingId : issuesToExtendWithDetailsByToolingId.keySet()) {
						final ValidationIssueDetailExtension extensions = ValidationIssueDetailExtensionProvider.INSTANCE.getExtensions(toolingId);
						final Collection<ValidationIssue> issues = issuesToExtendWithDetailsByToolingId.removeAll(toolingId);
						extensions.extendIssues(context, issues);
						for (ValidationIssue issue : issues) {
							index.put(issue.getId(), issue);
						}
					}
					index.commit();
					LOG.info("Persisted '{}' issues generated by rules '{}'.", persistedIssues, rulesToPersist);
				}
			}
			
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				throw new SnowowlRuntimeException(e);
			}
		}
		
		// TODO return ValidationResult object with status and new issue IDs as set
		return new ValidationResult(context.id(), context.branchPath());
	}

	private Multimap<String, ComponentIdentifier> fetchWhiteListEntries(BranchContext context, final Set<String> ruleIds) {
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
		
		return whiteListedEntries;
	}
	
	void setRuleIds(Collection<String> ruleIds) {
		this.ruleIds = ruleIds;
	}
	
	void setRuleParameters(Map<String, Object> ruleParameters) {
		this.ruleParameters = ruleParameters;
	}
	
	private static final class IssuesToPersist {
		
		public final String ruleId;
		public final Collection<ComponentIdentifier> affectedComponentIds;

		public IssuesToPersist(String ruleId, Collection<ComponentIdentifier> affectedComponentIds) {
			this.ruleId = ruleId;
			this.affectedComponentIds = affectedComponentIds;
		}
		
	}
	
}
