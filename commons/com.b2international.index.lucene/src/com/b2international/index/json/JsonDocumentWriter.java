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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TermQuery;

import com.b2international.index.write.Writer;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class JsonDocumentWriter implements Writer {

	private final IndexWriter writer;
	private final ObjectMapper mapper;
	private final ReferenceManager<IndexSearcher> searchers;

	public JsonDocumentWriter(IndexWriter writer, ReferenceManager<IndexSearcher> searchers, ObjectMapper mapper) {
		this.writer = writer;
		this.searchers = searchers;
		this.mapper = mapper;
	}
	
	@Override
	public void close() throws Exception {
		// TODO rollback changes if there were exceptions
		searchers.maybeRefreshBlocking();
	}

	@Override
	public void put(String type, String key, Object object) throws IOException {
		final Document doc = new Document();
		doc.add(new StringField("_id", key, Store.YES));
		doc.add(new StringField("_type", type, Store.YES));
		doc.add(new StoredField("_source", mapper.writeValueAsBytes(object)));
		writer.addDocument(doc);
	}

	@Override
	public boolean remove(String type, String key) throws IOException {
		final BooleanQuery bq = new BooleanQuery(true);
		bq.add(new TermQuery(new Term("_id", key)), Occur.MUST);
		bq.add(new TermQuery(new Term("_type", type)), Occur.MUST);
		writer.deleteDocuments(bq);
		// TODO do we need boolean return value here???
		return true;
	}

	@Override
	public <T> boolean remove(Class<T> type, String key) throws IOException {
		return false;
	}

}
