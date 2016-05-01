/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.util.Collections;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TotalHitCountCollector;

import com.b2international.index.IndexException;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.LuceneQueryBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.read.Searcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

/**
 * @since 4.7
 */
public class JsonDocumentSearcher implements Searcher {

	private final ObjectMapper mapper;
	private final IndexSearcher searcher;
	private final ReferenceManager<IndexSearcher> searchers;

	public JsonDocumentSearcher(ReferenceManager<IndexSearcher> searchers, ObjectMapper mapper) {
		this.searchers = searchers;
		this.mapper = mapper;
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
		final org.apache.lucene.search.Query bq = JsonDocumentMapping.matchIdAndType(type, key);
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
	public <T> Iterable<T> search(Query<T> query) throws IOException {
		final Class<T> type = query.getType();
		final org.apache.lucene.search.Query lq = toLuceneQuery(type, query);
		
		final TotalHitCountCollector totalHitCollector = new TotalHitCountCollector();
		searcher.search(lq, totalHitCollector);
		final int totalHits = totalHitCollector.getTotalHits();
		
		if (query.getLimit() < 1 || totalHits < 1) {
			return Collections.emptySet();
		}
		
		final TopFieldDocs topDocs = searcher.search(lq, null, numDocsToRetrieve(query, totalHits), Sort.INDEXORDER, true, false);
		
		if (topDocs.scoreDocs.length < 1) {
			return Collections.emptySet();
		}
		final ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		final ImmutableList.Builder<T> matches = ImmutableList.builder();
		for (int i = query.getOffset(); i < scoreDocs.length; i++) {
			final Document doc = searcher.doc(scoreDocs[i].doc); // TODO: should expand & filter drive fieldsToLoad? Pass custom fieldValueLoader?
			final byte[] source = doc.getField("_source").binaryValue().bytes;
			matches.add(mapper.readValue(source, type));
		}
		return matches.build();
	}

	private int numDocsToRetrieve(Query<?> query, int totalHits) {
		return numDocsToRetrieve(query.getOffset(), query.getLimit(), totalHits);
	}
	
	protected int numDocsToRetrieve(final int offset, final int limit, final int totalHits) {
		return Ints.min(offset + limit, searcher.getIndexReader().maxDoc(), totalHits);
	}

	private <T> org.apache.lucene.search.Query toLuceneQuery(Class<T> root, Query<T> query) {
		return new LuceneQueryBuilder(root).build(
				Expressions.and(
						JsonDocumentMapping.matchType(root),
						query.getWhere()));
	}

	private static boolean isEmpty(TopDocs docs) {
		return docs == null || docs.scoreDocs == null || docs.scoreDocs.length == 0;
	}

}
