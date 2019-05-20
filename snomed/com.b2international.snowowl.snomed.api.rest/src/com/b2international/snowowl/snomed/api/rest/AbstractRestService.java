/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.http.AcceptHeader;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * @since 1.0
 */
public abstract class AbstractRestService {

	/**
	 * Two minutes timeout value for commit requests in milliseconds.
	 */
	protected static final long COMMIT_TIMEOUT = 120L * 1000L;

	/**
	 * The currently supported versioned media type of the snowowl RESTful API.
	 */
	public static final String SO_MEDIA_TYPE = "application/vnd.com.b2international.snowowl+json";

	@Autowired
	@Value("${repositoryId}")
	protected String repositoryId;
	
	@Autowired
	protected IEventBus bus;

	private final Pattern sortKeyPattern;
	
	public AbstractRestService(Set<String> sortFields) {
		final Set<String> allowedSortFields = ImmutableSet.<String>builder()
			.addAll(Collections3.toImmutableSet(sortFields))
			.add(SearchIndexResourceRequest.DOC_ID.getField())
			.add(SearchIndexResourceRequest.SCORE.getField())
			.build();
		this.sortKeyPattern = Pattern.compile("^(" + String.join("|", allowedSortFields) + ")([:](asc|desc))?$");	
	}
	
	/**
	 * Extract {@link SearchResourceRequest.Sort}s from the given list of sortKeys. The returned list maintains the same order as the input sortKey
	 * list.
	 * 
	 * @param sortKeys
	 * @return
	 */ 
	protected final List<Sort> extractSortFields(List<String> sortKeys) {
		return extractSortFields(sortKeys, null, Collections.emptyList());
	}
	
	/**
	 * Extract {@link SearchResourceRequest.Sort}s from the given list of sortKeys. The returned list maintains the same order as the input sortKey
	 * list.
	 * 
	 * @param sortKeys
	 * @param branch 
	 * @param extendedLocales 
	 * @return
	 */
	protected final List<Sort> extractSortFields(List<String> sortKeys, String branch, List<ExtendedLocale> extendedLocales) {
		if (CompareUtils.isEmpty(sortKeys)) {
			return Collections.emptyList();
		}
		final List<Sort> result = Lists.newArrayList();
		for (String sortKey : sortKeys) {
			Matcher matcher = sortKeyPattern.matcher(sortKey);
			if (matcher.matches()) {
				String field = matcher.group(1);
				String order = matcher.group(2);
				result.add(toSort(field, !"desc".equals(order), branch, extendedLocales));
			} else {
				throw new BadRequestException("Sort key '%s' is not supported, or incorrect sort field pattern.", sortKey);				
			}
		}
		return result;
	}

	/**
	 * Subclasses may optionally override this method in order to support special sorting requirements for special sort fields. By default this method
	 * returns a field based sort on the given field.
	 * 
	 * @param field
	 * @param ascending
	 * @param branch 
	 * @param extendedLocales 
	 * @return
	 */
	protected Sort toSort(String field, boolean ascending, String branch, List<ExtendedLocale> extendedLocales) {
		return SearchResourceRequest.SortField.of(field, ascending);
	}
	
	protected final List<ExtendedLocale> getExtendedLocales(final String acceptLanguage) {
		try {
			return AcceptHeader.parseExtendedLocales(new StringReader(acceptLanguage));
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
	}

}
