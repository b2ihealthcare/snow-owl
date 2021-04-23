/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.4
 */
public class DecimalFieldTest extends BaseIndexTest {

	private static final BigDecimal REALLY_SMALL = new BigDecimal("1234567890123456789012345678901E-100");
	private static final BigDecimal REALLY_BIG = new BigDecimal("1234567890123456789012345678901E100");
	private static final BigDecimal VALUE_00 = new BigDecimal("0.0");
	private static final BigDecimal VALUE_05 = new BigDecimal("0.5");
	private static final BigDecimal VALUE_10 = new BigDecimal("1.0");
	private static final BigDecimal VALUE_20 = new BigDecimal("2.0");
	private static final String KEY3 = "key3";
	private static final String KEY4 = "key4";
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return Set.of(DataWithDecimal.class);
	}

	@Before
	public void setup() {
		indexDocuments(
			new DataWithDecimal(KEY1, VALUE_10),
			new DataWithDecimal(KEY2, VALUE_05),
			new DataWithDecimal(KEY3, VALUE_20),
			new DataWithDecimal(KEY4, VALUE_00)
		);
	}
	
	@Test
	public void searchEquals() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.match("value", VALUE_10))
				.build());
		assertThat(hits)
			.containsOnly(new DataWithDecimal(KEY1, VALUE_10));
	}
	
	@Test
	public void searchNotEquals() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.builder()
						.mustNot(Expressions.match("value", VALUE_10))
						.build())
				.build());
		assertThat(hits)
			.hasSize(3)
			.containsOnly(
				new DataWithDecimal(KEY4, VALUE_00),
				new DataWithDecimal(KEY2, VALUE_05), 
				new DataWithDecimal(KEY3, VALUE_20)
			);
	}
	
	@Test
	public void searchRange() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.matchRange("value", VALUE_10, VALUE_20, true, true))
				.build());
		assertThat(hits)
			.hasSize(2)
			.containsOnly(new DataWithDecimal(KEY1, VALUE_10), new DataWithDecimal(KEY3, VALUE_20));
	}
	
	@Test
	public void searchLessThan() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.matchRange("value", null, VALUE_10, false, false))
				.build());
		assertThat(hits)
			.hasSize(2)
			.containsOnly(
				new DataWithDecimal(KEY4, VALUE_00),
				new DataWithDecimal(KEY2, VALUE_05)
			);
	}
	
	@Test
	public void searchGreaterThan() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.matchRange("value", VALUE_10, null, false, false))
				.build());
		assertThat(hits)
			.hasSize(1)
			.containsOnly(new DataWithDecimal(KEY3, VALUE_20));
	}
	
	@Test
	public void searchLessThanOrEquals() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.matchRange("value", null, VALUE_10, false, true))
				.build());
		assertThat(hits)
			.hasSize(3)
			.containsOnly(
				new DataWithDecimal(KEY4, VALUE_00),
				new DataWithDecimal(KEY2, VALUE_05), 
				new DataWithDecimal(KEY1, VALUE_10)
			);
	}
	
	@Test
	public void searchGreaterThanOrEquals() throws Exception {
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.matchRange("value", VALUE_10, null, true, false))
				.build());
		assertThat(hits)
			.hasSize(2)
			.containsOnly(new DataWithDecimal(KEY1, VALUE_10), new DataWithDecimal(KEY3, VALUE_20));
	}
	
	@Test
	public void indexReallyBigPositiveDecimal() throws Exception {
		indexDocument(new DataWithDecimal(KEY1, REALLY_BIG));
		final DataWithDecimal actual = getDocument(DataWithDecimal.class, KEY1);
		assertEquals(new DataWithDecimal(KEY1, REALLY_BIG), actual);
		
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.match("value", REALLY_BIG))
				.build());
		assertThat(hits)
			.containsOnly(new DataWithDecimal(KEY1, REALLY_BIG));
	}
	
	@Test
	public void indexReallySmallPositiveDecimal() throws Exception {
		final DataWithDecimal expected = new DataWithDecimal(KEY1, REALLY_SMALL);
		indexDocument(expected);
		final DataWithDecimal actual = getDocument(DataWithDecimal.class, KEY1);
		assertEquals(expected, actual);
		
		final Hits<DataWithDecimal> hits = search(Query.select(DataWithDecimal.class)
				.where(Expressions.match("value", REALLY_SMALL))
				.build());
		assertThat(hits)
			.containsOnly(new DataWithDecimal(KEY1, REALLY_SMALL));
	}
	
	@Doc
	static class DataWithDecimal {
		
		@ID
		private final String id;
		
		private final BigDecimal value;
		
		@JsonCreator
		public DataWithDecimal(@JsonProperty("id") String id, @JsonProperty("value") BigDecimal value) {
			this.id = id;
			this.value = checkNotNull(value);
		}
		
		public String getId() {
			return id;
		}
		
		public BigDecimal getValue() {
			return value;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(id, value);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			DataWithDecimal other = (DataWithDecimal) obj;
			return Objects.equals(id, other.id) && value.compareTo(other.value) == 0;
		}
		
	}
	
}
