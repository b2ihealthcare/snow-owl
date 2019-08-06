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
package com.b2international.snowowl.snomed.core.ql;

import static com.google.common.collect.Maps.newHashMap;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.SyntaxException;
import com.b2international.snowowl.snomed.ql.ql.Query;

/**
 * @since 6.12
 */
public class DefaultSnomedQueryParser implements SnomedQueryParser {
	
	private final IParser qlParser;
	private final IResourceValidator validator;
	
	public DefaultSnomedQueryParser(IParser qlParser, IResourceValidator validator) {
		this.qlParser = qlParser;
		this.validator = validator;
	}

	@Override
	public Query parse(String expression) {
		if (expression == null) {
			throw new BadRequestException("Expression cannot be null.");
		} else if (StringUtils.isEmpty(expression)) {
			return null;
		} else {
			try (final StringReader reader = new StringReader(expression)) {
				final IParseResult parseResult = qlParser.parse(reader);
				if (parseResult.hasSyntaxErrors()) {
					final Map<Pair<Integer, Integer>, String> errors = newHashMap();
					for (INode node : parseResult.getSyntaxErrors()) {
						final SyntaxErrorMessage syntaxError = node.getSyntaxErrorMessage();
						errors.put(Pair.of(node.getTotalStartLine(), node.getTotalOffset()), syntaxError.getMessage());
					}
					throw new SyntaxException("QL", errors);
				} else {
					final Query query = (Query) parseResult.getRootASTElement();
					final Resource resource = new ResourceImpl();
					resource.getContents().add(query);
					final List<Issue> issues = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
					if (!issues.isEmpty()) {
						final Map<Pair<Integer, Integer>, String> errors = newHashMap();
						for (Issue issue : issues) {
							if (issue.getSeverity() == Severity.ERROR) {
								errors.put(Pair.of(issue.getLineNumber(), issue.getOffset()), issue.getMessage());
							}
						}
						if (!errors.isEmpty()) {
							throw new SyntaxException("QL", errors);
						}
					}
					return query;
				}
			}
		}
	}

}
