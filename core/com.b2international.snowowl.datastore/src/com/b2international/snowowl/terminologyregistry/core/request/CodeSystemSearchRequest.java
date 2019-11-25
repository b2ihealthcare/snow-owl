/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core.request;

import java.util.Collections;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.CodeSystem;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.identity.domain.Permission;

/**
 * @since 4.7
 */
final class CodeSystemSearchRequest extends SearchIndexResourceRequest<RepositoryContext, CodeSystems, CodeSystem> implements RepositoryAccessControl {

	private static final long serialVersionUID = 1L;

	public enum OptionKey {
		
		/**
		 * Filter code systems by their associated URIs.
		 */
		URI,
		
		/**
		 * Filter code systems by their associated tooling identifier.
		 */
		TOOLING
		
	}
	
	CodeSystemSearchRequest() {
	}

	@Override
	protected Class<CodeSystem> getDocumentType() {
		return CodeSystem.class;
	}
	
	@Override
	protected Expression prepareQuery(RepositoryContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		addIdFilter(queryBuilder, ids -> Expressions.builder()
				.should(CodeSystem.Expressions.shortNames(ids))
				.should(CodeSystem.Expressions.oids(ids))
				.build());
		
		if (containsKey(OptionKey.URI)) {
			queryBuilder.filter(CodeSystem.Expressions.uris(getCollection(OptionKey.URI, String.class)));
		}
		
		if (containsKey(OptionKey.TOOLING)) {
			queryBuilder.filter(CodeSystem.Expressions.toolings(getCollection(OptionKey.TOOLING, String.class)));
		}
		
		return queryBuilder.build();
	}

	@Override
	protected CodeSystems toCollectionResource(RepositoryContext context, Hits<CodeSystem> hits) {
		return new CodeSystems(hits.getHits(), hits.getScrollId(), hits.getSearchAfter(), limit(), hits.getTotal());
	}
	
	@Override
	protected CodeSystems createEmptyResult(int limit) {
		return new CodeSystems(Collections.emptyList(), null, null, limit, 0);
	}
	
	@Override
	public String getOperation() {
		return Permission.BROWSE;
	}

}
