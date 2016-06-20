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
package com.b2international.index.json;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.search.CollectionTerminatedException;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.BytesRef;

/**
 * @since 4.7
 */
class DocSourceCollector extends Collector {

	private final int offset;
	private final int limit;
	
	private final String[] ids;
	private final BytesRef[] sources;

	private BinaryDocValues idValues;
	private BinaryDocValues sourceValues;
	private int currentItem = 0;

	DocSourceCollector(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
		this.ids = new String[limit];
		this.sources = new BytesRef[limit];
	}

	@Override
	public final boolean acceptsDocsOutOfOrder() {
		return false;
	}

	@Override
	public final void setNextReader(final AtomicReaderContext context) throws IOException {
		if (currentItem >= limit) {
			throw new CollectionTerminatedException();
		}
		idValues = context.reader().getBinaryDocValues(JsonDocumentMapping._id().fieldName());
		sourceValues = context.reader().getBinaryDocValues("_source");
		if (sourceValues == null || idValues == null) {
			throw new CollectionTerminatedException();
		}
	}
	
	@Override
	public void collect(int doc) throws IOException {
		if (currentItem < offset) {
			currentItem++;
			return;
		}
		if (currentItem >= limit) {
			throw new CollectionTerminatedException();
		}
		ids[currentItem] = idValues.get(doc).utf8ToString();
		sources[currentItem] = sourceValues.get(doc);
		currentItem++;
	}
	
	@Override
	public void setScorer(Scorer scorer) throws IOException {
		// XXX no scoring support here
	}

	public BytesRef[] getSources() {
		return sources;
	}
	
	public String[] getIds() {
		return ids;
	}
	
}
