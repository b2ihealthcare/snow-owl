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
import java.util.Collections;

import com.b2international.commons.StringUtils;
import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.request.RevisionSearchRequest;

/**
 * @since 4.7
 */
final class CodeSystemSearchRequest extends RevisionSearchRequest<CodeSystems> {

	private static final long serialVersionUID = 1L;

	private String shortName;
	private String oid;
	
	CodeSystemSearchRequest() {
	}

	void setShortName(String shortName) {
		this.shortName = shortName;
	}

	void setOid(String oid) {
		this.oid = oid;
	}

	@Override
	protected CodeSystems doExecute(final BranchContext context) throws IOException {
		final ExpressionBuilder query = Expressions.builder();

		if (!StringUtils.isEmpty(shortName)) {
			query.must(CodeSystemEntry.Expressions.shortName(shortName));
		}

		if (!StringUtils.isEmpty(oid)) {
			query.must(CodeSystemEntry.Expressions.oid(oid));
		}
		
		final Searcher searcher = context.service(Searcher.class);

		final Hits<CodeSystemEntry> hits = searcher.search(Query.select(CodeSystemEntry.class)
				.where(query.build())
				.offset(offset())
				.limit(limit())
				.build());

		return new CodeSystems(hits.getHits(), offset(), limit(), hits.getTotal());
	}
	
	@Override
	protected CodeSystems createEmptyResult(int offset, int limit) {
		return new CodeSystems(Collections.emptyList(), offset, limit, 0);
	}

}
