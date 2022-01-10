/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons.rest;

import java.lang.reflect.Type;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;

/**
 * @since 8.0.1
 */
public class CustomJackson2ObjectMapperFactory implements Jackson2ObjectMapperFactory {
	
	@Override
	public com.fasterxml.jackson.databind.ObjectMapper create(final Type cls, final String charset) {
		final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		// bbanfai: added date format
		final StdDateFormat dateFormat = new StdDateFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		mapper.setDateFormat(dateFormat);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		return mapper;
	}
}
