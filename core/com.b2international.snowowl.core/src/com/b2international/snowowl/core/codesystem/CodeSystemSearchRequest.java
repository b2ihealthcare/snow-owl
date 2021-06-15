/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import java.util.Collections;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.BaseResourceSearchRequest;

/**
 * @since 4.7
 */
final class CodeSystemSearchRequest extends BaseResourceSearchRequest<CodeSystems> {

	private static final long serialVersionUID = 3L;

	CodeSystemSearchRequest() { }

	/**
	 * @since 7.9
	 */
	public enum OptionKey {
		/** Search by specific tooling ID */
		TOOLING_ID,
		/** HL7 registry OID **/
		OID, 
		/**
		 * Match CodeSystem by their Upgrade of property 
		 */
		UPGRADE_OF
	}
	
	@Override
	protected Class<ResourceDocument> getDocumentType() {
		return ResourceDocument.class;
	}
	
	@Override
	protected Expression prepareQuery(RepositoryContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		addIdFilter(queryBuilder, ResourceDocument.Expressions::ids);
		addTitleExactFilter(queryBuilder);
		addTitleFilter(queryBuilder);
		
		addFilter(queryBuilder, OptionKey.TOOLING_ID, String.class, ResourceDocument.Expressions::toolingIds);
		addFilter(queryBuilder, OptionKey.OID, String.class, ResourceDocument.Expressions::oids);
		addFilter(queryBuilder, OptionKey.UPGRADE_OF, ResourceURI.class, ResourceDocument.Expressions::upgradeOfs);

		
		return queryBuilder.build();
	}
	
	@Override
	protected CodeSystems toCollectionResource(RepositoryContext context, Hits<ResourceDocument> hits) {
		final CodeSystemConverter converter = new CodeSystemConverter(context, expand(), null);
		return converter.convert(hits.getHits(), hits.getSearchAfter(), hits.getLimit(), hits.getTotal());
	}
	
	@Override
	protected CodeSystems createEmptyResult(int limit) {
		return new CodeSystems(Collections.emptyList(), null, limit, 0);
	}
}
