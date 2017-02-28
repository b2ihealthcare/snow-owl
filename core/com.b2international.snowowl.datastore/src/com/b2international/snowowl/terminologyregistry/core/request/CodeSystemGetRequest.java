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
package com.b2international.snowowl.terminologyregistry.core.request;

import java.io.IOException;

import com.b2international.index.Searcher;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.request.BaseResourceRequest;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
final class CodeSystemGetRequest extends BaseResourceRequest<BranchContext, CodeSystemEntry> {

	private static final long serialVersionUID = 1L;

	private String uniqueId;
	
	CodeSystemGetRequest() {
	}

	void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Override
	public CodeSystemEntry execute(final BranchContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		queryBuilder.should(CodeSystemEntry.Expressions.shortName(uniqueId));
		queryBuilder.should(CodeSystemEntry.Expressions.oid(uniqueId));
		
		final Query<CodeSystemEntry> query = Query.select(CodeSystemEntry.class)
				.where(queryBuilder.build())
				.limit(2)
				.build();
		
		try {
			final CodeSystemEntry codeSystem = Iterables.getOnlyElement(context.service(Searcher.class).search(query), null);
			if (codeSystem == null) {
				throw new CodeSystemNotFoundException(uniqueId);
			} else {
				return codeSystem;
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
		
	}

}
