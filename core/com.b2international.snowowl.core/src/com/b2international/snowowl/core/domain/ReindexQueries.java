/*******************************************************************************
 * Copyright (c) 2023 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 8.9
 */
public class ReindexQueries extends ListCollectionResource<ReindexQuery> {

	private static final long serialVersionUID = -6200698613037334274L;

	public ReindexQueries(@JsonProperty("items") final List<ReindexQuery> items) {
		super(items);
	}

}
