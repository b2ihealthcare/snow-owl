/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ecl;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.snomed.ecl.Ecl;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.google.common.collect.ImmutableSet;

/**
 * @since 7.7
 */
public class SnomedEclShortcutTest extends BaseSnomedEclEvaluationRequestTest {

	public SnomedEclShortcutTest() {
		super(Trees.INFERRED_FORM, false);
	}

	private static final String ROOT_ID = Concepts.ROOT_CONCEPT;
	
	@Test
	public void queryMinusAll() throws Exception {
		final Expression actual = eval(ROOT_ID + " MINUS *");
		final Expression expected = Expressions.matchNone();
		assertEquals(expected, actual);
	}
	
	@Test
	public void queryMinusNestedAll() throws Exception {
		final Expression actual = eval(ROOT_ID + " MINUS (*)");
		final Expression expected = Expressions.matchNone();
		assertEquals(expected, actual);
	}
	
	@Test
	public void queryOrAll() throws Exception {
		final Expression actual = eval(ROOT_ID + " OR *");
		final Expression expected = Expressions.matchAll();
		assertEquals(expected, actual);
	}
	
	@Test
	public void queryAndAll() throws Exception {
		final Expression actual = eval(ROOT_ID + " AND *");
		final Expression expected = SnomedConceptDocument.Expressions.id(ROOT_ID);
		assertEquals(expected, actual);
	}
	
	@Test
	public void queryAndNestedAll() throws Exception {
		final Expression actual = eval(ROOT_ID + " AND (*)");
		final Expression expected = SnomedConceptDocument.Expressions.id(ROOT_ID);
		assertEquals(expected, actual);
	}
	
	@Test
	public void idsOnlyOrExpression() throws Exception {
		final Set<String> ids = ImmutableSet.of(ROOT_ID, Concepts.ABBREVIATION, Concepts.ACCEPTABILITY, Concepts.AMBIGUOUS);
		final Expression actual = eval(Ecl.or(ids));
		final Expression expected = SnomedConceptDocument.Expressions.ids(ids);
		assertEquals(expected, actual);
	}
	
	@Test
	public void idsOnlyAndExpression() throws Exception {
		final Set<String> ids = ImmutableSet.of(ROOT_ID, Concepts.ABBREVIATION, Concepts.ACCEPTABILITY, Concepts.AMBIGUOUS);
		final Expression actual = eval(Ecl.and(ids));
		final Expression expected = Expressions.matchNone();
		assertEquals(expected, actual);
	}
	
}
