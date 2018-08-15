/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;

public final class SortParser {

	private static final Pattern FIELD_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\.]+$");

	private static final char PART_SEPARATOR = ',';
	private static final char DIRECTION_SEPARATOR = ':';

	private static final Splitter PART_SPLITTER = Splitter.on(PART_SEPARATOR)
			.omitEmptyStrings()
			.trimResults();

	public static List<SearchResourceRequest.Sort> parse(final String sortBy) {

		return PART_SPLITTER.splitToList(sortBy)
				.stream()
				.map(part -> {
					final int separatorIdx = part.indexOf(DIRECTION_SEPARATOR);

					final String field;
					if (separatorIdx > 0) {
						field = part.substring(0, separatorIdx);
					} else {
						field = part;
					}

					if (!FIELD_PATTERN.matcher(field).matches()) {
						throw new IllegalArgumentException("Invalid sort field '" + field + "'.");
					}

					final boolean ascending;
					if (separatorIdx > 0) {
						final String order = part.substring(separatorIdx + 1);

						switch (order) {
							case "asc": ascending = true; break;
							case "desc": ascending = false; break;
							default: throw new IllegalArgumentException("Unexpected sort order string '" + order + "' (must be 'asc' or 'desc').");
						}
					} else {
						ascending = true;
					}

					if (ascending) {
						return SearchResourceRequest.SortField.ascending(field);
					} else {
						return SearchResourceRequest.SortField.descending(field);
					}
				})
				.collect(Collectors.toList());
	}
}
