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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;

/**
 * Wraps a {@link Weight} instance, allowing to selectively override methods even if the originating Weight class is
 * hidden.
 * 
 */
public class WeightAdapter extends Weight {

	private final Weight wrappedWeight;

	public WeightAdapter(@Nonnull final Weight wrappedWeight) {
		this.wrappedWeight = checkNotNull(wrappedWeight, "wrappedWeight");
	}

	public Explanation explain(final AtomicReaderContext context, final int doc) throws IOException {
		return wrappedWeight.explain(context, doc);
	}

	public Query getQuery() {
		return wrappedWeight.getQuery();
	}

	public float getValueForNormalization() throws IOException {
		return wrappedWeight.getValueForNormalization();
	}

	public void normalize(final float norm, final float topLevelBoost) {
		wrappedWeight.normalize(norm, topLevelBoost);
	}

	public Scorer scorer(final AtomicReaderContext context, final Bits acceptDocs) throws IOException {
		return wrappedWeight.scorer(context, acceptDocs);
	}

	public boolean scoresDocsOutOfOrder() {
		return wrappedWeight.scoresDocsOutOfOrder();
	}
}