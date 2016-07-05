/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.google.common.collect.Iterables;

/**
 * @since 4.5
 * @param <R>
 *            - the return type of the GET request
 */
public abstract class RevisionGetRequest<R> extends BaseResourceRequest<BranchContext, R> {

	private final String category;
	
	@NotEmpty
	private String componentId;
	
	protected RevisionGetRequest(ComponentCategory category) {
		this(category.getDisplayName());
	}
	
	protected RevisionGetRequest(String category) {
		this.category = checkNotNull(category, "category");
	}
	
	protected final void setComponentId(String componentId) {
		this.componentId = componentId;
	}
	
	@Override
	public final R execute(BranchContext context) {
		final Query<? extends RevisionDocument> query = Query.select(getType())
				.where(RevisionDocument.Expressions.id(componentId))
				.limit(1)
				.build();
		try {
			Hits<? extends RevisionDocument> hits = context.service(RevisionSearcher.class).search(query);
			final RevisionDocument hit = Iterables.getOnlyElement(hits, null);
			if (hit == null) {
				throw new ComponentNotFoundException(category, componentId);
			} else {
				return process(context, hit, expand());
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	protected abstract Class<? extends RevisionDocument> getType();

	protected abstract R process(BranchContext context, IComponent<String> component, Options expand);

	@Override
	public final String toString() {
		return String.format("{type:'%s', componentId:'%s', expand:%s, locales:%s}", 
				getClass().getSimpleName(), 
				componentId,
				expand(), 
				formatStringList(locales()));
	}

}
