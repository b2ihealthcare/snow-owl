/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.resource;

import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchPageableCollectionResourceRequestBuilder;
import com.b2international.snowowl.core.request.resource.BaseResourceSearchRequest.OptionKey;
import com.b2international.snowowl.core.request.search.TermFilter;

/**
 * @since 8.0
 */
public abstract class BaseResourceSearchRequestBuilder<RB extends BaseResourceSearchRequestBuilder<RB, R>, R extends PageableCollectionResource<?>>
		extends SearchPageableCollectionResourceRequestBuilder<RB, RepositoryContext, R>
		implements ResourceRepositoryRequestBuilder<R> {

	public final RB filterByUrl(String url) {
		return addOption(OptionKey.URL, url);
	}
	
	public final RB filterByUrls(Iterable<String> urls) {
		return addOption(OptionKey.URL, urls);
	}

	/**
	 * Filter matches by a {@link TermFilter} configuration
	 * 
	 * @param termFilter - configuration
	 * @return this builder
	 * @see TermFilter hierarchy
	 */
	public final RB filterByTitle(final TermFilter termFilter) {
		return addOption(OptionKey.TITLE, termFilter);
	}
	
	/**
	 * "Smart" search by title (taking prefixes, stemming, etc. into account)
	 * 
	 * @param title - the title to search for
	 * @return this builder
	 */
	public final RB filterByTitle(final String title) {
		return filterByTitle(title != null ? TermFilter.match().term(title).build() : null);
	}
	
	/**
	 * Exact case sensitive match on title
	 * 
	 * @param exactTitle - the title to search for
	 * @return this builder
	 */
	public final RB filterByExactTitle(final String exactTitle) {
		return filterByTitle(exactTitle != null ? TermFilter.exact().term(exactTitle).caseSensitive(true).build() : null);
	}
	
	/**
	 * Exact case insensitive ASCII folded match on title
	 * 
	 * @param exactTitle - the title to search for
	 * @return this builder
	 */
	public final RB filterByExactTitleIgnoreCase(final String exactTitle) {
		return filterByTitle(exactTitle != null ? TermFilter.exact().term(exactTitle).caseSensitive(false).build() : null);
	}
	
	/**
	 * Filters matches by their title (exact match).
	 * 
	 * @param titles - at least one of these titles match
	 * @return this builder
	 */
	public final RB filterByTitleExact(Iterable<String> titles) {
		return addOption(OptionKey.TITLE_EXACT, titles);
	}
	
	public final RB filterByBundleId(String bundleId) {
		return addOption(OptionKey.BUNDLE_ID, bundleId);
	}

	public final RB filterByBundleIds(Iterable<String> bundleIds) {
		return addOption(OptionKey.BUNDLE_ID, bundleIds);
	}
	
	public final RB filterByBundleAncestorId(String bundleAncestorId) {
		return addOption(OptionKey.BUNDLE_ANCESTOR_ID, bundleAncestorId);
	}
	
	public final RB filterByBundleAncestorIds(Iterable<String> bundleAncestorIds) {
		return addOption(OptionKey.BUNDLE_ANCESTOR_ID, bundleAncestorIds);
	}

	public final RB filterByOid(String oid) {
		return addOption(OptionKey.OID, oid);
	}
	
	public final RB filterByOids(Iterable<String> oids) {
		return addOption(OptionKey.OID, oids);
	}

	public final RB filterByStatus(String status) {
		return addOption(OptionKey.STATUS, status);
	}

	public final RB filterByStatus(Iterable<String> status) {
		return addOption(OptionKey.STATUS, status);
	}
	
	/**
	 * 
	 * 
	 * @param settings
	 * @return
	 */
	public final RB filterBySettings(Iterable<String> settings) {
		return addOption(OptionKey.SETTINGS, settings);
	}	

	public final RB filterByOwner(Iterable<String> owner) {
		return addOption(OptionKey.OWNER, owner);
	}
	
	/**
	 * Internal API. Allows plug-ins to search for hidden resources.
	 * 
	 * @param hidden
	 * @return
	 * @since 9.0
	 */
	public final RB filterByHidden(Boolean hidden) {
		return addOption(OptionKey.HIDDEN, hidden);
	}
}
