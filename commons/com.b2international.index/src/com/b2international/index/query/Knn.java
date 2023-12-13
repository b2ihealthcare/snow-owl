/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.floats.FloatList;
import com.b2international.commons.exceptions.BadRequestException;

/**
 * @since 8.5
 */
public final class Knn<T> {

	private Class<T> from;
	private String field;
	private Expression filter;
	private int k;
	private int numCandidates;
	private FloatList queryVector;
	
	public Knn() {
	}
	
	public Class<T> getFrom() {
		return from;
	}
	
	public String getField() {
		return field;
	}
	
	public Expression getFilter() {
		return filter;
	}
	
	public int getK() {
		return k;
	}
	
	public int getNumCandidates() {
		return numCandidates;
	}
	
	public FloatList getQueryVector() {
		return queryVector;
	}
	
	public static <T> KnnBuilder<T> select(Class<T> from) {
		return new KnnBuilder<>(from);
	}
	
	public Knn<T> withFilter(Expression filter) {
		return Knn.select(from)
				.field(field)
				.filter(filter)
				.k(k)
				.numCandidates(numCandidates)
				.queryVector(queryVector)
				.build();
	}
	
	public static final class KnnBuilder<T> implements Buildable<Knn<T>> {

		private final Class<T> from;
		private String field;
		private Expression filter;
		private int k;
		private int numCandidates;
		private FloatList queryVector;

		public KnnBuilder(Class<T> from) {
			this.from = from;
		}
		
		public KnnBuilder<T> field(String field) {
			this.field = field;
			return this;
		}
		
		public KnnBuilder<T> filter(Expression filter) {
			this.filter = filter;
			return this;
		}
		
		public KnnBuilder<T> k(int k) {
			this.k = k;
			return this;
		}
		
		public KnnBuilder<T> numCandidates(Integer numCandidates) {
			this.numCandidates = numCandidates != null ? numCandidates : k;
			return this;
		}
		
		public KnnBuilder<T> queryVector(float...values) {
			return queryVector(PrimitiveLists.newFloatArrayList(values));
		}
		
		public KnnBuilder<T> queryVector(FloatList queryVector) {
			this.queryVector = queryVector;
			return this;
		}
		
		@Override
		public Knn<T> build() {
			if (numCandidates < k) {
				throw new BadRequestException("numCandidates ('%s') cannot be less than the selected limit of '%s'.", numCandidates, k);
			}
			
			if (k > 10000) {
				throw new BadRequestException("knn search cannot return more than 10k results.");
			}
			
			if (numCandidates > 10000) {
				throw new BadRequestException("numCandidates cannot be greater than 10000. It was '%s'", numCandidates);
			}
				
				
			Knn<T> knn = new Knn<>();
			knn.from = from;
			knn.field = field;
			knn.filter = filter;
			knn.k = k;
			knn.numCandidates = numCandidates;
			knn.queryVector = queryVector;
			
			
			return knn;
		}
		
		
		
	}

}
