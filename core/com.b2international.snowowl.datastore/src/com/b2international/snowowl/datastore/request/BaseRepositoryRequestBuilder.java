package com.b2international.snowowl.datastore.request;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.7
 * @param <B> - the builder type
 * @param <R> - the response type
 */
public abstract class BaseRepositoryRequestBuilder<B extends BaseRepositoryRequestBuilder<B, C, R>, C extends ServiceProvider, R> extends BaseRequestBuilder<B, C, R> {

	private final String repositoryId;
	
	protected BaseRepositoryRequestBuilder(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	protected final Request<ServiceProvider, R> wrap(Request<RepositoryContext, R> req) {
		return new RepositoryRequest<>(repositoryId, req);
	}
	
}
