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

import static com.b2international.snowowl.datastore.index.RevisionDocument.Expressions.id;
import static com.b2international.snowowl.datastore.index.RevisionDocument.Expressions.ids;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.referringRefSet;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Fields.REFERRING_REFSETS;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.ancestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.parents;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.xtext.parser.IParser;
import org.junit.Before;
import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.collections.PrimitiveSets;
import com.b2international.index.Index;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Builder;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.EclStandaloneSetup;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @since 5.4
 */
public class SnomedEclEvaluationRequestTest extends BaseRevisionIndexTest {

	private static final String ROOT_ID = Concepts.ROOT_CONCEPT;
	private static final String OTHER_ID = Concepts.ABBREVIATION;
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.of(SnomedConceptDocument.class, SnomedDescriptionIndexEntry.class, SnomedRelationshipIndexEntry.class, SnomedRefSetMemberIndexEntry.class);
	}
	
	private BranchContext context;

	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	@Before
	public void setup() {
		super.setup();
		final Injector injector = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
		final IParser parser = injector.getInstance(IParser.class);
		context = TestBranchContext.on(MAIN)
				.with(EclParser.class, new DefaultEclParser(parser))
				.with(Index.class, rawIndex())
				.with(RevisionIndex.class, index())
				.build();
	}
	
	@Test(expected=BadRequestException.class)
	public void syntaxErrorsShouldThrowException() throws Exception {
		eval("invalid");
	}
	
	private Expression eval(String expression) {
		return SnomedRequests.prepareEclEvaluation(expression).build().execute(context).getSync();		
	}

	@Test
	public void self() throws Exception {
		final Expression actual = eval(ROOT_ID);
		final Expression expected = RevisionDocument.Expressions.id(ROOT_ID);
		assertEquals(expected, actual);
	}
	
	@Test
	public void selfWithTerm() throws Exception {
		final Expression actual = eval(String.format("%s|SNOMED CT Root|", ROOT_ID));
		final Expression expected = RevisionDocument.Expressions.id(ROOT_ID);
		assertEquals(expected, actual);
	}
	
	@Test
	public void any() throws Exception {
		final Expression actual = eval("*");
		final Expression expected = Expressions.matchAll();
		assertEquals(expected, actual);
	}
	
	@Test
	public void memberOf() throws Exception {
		final Expression actual = eval("^"+Concepts.REFSET_DESCRIPTION_TYPE);
		final Expression expected = referringRefSet(Concepts.REFSET_DESCRIPTION_TYPE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void memberOfAny() throws Exception {
		final Expression actual = eval("^*");
		final Expression expected = Expressions.exists(REFERRING_REFSETS);
		assertEquals(expected, actual);
	}
	
	@Test
	public void descendantOf() throws Exception {
		final Expression actual = eval("<"+ROOT_ID);
		final Expression expected = Expressions.builder()
				.should(parents(Collections.singleton(ROOT_ID)))
				.should(ancestors(Collections.singleton(ROOT_ID)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void descendantOrSelfOf() throws Exception {
		final Expression actual = eval("<<"+ROOT_ID);
		final Expression expected = Expressions.builder()
				.should(ids(Collections.singleton(ROOT_ID)))
				.should(parents(Collections.singleton(ROOT_ID)))
				.should(ancestors(Collections.singleton(ROOT_ID)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void descendantOfMemberOf() throws Exception {
		try {
			eval("<^"+Concepts.REFSET_DESCRIPTION_TYPE);
		} catch (SnowowlRuntimeException e) {
			if (!(e.getCause() instanceof UnsupportedOperationException)) {
				fail("Should throw UnsupportedOperationException until nested expression evaluation is not supported");
			}
		}
	}
	
	@Test
	public void descendantOrSelfOfMemberOf() throws Exception {
		try {
			eval("<<^"+Concepts.REFSET_DESCRIPTION_TYPE);
		} catch (SnowowlRuntimeException e) {
			if (!(e.getCause() instanceof UnsupportedOperationException)) {
				fail("Should throw UnsupportedOperationException until nested expression evaluation is not supported");
			}
		}
	}
	
	@Test
	public void childOf() throws Exception {
		final Expression actual = eval("<!"+ROOT_ID);
		final Expression expected = parents(Collections.singleton(ROOT_ID));
		assertEquals(expected, actual);
	}
	
	@Test
	public void parentOf() throws Exception {
		// SCT Core module has a single parent in this test case
		indexRevision(MAIN, STORAGE_KEY1, doc(Concepts.MODULE_SCT_CORE).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(Concepts.MODULE_ROOT))).build());
		final Expression actual = eval(">!"+Concepts.MODULE_SCT_CORE);
		final Expression expected = ids(Collections.singleton(Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void ancestorOf() throws Exception {
		// SCT Core module has a single parent and a single ancestor in this test case
		indexRevision(MAIN, STORAGE_KEY1, doc(Concepts.MODULE_SCT_CORE)
				.ancestors(PrimitiveSets.newLongOpenHashSet(Long.parseLong(Concepts.ROOT_CONCEPT)))
				.parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(Concepts.MODULE_ROOT)))
				.build());
		final Expression actual = eval(">"+Concepts.MODULE_SCT_CORE);
		final Expression expected = ids(ImmutableSet.of(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void ancestorOrSelfOf() throws Exception {
		// SCT Core module has a single parent and a single ancestor in this test case
		indexRevision(MAIN, STORAGE_KEY1, doc(Concepts.MODULE_SCT_CORE)
				.ancestors(PrimitiveSets.newLongOpenHashSet(Long.parseLong(Concepts.ROOT_CONCEPT)))
				.parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(Concepts.MODULE_ROOT)))
				.build());
		final Expression actual = eval(">>"+Concepts.MODULE_SCT_CORE);
		final Expression expected = ids(ImmutableSet.of(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT, Concepts.MODULE_SCT_CORE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void selfAndOther() throws Exception {
		final Expression actual = eval(ROOT_ID + " AND " + OTHER_ID);
		final Expression expected = Expressions.builder()
				.must(id(ROOT_ID))
				.must(id(OTHER_ID))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void selfAndOtherWithCommaAsOperator() throws Exception {
		final Expression actual = eval(ROOT_ID + " , " + OTHER_ID);
		final Expression expected = Expressions.builder()
				.must(id(ROOT_ID))
				.must(id(OTHER_ID))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void selfOrOther() throws Exception {
		final Expression actual = eval(ROOT_ID + " OR " + OTHER_ID);
		final Expression expected = Expressions.builder()
				.should(id(ROOT_ID))
				.should(id(OTHER_ID))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void selfAndNotOther() throws Exception {
		final Expression actual = eval(ROOT_ID + " MINUS " + OTHER_ID);
		final Expression expected = Expressions.builder()
				.must(id(ROOT_ID))
				.mustNot(id(OTHER_ID))
				.build();
		assertEquals(expected, actual);
	}
	
	private Builder doc(final String id) {
		return SnomedConceptDocument.builder()
				.id(id)
				.iconId(Concepts.ROOT_CONCEPT)
				.active(true)
				.released(true)
				.exhaustive(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.primitive(true)
				.parents(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.ancestors(PrimitiveSets.newLongOpenHashSet())
				.statedParents(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet())
				.referringRefSets(Collections.<String>emptySet())
				.referringMappingRefSets(Collections.<String>emptySet());
	}

}
