/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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
import java.util.List;

import org.elasticsearch.core.Map;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.Fixtures.DataWithMap;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;

/**
 * @since 9.0.0
 */
public class DynamicFieldQueryTest extends BaseIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(DataWithMap.class);
	}
	
	@Before
	public void setup() {
		indexDocuments(
			new DataWithMap(KEY1, Map.of(
				"stringField", "hello",
				"longField", 1L
			)),
			new DataWithMap(KEY2, Map.of(
				"stringField", "helloka",
				"longField", 2L
			)),
			new DataWithMap("key3", Map.of(
				"stringField", "hellomi",
				"longField", 3L
			))
		);
	}
	
	@Test
	public void dynamicKeywordFieldTest_Exact() throws Exception {
		Hits<DataWithMap> matches = search(Query.select(DataWithMap.class).where(Expressions.matchDynamic("properties", List.of("stringField#hello"))).build());
		assertThat(matches)
			.extracting(DataWithMap::getProperties)
			.extracting(m -> m.get("stringField"))
			.containsOnly("hello");
	}
	
	@Test
	public void dynamicKeywordFieldTest_In() throws Exception {
		Hits<DataWithMap> matches = search(Query.select(DataWithMap.class).where(Expressions.matchDynamic("properties", List.of("stringField#in:hello,hellomi"))).build());
		assertThat(matches)
			.extracting(DataWithMap::getProperties)
			.extracting(m -> m.get("stringField"))
			.containsOnly("hello", "hellomi");
	}
	
	@Test
	public void dynamicKeywordFieldTest_Range_Interval() throws Exception {
		Hits<DataWithMap> matches = search(Query.select(DataWithMap.class).where(Expressions.matchDynamic("properties", List.of("stringField#range:hello..hellomi"))).build());
		assertThat(matches)
			.extracting(DataWithMap::getProperties)
			.extracting(m -> m.get("stringField"))
			.containsOnly("hello", "helloka", "hellomi");
	}
	
	@Test
	public void dynamicLongFieldTest_Exact() throws Exception {
		Hits<DataWithMap> matches = search(Query.select(DataWithMap.class).where(Expressions.matchDynamic("properties", List.of("longField#1"))).build());
		assertThat(matches)
			.extracting(DataWithMap::getProperties)
			.extracting(m -> m.get("longField"))
			.containsOnly(1); // XXX for smaller numbers deserialization can return integer instead of a long, which is okay
	}
	
	@Test
	public void dynamicLongFieldTest_In() throws Exception {
		Hits<DataWithMap> matches = search(Query.select(DataWithMap.class).where(Expressions.matchDynamic("properties", List.of("longField#in:1,2"))).build());
		assertThat(matches)
			.extracting(DataWithMap::getProperties)
			.extracting(m -> m.get("longField"))
			.containsOnly(1, 2); // XXX for smaller numbers deserialization can return integer instead of a long, which is okay
	}
	
	@Test(expected = BadRequestException.class)
	public void dynamicLongFieldTest_Range_NoRange() throws Exception {
		search(Query.select(DataWithMap.class).where(Expressions.matchDynamic("properties", List.of("longField#range:3"))).build());
	}
	
	@Test(expected = BadRequestException.class)
	public void dynamicLongFieldTest_Range_MultipleRanges() throws Exception {
		search(Query.select(DataWithMap.class).where(Expressions.matchDynamic("properties", List.of("longField#range:3..4..5"))).build());
	}
	
	@Test
	public void dynamicLongFieldTest_Range_Interval() throws Exception {
		Hits<DataWithMap> matches = search(Query.select(DataWithMap.class).where(Expressions.matchDynamic("properties", List.of("longField#range:1..3"))).build());
		assertThat(matches)
			.extracting(DataWithMap::getProperties)
			.extracting(m -> m.get("longField"))
			.containsOnly(1, 2, 3); // XXX for smaller numbers deserialization can return integer instead of a long, which is okay
	}
	
	@Test
	public void dynamicLongFieldTest_Range_Min() throws Exception {
		Hits<DataWithMap> matches = search(Query.select(DataWithMap.class).where(Expressions.matchDynamic("properties", List.of("longField#range:2.."))).build());
		assertThat(matches)
			.extracting(DataWithMap::getProperties)
			.extracting(m -> m.get("longField"))
			.containsOnly(2, 3); // XXX for smaller numbers deserialization can return integer instead of a long, which is okay
	}
	
	@Test
	public void dynamicLongFieldTest_Range_Max() throws Exception {
		Hits<DataWithMap> matches = search(Query.select(DataWithMap.class).where(Expressions.matchDynamic("properties", List.of("longField#range:..2"))).build());
		assertThat(matches)
			.extracting(DataWithMap::getProperties)
			.extracting(m -> m.get("longField"))
			.containsOnly(1, 2); // XXX for smaller numbers deserialization can return integer instead of a long, which is okay
	}

}
