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
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.FixedBitSet;

import com.google.common.base.Preconditions;

/**
 * A {@link Collector collector} storing all document ID in a {@link DocIds} instance. 
 * @see DocIds
 * @see DocIdsIterator
 */
public class DocIdCollector extends SimpleCollector {

	private FixedBitSet docIds;
	private int numDocIds;
	private int docBase;

	/**
	 * Factory method for creating {@link DocIdCollector} instance.
	 * @param size the number of documents that are expected to be collected.
	 * <br><b>Note:&nbsp;</b> if more documents are collected, unexpected exceptions may be thrown. Usually clients 
	 * should pass {@link IndexReader#maxDoc()} of the same {@link IndexReader} with which the search is executed.
	 */
	public static DocIdCollector create(final int size) {
		return new DocIdCollector(size);
	}
	
	/**
	 * Creates a document ID collector instance.
	 * @param size the size of the backing bit set. Preferably the maximum document ID. 
	 * @see DocIdCollector#create(int)
	 */
	public DocIdCollector(final int size) {
		docIds = new FixedBitSet(size);
	}
	
	@Override
	public boolean needsScores() {
		return false;
	}
	
	@Override
	public void setScorer(Scorer scorer) throws IOException {
		//intentionally ignored
	}

	@Override
	public void collect(int doc) throws IOException {
		docIds.set(docBase + doc);
		
		++numDocIds;
	}

	@Override
	public void doSetNextReader(LeafReaderContext context) throws IOException {
		docBase = Preconditions.checkNotNull(context, "Atomic reader context argument cannot be null.").docBase;
	}

	/**
	 * Returns with the {@link DocIds} instance.
	 * @return the document IDs.
	 */
	public DocIds getDocIDs() {
		
		return new DocIds() {
			
			@Override public int size() {
				return numDocIds;
			}
			
			@Override public DocIdsIterator iterator() throws IOException {
				return new DocIdsIterator() {
					
					private DocIdSetIterator docIdSetItr = new BitDocIdSet(docIds).iterator();
					private int nextDoc;
					
					@Override public boolean next() {
						
						try {
							
							nextDoc = docIdSetItr.nextDoc();
							return nextDoc != DocIdSetIterator.NO_MORE_DOCS;
							
						} catch (final IOException e) {
							
							//this cannot happen as we are iterating over a bit set
							nextDoc = DocIdSetIterator.NO_MORE_DOCS;
							
							return false;
							
						}
						
					}
					
					@Override public int getDocID() {
						return nextDoc;
					}
				};
			}
			
			@Override public DocIdSet getDocIDs() {
				return new BitDocIdSet(docIds);
			}
		};
		
	}
	
	/**
	 * Document IDs.
	 * @see DocIdsIterator
	 */
	public interface DocIds {
		
	  /**Returns an iterator over the document IDs.*/
	  public DocIdsIterator iterator() throws IOException;

	  /**Returns the set of doc IDs.*/
	  public DocIdSet getDocIDs();

	  /**Returns the number of documents.*/
	  public int size();
		
	}
	
	/**
	 * Iterator over document IDs. Each {@link #next()} retrieves the next document ID which can be later be retrieved by {@link #getDocId()}.
	 * <p><b>NOTE:&nbsp;</b> clients must call {@link #next()} before {@link #getDocId()} otherwise the returned values are unexpected.</p>
	 * 
	 */
	public interface DocIdsIterator {
		
		/**Iterate to the next document. Returns {@code true} if there is such document. Otherwise {@code false}.*/
		boolean next();
		
		/**Returns the ID of the current document.*/
		int getDocID();
		
	}
}
