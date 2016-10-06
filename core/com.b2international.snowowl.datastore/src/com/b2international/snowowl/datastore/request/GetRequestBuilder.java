/*******************************************************************************
 * Copyright (c) 2016 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.datastore.request;

import com.b2international.snowowl.core.domain.RepositoryContext;

/**
 * @since 5.2
 */
public abstract class GetRequestBuilder<B extends GetRequestBuilder<B, R, T>, R, T> extends BaseResourceRequestBuilder<B, R> {
	
	private String docId;
	
	protected GetRequestBuilder() {
		super();
	}
	
	public final B setDocId(final String docId) {
		this.docId = docId;
		return getSelf();
	}
	
	@Override
	protected BaseResourceRequest<RepositoryContext, R> create() {
		final GetRequest<R, T> req = createGetRequest();
		req.setDocId(docId);
		return req;
	}

	protected abstract GetRequest<R, T> createGetRequest();
	
}
