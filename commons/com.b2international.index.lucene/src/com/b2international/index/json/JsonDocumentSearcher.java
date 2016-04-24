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
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import com.b2international.index.IndexException;
import com.b2international.index.query.Query.AfterWhereBuilder;
import com.b2international.index.query.Query.QueryBuilder;
import com.b2international.index.read.Searcher;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		final BooleanQuery bq = new BooleanQuery(true);
		bq.add(new TermQuery(new Term("_id", key)), Occur.MUST);
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
	public Map<String, Object> get(String type, String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Iterable<T> search(Class<T> type, AfterWhereBuilder query) {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryBuilder query() {
		throw new UnsupportedOperationException();
	}
	
	private static boolean isEmpty(TopDocs docs) {
		return docs == null || docs.scoreDocs == null || docs.scoreDocs.length == 0;
	}

}
