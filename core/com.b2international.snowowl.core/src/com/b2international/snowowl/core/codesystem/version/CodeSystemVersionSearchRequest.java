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
package com.b2international.snowowl.core.codesystem.version;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.StringUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.codesystem.CodeSystemVersion;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.codesystem.CodeSystemVersions;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;

/**
 * @since 4.7
 */
final class CodeSystemVersionSearchRequest 
	extends SearchIndexResourceRequest<RepositoryContext, CodeSystemVersions, CodeSystemVersionEntry> 
	implements RepositoryAccessControl {

	private static final long serialVersionUID = 2L;

	private String versionId;
	private String parentBranchPath;
	
	/**
	 * @since 6.15
	 */
	public static enum OptionKey {
		
		/**
		 * Filter versions by code system short name.
		 */
		SHORT_NAME,
		
		/**
		 * Filter versions by created date starting from this value, inclusive.
		 */
		CREATED_AT_START,
		
		/**
		 * Filter versions by created date ending with this value, inclusive.
		 */
		CREATED_AT_END,
		
		/**
		 * Filter versions by effective date starting with this value, inclusive.
		 */
		EFFECTIVE_TIME_START,
		
		/**
		 * Filter versions by effective date ending with this value, inclusive.
		 */
		EFFECTIVE_TIME_END,
	}
	
	CodeSystemVersionSearchRequest() { }

	void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	
	void setParentBranchPath(String parentBranchPath) {
		this.parentBranchPath = parentBranchPath;
	}
	
	@Override
	protected Expression prepareQuery(RepositoryContext context) {
		final ExpressionBuilder query = Expressions.builder();

		if (containsKey(OptionKey.SHORT_NAME)) {
			final Collection<String> codeSystemShortNames = getCollection(OptionKey.SHORT_NAME, String.class);
			query.filter(CodeSystemVersionEntry.Expressions.shortNames(codeSystemShortNames));
		}
		
		if (!StringUtils.isEmpty(versionId)) {
			query.filter(CodeSystemVersionEntry.Expressions.versionId(versionId));
		}

		if (containsKey(OptionKey.EFFECTIVE_TIME_START) || containsKey(OptionKey.EFFECTIVE_TIME_END)) {
			final long from = containsKey(OptionKey.EFFECTIVE_TIME_START) ? get(OptionKey.EFFECTIVE_TIME_START, Long.class) : 0;
			final long to = containsKey(OptionKey.EFFECTIVE_TIME_END) ? get(OptionKey.EFFECTIVE_TIME_END, Long.class) : Long.MAX_VALUE;
			query.filter(CodeSystemVersionEntry.Expressions.effectiveDate(from, to));
		}
		
		if (containsKey(OptionKey.EFFECTIVE_TIME_START) || containsKey(OptionKey.EFFECTIVE_TIME_END)) {
			
		}
		
		if (!StringUtils.isEmpty(parentBranchPath)) {
			query.filter(CodeSystemVersionEntry.Expressions.parentBranchPath(parentBranchPath));
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
		return new CodeSystemVersions(toResource(hits), hits.getSearchAfter(), limit(), hits.getTotal());
	}
	
	private List<CodeSystemVersion> toResource(Hits<CodeSystemVersionEntry> hits) {
		return hits.stream().map(this::toResource).collect(Collectors.toList());
	}
	
	private CodeSystemVersion toResource(CodeSystemVersionEntry input) {
		CodeSystemVersion version = new CodeSystemVersion();
		version.setId(input.getId()); // TODO replace ID with URI in 8.0
		version.setVersion(input.getVersionId());
		version.setDescription(input.getDescription());
		version.setEffectiveDate(EffectiveTimes.format(input.getEffectiveDate(), DateFormats.SHORT));
		version.setImportDate(toDate(input.getImportDate()));
		version.setLastModificationDate(toDate(input.getLatestUpdateDate()));
		version.setPath(input.getPath());
		version.setUri(input.getCodeSystemURI());
		return version;
	}
	
	private static Date toDate(final long timeStamp) {
		return timeStamp >= 0L ? new Date(timeStamp) : null;
	}

	@Override
	protected CodeSystemVersions createEmptyResult(int limit) {
		return new CodeSystemVersions(Collections.emptyList(), null, limit, 0);
	}

	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}

}
