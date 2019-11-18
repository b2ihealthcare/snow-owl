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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.request.SearchIndexResourceRequest;
import com.b2international.snowowl.identity.domain.Permission;

/**
 * @since 4.7
 */
final class CodeSystemVersionSearchRequest 
	extends SearchIndexResourceRequest<RepositoryContext, CodeSystemVersions, CodeSystemVersionEntry> 
	implements RepositoryAccessControl {

	private static final long serialVersionUID = 2143809359873750383L;

	/**
	 * @since 6.15
	 */
	public static enum OptionKey {
		
		/**
		 * Match versions by their code system property.
		 */
		CODE_SYSTEM_SHORT_NAME,
		
		/**
		 * Match versions by their version ID property.
		 */
		VERSION_ID,
		
		/**
		 * Match versions by their effective date property.
		 */
		EFFECTIVE_DATE,
		
		/**
		 * Combined filter option key for parent branch path and version ID.
		 */
		BRANCH_PATH,
		
		/**
		 * Parent branch path property to match versions for.
		 */
		PARENT_BRANCH_PATH,
		
		/**
		 * Filter versions by effective date starting from this value, inclusive.
		 */
		CREATED_AT_START,
		
		/**
		 * Filter versions by effective date ending with this value, inclusive.
		 */
		CREATED_AT_END, 
		
	}
	
	CodeSystemVersionSearchRequest() {
	}

	@Override
	protected Expression prepareQuery(RepositoryContext context) {
		final ExpressionBuilder query = Expressions.builder();

		if (containsKey(OptionKey.CODE_SYSTEM_SHORT_NAME)) {
			query.filter(CodeSystemVersionEntry.Expressions.shortNames(getCollection(OptionKey.CODE_SYSTEM_SHORT_NAME, String.class)));
		}
		
		if (containsKey(OptionKey.BRANCH_PATH)) {
			if (containsKey(OptionKey.VERSION_ID) || containsKey(OptionKey.PARENT_BRANCH_PATH)) {
				throw new BadRequestException("Cannot filter by using both (versionId and/or parentBranchPath) and the special branchPath filter.");
			}
			
			final String branchPath = getString(OptionKey.BRANCH_PATH);
			final Path path = Paths.get(branchPath);
			if (path.getNameCount() == 0) {
				throw new BadRequestException("Invalid branch path, specify an exact version branch (eg. `MAIN/2002-01-31`). Got: '%s'.", branchPath);
			}
			
			// apply version ID filter
			final String versionId = path.getFileName().toString();
			query.filter(CodeSystemVersionEntry.Expressions.versionIds(Collections.singleton(versionId)));
			
			// apply parent branch filter
			final String parentBranch = path.subpath(0, path.getNameCount() - 1).toString();
			query.filter(CodeSystemVersionEntry.Expressions.parentBranchPaths(Collections.singleton(parentBranch)));
		}
		
		if (containsKey(OptionKey.VERSION_ID)) {
			query.filter(CodeSystemVersionEntry.Expressions.versionIds(getCollection(OptionKey.VERSION_ID, String.class)));
		}
		
		if (containsKey(OptionKey.PARENT_BRANCH_PATH)) {
			query.filter(CodeSystemVersionEntry.Expressions.parentBranchPaths(getCollection(OptionKey.PARENT_BRANCH_PATH, String.class)));
		}
		
		if (containsKey(OptionKey.EFFECTIVE_DATE)) {
			query.filter(CodeSystemVersionEntry.Expressions.effectiveDate(get(OptionKey.EFFECTIVE_DATE, Date.class)));
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

	@Override
	public String getOperation() {
		return Permission.BROWSE;
	}

}
