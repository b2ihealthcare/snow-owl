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

import java.util.Collection;
import java.util.Collections;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;

/**
 * @since 4.7
 */
final class CodeSystemSearchRequest 
	extends SearchIndexResourceRequest<RepositoryContext, CodeSystems, ResourceDocument> 
	implements RepositoryAccessControl {

	private static final long serialVersionUID = 3L;

	CodeSystemSearchRequest() { }

	/**
	 * @since 7.9
	 */
	public enum OptionKey {
		/** Search by specific tooling ID */
		TOOLING_ID,
		/** "Smart" search by title (taking prefixes, stemming, etc. into account) */
		TITLE,
		/** Exact match for code system title */
		TITLE_EXACT,
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
		
		addToolingIdFilter(queryBuilder);
		addNameFilter(queryBuilder);
		addNameExactFilter(queryBuilder);
		addOidFilter(queryBuilder);
		addUpgradeOfFilter(queryBuilder);
		
		return queryBuilder.build();
	}

	private void addToolingIdFilter(final ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.TOOLING_ID)) {
			queryBuilder.filter(ResourceDocument.Expressions.toolingIds(getCollection(OptionKey.TOOLING_ID, String.class)));
		}
	}

	private void addNameFilter(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.TITLE)) {
			final String searchTerm = getString(OptionKey.TITLE);
			ExpressionBuilder termFilter = Expressions.builder();
			termFilter.should(Expressions.dismaxWithScoreCategories(
				ResourceDocument.Expressions.matchNameExact(searchTerm),
				ResourceDocument.Expressions.matchNameAllTermsPresent(searchTerm),
				ResourceDocument.Expressions.matchNameAllPrefixesPresent(searchTerm)
			));
			termFilter.should(Expressions.boost(ResourceDocument.Expressions.id(searchTerm), 1000.0f));
			queryBuilder.must(termFilter.build());
		}
	}
	
	private void addNameExactFilter(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.TITLE_EXACT)) {
			queryBuilder.must(ResourceDocument.Expressions.matchNameOriginal(getString(OptionKey.TITLE_EXACT)));
		}		
	}
	
	private void addOidFilter(final ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.OID)) {
			queryBuilder.filter(ResourceDocument.Expressions.oids(getCollection(OptionKey.OID, String.class)));
		}
	}
	
	private void addUpgradeOfFilter(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.UPGRADE_OF)) {
			Collection<ResourceURI> upgradeOfs = getCollection(OptionKey.UPGRADE_OF, ResourceURI.class);
			queryBuilder.filter(ResourceDocument.Expressions.upgradeOf(upgradeOfs));
		}
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
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}
}
