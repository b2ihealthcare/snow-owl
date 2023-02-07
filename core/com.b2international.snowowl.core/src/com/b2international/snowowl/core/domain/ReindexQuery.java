/*******************************************************************************
 * Copyright (c) 2023 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.domain;

import java.io.Serializable;

import com.b2international.index.es.query.QueryBuilderDeserializer;
import com.b2international.index.es.query.QueryBuilderSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @since 8.9
 */
public class ReindexQuery implements Serializable {

	private static final long serialVersionUID = -1724713916733180036L;

	private String index;
	private org.elasticsearch.index.query.QueryBuilder query;

	public static ReindexQuery of(String index, org.elasticsearch.index.query.QueryBuilder query) {
		final ReindexQuery rq = new ReindexQuery();
		rq.setIndex(index);
		rq.setQuery(query);
		return rq;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(final String index) {
		this.index = index;
	}

	@JsonSerialize(using = QueryBuilderSerializer.class)
	public org.elasticsearch.index.query.QueryBuilder getQuery() {
		return query;
	}

	@JsonDeserialize(using = QueryBuilderDeserializer.class)
	public void setQuery(final org.elasticsearch.index.query.QueryBuilder query) {
		this.query = query;
	}

}
