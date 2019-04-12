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
package com.b2international.snowowl.snomed.api.rest;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.AcceptHeader;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 1.0
 */
public abstract class AbstractRestService {

	/**
	 * Two minutes timeout value for commit requests in milliseconds.
	 */
	protected static final long COMMIT_TIMEOUT = 120L * 1000L;

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
	
	@Autowired
	protected IEventBus bus;

	@Autowired
	@Value("${repositoryId}")
	protected String repositoryId;
	
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
