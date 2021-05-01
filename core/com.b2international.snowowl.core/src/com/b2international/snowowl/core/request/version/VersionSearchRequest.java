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
package com.b2international.snowowl.core.request.version;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.core.version.Versions;

/**
 * @since 4.7
 */
public final class VersionSearchRequest 
	extends SearchIndexResourceRequest<ServiceProvider, Versions, VersionDocument> 
	implements RepositoryAccessControl {

	private static final long serialVersionUID = 3L;

	/**
	 * @since 6.15
	 */
	public static enum OptionKey {
		/**
		 * Filter versions by their tag.
		 */
		VERSION,
		
		/**
		 * Filter versions by associated resource.
		 */
		RESOURCE,
		
		/**
		 * Filter versions by effective date starting with this value, inclusive.
		 */
		EFFECTIVE_TIME_START,
		
		/**
		 * Filter versions by effective date ending with this value, inclusive.
		 */
		EFFECTIVE_TIME_END,
	}
	
	VersionSearchRequest() { }

	@Override
	protected Expression prepareQuery(ServiceProvider context) {
		final ExpressionBuilder query = Expressions.builder();

		if (containsKey(OptionKey.RESOURCE)) {
			final Collection<ResourceURI> resourceUris = getCollection(OptionKey.RESOURCE, ResourceURI.class);
			query.filter(VersionDocument.Expressions.resources(resourceUris));
		}
		
		if (containsKey(OptionKey.VERSION)) {
			final Collection<String> versions = getCollection(OptionKey.VERSION, String.class);
			query.filter(VersionDocument.Expressions.versions(versions));
		}

		if (containsKey(OptionKey.EFFECTIVE_TIME_START) || containsKey(OptionKey.EFFECTIVE_TIME_END)) {
			final long from = containsKey(OptionKey.EFFECTIVE_TIME_START) ? get(OptionKey.EFFECTIVE_TIME_START, Long.class) : 0;
			final long to = containsKey(OptionKey.EFFECTIVE_TIME_END) ? get(OptionKey.EFFECTIVE_TIME_END, Long.class) : Long.MAX_VALUE;
			query.filter(VersionDocument.Expressions.effectiveTime(from, to));
		}
		
		if (containsKey(OptionKey.EFFECTIVE_TIME_START) || containsKey(OptionKey.EFFECTIVE_TIME_END)) {
			
		}
		
		return query.build();
	}
	
	@Override
	protected Class<VersionDocument> getDocumentType() {
		return VersionDocument.class;
	}

	@Override
	protected Versions toCollectionResource(ServiceProvider context, Hits<VersionDocument> hits) {
		return new Versions(toResource(hits), hits.getSearchAfter(), limit(), hits.getTotal());
	}
	
	private List<Version> toResource(Hits<VersionDocument> hits) {
		return hits.stream().map(this::toResource).collect(Collectors.toList());
	}
	
	private Version toResource(VersionDocument doc) {
		Version version = new Version();
		version.setId(doc.getId());
		version.setVersion(doc.getVersion());
		version.setDescription(doc.getDescription());
		version.setEffectiveTime(doc.getEffectiveTimeAsLocalDate());
		version.setResource(doc.getResource());
		version.setBranchPath(doc.getBranchPath());
		return version;
	}
	
	@Override
	protected Versions createEmptyResult(int limit) {
		return new Versions(Collections.emptyList(), null, limit, 0);
	}

	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}

}
