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
import org.eclipse.xtext.serializer.ISerializer;
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
import com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
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
	private static final String HAS_ACTIVE_INGREDIENT = Concepts.HAS_ACTIVE_INGREDIENT;
	private static final String SUBSTANCE = Concepts.SUBSTANCE;
	
	
	// random IDs
	private static final String TRIPHASIL_TABLET = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String PANADOL_TABLET = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String ABACAVIR_TABLET = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INGREDIENT1 = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INGREDIENT2 = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INGREDIENT3 = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String HAS_BOSS = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String DRUG_ROOT = RandomSnomedIdentiferGenerator.generateConceptId();
	
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
		context = TestBranchContext.on(MAIN)
				.with(EclParser.class, new DefaultEclParser(injector.getInstance(IParser.class)))
				.with(EclSerializer.class, new DefaultEclSerializer(injector.getInstance(ISerializer.class)))
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
	
	@Test
	public void refinementAttributeEquals() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s:%s=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT1));
		final Expression expected = ids(ImmutableSet.of(PANADOL_TABLET, TRIPHASIL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementOfTwoConceptsWithAndGrouping() throws Exception {
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
		final Expression actual = eval(String.format("<%s: [0..*] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		// since 0..* cardinality is equal to just the focusConcepts, then this will eval to <DRUG_ROOT
		final Expression expected = Expressions.builder()
				.should(parents(Collections.singleton(DRUG_ROOT)))
				.should(ancestors(Collections.singleton(DRUG_ROOT)))
				.build();
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
	public void refinementCardinalityZeroToZero() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [0..0] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected =	
				Expressions.builder()
					// focusConcepts
					.must(
						Expressions.builder()
							.should(parents(Collections.singleton(DRUG_ROOT)))
							.should(ancestors(Collections.singleton(DRUG_ROOT)))
						.build()
					)
					.mustNot(ids(ImmutableSet.of(TRIPHASIL_TABLET, PANADOL_TABLET)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityZeroToOne() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [0..1] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected =	
				Expressions.builder()
					// focusConcepts
					.must(
						Expressions.builder()
							.should(parents(Collections.singleton(DRUG_ROOT)))
							.should(ancestors(Collections.singleton(DRUG_ROOT)))
						.build()
					)
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
	 * 			<li>PANADOL_TABLET (drug with a single outgoing HAI relationship to INGREDIENT1)</li>
	 * 			<li>TRIPHASIL_TABLET (drug with two outgoing HAI relationships, to INGREDIENT1 and 2, and one HAS_BOSS to INGREDIENT2)</li>
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
		// has active ingredient relationships
		indexRevision(MAIN, nextStorageKey(), relationship(PANADOL_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT1).build());
		indexRevision(MAIN, nextStorageKey(), relationship(TRIPHASIL_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT1).build());
		indexRevision(MAIN, nextStorageKey(), relationship(TRIPHASIL_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT2).build());
		indexRevision(MAIN, nextStorageKey(), relationship(TRIPHASIL_TABLET, HAS_BOSS, INGREDIENT2).build());
	}

	private SnomedConceptDocument.Builder concept(final String id) {
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
	
	private SnomedRelationshipIndexEntry.Builder relationship(final String source, final String type, final String destination) {
		return SnomedRelationshipIndexEntry.builder()
				.id(RandomSnomedIdentiferGenerator.generateRelationshipId())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.sourceId(source)
				.typeId(type)
				.destinationId(destination)
				.characteristicTypeId(Concepts.STATED_RELATIONSHIP)
				.modifierId(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);
	}

}
