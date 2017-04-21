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
import com.google.common.collect.Iterables;
import com.google.common.primitives.Primitives;

/**
 * @since 5.10
 */
public class EsDocumentSearcher implements Searcher {

	private final EsIndexAdmin admin;
	private final ObjectMapper mapper;

	public EsDocumentSearcher(EsIndexAdmin admin, ObjectMapper mapper) {
		this.admin = admin;
		this.mapper = mapper;
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
		final SearchRequestBuilder req = client.prepareSearch(admin.name())
			.setRouting(mapping.typeAsString())
			.setTypes(mapping.typeAsString())
			.setFrom(query.getOffset())
			.setSize(query.getLimit())
			.setQuery(new EsQueryBuilder(mapping).build(query.getWhere()));
		
		if (query.getFields().isEmpty()) {
			req.setFetchSource(true);
		} else {
			req.setFetchSource(query.getFields().toArray(new String[]{}), null);
		}
		
		addSort(req, query.getSortBy());
		
		final SearchResponse response = req.get();
		final SearchHits hits = response.getHits();
		final ImmutableList.Builder<T> result = ImmutableList.builder();
		
		final ObjectReader reader = select != from 
				? mapper.reader(select).without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) 
				: mapper.reader(select);
		
		for (SearchHit hit : hits) {
			final T value;
			if (Primitives.isWrapperType(query.getSelect()) || String.class.isAssignableFrom(query.getSelect())) {
				value = (T) hit.getSource().get(Iterables.getOnlyElement(query.getFields()));
			} else {
				value = reader.readValue(hit.source());
			}
			if (value instanceof WithId) {
				((WithId) value).set_id(hit.getId());
			}
			if (value instanceof WithScore) {
				((WithScore) value).setScore(hit.getScore());
			}
			result.add(value);
		}
		return new Hits<T>(result.build(), query.getOffset(), query.getLimit(), (int) hits.getTotalHits());
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
