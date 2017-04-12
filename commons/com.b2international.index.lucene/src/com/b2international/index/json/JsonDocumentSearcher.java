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
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.util.OrderedBytes;
import org.apache.hadoop.hbase.util.SimplePositionedMutableByteRange;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;

import com.b2international.index.Hits;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.IndexException;
import com.b2international.index.LuceneIndexAdmin;
import com.b2international.index.Searcher;
import com.b2international.index.WithHash;
import com.b2international.index.WithId;
import com.b2international.index.WithScore;
import com.b2international.index.lucene.DelegatingFieldComparator;
import com.b2international.index.lucene.IndexField;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
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
		final QueryProfiler profiler = new QueryProfiler(query, slowLogConfig);
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
				final ImmutableList.Builder<T> matches = ImmutableList.builder();
				if (query.getFields() != null && !query.getFields().isEmpty()) {
					fetchFieldDocs(query, mapping, offset, topDocs, matches);
				} else {
					fetchScoreDocs(query, mapping, offset, topDocs, matches);
				}
				profiler.end(Phase.FETCH);
				return new Hits<>(matches.build(), offset, limit, topDocs.totalHits);
			}
		} finally {
			profiler.log(log);
		}
	}

	private <T> void fetchFieldDocs(Query<T> query, DocumentMapping mapping, int offset, TopFieldDocs topDocs, ImmutableList.Builder<T> matches) {
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		Set<String> fields = query.getFields();
		
		for (int i = offset; i < scoreDocs.length; i++) {
			FieldDoc fieldDoc = (FieldDoc) scoreDocs[i];
			ImmutableMap.Builder<String, Object> fieldValues = ImmutableMap.builder();
			
			for (int j = 0; j < topDocs.fields.length; j++) {
				SortField sortField = topDocs.fields[j];
				String key = sortField.getField();
				if (fields.contains(key)) {
					Object value = fieldDoc.fields[j];

					Class<?> fieldType = mapping.getFieldType(key);
					
					if (NumericClassUtils.isFloat(fieldType)) {
						fieldValues.put(key, (Float) value);  
					} else if (NumericClassUtils.isLong(fieldType)) {
						fieldValues.put(key, (Long) value);
					} else if (NumericClassUtils.isInt(fieldType)) {
						fieldValues.put(key, (Integer) value);
					} else if (NumericClassUtils.isShort(fieldType)) {
						fieldValues.put(key, ((Integer) value).shortValue());
					} else if (NumericClassUtils.isBigDecimal(fieldType)) {
						BytesRef bytesRef = (BytesRef) value;
						SimplePositionedMutableByteRange src = new SimplePositionedMutableByteRange(bytesRef.bytes, bytesRef.offset, bytesRef.length);
						fieldValues.put(key, OrderedBytes.decodeNumericAsBigDecimal(src));
					} else if (String.class.isAssignableFrom(fieldType)) {
						BytesRef bytesRef = (BytesRef) value;
						fieldValues.put(key, bytesRef.utf8ToString());
					} else {
						throw new UnsupportedOperationException("Unhandled field value for field: " + key + " of type: " + fieldType);
					}
				}
			}
			
			Map<String, Object> hit = fieldValues.build();
			if (!hit.keySet().containsAll(fields)) {
				throw new IllegalStateException(String.format("Missing fields on partially loaded document: %s", Sets.difference(fields, hit.keySet())));
			}
			
			T readValue = null;
			
			if (fields.size() == 1) {
				Object singleValue = Iterables.getOnlyElement(hit.values());
				if (query.getSelect().isAssignableFrom(singleValue.getClass())) {
					readValue = (T) singleValue;
				}
			}
			
			if (readValue == null) {
				readValue = mapper.convertValue(hit, query.getSelect());
			}
			
			if (query.isWithScores()) {
				/* 
				 * When a query is asking for scores to be returned, the object should support 
				 * recording it by implementing WithScore.
				 */
				((WithScore) readValue).setScore(fieldDoc.score);
			}
			
			matches.add(readValue);
		}
	}

	private <T> void fetchScoreDocs(Query<T> query, DocumentMapping mapping, int offset, TopFieldDocs topDocs, ImmutableList.Builder<T> matches) throws IOException {
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		Class<T> select = query.getSelect();
		Class<?> from = query.getFrom();
		final boolean isWithId = WithId.class.isAssignableFrom(select);
		final boolean isWithHash = WithHash.class.isAssignableFrom(select);
		
		// if select is a different type, then use that as JsonView on from, otherwise select all props
		final ObjectReader reader = select != from 
				? mapper.reader(select).without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) 
				: mapper.reader(select);
		
		final byte[][] sources = new byte[scoreDocs.length - offset][];
		final String[] ids = isWithId ? new String[scoreDocs.length - offset] : null; 
		final String[] hashes = isWithHash ? new String[scoreDocs.length - offset] : null;
		
		final IndexField<String> _id = JsonDocumentMapping._id();
		final IndexField<String> _hash = JsonDocumentMapping._hash();
		
		for (int i = offset; i < scoreDocs.length; i++) {
			Document doc = searcher.doc(scoreDocs[i].doc);
			final int arrayIdx = i - offset;
			sources[arrayIdx] = doc.getBinaryValue("_source").bytes;
			if (isWithId) {
				ids[arrayIdx] = _id.getValue(doc);
			}
			if (isWithHash) {
				hashes[arrayIdx] = _hash.getValue(doc);
			}
		}
		
		for (int i = offset; i < scoreDocs.length; i++) {
			final int arrayIdx = i - offset;
			T readValue = reader.readValue(sources[arrayIdx]);
			if (isWithId) {
				((WithId) readValue).set_id(ids[arrayIdx]);
			}
			if (isWithHash) {
				((WithHash) readValue).set_hash(hashes[arrayIdx]);
			}
			if (query.isWithScores()) {
				((WithScore) readValue).setScore(scoreDocs[i].score);
			}
			matches.add(readValue);
		}
	}

	private DocumentMapping getDocumentMapping(Query<?> query) {
		if (query.getParentType() != null) {
			return mappings.getMapping(query.getParentType()).getNestedMapping(query.getFrom());
		} else {
			return mappings.getMapping(query.getFrom());
		}
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
		
		final Set<String> nonSortedFields = query.getFields() == null ? newHashSet() : newHashSet(query.getFields());
		final List<SortField> convertedItems = newArrayListWithExpectedSize(items.size());
		
		for (final SortByField item : Iterables.filter(items, SortByField.class)) {
            String field = item.getField();
            SortBy.Order order = item.getOrder();
            
			switch (field) {
            case SortBy.FIELD_DOC:
                convertedItems.add(new SortField(null, SortField.Type.DOC, order == SortBy.Order.DESC));
                break;
            case SortBy.FIELD_SCORE:
                // XXX: default order for scores is *descending*
                convertedItems.add(new SortField(null, SortField.Type.SCORE, order == SortBy.Order.ASC));
                break;
            default:
                convertedItems.add(toLuceneSortField(mapping, field, order == SortBy.Order.DESC));
                nonSortedFields.remove(field);
            }
        }
		
		for (String nonSortedField : nonSortedFields) {
			/* 
			 * Add custom sort fields to the end that won't change the document order, but result in a 
			 * field access, for which the value can later be retrieved from FieldDocs.
			 */
			SortField luceneSortField = toLuceneSortField(mapping, nonSortedField, false);
			SortField fetchOnlySortField = new SortField(nonSortedField, new FieldComparatorSource() {
				@Override
				public FieldComparator<?> newComparator(String fieldname, int numHits, int sortPos, boolean reversed) throws IOException {
					return new DelegatingFieldComparator(luceneSortField.getComparator(numHits, sortPos)) {
						@Override public int compare(int slot1, int slot2) { return 0; }
						@Override public int compareValues(Object first, Object second) { return 0; }
					};
				}
			});

			convertedItems.add(fetchOnlySortField);
		}
		
		return new org.apache.lucene.search.Sort(Iterables.toArray(convertedItems, SortField.class));
	}
	
	private SortField toLuceneSortField(DocumentMapping mapping, String sortField, boolean reverse) {
		final Class<?> fieldType = mapping.getFieldType(sortField);

		if (NumericClassUtils.isCollection(fieldType)) {
			throw new IllegalArgumentException("Can't sort on field collection: " + sortField);
		}
		
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

	private int numDocsToRetrieve(final int offset, final int limit) {
		return Ints.min(offset + limit, searcher.getIndexReader().maxDoc());
	}

	private static boolean isEmpty(TopDocs docs) {
		return docs == null || docs.scoreDocs == null || docs.scoreDocs.length == 0;
	}
}
