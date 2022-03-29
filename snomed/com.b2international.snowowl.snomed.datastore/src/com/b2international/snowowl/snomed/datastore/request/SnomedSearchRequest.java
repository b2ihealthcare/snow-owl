/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Set;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.request.ecl.AbstractComponentSearchRequest;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.core.ecl.EclExpression;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;

/**
 * Abstract class for SNOMED CT search requests.
 * @since 4.5
 * @param <R> - the return type of this request
 * @param <D> - the document type to search for
 */
public abstract class SnomedSearchRequest<R, D extends SnomedDocument> 
		extends AbstractComponentSearchRequest<BranchContext, R, D>
		implements AccessControl {

	private static final long serialVersionUID = 1L;

	protected enum OptionKey {
		
		/**
		 * Component's released flag to match
		 */
		RELEASED,
		/**
		 * Component status to match
		 */
		ACTIVE,
		
		/**
		 * Component module ID to match
		 */
		MODULE,
		
		/**
		 * Filter components by effective time starting from this value, inclusive.
		 */
		EFFECTIVE_TIME_START,
		
		/**
		 * Filter components by effective time ending with this value, inclusive.
		 */
		EFFECTIVE_TIME_END,
		
		/**
		 * Use this expression form for all ECL evaluations, by default it is set to inferred.
		 */
		ECL_EXPRESSION_FORM
		
	}
	
	protected SnomedSearchRequest() {}
	
	protected final void addReleasedClause(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.RELEASED)) {
			queryBuilder.filter(SnomedDocument.Expressions.released(getBoolean(OptionKey.RELEASED)));
		}
	}
	
	protected final void addActiveClause(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.ACTIVE)) {
			queryBuilder.filter(SnomedDocument.Expressions.active(getBoolean(OptionKey.ACTIVE)));
		}
	}
	
	protected final void addEffectiveTimeClause(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.EFFECTIVE_TIME_START) || containsKey(OptionKey.EFFECTIVE_TIME_END)) {
			final long from = containsKey(OptionKey.EFFECTIVE_TIME_START) ? get(OptionKey.EFFECTIVE_TIME_START, Long.class) : 0;
			final long to = containsKey(OptionKey.EFFECTIVE_TIME_END) ? get(OptionKey.EFFECTIVE_TIME_END, Long.class) : Long.MAX_VALUE;
			queryBuilder.filter(SnomedDocument.Expressions.effectiveTime(from, to));
		}
	}
	
	@Override
	protected final boolean isComponentIdentifier(String eclExpression) {
		return SnomedIdentifiers.isConceptIdentifier(eclExpression);
	}
	
	@Override
	protected Promise<Set<String>> resolve(BranchContext context, String expression) {
		return EclExpression.of(expression, eclExpressionForm()).resolve(context);
	}
	
	@Override
	protected Promise<Expression> resolveToExpression(BranchContext context, String eclExpression) {
		return EclExpression.of(eclExpression, eclExpressionForm()).resolveToExpression(context);
	}
	
	protected final String eclExpressionForm() {
		return containsKey(OptionKey.ECL_EXPRESSION_FORM) ? getString(OptionKey.ECL_EXPRESSION_FORM) : Trees.INFERRED_FORM;
	}
	
	@Override
	public final String getOperation() {
		return Permission.OPERATION_BROWSE;
	}
	
}
