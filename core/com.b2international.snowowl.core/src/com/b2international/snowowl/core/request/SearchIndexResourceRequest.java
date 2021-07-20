/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * @since 5.11
 * @param <C> - the required execution context of this request
 * @param <B> - the return type
 * @param <D> - the document type to search for 
 */
public abstract class SearchIndexResourceRequest<C extends ServiceProvider, B, D> extends SearchResourceRequest<C, B> {

	private static final long serialVersionUID = 1L;

	/**
	 * Special field name for sorting based on the document score (relevance).
	 */
	public static final SortField SCORE = SortField.descending(SortBy.FIELD_SCORE);
	
	@Override
	protected final B doExecute(C context) throws IOException {
		final Searcher searcher = searcher(context);
		final Expression where = prepareQuery(context);
		
		ImmutableSet.Builder<String> additionalFieldsToLoad = ImmutableSet.builder();
		
		// configure additional fields to load when subsetting the response
		List<String> fields = fields();
		if (!fields().isEmpty()) {
			collectAdditionalFieldsToFetch(additionalFieldsToLoad);
			
			// in case of Revisions always include the ID field (if not requested) to avoid low-level error
			// after configuring the additional field inclusions
			if (Revision.class.isAssignableFrom(getFrom()) && !fields().contains(Revision.Fields.ID)) {
				additionalFieldsToLoad.add(Revision.Fields.ID);
			}
			
			final Set<String> additionalFields = additionalFieldsToLoad.build();
			
			if (!additionalFields.isEmpty()) {
				fields = Lists.newArrayList(fields());
				for (String additionalField : additionalFields) {
					if (!fields.contains(additionalField)) {
						fields.add(additionalField);
					}
				}
			}
		}
		
		
		final Hits<D> hits = searcher.search(Query.select(getSelect())
				.from(getFrom())
				.fields(fields)
				.where(where)
				.searchAfter(searchAfter())
				.limit(limit())
				.sortBy(querySortBy(context))
				.withScores(trackScores())
				.build());
		
		return toCollectionResource(context, hits);
	}
	
	/**
	 * Subclasses may override this method to provide additional fields to fetch from the underlying index, if those fields are necessary to complete
	 * the request. This method is only being called if there is at least client requested field. If there is none it will load the entire object and
	 * no need to configure additional fields.
	 * 
	 * @param additionalFieldsToLoad
	 */
	protected void collectAdditionalFieldsToFetch(ImmutableSet.Builder<String> additionalFieldsToLoad) {
	}

	/**
	 * Returns the default {@link Searcher} attached to the given context that can search {@link #getDocumentType() document}s. Subclasses may override this if they would like to use a different
	 * searcher service.
	 * 
	 * @param context
	 * @return
	 */
	protected Searcher searcher(C context) {
		if (Revision.class.isAssignableFrom(getFrom())) {
			return context.service(RevisionSearcher.class);
		} else {
			return context.service(Searcher.class);
		}
	}

	/**
	 * Prepares the search query with the clauses, filters specified in this request. 
	 * @param context - the context that can be used to prepare the query 
	 */
	protected abstract Expression prepareQuery(C context);
	
	/**
	 * Subclasses may override to configure scoring. By default disabled score tracking in search requests.
	 * @return whether the search should compute scores for the prepared query or not
	 */
	protected boolean trackScores() {
		return false;
	}
	
	/**
	 * @return the view class to return as matches
	 */
	protected Class<D> getSelect() {
		return getDocumentType();
	}
	
	/**
	 * @return the document type from which the hits will be returned 
	 */
	protected Class<?> getFrom() {
		return getSelect();
	}
	
	/**
	 * @return the type of documents to search for.
	 * @deprecated - will be replaced by {@link #getSelect()} and {@link #getFrom()} in 8.0
	 */
	protected Class<D> getDocumentType() {
		throw new UnsupportedOperationException("No longer supported, use getSelect() and getFrom() methods instead.");
	}
	
	/**
	 * Converts the document hits to a API response of {@link CollectionResource} subtype.
	 * @param context - the context that can be used to convert the hits
	 * @param hits - the hits to convert to API response
	 * @return
	 */
	protected abstract B toCollectionResource(C context, Hits<D> hits);

	/**
	 * @param context
	 * @return the raw expression configured with all specified filters.
	 */
	public final Expression toRawQuery(C context) {
		try {
			return prepareQuery(context);
		} catch (NoResultException e) {
			return Expressions.matchNone();
		}
	}
	
}
