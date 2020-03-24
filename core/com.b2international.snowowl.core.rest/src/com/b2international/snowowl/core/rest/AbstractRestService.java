/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Provider;

/**
 * @since 1.0
 */
public abstract class AbstractRestService {

	/**
	 * Two minutes timeout value for commit requests in minutes.
	 */
	protected static final long COMMIT_TIMEOUT = 2L;

	/**
	 * The media type produced and accepted by Snow Owl's RESTful API for JSON content.
	 */
	public static final String JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON_UTF8_VALUE;
	
	/**
	 * The media type produced and accepted by Snow Owl's RESTful API for text content.
	 */
	public static final String TEXT_MEDIA_TYPE = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8";

	/**
	 * The media type produced and accepted by Snow Owl's RESTful API for comma-separated values.
	 */
	public static final String CSV_MEDIA_TYPE = "text/csv;charset=UTF-8";

	/**
	 * The media type produced and accepted by Snow Owl's RESTful API for byte streams.
	 */
	public static final String OCTET_STREAM_MEDIA_TYPE = MediaType.APPLICATION_OCTET_STREAM_VALUE;

	/**
	 * The media type produced and accepted by Snow Owl's RESTful API for multipart form data (file uploads).
	 */
	public static final String MULTIPART_MEDIA_TYPE = MediaType.MULTIPART_FORM_DATA_VALUE;
	
	/**
	 * Header to use when impersonating a commit request. 
	 */
	public static final String X_AUTHOR = "X-Author";
	
	@Autowired
	private Provider<IEventBus> bus;

	private final Pattern sortKeyPattern;
	
	public AbstractRestService() {
		this(Collections.emptySet());
	}
	
	protected final IEventBus getBus() {
		return bus.get();
	}
	
	public AbstractRestService(Set<String> sortFields) {
		final Set<String> allowedSortFields = ImmutableSet.<String>builder()
			.addAll(Collections3.toImmutableSet(sortFields))
			.add(SearchIndexResourceRequest.DOC_ID.getField())
			.add(SearchIndexResourceRequest.SCORE.getField())
			.build();
		this.sortKeyPattern = Pattern.compile("^(" + String.join("|", allowedSortFields) + ")(?:[:](asc|desc))?$");
	}
	
	/**
	 * Extract {@link SearchResourceRequest.Sort}s from the given list of sortKeys. The returned list maintains the same order as the input sortKey
	 * list.
	 * 
	 * @param sortKeys
	 * @return
	 */ 
	protected final List<Sort> extractSortFields(List<String> sortKeys) {
		return extractSortFields(sortKeys, null, null);
	}
	
	/**
	 * Extract {@link SearchResourceRequest.Sort}s from the given list of sortKeys. The returned list maintains the same order as the input sortKey
	 * list.
	 * 
	 * @param sortKeys
	 * @param branch 
	 * @param acceptLanguage 
	 * @return
	 */
	protected final List<Sort> extractSortFields(List<String> sortKeys, String branch, String acceptLanguage) {
		if (CompareUtils.isEmpty(sortKeys)) {
			return Collections.emptyList();
		}
		final List<Sort> result = Lists.newArrayList();
		for (String sortKey : sortKeys) {
			Matcher matcher = sortKeyPattern.matcher(sortKey);
			if (matcher.matches()) {
				String field = matcher.group(1);
				String order = matcher.group(2);
				result.add(toSort(field, !"desc".equals(order), branch, acceptLanguage));
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
	 * @param acceptLanguage 
	 * @return
	 */
	protected Sort toSort(String field, boolean ascending, String branch, String acceptLanguage) {
		return SearchResourceRequest.SortField.of(field, ascending);
	}
	
	/**
	 * Creates a Location header URI that should be returned from all POST resource create endpoints.
	 * @param resourceId - the identifier of the resource
	 * @return a URI to be added as Location header value
	 */
	protected final URI getResourceLocationURI(String resourceId) {
		return MvcUriComponentsBuilder.fromController(getClass()).pathSegment(resourceId).build().toUri();
	}
	
	/**
	 * Creates a Location header URI that should be returned from all POST resource create endpoints.
	 * @param branch - the branch where the resource has been created
	 * @param resourceId - the identifier of the resource
	 * @return a URI to be added as Location header value
	 */
	protected final URI getResourceLocationURI(String branch, String resourceId) {
		return MvcUriComponentsBuilder.fromController(getClass()).pathSegment(resourceId).build(branch);
	}

}
