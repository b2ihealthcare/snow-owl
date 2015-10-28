/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @since 1.0
 */
public abstract class AbstractRestService {

	/**
	 * The currently supported versioned media type of the snowowl RESTful API.
	 */
	public static final String SO_MEDIA_TYPE = "application/vnd.com.b2international.snowowl+json; charset=utf-8";

	/**
	 * The currently supported versioned media type of the IHTSDO SNOMED CT Browser RESTful API.
	 */
	public static final String IHTSDO_V1_MEDIA_TYPE = "application/vnd.org.ihtsdo.browser+json; charset=utf-8";

	public static final String APPLICATION_JSON_VALUE = "application/json; charset=utf-8";

	public static final String APPLICATION_CSV_VALUE = "text/csv; charset=utf-8";

	public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream; charset=utf-8";

	@Autowired
	@Value("${repositoryId}")
	protected String repositoryId;
}
