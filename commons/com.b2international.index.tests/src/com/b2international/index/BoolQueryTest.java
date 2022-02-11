/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

import org.junit.Test;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;

/**
 * @since 8.1
 */
public class BoolQueryTest extends BaseIndexTest {

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
	public void mergeMultipleSingleTermShouldClauses() throws Exception {
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
	public void mergeMultipleSingleAndMultiTermShouldClauses() throws Exception {
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
