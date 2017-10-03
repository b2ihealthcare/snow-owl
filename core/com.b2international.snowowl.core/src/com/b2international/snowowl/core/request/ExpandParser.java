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
package com.b2international.snowowl.core.request;

import java.io.IOException;
import java.util.Map;

import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.5
 */
public class ExpandParser {

	private static final JsonFactory JSON_FACTORY = new JsonFactory().enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);

	public static Options parse(final String expand) {
		
		String jsonizedOptionPart = String.format("{%s}", expand.replace("(", ":{").replace(')', '}'));

		try {
			JsonParser parser = JSON_FACTORY.createParser(jsonizedOptionPart);
			parser.setCodec(new ObjectMapper());
			Map<String, Object> source = parser.<Map<String, Object>>readValueAs(new TypeReference<Map<String, Object>>() { });
			return OptionsBuilder.newBuilder().putAll(source).build();
		} catch (JsonParseException e) {
			throw new BadRequestException("Expansion parameter %s is malformed.", expand);
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Caught I/O exception while reading expansion parameters.", e);
		}
	}
}
