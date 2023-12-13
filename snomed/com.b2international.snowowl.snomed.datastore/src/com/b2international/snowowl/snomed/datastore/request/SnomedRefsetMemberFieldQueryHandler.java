/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Collection;
import java.util.function.Function;

import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.google.common.base.Preconditions;

/**
 * @since 8.1.0
 */
public class SnomedRefsetMemberFieldQueryHandler<T> {

	@FunctionalInterface
	public static interface EclEvaluator {
		
		Collection<String> eval(Collection<String> expressions);
		
	}
	
	private final Class<T> fieldType;
	private final Function<Collection<T>, Expression> expressionConverter;
	private final boolean eclCapable;

	public SnomedRefsetMemberFieldQueryHandler(Class<T> fieldType, Function<Collection<T>, Expression> expressionConverter, boolean eclCapable) {
		this.fieldType = fieldType;
		this.expressionConverter = expressionConverter;
		this.eclCapable = eclCapable;
		Preconditions.checkArgument(String.class.isAssignableFrom(fieldType) || !eclCapable, "A non-String field type cannot support ECL evaluation.");
	}
	
	public void prepareQuery(ExpressionBuilder queryBuilder, SearchResourceRequest.Operator op, Collection<T> values, EclEvaluator eclEvaluator) {
		
		final Expression expression;
		if (String.class.isAssignableFrom(fieldType) && eclCapable) {
			expression = expressionConverter.apply((Collection<T>) eclEvaluator.eval((Collection<String>) values));
		} else {
			expression = expressionConverter.apply(values);
		}
		
		// TODO support range operators on non-String field, like integers
		switch (op) {
		case EQUALS:
			queryBuilder.filter(expression);
			break;
		case NOT_EQUALS:
			queryBuilder.mustNot(expression);
			break;
		default: throw new NotImplementedException();
		}
	}

	public Class<T> getFieldType() {
		return fieldType;
	}
	
}
