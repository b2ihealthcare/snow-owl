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

import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.CollectionTerminatedException;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.SimpleCollector;

/**
 * Abstract collector without scorer feature. The doc IDs will *NOT* be visited in order.
 */
public abstract class AbstractDocsOutOfOrderCollector extends SimpleCollector {

	@Override
	public final void setScorer(final Scorer scorer) throws IOException {
	}
	
	@Override
	public boolean needsScores() {
		return false;
	}

	@Override
	public final void doSetNextReader(final LeafReaderContext context) throws IOException {
		initDocValues(context.reader());

		if (!isLeafCollectible()) {
			throw new CollectionTerminatedException();
		}
	}

	/**
	 * Initializes docvalues using the specified leaf reader.
	 * 
	 * @param leafReader the {@link AtomicReader} to use for retrieving docvalues
	 */
	protected abstract void initDocValues(LeafReader leafReader) throws IOException;

	/**
	 * Checks whether all required fields are available for reading.
	 * 
	 * @return {@code true} if the current reader can be used to collect docvalues using {@code docId}s from the
	 * callback method, {@code false} otherwise
	 */
	protected abstract boolean isLeafCollectible();
}
