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

import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_SYSTEM_SHORT_NAME;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_VERSION_ID;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.datastore.request.SearchRequest;
import com.b2international.snowowl.terminologyregistry.core.builder.CodeSystemVersionEntryBuilder;
import com.google.common.collect.Lists;

/**
 * @since 4.7
 */
final class CodeSystemVersionSearchRequest extends SearchRequest<CodeSystemVersions> {

	private static final long serialVersionUID = 1L;

	private String codeSystemShortName;
	private String versionId;

	void setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
	}
	
	void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	@Override
	protected CodeSystemVersions doExecute(final BranchContext context) throws IOException {
		final Query query = createQuery();
		final IndexSearcher searcher = context.service(IndexSearcher.class);
		
		final int totalHits = getTotalHits(searcher, query);

		if (totalHits == 0) {
			return new CodeSystemVersions(Collections.<ICodeSystemVersion> emptyList());
		} else {
			final List<ICodeSystemVersion> versions = Lists.newArrayList();
			final TopDocs topDocs = searcher.search(query, totalHits);
			final ScoreDoc[] scoreDocs = topDocs.scoreDocs;

			for (final ScoreDoc scoreDoc : scoreDocs) {
				final Document doc = searcher.doc(scoreDoc.doc);
				versions.add(context.service(CodeSystemVersionEntryBuilder.class).build(doc));
			}

			return new CodeSystemVersions(versions);
		}
	}

	private Query createQuery() {
		final BooleanQuery query = new BooleanQuery();

		if (!StringUtils.isEmpty(codeSystemShortName)) {
			final TermQuery systemShortNameQuery = new TermQuery(new Term(VERSION_SYSTEM_SHORT_NAME, codeSystemShortName));
			query.add(systemShortNameQuery, Occur.MUST);
		}
		
		if (!StringUtils.isEmpty(versionId)) {
			final TermQuery versionIdQuery = new TermQuery(new Term(VERSION_VERSION_ID, versionId));
			query.add(versionIdQuery, Occur.MUST);
		}
		
		if (!query.clauses().isEmpty()) {
			return query;
		} else {
			return new MatchAllDocsQuery();
		}
	}

	@Override
	protected Class<CodeSystemVersions> getReturnType() {
		return CodeSystemVersions.class;
	}

}
