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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.Bits;

/**
 * Based on org.apache.lucene.search.MultiPhraseQuery.MultiPhraseWeight.
 * 
 */
class PhrasePrefixWeight extends Weight {
	private final Similarity similarity;
	private final Similarity.SimWeight stats;
	private final Map<Term, TermContext> termContexts = new HashMap<Term, TermContext>();
	private final PhrasePrefixQuery query;

	public PhrasePrefixWeight(IndexSearcher searcher, PhrasePrefixQuery query) throws IOException {
		this.similarity = searcher.getSimilarity();
		this.query = query;
		final IndexReaderContext context = searcher.getTopReaderContext();

		// compute idf
		ArrayList<TermStatistics> allTermStats = new ArrayList<TermStatistics>();
		for (final Term[] terms : query.getTermArrays()) {
			for (Term term : terms) {
				TermContext termContext = termContexts.get(term);
				if (termContext == null) {
					termContext = TermContext.build(context, term);
					termContexts.put(term, termContext);
				}
				allTermStats.add(searcher.termStatistics(term, termContext));
			}
		}
		stats = similarity.computeWeight(query.getBoost(), searcher.collectionStatistics(getField(query)),
				allTermStats.toArray(new TermStatistics[allTermStats.size()]));
	}

	private String getField(PhrasePrefixQuery query) {
		return query.getTermArrays().get(0)[0].field();
	}

	@Override
	public Query getQuery() {
		return query;
	}

	@Override
	public float getValueForNormalization() {
		return stats.getValueForNormalization();
	}

	@Override
	public void normalize(float queryNorm, float topLevelBoost) {
		stats.normalize(queryNorm, topLevelBoost);
	}

	@Override
	public Scorer scorer(AtomicReaderContext context, Bits acceptDocs) throws IOException {
		assert !query.getTermArrays().isEmpty();
		final AtomicReader reader = context.reader();
		final Bits liveDocs = acceptDocs;

		PostingsAndFreq[] postingsFreqs = new PostingsAndFreq[query.getTermArrays().size()];

		final Terms fieldTerms = reader.terms(getField(query));
		if (fieldTerms == null) {
			return null;
		}

		// Reuse single TermsEnum below:
		final TermsEnum termsEnum = fieldTerms.iterator(null);

		for (int pos = 0; pos < postingsFreqs.length; pos++) {
			Term[] terms = query.getTermArrays().get(pos);

			final DocsAndPositionsEnum postingsEnum;
			int docFreq;

			if (terms.length > 1) {
				postingsEnum = new UnionDocsAndPositionsEnum(liveDocs, context, terms, termContexts, termsEnum);

				// coarse -- this overcounts since a given doc can
				// have more than one term:
				docFreq = 0;
				for (int termIdx = 0; termIdx < terms.length; termIdx++) {
					final Term term = terms[termIdx];
					TermState termState = termContexts.get(term).get(context.ord);
					if (termState == null) {
						// Term not in reader
						continue;
					}
					termsEnum.seekExact(term.bytes(), termState);
					docFreq += termsEnum.docFreq();
				}

				if (docFreq == 0) {
					// None of the terms are in this reader
					return null;
				}
			} else {
				final Term term = terms[0];
				TermState termState = termContexts.get(term).get(context.ord);
				if (termState == null) {
					// Term not in reader
					return null;
				}
				termsEnum.seekExact(term.bytes(), termState);
				postingsEnum = termsEnum.docsAndPositions(liveDocs, null, 0);

				if (postingsEnum == null) {
					// term does exist, but has no positions
					assert termsEnum.docs(liveDocs, null, 0) != null : "termstate found but no term exists in reader";
					throw new IllegalStateException("field \"" + term.field()
							+ "\" was indexed without position data; cannot run PhraseQuery (term=" + term.text() + ")");
				}

				docFreq = termsEnum.docFreq();
			}

			postingsFreqs[pos] = new PostingsAndFreq(postingsEnum, docFreq, query.getPositions()[pos], terms);
		}

		// sort by increasing docFreq order
		Arrays.sort(postingsFreqs);
		PhrasePrefixScorer s = new PhrasePrefixScorer(this, postingsFreqs, similarity.simScorer(stats, context));
		if (s.noDocs) {
			return null;
		} else {
			return s;
		}
	}

	@Override
	public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
		return new ComplexExplanation(false, 0.0f, "no matching term");
	}
}