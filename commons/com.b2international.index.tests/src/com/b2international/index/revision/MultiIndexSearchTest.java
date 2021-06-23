/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.elasticsearch.common.collect.List;
import org.junit.Test;

import com.b2international.index.Doc;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 8.0
 */
public class MultiIndexSearchTest extends BaseRevisionIndexTest {

	@Doc
	public static class RevisionTypeA extends Revision {

		private final String field1;
		private final String field2;

		@JsonCreator
		public RevisionTypeA(
				@JsonProperty("id") String id, 
				@JsonProperty("field1") String field1, 
				@JsonProperty("field2") String field2) {
			super(id);
			this.field1 = field1;
			this.field2 = field2;
		}
		
		public String getField1() {
			return field1;
		}
		
		public String getField2() {
			return field2;
		}
		
	}
	
	@Doc
	public static class RevisionTypeB extends Revision {

		private final String field1;
		private final String field3;

		@JsonCreator
		public RevisionTypeB(
				@JsonProperty("id") String id, 
				@JsonProperty("field1") String field1, 
				@JsonProperty("field3") String field3) {
			super(id);
			this.field1 = field1;
			this.field3 = field3;
		}
		
		public String getField1() {
			return field1;
		}
		
		public String getField3() {
			return field3;
		}
		
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(RevisionTypeA.class, RevisionTypeB.class);
	}
	
	@Test
	public void search() throws Exception {
		indexRevision(
			MAIN, 
			new RevisionTypeA(STORAGE_KEY1, "field1_A1", "field2_A1"),
			new RevisionTypeA(STORAGE_KEY2, "field1_A2", "field2_A2"),
			new RevisionTypeB(STORAGE_KEY3, "field1_B1", "field3_B1"),
			new RevisionTypeB(STORAGE_KEY4, "field1_B2", "field3_B2")
		);
		Hits<String> hits = search(MAIN, Query.select(String.class)
				.fields(Revision.Fields.ID)
				.from(RevisionTypeA.class, RevisionTypeB.class)
				.where(Expressions.matchAll())
				.build());
		assertThat(hits).containsExactly(STORAGE_KEY1, STORAGE_KEY2, STORAGE_KEY3, STORAGE_KEY4);
	}
	
	@Test
	public void sort() throws Exception {
		indexRevision(
			MAIN, 
			new RevisionTypeA(STORAGE_KEY1, "field1_A1", "field2_A1"),
			new RevisionTypeA(STORAGE_KEY2, "field1_A2", "field2_A2"),
			new RevisionTypeB(STORAGE_KEY3, "field1_B1", "field3_B1"),
			new RevisionTypeB(STORAGE_KEY4, "field1_B2", "field3_B2")
		);
		Hits<String> hits = search(MAIN, Query.select(String.class)
				.fields(Revision.Fields.ID)
				.from(RevisionTypeA.class, RevisionTypeB.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("field1", Order.DESC))
				.build());
		assertThat(hits)
			.containsExactly(STORAGE_KEY4, STORAGE_KEY3, STORAGE_KEY2, STORAGE_KEY1);
	}
	
}
