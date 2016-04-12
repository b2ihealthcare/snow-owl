/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.queries.ChainedFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TotalHitCountCollector;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

/**
 * @since 4.5
 */
public abstract class SearchRequest<B> extends BaseResourceRequest<BranchContext, B> {

	enum OptionKey {
		EXPAND
	}
	
	@Min(0)
	private int offset;
	
	@Min(0)
	private int limit;
	
	@NotNull
	private Options options;
	
	@NotNull
	private Collection<String> componentIds;
	
	protected SearchRequest() {}
	
	void setLimit(int limit) {
		this.limit = limit;
	}
	
	void setOffset(int offset) {
		this.offset = offset;
	}
	
	void setOptions(Options options) {
		this.options = options;
	}
	
	void setComponentIds(Collection<String> componentIds) {
		this.componentIds = componentIds;
	}
	
	protected final int offset() {
		return offset;
	}
	
	protected final int limit() {
		return limit;
	}
	
	protected final Options options() {
		return options;
	}
	
	protected final boolean containsKey(Enum<?> key) {
		return options.containsKey(key.name());
	}
	
	protected final Object get(Enum<?> key) {
		return options.get(key.name());
	}

	protected final <T> T get(Enum<?> key, Class<T> expectedType) {
		return options.get(key.name(), expectedType);
	}

	protected final boolean getBoolean(Enum<?> key) {
		return options.getBoolean(key.name());
	}

	protected final String getString(Enum<?> key) {
		return options.getString(key.name());
	}

	protected final <T> Collection<T> getCollection(Enum<?> key, Class<T> type) {
		return options.getCollection(key.name(), type);
	}
	
	protected final <T> List<T> getList(Enum<?> key, Class<T> type) {
		return options.getList(key.name(), type);
	}
	
	protected final Options getOptions(Enum<?> key) {
		return options.getOptions(key.name());
	}
	
	protected final Collection<String> componentIds() {
		return componentIds;
	}
	
	protected Filter createComponentIdFilter() {
		return Mappings.id().createTermsFilter(componentIds);
	}
	
	protected Query createConstantScoreQuery(Query query) {
		return (query instanceof ConstantScoreQuery) ? query : new ConstantScoreQuery(query);
	}

	protected void addFilterClause(final BooleanFilter target, final Filter filter, final Occur occur) {
		target.add(checkNotNull(filter, "Filter clause to add was null"), occur);
	}
	
	protected void addFilterClause(final List<Filter> target, final Filter filter) {
		target.add(checkNotNull(filter, "Filter clause to add was null"));
	}
	
	protected Query createFilteredQuery(final Query query, final BooleanFilter filter) {
		if (!filter.clauses().isEmpty()) {
			return new FilteredQuery(query, filter);
		} else {
			return query;
		}
	}
	
	protected Query createFilteredQuery(final Query query, final List<Filter> filters, final List<Integer> ops) {
		if (!filters.isEmpty()) {
			return new FilteredQuery(query, new ChainedFilter(Iterables.toArray(filters, Filter.class), Ints.toArray(ops)));
		} else {
			return query;
		}
	}
	
	protected int getTotalHits(final IndexSearcher searcher, final Query query) throws IOException {
		final TotalHitCountCollector totalCollector = new TotalHitCountCollector();
		searcher.search(createConstantScoreQuery(query), totalCollector);
		return totalCollector.getTotalHits();
	}
	
	protected int numDocsToRetrieve(final IndexSearcher searcher, final int totalHits) {
		return numDocsToRetrieve(searcher, offset(), limit(), totalHits);
	}
	
	protected int numDocsToRetrieve(final IndexSearcher searcher, final int offset, final int limit, final int totalHits) {
		return Ints.min(offset + limit, searcher.getIndexReader().maxDoc(), totalHits);
	}

	@Override
	public final B execute(BranchContext context) {
		try {
			return doExecute(context);
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Caught exception while executing search request.", e);
		}
	}

	protected abstract B doExecute(BranchContext context) throws IOException;
	
	@Override
	public String toString() {
		return String.format("{type:'%s', offset:%s, limit:%s, componentIds:%s, locales:%s, options:%s}", 
				getClass().getSimpleName(),
				offset,
				limit,
				formatStringList(componentIds),
				formatStringList(locales()),
				options);
	}

	protected void addComponentIdFilter(BooleanFilter filter) {
		if (!componentIds().isEmpty()) {
			addFilterClause(filter, createComponentIdFilter(), Occur.MUST);
		}		
	}

}
