/*
 * Copyright 2022-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.request.expand;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import net.jodah.typetools.TypeResolver;

/**
 * @since 8.1
 * @param <R> - the resource's class type who's instances need to be expanded with additional fields
 */
public interface ResourceExpander<R> {

	// The "default default" limit to use when no limit is given
	int DEFAULT_LIMIT = 50;
	
	String LIMIT_OPTION_KEY = "limit";
	String FIELD_OPTION_KEY = "field";
	String SORT_OPTION_KEY = "sort";
	String EXPAND_OPTION_KEY = "expand";
	
	/**
	 * Expands resources with additional fields.
	 * 
	 * @param results - the list of results to expand
	 */
	void expand(List<R> results);
	
	/**
	 * @return the class of the target type this expander can expand
	 */
	@SuppressWarnings("unchecked")
	default Class<R> getType() {
		final Class<?>[] types = TypeResolver.resolveRawArguments(ResourceExpander.class, getClass());
		checkState(TypeResolver.Unknown.class != types[0], "Couldn't resolve target type parameter for expander class %s", getClass().getSimpleName());
		return (Class<R>) types[0];
	}
	
}
