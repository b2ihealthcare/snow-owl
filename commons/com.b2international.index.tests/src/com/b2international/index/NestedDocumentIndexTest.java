/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.Fixtures.NestedData;
import com.b2international.index.Fixtures.ParentData;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.7
 */
public class NestedDocumentIndexTest extends BaseIndexTest {

	@Override
	protected final Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(ParentData.class);
	}
	
	@Test
	public void indexDocumentWithNestedPart() throws Exception {
		final ParentData data = new ParentData(KEY1, "field1", new NestedData("field2"));
		indexDocument(data);
		assertEquals(data, getDocument(ParentData.class, KEY1));
	}
	
	@Test
	public void searchNestedDocument() throws Exception {
		final ParentData data = new ParentData(KEY1, "field1", new NestedData("field2"));
		final ParentData data2 = new ParentData(KEY2, "field1", new NestedData("field2Changed"));
		indexDocuments(data, data2);
		
		final Query<ParentData> query = Query.select(ParentData.class).where(Expressions.nestedMatch("nestedData", Expressions.exactMatch("field2", "field2"))).build();
		final Iterable<ParentData> matches = search(query);
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(data);
	}                                                                                                                                            
	
}
