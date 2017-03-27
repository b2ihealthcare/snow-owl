/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.json;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.slf4j.Logger;

import com.b2international.index.Hits;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.IndexException;
import com.b2international.index.LuceneIndexAdmin;
import com.b2international.index.Searcher;
import com.b2international.index.WithId;
import com.b2international.index.WithScore;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.LuceneQueryBuilder;
import com.b2international.index.query.Phase;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.MultiSortBy;
import com.b2international.index.query.SortBy.SortByField;
import com.b2international.index.query.slowlog.QueryProfiler;
import com.b2international.index.query.slowlog.SlowLogConfig;
import com.b2international.index.util.NumericClassUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

/**
 * @since 4.7
 */
public class JsonDocumentSearcher implements Searcher {

	private final ObjectMapper mapper;
	private final IndexSearcher searcher;
	private final Mappings mappings;
	private final ReferenceManager<IndexSearcher> searchers;
	private final SlowLogConfig slowLogConfig;
	private final Logger log;

	public JsonDocumentSearcher(LuceneIndexAdmin admin, ObjectMapper mapper) {
		this.log = admin.log();
		this.mapper = mapper;
		this.mappings = admin.mappings();
		this.searchers = admin.getManager();
		this.slowLogConfig = (SlowLogConfig) admin.settings().get(IndexClientFactory.SLOW_LOG_KEY);
		try {
			searcher = searchers.acquire();
		} catch (IOException e) {
			throw new IndexException("Couldn't acquire index searcher", e);
		}
	}
	
	@Override
	public void close() throws Exception {
		searchers.release(searcher);
	}

	@Override
	public <T> T get(Class<T> type, String key) throws IOException {
		final org.apache.lucene.search.Query bq = new LuceneQueryBuilder(mappings.getMapping(type)).build(DocumentMapping.matchId(key));
		final TopDocs topDocs = searcher.search(bq, 1);
		if (isEmpty(topDocs)) {
			return null;
		} else {
			final Document doc = searcher.doc(topDocs.scoreDocs[0].doc);
			final byte[] source = doc.getField("_source").binaryValue().bytes;
			return mapper.readValue(source, type);
		}
	}

	@Override
	public <T> Hits<T> search(Query<T> query) throws IOException {
		if (query.getFields() == null || query.getFields().isEmpty()) {
			return searchDefault(query);
		}
		
		if (!SortBy.DOC.equals(query.getSortBy())) {
			throw new IllegalArgumentException("Queries with field selection can't be sorted: " + query.getSortBy()); 
		}
		
		return searchByCollector(query);
	}

	private <T> Hits<T> searchByCollector(Query<T> query) throws IOException {
		final QueryProfiler profiler = new QueryProfiler(query, slowLogConfig);
		try {
			profiler.start(Phase.QUERY);
			final Class<T> select = query.getSelect();
			final DocumentMapping mapping = getDocumentMapping(query);
			final org.apache.lucene.search.Query lq = toLuceneQuery(mapping, query);
			
			final int offset = query.getOffset();
			int limit = query.getLimit();
			
			if (limit < 1) {
				final int totalHits = searcher.count(lq);
				return new Hits<>(Collections.emptyList(), offset, limit, totalHits);
			} else {
				if (limit == Integer.MAX_VALUE || limit == Integer.MAX_VALUE - 1 /*SearchRequest max value*/) {
					// if all values required, or clients expect all values to be returned reduce limit to total hits
					limit = searcher.count(lq);
				} 
				int maxDoc = searcher.getIndexReader().maxDoc();
				if (maxDoc <= 0 || limit < 1) {
					return new Hits<>(Collections.emptyList(), offset, limit, 0);
				} else {
					final Set<String> fields = query.getFields();
					final List<Map<String, Object>> rawHits = searcher.search(lq, new DocValueCollectorManager(fields, mapping));
					final List<T> hits = FluentIterable.from(rawHits)
							.transform(new Function<Map<String, Object>, T>() {
								@Override
								public T apply(Map<String, Object> input) {
									return mapper.convertValue(input, select);
								}
							}).toList();
					return new Hits<>(hits, offset, limit, rawHits.size());
				}
			}
		} finally {
			profiler.end(Phase.QUERY);
			profiler.log(log);
		}
	}

	private <T> Hits<T> searchDefault(Query<T> query) throws IOException {
		final QueryProfiler profiler = new QueryProfiler(query, slowLogConfig);
		final Class<T> select = query.getSelect();
		final Class<?> from = query.getFrom();
		final DocumentMapping mapping = getDocumentMapping(query);
		final org.apache.lucene.search.Query lq = toLuceneQuery(mapping, query);
		final org.apache.lucene.search.Sort ls = toLuceneSort(mapping, query);
		
		final int offset = query.getOffset();
		int limit = query.getLimit();
		try {
			// QUERY PHASE
			profiler.start(Phase.QUERY);
			
			final TopFieldDocs topDocs;
			if (limit < 1) {
				final int totalHits = searcher.count(lq);
				topDocs = new TopFieldDocs(totalHits, null, null, 0);
			} else {
				if (limit == Integer.MAX_VALUE || limit == Integer.MAX_VALUE - 1 /*SearchRequest max value*/) {
					// if all values required, or clients expect all values to be returned
					// use collector instead of TopDocs, TODO bring back DocSourceCollector to life
					// reduce limit to max. total hits
					limit = searcher.count(lq);
				} 
				int maxDoc = searcher.getIndexReader().maxDoc();
				if (maxDoc <= 0 || limit < 1) {
					topDocs = new TopFieldDocs(0, null, null, 0);
				} else {
					topDocs = searcher.search(lq, numDocsToRetrieve(offset, limit), ls, query.isWithScores(), false);
				}
			}
			profiler.end(Phase.QUERY);
			
			// FETCH PHASE
			if (topDocs.scoreDocs == null || topDocs.scoreDocs.length < 1) {
				return new Hits<>(Collections.<T>emptyList(), offset, limit, topDocs.totalHits);
			} else {
				profiler.start(Phase.FETCH);
				
				// if select is a different type, then use that as JsonView on from, otherwise select all props
				final ObjectReader reader = select != from ? mapper.reader(select).without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) : mapper.reader(select);
				final ScoreDoc[] scoreDocs = topDocs.scoreDocs;
				final String[] ids = new String[scoreDocs.length - offset];
				final byte[][] sources = new byte[scoreDocs.length - offset][];
				
				for (int i = offset; i < scoreDocs.length; i++) {
					final Document doc = searcher.doc(scoreDocs[i].doc);
					sources[i - offset] = doc.getBinaryValue("_source").bytes;
					ids[i - offset] = JsonDocumentMapping._id().getValue(doc);
				}
				
				final ImmutableList.Builder<T> matches = ImmutableList.builder();
				for (int i = offset; i < scoreDocs.length; i++) {
					final T readValue = reader.readValue(sources[i - offset]);
					if (readValue instanceof WithId) {
						((WithId) readValue).set_id(ids[i - offset]);
					}
					if (query.isWithScores() && readValue instanceof WithScore) {
						((WithScore) readValue).setScore(scoreDocs[i].score);
					}
					matches.add(readValue);
				}
				profiler.end(Phase.FETCH);
				return new Hits<>(matches.build(), offset, limit, topDocs.totalHits);
			}
		} finally {
			profiler.log(log);
		}
	}

	private DocumentMapping getDocumentMapping(Query<?> query) {
		if (query.getParentType() != null) {
			return mappings.getMapping(query.getParentType()).getNestedMapping(query.getFrom());
		}
		return mappings.getMapping(query.getFrom());
	}

	private org.apache.lucene.search.Query toLuceneQuery(DocumentMapping mapping, Query<?> query) {
		return new LuceneQueryBuilder(mapping).build(query.getWhere());
	}

	private org.apache.lucene.search.Sort toLuceneSort(DocumentMapping mapping, Query<?> query) {
		final SortBy sortBy = query.getSortBy();
		final List<SortBy> items = newArrayList();

		// Unpack the top level multi-sort if present
		if (sortBy instanceof MultiSortBy) {
			items.addAll(((MultiSortBy) sortBy).getItems());
		} else {
			items.add(sortBy);
		}

		SortField[] convertedFields = FluentIterable.from(items)
				.filter(SortByField.class)
				.transform(item -> {
					switch (item.getField()) {
					case SortBy.FIELD_DOC:
						return new SortField(null, SortField.Type.DOC, item.getOrder() == SortBy.Order.DESC);
					case SortBy.FIELD_SCORE:
						return new SortField(null, SortField.Type.SCORE, item.getOrder() == SortBy.Order.ASC); // XXX: default order for scores is *descending*
					default:
						final String sortField = item.getField();
						final Field mappingField = mapping.getField(sortField);

						if (NumericClassUtils.isCollection(mappingField)) {
							throw new IllegalArgumentException("Can't sort on field collection: " + sortField);
						}

						final Class<?> fieldType = mappingField.getType();
						final boolean reverse = (item.getOrder() == SortBy.Order.DESC);

						if (NumericClassUtils.isLong(fieldType)) {
							return new SortField(sortField, Type.LONG, reverse);
						} else if (NumericClassUtils.isFloat(fieldType)) {
							return new SortField(sortField, Type.FLOAT, reverse);
						} else if (NumericClassUtils.isInt(fieldType) || NumericClassUtils.isShort(fieldType)) {
							return new SortField(sortField, Type.INT, reverse);
						} else if (NumericClassUtils.isBigDecimal(fieldType) || String.class.isAssignableFrom(fieldType)) {
							// TODO: STRING mode might be faster, but requires SortedDocValueFields
							return new SortField(sortField, Type.STRING_VAL, reverse);
						} else {
							throw new IllegalArgumentException("Unsupported sort field type: " + fieldType + " for field: " + sortField);
						}
					}
				})
				.toArray(SortField.class);

		return new org.apache.lucene.search.Sort(convertedFields);
	}
	
	private int numDocsToRetrieve(final int offset, final int limit) {
		return Ints.min(offset + limit, searcher.getIndexReader().maxDoc());
	}

	private static boolean isEmpty(TopDocs docs) {
		return docs == null || docs.scoreDocs == null || docs.scoreDocs.length == 0;
	}
}
