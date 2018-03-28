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
package com.b2international.snowowl.core.validation.eval;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.scripting.api.ScriptEngine;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.validation.issue.IssueDetail;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.1
 */
public final class GroovyScriptValidationRuleEvaluator implements ValidationRuleEvaluator {

	private final Path validationResourcesDirectory;

	public GroovyScriptValidationRuleEvaluator(Path validationResourcesDirectory) {
		this.validationResourcesDirectory = validationResourcesDirectory;
	}
	
	@Override
	public List<IssueDetail> eval(BranchContext context, ValidationRule rule) throws Exception {
		final String script = Files
			.lines(validationResourcesDirectory.resolve(rule.getImplementation()))
			.collect(Collectors.joining(System.getProperty("line.separator")));
		
		return ScriptEngine.run("groovy", context.service(ClassLoader.class), script, ImmutableMap.<String, Object>of("ctx", context));
	}

	@Override
	public String type() {
		return "script-groovy";
	}

}
