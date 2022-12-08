/*******************************************************************************
 * Copyright (c) 2022 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.domain;

import java.io.Serializable;

import org.elasticsearch.index.query.QueryBuilder;

import com.b2international.index.es.query.QueryBuilderDeserializer;
import com.b2international.index.es.query.QueryBuilderSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @since 8.8
 */
public class ReindexQuery implements Serializable {

	private static final long serialVersionUID = -1724713916733180036L;

	private String index;

	private QueryBuilder query;

	public String getIndex() {
		return index;
	}

	public void setIndex(final String index) {
		this.index = index;
	}

	@JsonSerialize(using = QueryBuilderSerializer.class)
	public QueryBuilder getQuery() {
		return query;
	}

	@JsonDeserialize(using = QueryBuilderDeserializer.class)
	public void setQuery(final QueryBuilder query) {
		this.query = query;
	}

}
