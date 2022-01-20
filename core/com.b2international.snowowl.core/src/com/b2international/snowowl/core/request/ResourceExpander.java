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
package com.b2international.snowowl.core.request;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import com.b2international.commons.options.Options;

import net.jodah.typetools.TypeResolver;

/**
 * @since 8.1
 * @param R - the resource's class type who's instances need to be expanded with additional fields
 */
public interface ResourceExpander<R> {

	int DEFAULT_LIMIT = 50;
	
	/**
	 * Expands resources with additional fields.
	 * 
	 * @param results - the list of results to expand
	 */
	void expand(List<R> results);
	
	default int getLimit(final Options expandOptions) {
		return expandOptions.containsKey("limit") ? expandOptions.get("limit", Integer.class) : DEFAULT_LIMIT;
	}
	
	/**
	 * @return the class of the target type this expander can expand
	 */
	@SuppressWarnings("unchecked")
	default Class<R> getType() {
		final Class<?>[] types = TypeResolver.resolveRawArguments(BaseResourceConverter.class, getClass());
		checkState(TypeResolver.Unknown.class != types[0], "Couldn't resolve target type parameter for expander class %s", getClass().getSimpleName());
		return (Class<R>) types[1];
	}
	
}
