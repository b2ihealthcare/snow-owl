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

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Fields.REFERRING_REFSETS;
import static com.b2international.snowowl.datastore.index.RevisionDocument.Expressions.id;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.referringRefSet;

import java.io.StringReader;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.ecl.ecl.Any;
import com.b2international.snowowl.snomed.ecl.ecl.ConceptReference;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.ecl.MemberOf;
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
		return throwUnsupported(eObject);
	}

	protected Promise<Expression> eval(ExpressionConstraint expression) {
		return evaluate(expression.getExpression());
	}
	
	// handlers for FocusConcept subtypes
	protected Promise<Expression> eval(Any any) {
		return Promise.immediate(Expressions.matchAll());
	}
	
	protected Promise<Expression> eval(ConceptReference concept) {
		return Promise.immediate(id(concept.getId()));
	}
	
	protected Promise<Expression> eval(MemberOf memberOf) {
		if (memberOf.getConcept() instanceof ConceptReference) {
			final ConceptReference concept = (ConceptReference) memberOf.getConcept();
			return Promise.immediate(referringRefSet(concept.getId()));
		} else if (memberOf.getConcept() instanceof Any) {
			return Promise.immediate(Expressions.exists(REFERRING_REFSETS));
		} else {
			return throwUnsupported(memberOf.getConcept());
		}
	}
	
	private Promise<Expression> throwUnsupported(EObject eObject) {
		throw new UnsupportedOperationException("Unhandled ECL grammar feature: " + eObject.eClass().getName());
	}
	
}
