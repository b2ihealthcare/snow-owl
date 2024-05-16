/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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
 * @since 9.2.0
 */
public class WildcardQueryTest extends BaseIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(Data.class);
	}

	@Before
	public void before() {
		Data data1 = new Data(KEY1);
		data1.setField1("fixture term one");
		Data data2 = new Data(KEY2);
		data2.setField1("fixture term two [x]");
		indexDocuments(data1, data2);
	}
	
	@Test
	public void wildcardWithAnyCharacter() throws Exception {
		Hits<Data> matches = search(Query.select(Data.class).where(Expressions.wildcard("field1", "fix*one")).build());
		assertThat(matches).extracting(Data::getId).containsOnly(KEY1);
	}
	
	@Test
	public void wildcardWithQuestionMark() throws Exception {
		Hits<Data> matches = search(Query.select(Data.class).where(Expressions.wildcard("field1", "*o?e*")).build());
		assertThat(matches).extracting(Data::getId).containsOnly(KEY1);
	}
	
	@Test
	public void wildcardWithAnyOtherRegexlikeCharacter() throws Exception {
		Hits<Data> matches = search(Query.select(Data.class).where(Expressions.wildcard("field1", "*[*")).build());
		assertThat(matches).extracting(Data::getId).containsOnly(KEY2);
	}

}
