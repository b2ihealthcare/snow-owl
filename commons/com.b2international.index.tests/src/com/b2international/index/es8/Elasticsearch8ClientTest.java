/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.es8;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Map;

import org.elasticsearch.core.List;
import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.floats.FloatList;
import com.b2international.index.Doc;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Knn;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.Revision;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

/**
 * @since 8.5
 */
public class Elasticsearch8ClientTest extends BaseRevisionIndexTest {

	@Doc(type = "rev-dense-vector")
	private static final class RevisionWithDenseVector extends Revision {
		
		private final FloatList value;
		
		@JsonCreator
		public RevisionWithDenseVector(@JsonProperty("id") String id, @JsonProperty("value") FloatList value) {
			super(id);
			this.value = value;
		}
		
		public FloatList getValue() {
			return value;
		}
		
	}
	
	@Override
	protected String version() {
		return "8";
	}
		
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	@Override
	protected Map<String, Object> getIndexSettings() {
		return ImmutableMap.<String, Object>builder()
			.putAll(super.getIndexSettings())
			.put("rev-dense-vector", Map.of(
				"mappings", Map.of(
					"properties", Map.of(
						"value", Map.of(
							"type", "dense_vector",
							"index", true,
							"dims", 3,
							"similarity", "cosine"
						)
					)
				)
			))
			.build();
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(RevisionWithDenseVector.class);
	}
	
	@Test
	public void knn_revisions() throws Exception {
		indexRevision(MAIN, 
			new RevisionWithDenseVector(STORAGE_KEY1, PrimitiveLists.newFloatArrayList(0.31f, 0.61f, 0.71f)),
			new RevisionWithDenseVector(STORAGE_KEY2, PrimitiveLists.newFloatArrayList(0.32f, 0.62f, 0.72f))
		);
		String branchA = createBranch(MAIN, "a");
		indexRevision(branchA, 
			new RevisionWithDenseVector(STORAGE_KEY3, PrimitiveLists.newFloatArrayList(0.29f, 0.59f, 0.69f)),
			new RevisionWithDenseVector(STORAGE_KEY4, PrimitiveLists.newFloatArrayList(0.28f, 0.58f, 0.68f))
		);
		
		Hits<RevisionWithDenseVector> matches = index().read(MAIN, searcher -> {
			return searcher.knn(
				Knn
					.select(RevisionWithDenseVector.class)
					.field("value")
					.k(5)
					.numCandidates(5)
					.queryVector(0.3f, 0.6f, 0.7f)
					.filter(Expressions.matchAll())
					.build()
			);
		});
		
		assertThat(matches)
			.extracting(RevisionWithDenseVector::getId)
			.containsOnly(STORAGE_KEY1, STORAGE_KEY2);
		
	}

}
