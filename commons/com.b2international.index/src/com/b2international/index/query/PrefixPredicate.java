/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.query;

import java.util.stream.Collectors;

import org.elasticsearch.common.util.iterable.Iterables;

import com.b2international.commons.exceptions.BadRequestException;

/**
 * @since 5.0
 */
public final class PrefixPredicate extends SetPredicate<String> {

	PrefixPredicate(String field, Iterable<String> arguments) {
		super(field, arguments);
		final long numberOfTerms = Iterables.size(arguments);
		// XXX the usual maxClauseCount default value in ES is 1024, so allow room for other clauses before responding with an error to the caller 
		if (numberOfTerms > 1000) {
			throw new BadRequestException("Too many ('%s') prefix query clauses supplied for '%s' field.", numberOfTerms, field);
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s = (%s)", getField(), values().stream().map(value -> value.concat("*")).collect(Collectors.joining(",")));
	}

}