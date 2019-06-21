/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.b2international.index.Hits;
import com.b2international.index.Scroll;
import com.b2international.index.Searcher;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.request.SearchResourceRequest;

/**
 * @since 5.11
 * @param <C> - the required execution context of this request
 * @param <B> - the return type
 * @param <D> - the document type to search for 
 */
public abstract class SearchIndexResourceRequest<C extends ServiceProvider, B, D> extends SearchResourceRequest<C, B> {

	/**
	 * Special field name for sorting based on the document's natural occurrence (document order). 
	 */
	public static final SortField DOC_ID = SortField.ascending(DocumentMapping._ID);
	
	/**
	 * Special field name for sorting based on the document score (relevance).
	 */
	public static final SortField SCORE = SortField.descending(SortBy.FIELD_SCORE);
	
	@Override
	protected final B doExecute(C context) throws IOException {
		final Class<D> docType = getDocumentType();
		final Searcher searcher = searcher(context);
		final Hits<D> hits;
		if (isScrolled()) {
			hits = searcher.scroll(new Scroll<>(docType, docType, fields(), scrollId(), scrollKeepAlive()));
		} else {
			final Expression where = prepareQuery(context);
			hits = searcher.search(Query.select(docType)
					.fields(fields())
					.where(where)
					.scroll(scrollKeepAlive())
					.searchAfter(searchAfter())
					.limit(limit())
					.sortBy(sortBy())
					.withScores(trackScores())
					.build());
		}
		
		return toCollectionResource(context, hits);
	}
	
	/**
	 * Returns the default {@link Searcher} attached to the given context that can search {@link #getDocumentType() document}s. Subclasses may override this if they would like to use a different
	 * searcher service.
	 * 
	 * @param context
	 * @return
	 */
	protected Searcher searcher(C context) {
		if (Revision.class.isAssignableFrom(getDocumentType())) {
			return context.service(RevisionSearcher.class);
		} else {
			return context.service(Searcher.class);
		}
	}

	protected final ExpressionBuilder addIdFilter(ExpressionBuilder queryBuilder, Function<Collection<String>, Expression> expressionFactory) {
		return applyIdFilter(queryBuilder, (qb, ids) -> qb.filter(expressionFactory.apply(ids)));
	}
	
	protected final SortBy sortBy() {
		if (containsKey(OptionKey.SORT_BY)) {
			List<Sort> sorts = getList(OptionKey.SORT_BY, Sort.class);
			if (!sorts.isEmpty()) {
				SortBy.Builder sortBuilder = SortBy.builder();
				for (Sort sort : sorts) {
					if (sort instanceof SortField) {
						SortField sortField = (SortField) sort;
						sortBuilder.sortByField(sortField.getField(), sortField.isAscending() ? Order.ASC : Order.DESC);
					} else if (sort instanceof SortScript) {
						SortScript sortScript = (SortScript) sort;
						sortBuilder.sortByScript(sortScript.getScript(), sortScript.getArguments(), sortScript.isAscending() ? Order.ASC : Order.DESC);
					} else {
						throw new UnsupportedOperationException("Cannot handle sort type " + sort);
					}
				}
				return sortBuilder.build();
			}
		}		
		return SortBy.DOC_ID;
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
	 * Returns the type of documents to search for.
	 * @return
	 */
	protected abstract Class<D> getDocumentType();
	
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
		return prepareQuery(context);
	}
	
}
