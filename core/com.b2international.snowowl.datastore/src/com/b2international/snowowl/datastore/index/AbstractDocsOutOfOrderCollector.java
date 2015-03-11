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

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.CollectionTerminatedException;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;

/**
 * Abstract collector without scorer feature. The doc IDs will *NOT* be visited in order.
 */
public abstract class AbstractDocsOutOfOrderCollector extends Collector {

	@Override
	public final void setScorer(final Scorer scorer) throws IOException {
		return;
	}

	@Override
	public final boolean acceptsDocsOutOfOrder() {
		return true;
	}

	@Override
	public final void setNextReader(final AtomicReaderContext context) throws IOException {
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
	protected abstract void initDocValues(AtomicReader leafReader) throws IOException;

	/**
	 * Checks whether all required fields are available for reading.
	 * 
	 * @return {@code true} if the current reader can be used to collect docvalues using {@code docId}s from the
	 * callback method, {@code false} otherwise
	 */
	protected abstract boolean isLeafCollectible();
}
