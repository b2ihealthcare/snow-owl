/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.b2international.collections.PrimitiveCollection;
import com.b2international.index.admin.EsIndexAdmin;
import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.aggregations.Bucket;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.EsQueryBuilder;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.MultiSortBy;
import com.b2international.index.query.SortBy.Order;
import com.b2international.index.query.SortBy.SortByField;
import com.b2international.index.query.SortBy.SortByScript;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

/**
 * @since 5.10
 */
public class EsDocumentSearcher implements Searcher {

	private static final String[] EXCLUDED_SOURCE_FIELDS = { DocumentMapping._HASH };
	
	private final EsIndexAdmin admin;
	private final ObjectMapper mapper;
	private final int resultWindow;

	public EsDocumentSearcher(EsIndexAdmin admin, ObjectMapper mapper) {
		this.admin = admin;
		this.mapper = mapper;
		this.resultWindow = Integer.parseInt((String) admin.settings().get(IndexClientFactory.RESULT_WINDOW_KEY));
	}

	@Override
	public <T> T get(Class<T> type, String key) throws IOException {
		checkArgument(!Strings.isNullOrEmpty(key), "Key cannot be empty");
		final DocumentMapping mapping = admin.mappings().getMapping(type);
		final GetResponse response = admin.client()
				.prepareGet(admin.getTypeIndex(mapping), mapping.typeAsString(), key)
				.setFetchSource(true)
				.get();
		if (response.isExists()) {
			final byte[] bytes = response.getSourceAsBytes();
			return mapper.readValue(bytes, 0, bytes.length, type);
		} else {
			return null;
		}
	}
	
	@Override
	public <T> Iterable<T> get(Class<T> type, Iterable<String> keys) throws IOException {
		return search(Query.select(type).where(Expressions.matchAny(DocumentMapping._ID, keys)).limit(Iterables.size(keys)).build());
	}

	@Override
	public <T> Hits<T> search(Query<T> query) throws IOException {
		final Client client = admin.client();
		final DocumentMapping mapping = admin.mappings().getDocumentMapping(query);
		
		// Restrict variables to the theoretical maximum
		final int limit = query.getLimit();
		final int toRead = Ints.min(limit, resultWindow);
		
		final EsQueryBuilder esQueryBuilder = new EsQueryBuilder(mapping);
		final QueryBuilder esQuery = esQueryBuilder.build(query.getWhere());
		
		final SearchRequestBuilder req = client.prepareSearch(admin.getTypeIndex(mapping))
			.setTypes(mapping.typeAsString())
			.setSize(toRead)
			.setQuery(esQuery)
			.setTrackScores(esQueryBuilder.needsScoring());
		
		// field selection
		final boolean fetchSource = applySourceFiltering(query, mapping, req);
		
		// this won't load fields like _parent, _routing, _uid at all
		// and _id in cases where we explicitly require the _source
		// ES internals require loading the _id field when we require the _source
		if (fetchSource	|| query.isDocIdOnly()) {
			req.storedFields("_id");
		} else {
			req.storedFields("_none_");
		}
		
		// sorting
		addSort(mapping, req, query.getSortBy());
		
		// scrolling
		final TimeValue scrollTime = TimeValue.timeValueSeconds(60);
		final boolean isLocalScroll = limit > resultWindow;
		final boolean isScrolled = !Strings.isNullOrEmpty(query.getScrollKeepAlive());
		final boolean isLiveScrolled = query.getSearchAfter() != null;
		if (isLocalScroll) {
			checkArgument(!isScrolled, "Cannot fetch more than '%s' items when scrolling is specified. You requested '%s' items.", resultWindow, limit);
			checkArgument(!isLiveScrolled, "Cannot use search after when requesting more number of items (%s) than the max result window (%s).", limit, resultWindow);
			req.setScroll(scrollTime);
		} else if (isScrolled) {
			checkArgument(!isLiveScrolled, "Cannot scroll and live scroll at the same time");
			req.setScroll(query.getScrollKeepAlive());
		} else if (isLiveScrolled) {
			checkArgument(!isScrolled, "Cannot scroll and live scroll at the same time");
			req.searchAfter(query.getSearchAfter());
		}
		
		// disable explain explicitly, just in case
		req.setExplain(false);
		// disable version field explicitly, just in case
		req.setVersion(false);
		
		// fetch phase
		SearchResponse response = null; 
		try {
			response = req.get();
		} catch (Exception e) {
			admin.log().error("Couldn't execute query", e);
			throw new IndexException("Couldn't execute query: " + e.getMessage(), null);
		}
		
		final int totalHits = (int) response.getHits().getTotalHits();
		int numDocsToFetch = Math.min(limit, totalHits) - response.getHits().getHits().length;
		
		final Builder<SearchHit> allHits = ImmutableList.builder();
		allHits.add(response.getHits().getHits());

		while (isLocalScroll && numDocsToFetch > 0) {
			response = client.prepareSearchScroll(response.getScrollId()).setScroll(scrollTime).get();
			int fetchedDocs = response.getHits().getHits().length;
			if (fetchedDocs == 0) {
				break;
			}
			numDocsToFetch -= fetchedDocs;
			allHits.add(response.getHits().getHits());
		}
		
		// clear the custom local scroll
		if (isLocalScroll) {
			client.prepareClearScroll().addScrollId(response.getScrollId()).get();
		}
		
		final Class<T> select = query.getSelect();
		final Class<?> from = query.getFrom();
		
		return toHits(select, from, query.getFields(), fetchSource, limit, totalHits, response.getScrollId(), query.getSortBy(), allHits.build());
	}

	private <T> boolean applySourceFiltering(Query<T> query, final DocumentMapping mapping, final SearchRequestBuilder req) {
		// No specific fields requested? Use _source to retrieve all of them
		if (query.getFields().isEmpty()) {
			req.setFetchSource(null, EXCLUDED_SOURCE_FIELDS);
			return true;
		}
		
		// Only IDs required? _source is not needed at all
		if (query.isDocIdOnly()) {
			req.setFetchSource(false);
			return false;
		}
		
		// Any field requested that can only retrieved from _source? Use source filtering
		if (requiresDocumentSourceField(mapping, query.getFields())) {
			req.setFetchSource(Iterables.toArray(query.getFields(), String.class), EXCLUDED_SOURCE_FIELDS);
			return true;
		}
		
		// Use docValues otherwise for field retrieval
		query.getFields().stream().forEach(req::addDocValueField);
		req.setFetchSource(false);
		return false;
	}

	private boolean requiresDocumentSourceField(DocumentMapping mapping, List<String> fields) {
		return fields
			.stream()
			.filter(field -> {
				Class<?> fieldType = mapping.getFieldType(field);
				return mapping.isText(field)
						|| Iterable.class.isAssignableFrom(fieldType) 
						|| PrimitiveCollection.class.isAssignableFrom(fieldType) 
						|| fieldType.getClass().isArray();
			})
			.findFirst()
			.isPresent();
	}

	@Override
	public <T> Hits<T> scroll(Scroll<T> scroll) throws IOException {
		final SearchResponse response = admin.client()
				.prepareSearchScroll(scroll.getScrollId())
				.setScroll(scroll.getKeepAlive())
				.get();
		
		final DocumentMapping mapping = admin.mappings().getMapping(scroll.getFrom());
		final boolean fetchSource = scroll.getFields().isEmpty() || requiresDocumentSourceField(mapping, scroll.getFields());
		return toHits(scroll.getSelect(), scroll.getFrom(), scroll.getFields(), fetchSource, response.getHits().getHits().length, (int) response.getHits().getTotalHits(), response.getScrollId(), null, response.getHits());
	}
	
	@Override
	public void cancelScroll(String scrollId) {
		admin.client().prepareClearScroll().addScrollId(scrollId).get();
	}
	
	private <T> Hits<T> toHits(
			Class<T> select, 
			Class<?> from, 
			final List<String> fields, 
			final boolean fetchSource,
			final int limit, 
			final int totalHits, 
			final String scrollId,
			final SortBy sortBy,
			final Iterable<SearchHit> hits) throws IOException {
		final HitConverter<T> hitConverter = HitConverter.getConverter(mapper, select, from, fetchSource, fields);
		Object[] searchAfterSortValues = null;
		final ImmutableList.Builder<T> result = ImmutableList.builder();
		for (Iterator<SearchHit> iterator = hits.iterator(); iterator.hasNext();) {
			SearchHit hit = iterator.next();
			// if this was the last value then collect the sort values for searchAfter
			final T value = hitConverter.convert(hit);
			if (value instanceof WithId) {
				((WithId) value).set_id(hit.getId());
			}
			if (value instanceof WithScore) {
				((WithScore) value).setScore(Float.isNaN(hit.getScore()) ? 0.0f : hit.getScore());
			}
			result.add(value);
			if (!iterator.hasNext()) {
				searchAfterSortValues = hit.getSortValues();
			}
		}
		return new Hits<T>(result.build(), scrollId, searchAfterSortValues, limit, totalHits);
	}

	private void addSort(DocumentMapping mapping, SearchRequestBuilder req, SortBy sortBy) {
		for (final SortBy item : getSortFields(sortBy)) {
			if (item instanceof SortByField) {
				SortByField sortByField = (SortByField) item;
				String field = sortByField.getField();
				SortBy.Order order = sortByField.getOrder();
				SortOrder sortOrder = order == SortBy.Order.ASC ? SortOrder.ASC : SortOrder.DESC;
				
				switch (field) {
				case SortBy.FIELD_SCORE:
					// XXX: default order for scores is *descending*
					req.addSort(SortBuilders.scoreSort().order(sortOrder)); 
					break;
				case DocumentMapping._ID: //$FALL-THROUGH$
					field = DocumentMapping._ID;
				default:
					req.addSort(SortBuilders.fieldSort(field).order(sortOrder));
				}
			} else if (item instanceof SortByScript) {
				SortByScript sortByScript = (SortByScript) item;
				SortBy.Order order = sortByScript.getOrder();
				String script = sortByScript.getName();
				SortOrder sortOrder = order == SortBy.Order.ASC ? SortOrder.ASC : SortOrder.DESC;
				
				// if this is a named script then get it from the current mapping
				if (mapping.getScript(script) != null) {
					script = mapping.getScript(script).script();
				}
				
				Map<String, Object> arguments = sortByScript.getArguments();
				
				req.addSort(SortBuilders.scriptSort(new org.elasticsearch.script.Script(ScriptType.INLINE, "painless", script, arguments), ScriptSortType.STRING)
						.order(sortOrder));
				
			} else {
				throw new UnsupportedOperationException("Unsupported SortBy implementation " + item);
			}
        }
	}

	private Iterable<SortBy> getSortFields(SortBy sortBy) {
		final List<SortBy> items = newArrayList();

		// Unpack the top level multi-sort if present
		if (sortBy instanceof MultiSortBy) {
			items.addAll(((MultiSortBy) sortBy).getItems());
		} else {
			items.add(sortBy);
		}

		Optional<SortByField> existingDocIdSort = items.stream()
			.filter(SortByField.class::isInstance)
			.map(SortByField.class::cast)
			.filter(field -> DocumentMapping._ID.equals(field.getField()))
			.findFirst();
		
		if (!existingDocIdSort.isPresent()) {
			// add _id field as tiebreaker if not defined in the original SortBy
			items.add(SortBy.field(DocumentMapping._ID, Order.DESC));
		}
		
		return Iterables.filter(items, SortBy.class);
	}
	
	@Override
	public <T> Aggregation<T> aggregate(AggregationBuilder<T> aggregation) throws IOException {
		final String aggregationName = aggregation.getName();
		final Client client = admin.client();
		final DocumentMapping mapping = admin.mappings().getMapping(aggregation.getFrom());
		
		final EsQueryBuilder esQueryBuilder = new EsQueryBuilder(mapping);
		final QueryBuilder esQuery = esQueryBuilder.build(aggregation.getQuery());
		
		final SearchRequestBuilder req = client.prepareSearch(admin.getTypeIndex(mapping))
			.addAggregation(toEsAggregation(mapping, aggregation))
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
		
		final ObjectReader reader = HitConverter.getResultObjectReader(mapper, aggregation.getFrom(), aggregation.getFrom());
		
		ImmutableMap.Builder<Object, Bucket<T>> buckets = ImmutableMap.builder();
		Terms aggregationResult = response.getAggregations().<Terms>get(aggregationName);
		for (org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket bucket : aggregationResult.getBuckets()) {
			final TopHits topHits = bucket.getAggregations().get(topHitsAggName(aggregation));
			final ImmutableList.Builder<T> hits = ImmutableList.builder();
			
			if (topHits != null) {
				final SearchHits topSearchHits = topHits.getHits();
				for (SearchHit hit : topSearchHits) {
					final byte[] bytes = BytesReference.toBytes(hit.getSourceRef());
					T value = reader.readValue(bytes, 0, bytes.length);
					hits.add(value);
				}
			}
			
			buckets.put(bucket.getKey(), new Bucket<>(bucket.getKey(), new Hits<>(hits.build(), null, null, aggregation.getBucketHitsLimit(), (int) bucket.getDocCount())));
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
			final String rawScript = aggregation.getScript();
			termsAgg.script(new org.elasticsearch.script.Script(ScriptType.INLINE, "painless", rawScript, Collections.emptyMap()));
		} else {
			throw new IllegalArgumentException("Specify either field or script parameter");
		}
		
		// add top hits agg to get the top N items for each bucket
		if (aggregation.getBucketHitsLimit() > 0) {
			termsAgg.subAggregation(AggregationBuilders.topHits(topHitsAggName(aggregation))
					.fetchSource(null, EXCLUDED_SOURCE_FIELDS)
					.size(aggregation.getBucketHitsLimit()));
		}
		
		return termsAgg;
	}

	private String topHitsAggName(AggregationBuilder<?> aggregation) {
		return aggregation.getName() + "-top-hits";
	}

}
