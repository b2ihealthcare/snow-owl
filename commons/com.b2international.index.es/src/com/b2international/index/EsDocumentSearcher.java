/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.b2international.index.admin.EsIndexAdmin;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.EsQueryBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.MultiSortBy;
import com.b2international.index.query.SortBy.SortByField;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Primitives;

/**
 * @since 5.10
 */
public class EsDocumentSearcher implements Searcher {

	private final EsIndexAdmin admin;
	private final ObjectMapper mapper;
	private final int resultWindow;

	public EsDocumentSearcher(EsIndexAdmin admin, ObjectMapper mapper) {
		this.admin = admin;
		this.mapper = mapper;
		this.resultWindow = IndexClientFactory.DEFAULT_RESULT_WINDOW;
	}

	@Override
	public void close() throws Exception {
		// nothing to do
	}

	@Override
	public <T> T get(Class<T> type, String key) throws IOException {
		final DocumentMapping mapping = admin.mappings().getMapping(type);
		final GetResponse response = admin.client()
				.prepareGet(admin.name(), mapping.typeAsString(), key)
				.setRouting(mapping.typeAsString())
				.setFetchSource(true)
				.get();
		if (response.isExists()) {
			return mapper.readValue(response.getSourceAsBytes(), type);
		} else {
			return null;
		}
	}

	@Override
	public <T> Hits<T> search(Query<T> query) throws IOException {
		final Class<T> select = query.getSelect();
		final Class<?> from = query.getFrom();

		final Client client = admin.client();
		final DocumentMapping mapping = admin.mappings().getDocumentMapping(query);
		
		// Restrict variables to the theoretical maximum
		int offset = query.getOffset();
		final int limit = query.getLimit();
		final int toRead = Ints.min(limit, resultWindow);
		
		final TimeValue scrollTime = TimeValue.timeValueSeconds(60);
		
		final QueryBuilder esQuery = new EsQueryBuilder(mapping).build(query.getWhere());
		
		final SearchRequestBuilder req = client.prepareSearch(admin.name())
			.setRouting(mapping.typeAsString())
			.setTypes(mapping.typeAsString())
			.setSize(toRead)
			.setScroll(scrollTime)
			.setQuery(esQuery);
		
		if (query.getFields().isEmpty()) {
			req.setFetchSource(true);
		} else {
			if (query.isDocIdOnly()) {
				req.setNoFields();
			} else {
				req.setFetchSource(query.getFields().toArray(new String[]{}), null);
			}
		}
		
		addSort(req, query.getSortBy());
		
		SearchResponse response = req.get();
		final Builder<SearchHits> allHits = ImmutableList.builder();
		final int totalHits = (int) response.getHits().getTotalHits();

		int numDocsToFetch = query.getOffset() + query.getLimit();
		numDocsToFetch -= response.getHits().getHits().length;
		allHits.add(response.getHits());
		
		if (numDocsToFetch <= 0) {
			// quickly cancel the scroll to free up resources
			client.prepareClearScroll().addScrollId(response.getScrollId()).get();
		} else {
			while (numDocsToFetch > 0) {
				response = client.prepareSearchScroll(response.getScrollId())
					.setScroll(scrollTime)
					.get();
				final int fetchedHits = response.getHits().getHits().length;
				if (fetchedHits == 0) {
					break;
				}
				numDocsToFetch -= fetchedHits;
				allHits.add(response.getHits());
			}
		}
		
		final ImmutableList.Builder<T> result = ImmutableList.builder();
		
		final ObjectReader reader = select != from 
				? mapper.readerFor(select).without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) 
				: mapper.readerFor(select);
		
		int remainingLimit = query.getLimit();
		outer: for (SearchHits hits : allHits.build()) {
			for (SearchHit hit : hits) {
				if (offset != 0) {
					offset--;
					continue;
				}
				final T value;
				if (Primitives.isWrapperType(query.getSelect()) || String.class.isAssignableFrom(query.getSelect())) {
					if (query.isDocIdOnly()) {
						value = (T) hit.id();
					} else {
						value = (T) hit.getSource().get(Iterables.getOnlyElement(query.getFields()));
					}
				} else {
					value = reader.readValue(hit.source());
				}
				if (value instanceof WithId) {
					((WithId) value).set_id(hit.getId());
				}
				if (value instanceof WithScore) {
					((WithScore) value).setScore(Float.isNaN(hit.getScore()) ? 0.0f : hit.getScore());
				}
				result.add(value);
				remainingLimit--;
				if (remainingLimit == 0) {
					break outer;
				}
			}
		}
		return new Hits<T>(result.build(), offset, limit, totalHits);
	}

	private void addSort(SearchRequestBuilder req, SortBy sortBy) {
		final List<SortBy> items = newArrayList();

		// Unpack the top level multi-sort if present
		if (sortBy instanceof MultiSortBy) {
			items.addAll(((MultiSortBy) sortBy).getItems());
		} else {
			items.add(sortBy);
		}
		
		for (final SortByField item : Iterables.filter(items, SortByField.class)) {
            String field = item.getField();
            SortBy.Order order = item.getOrder();
            
			switch (field) {
            case SortBy.FIELD_SCORE:
                // XXX: default order for scores is *descending*
                req.addSort(SortBuilders.scoreSort().order(order == SortBy.Order.ASC ? SortOrder.ASC : SortOrder.DESC)); 
                break;
            default:
            	req.addSort(SortBuilders.fieldSort(field).order(order == SortBy.Order.ASC ? SortOrder.ASC : SortOrder.DESC));
            }
        }
	}

}
