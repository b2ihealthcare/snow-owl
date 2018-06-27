/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.assertj.core.api.Condition;
import org.junit.Test;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionFixtures.AnalyzedData;
import com.b2international.index.revision.RevisionFixtures.BooleanData;
import com.b2international.index.revision.RevisionFixtures.RevisionData;
import com.b2international.index.revision.RevisionFixtures.RangeData;
import com.b2international.index.revision.RevisionFixtures.ScoredData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * @since 5.0
 */
public class SingleDocumentRevisionIndexSearchTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(RevisionData.class, ScoredData.class, BooleanData.class, RangeData.class, AnalyzedData.class);
	}
	
	@Test
	public void searchMatchAll() throws Exception {
		final RevisionData first = new RevisionData(STORAGE_KEY1, "field1", "field2");
		final RevisionData second = new RevisionData(STORAGE_KEY2, "field1", "field2");
		
		indexRevision(MAIN, first);
		indexRevision(MAIN, second);
		
		final Query<RevisionData> query = Query.select(RevisionData.class).where(Expressions.builder().build()).build();
		final Iterable<RevisionData> matches = search(MAIN, query);
		
		assertThat(matches).hasSize(2);
		assertThat(matches).containsAll(Lists.newArrayList(first, second));
	}
	
	@Test
	public void searchMatchNone() throws Exception {
		final RevisionData first = new RevisionData(STORAGE_KEY1, "field1", "field2");
		final RevisionData second = new RevisionData(STORAGE_KEY2, "field1", "field2");
		
		indexRevision(MAIN, first);
		indexRevision(MAIN, second);
		
		final Query<RevisionData> query = Query.select(RevisionData.class).where(Expressions.matchNone()).build();
		final Iterable<RevisionData> matches = search(MAIN, query);
		
		assertThat(matches).hasSize(0);
	}
	
	@Test
	public void searchDifferentRevisions() throws Exception {
		final RevisionData first = new RevisionData(STORAGE_KEY1, "field1", "field2");
		final RevisionData second = new RevisionData(STORAGE_KEY2, "field1Changed", "field2");
		
		indexRevision(MAIN, first);
		indexRevision(MAIN, second);
		
		final Query<RevisionData> query = Query.select(RevisionData.class).where(Expressions.exactMatch("field1", "field1")).build();
		final Iterable<RevisionData> matches = search(MAIN, query);
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(first);
	}

	@Test
	public void searchMultipleRevisions() throws Exception {
		final RevisionData first = new RevisionData(STORAGE_KEY1, "field1", "field2");
		final RevisionData second = new RevisionData(STORAGE_KEY1, "field1", "field2Changed");
		
		indexRevision(MAIN, first);
		indexRevision(MAIN, second);
		
		final Query<RevisionData> query = Query.select(RevisionData.class).where(Expressions.exactMatch("field1", "field1")).build();
		final Iterable<RevisionData> matches = search(MAIN, query);
		// only second version should match, the first revision should be unaccessible without timestamp
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(second);
	}
	
	
	@Test
	public void searchWithCustomScore() throws Exception {
		final ScoredData first = new ScoredData(STORAGE_KEY1, "field1", "field2", 1.0f);
		final ScoredData second = new ScoredData(STORAGE_KEY2, "field1", "field2.2", 2.0f);
		
		indexRevision(MAIN, first);
		indexRevision(MAIN, second);
		
		final Query<ScoredData> query = Query.select(ScoredData.class).where(Expressions.scriptScore(
				Expressions.exactMatch("field1", "field1"), "doi"))
				.withScores(true)
				.build();
		
		final Iterable<ScoredData> matches = search(MAIN, query);
		
		assertThat(matches).hasSize(2);
		assertThat(matches).contains(first, second);
		assertThat(matches).are(new Condition<ScoredData>() {
			@Override
			public boolean matches(ScoredData input) {
				return input.getScore() == input.getDoi();
			}
		});
	}
	
	@Test
	public void searchWithCustomScoreParams() throws Exception {
		final ScoredData first = new ScoredData(STORAGE_KEY1, "field1", "field2", 1.0f);
		final ScoredData second = new ScoredData(STORAGE_KEY2, "field1", "field2.2", 2.0f);
		
		indexRevision(MAIN, first);
		indexRevision(MAIN, second);
		
		final int factor = 2;
		final Query<ScoredData> query = Query.select(ScoredData.class).where(Expressions.scriptScore(
				Expressions.exactMatch("field1", "field1"), "doiFactor", ImmutableMap.of("factor", factor)))
				.withScores(true)
				.build();
		
		final Iterable<ScoredData> matches = search(MAIN, query);
		
		assertThat(matches).hasSize(factor);
		assertThat(matches).contains(first, second);
		assertThat(matches).are(new Condition<ScoredData>() {
			@Override
			public boolean matches(ScoredData input) {
				return Math.abs((double) factor * input.getDoi() - (double) input.getScore()) < 0.00001;
			}
		});
	}
	
	@Test
	public void searchWithMinimumShouldMatch() throws Exception {
		final RevisionData first = new RevisionData(STORAGE_KEY1, "field1", "field1");
		final RevisionData second = new RevisionData(STORAGE_KEY2, "field1", "field2");
		
		indexRevision(MAIN, first);
		indexRevision(MAIN, second);
		
		final Expression expression = Expressions
				.builder()
				.should(Expressions.exactMatch("field1", "field1"))
				.should(Expressions.exactMatch("field2", "field2"))
				.setMinimumNumberShouldMatch(2)
				.build();
		
		final Query<RevisionData> query = Query.select(RevisionData.class).where(expression).build();
		final Iterable<RevisionData> matches = search(MAIN, query);
		
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(second);
	}
	
	@Test
	public void searchWithPrefix() throws Exception {
		final RevisionData first = new RevisionData(STORAGE_KEY1, "pref1Field1", "field2");
		final RevisionData second = new RevisionData(STORAGE_KEY2, "pref2Field1", "field2");
		
		indexRevision(MAIN, first);
		indexRevision(MAIN, second);
		
		final Query<RevisionData> query= Query.select(RevisionData.class)
				.where(Expressions.prefixMatch("field1", "pref1"))
				.build();
		final Iterable<RevisionData> matches = search(MAIN, query);
		
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(first);
	}
	
	@Test
	public void searchWithFilter() throws Exception{
		final RevisionData first = new RevisionData(STORAGE_KEY1, "field1", "field2");
		final RevisionData second = new RevisionData(STORAGE_KEY2, "field1", "field2");
		
		indexRevision(MAIN, first);
		indexRevision(MAIN, second);
		
		final Expression expression = Expressions.builder().filter(Expressions.exactMatch("field1", "field1")).build();
		final Query<RevisionData> query = Query.select(RevisionData.class).where(expression).build();
		final Iterable<RevisionData> matches = search(MAIN, query);
		
		assertThat(matches).hasSize(2);
		assertThat(matches).containsAll(Lists.newArrayList(first, second));
	}

	@Test
	public void searchWithMatchBool() throws Exception {
		final BooleanData first = new BooleanData(STORAGE_KEY1, "field1", "field2", true);
		final BooleanData second = new BooleanData(STORAGE_KEY2, "field1", "field2", false);
		
		indexRevision(MAIN, first);
		indexRevision(MAIN, second);
		
		final Query<BooleanData> query = Query.select(BooleanData.class).where(Expressions.match("active", true)).build();
		final Iterable<BooleanData> matches = search(MAIN, query);
		
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(first);
	}
	
	@Test
	public void searchWithMatchRange() throws Exception {
		final RangeData first = new RangeData(STORAGE_KEY1, "field1", "field2", 2, 4);
		final RangeData second = new RangeData(STORAGE_KEY2, "field1", "field2", 3, 5);
		
		indexRevision(MAIN, first);
		indexRevision(MAIN, second);
		
		final Expression expression = Expressions.builder()
				.filter(Expressions.matchRange("from", 2, 3))
				.filter(Expressions.matchRange("to", 3, 4))
				.build();
		final Query<RangeData> query = Query.select(RangeData.class).where(expression).build();
		final Iterable<RangeData> matches = search(MAIN, query);
		
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(first);
	}
	
	@Test
	public void searchWithMatchTextAny() {
		final RevisionData first = new RevisionData(STORAGE_KEY1, "a", "field2");
		final RevisionData second = new RevisionData(STORAGE_KEY2, "b", "field2");
		
		indexRevision(MAIN, first);
		indexRevision(MAIN, second);
		
		final Query<RevisionData> query = Query.select(RevisionData.class).where(Expressions.matchTextAny("field1", "a b")).build();
		final Iterable<RevisionData> matches = search(MAIN, query);
		
		assertThat(matches).hasSize(2);
		assertThat(matches).containsAll(Lists.newArrayList(first, second));
	}
	
	@Test
	public void searchWithMatchTextFuzzy() {
		final RevisionData data = new RevisionData(STORAGE_KEY1, "field1", "field2");
		
		indexRevision(MAIN, data);
		
		final Query<RevisionData> query = Query.select(RevisionData.class).where(Expressions.matchTextFuzzy("field1", "field2")).build();
		final Iterable<RevisionData> matches = search(MAIN, query);
		
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(data);
	}
	
}
