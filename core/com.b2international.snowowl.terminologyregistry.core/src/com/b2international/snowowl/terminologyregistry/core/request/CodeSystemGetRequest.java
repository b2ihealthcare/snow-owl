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
package com.b2international.snowowl.terminologyregistry.core.request;

import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_OID;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_SHORT_NAME;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.datastore.request.BaseResourceRequest;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemEntry;

/**
 * @since 4.7
 */
final class CodeSystemGetRequest extends BaseResourceRequest<BranchContext, CodeSystemEntry> {

	private static final long serialVersionUID = 1L;

	private String uniqueId;
	
	CodeSystemGetRequest() {
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Override
	public CodeSystemEntry execute(final BranchContext context) {
		try {
			return doExecute(context);
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Caught exception while executing code system get request.", e);
		}
	}

	private CodeSystemEntry doExecute(final BranchContext context) throws IOException {
		final TermQuery shortNameQuery = new TermQuery(new Term(SYSTEM_SHORT_NAME, uniqueId));
		final TermQuery oidQuery = new TermQuery(new Term(SYSTEM_OID, uniqueId));

		final BooleanQuery boolQuery = new BooleanQuery();
		boolQuery.add(shortNameQuery, Occur.SHOULD);
		boolQuery.add(oidQuery, Occur.SHOULD);

		final IndexSearcher searcher = context.service(IndexSearcher.class);
		final int totalHits = getTotalHits(searcher, boolQuery);

		if (totalHits == 0) {
			throw new CodeSystemNotFoundException(uniqueId);
		} else if (totalHits > 1) {
			throw new SnowowlRuntimeException(String.format("More than one code system was found with unique ID %s.", uniqueId));
		} else {
			final TopDocs topDocs = searcher.search(boolQuery, 1);
			final Document doc = searcher.doc(topDocs.scoreDocs[0].doc);
			return CodeSystemEntry.builder(doc).build();
		}
	}

	private int getTotalHits(final IndexSearcher searcher, final Query query) throws IOException {
		final TotalHitCountCollector totalCollector = new TotalHitCountCollector();
		searcher.search(query, totalCollector);

		return totalCollector.getTotalHits();
	}

	@Override
	protected Class<CodeSystemEntry> getReturnType() {
		return CodeSystemEntry.class;
	}

}
