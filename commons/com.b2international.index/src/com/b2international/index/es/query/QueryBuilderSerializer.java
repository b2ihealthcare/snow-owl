/*******************************************************************************
 * Copyright (c) 2022 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.index.es.query;

import java.io.IOException;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @since 8.8
 */
public class QueryBuilderSerializer extends JsonSerializer<QueryBuilder> {

	@Override
	public void serialize(final QueryBuilder value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
		gen.writeString(SearchSourceBuilder.searchSource().query(value).toString());
	}

}
