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
package com.b2international.snowowl.snomed.core.ecl;

import static org.junit.Assert.*;

import java.util.Collection;

import org.eclipse.xtext.parser.IParser;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.query.Expression;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.ecl.EclStandaloneSetup;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @since 5.4
 */
public class EclEvaluatorTest extends BaseRevisionIndexTest {

	private static final String ROOT_ID = Concepts.ROOT_CONCEPT;
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.of(SnomedConceptDocument.class);
	}
	
	private EclEvaluator evaluator;

	@Before
	public void givenEvaluator() {
		final Injector injector = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
		evaluator = new DefaultEclEvaluator(injector.getInstance(IParser.class));
	}
	
	@Test(expected=BadRequestException.class)
	public void syntaxErrorsShouldThrowException() throws Exception {
		evaluator.evaluate("invalid").getSync();	
	}
	
	@Test
	public void self() throws Exception {
		final Expression actual = evaluator.evaluate(ROOT_ID).getSync();
		final Expression expected = RevisionDocument.Expressions.id(ROOT_ID);
		assertEquals(expected, actual);
	}
	
	@Test
	public void selfWithTerm() throws Exception {
		final Expression actual = evaluator.evaluate(String.format("%s|SNOMED CT Root|", ROOT_ID)).getSync();
		final Expression expected = RevisionDocument.Expressions.id(ROOT_ID);
		assertEquals(expected, actual);
	}

}
