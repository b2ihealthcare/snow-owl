/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Iterables;

/**
 * @since 5.2
 */
public abstract class GetRequest<R, T> extends BaseResourceRequest<RepositoryContext, R> {
	
	private final String type;
	
	@NotEmpty
	@JsonProperty
	private String docId;
	
	protected GetRequest(final String type) {
		this.type = checkNotNull(type, "type");
	}

	protected final void setDocId(final String docId) {
		this.docId = docId;
	}
	
	@Override
	public R execute(final RepositoryContext context) {
		final Query<T> query = Query.select(getDocType())
				.where(Expressions.exactMatch("id", docId))
				.limit(1)
				.build();
		
		try {
			final Hits<T> hits = context.service(Searcher.class).search(query);
			final T hit = Iterables.getOnlyElement(hits, null);
			
			if (hit == null) {
				throw new NotFoundException(type, docId);
			} else {
				return process(context, hit, expand());
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	protected abstract Class<T> getDocType();
	
	protected abstract R process(RepositoryContext context, T doc, Options expand);
	
}
