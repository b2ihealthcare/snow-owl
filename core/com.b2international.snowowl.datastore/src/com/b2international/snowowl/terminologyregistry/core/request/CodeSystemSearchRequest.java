/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.identity.domain.Permission;

/**
 * @since 4.7
 */
final class CodeSystemSearchRequest extends SearchIndexResourceRequest<RepositoryContext, CodeSystems, CodeSystemEntry> implements RepositoryAccessControl {

	private static final long serialVersionUID = 1L;

	CodeSystemSearchRequest() {
	}

	@Override
	protected Class<CodeSystemEntry> getDocumentType() {
		return CodeSystemEntry.class;
	}
	
	@Override
	protected Expression prepareQuery(RepositoryContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		addIdFilter(queryBuilder, ids -> Expressions.builder()
				.should(CodeSystemEntry.Expressions.shortNames(ids))
				.should(CodeSystemEntry.Expressions.oids(ids))
				.build());
		return queryBuilder.build();
	}

	@Override
	protected CodeSystems toCollectionResource(RepositoryContext context, Hits<CodeSystemEntry> hits) {
		return new CodeSystems(hits.getHits(), hits.getSearchAfter(), limit(), hits.getTotal());
	}
	
	@Override
	protected CodeSystems createEmptyResult(int limit) {
		return new CodeSystems(Collections.emptyList(), null, limit, 0);
	}
	
	@Override
	public String getOperation() {
		return Permission.BROWSE;
	}

}
