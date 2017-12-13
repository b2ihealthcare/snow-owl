/*******************************************************************************
 * Copyright (c) 2017 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.validation.whitelist;

import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.1
 */
public final class ValidationWhiteLists extends PageableCollectionResource<ValidationWhiteList>{

	public ValidationWhiteLists(int limit, int total) {
		this(Collections.emptyList(), null, null, limit, total);
	}

	@JsonCreator
	public ValidationWhiteLists(
			@JsonProperty("items") List<ValidationWhiteList> items, 
			@JsonProperty("scrollId") String scrollId, 
			@JsonProperty("searchAfter") Object[] searchAfter, 
			@JsonProperty("limit") int limit,
			@JsonProperty("total") int total) {
		super(items, scrollId, searchAfter, limit, total);
	}
	
}
