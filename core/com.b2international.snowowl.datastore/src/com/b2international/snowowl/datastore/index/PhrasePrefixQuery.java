/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.index;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.BytesRef;

import com.google.common.collect.Lists;

/**
 * A combination of {@link PhraseQuery} and {@link PrefixQuery} that matches phrases at the beginning of the field only,
 * not at arbitrary offsets.
 * 
 */
public class PhrasePrefixQuery extends MultiPhraseQuery {

	private static final int MAX_TERM_COUNT = 350;

	private boolean rewritten;
	
	@Override
	public Query rewrite(IndexReader reader) {
	    
		if (rewritten) {
			return this;
		}
		
		PhrasePrefixQuery rewrittenQuery = new PhrasePrefixQuery();
		
		final List<Term[]> termArrays = getTermArrays();
		final int[] positions = getPositions();
		final List<Term> matchingTerms = Lists.newArrayList();
		
		for (int i = 0; i < termArrays.size(); i++) {
			
			final Term[] termArray = termArrays.get(i);
			
			for (int j = 0; j < termArray.length; j++) {
				
				IndexReaderContext topReaderContext = reader.getContext();
				for (AtomicReaderContext context : topReaderContext.leaves()) {
					
					try {
						final Fields fields = context.reader().fields();
						if (fields == null) {
							// reader has no fields
							continue;
						}
	
						final Term prefixTerm = termArray[j];
						final Terms terms = fields.terms(prefixTerm.field());
						if (terms == null) {
							// field does not exist
							continue;
						}
						
						TermsEnum termsEnum = null;
						int termCount = 0;
						
						termsEnum = terms.iterator(null);
						BytesRef bytes;
						while((bytes = termsEnum.next()) != null) {
							++termCount;
							
							if (termCount > MAX_TERM_COUNT) {
								break;
							}
							
							if (bytes.utf8ToString().startsWith(prefixTerm.text())) {
								matchingTerms.add(new Term(prefixTerm.field(), bytes));
							}
							
						};
						
						if (matchingTerms.isEmpty()) {
							// Couldn't find prefix candidates for a term, return empty boolean query (matches no documents) as the rewritten query
							return new BooleanQuery();
						}
						
						rewrittenQuery.add(matchingTerms.toArray(new Term[matchingTerms.size()]), positions[i]);
						matchingTerms.clear();
						
					} catch (IOException ignored) {
						// Nothing to do
					}
				}
				
			}
		}
	
		if (rewrittenQuery.getTermArrays().isEmpty()) {
			// No prefix terms could be resolved; return an empty boolean query as above. 
			return new BooleanQuery();
		}

		rewrittenQuery.setBoost(getBoost());
		rewrittenQuery.rewritten = true;
		return rewrittenQuery;
	}

	public PhrasePrefixQuery() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation wraps the {@link Weight} created in the superclass so we can use a custom scorer.
	 * 
	 * @see MultiPhraseQuery.MultiPhraseWeight
	 */
	@Override
	public Weight createWeight(final IndexSearcher searcher) throws IOException {
		return new PhrasePrefixWeight(searcher, this);
	}
}