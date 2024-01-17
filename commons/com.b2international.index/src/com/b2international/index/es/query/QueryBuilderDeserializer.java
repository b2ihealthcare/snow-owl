/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.index.es.query;

import java.io.IOException;
import java.util.Collections;

import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.LoggingDeprecationHandler;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.NamedXContentRegistry;
import org.elasticsearch.xcontent.XContentParser;
import org.elasticsearch.xcontent.XContentType;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @since 8.9.0
 */
public class QueryBuilderDeserializer extends StdDeserializer<org.elasticsearch.index.query.QueryBuilder> {

	private static final long serialVersionUID = 8179941099961112852L;

	private static final NamedXContentRegistry registry;

	static {
		final SearchModule module = new SearchModule(Settings.EMPTY, false, Collections.emptyList());
		registry = new NamedXContentRegistry(module.getNamedXContents());
	}

	protected QueryBuilderDeserializer() {
		super(org.elasticsearch.index.query.QueryBuilder.class);
	}

	@Override
	public org.elasticsearch.index.query.QueryBuilder deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
		final XContentParser parser = XContentHelper.createParser(registry, LoggingDeprecationHandler.INSTANCE, new BytesArray(p.getValueAsString()), XContentType.JSON);
		return SearchSourceBuilder.fromXContent(parser).query();
	}


}
