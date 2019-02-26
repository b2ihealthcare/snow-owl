/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ql;

import static com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator.generateDescriptionId;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.Before;
import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.Index;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.MatchNone;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclParser;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclSerializer;
import com.b2international.snowowl.snomed.core.ecl.EclParser;
import com.b2international.snowowl.snomed.core.ecl.EclSerializer;
import com.b2international.snowowl.snomed.core.ecl.TestBranchContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.snomed.ql.QLStandaloneSetup;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @since 6.13
 */
public class SnomedQueryEvaluationRequestTest extends BaseRevisionIndexTest {

	private static final String ROOT_ID = Concepts.ROOT_CONCEPT;
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.of(SnomedConceptDocument.class, SnomedDescriptionIndexEntry.class, SnomedRelationshipIndexEntry.class, SnomedRefSetMemberIndexEntry.class);
	}
	
	private BranchContext context;

	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	@Before
	public void setup() {
		super.setup();
		final Injector eclInjector = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
		final Injector queryInjector = new QLStandaloneSetup().createInjectorAndDoEMFRegistration();
		context = TestBranchContext.on(MAIN)
				.with(EclParser.class, new DefaultEclParser(eclInjector.getInstance(IParser.class), eclInjector.getInstance(IResourceValidator.class)))
				.with(EclSerializer.class, new DefaultEclSerializer(eclInjector.getInstance(ISerializer.class)))
				.with(SnomedQueryParser.class, new DefaultSnomedQueryParser(queryInjector.getInstance(IParser.class), queryInjector.getInstance(IResourceValidator.class)))
				.with(SnomedQuerySerializer.class, new DefaultSnomedQuerySerializer(queryInjector.getInstance(ISerializer.class)))
				.with(Index.class, rawIndex())
				.with(RevisionIndex.class, index())
				.build();
	}
	
	private Expression eval(String expression) {
		return new RevisionIndexReadRequest<>(SnomedRequests.prepareQueryEvaluation(expression).build())
				.execute(context)
				.getSync();		
	}
	
	@Test(expected = BadRequestException.class)
	public void syntaxError() throws Exception {
		eval("invalid");
	}
	
	@Test(expected = BadRequestException.class)
	public void _null() throws Exception {
		eval(null);
	}
	
	@Test
	public void empty() throws Exception {
		final Expression actual = eval("");
		final Expression expected = MatchNone.INSTANCE;
		assertEquals(expected, actual);
	}
	
	@Test
	public void whitespaces() {
		final Expression actual = eval(" \n \t");
		final Expression expected = MatchNone.INSTANCE;
		assertEquals(expected, actual);
	}
	
	@Test
	public void any() throws Exception {
		final Expression actual = eval("*");
		final Expression expected = Expressions.matchAll();
		assertEquals(expected, actual);
	}
	
	@Test
	public void selfWithTerm() throws Exception {
		final Expression actual = eval(String.format("%s|SNOMED CT Root|", ROOT_ID));
		final Expression expected = RevisionDocument.Expressions.id(ROOT_ID);
		assertEquals(expected, actual);
	}
	
	@Test
	public void activeOnly() throws Exception {
		final Expression actual = eval("* {{ active=true }}");
		final Expression expected = SnomedDocument.Expressions.active();
		assertEquals(expected, actual);
	}
	
	@Test
	public void inactiveOnly() throws Exception {
		final Expression actual = eval("* {{ active=false }}");
		final Expression expected = SnomedDocument.Expressions.inactive();
		assertEquals(expected, actual);
	}
	
	@Test
	public void moduleFilter() throws Exception {
		final Expression actual = eval("* {{ moduleId= "+Concepts.MODULE_SCT_CORE+" }}");
		final Expression expected = SnomedDocument.Expressions.modules(ImmutableList.of(Concepts.MODULE_SCT_CORE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void termFilter() throws Exception {
		
		indexRevision(MAIN, nextStorageKey(), SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.SYNONYM)
				.build());
		
		final Expression actual = eval("* {{ term = \"Clin find\" }}");
		final Expression expected = SnomedDocument.Expressions.ids(ImmutableList.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test(expected = BadRequestException.class)
	public void termFilterLessThanTwoChars() throws Exception {
		eval("* {{ term = \"C\" }}");
	}
	
	@Test
	public void conjunctionActiveAndModuleId() throws Exception {
		final Expression actual = eval("* {{ active=true, moduleId = "+ Concepts.MODULE_SCT_CORE +" }}");
		final Expression expected = Expressions.builder()
				.filter(SnomedDocument.Expressions.active())
				.filter(SnomedDocument.Expressions.modules(ImmutableList.of(Concepts.MODULE_SCT_CORE)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void disjunctionActiveAndModuleId() throws Exception {
		final Expression actual = eval("* {{ active=true OR moduleId = "+ Concepts.MODULE_SCT_CORE +" }}");
		final Expression expected = Expressions.builder()
				.should(SnomedDocument.Expressions.active())
				.should(SnomedDocument.Expressions.modules(ImmutableList.of(Concepts.MODULE_SCT_CORE)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void exclusionActiveAndModuleId() throws Exception {
		final Expression actual = eval("* {{ active=true MINUS moduleId = "+ Concepts.MODULE_SCT_CORE +" }}");
		final Expression expected = Expressions.builder()
				.filter(SnomedDocument.Expressions.active())
				.mustNot(SnomedDocument.Expressions.modules(ImmutableList.of(Concepts.MODULE_SCT_CORE)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test(expected = BadRequestException.class)
	public void conjunctionDomainInconsistency() throws Exception {
		eval("* {{ active=true AND Description.moduleId = "+ Concepts.MODULE_SCT_CORE +" }}");
	}
	
	@Test(expected = BadRequestException.class)
	public void disjunctionDomainInconsistency() throws Exception {
		eval("* {{ Description.active=true OR moduleId = "+ Concepts.MODULE_SCT_CORE +" }}");
	}
	
	@Test(expected = BadRequestException.class)
	public void exclusionDomainInconsistency() throws Exception {
		eval("* {{ active=true MINUS term = \"Clin find\" }}");
	}
	
	@Test
	public void descriptionTypeFilter() throws Exception {
		indexRevision(MAIN, nextStorageKey(), SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.TEXT_DEFINITION)
				.build());
		final Expression actual = eval("* {{ typeId = "+Concepts.TEXT_DEFINITION+" }}");
		final Expression expected = SnomedDocument.Expressions.ids(ImmutableList.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test(expected = BadRequestException.class)
	public void conjunctionAmbiguity() throws Exception {
		eval("* {{ Description.active=true AND Description.moduleId = "+ Concepts.MODULE_SCT_CORE +" OR term=\"clinical finding\" }}");
	}
	
	@Test(expected = BadRequestException.class)
	public void disjunctionAmbiguity() throws Exception {
		eval("* {{ Description.active=true OR Description.moduleId = "+ Concepts.MODULE_SCT_CORE +" AND term=\"clinical finding\" }}");
	}
	
	@Test(expected = BadRequestException.class)
	public void exclusionAmbiguity() throws Exception {
		eval("* {{ Description.active=true OR Description.moduleId = "+ Concepts.MODULE_SCT_CORE +" MINUS term=\"clinical finding\" }}");
	}
	
	@Test
	public void multiDomainQueryAnd() throws Exception {
		Expression actual = eval("* {{ active=false }} AND * {{ term=\"clin find\" }}");
		Expression expected = Expressions.builder()
				.filter(SnomedDocument.Expressions.inactive())
				.filter(SnomedDocument.Expressions.ids(Collections.emptySet()))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void multiDomainQueryOR() throws Exception {
		Expression actual = eval("* {{ active=false }} OR * {{ term=\"clin find\" }}");
		Expression expected = Expressions.builder()
				.should(SnomedDocument.Expressions.inactive())
				.should(SnomedDocument.Expressions.ids(Collections.emptySet()))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void multiDomainQueryExclusion() throws Exception {
		Expression actual = eval("* {{ active=false }} MINUS * {{ term=\"clin find\" }}");
		Expression expected = Expressions.builder()
				.filter(SnomedDocument.Expressions.inactive())
				.mustNot(SnomedDocument.Expressions.ids(Collections.emptySet()))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void preferredInFilter() throws Exception {
		indexRevision(MAIN, nextStorageKey(), SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(ImmutableSet.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.acceptableIn(ImmutableSet.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.build());
		
		indexRevision(MAIN, nextStorageKey(), SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.SUBSTANCE)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(ImmutableSet.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.acceptableIn(ImmutableSet.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.build());
		
		final Expression actual = eval("* {{ preferredIn = "+Concepts.REFSET_LANGUAGE_TYPE_UK+" }}");
		final Expression expected = SnomedDocument.Expressions.ids(ImmutableList.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void acceptableInFilter() throws Exception {
		indexRevision(MAIN, nextStorageKey(), SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(ImmutableSet.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.acceptableIn(ImmutableSet.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.build());
		
		indexRevision(MAIN, nextStorageKey(), SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.SUBSTANCE)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(ImmutableSet.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.acceptableIn(ImmutableSet.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.build());
		
		final Expression actual = eval("* {{ acceptableIn = "+Concepts.REFSET_LANGUAGE_TYPE_UK+" }}");
		final Expression expected = SnomedDocument.Expressions.ids(ImmutableList.of(Concepts.SUBSTANCE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void languageRefSetFilter() throws Exception {
		indexRevision(MAIN, nextStorageKey(), SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(ImmutableSet.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.acceptableIn(ImmutableSet.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.build());
		
		indexRevision(MAIN, nextStorageKey(), SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.SUBSTANCE)
				.typeId(Concepts.TEXT_DEFINITION)
				.preferredIn(ImmutableSet.of(Concepts.REFSET_LANGUAGE_TYPE_US))
				.acceptableIn(ImmutableSet.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.build());
		
		final Expression actual = eval("* {{ languageRefSetId = "+Concepts.REFSET_LANGUAGE_TYPE_UK+" }}");
		final Expression expected = SnomedDocument.Expressions.ids(ImmutableSet.of(Concepts.ROOT_CONCEPT, Concepts.SUBSTANCE));
		assertEquals(expected, actual);
	}
	
	@Test(expected = BadRequestException.class)
	public void invalidLanguageCodeFilter() throws Exception {
		eval("* {{ languageCode = \"en-sg\" }}");
	}
	
	@Test
	public void languageCodeFilter() throws Exception {
		indexRevision(MAIN, nextStorageKey(), SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.TEXT_DEFINITION)
				.languageCode("en")
				.build());
		
		Expression actual = eval("* {{ languageCode = \"en\" }}");
		Expression expected = SnomedDocument.Expressions.ids(ImmutableSet.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void caseSignificanceIdFilter() throws Exception {
		indexRevision(MAIN, nextStorageKey(), SnomedDescriptionIndexEntry.builder()
				.id(generateDescriptionId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.term("Clinical finding")
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.TEXT_DEFINITION)
				.languageCode("en")
				.caseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
				.build());
		
		Expression actual = eval("* {{ caseSignificanceId = "+Concepts.ENTIRE_TERM_CASE_INSENSITIVE+" }}");
		Expression expected = SnomedDocument.Expressions.ids(ImmutableSet.of(Concepts.ROOT_CONCEPT));
		assertEquals(expected, actual);
	}
	
}
