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
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.PriorityQueue;

/**
 * Repeated here because of package visibility.
 * 
 */
class UnionDocsAndPositionsEnum extends DocsAndPositionsEnum {

	private static final class DocsQueue extends PriorityQueue<DocsAndPositionsEnum> {
		DocsQueue(List<DocsAndPositionsEnum> docsEnums) throws IOException {
			super(docsEnums.size());

			Iterator<DocsAndPositionsEnum> i = docsEnums.iterator();
			while (i.hasNext()) {
				DocsAndPositionsEnum postings = i.next();
				if (postings.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
					add(postings);
				}
			}
		}

		@Override
		public final boolean lessThan(DocsAndPositionsEnum a, DocsAndPositionsEnum b) {
			return a.docID() < b.docID();
		}
	}

	private static final class IntQueue {
		private int _arraySize = 16;
		private int _index = 0;
		private int _lastIndex = 0;
		private int[] _array = new int[_arraySize];

		final void add(int i) {
			if (_lastIndex == _arraySize)
				growArray();

			_array[_lastIndex++] = i;
		}

		final int next() {
			return _array[_index++];
		}

		final void sort() {
			Arrays.sort(_array, _index, _lastIndex);
		}

		final void clear() {
			_index = 0;
			_lastIndex = 0;
		}

		final int size() {
			return (_lastIndex - _index);
		}

		private void growArray() {
			int[] newArray = new int[_arraySize * 2];
			System.arraycopy(_array, 0, newArray, 0, _arraySize);
			_array = newArray;
			_arraySize *= 2;
		}
	}

	private int _doc;
	private int _freq;
	private DocsQueue _queue;
	private IntQueue _posList;

	public UnionDocsAndPositionsEnum(Bits liveDocs, AtomicReaderContext context, Term[] terms,
			Map<Term, TermContext> termContexts, TermsEnum termsEnum) throws IOException {
		List<DocsAndPositionsEnum> docsEnums = new LinkedList<DocsAndPositionsEnum>();
		for (int i = 0; i < terms.length; i++) {
			final Term term = terms[i];
			TermState termState = termContexts.get(term).get(context.ord);
			if (termState == null) {
				// Term doesn't exist in reader
				continue;
			}
			termsEnum.seekExact(term.bytes(), termState);
			DocsAndPositionsEnum postings = termsEnum.docsAndPositions(liveDocs, null, 0);
			if (postings == null) {
				// term does exist, but has no positions
				throw new IllegalStateException("field \"" + term.field()
						+ "\" was indexed without position data; cannot run PhraseQuery (term=" + term.text() + ")");
			}
			docsEnums.add(postings);
		}

		_queue = new DocsQueue(docsEnums);
		_posList = new IntQueue();
	}

	@Override
	public final int nextDoc() throws IOException {
		if (_queue.size() == 0) {
			return NO_MORE_DOCS;
		}

		// TODO: move this init into positions(): if the search
		// doesn't need the positions for this doc then don't
		// waste CPU merging them:
		_posList.clear();
		_doc = _queue.top().docID();

		// merge sort all positions together
		DocsAndPositionsEnum postings;
		do {
			postings = _queue.top();

			final int freq = postings.freq();
			for (int i = 0; i < freq; i++) {
				_posList.add(postings.nextPosition());
			}

			if (postings.nextDoc() != NO_MORE_DOCS) {
				_queue.updateTop();
			} else {
				_queue.pop();
			}
		} while (_queue.size() > 0 && _queue.top().docID() == _doc);

		_posList.sort();
		_freq = _posList.size();

		return _doc;
	}

	@Override
	public int nextPosition() {
		return _posList.next();
	}

	@Override
	public int startOffset() {
		return -1;
	}

	@Override
	public int endOffset() {
		return -1;
	}

	@Override
	public BytesRef getPayload() {
		return null;
	}

	@Override
	public final int advance(int target) throws IOException {
		while (_queue.top() != null && target > _queue.top().docID()) {
			DocsAndPositionsEnum postings = _queue.pop();
			if (postings.advance(target) != NO_MORE_DOCS) {
				_queue.add(postings);
			}
		}
		return nextDoc();
	}

	@Override
	public final int freq() {
		return _freq;
	}

	@Override
	public final int docID() {
		return _doc;
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.DocIdSetIterator#cost()
	 */
	@Override
	public long cost() {
		return 0;
	}
}