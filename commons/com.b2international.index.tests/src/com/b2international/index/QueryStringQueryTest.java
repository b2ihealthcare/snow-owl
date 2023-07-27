/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.elasticsearch.core.List;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;

/**
 * @since 8.12
 */
public class QueryStringQueryTest extends BaseIndexTest {

	private static final String KEY3 = "key3";
	
	private Data data1;
	private Data data2;
	private Data data3;

	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(Data.class);
	}

	@Before
	public void setup() {
		data1 = new Data(KEY1);
		data1.setField1("value1");
		data1.setField2("value2");
		
		data2 = new Data(KEY2);
		data2.setField1("value2");
		data2.setField2("value1");
		
		data3 = new Data(KEY3);
		data3.setField1("field1");
		data3.setField2("field2");
		
		indexDocuments(
			data1,
			data2,
			data3
		);
	}
	
	@Test
	public void without_defaultField() throws Exception {
		Hits<String> hits = search(Query.select(String.class)
				.from(Data.class)
				.fields("id")
				.where(Expressions.queryString("value1"))
				.build());
		
		assertThat(hits).containsOnly(KEY1, KEY2);
	}
	
	@Test
	public void with_defaultField() throws Exception {
		Hits<String> hits = search(Query.select(String.class)
				.from(Data.class)
				.fields("id")
				.where(Expressions.queryString("value1", "field1"))
				.build());
		
		assertThat(hits).containsOnly(KEY1);
	}
	
	@Test
	public void explicit_fields() throws Exception {
		Hits<String> hits = search(Query.select(String.class)
				.from(Data.class)
				.fields("id")
				.where(Expressions.queryString("field1:value1"))
				.build());
		
		assertThat(hits).containsOnly(KEY1);
	}
	
	@Test
	public void prefix() throws Exception {
		Hits<String> hits = search(Query.select(String.class)
				.from(Data.class)
				.fields("id")
				.where(Expressions.queryString("field1:value*"))
				.build());
		
		assertThat(hits).containsOnly(KEY1, KEY2);
	}
	
	@Test
	public void fuzzy() throws Exception {
		Hits<String> hits = search(Query.select(String.class)
				.from(Data.class)
				.fields("id")
				.where(Expressions.queryString("field1:valeu1~1"))
				.build());
		
		assertThat(hits).containsOnly(KEY1);
	}

}
