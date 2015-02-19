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

import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity.SimScorer;

/**
 * A modification of {@link org.apache.lucene.search.ExactPhraseScorer} that
 * does not allow offsets different from zero (ie., the matched field has to
 * begin with the matched phrase). Repeated here instead of subclassed because
 * of package visibility.
 * <p>
 * Changes:
 * <ul>
 * <li>generation counting, chunk-by-chunk iteration removed
 * <li>position counting removed
 * <li>only matches at position 0 are accepted and counted
 * </ul>
 * 
 */
public class PhrasePrefixScorer extends Scorer {

	private final int endMinus1;

	private final static int CHUNK = 4096;

	private int pos0Count;

	boolean noDocs;

	private final static class ChunkState {
		final DocsAndPositionsEnum posEnum;
		final int offset;
		final boolean useAdvance;
		int posUpto;
		int posLimit;
		int pos;
		int lastPos;

		public ChunkState(DocsAndPositionsEnum posEnum, int offset, boolean useAdvance) {
			this.posEnum = posEnum;
			this.offset = offset;
			this.useAdvance = useAdvance;
		}
	}

	private final ChunkState[] chunkStates;

	private int docID = -1;
	private int freq;

	private final SimScorer docScorer;

	PhrasePrefixScorer(Weight weight, PostingsAndFreq[] postings, SimScorer docScorer)
			throws IOException {
		super(weight);
		this.docScorer = docScorer;

		chunkStates = new ChunkState[postings.length];

		endMinus1 = postings.length - 1;

		for (int i = 0; i < postings.length; i++) {

			// Coarse optimization: advance(target) is fairly
			// costly, so, if the relative freq of the 2nd
			// rarest term is not that much (> 1/5th) rarer than
			// the first term, then we just use .nextDoc() when
			// ANDing. This buys ~15% gain for phrases where
			// freq of rarest 2 terms is close:
			final boolean useAdvance = postings[i].docFreq > 5 * postings[0].docFreq;
			chunkStates[i] = new ChunkState(postings[i].postings, -postings[i].position, useAdvance);
			if (i > 0 && postings[i].postings.nextDoc() == DocIdSetIterator.NO_MORE_DOCS) {
				noDocs = true;
				return;
			}
		}
	}

	@Override
	public int nextDoc() throws IOException {
		while (true) {

			// first (rarest) term
			final int doc = chunkStates[0].posEnum.nextDoc();
			if (doc == DocIdSetIterator.NO_MORE_DOCS) {
				docID = doc;
				return doc;
			}

			// not-first terms
			int i = 1;
			while (i < chunkStates.length) {
				final ChunkState cs = chunkStates[i];
				int doc2 = cs.posEnum.docID();
				if (cs.useAdvance) {
					if (doc2 < doc) {
						doc2 = cs.posEnum.advance(doc);
					}
				} else {
					int iter = 0;
					while (doc2 < doc) {
						// safety net -- fallback to .advance if we've
						// done too many .nextDocs
						if (++iter == 50) {
							doc2 = cs.posEnum.advance(doc);
							break;
						} else {
							doc2 = cs.posEnum.nextDoc();
						}
					}
				}
				if (doc2 > doc) {
					break;
				}
				i++;
			}

			if (i == chunkStates.length) {
				// this doc has all the terms -- now test whether
				// phrase occurs
				docID = doc;

				freq = phraseFreq();
				if (freq != 0) {
					return docID;
				}
			}
		}
	}

	@Override
	public int advance(int target) throws IOException {

		// first term
		int doc = chunkStates[0].posEnum.advance(target);
		if (doc == DocIdSetIterator.NO_MORE_DOCS) {
			docID = DocIdSetIterator.NO_MORE_DOCS;
			return doc;
		}

		while (true) {

			// not-first terms
			int i = 1;
			while (i < chunkStates.length) {
				int doc2 = chunkStates[i].posEnum.docID();
				if (doc2 < doc) {
					doc2 = chunkStates[i].posEnum.advance(doc);
				}
				if (doc2 > doc) {
					break;
				}
				i++;
			}

			if (i == chunkStates.length) {
				// this doc has all the terms -- now test whether
				// phrase occurs
				docID = doc;
				freq = phraseFreq();
				if (freq != 0) {
					return docID;
				}
			}

			doc = chunkStates[0].posEnum.nextDoc();
			if (doc == DocIdSetIterator.NO_MORE_DOCS) {
				docID = doc;
				return doc;
			}
		}
	}

	@Override
	public String toString() {
		return "ExactPhraseScorer(" + weight + ")";
	}

	@Override
	public int freq() {
		return freq;
	}

	@Override
	public int docID() {
		return docID;
	}

	@Override
	public float score() {
		return docScorer.score(docID, freq);
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.DocIdSetIterator#cost()
	 */
	@Override
	public long cost() {
		return 0;
	}

	private int phraseFreq() throws IOException {

		freq = 0;

		// init chunks
		for (int i = 0; i < chunkStates.length; i++) {
			final ChunkState cs = chunkStates[i];
			cs.posLimit = cs.posEnum.freq();
			cs.pos = cs.offset + cs.posEnum.nextPosition();
			cs.posUpto = 1;
			cs.lastPos = -1;
		}

		int chunkStart = 0;
		int chunkEnd = CHUNK;

		// TODO: we could fold in chunkStart into offset and
		// save one subtract per pos incr

		// first term
			{
				final ChunkState cs = chunkStates[0];
				while (cs.pos < chunkEnd) {
					if (cs.pos > cs.lastPos) {
						cs.lastPos = cs.pos;
						final int posIndex = cs.pos - chunkStart;
						
						if (0 != posIndex) {
							return freq;
						}
						
						pos0Count = 1;
					}

					if (cs.posUpto == cs.posLimit) {
						break;
					}
					cs.posUpto++;
					cs.pos = cs.offset + cs.posEnum.nextPosition();
				}
			}

			// middle terms
			boolean any = true;
			for (int t = 1; t < endMinus1; t++) {
				final ChunkState cs = chunkStates[t];
				any = false;
				while (cs.pos < chunkEnd) {
					if (cs.pos > cs.lastPos) {
						cs.lastPos = cs.pos;
						final int posIndex = cs.pos - chunkStart;
						if (posIndex >= 0 && pos0Count == t) {
							// viable
							pos0Count++;
							any = true;
						}
					}

					if (cs.posUpto == cs.posLimit) {
						break;
					}
					cs.posUpto++;
					cs.pos = cs.offset + cs.posEnum.nextPosition();
				}

				if (!any) {
					break;
				}
			}

			if (!any) {
				// petered out for this chunk
				return freq;
			}

			// last term
			if (0 == endMinus1 && 1 == pos0Count) {
				freq++;
			} else {
				final ChunkState cs = chunkStates[endMinus1];
				while (cs.pos < chunkEnd) {
					if (cs.pos > cs.lastPos) {
						cs.lastPos = cs.pos;
						final int posIndex = cs.pos - chunkStart;
						if (posIndex == 0 && pos0Count == endMinus1) {
							freq++;
						}
					}

					if (cs.posUpto == cs.posLimit) {
						break;
					}
					cs.posUpto++;
					cs.pos = cs.offset + cs.posEnum.nextPosition();
				}
			}

		return freq;
	}
}