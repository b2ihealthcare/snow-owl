/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * @since 9.0.0
 */
public interface SortSupport {

	/**
	 * Creates a new {@link Pattern} instance based on the given accepted sort field names.
	 * @param sortFields - the sort field names to accept in the pattern
	 * @return a new {@link Pattern} instance, never <code>null</code>
	 */
	static Pattern createSortKeyPattern(Set<String> sortFields) {
		final Set<String> allowedSortFields = ImmutableSet.<String>builder()
			.addAll(Collections3.toImmutableSet(sortFields))
			.add(SearchIndexResourceRequest.SCORE.getField())
			.build();
		return Pattern.compile("^(" + String.join("|", allowedSortFields) + ")(?:[:](asc|desc))?$");
	}
	
	/**
	 * Extract {@link SearchResourceRequest.Sort}s from the given list of sortKeys. The returned list maintains the same order as the input sortKey
	 * list.
	 * 
	 * @param sortKeys
	 * @return
	 */ 
	static List<Sort> extractSortFields(List<String> sortKeys, Pattern sortKeyPattern) {
		if (CompareUtils.isEmpty(sortKeys)) {
			return Collections.emptyList();
		}
		final List<Sort> result = Lists.newArrayList();
		for (String sortKey : sortKeys) {
			Matcher matcher = sortKeyPattern.matcher(sortKey);
			if (matcher.matches()) {
				String field = matcher.group(1);
				String order = matcher.group(2);
				result.add(SearchResourceRequest.SortField.of(field, !"desc".equals(order)));
			} else {
				throw new BadRequestException("Sort key '%s' is not supported, or incorrect sort field pattern.", sortKey);				
			}
		}
		return result;
	}
	
}
