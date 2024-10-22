/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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

import static com.b2international.index.revision.Revision.Expressions.id;
import static com.b2international.index.revision.Revision.Expressions.ids;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.activeMemberOf;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Fields.ACTIVE_MEMBER_OF;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.ancestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.parents;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedAncestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedParents;
import static com.b2international.snowowl.test.commons.snomed.DocumentBuilders.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.TooCostlyException;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.MatchNone;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator;

/**
 * @since 5.4
 */
@RunWith(Parameterized.class)
public class SnomedEclEvaluationRequestTest extends BaseSnomedEclEvaluationRequestTest {

	private static final long SUBSTANCEL = Long.parseLong(SUBSTANCE);
	
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
	private static final long DRUG_ROOTL = Long.parseLong(DRUG_ROOT);
	
	private static final String MANUFACTURED = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String HAS_TRADE_NAME = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String PREFERRED_STRENGTH = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String DRUG_1_MG = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String DRUG_1D_MG = RandomSnomedIdentiferGenerator.generateConceptId();

	private final String expressionForm;
	private final boolean statementsWithValue;
	
	public SnomedEclEvaluationRequestTest(String expressionForm, boolean statementsWithValue) {
		this.expressionForm = expressionForm;
		this.statementsWithValue = statementsWithValue;
	}
	
	@Parameters(name = "{0} {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			// Test CD members in all three forms
			{ Trees.INFERRED_FORM, false },
			{ Trees.STATED_FORM,   false },
			{ AXIOM,               false }, // special test parameter to indicate stated form on axiom members
			
			// New statements with value are expected to 
			// appear in axiom and inferred form only
			{ Trees.INFERRED_FORM, true  },
			{ AXIOM,               true  }, 
		});
	}
	
	protected final boolean isAxiom() {
		return AXIOM.equals(expressionForm);
	}

	protected final boolean isInferred() {
		return Trees.INFERRED_FORM.equals(expressionForm);
	}
	
	protected final String getCharacteristicType() {
		return isInferred() ? Concepts.INFERRED_RELATIONSHIP : Concepts.STATED_RELATIONSHIP;
	}
	
	protected final boolean isStatementsWithValue() {
		return statementsWithValue;
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
	public void self() throws Exception {
		final Expression actual = eval(ROOT_ID);
		final Expression expected = RevisionDocument.Expressions.id(ROOT_ID);
		assertEquals(expected, actual);
	}
	
	@Test(expected = BadRequestException.class)
	public void alternateIdentifierUnsupported() throws Exception {
		eval("LOINC#12345-6");
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
		final Expression expected = activeMemberOf(Concepts.REFSET_DESCRIPTION_TYPE);
		assertEquals(expected, actual);
	}
	
	@Test
	public void memberOfAny() throws Exception {
		final Expression actual = eval("^*");
		final Expression expected = Expressions.exists(ACTIVE_MEMBER_OF);
		assertEquals(expected, actual);
	}
	
	@Test
	public void memberOfNested() throws Exception {
		indexRevision(MAIN, concept(Concepts.SYNONYM)
				.parents(Long.parseLong(Concepts.REFSET_DESCRIPTION_TYPE))
				.statedParents(Long.parseLong(Concepts.REFSET_DESCRIPTION_TYPE))
				.ancestors(IComponent.ROOT_IDL)
				.statedAncestors(IComponent.ROOT_IDL)
				.build());
		final Expression actual = eval("^(<" + Concepts.REFSET_DESCRIPTION_TYPE + ")");
		final Expression expected = activeMemberOf(Collections.singleton(Concepts.SYNONYM));
		assertEquals(expected, actual);
	}
	
	@Test
	public void memberOfSupportedRefsetField() throws Exception {
		final Expression actual = eval("^ [referencedComponentId]"+Concepts.REFSET_DESCRIPTION_TYPE);
		final Expression expected = activeMemberOf(Concepts.REFSET_DESCRIPTION_TYPE);
		assertEquals(expected, actual);
	}
	
	@Test(expected = BadRequestException.class)
	public void memberOfUnsupportedRefsetField() throws Exception {
		eval("^ [moduleId, mapTarget]"+Concepts.REFSET_DESCRIPTION_TYPE);
	}
	
	@Test(expected = BadRequestException.class)
	public void memberOfUnsupportedWildcard() throws Exception {
		eval("^ [*]"+Concepts.REFSET_DESCRIPTION_TYPE);
	}
	
	@Test
	public void descendantOf() throws Exception {
		final Expression actual = eval("<"+ROOT_ID);
		final Expression expected = descendantsOf(ROOT_ID);
		assertEquals(expected, actual);
	}
	
	@Test
	public void descendantOfAny() throws Exception {
		// special case that converts to a negated parent query 
		final Expression actual = eval("<*");
		final Expression expected;
		if (isInferred()) {
			expected = Expressions.bool().mustNot(parents(Collections.singleton(IComponent.ROOT_ID))).build();
		} else {
			expected = Expressions.bool().mustNot(statedParents(Collections.singleton(IComponent.ROOT_ID))).build();
			
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void descendantOrSelfOf() throws Exception {
		final Expression actual = eval("<<"+ROOT_ID);
		Expression expected;
		if (isInferred()) {
			expected = Expressions.bool()
					.should(ids(Collections.singleton(ROOT_ID)))
					.should(parents(Collections.singleton(ROOT_ID)))
					.should(ancestors(Collections.singleton(ROOT_ID)))
					.build();
			
		} else {
			expected = Expressions.bool()
					.should(ids(Collections.singleton(ROOT_ID)))
					.should(statedParents(Collections.singleton(ROOT_ID)))
					.should(statedAncestors(Collections.singleton(ROOT_ID)))
					.build();
		}
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
	public void descendantOrSelfAny() throws Exception {
		final Expression actual = eval("<< *");
		assertEquals(Expressions.matchAll(), actual);
	}
	
	@Test
	public void childOf() throws Exception {
		final Expression actual = eval("<!"+ROOT_ID);
		Expression expected; 
		if (isInferred()) {
			expected = parents(Collections.singleton(ROOT_ID));
		} else {
			expected = statedParents(Collections.singleton(ROOT_ID));
			
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void childOfAny() throws Exception {
		final Expression actual = eval("<!*");
		Expression expected; 
		if (isInferred()) {
			expected = Expressions.bool().mustNot(parents(Collections.singleton(IComponent.ROOT_ID))).build();
		} else {
			expected = Expressions.bool().mustNot(statedParents(Collections.singleton(IComponent.ROOT_ID))).build();
			
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void childOrSelfOf() throws Exception {
		final Expression actual = eval("<<!"+ROOT_ID);
		Expression expectedParentsClause; 
		if (isInferred()) {
			expectedParentsClause = parents(Collections.singleton(ROOT_ID));
		} else {
			expectedParentsClause = statedParents(Collections.singleton(ROOT_ID));
			
		}
		assertEquals(
			Expressions.bool()
				.should(ids(Collections.singleton(ROOT_ID)))
				.should(expectedParentsClause)
			.build(), 
			actual
		);
	}
	
	@Test
	public void childOrSelfOfAny() throws Exception {
		final Expression actual = eval("<<! *");
		assertEquals(Expressions.matchAll(), actual);
	}
	
	@Test
	public void parentOf() throws Exception {
		// SCT Core module has a single parent in this test case
		indexRevision(MAIN, concept(Concepts.MODULE_SCT_CORE)
				.parents(Long.parseLong(Concepts.MODULE_ROOT))
				.statedParents(Long.parseLong(Concepts.MODULE_ROOT))
				.build());
		final Expression actual = eval(">!"+Concepts.MODULE_SCT_CORE);
		final Expression expected = ids(Collections.singleton(Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void parentOrSelfOf() throws Exception {
		// SCT Core module has a single parent in this test case
		indexRevision(MAIN, concept(Concepts.MODULE_SCT_CORE)
				.parents(Long.parseLong(Concepts.MODULE_ROOT))
				.statedParents(Long.parseLong(Concepts.MODULE_ROOT))
				.build());
		final Expression actual = eval(">>!"+Concepts.MODULE_SCT_CORE);
		final Expression expected = ids(Set.of(Concepts.MODULE_SCT_CORE, Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void ancestorOf() throws Exception {
		// SCT Core module has a single parent and a single ancestor in this test case
		indexRevision(MAIN, concept(Concepts.MODULE_SCT_CORE)
				.ancestors(Long.parseLong(Concepts.ROOT_CONCEPT))
				.parents(Long.parseLong(Concepts.MODULE_ROOT))
				.statedAncestors(Long.parseLong(Concepts.ROOT_CONCEPT))
				.statedParents(Long.parseLong(Concepts.MODULE_ROOT))
				.build());
		final Expression actual = eval(">"+Concepts.MODULE_SCT_CORE);
		final Expression expected = ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT));
		assertEquals(expected, actual);
	}
	
	@Test
	public void ancestorOrSelfOf() throws Exception {
		// SCT Core module has a single parent and a single ancestor in this test case
		indexRevision(MAIN, concept(Concepts.MODULE_SCT_CORE)
				.ancestors(Long.parseLong(Concepts.ROOT_CONCEPT))
				.parents(Long.parseLong(Concepts.MODULE_ROOT))
				.statedAncestors(Long.parseLong(Concepts.ROOT_CONCEPT))
				.statedParents(Long.parseLong(Concepts.MODULE_ROOT))
				.build());
		final Expression actual = eval(">>"+Concepts.MODULE_SCT_CORE);
		final Expression expected = ids(Set.of(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT, Concepts.MODULE_SCT_CORE));
		assertEquals(expected, actual);
	}
	
	@Test
	public void ancestorOrSelfOfAny() throws Exception {
		final Expression actual = eval(">> *");
		assertEquals(Expressions.matchAll(), actual);
	}

	@Test
	public void topOf() throws Exception {
		// SCT core module and nested model component module
		indexRevision(MAIN, concept(Concepts.MODULE_SCT_CORE)
			.ancestors(Long.parseLong(Concepts.ROOT_CONCEPT))
			.parents(Long.parseLong(Concepts.MODULE_ROOT))
			.statedAncestors(Long.parseLong(Concepts.ROOT_CONCEPT))
			.statedParents(Long.parseLong(Concepts.MODULE_ROOT))
			.build());

		indexRevision(MAIN, concept(Concepts.MODULE_SCT_MODEL_COMPONENT)
			.ancestors(Long.parseLong(Concepts.ROOT_CONCEPT), Long.parseLong(Concepts.MODULE_ROOT))
			.parents(Long.parseLong(Concepts.MODULE_SCT_CORE))
			.statedAncestors(Long.parseLong(Concepts.ROOT_CONCEPT), Long.parseLong(Concepts.MODULE_ROOT))
			.statedParents(Long.parseLong(Concepts.MODULE_SCT_CORE))
			.build());
		
		final Expression actual = eval("!!> (< " + Concepts.MODULE_ROOT + ")");
		final Expression expected = Expressions.bool()
			.filter(descendantsOf(Concepts.MODULE_ROOT))
			.mustNot(descendantsOf(Concepts.MODULE_SCT_CORE, Concepts.MODULE_SCT_MODEL_COMPONENT))
			.build();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void topOfAny() throws Exception {
		final Expression actual = eval("!!> *");
		final Expression expected;
		
		if (isInferred()) {
			expected = parents(Collections.singleton(IComponent.ROOT_ID));
		} else {
			expected = statedParents(Collections.singleton(IComponent.ROOT_ID));
		}

		assertEquals(expected, actual);
	}
	
	@Test
	public void bottomOf() throws Exception {
		// SCT core module and nested model component module
		indexRevision(MAIN, concept(Concepts.MODULE_SCT_CORE)
			.ancestors(Long.parseLong(Concepts.ROOT_CONCEPT))
			.parents(Long.parseLong(Concepts.MODULE_ROOT))
			.statedAncestors(Long.parseLong(Concepts.ROOT_CONCEPT))
			.statedParents(Long.parseLong(Concepts.MODULE_ROOT))
			.build());

		indexRevision(MAIN, concept(Concepts.MODULE_SCT_MODEL_COMPONENT)
			.ancestors(Long.parseLong(Concepts.ROOT_CONCEPT), Long.parseLong(Concepts.MODULE_ROOT))
			.parents(Long.parseLong(Concepts.MODULE_SCT_CORE))
			.statedAncestors(Long.parseLong(Concepts.ROOT_CONCEPT), Long.parseLong(Concepts.MODULE_ROOT))
			.statedParents(Long.parseLong(Concepts.MODULE_SCT_CORE))
			.build());
		
		final Expression actual = eval("!!< (< " + Concepts.MODULE_ROOT + ")");
		final Expression expected = ids(Set.of(Concepts.MODULE_SCT_MODEL_COMPONENT));
		assertEquals(expected, actual);
	}
	
	@Test(expected = TooCostlyException.class)
	public void bottomOfAnyTooCostly() throws Exception {
		eval("!!< *");
	}
	
	@Test
	public void selfAndSelf() throws Exception {
		final Expression actual = eval(ROOT_ID + " AND " + ROOT_ID);
		final Expression expected = Expressions.exactMatch(Revision.Fields.ID, ROOT_ID);
		assertEquals(expected, actual);
	}
	
	@Test
	public void selfAndOther() throws Exception {
		final Expression actual = eval(ROOT_ID + " AND " + OTHER_ID);
		final Expression expected = Expressions.matchNone();
		assertEquals(expected, actual);
	}
	
	@Test
	public void selfAndOtherWithCommaAsOperator() throws Exception {
		final Expression actual = eval(ROOT_ID + " , " + OTHER_ID);
		final Expression expected = Expressions.matchNone();
		assertEquals(expected, actual);
	}
	
	@Test
	public void selfOrOther() throws Exception {
		final Expression actual = eval(ROOT_ID + " OR " + OTHER_ID);
		final Expression expected = ids(Set.of(ROOT_ID, OTHER_ID));
		assertEquals(expected, actual);
	}
	
	@Test
	public void selfAndNotOther() throws Exception {
		final Expression actual = eval(ROOT_ID + " MINUS " + OTHER_ID);
		final Expression expected = Expressions.bool()
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
	
	@Test
	public void largeIdSetWithOrOperators() throws Exception {
		eval(IntStream.range(0, 10_000)
			.mapToObj(i -> RandomSnomedIdentiferGenerator.generateConceptId())
			.collect(Collectors.joining(" OR ")));
	}

	// XXX until we figure out how to process these effectively
	@Test(expected = TooCostlyException.class)
	public void largeEclOrOperatorOnly() throws Exception {
		eval(IntStream.range(0, 10_000)
			.mapToObj(i -> {
				if (i % 10 == 0) {
					return "<" + RandomSnomedIdentiferGenerator.generateConceptId();
				} else {
					return RandomSnomedIdentiferGenerator.generateConceptId();
				}
			})
			.collect(Collectors.joining(" OR ")));
	}
	
	@Test(expected = BadRequestException.class)
	public void binaryOperatorAmbiguityOrAnd() throws Exception {
		eval(String.format("%s OR %s AND %s", ROOT_ID, ROOT_ID, ROOT_ID));
	}
	
	@Test(expected = BadRequestException.class)
	public void binaryOperatorAmbiguityAndOr() throws Exception {
		eval(String.format("%s AND %s OR %s", ROOT_ID, ROOT_ID, ROOT_ID));
	}
	
	@Test(expected = BadRequestException.class)
	public void ambiguousOrOperatorInRefinement() throws Exception {
		eval("(<64572001 |Disease (disorder)|: 363698007 |Finding site| = 74281007 |Myocardium structure| OR 404684003 |Clinical finding|)");
	}

	@Test
	public void ambiguousOrOperatorInConstraint() throws Exception {
		eval("(404684003 |Clinical finding (finding)| OR <64572001 |Disease (disorder)|: 363698007 |Finding site| = 74281007 |Myocardium structure|)");
	}
	
	@Test
	public void refinementAttributeEquals() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s:%s=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT1));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(PANADOL_TABLET, TRIPHASIL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAttributeSubExpression() throws Exception {
		generateDrugHierarchy();
		indexRevision(MAIN, 
			concept(HAS_ACTIVE_INGREDIENT).build(),
			concept(HAS_BOSS).build()
		);
		final Expression actual = eval(String.format("<%s:(%s OR %s)=(%s OR %s)", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, HAS_BOSS, INGREDIENT1, INGREDIENT2));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(PANADOL_TABLET, TRIPHASIL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAttributeEqualsSingleMatch() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s:%s=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT2));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Collections.singleton(TRIPHASIL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAttributeNotEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s:%s!=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT1));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Collections.singleton(TRIPHASIL_TABLET))
		);
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
		final Expression expected = and(
			descendantsOf(SUBSTANCE),
			ids(Set.of(INGREDIENT1, INGREDIENT2))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAnyAttributeName() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: R *=%s", SUBSTANCE, TRIPHASIL_TABLET));
		final Expression expected = and(
			descendantsOf(SUBSTANCE),
			ids(Set.of(INGREDIENT1, INGREDIENT2))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAnyAttributeValue() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: R %s=*", SUBSTANCE, HAS_ACTIVE_INGREDIENT));
		final Expression expected = and(
			descendantsOf(SUBSTANCE),
			ids(Set.of(INGREDIENT1, INGREDIENT2))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAttributeValueDescendantOf() throws Exception {
		generateDrugHierarchy();

		final Expression actual = eval(String.format("<%s: %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(TRIPHASIL_TABLET, PANADOL_TABLET))
		);
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
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(TRIPHASIL_TABLET, PANADOL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityZeroToZeroEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [0..0] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected =	Expressions.bool()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(Set.of(TRIPHASIL_TABLET, PANADOL_TABLET)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityZeroToZeroNotEquals() throws Exception {
		generateDrugHierarchy();
		indexRevision(MAIN, 
			concept(DRUG_WITH_INVALID_HAI)
				.parents(DRUG_ROOTL)
				.statedParents(DRUG_ROOTL)
				.build()
		);
		if (isAxiom()) {
			indexRevision(MAIN, classAxioms(DRUG_WITH_INVALID_HAI, 
				HAS_ACTIVE_INGREDIENT, DRUG_WITH_INVALID_HAI, 0
			).build());
		} else {
			indexRevision(MAIN, relationship(DRUG_WITH_INVALID_HAI, HAS_ACTIVE_INGREDIENT, DRUG_WITH_INVALID_HAI, getCharacteristicType()).relationshipGroup(0).build());
		}
		
		final Expression actual = eval(String.format("<%s: [0..0] %s != <%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected =	Expressions.bool()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(Set.of(DRUG_WITH_INVALID_HAI)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityZeroToZeroNotEqualsToSingleValue() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [0..0] %s != %s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT1));
		final Expression expected =	Expressions.bool()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(Set.of(TRIPHASIL_TABLET)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityZeroToOne() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [0..1] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected =	Expressions.bool()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(Set.of(TRIPHASIL_TABLET)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementCardinalityExactlyOne() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [1..1] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Collections.singleton(PANADOL_TABLET))
		);
		assertEquals(expected, actual);
	}

	@Test
	public void refinementCardinalityOneOrTwo() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: [1..2] %s=<%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(TRIPHASIL_TABLET, PANADOL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementWithAttributeConjunction() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s:%s=%s,%s=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT1, HAS_ACTIVE_INGREDIENT, INGREDIENT2));
		final Expression expected = and(
			and(
				descendantsOf(DRUG_ROOT),
				ids(Set.of(PANADOL_TABLET, TRIPHASIL_TABLET))
			),
			and(
				descendantsOf(DRUG_ROOT),
				ids(Set.of(TRIPHASIL_TABLET))
			)
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementWithAttributeDisjunction() throws Exception {
		generateDrugHierarchy();
		generateTisselKit();
		final Expression actual = eval(String.format("<%s:%s=%s OR %s=%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT2, HAS_ACTIVE_INGREDIENT, INGREDIENT4));
		final Expression expected = Expressions.bool()
				.should(and(
					descendantsOf(DRUG_ROOT),
					ids(Set.of(TRIPHASIL_TABLET))
				))
				.should(and(
					descendantsOf(DRUG_ROOT),
					ids(Set.of(TISSEL_KIT)))
				)
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementWithConjunctionAndDisjunction() throws Exception {
		generateDrugHierarchy();
		generateTisselKit();
		final Expression actual = eval(String.format("<%s:%s=%s OR (%s=%s AND %s=%s)", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT2, HAS_ACTIVE_INGREDIENT, INGREDIENT4, HAS_ACTIVE_INGREDIENT, INGREDIENT2));
		final Expression expected = Expressions.bool()
				.should(and(
					descendantsOf(DRUG_ROOT),
					ids(Set.of(TRIPHASIL_TABLET))
				))
				.should(and(
					and(
						descendantsOf(DRUG_ROOT),
						ids(Set.of(TISSEL_KIT))
					),
					and(
						descendantsOf(DRUG_ROOT),
						ids(Set.of(TRIPHASIL_TABLET))
					)
				))
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
		final Expression expected = ids(Set.of(INGREDIENT1));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dottedWithDescendantOf() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s.%s", DRUG_ROOT, HAS_ACTIVE_INGREDIENT));
		final Expression expected = ids(Set.of(INGREDIENT1, INGREDIENT2));
		assertEquals(expected, actual);
	}
	
	@Test
	public void dottedWithComplexLeftSide() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("(<%s MINUS %s).%s", DRUG_ROOT, TRIPHASIL_TABLET, HAS_ACTIVE_INGREDIENT));
		final Expression expected = ids(Set.of(INGREDIENT1));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementGroupWithDefaultCardinality() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: {%s=<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(ASPIRIN_TABLET, ALGOFLEX_TABLET, TRIPLEX_TABLET))
		);
		assertEquals(expected, actual);
	}

	@Test
	public void refinementGroupCardinalityTwoToTwo() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: [2..2] {%s=<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(ALGOFLEX_TABLET))
		);
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
		final Expression expected = Expressions.bool()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(Set.of(ASPIRIN_TABLET, ALGOFLEX_TABLET, TRIPLEX_TABLET)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementGroupCardinalityZeroToOne() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: [0..1] {%s=<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = Expressions.bool()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(Set.of(ALGOFLEX_TABLET)))
				.build();
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementWithGroupConjunction() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: [1..1] {%s=<%s,%s=<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE, HAS_BOSS, SUBSTANCE));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(ASPIRIN_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementWithZeroToOneCardinalityInAttributeConjuction() throws Exception {
		generateHierarchy();
		final Expression actual = eval(String.format("<<%s:{[0..1]%s=<<%s,[1..1]%s=<<%s,[1..1]%s=<<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE, HAS_BOSS, SUBSTANCE, HAS_TRADE_NAME, SUBSTANCE));
		
		final Expression descendantsOrSelfOf = Expressions.bool()
				.should(ids(Collections.singleton(DRUG_ROOT)))
				.should(isInferred() ? parents(Collections.singleton(DRUG_ROOT)) : statedParents(Collections.singleton(DRUG_ROOT)))
				.should(isInferred() ? ancestors(Collections.singleton(DRUG_ROOT)) : statedAncestors(Collections.singleton(DRUG_ROOT)))
				.build();
		
		final Expression expected = and(
			descendantsOrSelfOf,
			ids(Set.of(ABACAVIR_TABLET, PANADOL_TABLET))
		);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementGroupDefaultCardinalityAndRelationshipOneToOneCardinality() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: {[1..1] %s=<%s,[1..1] %s=<%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE, HAS_BOSS, SUBSTANCE));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(ASPIRIN_TABLET, ALGOFLEX_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementWithGroupDisjunction() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: [1..1] {%s=%s OR %s=%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT5, HAS_BOSS, INGREDIENT5));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(ASPIRIN_TABLET, TRIPLEX_TABLET))
		);
		assertEquals(expected, actual);
	}

	@Test
	public void refinementGroupNestedConjunctionDisjunction() throws Exception {
		generateDrugsWithGroups();
		
		final Expression actual = eval(String.format("<%s: [1..1] {(%s=%s AND %s=%s) OR %s=%s}", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, INGREDIENT5, HAS_BOSS, INGREDIENT6, HAS_ACTIVE_INGREDIENT, INGREDIENT6));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(ASPIRIN_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementBooleanValueEquals() throws Exception {
		// Boolean evaluation only works with CD members; skip test in value mode
		assumeFalse(isStatementsWithValue());
		
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: %s = true", DRUG_ROOT, MANUFACTURED));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(PANADOL_TABLET, TRIPHASIL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementBooleanValueNotEquals() throws Exception {
		// Boolean evaluation only works with CD members; skip test in value mode
		assumeFalse(isStatementsWithValue());

		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: %s != true", DRUG_ROOT, MANUFACTURED));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(AMOXICILLIN_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementStringEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: %s = 'PANADOL'", DRUG_ROOT, HAS_TRADE_NAME));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(PANADOL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAnyStringEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("*: %s = 'PANADOL'", HAS_TRADE_NAME));
		final Expression expected = ids(Set.of(PANADOL_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementStringNotEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: %s != 'PANADOL'", DRUG_ROOT, HAS_TRADE_NAME));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT), 
			ids(Set.of(TRIPHASIL_TABLET, AMOXICILLIN_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementStringManyValuedEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("<%s: %s = ('PANADOL' 'TRIPHASIL')", DRUG_ROOT, HAS_TRADE_NAME));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(PANADOL_TABLET, TRIPHASIL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementAnyStringNotEquals() throws Exception {
		generateDrugHierarchy();
		
		final Expression actual = eval(String.format("*: %s != 'PANADOL'", HAS_TRADE_NAME));
		final Expression expected = ids(Set.of(TRIPHASIL_TABLET, AMOXICILLIN_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerEquals() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s: %s = #500", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(PANADOL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerEqualsNegative() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s: %s = #-500", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(TRIPHASIL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerNotEquals() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s: %s != #500", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(TRIPHASIL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalEquals() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s: %s = #5.5", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(AMOXICILLIN_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalEqualsNegative() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s: %s = #-5.5", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(ABACAVIR_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalNotEquals() throws Exception {
		generateDrugHierarchy();
		final Expression actual = eval(String.format("<%s: %s != #5.5", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(ABACAVIR_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerLessThan() throws Exception {
		generateDrugHierarchy();
		generateDrugWithIntegerStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s < #1", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(TRIPHASIL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalLessThan() throws Exception {
		generateDrugHierarchy();
		generateDrugWithDecimalStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s < #1.0", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(ABACAVIR_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerLessThanOrEquals() throws Exception {
		generateDrugHierarchy();
		generateDrugWithIntegerStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s <= #1", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(TRIPHASIL_TABLET, DRUG_1_MG))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalLessThanOrEquals() throws Exception {
		generateDrugHierarchy();
		generateDrugWithDecimalStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s <= #1.0", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(ABACAVIR_TABLET, DRUG_1D_MG))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerGreaterThan() throws Exception {
		generateDrugHierarchy();
		generateDrugWithIntegerStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s > #1", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(PANADOL_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalGreaterThan() throws Exception {
		generateDrugHierarchy();
		generateDrugWithDecimalStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s > #1.0", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(AMOXICILLIN_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementIntegerGreaterThanEquals() throws Exception {
		generateDrugHierarchy();
		generateDrugWithIntegerStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s >= #1", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(PANADOL_TABLET, DRUG_1_MG))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void refinementDecimalGreaterThanEquals() throws Exception {
		generateDrugHierarchy();
		generateDrugWithDecimalStrengthOfValueOne();
		final Expression actual = eval(String.format("<%s: %s >= #1.0", DRUG_ROOT, PREFERRED_STRENGTH));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(AMOXICILLIN_TABLET, DRUG_1D_MG))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void descendantsOfAllMembersOfReferenceSet() throws Exception {
		final String refSetId = Concepts.REFSET_DESCRIPTION_TYPE;
		final String member1 = RandomSnomedIdentiferGenerator.generateConceptId();
		final String member2 = RandomSnomedIdentiferGenerator.generateConceptId();
		
		indexRevision(MAIN, 
			concept(member1)
				.activeMemberOf(Set.of(refSetId))
				.build(),
			concept(member2)
				.activeMemberOf(Set.of(refSetId))
				.build()
		);
		
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
		final Expression expected = ids(Set.of(ASPIRIN_TABLET, TRIPLEX_TABLET, ALGOFLEX_TABLET));
		assertEquals(expected, actual);
	}
	
	@Test
	public void defaultGroupCardinalityWithZeroToOneAttributeCardinality() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: { [0..1] %s = <%s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = and(
			descendantsOf(DRUG_ROOT),
			ids(Set.of(ASPIRIN_TABLET, TRIPLEX_TABLET, ALGOFLEX_TABLET))
		);
		assertEquals(expected, actual);
	}
	
	@Test
	public void groupCardinalityZeroToZeroWithDefaultAttributeCardinality() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: [0..0] { %s = <%s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = Expressions.bool()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(Set.of(ALGOFLEX_TABLET, TRIPLEX_TABLET, ASPIRIN_TABLET)))
				.build();;
		assertEquals(expected, actual);
	}
	
	@Test
	public void groupCardinalityZeroToOneWithDefaultAttributeCardinality() throws Exception {
		generateDrugsWithGroups();
		final Expression actual = eval(String.format("<%s: [0..1] { %s = <%s }", DRUG_ROOT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		final Expression expected = Expressions.bool()
				.filter(descendantsOf(DRUG_ROOT))
				.mustNot(ids(Set.of(ALGOFLEX_TABLET)))
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
		final Expression expected = ids(Set.of(ASPIRIN_TABLET, TRIPLEX_TABLET, EPOX_TABLET));
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
		StagingArea staging = index()
			.prepareCommit(MAIN)
			.stageNew(concept(INGREDIENT1)
					.parents(SUBSTANCEL)
					.statedParents(SUBSTANCEL)
					.build())
			.stageNew(concept(INGREDIENT2)
					.parents(SUBSTANCEL)
					.statedParents(SUBSTANCEL)
					.build())
			.stageNew(concept(INGREDIENT3)
					.parents(SUBSTANCEL)
					.statedParents(SUBSTANCEL)
					.build())
			// drugs
			.stageNew(concept(ABACAVIR_TABLET)
					.parents(DRUG_ROOTL)
					.statedParents(DRUG_ROOTL)
					.build())
			.stageNew(concept(PANADOL_TABLET)
					.parents(DRUG_ROOTL)
					.statedParents(DRUG_ROOTL)
					.build())
			.stageNew(concept(TRIPHASIL_TABLET)
					.parents(DRUG_ROOTL)
					.statedParents(DRUG_ROOTL)
					.build())
			.stageNew(concept(AMOXICILLIN_TABLET)
					.parents(DRUG_ROOTL)
					.statedParents(DRUG_ROOTL)
					.build());
		
		// has active ingredient relationships
		if (isAxiom()) {
			staging.stageNew(classAxioms(PANADOL_TABLET, 
				HAS_ACTIVE_INGREDIENT, INGREDIENT1, 0
			).build());
			staging.stageNew(classAxioms(TRIPHASIL_TABLET, 
				HAS_ACTIVE_INGREDIENT, INGREDIENT1, 0,
				HAS_ACTIVE_INGREDIENT, INGREDIENT2, 0,
				HAS_BOSS, INGREDIENT2, 0
			).build());
		} else {
			staging
				.stageNew(relationship(PANADOL_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT1, getCharacteristicType()).relationshipGroup(0).build())
				.stageNew(relationship(TRIPHASIL_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT1, getCharacteristicType()).relationshipGroup(0).build())
				.stageNew(relationship(TRIPHASIL_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT2, getCharacteristicType()).relationshipGroup(0).build())
				.stageNew(relationship(TRIPHASIL_TABLET, HAS_BOSS, INGREDIENT2, getCharacteristicType()).relationshipGroup(0).build());
		}
		
		// XXX: This relationship's characteristicType setting is here for a reason so in ecl searches we won't find this
		staging.stageNew(relationship(AMOXICILLIN_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT1, getCharacteristicType())
				.relationshipGroup(0)
				.characteristicTypeId(isInferred() ? Concepts.STATED_RELATIONSHIP : Concepts.INFERRED_RELATIONSHIP) // inverse!
				.build());
		
		if (isStatementsWithValue()) {
			if (isAxiom()) {
				staging
				// trade names and strength as combined axioms
				.stageNew(classAxiomsWithValue(PANADOL_TABLET, 
					HAS_TRADE_NAME, "PANADOL", 0,
					PREFERRED_STRENGTH, 500, 0).build())
				.stageNew(classAxiomsWithValue(TRIPHASIL_TABLET, 
					HAS_TRADE_NAME, "TRIPHASIL", 0,
					PREFERRED_STRENGTH, -500, 0).build())
				.stageNew(classAxiomsWithValue(AMOXICILLIN_TABLET, 
					HAS_TRADE_NAME, "AMOXICILLIN", 0,
					PREFERRED_STRENGTH, BigDecimal.valueOf(5.5d), 0).build())
				// strengths as axioms
				.stageNew(classAxiomsWithValue(ABACAVIR_TABLET, 
					PREFERRED_STRENGTH, BigDecimal.valueOf(-5.5d), 0).build());

			} else {
				staging
				// trade names
				.stageNew(stringValue(PANADOL_TABLET, HAS_TRADE_NAME, "PANADOL", getCharacteristicType()).build())
				.stageNew(stringValue(TRIPHASIL_TABLET, HAS_TRADE_NAME, "TRIPHASIL", getCharacteristicType()).build())
				.stageNew(stringValue(AMOXICILLIN_TABLET, HAS_TRADE_NAME, "AMOXICILLIN", getCharacteristicType()).build())
				// strengths
				.stageNew(integerValue(PANADOL_TABLET, PREFERRED_STRENGTH, 500, getCharacteristicType()).build())
				.stageNew(integerValue(TRIPHASIL_TABLET, PREFERRED_STRENGTH, -500, getCharacteristicType()).build())
				.stageNew(decimalValue(AMOXICILLIN_TABLET, PREFERRED_STRENGTH, BigDecimal.valueOf(5.5d), getCharacteristicType()).build())
				.stageNew(decimalValue(ABACAVIR_TABLET, PREFERRED_STRENGTH, BigDecimal.valueOf(-5.5d), getCharacteristicType()).build());
				// XXX: manufactured flags are not indexed as relationships do not support boolean values
			}
		} else {
			staging
			// trade names
			.stageNew(stringMember(PANADOL_TABLET, HAS_TRADE_NAME, "PANADOL", getCharacteristicType()).build())
			.stageNew(stringMember(TRIPHASIL_TABLET, HAS_TRADE_NAME, "TRIPHASIL", getCharacteristicType()).build())
			.stageNew(stringMember(AMOXICILLIN_TABLET, HAS_TRADE_NAME, "AMOXICILLIN", getCharacteristicType()).build())
			// strengths
			.stageNew(integerMember(PANADOL_TABLET, PREFERRED_STRENGTH, 500, getCharacteristicType()).build())
			.stageNew(integerMember(TRIPHASIL_TABLET, PREFERRED_STRENGTH, -500, getCharacteristicType()).build())
			.stageNew(decimalMember(AMOXICILLIN_TABLET, PREFERRED_STRENGTH, BigDecimal.valueOf(5.5d), getCharacteristicType()).build())
			.stageNew(decimalMember(ABACAVIR_TABLET, PREFERRED_STRENGTH, BigDecimal.valueOf(-5.5d), getCharacteristicType()).build())
			// manufactured flags
			.stageNew(booleanMember(PANADOL_TABLET, MANUFACTURED, true, getCharacteristicType()).build())
			.stageNew(booleanMember(TRIPHASIL_TABLET, MANUFACTURED, true, getCharacteristicType()).build())
			.stageNew(booleanMember(AMOXICILLIN_TABLET, MANUFACTURED, false, getCharacteristicType()).build());
		}
		
		staging.commit(currentTime(), UUID.randomUUID().toString(), "Initialize generated drugs");
	}

	/**
	 * Generates the following test fixtures:
	 * <ul>
	 * 	<li>Substances (children of SUBSTANCE):
	 * 		<ul>
	 * 			<li>INGREDIENT1 (ingredient with three inbound from PANADOL and ABACAVIR TABLET)</li>
	 * 			<li>INGREDIENT2 (ingredient with five inbound from TRIPHASIL AND PANADOL AND ABACAVIR TABLET)</li>
	 * 		</ul>
	 * 	</li>
	 * 	<li>Drugs (children of DRUG_ROOT):
	 * 		<ul>
	 * 			<li>ABACAVIR_TABLET (drug with two outgoing inferred relationships, one HAS_BOSS and one HAS_TRADE_NAME relationship to INGREDIENT1)</li>
	 * 			<li>PANADOL_TABLET (drug with three outgoing inferred relationships, one HAI to INGREDIENT1, one HAS_BOSS to INGREDIENT 2 and one HAS_TRADE_NAME to INGREDIENT2)</li>
	 * 			<li>TRIPHASIL_TABLET (drug with three outgoing inferred relationships, one HAI, one HAS_BOSS and one HAS_TRADE_NAME to INGREDIENT2)</li>
	 * 		</ul>
	 * 	</li>
	 * </ul>
	 */
	private void generateHierarchy() {
		StagingArea staging = index().prepareCommit(MAIN)
			// substances
			.stageNew(concept(INGREDIENT1)
					.parents(SUBSTANCEL)
					.statedParents(SUBSTANCEL)
					.build())
			.stageNew(concept(INGREDIENT2)
				.parents(SUBSTANCEL)
				.statedParents(SUBSTANCEL)
				.build())
			//drugs
			.stageNew(concept(ABACAVIR_TABLET)
				.parents(DRUG_ROOTL)
				.statedParents(DRUG_ROOTL)
				.build())
			.stageNew(concept(PANADOL_TABLET)
				.parents(DRUG_ROOTL)
				.statedParents(DRUG_ROOTL)
				.build())
			.stageNew(concept(TRIPHASIL_TABLET)
				.parents(DRUG_ROOTL)
				.statedParents(DRUG_ROOTL)
				.build());
		
		if (isAxiom()) {
			staging.stageNew(classAxioms(PANADOL_TABLET, 
				HAS_ACTIVE_INGREDIENT, INGREDIENT1, 1,
				HAS_BOSS, INGREDIENT2, 1,
				HAS_TRADE_NAME, INGREDIENT2, 1
			).build());
			staging.stageNew(classAxioms(ABACAVIR_TABLET, 
				HAS_BOSS, INGREDIENT1, 1,
				HAS_TRADE_NAME, INGREDIENT1, 1
			).build());
			staging.stageNew(classAxioms(TRIPHASIL_TABLET, 
				HAS_ACTIVE_INGREDIENT, INGREDIENT2, 2,
				HAS_BOSS, INGREDIENT2, 2,
				HAS_TRADE_NAME, INGREDIENT2, 1
			).build());
		} else {
			staging
				.stageNew(relationship(PANADOL_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT1, getCharacteristicType()).relationshipGroup(1).build())
				.stageNew(relationship(PANADOL_TABLET, HAS_BOSS, INGREDIENT2, getCharacteristicType()).relationshipGroup(1).build())
				.stageNew(relationship(PANADOL_TABLET, HAS_TRADE_NAME, INGREDIENT2, getCharacteristicType()).relationshipGroup(1).build())
				.stageNew(relationship(ABACAVIR_TABLET, HAS_BOSS, INGREDIENT1, getCharacteristicType()).relationshipGroup(1).build())
				.stageNew(relationship(ABACAVIR_TABLET, HAS_TRADE_NAME, INGREDIENT1, getCharacteristicType()).relationshipGroup(1).build());
			
			staging
				.stageNew(relationship(TRIPHASIL_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT2, getCharacteristicType()).relationshipGroup(2).build())
				.stageNew(relationship(TRIPHASIL_TABLET, HAS_BOSS, INGREDIENT2, getCharacteristicType()).relationshipGroup(2).build())
				.stageNew(relationship(TRIPHASIL_TABLET, HAS_TRADE_NAME, INGREDIENT2, getCharacteristicType()).relationshipGroup(1).build());
		}
		
		staging.commit(currentTime(), "test", "Generate hierarchy");
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
		StagingArea staging = index().prepareCommit(MAIN);
		staging.stageNew(concept(INGREDIENT4)
					.parents(SUBSTANCEL)
					.statedParents(SUBSTANCEL)
					.build());
		staging.stageNew(concept(TISSEL_KIT)
					.parents(DRUG_ROOTL)
					.statedParents(DRUG_ROOTL)
					.build());
		
		if (isAxiom()) {
			staging.stageNew(classAxioms(TISSEL_KIT, 
				HAS_ACTIVE_INGREDIENT, INGREDIENT4, 0
			).build());
		} else {
			staging.stageNew(relationship(TISSEL_KIT, HAS_ACTIVE_INGREDIENT, INGREDIENT4, getCharacteristicType()).relationshipGroup(0).build());
		}
		
		staging.commit(currentTime(), "test", "Generate TISSEL Kit");
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
		StagingArea staging = index().prepareCommit(MAIN);
		
		staging
			.stageNew(concept(INGREDIENT5)
					.parents(SUBSTANCEL)
					.statedParents(SUBSTANCEL)
					.build())
			.stageNew(concept(INGREDIENT6)
					.parents(SUBSTANCEL)
					.statedParents(SUBSTANCEL)
					.build())
			
			.stageNew(concept(EPOX_TABLET)
					.parents(DRUG_ROOTL)
					.statedParents(DRUG_ROOTL)
					.build())
			.stageNew(concept(ASPIRIN_TABLET)
					.parents(DRUG_ROOTL)
					.statedParents(DRUG_ROOTL)
					.build())
			.stageNew(concept(ALGOFLEX_TABLET)
					.parents(DRUG_ROOTL)
					.statedParents(DRUG_ROOTL)
					.build())
			.stageNew(concept(TRIPLEX_TABLET)
					.parents(DRUG_ROOTL)
					.statedParents(DRUG_ROOTL)
					.build());
			
		if (isAxiom()) {
			staging
				.stageNew(classAxioms(EPOX_TABLET, 
						HAS_ACTIVE_INGREDIENT, INGREDIENT5, 0
				).build())
				.stageNew(classAxioms(ASPIRIN_TABLET, 
					HAS_ACTIVE_INGREDIENT, INGREDIENT5, 1,
					HAS_BOSS, INGREDIENT6, 1
				).build())
				.stageNew(classAxioms(ALGOFLEX_TABLET, 
					HAS_ACTIVE_INGREDIENT, INGREDIENT5, 1,
					HAS_BOSS, INGREDIENT6, 1,
					HAS_ACTIVE_INGREDIENT, INGREDIENT6, 2,
					HAS_BOSS, INGREDIENT5, 2
				).build())
				.stageNew(classAxioms(TRIPLEX_TABLET, 
						HAS_ACTIVE_INGREDIENT, INGREDIENT5, 1,
						HAS_BOSS, INGREDIENT6, 2)
				.build());
		} else {
			staging
				.stageNew(relationship(EPOX_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT5, getCharacteristicType()).relationshipGroup(0).build())
			
				.stageNew(relationship(ASPIRIN_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT5, getCharacteristicType()).relationshipGroup(1).build())
				.stageNew(relationship(ASPIRIN_TABLET, HAS_BOSS, INGREDIENT6, getCharacteristicType()).relationshipGroup(1).build())
				
				.stageNew(relationship(ALGOFLEX_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT5, getCharacteristicType()).relationshipGroup(1).build())
				.stageNew(relationship(ALGOFLEX_TABLET, HAS_BOSS, INGREDIENT6, getCharacteristicType()).relationshipGroup(1).build())
				.stageNew(relationship(ALGOFLEX_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT6, getCharacteristicType()).relationshipGroup(2).build())
				.stageNew(relationship(ALGOFLEX_TABLET, HAS_BOSS, INGREDIENT5, getCharacteristicType()).relationshipGroup(2).build())
				
				.stageNew(relationship(TRIPLEX_TABLET, HAS_ACTIVE_INGREDIENT, INGREDIENT5, getCharacteristicType()).relationshipGroup(1).build())
				.stageNew(relationship(TRIPLEX_TABLET, HAS_BOSS, INGREDIENT6, getCharacteristicType()).relationshipGroup(2).build());
		}
			
		staging.commit(currentTime(), UUID.randomUUID().toString(), "Initialize Drugs with groups");
	}
	
	private void generateDrugWithIntegerStrengthOfValueOne() {
		indexRevision(MAIN, 
			concept(DRUG_1_MG)
				.parents(DRUG_ROOTL)
				.statedParents(DRUG_ROOTL)
				.build(),
				
			isStatementsWithValue() 
				? integerValue(DRUG_1_MG, PREFERRED_STRENGTH, 1, getCharacteristicType()).build()
				: integerMember(DRUG_1_MG, PREFERRED_STRENGTH, 1, getCharacteristicType()).build()
		);
	}
	
	private void generateDrugWithDecimalStrengthOfValueOne() {
		indexRevision(MAIN, 
			concept(DRUG_1D_MG)
				.parents(DRUG_ROOTL)
				.statedParents(DRUG_ROOTL)	
				.build(),
				
			isStatementsWithValue()
				? decimalValue(DRUG_1D_MG, PREFERRED_STRENGTH, BigDecimal.valueOf(1.0d), getCharacteristicType()).build()
				: decimalMember(DRUG_1D_MG, PREFERRED_STRENGTH, BigDecimal.valueOf(1.0d), getCharacteristicType()).build()
		);
	}
	
	private static Expression and(Expression left, Expression right) {
		return Expressions.bool().filter(left).filter(right).build();
	}
	
	private Expression descendantsOf(String...conceptIds) {
		if (isInferred()) {
			return Expressions.bool()
					.should(parents(Set.of(conceptIds)))
					.should(ancestors(Set.of(conceptIds)))
					.build();
		} else {
			return Expressions.bool()
					.should(statedParents(Set.of(conceptIds)))
					.should(statedAncestors(Set.of(conceptIds)))
					.build();
		}
	}

}
