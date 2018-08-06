/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Contains handy utility methods to work with responses in Spring MVC.
 * 
 * @since 1.0
 */
public class Responses {

	private Responses() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}

	/**
	 * Creates a {@link ResponseBuilder} with the HTTP status code CREATED.
	 * 
	 * @param location
	 *            the Location header value to find the created resource
	 * @return
	 */
	public static final ResponseBuilder created(URI location) {
		return status(HttpStatus.CREATED).location(location);
	}

	/**
	 * Creates a {@link ResponseBuilder} with the HTTP status code ACCEPTED.
	 * 
	 * @param location
	 *            the Location header value to find/poll the resource describing current status of the asynchronous request, or the end result
	 * @return
	 */
	public static ResponseBuilder accepted(URI location) {
		return status(HttpStatus.ACCEPTED).location(location);
	}

	/**
	 * Creates a {@link ResponseBuilder} with the HTTP status code OK.
	 * 
	 * @return
	 */
	public static final ResponseBuilder ok() {
		return status(HttpStatus.OK);
	}
	
	/**
	 * Creates a {@link ResponseBuilder} with the HTTP status code NO_CONTENT.
	 * 
	 * @return
	 */
	public static ResponseBuilder noContent() {
		return status(HttpStatus.NO_CONTENT);
	}

	/**
	 * Creates a {@link ResponseBuilder} with the HTTP status code NOT_MODIFIED.
	 * 
	 * @param tag
	 *            - HTTP ETag value associated with the not modified response.
	 * @return
	 */
	public static final ResponseBuilder notModified(String tag) {
		return status(HttpStatus.NOT_MODIFIED);
	}
	
	/**
	 * Creates a {@link ResponseBuilder} with the given HTTP status code.
	 * 
	 * @param status
	 * @return
	 */
	public static final ResponseBuilder status(HttpStatus status) {
		return builder().status(status);
	}

	/**
	 * Creates an empty {@link ResponseBuilder} instance.
	 * 
	 * @return
	 */
	public static ResponseBuilder builder() {
		return new ResponseBuilder();
	}

	/**
	 * {@link ResponseBuilder}
	 * 
	 * @since 1.0
	 */
	public static class ResponseBuilder {

		private HttpStatus status;
		private HttpHeaders headers = new HttpHeaders();

		/**
		 * Sets the HTTP status to the given status.
		 * 
		 * @param status
		 *            - the HTTP status to set, may not be <code>null</code>.
		 * @return
		 */
		public ResponseBuilder status(HttpStatus status) {
			this.status = checkNotNull(status, "Status must be defined");
			return this;
		}

		/**
		 * Sets the location header to the given URI value.
		 * 
		 * @param location
		 * @return
		 */
		public ResponseBuilder location(URI location) {
			headers.setLocation(location);
			return this;
		}

		/**
		 * Adds a custom header name - value.
		 * 
		 * @param name
		 * @param value
		 * @return
		 */
		public ResponseBuilder header(String name, String value) {
			headers.add(name, value);
			return this;
		}

		/**
		 * Builds a {@link ResponseEntity} from the accumulated response properties with the given entity as body.
		 * 
		 * @param body
		 * @return
		 */
		public <T> ResponseEntity<T> build(T body) {
			if (!headers.isEmpty()) {
				return new ResponseEntity<T>(body, headers, status);
			}
			return new ResponseEntity<T>(body, status);
		}

		/**
		 * Builds a {@link ResponseEntity} from the accumulated response properties with a {@link Void} <code>null</code> body.
		 * 
		 * @return
		 */
		public ResponseEntity<Void> build() {
			return build((Void)null);
		}

	}

}
