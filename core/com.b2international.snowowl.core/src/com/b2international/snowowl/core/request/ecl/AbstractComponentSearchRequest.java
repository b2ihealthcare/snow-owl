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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.commons.exceptions.SyntaxException;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snomed.ecl.Ecl;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * Abstract base request class that provides capabilities to use SNOMED CT Expression Constraint Language in any terminology tooling's search request implementation.
 * 
 * @since 8.2.0
 */
public abstract class AbstractComponentSearchRequest<C extends ServiceProvider, B, D>
		extends SearchIndexResourceRequest<C, B, D> {

	private static final long serialVersionUID = 1L;

	/**
	 * @since 8.2.0
	 */
	public enum OptionKey {
		/**
		 * ECL expression to match
		 */
		ECL,
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	protected void prepareQuery(C context, ExpressionBuilder queryBuilder) {
		addEclFilter(context, queryBuilder);
	}

	protected final void addEclFilter(C context, ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.ECL)) {
			final String eclExpressionValue = getString(OptionKey.ECL);
			Expression eclExpression = resolveToExpression(context, eclExpressionValue).getSync(3, TimeUnit.MINUTES);
			if (eclExpression.isMatchNone()) {
				throw new NoResultException();
			} else if (!eclExpression.isMatchAll()) {
				queryBuilder.filter(eclExpression);
			}
		}
	}

	/**
	 * Append an ECL aware clause to the given queryBuilder based on the values present under the eclCapableOptionKey.
	 * 
	 * @param context - the context to use to evaluate ECL expressions
	 * @param queryBuilder - the queryBuilder to append the clause to
	 * @param eclCapableOptionKey - the option key that can be ECL aware option values
	 * @param matchingIdsToExpression - a Function that converts matching ID set to an appropriate clause expression
	 */
	protected final void addEclFilter(final C context, final ExpressionBuilder queryBuilder, final Enum<?> eclCapableOptionKey, final Function<Collection<String>, Expression> matchingIdsToExpression) {
		if (containsKey(eclCapableOptionKey)) {
			// trim all input values before using them
			final Collection<String> optionValues = getCollection(eclCapableOptionKey, String.class);
			addEclFilter(context, queryBuilder, optionValues, matchingIdsToExpression);
		}
	}

	/**
	 * Append an ECL aware clause to the given queryBuilder based on the given optionValues.
	 * 
	 * @param context - the context to use to evaluate ECL expressions
	 * @param queryBuilder - the queryBuilder to append the clause to
	 * @param optionValues - a set of optionValues where an ECL expression will be converted to a set of IDs  
	 * @param matchingIdsToExpression - a Function that converts matching ID set to an appropriate clause expression
	 */
	protected final void addEclFilter(final C context, final ExpressionBuilder queryBuilder, final Collection<String> optionValues, final Function<Collection<String>, Expression> matchingIdsToExpression) {
		Collection<String> eclFilter = evaluateEclFilter(context, optionValues);
		if (eclFilter != null) {
			queryBuilder.filter(matchingIdsToExpression.apply(eclFilter));
		}
	}

	protected final Collection<String> evaluateEclFilter(final C context, final Collection<String> optionValues) {
		if (optionValues.isEmpty()) {
			return Collections.emptySet();
		}
		Collection<String> idFilter = FluentIterable.from(optionValues).transform(String::trim).toSet();
		if (idFilter.size() == 1) {
			// if only a single item is available in the idFilter
			final String expression = Iterables.getOnlyElement(idFilter);
			if (!isComponentIdentifier(expression)) {
				// and it's not a component ID, then evaluate it

				// unless it is an Any ECL expression, which allows any value
				if (Ecl.ANY.equals(expression)) {
					return null;
				}
				
				// TODO replace sync call to concept search with async promise
				try {
					idFilter = resolve(context, expression).getSync(3, TimeUnit.MINUTES);
				} catch (SyntaxException e) {
					throw new SearchResourceRequest.NoResultException();
				}
				
				if (idFilter.isEmpty()) {
					throw new SearchResourceRequest.NoResultException();
				}
			}
		}
		return idFilter;
	}

	/**
	 * Resolves the given eclExpression to a set of matching component identifiers.
	 *  
	 * @param context
	 * @param eclExpression
	 * @return
	 */
	protected abstract Promise<Set<String>> resolve(C context, String eclExpression);
	
	/**
	 * Resolves the given eclExpression to an expression that can be appended to a query to restrict the results to only the components that match the ecl expression.
	 * 
	 * @param context
	 * @param eclExpression
	 * @return
	 */
	protected abstract Promise<Expression> resolveToExpression(C context, String eclExpression);

	/**
	 * Returns <code>true</code> when the given eclExpression denotes a single
	 * component identifier in the current tooling. This method should only return
	 * <code>true</code> if and only if it can decide <code>true</code> value
	 * without using any other sources of information, meaning it should detect that
	 * the given string value is a component identifier based on its characters. If
	 * this cannot be determined safely, or any string can be used to denote a
	 * component ID then this method should always return <code>false</code>. By
	 * default it returns <code>false</code>.
	 * 
	 * @param eclExpression - the value to check whether it is a component ID or not
	 * @return
	 */
	protected boolean isComponentIdentifier(String eclExpression) {
		return false;
	}
	
}
