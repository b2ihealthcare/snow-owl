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
package com.b2international.snowowl.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 1.0
 */
public abstract class AbstractRestService {

	/**
	 * The media type produced and accepted by Snow Owl's RESTful API for JSON content.
	 */
	public static final String JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON_UTF8_VALUE;
	
	/**
	 * The media type produced and accepted by Snow Owl's RESTful API for text content.
	 */
	public static final String TEXT_MEDIA_TYPE = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8";
	
	public static final String CSV_MEDIA_TYPE = "text/csv";
	
	@Autowired
	protected IEventBus bus;

}
