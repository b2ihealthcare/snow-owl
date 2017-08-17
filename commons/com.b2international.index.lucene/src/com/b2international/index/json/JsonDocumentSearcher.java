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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;
import org.slf4j.Logger;

import com.b2international.index.Hits;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.IndexException;
import com.b2international.index.LuceneIndexAdmin;
import com.b2international.index.ScriptEngine;
import com.b2international.index.Searcher;
import com.b2international.index.WithHash;
import com.b2international.index.WithId;
import com.b2international.index.WithScore;
import com.b2international.index.lucene.DelegatingFieldComparator;
import com.b2international.index.lucene.IndexField;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.Expression;
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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

/**
 * @since 4.7
 */
public class JsonDocumentSearcher implements Searcher {

	private static final Set<String> SOURCE_ONLY = ImmutableSet.of("_source");

	private final ObjectMapper mapper;
	private final IndexSearcher searcher;
	private final Mappings mappings;
	private final ReferenceManager<IndexSearcher> searchers;
	private final SlowLogConfig slowLogConfig;
	private final int resultWindow;
	private final Logger log;
	private final QueryBuilder luceneQueryBuilder;
	private final ScriptEngine scriptEngine;

	public JsonDocumentSearcher(LuceneIndexAdmin admin, ObjectMapper mapper) {
		this.scriptEngine = admin;
		this.log = admin.log();
		this.mapper = mapper;
		this.mappings = admin.mappings();
		this.searchers = admin.getManager();
		this.luceneQueryBuilder = admin.getQueryBuilder();
		this.slowLogConfig = (SlowLogConfig) admin.settings().get(IndexClientFactory.SLOW_LOG_KEY);
		this.resultWindow = (int) admin.settings().get(IndexClientFactory.RESULT_WINDOW_KEY);

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
		final DocumentMapping mapping = mappings.getMapping(type);
		final String typeAsString = mapping.typeAsString();

		PostingsEnum idPostingsEnum = null;
		PostingsEnum typePostingsEnum = null;
		
		List<LeafReaderContext> leaves = searcher.getIndexReader().leaves();
		for (LeafReaderContext leafReaderContext : leaves) {
			LeafReader leafReader = leafReaderContext.reader();
			
			// This segment should have indexed terms for fields "_id" and "_type"
			Terms idTerms = leafReader.terms(DocumentMapping._ID);
			Terms typeTerms = leafReader.terms(DocumentMapping._TYPE);
			if (idTerms == null || typeTerms == null) {
				continue;
			}
			
			// A term for field "_id" with value "key" should exist
			TermsEnum idEnum = idTerms.iterator();
			if (!idEnum.seekExact(new BytesRef(key))) {
				continue;
			}

			// A term for field "_type" with value "typeAsString" should exist
			TermsEnum typeEnum = typeTerms.iterator();
			if (!typeEnum.seekExact(new BytesRef(typeAsString))) {
				continue;
			}
			
			Bits liveDocs = leafReader.getLiveDocs();
			idPostingsEnum = idEnum.postings(idPostingsEnum);
			int idDoc = PostingsEnum.NO_MORE_DOCS;
			
			// Now iterate over all documents that had an "_id" field with value "key" 
			while ((idDoc = idPostingsEnum.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
				if (liveDocs != null && !liveDocs.get(idDoc)) {
					continue;
				}
				
				// Found a live document; check if it is also in the expected type term's postings
				typePostingsEnum = typeEnum.postings(typePostingsEnum, PostingsEnum.NONE);
				int typeDoc = typePostingsEnum.advance(idDoc);

				if (typeDoc == idDoc) {
					Document document = leafReader.document(idDoc, SOURCE_ONLY);
					BytesRef source = document.getBinaryValue("_source");
					return mapper.readValue(source.bytes, source.offset, source.length, type);
				}
			}
		}

		return null;
	}

	@Override
	public <T> Hits<T> search(Query<T> query) throws IOException {
		final QueryProfiler profiler = new QueryProfiler(query, slowLogConfig);
		final DocumentMapping mapping = mappings.getDocumentMapping(query);
		final int offset = query.getOffset();
		final int limit = query.getLimit();
		
		try {
			// QUERY PHASE
			profiler.start(Phase.QUERY);
			final TopFieldDocs topDocs = getTopDocs(query, mapping, offset, limit);
			profiler.end(Phase.QUERY);
			
			// FETCH PHASE
			if (topDocs.scoreDocs == null || topDocs.scoreDocs.length < 1) {
				return new Hits<>(Collections.<T>emptyList(), offset, limit, topDocs.totalHits);
			} else {
				profiler.start(Phase.FETCH);
				final ImmutableList.Builder<T> matches = ImmutableList.builder();
				if (query.getFields() != null && !query.getFields().isEmpty()) {
					fetchFieldDocs(query, mapping, topDocs, matches);
				} else {
					fetchScoreDocs(query, mapping, topDocs, matches);
				}
				profiler.end(Phase.FETCH);
				return new Hits<>(matches.build(), offset, limit, topDocs.totalHits);
			}
		} finally {
			profiler.log(log);
		}
	}

	private <T> TopFieldDocs getTopDocs(Query<T> query, final DocumentMapping mapping, final int offset, final int limit) throws IOException {
		final org.apache.lucene.search.Query lq = toLuceneQuery(mapping, query.getWhere());
		final org.apache.lucene.search.Sort ls = toLuceneSort(mapping, query);
		
		if (limit < 1) {
			// The request is only interested in the query hit count
			final int totalHits = searcher.count(lq);
			return new TopFieldDocs(totalHits, null, null, 0);
		}
		
		int numDocs = searcher.getIndexReader().numDocs();
		if (numDocs <= 0) {
			// We don't have any live documents to query
			return new TopFieldDocs(0, null, null, 0);
		}
		
		// Restrict variables to the theoretical maximum
		int toRead = Ints.min(limit, numDocs);
		int toSkip = Ints.min(offset, numDocs);
				
		// Skip over hits in the "offset" range, in "resultWindow"-sized hops
		FieldDoc after = null;
		TopFieldDocs windowTopDocs;
		boolean checkAllSkipped = true;
		
		while (toSkip > 0) {
			int numHits = Ints.min(toSkip, resultWindow);
			windowTopDocs = searcher.searchAfter(after, lq, numHits, ls, false, false);
			
			// Skipping past all results? Needs to be checked in the first iteration only
			if (checkAllSkipped) {
				if (toSkip >= windowTopDocs.totalHits) {
					return new TopFieldDocs(windowTopDocs.totalHits, null, null, 0);
				} else {
					checkAllSkipped = false;
				}
			}
			
			checkState (windowTopDocs.scoreDocs.length == numHits);
			
			toSkip -= windowTopDocs.scoreDocs.length;
			after = (FieldDoc) windowTopDocs.scoreDocs[windowTopDocs.scoreDocs.length - 1];
		}
				
		checkState(toSkip == 0);
		
		// Peek ahead, get total hits
		windowTopDocs = searcher.searchAfter(after, lq, 1, ls, false, false);
				
		if (windowTopDocs.scoreDocs.length < 1) {
			// We will not even have one relevant document to read in the final part; exit early
			return new TopFieldDocs(windowTopDocs.totalHits, null, null, 0);
		}
		
		// Restrict "toRead" again, then read the relevant topDocs in "resultWindow" hops
		toRead = Ints.min(toRead, windowTopDocs.totalHits - offset);
		TopFieldDocs topDocs = new TopFieldDocs(windowTopDocs.totalHits, new FieldDoc[toRead], ls.getSort(), 0);
				
		while (toRead > 0) {
			int numHits = Ints.min(toRead, resultWindow);
			windowTopDocs = searcher.searchAfter(after, lq, numHits, ls, query.isWithScores(), false);
			
			checkState (windowTopDocs.scoreDocs.length == numHits);
			
			System.arraycopy(windowTopDocs.scoreDocs, 0, 
					topDocs.scoreDocs, topDocs.scoreDocs.length - toRead, 
					windowTopDocs.scoreDocs.length);
			
			toRead -= windowTopDocs.scoreDocs.length;
			after = (FieldDoc) windowTopDocs.scoreDocs[windowTopDocs.scoreDocs.length - 1];
		}
				
		return topDocs;
	}

	private <T> void fetchFieldDocs(Query<T> query, DocumentMapping mapping, TopFieldDocs topDocs, ImmutableList.Builder<T> matches) {
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		Set<String> fields = query.getFields();
		
		for (int i = 0; i < scoreDocs.length; i++) {
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
					} else if (NumericClassUtils.isBigDecimal(fieldType) || String.class.isAssignableFrom(fieldType)) {
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

	private <T> void fetchScoreDocs(Query<T> query, DocumentMapping mapping, TopFieldDocs topDocs, ImmutableList.Builder<T> matches) throws IOException {
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		Class<T> select = query.getSelect();
		Class<?> from = query.getFrom();
		final boolean isWithId = WithId.class.isAssignableFrom(select);
		final boolean isWithHash = WithHash.class.isAssignableFrom(select);
		
		// if select is a different type, then use that as JsonView on from, otherwise select all props
		final ObjectReader reader = select != from 
				? mapper.reader(select).without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) 
				: mapper.reader(select);
		
		final byte[][] sources = new byte[scoreDocs.length][];
		final String[] ids = isWithId ? new String[scoreDocs.length] : null; 
		final String[] hashes = isWithHash ? new String[scoreDocs.length] : null;
		
		final IndexField<String> _id = JsonDocumentMapping._id();
		final IndexField<String> _hash = JsonDocumentMapping._hash();
		
		for (int i = 0; i < scoreDocs.length; i++) {
			Document doc = searcher.doc(scoreDocs[i].doc);
			sources[i] = doc.getBinaryValue("_source").bytes;
			if (isWithId) {
				ids[i] = _id.getValue(doc);
			}
			if (isWithHash) {
				hashes[i] = _hash.getValue(doc);
			}
		}
		
		for (int i = 0; i < scoreDocs.length; i++) {
			T readValue = reader.readValue(sources[i]);
			if (isWithId) {
				((WithId) readValue).set_id(ids[i]);
			}
			if (isWithHash) {
				((WithHash) readValue).set_hash(hashes[i]);
			}
			if (query.isWithScores()) {
				((WithScore) readValue).setScore(scoreDocs[i].score);
			}
			matches.add(readValue);
		}
	}

	private org.apache.lucene.search.Query toLuceneQuery(DocumentMapping mapping, Expression where) {
		return new LuceneQueryBuilder(mapping, luceneQueryBuilder, scriptEngine).build(where);
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
		boolean sortById = false;
		
		for (final SortByField item : Iterables.filter(items, SortByField.class)) {
            String field = item.getField();
            SortBy.Order order = item.getOrder();
            
			switch (field) {
			case DocumentMapping._ID:
				convertedItems.add(new SortField(DocumentMapping._ID, SortField.Type.STRING_VAL, order == SortBy.Order.DESC));
				nonSortedFields.remove(field);
				sortById = true;
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
		
		// Tie-breaker, required to make "searchAfter" work
		if (!sortById) {
			convertedItems.add(new SortField(DocumentMapping._ID, SortField.Type.STRING_VAL, false));
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
}
