/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ecl;

import java.io.StringReader;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * @since 5.4
 */
public class DefaultEclEvaluator implements EclEvaluator {

	private final PolymorphicDispatcher<Promise<Expression>> dispatcher = PolymorphicDispatcher.createForSingleTarget("eval", this);
	private final IParser eclParser;

	public DefaultEclEvaluator(IParser eclParser) {
		this.eclParser = eclParser;
	}
	
	@Override
	public Promise<Expression> evaluate(String expression) {
		if (Strings.isNullOrEmpty(expression)) {
			return Promise.fail(new BadRequestException("Expression should be specified"));
		} else {
			try (final StringReader reader = new StringReader(expression)) {
				final IParseResult parseResult = eclParser.parse(reader);
				if (parseResult.hasSyntaxErrors()) {
					final String message = Joiner.on("\n").join(Iterables.transform(parseResult.getSyntaxErrors(), (node) -> node.getSyntaxErrorMessage().getMessage()));
					return Promise.fail(new BadRequestException(message));
				} else {
					// TODO validate
					return evaluate(parseResult.getRootASTElement());
				}
			}
		}
	}
	
	private Promise<Expression> evaluate(EObject expression) {
		return dispatcher.invoke(expression);
	}

	protected Promise<Expression> eval(EObject eObject) {
		throw new UnsupportedOperationException("Unhandled ECL grammar feature: " + eObject);
	}
	
	protected Promise<Expression> eval(com.b2international.snowowl.snomed.ecl.ecl.Expression expression) {
		return evaluate(expression.getExpression());
	}
	
	protected Promise<Expression> eval(com.b2international.snowowl.snomed.ecl.ecl.ConceptReference concept) {
		return Promise.immediate(RevisionDocument.Expressions.id(concept.getId()));
	}
	
}
