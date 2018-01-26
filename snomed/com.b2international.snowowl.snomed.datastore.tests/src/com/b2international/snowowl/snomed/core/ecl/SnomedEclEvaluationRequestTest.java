/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.b2international.snowowl.snomed.core.tests.util.DocumentBuilders.concept;
import static com.b2international.snowowl.snomed.core.tests.util.DocumentBuilders.decimalMember;
import static com.b2international.snowowl.snomed.core.tests.util.DocumentBuilders.integerMember;
import static com.b2international.snowowl.snomed.core.tests.util.DocumentBuilders.relationship;
import static com.b2international.snowowl.snomed.core.tests.util.DocumentBuilders.stringMember;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.referringMappingRefSet;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.referringMappingRefSets;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.referringRefSet;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.referringRefSets;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Fields.REFERRING_MAPPING_REFSETS;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Fields.REFERRING_REFSETS;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.ancestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.parents;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.collections.PrimitiveSets;
import com.b2international.index.Index;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.MatchNone;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.EclStandaloneSetup;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @since 5.4
 */
public class SnomedEclEvaluationRequestTest extends BaseRevisionIndexTest {

	private static final String ROOT_ID = Concepts.ROOT_CONCEPT;
	private static final String OTHER_ID = Concepts.ABBREVIATION;
	private static final String HAS_ACTIVE_INGREDIENT = Concepts.HAS_ACTIVE_INGREDIENT;
	private static final String SUBSTANCE = Concepts.SUBSTANCE;
	
	
	// random IDs
	private static final String TRIPHASIL_TABLET = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String PANADOL_TABLET = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String ABACAVIR_TABLET = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String AMOXICILLIN_TABLET = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String TISSEL_KIT = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String ASPIRIN_TABLET = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String EPOX_TABLET = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String ALGOFLEX_TABLET = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String TRIPLEX_TABLET = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String DRUG_WITH_INVALID_HAI = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INGREDIENT1 = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INGREDIENT2 = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INGREDIENT3 = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INGREDIENT4 = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INGREDIENT5 = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INGREDIENT6 = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String HAS_BOSS = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String DRUG_ROOT = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String HAS_TRADE_NAME = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String PREFERRED_STRENGTH = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String DRUG_1_MG = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String DRUG_1D_MG = RandomSnomedIdentiferGenerator.generateConceptId();
	
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
		final Injector injector = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
		context = TestBranchContext.on(MAIN)
				.with(EclParser.class, new DefaultEclParser(injector.getProvider(IParser.class), injector.getProvider(IResourceValidator.class)))
				.with(EclSerializer.class, new DefaultEclSerializer(injector.getProvider(ISerializer.class)))
				.with(Index.class, rawIndex())
				.with(RevisionIndex.class, index())
				.build();
	}
	
	@Test(expected = BadRequestException.class)
	public void syntaxErrorsShouldThrowException() throws Exception {
		eval("invalid");
	}
	
	private Expression eval(String expression) {
		return SnomedRequests.prepareEclEvaluation(expression).build().execute(context).getSync();		
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
		final Expression expected = Expressions.builder()
				.should(referringRefSet(Concepts.REFSET_DESCRIPTION_TYPE))
				.should(referringMappingRefSet(Concepts.REFSET_DESCRIPTION_TYPE))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void memberOfAny() throws Exception {
		final Expression actual = eval("^*");
		final Expression expected = Expressions.builder()
				.should(Expressions.exists(REFERRING_REFSETS))
				.should(Expressions.exists(REFERRING_MAPPING_REFSETS))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void memberOfNested() throws Exception {
		indexRevision(MAIN, nextStorageKey(), concept(Concepts.SYNONYM)
				.parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(Concepts.REFSET_DESCRIPTION_TYPE)))
				.ancestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build());
		final Expression actual = eval("^(<" + Concepts.REFSET_DESCRIPTION_TYPE + ")");
		final Expression expected = Expressions.builder()
				.should(referringRefSets(Collections.singleton(Concepts.SYNONYM)))
				.should(referringMappingRefSets(Collections.singleton(Concepts.SYNONYM)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void descendantOf() throws Exception {
		final Expression actual = eval("<"+ROOT_ID);
		final Expression expected = descendantsOf(ROOT_ID);
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
		indexRevision(MAIN, STORAGE_KEY1, concept(Concepts.MODULE_SCT_CORE).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(Concepts.MODULE_ROOT))).build());
		final Expression actual = eval(">!"+Concepts.MODULE_SCT_CORE);
		final Expression expected = ids(Collections.singleton(Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void ancestorOf() throws Exception {
		// SCT Core module has a single parent and a single ancestor in this test case
		indexRevision(MAIN, STORAGE_KEY1, concept(Concepts.MODULE_SCT_CORE)
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
		indexRevision(MAIN, STORAGE_KEY1, concept(Concepts.MODULE_SCT_CORE)
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
				.filter(id(ROOT_ID))
				.filter(id(OTHER_ID))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void selfAndOtherWithCommaAsOperator() throws Exception {
		final Expression actual = eval(ROOT_ID + " , " + OTHER_ID);
		final Expression expected = Expressions.builder()
				.filter(id(ROOT_ID))
				.filter(id(OTHER_ID))
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
				.filter(id(ROOT_ID))
				.mustNot(id(OTHER_ID))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void multipleAndOperatorsWithoutBrackets() throws Exception {
		final Expression actual = eval(String.format("%s AND %s AND %s", ROOT_ID, ROOT_ID, ROOT_ID));
		assertNotNull(actual);
	}
	
	@Test
	public void multipleOrOperatorsWithoutBrackets() throws Exception {
		final Expression actual = eval(String.format("%s OR %s OR %s", ROOT_ID, ROOT_ID, ROOT_ID));
		assertNotNull(actual);
	}
	
	@Test(expected = BadRequestException.class)
	public void binaryOperatorAmbiguityOrAnd() throws Exception {
		eval(String.format("%s OR %s AND %s", ROOT_ID, ROOT_ID, ROOT_ID));
	}
	
	@Test(expected = BadRequestException.class)
	public void binaryOperatorAmbiguityAndOr() throws Exception {
		eval(String.format("%s AND %s OR %s", ROOT_ID, ROOT_ID, ROOT_ID));
	}
	
	@Test
	public void refinementAttributeEquals() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s:%s=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT1));
		final Expression expected = ids(ImmutableSet.of(PANADOL_TABLET, TRIPHASIL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAttributeSubExpression() throws Exception {
		generateDrugHierarchy();
		indexRevision(MAIN, nextStorageKey(), concept(HAS_ACTIVE_INGREDIENT).build());
		indexRevision(MAIN, nextStorageKey(), concept(HAS_BOSS).build());
		final Expression actual = eval(String.format("<%s:(%s OR %s)=(%s OR %s)", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, HAS_BOSS, INGREDIENT1, INGREDIENT2));
		final Expression expected = ids(ImmutableSet.of(PANADOL_TABLET, TRIPHASIL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAttributeEqualsSingleMatch() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s:%s=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT2));
		final Expression expected = ids(Collections.singleton(TRIPHASIL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAttributeNotEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s:%s!=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT1));
		final Expression expected = ids(Collections.singleton(TRIPHASIL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAnyAttributeNotEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("*:%s!=%s", HAS_ACTIVE_INGREDIENT, INGREDIENT1));
		final Expression expected = ids(Collections.singleton(TRIPHASIL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementReversedAttributeEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: R %s=%s", SUBSTANCE, HAS_ACTIVE_INGREDIENT, TRIPHASIL_TABLET));
		final Expression expected = ids(ImmutableSet.of(INGREDIENT1, INGREDIENT2));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAnyAttributeName() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: R *=%s", SUBSTANCE, TRIPHASIL_TABLET));
		final Expression expected = ids(ImmutableSet.of(INGREDIENT1, INGREDIENT2));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAnyAttributeValue() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: R %s=*", SUBSTANCE, HAS_ACTIVE_INGREDIENT));
		final Expression expected = ids(ImmutableSet.of(INGREDIENT1, INGREDIENT2));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAttributeValueDescendantOf() throws Exception {
		generateDrugHierarchy();

		final Expression actual = eval(String.format("<%s: %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = ids(ImmutableSet.of(TRIPHASIL_TABLET, PANADOL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityZeroToUnbounded() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [0..*] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		// since 0..* cardinality is equal to just the focusConcepts, then this will eval to all concepts under DRUG_ROOT
		final Expression expected = descendantsOf(DRUG_ROOT);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityAtLeastOne() throws Exception {
		generateDrugHierarchy();
		
		// since 1..* cardinality is the default cardinality (like omitting the entire [1..*] part from the text), 
		// this will properly eval to the concepts having at least one relationships without actual cardinality 
		final Expression actual = eval(String.format("<%s: [1..*] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = ids(ImmutableSet.of(TRIPHASIL_TABLET, PANADOL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityZeroToZeroEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [0..0] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected =	Expressions.builder()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(ImmutableSet.of(TRIPHASIL_TABLET, PANADOL_TABLET)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityZeroToZeroNotEquals() throws Exception {
		generateDrugHierarchy();
		indexRevision(MAIN, nextStorageKey(), concept(DRUG_WITH_INVALID_HAI).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(DRUG_ROOT))).build());
		indexRevision(MAIN, nextStorageKey(), relationship(DRUG_WITH_INVALID_HAI, HAS_ACTIVE_INGREDIENT, DRUG_WITH_INVALID_HAI).group(0).build());
		
		final Expression actual = eval(String.format("<%s: [0..0] %s != <%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected =	Expressions.builder()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(ImmutableSet.of(DRUG_WITH_INVALID_HAI)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityZeroToZeroNotEqualsToSingleValue() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [0..0] %s != %s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT1));
		final Expression expected =	Expressions.builder()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(ImmutableSet.of(TRIPHASIL_TABLET)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityZeroToOne() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [0..1] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected =	Expressions.builder()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(ImmutableSet.of(TRIPHASIL_TABLET)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityExactlyOne() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [1..1] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = ids(Collections.singleton(PANADOL_TABLET));
		assertEquals(expected, actual);
	}

	@Test
	public void refinementCardinalityOneOrTwo() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [1..2] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = ids(ImmutableSet.of(TRIPHASIL_TABLET, PANADOL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementWithAttributeConjunction() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s:%s=%s,%s=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT1, HAS_ACTIVE_INGREDIENT, INGREDIENT2));
		final Expression expected = Expressions.builder()
				.filter(ids(ImmutableSet.of(PANADOL_TABLET, TRIPHASIL_TABLET)))
				.filter(ids(ImmutableSet.of(TRIPHASIL_TABLET)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementWithAttributeDisjunction() throws Exception {
		generateDrugHierarchy();
		generateTisselKit();
		final Expression actual = eval(String.format("<%s:%s=%s OR %s=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT2, HAS_ACTIVE_INGREDIENT, INGREDIENT4));
		final Expression expected = Expressions.builder()
				.should(ids(ImmutableSet.of(TRIPHASIL_TABLET)))
				.should(ids(ImmutableSet.of(TISSEL_KIT)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementWithConjunctionAndDisjunction() throws Exception {
		generateDrugHierarchy();
		generateTisselKit();
		final Expression actual = eval(String.format("<%s:%s=%s OR (%s=%s AND %s=%s)", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT2, HAS_ACTIVE_INGREDIENT, INGREDIENT4, HAS_ACTIVE_INGREDIENT, INGREDIENT2));
		final Expression expected = Expressions.builder()
				.should(ids(ImmutableSet.of(TRIPHASIL_TABLET)))
				.should(
					Expressions.builder()
						.filter(ids(ImmutableSet.of(TISSEL_KIT)))
						.filter(ids(ImmutableSet.of(TRIPHASIL_TABLET)))
					.build())
				.build();
		assertEquals(expected, actual);
	}
	
	@Test(expected = BadRequestException.class)
	public void refinementAmbiguityAndOr() throws Exception {
		eval(String.format("<%s:%s=%s AND %s=%s OR %s=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT2, HAS_ACTIVE_INGREDIENT, INGREDIENT4, HAS_ACTIVE_INGREDIENT, INGREDIENT2));
	}
	
	@Test(expected = BadRequestException.class)
	public void refinementAmbiguityOrAnd() throws Exception {
		eval(String.format("<%s:%s=%s OR %s=%s AND %s=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT2, HAS_ACTIVE_INGREDIENT, INGREDIENT4, HAS_ACTIVE_INGREDIENT, INGREDIENT2));
	}
	
	@Test
	public void refinementReversedCardinalityZeroToUnbounded() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [0..*] R %s=%s", SUBSTANCE, HAS_ACTIVE_INGREDIENT, TRIPHASIL_TABLET));
		final Expression expected = descendantsOf(SUBSTANCE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void dotted() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("%s.%s", PANADOL_TABLET, HAS_ACTIVE_INGREDIENT));
		final Expression expected = ids(ImmutableSet.of(INGREDIENT1));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dottedWithDescendantOf() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s.%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT));
		final Expression expected = ids(ImmutableSet.of(INGREDIENT1, INGREDIENT2));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dottedWithComplexLeftSide() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("(<%s MINUS %s).%s", DRUG_ROOT, TRIPHASIL_TABLET, HAS_ACTIVE_INGREDIENT));
		final Expression expected = ids(ImmutableSet.of(INGREDIENT1));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementGroupWithDefaultCardinality() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: {%s=<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = ids(ImmutableSet.of(ASPIRIN_TABLET, ALGOFLEX_TABLET, TRIPLEX_TABLET));
		assertEquals(expected, actual);
	}

	@Test
	public void refinementGroupCardinalityTwoToTwo() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: [2..2] {%s=<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = ids(ImmutableSet.of(ALGOFLEX_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementGroupCardinalityZeroToUnbounded() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: [0..*] {%s=<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = descendantsOf(DRUG_ROOT);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementGroupCardinalityZeroToZero() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: [0..0] {%s=<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = Expressions.builder()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(ImmutableSet.of(ASPIRIN_TABLET, ALGOFLEX_TABLET, TRIPLEX_TABLET)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementGroupCardinalityZeroToOne() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: [0..1] {%s=<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = Expressions.builder()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(ImmutableSet.of(ALGOFLEX_TABLET)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementWithGroupConjunction() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: [1..1] {%s=<%s,%s=<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE, HAS_BOSS, SUBSTANCE));
		final Expression expected = ids(ImmutableSet.of(ASPIRIN_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementGroupDefaultCardinalityAndRelationshipOneToOneCardinality() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: {[1..1] %s=<%s,[1..1] %s=<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE, HAS_BOSS, SUBSTANCE));
		final Expression expected = ids(ImmutableSet.of(ASPIRIN_TABLET, ALGOFLEX_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementWithGroupDisjunction() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: [1..1] {%s=%s OR %s=%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT5, HAS_BOSS, INGREDIENT5));
		final Expression expected = ids(ImmutableSet.of(ASPIRIN_TABLET, TRIPLEX_TABLET));
		assertEquals(expected, actual);
	}

	@Test
	public void refinementGroupNestedConjunctionDisjunction() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: [1..1] {(%s=%s AND %s=%s) OR %s=%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT5, HAS_BOSS, INGREDIENT6, HAS_ACTIVE_INGREDIENT, INGREDIENT6));
		final Expression expected = ids(ImmutableSet.of(ASPIRIN_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementStringEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: %s = 'PANADOL'", DRUG_ROOT, HAS_TRADE_NAME));
		final Expression expected = ids(ImmutableSet.of(PANADOL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAnyStringEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("*: %s = 'PANADOL'", HAS_TRADE_NAME));
		final Expression expected = ids(ImmutableSet.of(PANADOL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementStringNotEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: %s != 'PANADOL'", DRUG_ROOT, HAS_TRADE_NAME));
		final Expression expected = ids(ImmutableSet.of(TRIPHASIL_TABLET, AMOXICILLIN_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAnyStringNotEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("*: %s != 'PANADOL'", HAS_TRADE_NAME));
		final Expression expected = ids(ImmutableSet.of(TRIPHASIL_TABLET, AMOXICILLIN_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerEquals() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s: %s = #500", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(PANADOL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerEqualsNegative() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s: %s = #-500", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(TRIPHASIL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerNotEquals() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s: %s != #500", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(TRIPHASIL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalEquals() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s: %s = #5.5", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(AMOXICILLIN_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalEqualsNegative() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s: %s = #-5.5", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(ABACAVIR_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalNotEquals() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s: %s != #5.5", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(ABACAVIR_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerLessThan() throws Exception {
		generateDrugHierarchy();
		generateDrugWithIntegerStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s < #1", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(TRIPHASIL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalLessThan() throws Exception {
		generateDrugHierarchy();
		generateDrugWithDecimalStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s < #1.0", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(ABACAVIR_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerLessThanOrEquals() throws Exception {
		generateDrugHierarchy();
		generateDrugWithIntegerStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s <= #1", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(TRIPHASIL_TABLET, DRUG_1_MG));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalLessThanOrEquals() throws Exception {
		generateDrugHierarchy();
		generateDrugWithDecimalStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s <= #1.0", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(ABACAVIR_TABLET, DRUG_1D_MG));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerGreaterThan() throws Exception {
		generateDrugHierarchy();
		generateDrugWithIntegerStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s > #1", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(PANADOL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalGreaterThan() throws Exception {
		generateDrugHierarchy();
		generateDrugWithDecimalStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s > #1.0", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(AMOXICILLIN_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerGreaterThanEquals() throws Exception {
		generateDrugHierarchy();
		generateDrugWithIntegerStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s >= #1", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(PANADOL_TABLET, DRUG_1_MG));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalGreaterThanEquals() throws Exception {
		generateDrugHierarchy();
		generateDrugWithDecimalStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s >= #1.0", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = ids(ImmutableSet.of(AMOXICILLIN_TABLET, DRUG_1D_MG));
		assertEquals(expected, actual);
	}
	
	@Test
	public void descendantsOfAllMembersOfReferenceSet() throws Exception {
		final String refSetId = Concepts.REFSET_DESCRIPTION_TYPE;
		final String member1 = RandomSnomedIdentiferGenerator.generateConceptId();
		final String member2 = RandomSnomedIdentiferGenerator.generateConceptId();
		
		indexRevision(MAIN, nextStorageKey(), concept(member1)
				.referringRefSets(ImmutableSet.of(refSetId))
				.build());
		indexRevision(MAIN, nextStorageKey(), concept(member2)
				.referringRefSets(ImmutableSet.of(refSetId))
				.build());
		
		final Expression actual = eval(String.format("<^%s", refSetId));
		final Expression expected = descendantsOf(member1, member2);
		assertEquals(expected, actual);
	}

	@Test
	public void defaultGroupCardinalityWithZeroToZeroAttributeCardinality() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: { [0..0] %s = <%s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = Expressions.matchNone();
		assertEquals(expected, actual);
	}
	
	@Ignore
	@Test
	public void defaultGroupCardinalityWithZeroToZeroAttributeCardinalityNotEquals() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: { [0..0] %s != %s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT5));
		final Expression expected = ids(ImmutableSet.of(ASPIRIN_TABLET, TRIPLEX_TABLET, ALGOFLEX_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void defaultGroupCardinalityWithZeroToOneAttributeCardinality() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: { [0..1] %s = <%s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = ids(ImmutableSet.of(ASPIRIN_TABLET, TRIPLEX_TABLET, ALGOFLEX_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void groupCardinalityZeroToZeroWithDefaultAttributeCardinality() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: [0..0] { %s = <%s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = Expressions.builder()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(ImmutableSet.of(ALGOFLEX_TABLET, TRIPLEX_TABLET, ASPIRIN_TABLET)))
				.build();;
		assertEquals(expected, actual);
	}
	
	@Test
	public void groupCardinalityZeroToOneWithDefaultAttributeCardinality() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: [0..1] { %s = <%s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = Expressions.builder()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(ImmutableSet.of(ALGOFLEX_TABLET)))
				.build();;
		assertEquals(expected, actual);
	}
	
	@Test
	public void groupCardinalityZeroToOneWithDefaultAttributeCardinalityNotEquals() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: [0..1] { %s != <%s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = descendantsOf(DRUG_ROOT);
		assertEquals(expected, actual);
	}
	
	@Ignore("Unsupported attribute cardinality in group with cardinality")
	@Test
	public void groupCardinalityZeroToZeroWithZeroToZeroAttributeCardinality() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: [0..0] { [0..0] %s = <%s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = Expressions.matchNone();
		assertEquals(expected, actual);
	}
	
	@Ignore("Unsupported attribute cardinality in group with cardinality")
	@Test
	public void groupCardinalityZeroToOneWithZeroToZeroAttributeCardinality() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: [0..1] { [0..0] %s = <%s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = Expressions.matchNone();
		assertEquals(expected, actual);
	}
	
	@Ignore("Unsupported attribute cardinality in group with cardinality")
	@Test
	public void groupCardinalityZeroToZeroWithZeroToOneAttributeCardinality() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: [0..0] { [0..1] %s = <%s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = Expressions.matchNone();
		assertEquals(expected, actual);
	}
	
	@Ignore("Unsupported attribute cardinality in group with cardinality")
	@Test
	public void groupCardinalityZeroToOneWithZeroToOneAttributeCardinality() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: [0..1] { [0..1] %s = <%s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = ids(ImmutableSet.of(ASPIRIN_TABLET, TRIPLEX_TABLET, EPOX_TABLET));
		assertEquals(expected, actual);
	}
	
	/**
	 * Generates the following test fixtures:
	 * <ul>
	 * 	<li>Substances (children of SUBSTANCE):
	 * 		<ul>
	 * 			<li>INGREDIENT1 (ingredient with two inbound from PANADOL and TRIPHASIL TABLET)</li>
	 * 			<li>INGREDIENT2 (ingredient with one inbound from TRIPHASIL TABLET)</li>
	 * 			<li>INGREDIENT3 (ingredient without any inbound HAI relationships)</li>
	 * 		</ul>
	 * 	</li>
	 * 	<li>Drugs (children of DRUG_ROOT):
	 * 		<ul>
	 * 			<li>ABACAVIR_TABLET (drug without any outgoing HAI relationships)</li>
	 * 			<li>PANADOL_TABLET (drug with a single outgoing inferred HAI relationship to INGREDIENT1)</li>
	 * 			<li>TRIPHASIL_TABLET (drug with two outgoing inferred HAI relationships, to INGREDIENT1 and 2, and one HAS_BOSS to INGREDIENT2)</li>
	 * 			<li>AMOXICILLIN_TABLET (drug with one outgoing stated HAI relationship to INGREDIENT1)</li>
	 * 		</ul>
	 * 	</li>
	 * </ul>
	 */
	private void generateDrugHierarchy() {
		// substances
		indexRevision(MAIN, nextStorageKey(), concept(INGREDIENT1).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(SUBSTANCE))).build());
		indexRevision(MAIN, nextStorageKey(), concept(INGREDIENT2).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(SUBSTANCE))).build());
		indexRevision(MAIN, nextStorageKey(), concept(INGREDIENT3).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(SUBSTANCE))).build());
		// drugs
		indexRevision(MAIN, nextStorageKey(), concept(ABACAVIR_TABLET).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(DRUG_ROOT))).build());
		indexRevision(MAIN, nextStorageKey(), concept(PANADOL_TABLET).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(DRUG_ROOT))).build());
		indexRevision(MAIN, nextStorageKey(), concept(TRIPHASIL_TABLET).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(DRUG_ROOT))).build());
		indexRevision(MAIN, nextStorageKey(), concept(AMOXICILLIN_TABLET).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(DRUG_ROOT))).build());
		// has active ingredient relationships
		indexRevision(MAIN, nextStorageKey(), relationship(PANADOL_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT1).group(0).build());
		indexRevision(MAIN, nextStorageKey(), relationship(TRIPHASIL_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT1).group(0).build());
		indexRevision(MAIN, nextStorageKey(), relationship(TRIPHASIL_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT2).group(0).build());
		indexRevision(MAIN, nextStorageKey(), relationship(TRIPHASIL_TABLET, HAS_BOSS, INGREDIENT2).group(0).build());
		indexRevision(MAIN, nextStorageKey(), relationship(AMOXICILLIN_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT1).group(0).characteristicTypeId(Concepts.STATED_RELATIONSHIP).build());
		// trade names
		indexRevision(MAIN, nextStorageKey(), stringMember(PANADOL_TABLET, HAS_TRADE_NAME, "PANADOL").build());
		indexRevision(MAIN, nextStorageKey(), stringMember(TRIPHASIL_TABLET, HAS_TRADE_NAME, "TRIPHASIL").build());
		indexRevision(MAIN, nextStorageKey(), stringMember(AMOXICILLIN_TABLET, HAS_TRADE_NAME, "AMOXICILLIN").build());
		// strengths
		indexRevision(MAIN, nextStorageKey(), integerMember(PANADOL_TABLET, PREFERRED_STRENGTH, 500).build());
		indexRevision(MAIN, nextStorageKey(), integerMember(TRIPHASIL_TABLET, PREFERRED_STRENGTH, -500).build());
		indexRevision(MAIN, nextStorageKey(), decimalMember(AMOXICILLIN_TABLET, PREFERRED_STRENGTH, BigDecimal.valueOf(5.5d)).build());
		indexRevision(MAIN, nextStorageKey(), decimalMember(ABACAVIR_TABLET, PREFERRED_STRENGTH, BigDecimal.valueOf(-5.5d)).build());
	}

	/**
	 * Generates the following test fixtures:
	 * <ul>
	 * 	<li>Substances (children of SUBSTANCE):
	 * 		<ul>
	 * 			<li>INGREDIENT4 (ingredient with one inbound HAI from TISSEL_KIT)</li>
	 * 		</ul>
	 * 	</li>
	 * 	<li>Drugs (children of DRUG_ROOT):
	 * 		<ul>
	 * 			<li>TISSEL_KIT (drug with one outgoing inferred HAI relationship to INGREDIENT4)</li>
	 * 		</ul>
	 * 	</li>
	 * </ul>
	 */
	private void generateTisselKit() {
		indexRevision(MAIN, nextStorageKey(), concept(INGREDIENT4).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(SUBSTANCE))).build());
		indexRevision(MAIN, nextStorageKey(), concept(TISSEL_KIT).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(DRUG_ROOT))).build());
		indexRevision(MAIN, nextStorageKey(), relationship(TISSEL_KIT, HAS_ACTIVE_INGREDIENT, INGREDIENT4).group(0).build());
	}
	
	/**
	 * Generates the following test fixtures:
	 * <ul>
	 * 	<li>Substances (children of SUBSTANCE):
	 * 		<ul>
	 * 			<li>INGREDIENT5 (ingredient with two inbound HAI from ASPIRIN_TABLET and ALGOFLEX_TABLET, one inbound HAS_BOSS from ALGOFLEX_TABLET)</li>
	 * 			<li>INGREDIENT6 (ingredient with two inbound HAS_BOSS from ASPIRIN_TABLET and ALGOFLEX_TABLET, one inbound HAI from ALGOFLEX_TABLET)</li>
	 * 		</ul>
	 * 	</li>
	 * 	<li>Drugs (children of DRUG_ROOT):
	 * 		<ul>
	 * 			<li>EPOX_TABLET (drug with one ungrouped HAI relationship to INGREDIENT5)</li>
	 * 			<li>ASPIRIN_TABLET (drug with one HAI relationship to INGREDIENT5 in group 1 and one HAS_BOSS to INGREDIENT6 in group 1)</li>
	 * 			<li>ALGOFLEX_TABLET (drug with two HAI relationship to INGREDIENT5,INGREDIENT6 in group 1 and 2 and two HAS_BOSS to INGREDIENT6 and 5 in group 1 and 2)</li>
	 * 			<li>TRIPLEX_TABLET (drug with one HAI relationship to INGREDIENT5 in group 1 and one HAS_BOSS to ING6 in group 2)</li>
	 * 		</ul>
	 * 	</li>
	 * </ul>
	 */
	private void generateDrugsWithGroups() {
		indexRevision(MAIN, nextStorageKey(), concept(INGREDIENT5).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(SUBSTANCE))).build());
		indexRevision(MAIN, nextStorageKey(), concept(INGREDIENT6).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(SUBSTANCE))).build());
		
		indexRevision(MAIN, nextStorageKey(), concept(EPOX_TABLET).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(DRUG_ROOT))).build());
		indexRevision(MAIN, nextStorageKey(), concept(ASPIRIN_TABLET).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(DRUG_ROOT))).build());
		indexRevision(MAIN, nextStorageKey(), concept(ALGOFLEX_TABLET).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(DRUG_ROOT))).build());
		indexRevision(MAIN, nextStorageKey(), concept(TRIPLEX_TABLET).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(DRUG_ROOT))).build());
		
		indexRevision(MAIN, nextStorageKey(), relationship(EPOX_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT5).group(0).build());
		
		indexRevision(MAIN, nextStorageKey(), relationship(ASPIRIN_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT5).group(1).build());
		indexRevision(MAIN, nextStorageKey(), relationship(ASPIRIN_TABLET, HAS_BOSS, INGREDIENT6).group(1).build());

		indexRevision(MAIN, nextStorageKey(), relationship(ALGOFLEX_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT5).group(1).build());
		indexRevision(MAIN, nextStorageKey(), relationship(ALGOFLEX_TABLET, HAS_BOSS, INGREDIENT6).group(1).build());
		indexRevision(MAIN, nextStorageKey(), relationship(ALGOFLEX_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT6).group(2).build());
		indexRevision(MAIN, nextStorageKey(), relationship(ALGOFLEX_TABLET, HAS_BOSS, INGREDIENT5).group(2).build());
		
		indexRevision(MAIN, nextStorageKey(), relationship(TRIPLEX_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT5).group(1).build());
		indexRevision(MAIN, nextStorageKey(), relationship(TRIPLEX_TABLET, HAS_BOSS, INGREDIENT6).group(2).build());
	}
	
	private void generateDrugWithIntegerStrengthOfValueOne() {
		indexRevision(MAIN, nextStorageKey(), concept(DRUG_1_MG).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(DRUG_ROOT))).build());
		indexRevision(MAIN, nextStorageKey(), integerMember(DRUG_1_MG, PREFERRED_STRENGTH, 1).build());
	}
	
	private void generateDrugWithDecimalStrengthOfValueOne() {
		indexRevision(MAIN, nextStorageKey(), concept(DRUG_1D_MG).parents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(DRUG_ROOT))).build());
		indexRevision(MAIN, nextStorageKey(), decimalMember(DRUG_1D_MG, PREFERRED_STRENGTH, BigDecimal.valueOf(1.0d)).build());
	}

	private static Expression descendantsOf(String...conceptIds) {
		return Expressions.builder()
				.should(parents(ImmutableSet.copyOf(conceptIds)))
				.should(ancestors(ImmutableSet.copyOf(conceptIds)))
				.build();
	}

}
