/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.query;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

/**
 * @since 5.10
 */
public final class ScoreValueSource extends ValueSource {

	private final Query query;

	public ScoreValueSource(Query query) {
		this.query = query;
	}
	
	@Override
	public void createWeight(Map context, IndexSearcher searcher) throws IOException {
		context.put("weight", searcher.createNormalizedWeight(query, true));
	}
	
	@Override
	public FunctionValues getValues(@SuppressWarnings("rawtypes") Map context, LeafReaderContext readerContext) throws IOException {
		final Weight weight = (Weight) context.get("weight");
		if (weight == null) {
			throw new IllegalStateException("scores are missing");
		}
		final Scorer scorer = weight.scorer(readerContext);
		return new DoubleDocValues(this) {
			@Override
			public double doubleVal(int document) {
				try {
					return scorer.score();
				} catch (IOException exception) {
					throw new RuntimeException(exception);
				}
			}
		};
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (getClass() != o.getClass()) return false;
		ScoreValueSource other = (ScoreValueSource) o;
		return Objects.equals(query, other.query);
	}

	@Override
	public int hashCode() {
		return Objects.hash(query);
	}

	@Override
	public String description() {
		return "score()";
	}

}
