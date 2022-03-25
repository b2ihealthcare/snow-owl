/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.ecl;

import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.index.query.Expression;
import com.b2international.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.ecl.EclParser;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

/**
 * @since 8.2.0
 */
public class EclEvaluationRequest<C extends ServiceProvider> implements Request<C, Promise<Expression>> {

	private static final long serialVersionUID = 1L;

	private final PolymorphicDispatcher<Promise<Expression>> dispatcher = PolymorphicDispatcher.createForSingleTarget("eval", 2, 2, this);
	
	@Nullable
	@JsonProperty
	private String expression;
	
	@Nullable
	private Set<String> ignoredSyntaxErrorCodes;
	
	@Override
	public final Promise<Expression> execute(C context) {
		final ExpressionConstraint expressionConstraint = context.service(EclParser.class).parse(expression, ignoredSyntaxErrorCodes);
		return doEval(context, expressionConstraint);
	}

	public final Promise<Expression> doEval(C context, final ExpressionConstraint expressionConstraint) {
		final ExpressionConstraint rewritten = rewrite(context, expressionConstraint);
		return evaluate(context, rewritten);
	}
	
	/**
	 * Subclasses may optionally override this method to rewrite the parsed ECL query before evaluation.
	 *  
	 * @param context
	 * @param expressionConstraint
	 * @return
	 */
	protected ExpressionConstraint rewrite(C context, ExpressionConstraint expressionConstraint) {
		return expressionConstraint;
	}
	
	public final void setExpression(String expression) {
		this.expression = expression;
	}
	
	public final void setIgnoredSyntaxErrorCodes(Iterable<String> ignoredSyntaxErrorCodes) {
		this.ignoredSyntaxErrorCodes = ignoredSyntaxErrorCodes == null ? null : ImmutableSet.copyOf(ignoredSyntaxErrorCodes);
	}
	
	protected final Promise<Expression> evaluate(C context, EObject expression) {
		return dispatcher.invoke(context, expression);
	}

	protected Promise<Expression> eval(C context, EObject eObject) {
		return throwUnsupported(eObject);
	}
	
	public static <T> Promise<T> throwUnsupported(EObject eObject) {
		throw new NotImplementedException("Not implemented ECL feature: %s", eObject.eClass().getName());
	}

}
