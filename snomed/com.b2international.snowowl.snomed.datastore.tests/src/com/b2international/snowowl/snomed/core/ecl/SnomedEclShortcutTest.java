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

import java.util.Collection;
import java.util.Set;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.Before;
import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.Index;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snomed.ecl.Ecl;
import com.b2international.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @since 7.7
 */
public class SnomedEclShortcutTest extends BaseRevisionIndexTest {

	private static final Injector INJECTOR = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
	
	private static final String ROOT_ID = Concepts.ROOT_CONCEPT;
	
	private BranchContext context;
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	@Before
	public void setup() {
		SnomedCoreConfiguration config = new SnomedCoreConfiguration();
		config.setConcreteDomainSupported(true);
		
		context = TestBranchContext.on(MAIN)
				.with(EclParser.class, new DefaultEclParser(INJECTOR.getInstance(IParser.class), INJECTOR.getInstance(IResourceValidator.class)))
				.with(EclSerializer.class, new DefaultEclSerializer(INJECTOR.getInstance(ISerializer.class)))
				.with(Index.class, rawIndex())
				.with(RevisionIndex.class, index())
				.with(SnomedCoreConfiguration.class, config)
				.build();
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.of(SnomedConceptDocument.class, SnomedDescriptionIndexEntry.class, SnomedRelationshipIndexEntry.class, SnomedRefSetMemberIndexEntry.class);
	}
	
	private Expression eval(String expression) {
		return new RevisionIndexReadRequest<>(SnomedRequests.prepareEclEvaluation(expression)
				.setExpressionForm(Trees.INFERRED_FORM)
				.build())
				.execute(context)
				.getSync();
	}
	
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
