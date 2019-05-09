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

import com.b2international.commons.StringUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;

/**
 * @since 4.7
 */
final class CodeSystemVersionSearchRequest extends SearchIndexResourceRequest<RepositoryContext, CodeSystemVersions, CodeSystemVersionEntry> {

	private static final long serialVersionUID = 1L;

	private String codeSystemShortName;
	private String versionId;
	
	/**
	 * @since 6.15
	 */
	public static enum OptionKey {
		
		/**
		 * Filter versions by effective date starting from this value, inclusive.
		 */
		CREATED_AT_START,
		
		/**
		 * Filter versions by effective date ending with this value, inclusive.
		 */
		CREATED_AT_END
		
	}
	
	CodeSystemVersionSearchRequest() {
	}

	void setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
	}
	
	void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	
	@Override
	protected Expression prepareQuery(RepositoryContext context) {
		final ExpressionBuilder query = Expressions.builder();

		if (!StringUtils.isEmpty(codeSystemShortName)) {
			query.filter(CodeSystemVersionEntry.Expressions.shortName(codeSystemShortName));
		}
		
		if (!StringUtils.isEmpty(versionId)) {
			query.filter(CodeSystemVersionEntry.Expressions.versionId(versionId));
		}
		
		if (containsKey(OptionKey.CREATED_AT_START) || containsKey(OptionKey.CREATED_AT_END)) {
			final long from = containsKey(OptionKey.CREATED_AT_START) ? get(OptionKey.CREATED_AT_START, Long.class) : 0;
			final long to = containsKey(OptionKey.CREATED_AT_END) ? get(OptionKey.CREATED_AT_END, Long.class) : Long.MAX_VALUE;
			query.filter(CodeSystemVersionEntry.Expressions.createdAt(from, to));
		}
		
		return query.build();
	}
	
	@Override
	protected Class<CodeSystemVersionEntry> getDocumentType() {
		return CodeSystemVersionEntry.class;
	}

	@Override
	protected CodeSystemVersions toCollectionResource(RepositoryContext context, Hits<CodeSystemVersionEntry> hits) {
		return new CodeSystemVersions(hits.getHits(), hits.getScrollId(), hits.getSearchAfter(), limit(), hits.getTotal());
	}
	
	@Override
	protected CodeSystemVersions createEmptyResult(int limit) {
		return new CodeSystemVersions(Collections.emptyList(), null, null, limit, 0);
	}

}
