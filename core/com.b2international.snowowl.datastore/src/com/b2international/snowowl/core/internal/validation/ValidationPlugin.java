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
package com.b2international.snowowl.core.internal.validation;

import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.extension.Component;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.eval.GroovyScriptValidationRuleEvaluator;
import com.b2international.snowowl.core.validation.eval.ValidationRuleEvaluator;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteList;
import com.b2international.snowowl.datastore.config.IndexSettings;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 6.0
 */
@Component
public final class ValidationPlugin extends Plugin {

	private static final Logger LOG = LoggerFactory.getLogger("validation");
	
	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
		if (env.isEmbedded() || env.isServer()) {
			final ObjectMapper mapper = env.service(ObjectMapper.class);
			final Index validationIndex = Indexes.createIndex(
				"validations", 
				mapper, 
				new Mappings(ValidationIssue.class, ValidationRule.class, ValidationWhiteList.class), 
				env.service(IndexSettings.class)
			);
			
			final ValidationRepository repository = new ValidationRepository(validationIndex);
			env.services().registerService(ValidationRepository.class, repository);
			
			// register always available validation rule evaluators
			ValidationRuleEvaluator.Registry.register(new GroovyScriptValidationRuleEvaluator(env.getConfigDirectory().toPath()));
			
			// initialize validation thread pool
			// TODO make this configurable
			int numberOfValidationThreads = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
			env.services().registerService(ValidationThreadPool.class, new ValidationThreadPool(numberOfValidationThreads));

			
			final List<File> listOfFiles = Arrays.asList(env.getConfigDirectory().listFiles());
			final Set<File> validationRuleFiles = Sets.newHashSet();
			final Pattern validationFilenamePattern = Pattern.compile("(validation-rules)-(\\w+).(json)");
			for (File file : listOfFiles) {
				final String fileName = file.getName();
				final Matcher match = validationFilenamePattern.matcher(fileName);
				if (match.matches()) {
					validationRuleFiles.add(file);
				}
			}
			
			final List<ValidationRule> availableRules = Lists.newArrayList();
			for (File validationRulesFile : validationRuleFiles) {
				LOG.info("Synchronizing validation rules from file: " + validationRulesFile);
				availableRules.addAll(mapper.readValue(validationRulesFile, new TypeReference<List<ValidationRule>>() {}));
			}
			
			repository.write(writer -> {
				final Map<String, ValidationRule> existingRules = Maps.uniqueIndex(ValidationRequests.rules().prepareSearch()
						.all()
						.buildAsync()
						.getRequest()
						.execute(env), ValidationRule::getId);
				
				// index all rules from the file, this will update existing rules as well
				final Set<String> ruleIds = newHashSet();
				for (ValidationRule rule : availableRules) {
					writer.put(rule.getId(), rule);
					ruleIds.add(rule.getId());
				}
				
				
				// delete rules and associated issues
				Set<String> rulesToDelete = Sets.difference(existingRules.keySet(), ruleIds);
				if (!rulesToDelete.isEmpty()) {
					final Set<String> issuesToDelete = newHashSet(writer.searcher().search(Query.select(String.class)
							.from(ValidationIssue.class)
							.fields(ValidationIssue.Fields.ID)
							.where(Expressions.builder()
									.filter(Expressions.matchAny(ValidationIssue.Fields.RULE_ID, rulesToDelete))
									.build())
							.limit(Integer.MAX_VALUE)
							.build())
							.getHits());
					writer.removeAll(ImmutableMap.<Class<?>, Set<String>>of(
							ValidationRule.class, rulesToDelete,
							ValidationIssue.class, issuesToDelete
							));
				}
				
				writer.commit();
				return null;
			});
				
		}
	}
	
}
