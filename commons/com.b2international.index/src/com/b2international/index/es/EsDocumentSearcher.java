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
package com.b2international.index.es;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.TotalHits.Relation;
import org.apache.solr.common.util.JavaBinCodec;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.b2international.commons.exceptions.FormattedRuntimeException;
import com.b2international.index.*;
import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.aggregations.Bucket;
import com.b2international.index.es.admin.EsIndexAdmin;
import com.b2international.index.es.client.EsClient;
import com.b2international.index.es.query.EsQueryBuilder;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.MultiSortBy;
import com.b2international.index.query.SortBy.SortByField;
import com.b2international.index.query.SortBy.SortByScript;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

/**
 * @since 5.10
 */
public class EsDocumentSearcher implements Searcher {

	private static final List<String> STORED_FIELDS_ID_ONLY = List.of("_id");
	private static final List<String> STORED_FIELDS_NONE = List.of("_none_");

	private final EsIndexAdmin admin;
	private final ObjectMapper mapper;
	private final int resultWindow;
	private final int maxTermsCount;

	public EsDocumentSearcher(EsIndexAdmin admin, ObjectMapper mapper) {
		this.admin = admin;
		this.mapper = mapper;
		this.resultWindow = Integer.parseInt((String) admin.settings().get(IndexClientFactory.RESULT_WINDOW_KEY));
		this.maxTermsCount = Integer.parseInt((String) admin.settings().get(IndexClientFactory.MAX_TERMS_COUNT_KEY));;
	}

	@Override
	public <T> T get(Class<T> type, String key) throws IOException {
		checkArgument(!Strings.isNullOrEmpty(key), "Key cannot be empty");
		final DocumentMapping mapping = admin.mappings().getMapping(type);
		final GetRequest req = new GetRequest(admin.getTypeIndex(mapping), key)
				.fetchSourceContext(FetchSourceContext.FETCH_SOURCE);
		final GetResponse res = admin.client().get(req);
		
		if (res.isExists()) {
			final byte[] bytes = res.getSourceAsBytes();
			return mapper.readValue(bytes, 0, bytes.length, type);
		} else {
			return null;
		}
	}
	
	@Override
	public <T> Iterable<T> get(Class<T> type, Iterable<String> keys) throws IOException {
		final DocumentMapping mapping = admin.mappings().getMapping(type);
		List<String> allKeys = ImmutableList.copyOf(keys);
		if (allKeys.size() > maxTermsCount) {
			List<T> results = Lists.newArrayListWithExpectedSize(allKeys.size());
			for (List<String> currentKeys : Lists.partition(allKeys, maxTermsCount)) {
				results.addAll(search(Query.select(type).where(Expressions.matchAny(mapping.getIdField(), currentKeys)).limit(currentKeys.size()).build()).getHits());
			}
			return results;
		} else {
			return search(Query.select(type).where(Expressions.matchAny(mapping.getIdField(), allKeys)).limit(allKeys.size()).build()).getHits();
		}
	}

	@Override
	public <T> Hits<T> search(Query<T> query) throws IOException {
		final EsClient client = admin.client();
		final List<DocumentMapping> mappings = admin.mappings().getDocumentMapping(query);
		final DocumentMapping primaryMapping = Iterables.getFirst(mappings, null);
		
		// Restrict variables to the theoretical maximum
		final int limit = query.getLimit();
		final int toRead = Ints.min(limit, resultWindow);
		
		// TODO support multiple document mappings during query building
		final EsQueryBuilder esQueryBuilder = new EsQueryBuilder(primaryMapping, admin.settings(), admin.log());
		final QueryBuilder esQuery = esQueryBuilder.build(query.getWhere());
		
		final SearchRequest req = new SearchRequest(admin.getTypeIndexes(mappings).toArray(length -> new String[length]));
		
		final SearchSourceBuilder reqSource = req.source()
			.size(toRead)
			.query(esQuery)
			.trackScores(esQueryBuilder.needsScoring())
			.trackTotalHitsUpTo(Integer.MAX_VALUE);
		
		// field selection
		final boolean fetchSource = applySourceFiltering(query.getFields(), primaryMapping, reqSource);
		
		// this won't load fields like _parent, _routing, _uid at all
		// and _id in cases where we explicitly require the _source
		// ES internals require loading the _id field when we require the _source
		if (fetchSource) {
			reqSource.storedFields(STORED_FIELDS_ID_ONLY);
		} else {
			reqSource.storedFields(STORED_FIELDS_NONE);
		}
		
		// scroll config
		final boolean isLocalScroll = limit > resultWindow;
		final boolean isScrolled = !Strings.isNullOrEmpty(query.getScrollKeepAlive());
		final boolean isLiveScrolled = !Strings.isNullOrEmpty(query.getSearchAfter());
		if (isLocalScroll) {
			checkArgument(!isScrolled, "Cannot fetch more than '%s' items when scrolling is specified. You requested '%s' items.", resultWindow, limit);
			checkArgument(!isLiveScrolled, "Cannot use search after when requesting more number of items (%s) than the max result window (%s).", limit, resultWindow);
			// do NOT start a scroll on the first request, just validate the search request state here
			// XXX commented row kept for historical reasons
//			req.scroll(scrollTime);
		} else if (isScrolled) {
			checkArgument(!isLiveScrolled, "Cannot scroll and live scroll at the same time");
			req.scroll(query.getScrollKeepAlive());
		} else if (isLiveScrolled) {
			checkArgument(!isScrolled, "Cannot scroll and live scroll at the same time");
			reqSource.searchAfter(fromSearchAfterToken(query.getSearchAfter()));
		}
		
		// sorting config with a default sort field based on scroll config
		addSort(primaryMapping, reqSource, query.getSortBy(), !isScrolled && !isLocalScroll);
		// disable explain explicitly, just in case
		reqSource.explain(false);
		// disable version field explicitly, just in case
		reqSource.version(false);
		
		// perform search
		SearchResponse response = null; 
		try {
			response = client.search(req);
		} catch (Exception e) {
			admin.log().error("Couldn't execute query", e);
			throw new IndexException("Couldn't execute query: " + e.getMessage(), null);
		}

		TotalHits totalHits = response.getHits().getTotalHits();
		checkState(totalHits.relation == Relation.EQUAL_TO, "Searches should always track total hits accurately");
		final int totalHitCount = (int) totalHits.value;
		final ImmutableList.Builder<SearchHit> allHits = ImmutableList.builder();
		int numDocsToFetch = Math.min(limit, totalHitCount) - response.getHits().getHits().length;

		// if the client requested all data at once and there are more data in the index
		// throw away the first batch and perform a local scroll
		if (isLocalScroll && numDocsToFetch > 0) {
			// WARN the caller that this might not be the most efficient way of fetching the data, consider using SearchAfter API or explicit Scroll API
			admin.log().warn("Returning all matches (totalHits: '{}') larger than the currently configured result_window ('{}') might not be the most efficient way of getting the data. Consider using the index pagination APIs (searchAfter or explicit scroll) instead.", totalHitCount, resultWindow);

			// perform search again with a default 60s scroll enabled
			final TimeValue scrollTime = TimeValue.timeValueSeconds(60);
			try {
				response = client.search(req.scroll(scrollTime));
			} catch (Exception e) {
				admin.log().error("Couldn't execute query", e);
				throw new IndexException("Couldn't execute query: " + e.getMessage(), null);
			}

			// recalc if there were index changes in the middle
			numDocsToFetch = Math.min(limit, totalHitCount) - response.getHits().getHits().length;
			// register all hits
			allHits.add(response.getHits().getHits());

			// then continue scroll
			while (numDocsToFetch > 0) {
				final SearchScrollRequest searchScrollRequest = new SearchScrollRequest(response.getScrollId())
						.scroll(scrollTime);

				response = client.scroll(searchScrollRequest);
				int fetchedDocs = response.getHits().getHits().length;
				if (fetchedDocs == 0) {
					break;
				}
				numDocsToFetch -= fetchedDocs;
				allHits.add(response.getHits().getHits());
			}
			
			// clear the custom local scroll
			final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
			clearScrollRequest.addScrollId(response.getScrollId());
			client.clearScroll(clearScrollRequest);
		} else {
			allHits.add(response.getHits().getHits());
		}

		final Class<T> select = query.getSelection().getSelect();
		final List<Class<?>> from = query.getSelection().getFrom();
		
		return toHits(select, from, query.getFields(), fetchSource, limit, totalHitCount, response.getScrollId(), query.getSortBy(), allHits.build());
	}

	private <T> boolean applySourceFiltering(List<String> fields, final DocumentMapping mapping, final SearchSourceBuilder reqSource) {
		// No specific fields requested? Use _source to retrieve all of them
		if (fields.isEmpty()) {
			reqSource.fetchSource(true);
			return true;
		}
		
		// Any field requested that can only retrieved from _source? Use source filtering
		if (requiresDocumentSourceField(mapping, fields)) {
			reqSource.fetchSource(Iterables.toArray(fields, String.class), null);
			return true;
		}
		
		// Use docValues otherwise for field retrieval
		fields.stream().forEach(field -> reqSource.docValueField(field));
		reqSource.fetchSource(false);
		return false;
	}

	private boolean requiresDocumentSourceField(DocumentMapping mapping, List<String> fields) {
		return fields
			.stream()
			.filter(mapping::isCollection)
			.findFirst()
			.isPresent();
	}

	@Override
	public <T> Hits<T> scroll(Scroll<T> scroll) throws IOException {
		final String scrollId = scroll.getScrollId();
		final SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
		searchScrollRequest.scroll(scroll.getKeepAlive());
		
		try {
			
			final SearchResponse response = admin.client()
					.scroll(searchScrollRequest);
			
			Class<?> from = Iterables.getOnlyElement(scroll.getSelection().getFrom());
			final DocumentMapping mapping = admin.mappings().getMapping(from);
			final boolean fetchSource = scroll.getFields().isEmpty() || requiresDocumentSourceField(mapping, scroll.getFields());
			return toHits(scroll.getSelection().getSelect(), List.of(from), scroll.getFields(), fetchSource, response.getHits().getHits().length, (int) response.getHits().getTotalHits().value, response.getScrollId(), null, response.getHits());	
			
		} catch (IOException | ElasticsearchStatusException e) {
			final Throwable rootCause = Throwables.getRootCause(e);
			
			if (rootCause instanceof ElasticsearchException && rootCause.getMessage().contains("No search context found for id [")) {
				throw new SearchContextMissingException(String.format("Search context missing for scrollId '%s'.", scrollId), null);
			} else if (e instanceof IOException) {
				throw e;
			} else {
				throw new IOException(e);
			}
		}
	}
	
	@Override
	public void cancelScroll(String scrollId) {
		final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
		clearScrollRequest.addScrollId(scrollId);
		
		try {
			admin.client().clearScroll(clearScrollRequest);
		} catch (IOException e) {
			throw new IndexException(String.format("Couldn't clear scroll state for scrollId '%s'.", scrollId), e);
		}
	}
	
	private <T> Hits<T> toHits(
			Class<T> select, 
			List<Class<?>> from, 
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
			if (value instanceof WithScore) {
				((WithScore) value).setScore(Float.isNaN(hit.getScore()) ? 0.0f : hit.getScore());
			}
			result.add(value);
			if (!iterator.hasNext()) {
				searchAfterSortValues = hit.getSortValues();
			}
		}
		return new Hits<T>(result.build(), scrollId, toSearchAfterToken(searchAfterSortValues), limit, totalHits);
	}
	
	private String toSearchAfterToken(final Object[] searchAfter) {
		if (searchAfter == null) {
			return null;
		}
		
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			final JavaBinCodec codec = new JavaBinCodec();
			codec.marshal(searchAfter, baos);
			codec.close();
			
			final byte[] tokenBytes = baos.toByteArray();
			return Base64.getUrlEncoder().encodeToString(tokenBytes);
		} catch (IOException e) {
			throw new FormattedRuntimeException("Couldn't encode searchAfter parameters to a token.", e);
		}
	}

	private Object[] fromSearchAfterToken(final String searchAfterToken) {
		if (Strings.isNullOrEmpty(searchAfterToken)) {
			return null;
		}
		
		final byte[] decodedToken = Base64
				.getUrlDecoder()
				.decode(searchAfterToken);
		
		try (final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(decodedToken))) {
			JavaBinCodec codec = new JavaBinCodec();
			List<?> obj = (List<?>) codec.unmarshal(dis);
			codec.close();
			return obj.toArray();
		} catch (final IOException e) {
			throw new FormattedRuntimeException("Couldn't decode searchAfter token.", e);
		}
	}

	private void addSort(DocumentMapping mapping, SearchSourceBuilder reqSource, SortBy sortBy, boolean liveScroll) {
		for (final SortBy item : getSortFields(sortBy)) {
			if (item instanceof SortByField) {
				SortByField sortByField = (SortByField) item;
				String field = sortByField.getField();
				SortBy.Order order = sortByField.getOrder();
				SortOrder sortOrder = order == SortBy.Order.ASC ? SortOrder.ASC : SortOrder.DESC;
				
				switch (field) {
				case SortBy.FIELD_SCORE:
					// XXX: default order for scores is *descending*
					reqSource.sort(SortBuilders.scoreSort().order(sortOrder)); 
					break;
				case "_default": //$FALL-THROUGH$
					if (liveScroll) {
						// for live scrolls use the document ID field as tiebreaker
						field = mapping.getDefaultSortField();
					} else {
						// for snapshot scrolls use the "_doc" field as tiebreaker
						field = "_doc";
					}
				default:
					reqSource.sort(SortBuilders.fieldSort(field).order(sortOrder));
				}
			} else if (item instanceof SortByScript) {
				SortByScript sortByScript = (SortByScript) item;
				SortBy.Order order = sortByScript.getOrder();
				SortOrder sortOrder = order == SortBy.Order.ASC ? SortOrder.ASC : SortOrder.DESC;
				
				reqSource.sort(SortBuilders.scriptSort(sortByScript.toEsScript(mapping), ScriptSortType.STRING)
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

		Optional<SortByField> existingDefaultSort = items.stream()
				.filter(SortByField.class::isInstance)
				.map(SortByField.class::cast)
				.filter(field -> SortBy.DEFAULT.getField().equals(field.getField()))
				.findFirst();
		
		if (!existingDefaultSort.isPresent()) {
			// add the default field (either _doc or ID field) as tie breaker
			items.add(SortBy.DEFAULT);
		}
		
		return Iterables.filter(items, SortBy.class);
	}
	
	@Override
	public <T> Aggregation<T> aggregate(AggregationBuilder<T> aggregation) throws IOException {
		final String aggregationName = aggregation.getName();
		final EsClient client = admin.client();
		final DocumentMapping mapping = admin.mappings().getMapping(aggregation.getFrom());
		
		final EsQueryBuilder esQueryBuilder = new EsQueryBuilder(mapping, admin.settings(), admin.log());
		final QueryBuilder esQuery = esQueryBuilder.build(aggregation.getQuery());
		
		final SearchRequest req = new SearchRequest(admin.getTypeIndex(mapping));
		
		final SearchSourceBuilder reqSource = req.source()
			.query(esQuery)
			.size(0)
			.trackScores(false)
			.trackTotalHitsUpTo(Integer.MAX_VALUE);
		
		// field selection
		final boolean fetchSource = applySourceFiltering(aggregation.getFields(), mapping, reqSource);
		reqSource.aggregation(toEsAggregation(mapping, aggregation, fetchSource));
		
		SearchResponse response = null; 
		try {
			response = client.search(req);
		} catch (Exception e) {
			admin.log().error("Couldn't execute aggregation", e);
			throw new IndexException("Couldn't execute aggregation: " + e.getMessage(), null);
		}
		
		
		ImmutableMap.Builder<Object, Bucket<T>> buckets = ImmutableMap.builder();
		Aggregations topLevelAggregations = response.getAggregations();
		Nested nested = topLevelAggregations.get(nestedAggName(aggregation));
		Terms aggregationResult;
		
		if (nested != null) {
			aggregationResult = nested.getAggregations().get(aggregationName);
		} else {
			aggregationResult = topLevelAggregations.get(aggregationName);
		}
				
		for (org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket bucket : aggregationResult.getBuckets()) {
			final TopHits topHits;
			if (nested != null) {
				final ReverseNested reverseNested = bucket.getAggregations().get(reverseNestedAggName(aggregation));
				topHits = reverseNested.getAggregations().get(topHitsAggName(aggregation));
			} else {
				topHits = bucket.getAggregations().get(topHitsAggName(aggregation));				
			}
			Hits<T> hits;
			if (topHits != null) {
				hits = toHits(aggregation.getSelect(), List.of(aggregation.getFrom()), aggregation.getFields(), fetchSource, aggregation.getBucketHitsLimit(), (int) bucket.getDocCount(), null, null, topHits.getHits()); 
			} else {
				hits = new Hits<>(Collections.emptyList(), null, null, aggregation.getBucketHitsLimit(), (int) bucket.getDocCount());
			}
			buckets.put(bucket.getKey(), new Bucket<>(bucket.getKey(), hits));
		}
		
		return new Aggregation<>(aggregationName, buckets.build());
	}

	private org.elasticsearch.search.aggregations.AggregationBuilder toEsAggregation(DocumentMapping mapping, AggregationBuilder<?> aggregation, boolean fetchSource) {
		final TermsAggregationBuilder termsAgg = AggregationBuilders
				.terms(aggregation.getName())
				.minDocCount(aggregation.getMinBucketSize())
				.size(Integer.MAX_VALUE);
		boolean isFieldAgg = !Strings.isNullOrEmpty(aggregation.getGroupByField());
		boolean isScriptAgg = !Strings.isNullOrEmpty(aggregation.getGroupByScript());
		if (isFieldAgg) {
			checkArgument(!isScriptAgg, "Specify either field or script parameter, not both");
			termsAgg.field(aggregation.getGroupByField());
		} else if (isScriptAgg) {
			termsAgg.script(aggregation.toEsScript(mapping));
		} else {
			throw new IllegalArgumentException("Specify either field or script parameter");
		}
		
		boolean isNested = !Strings.isNullOrEmpty(aggregation.getPath());
		// add top hits agg to get the top N items for each bucket
		if (aggregation.getBucketHitsLimit() > 0) {
			TopHitsAggregationBuilder topHitsAgg = AggregationBuilders.topHits(topHitsAggName(aggregation))
					.size(aggregation.getBucketHitsLimit());
			
			if (fetchSource) {
				topHitsAgg
					.storedFields(STORED_FIELDS_ID_ONLY)
					.fetchSource(true);
			} else {
				topHitsAgg
					.storedFields(STORED_FIELDS_NONE)
					.fetchSource(false);
				
				aggregation.getFields().forEach(field -> topHitsAgg.docValueField(field));
				
			}
			
			if (isNested) {
				termsAgg.subAggregation(AggregationBuilders.reverseNested(reverseNestedAggName(aggregation)).subAggregation(topHitsAgg));
			} else {
				termsAgg.subAggregation(topHitsAgg);
			}
		}
		
		if (isNested) {
			return AggregationBuilders
					.nested(nestedAggName(aggregation), aggregation.getPath())
					.subAggregation(termsAgg);
		}
		
		return termsAgg;
	}

	private String topHitsAggName(AggregationBuilder<?> aggregation) {
		return aggregation.getName() + "-top-hits";
	}
	
	private String nestedAggName(AggregationBuilder<?> aggregation) {
		return aggregation.getName() + "-nested";
	}
	
	private String reverseNestedAggName(AggregationBuilder<?> aggregation) {
		return aggregation.getName() + "-reverse-nested";
	}

	public int resultWindow() {
		return resultWindow;
	}
	
	public int maxTermsCount() {
		return maxTermsCount;
	}

}
