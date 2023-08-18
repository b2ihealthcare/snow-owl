/*
 * Copyright 2017-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.Writer;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.config.IndexConfiguration;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.internal.validation.ValidationThreadPool;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.issue.ValidationIssueDetailExtension;
import com.b2international.snowowl.core.validation.issue.ValidationIssueDetailExtensionProvider;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.core.validation.rule.ValidationRuleSearchRequestBuilder;
import com.b2international.snowowl.core.validation.rule.ValidationRules;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteListSearchRequestBuilder;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteLists;
import com.google.common.base.Stopwatch;
import com.google.common.collect.*;

/**
 * @since 6.0
 */
final class ValidateRequest implements Request<BranchContext, ValidationResult>, AccessControl {

	private static final long serialVersionUID = -2254266211853070728L;
	private static final Logger LOG = LoggerFactory.getLogger("validation");
	private static final long POLL_INTERVAL_MAX = 1000L;

	private static final class IssuesToPersist {
	
		public final String ruleId;
		public final Collection<ValidationIssueDetails> issueDetails;
	
		@SuppressWarnings("unchecked")
		public IssuesToPersist(final String ruleId, final Collection<?> evaluationResult) {
			this.ruleId = ruleId;
			if (evaluationResult.iterator().hasNext() && evaluationResult.iterator().next() instanceof ValidationIssueDetails) {
				this.issueDetails = (Collection<ValidationIssueDetails>) evaluationResult;
			} else {
				this.issueDetails = evaluationResult.stream()
						.map(result -> (ComponentIdentifier) result)
						.map(identifier -> new ValidationIssueDetails(identifier))
						.collect(Collectors.toList());
			}
		}
	
	}

	private String resultId;
	private Set<String> ruleIds;
	private Map<String, Object> ruleParameters;

	ValidateRequest() {}

	void setResultId(final String resultId) {
		this.resultId = resultId;
	}

	void setRuleIds(final Collection<String> ruleIds) {
		this.ruleIds = Collections3.toImmutableSortedSet(ruleIds);
	}

	void setRuleParameters(final Map<String, Object> ruleParameters) {
		this.ruleParameters = ruleParameters == null
			? ImmutableMap.of()
			: ImmutableMap.copyOf(ruleParameters);
	}

	@Override
	public ValidationResult execute(final BranchContext context) {
		return context.service(ValidationRepository.class).write(writer -> doValidate(context, writer));
	}

	private ValidationResult doValidate(final BranchContext context, final Writer index) throws IOException {
		final IndexConfiguration indexConfiguration = context.service(RepositoryConfiguration.class).getIndexConfiguration();
		final int pageSize = indexConfiguration.getPageSize();

		final String branchPath = context.path();
		final TerminologyResource resource = context.service(TerminologyResource.class);
		final ResourceURI resourceURI = resource.getResourceURI(branchPath);
		
		final ValidationRuleSearchRequestBuilder req = ValidationRequests.rules()
			.prepareSearch()
			.setLimit(pageSize);
		
		if (!CompareUtils.isEmpty(ruleIds)) {
			req.filterByIds(ruleIds);
		}

		final List<ValidationRule> rules = req.stream(context)
			.flatMap(ValidationRules::stream)
			.toList();

		final Set<String> knownRuleIds = rules.stream()
			.map(ValidationRule::getId)
			.collect(Collectors.toSet());

		// Check whether the selected rules are present in the system and report if one is missing
		if (!CompareUtils.isEmpty(ruleIds)) {
			final Set<String> missingRuleIds = Sets.difference(ruleIds, knownRuleIds);
			
			if (!missingRuleIds.isEmpty()) {
				throw new BadRequestException("The following ruleIds are missing from the system: %s. "
					+ "Remove them from the query parameter list or load them into the system.", missingRuleIds);
			}
		}

		final ValidationThreadPool pool = context.service(ValidationThreadPool.class);
		final BlockingQueue<IssuesToPersist> issuesToPersistQueue = Queues.newLinkedBlockingDeque();
		final List<Promise<Object>> validationPromises = Lists.newArrayList();
		
		// Evaluate selected rules
		for (final ValidationRule rule : rules) {
			checkArgument(rule.getCheckType() != null, "CheckType is missing for rule " + rule.getId());
			
			final ValidationRuleEvaluator evaluator = ValidationRuleEvaluator.Registry.get(rule.getType());
			if (evaluator == null) {
				// No corresponding evaluator exists, skip for now
				continue;
			}
			
			validationPromises.add(pool.submit(rule.getCheckType(), () -> {
				final Stopwatch w = Stopwatch.createStarted();

				try {
					LOG.info("Executing rule '{}'...", rule.getId());
					final List<?> evaluationResponse = evaluator.eval(context, rule, ruleParameters);
					issuesToPersistQueue.offer(new IssuesToPersist(rule.getId(), evaluationResponse));
					LOG.info("Execution of rule '{}' successfully completed in '{}'.", rule.getId(), w);
				} catch (final Exception e) {
					LOG.error("Execution of rule '{}' failed after '{}'.", rule.getId(), w, e);
				}
			}));
		}

		final Multimap<String, ComponentIdentifier> whiteListedEntries = fetchWhiteListEntries(context, ruleIds);
		final Promise<List<Object>> promise = Promise.all(validationPromises);

		while (!promise.isDone() || !issuesToPersistQueue.isEmpty()) {
			if (!issuesToPersistQueue.isEmpty()) {
				final Collection<IssuesToPersist> issuesToPersist = newArrayList(); 
				issuesToPersistQueue.drainTo(issuesToPersist);
				
				if (!issuesToPersist.isEmpty()) {
					final List<String> rulesToPersist = issuesToPersist.stream()
						.map(itp -> itp.ruleId)
						.collect(Collectors.toList());
					
					LOG.info("Persisting issues generated by rules '{}'...", rulesToPersist);
					
					// persist new issues generated by rules so far, extending them using the Issue Extension API
					int persistedIssues = 0;

					final Multimap<String, ValidationIssue> issuesToExtendByToolingId = HashMultimap.create();
					for (final IssuesToPersist newIssues : Iterables.consumingIterable(issuesToPersist)) {
						final String ruleId = newIssues.ruleId;
						final List<ValidationIssue> existingIssues = ValidationRequests.issues()
							.prepareSearch()
							.setLimit(pageSize)
							.filterByResultId(resultId)
							.filterByResourceUri(resourceURI)
							.filterByRule(ruleId)
							.stream(context)
							.flatMap(ValidationIssues::stream)
							.collect(Collectors.toList());

						final Set<String> issueIdsToDelete = Sets.newHashSet();
						final Map<ComponentIdentifier, ValidationIssue> existingIssuesByComponentIdentifier = new HashMap<>();

						for (final ValidationIssue issue : existingIssues) {
							if (existingIssuesByComponentIdentifier.containsKey(issue.getAffectedComponent())) {
								issueIdsToDelete.add(issue.getId());
							} else {
								existingIssuesByComponentIdentifier.put(issue.getAffectedComponent(), issue);
							}
						}

						// Remove whitelist entries as we are going through the Multimap rule-by-rule 
						final Collection<ComponentIdentifier> ruleWhiteListEntries = whiteListedEntries.removeAll(ruleId);
						final String toolingId = rules.stream()
							.filter(rule -> ruleId.equals(rule.getId()))
							.findFirst()
							.get()
							.getToolingId();
						
						for (final ValidationIssueDetails issueDetails : newIssues.issueDetails) {
							final ValidationIssue validationIssue;
							final ComponentIdentifier componentIdentifier = issueDetails.affectedComponentId;
							final ValidationIssue issueToCopy = existingIssuesByComponentIdentifier.remove(componentIdentifier);
							
							if (issueToCopy == null) {
								validationIssue = new ValidationIssue(
									UUID.randomUUID().toString(),
									resultId,
									ruleId,
									ComponentURI.of(resourceURI, componentIdentifier),
									ruleWhiteListEntries.contains(componentIdentifier));
							} else {
								validationIssue = new ValidationIssue(
									issueToCopy.getId(),
									issueToCopy.getResultId(),
									issueToCopy.getRuleId(),
									ComponentURI.of(resourceURI, issueToCopy.getAffectedComponent()),
									ruleWhiteListEntries.contains(issueToCopy.getAffectedComponent()));	
							}
							
							validationIssue.setDetails(ValidationIssueDetails.HIGHLIGHT_DETAILS, issueDetails.stylingDetails);
							issuesToExtendByToolingId.put(toolingId, validationIssue);
							persistedIssues++; 
						}

						existingIssues.stream()
							.filter(issue -> existingIssuesByComponentIdentifier.containsKey(issue.getAffectedComponent()))
							.forEach(issue -> issueIdsToDelete.add(issue.getId()));

						if (!issueIdsToDelete.isEmpty()) {
							index.removeAll(Collections.singletonMap(ValidationIssue.class, issueIdsToDelete));
						}
					}

					for (final String toolingId : issuesToExtendByToolingId.keySet()) {
						final ValidationIssueDetailExtension extensions = context.service(ValidationIssueDetailExtensionProvider.class).getExtensions(toolingId);
						final Collection<ValidationIssue> issues = issuesToExtendByToolingId.removeAll(toolingId);
						extensions.extendIssues(context, issues, ruleParameters);
						for (final ValidationIssue issue : issues) {
							index.put(issue);
						}
					}

					index.commit();
					LOG.info("Persisted '{}' issues generated by rules '{}'.", persistedIssues, rulesToPersist);
				}
			} else {
				try {
					// wait at least number of rules * 50ms for the next responses
					Thread.sleep(Math.min(((long) ruleIds.size()) * 100, POLL_INTERVAL_MAX));
				} catch (final InterruptedException e) {
					throw new SnowowlRuntimeException(e);
				}
			}
		}

		// TODO return ValidationResult object with status and new issue IDs as set
		return new ValidationResult(context.info().id(), context.path());
	}

	private Multimap<String, ComponentIdentifier> fetchWhiteListEntries(final BranchContext context, final Set<String> ruleIds) {
		final IndexConfiguration indexConfiguration = context.service(RepositoryConfiguration.class).getIndexConfiguration();
		final int pageSize = indexConfiguration.getPageSize();
		
		// Fetch all whitelist entries to determine whether an issue is whitelisted already or not
		final ValidationWhiteListSearchRequestBuilder whiteListReq = ValidationRequests.whiteList()
			.prepareSearch()
			.setLimit(pageSize);

		// Fetch whitelist entries associated with the defined rules
		if (!CompareUtils.isEmpty(ruleIds)) {
			whiteListReq.filterByRuleIds(ruleIds);
		}

		final Multimap<String, ComponentIdentifier> whiteListedEntries = HashMultimap.create();
		
		whiteListReq.stream(context)
			.flatMap(ValidationWhiteLists::stream)
			.forEach(whitelist -> whiteListedEntries.put(whitelist.getRuleId(), whitelist.getComponentIdentifier()));

		return whiteListedEntries;
	}

	@Override
	public String getOperation() {
		return Permission.OPERATION_EDIT;
	}
}
