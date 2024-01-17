/*
 * Copyright 2022-2023 B2i Healthcare, https://b2ihealthcare.com
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

import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.function.BiFunction;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.es.query.EsQueryBuilder;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.BoolExpression;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;

/**
 * @since 8.1
 */
public class BoolQueryTest extends BaseIndexTest {

	private static final Logger LOG = LoggerFactory.getLogger(BoolQueryTest.class);

	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(Fixtures.Data.class);
	}
	
	@Test
	public void deepShouldOnlyBooleanQueryShouldBeFlattenedToPreventError() throws Exception {
		// this kind of deep boolean query throws an error in both HTTP and TCP ES clients (flattening solves it for certain searches)
		search(Query.select(Fixtures.Data.class).where(generateDeepBooleanClause(1000, ExpressionBuilder::should)).build());
	}
	
	@Test
	public void deepFilterOnlyBooleanQueryShouldBeFlattenedToPreventError() throws Exception {
		// this kind of deep boolean query throws an error in both HTTP and TCP ES clients (flattening solves it for certain searches)
		search(Query.select(Fixtures.Data.class).where(generateDeepBooleanClause(1000, ExpressionBuilder::filter)).build());
	}
	
	@Test
	public void mergeSingleTermShouldClauses() throws Exception {
		String id1 = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		String id3 = UUID.randomUUID().toString();
		Expression actual = Expressions.bool()
			.should(Expressions.exactMatch("id", id1))
			.should(Expressions.exactMatch("id", id2))
			.should(Expressions.exactMatch("id", id3))
			.build();
		assertEquals(Expressions.matchAny("id", Set.of(id1, id2, id3)), actual);
	}
	
	@Test
	public void mergeSingleAndMultiTermShouldClauses() throws Exception {
		String id1 = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		String id3 = UUID.randomUUID().toString();
		String id4 = UUID.randomUUID().toString();
		Expression actual = Expressions.bool()
			.should(Expressions.exactMatch("id", id1))
			.should(Expressions.exactMatch("id", id2))
			.should(Expressions.matchAny("id", Set.of(id3, id4)))
			.build();
		assertEquals(Expressions.matchAny("id", Set.of(id1, id2, id3, id4)), actual);
	}
	
	@Test
	public void mergeSingleAndMultiTermFilterClauses() throws Exception {
		String id1 = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		String id3 = UUID.randomUUID().toString();
		String id4 = UUID.randomUUID().toString();
		Expression actual = Expressions.bool()
			.filter(Expressions.exactMatch("id", id1))
			.filter(Expressions.matchAny("id", Set.of(id1, id2)))
			.filter(Expressions.matchAny("id", Set.of(id1, id3, id4)))
			.build();
		
		IndexAdmin indexAdmin = index().admin();
		DocumentMapping mapping = indexAdmin.getIndexMapping().getMapping(Data.class);
		Map<String, Object> settings = indexAdmin.settings();
		
		// Single filter matching "id1" should be preserved
		QueryBuilder esQueryBuilder = new EsQueryBuilder(mapping, settings, LOG).build(actual);
		assertEquals(BoolQueryBuilder.NAME, esQueryBuilder.getName());
		assertEquals(1, ((BoolQueryBuilder) esQueryBuilder).filter().size());
	}
	
	@Test
	public void mergeDisjunctTermFilterClauses() throws Exception {
		String id1 = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		String id3 = UUID.randomUUID().toString();
		String id4 = UUID.randomUUID().toString();
		Expression actual = Expressions.bool()
			.filter(Expressions.exactMatch("id", id1))
			.filter(Expressions.matchAny("id", Set.of(id2, id3)))
			.filter(Expressions.matchAny("id", Set.of(id3, id4)))
			.build();
		
		IndexAdmin indexAdmin = index().admin();
		DocumentMapping mapping = indexAdmin.getIndexMapping().getMapping(Data.class);
		Map<String, Object> settings = indexAdmin.settings();
		
		QueryBuilder esQueryBuilder = new EsQueryBuilder(mapping, settings, LOG).build(actual);

		// All three clauses should be eliminated as a single field can not take up 3-4 different values required by the filter
		assertEquals(BoolQueryBuilder.NAME, esQueryBuilder.getName());
		assertEquals(1, ((BoolQueryBuilder) esQueryBuilder).filter().size());
		
		QueryBuilder firstFilter = ((BoolQueryBuilder) esQueryBuilder).filter().get(0);
		assertEquals(TermQueryBuilder.NAME, firstFilter.getName());
		assertEquals("match_none", ((TermQueryBuilder) firstFilter).fieldName());
	}
	
	@Test
	public void mergeDisjunctTermFilterClausesOnCollection() throws Exception {
		String id1 = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		String id3 = UUID.randomUUID().toString();
		String id4 = UUID.randomUUID().toString();
		BoolExpression actual = (BoolExpression) Expressions.bool()
			.filter(Expressions.exactMatch("longSortedSet", id1))
			.filter(Expressions.matchAny("longSortedSet", Set.of(id2, id3)))
			.filter(Expressions.matchAny("longSortedSet", Set.of(id3, id4)))
			.build();

		IndexAdmin indexAdmin = index().admin();
		DocumentMapping mapping = indexAdmin.getIndexMapping().getMapping(Data.class);
		Map<String, Object> settings = indexAdmin.settings();
		
		QueryBuilder esQueryBuilder = new EsQueryBuilder(mapping, settings, LOG).build(actual);
		
		// All three clauses should be preserved as longSortedSet might contain [id1, id2, id4] and thus satisfy all three constraints
		assertEquals(BoolQueryBuilder.NAME, esQueryBuilder.getName());
		assertEquals(3, ((BoolQueryBuilder) esQueryBuilder).filter().size());
	}

	private Expression generateDeepBooleanClause(int depth, BiFunction<ExpressionBuilder, Expression, ExpressionBuilder> boolClause) {
		ExpressionBuilder root = Expressions.bool();
		ExpressionBuilder current = root;
		boolClause.apply(current, Expressions.exactMatch("id", UUID.randomUUID().toString()));
		for (int i = 0; i < depth; i++) {
			ExpressionBuilder nested = Expressions.bool();
			boolClause.apply(nested, Expressions.exactMatch("id", UUID.randomUUID().toString()));
			current.should(nested.build());
			current = nested;
		}
		return root.build();
	}

}
