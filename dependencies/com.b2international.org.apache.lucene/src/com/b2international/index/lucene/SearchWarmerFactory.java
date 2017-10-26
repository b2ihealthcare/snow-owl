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
package com.b2international.index.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TotalHitCountCollector;

/**
 * A customized {@link SearcherFactory} which executes a very light query
 * (matching no known documents) to initialize internal data structures of the
 * returned {@link IndexSearcher}.
 */
public class SearchWarmerFactory extends SearcherFactory {

	private static final String EMPTY_STRING = "";

	@Override
	public IndexSearcher newSearcher(IndexReader reader, IndexReader previousReader) throws IOException {
		IndexSearcher searcher = super.newSearcher(reader, previousReader);

		// TODO: experiment with different queries (MatchAllDocs, a set of "typical" queries, etc.)
		final BooleanQuery.Builder query = new BooleanQuery.Builder();
		query.add(new TermQuery(new Term(EMPTY_STRING, EMPTY_STRING)), Occur.MUST);
		searcher.search(query.build(), new TotalHitCountCollector());

		return searcher;
	}
}
