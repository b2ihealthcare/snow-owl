/*
 * Copyright 2011-2023 B2i Healthcare, https://b2ihealthcare.com
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
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.snowowl.core.domain.SortSupport;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.eventbus.IEventBus;
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
	public static final String JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON_VALUE;

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
	 * The media type produced and accepted by Snow Owl's RESTful API for XML content.
	 */
	public static final String XML_MEDIA_TYPE = MediaType.APPLICATION_XML_VALUE;

	/**
	 * The media type produced and accepted by Snow Owl's syndication feeds.
	 */
	public static final String ATOM_MEDIA_TYPE = MediaType.APPLICATION_ATOM_XML_VALUE;

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

	public AbstractRestService(Set<String> sortFields) {
		this.sortKeyPattern = SortSupport.createSortKeyPattern(sortFields);
	}

	/**
	 * Extract {@link SearchResourceRequest.Sort}s from the given list of sortKeys. The returned list maintains the same order as the input sortKey
	 * list.
	 * 
	 * @param sortKeys
	 * @return
	 * @see SortSupport#extractSortFields(List, Pattern)
	 */
	protected final List<Sort> extractSortFields(List<String> sortKeys) {
		return SortSupport.extractSortFields(sortKeys, this.sortKeyPattern);
	}

	protected final IEventBus getBus() {
		return bus.get();
	}

	/**
	 * Creates a Location header URI builder from this controller class.
	 * 
	 * @return an {@link UriComponentsBuilder} instance using this class as base
	 */
	protected final UriComponentsBuilder createURIBuilder() {
		return MvcUriComponentsBuilder.fromController(getClass());
	}

	/**
	 * Creates a Location header URI that should be returned from all POST resource create endpoints.
	 * 
	 * @param resourceId
	 *            - the identifier of the resource
	 * @return a URI to be added as Location header value
	 */
	protected final URI getResourceLocationURI(String resourceId) {
		return createURIBuilder().pathSegment(resourceId).build().toUri();
	}

	/**
	 * Creates a Location header URI that should be returned from all POST resource create endpoints.
	 * 
	 * @param branch
	 *            - the branch where the resource has been created
	 * @param resourceId
	 *            - the identifier of the resource
	 * @return a URI to be added as Location header value
	 */
	protected final URI getResourceLocationURI(String branch, String resourceId) {
		return createURIBuilder().pathSegment(resourceId).build(branch);
	}

	/**
	 * Converts the given array to a {@link List} so it can be passed to methods that require {@link Iterable} instances.
	 * 
	 * @param <T>
	 * @param array
	 *            - the array to convert
	 * @return a {@link List} representing the same elements as the input array, or <code>null</code> if the input array argument was
	 *         <code>null</code>.
	 */
	protected <T> List<T> asList(T[] array) {
		return array == null ? null : List.of(array);
	}

}
