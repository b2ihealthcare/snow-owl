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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.b2international.index.admin.EsIndexAdmin;
import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.aggregations.Bucket;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.EsQueryBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.MultiSortBy;
import com.b2international.index.query.SortBy.SortByField;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
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
		this.resultWindow = Integer.parseInt((String) admin.settings().get(IndexClientFactory.RESULT_WINDOW_KEY));
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
			final byte[] bytes = BytesReference.toBytes(response.getSourceAsBytesRef());
			return mapper.readValue(bytes, 0, bytes.length, type);
		} else {
			return null;
		}
	}

	@Override
	public <T> Hits<T> search(Query<T> query) throws IOException {
		final Client client = admin.client();
		final DocumentMapping mapping = admin.mappings().getDocumentMapping(query);
		
		// Restrict variables to the theoretical maximum
		int offset = query.getOffset();
		final int limit = query.getLimit();
		final int toRead = Ints.min(limit, resultWindow);
		
		final TimeValue scrollTime = TimeValue.timeValueSeconds(60);
		
		final EsQueryBuilder esQueryBuilder = new EsQueryBuilder(mapping);
		final QueryBuilder esQuery = esQueryBuilder.build(query.getWhere());
		
		final SearchRequestBuilder req = client.prepareSearch(admin.name())
			.setRouting(mapping.typeAsString())
			.setTypes(mapping.typeAsString())
			.setSize(toRead)
			.setScroll(scrollTime)
			.setQuery(esQuery)
			.setTrackScores(esQueryBuilder.needsScoring());
		
		if (query.getFields().isEmpty()) {
			req.setFetchSource(true);
		} else {
			if (query.isDocIdOnly()) {
				req.setFetchSource(false);
			} else {
				req.setFetchSource(query.getFields().toArray(new String[]{}), null);
			}
		}
		
		addSort(req, query.getSortBy());
	
		SearchResponse response = null; 
		try {
			response = req.get();
		} catch (Exception e) {
			admin.log().error("Couldn't execute query", e);
			throw new IndexException("Couldn't execute query: " + e.getMessage(), null);
		}
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
		
		final ObjectReader reader = getResultObjectReader(query.getSelect(), query.getFrom());
		
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
						value = (T) hit.getId();
					} else {
						value = (T) hit.getSource().get(Iterables.getOnlyElement(query.getFields()));
					}
				} else {
					final byte[] bytes = BytesReference.toBytes(hit.getSourceRef());
					value = reader.readValue(bytes, 0, bytes.length);
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

	private ObjectReader getResultObjectReader(final Class<?> select, final Class<?> from) {
		return select != from 
				? mapper.readerFor(select).without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) 
				: mapper.readerFor(select);
	}

	private void addSort(SearchRequestBuilder req, SortBy sortBy) {
		final List<SortBy> items = newArrayList();

		// Unpack the top level multi-sort if present
		if (sortBy instanceof MultiSortBy) {
			items.addAll(((MultiSortBy) sortBy).getItems());
		} else {
			items.add(sortBy);
		}
		
		boolean sortById = false;
		
		for (final SortByField item : Iterables.filter(items, SortByField.class)) {
            String field = item.getField();
            SortBy.Order order = item.getOrder();
            
			switch (field) {
            case SortBy.FIELD_SCORE:
                // XXX: default order for scores is *descending*
                req.addSort(SortBuilders.scoreSort().order(order == SortBy.Order.ASC ? SortOrder.ASC : SortOrder.DESC)); 
                break;
            case DocumentMapping._ID: //$FALL-THROUGH$
            	sortById = true;
            	field = DocumentMapping._UID;
            default:
            	req.addSort(SortBuilders.fieldSort(field).order(order == SortBy.Order.ASC ? SortOrder.ASC : SortOrder.DESC));
            }
        }
		
		// add _id field as tiebreaker if not defined in the original SortBy
		if (!sortById) {
			req.addSort(SortBuilders.fieldSort(DocumentMapping._UID).order(SortOrder.DESC));
		}
	}
	
	@Override
	public <T> Aggregation<T> aggregate(AggregationBuilder<T> aggregation) throws IOException {
		final String aggregationName = aggregation.getName();
		final Client client = admin.client();
		final DocumentMapping mapping = admin.mappings().getMapping(aggregation.getFrom());
		
		final EsQueryBuilder esQueryBuilder = new EsQueryBuilder(mapping);
		final QueryBuilder esQuery = esQueryBuilder.build(aggregation.getQuery());
		
		final SearchRequestBuilder req = client.prepareSearch(admin.name())
			.addAggregation(toEsAggregation(mapping, aggregation))
			.setRouting(mapping.typeAsString())
			.setTypes(mapping.typeAsString())
			.setQuery(esQuery)
			.setSize(0)
			.setTrackScores(false);
		
		SearchResponse response = null; 
		try {
			response = req.get();
		} catch (Exception e) {
			admin.log().error("Couldn't execute aggregation", e);
			throw new IndexException("Couldn't execute aggregation: " + e.getMessage(), null);
		}
		
		final ObjectReader reader = getResultObjectReader(aggregation.getFrom(), aggregation.getFrom());
		
		ImmutableMap.Builder<Object, Bucket<T>> buckets = ImmutableMap.builder();
		Terms aggregationResult = response.getAggregations().<Terms>get(aggregationName);
		for (org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket bucket : aggregationResult.getBuckets()) {
			final TopHits topHits = bucket.getAggregations().get(topHitsAggName(aggregation));
			
			ImmutableList.Builder<T> hits = ImmutableList.builder();
			
			final SearchHits topSearchHits = topHits.getHits();
			for (SearchHit hit : topSearchHits) {
				final byte[] bytes = BytesReference.toBytes(hit.getSourceRef());
				T value = reader.readValue(bytes, 0, bytes.length);
				hits.add(value);
			}
			
			buckets.put(bucket.getKey(), new Bucket<>(bucket.getKey(), new Hits<>(hits.build(), 0, aggregation.getBucketHitsLimit(), (int) topSearchHits.getTotalHits())));
		}
		
		return new Aggregation<>(aggregationName, buckets.build());
	}

	private org.elasticsearch.search.aggregations.AggregationBuilder toEsAggregation(DocumentMapping mapping, AggregationBuilder<?> aggregation) {
		final TermsAggregationBuilder termsAgg = AggregationBuilders
				.terms(aggregation.getName())
				.minDocCount(aggregation.getMinBucketSize())
				.size(Integer.MAX_VALUE);
		boolean isFieldAgg = !Strings.isNullOrEmpty(aggregation.getField());
		boolean isScriptAgg = !Strings.isNullOrEmpty(aggregation.getScript());
		if (isFieldAgg) {
			checkArgument(!isScriptAgg, "Specify either field or script parameter, not both");
			termsAgg.field(aggregation.getField());
		} else if (isScriptAgg) {
			final String rawScript = mapping.getScript(aggregation.getScript()).script();
			termsAgg.script(new org.elasticsearch.script.Script(ScriptType.INLINE, "painless", rawScript, Collections.emptyMap()));
		} else {
			throw new IllegalArgumentException("Specify either field or script parameter");
		}
		
		// add top hits agg to get the top N items for each bucket
		termsAgg.subAggregation(AggregationBuilders.topHits(topHitsAggName(aggregation))
				.fetchSource(true)
				.size(aggregation.getBucketHitsLimit()));
		
		return termsAgg;
	}

	private String topHitsAggName(AggregationBuilder<?> aggregation) {
		return aggregation.getName() + "-top-hits";
	}

}
