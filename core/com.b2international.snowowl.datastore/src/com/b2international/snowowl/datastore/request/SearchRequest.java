/*******************************************************************************
 * Copyright (c) 2016 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.datastore.request;

import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.2
 */
public abstract class SearchRequest<B> extends BaseSearchRequest<RepositoryContext, B> {
	
	@NotNull
	private Collection<String> docIds;
	
	public void setDocIds(Collection<String> docIds) {
		this.docIds = docIds;
	}
	
	@JsonProperty
	protected final Collection<String> docIds() {
		return docIds;
	}

}
