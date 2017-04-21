/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

import org.junit.Test;

import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @since 5.4
 */
public class DecimalFieldTest extends BaseIndexTest {

	private static final BigDecimal REALLY_SMALL = new BigDecimal("1234567890123456789012345678901E-100");
	private static final BigDecimal REALLY_BIG = new BigDecimal("1234567890123456789012345678901E100");
	private static final BigDecimal VALUE_05 = new BigDecimal("0.5");
	private static final BigDecimal VALUE_10 = new BigDecimal("1.0");
	private static final BigDecimal VALUE_20 = new BigDecimal("2.0");
	private static final String KEY3 = "key3";
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.<Class<?>>of(DataWithDecimal.class);
	}
	
	@Override
	public void setup() {
		super.setup();
		indexDocument(KEY1, new DataWithDecimal(VALUE_10));
		indexDocument(KEY2, new DataWithDecimal(VALUE_05));
		indexDocument(KEY3, new DataWithDecimal(VALUE_20));
	}
	
	@Test
	public void searchEquals() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.match("value", VALUE_10))
				.build());
		assertThat(hits).hasSize(1);
		final DataWithDecimal hit = Iterables.getOnlyElement(hits);
		assertEquals(new DataWithDecimal(VALUE_10), hit);
	}
	
	@Test
	public void searchNotEquals() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.builder()
						.mustNot(Expressions.match("value", VALUE_10))
						.build())
				.build());
		assertThat(hits)
			.hasSize(2)
			.containsOnly(new DataWithDecimal(VALUE_05), new DataWithDecimal(VALUE_20));
	}
	
	@Test
	public void searchRange() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.matchRange("value", VALUE_10, VALUE_20, true, true))
				.build());
		assertThat(hits)
			.hasSize(2)
			.containsOnly(new DataWithDecimal(VALUE_10), new DataWithDecimal(VALUE_20));
	}
	
	@Test
	public void searchLessThan() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.matchRange("value", null, VALUE_10, false, false))
				.build());
		assertThat(hits)
			.hasSize(1)
			.containsOnly(new DataWithDecimal(VALUE_05));
	}
	
	@Test
	public void searchGreaterThan() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.matchRange("value", VALUE_10, null, false, false))
				.build());
		assertThat(hits)
			.hasSize(1)
			.containsOnly(new DataWithDecimal(VALUE_20));
	}
	
	@Test
	public void searchLessThanOrEquals() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.matchRange("value", null, VALUE_10, false, true))
				.build());
		assertThat(hits)
			.hasSize(2)
			.containsOnly(new DataWithDecimal(VALUE_05), new DataWithDecimal(VALUE_10));
	}
	
	@Test
	public void searchGreaterThanOrEquals() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.matchRange("value", VALUE_10, null, true, false))
				.build());
		assertThat(hits)
			.hasSize(2)
			.containsOnly(new DataWithDecimal(VALUE_10), new DataWithDecimal(VALUE_20));
	}
	
	@Test
	public void indexReallyBigPositiveDecimal() throws Exception {
		indexDocument(KEY1, new DataWithDecimal(REALLY_BIG));
		final DataWithDecimal actual = getDocument(DataWithDecimal.class, KEY1);
		assertEquals(new DataWithDecimal(REALLY_BIG), actual);
		
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.match("value", REALLY_BIG))
				.build());
		assertThat(hits).hasSize(1);
		final DataWithDecimal hit = Iterables.getOnlyElement(hits);
		assertEquals(new DataWithDecimal(REALLY_BIG), hit);
	}
	
	@Test
	public void indexReallySmallPositiveDecimal() throws Exception {
		final DataWithDecimal expected = new DataWithDecimal(REALLY_SMALL);
		indexDocument(KEY1, expected);
		final DataWithDecimal actual = getDocument(DataWithDecimal.class, KEY1);
		assertEquals(expected, actual);
		
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.match("value", REALLY_SMALL))
				.build());
		assertThat(hits).hasSize(1);
		final DataWithDecimal hit = Iterables.getOnlyElement(hits);
		assertEquals(new DataWithDecimal(REALLY_SMALL), hit);
	}
	
	@Doc
	static class DataWithDecimal {
		
		private final BigDecimal value;
		
		@JsonCreator
		public DataWithDecimal(@JsonProperty("value") BigDecimal value) {
			this.value = checkNotNull(value);
		}
		
		public BigDecimal getValue() {
			return value;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(value);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			DataWithDecimal other = (DataWithDecimal) obj;
			return value.compareTo(other.value) == 0;
		}
		
	}
	
}
