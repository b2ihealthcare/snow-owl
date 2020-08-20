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
package com.b2international.index;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.query.Expressions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 5.12.0
 */
public class AggregationsTest extends BaseIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.of(Data.class);
	}
	
	@Test
	public void aggregateOnFieldValue() throws Exception {
		final Data dup1 = new Data();
		dup1.setField1("dup1");
		dup1.setAnalyzedField("duplicate");
		
		final Data dup2 = new Data();
		dup2.setField1("dup2");
		dup2.setAnalyzedField("duplicate");
		
		final Data different = new Data();
		different.setField1("different");
		different.setAnalyzedField("different");
		
		indexDocuments(ImmutableMap.<String, Object>builder()
				.put(KEY1, dup1)
				.put(KEY2, dup2)
				.put("key3", different)
				.build());
		
		final Aggregation<Data> buckets = aggregate(
			AggregationBuilder.bucket("aggregateOnFieldValue", Data.class)
				.query(Expressions.matchAll())
				.onFieldValue("analyzedField.exact")
				.minBucketSize(2)
		);
		
		assertThat(buckets.getBuckets()).hasSize(1);
		assertThat(buckets.getBucket("duplicate")).containsOnly(dup1, dup2);
		
		final Aggregation<String[]> bucketsWithFieldSelect = aggregate(
			AggregationBuilder.bucket("aggregateOnFieldValueSelect", String[].class, Data.class)
				.query(Expressions.matchAll())
				.onFieldValue("analyzedField.exact")
				.fields("field1", "analyzedField")
				.minBucketSize(2)
		);
		
		assertThat(bucketsWithFieldSelect.getBuckets()).hasSize(1);
		assertThat(bucketsWithFieldSelect.getBucket("duplicate")).containsOnly(new String[] {"dup1", "duplicate"}, new String[] {"dup2", "duplicate"});
	}
	
	
	@Test
	public void aggregateOnScriptValue() throws Exception {
		final Data dup1 = new Data();
		dup1.setField1("field1");
		dup1.setField2("field2");
		
		final Data dup2 = new Data();
		dup2.setField1("field1");
		dup2.setField2("field2");
		
		final Data different = new Data();
		different.setField1("differentField1");
		different.setField2("differentField2");
		
		indexDocuments(ImmutableMap.<String, Object>builder()
				.put(KEY1, dup1)
				.put(KEY2, dup2)
				.put("key3", different)
				.build());
		
		final Aggregation<Data> buckets = aggregate(
			AggregationBuilder.bucket("aggregateOnScriptValue", Data.class)
				.query(Expressions.matchAll())
				.onScriptValue("return doc.field1.value + '_' + doc.field2.value")
				.minBucketSize(2)
		);
		
		assertThat(buckets.getBuckets()).hasSize(1);
		assertThat(buckets.getBucket(String.join("_", "field1", "field2"))).containsOnly(dup1, dup2);
	}
	
}
