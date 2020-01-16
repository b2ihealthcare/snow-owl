/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.scg.tests

import com.b2international.snowowl.snomed.scg.scg.Attribute
import com.b2international.snowowl.snomed.scg.scg.ConceptReference
import com.b2international.snowowl.snomed.scg.scg.DecimalValue
import com.b2international.snowowl.snomed.scg.scg.Expression
import com.b2international.snowowl.snomed.scg.scg.IntegerValue
import com.b2international.snowowl.snomed.scg.scg.StringValue
import com.b2international.snowowl.snomed.scg.scg.SubExpression
import com.google.inject.Inject
import java.math.BigDecimal
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue

@RunWith(XtextRunner)
@InjectWith(ScgInjectorProvider)
class ScgParsingTest {

	@Inject extension ParseHelper<Expression>
	@Inject extension ValidationTestHelper

	@Test
	def void test_empty() {
		''.assertNoErrors
	}

	// Official examples from https://github.com/IHTSDO/SNOMEDCT-Languages

	@Test
	def void test_simple_expression_1() {
		
		val expression = '''
			73211009 |diabetes mellitus|
		'''.assertNoErrors
		
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "73211009", "diabetes mellitus")
		assertNull(expression.expression.refinement)
		
	}

	@Test
	def void test_simple_expression_2() {
		
		val expression = '''
			73211009
		'''.assertNoErrors
		
		assertEquals(1, expression.expression.focusConcepts.size)
		assertEquals("73211009", expression.expression.focusConcepts.head.id)
		assertNull(expression.expression.focusConcepts.head.term)
		assertNull(expression.expression.refinement)
		
	}

	@Test
	def void test_multiple_focus_concepts_1() {
		
		val expression = '''
			421720008 |spray dose form| + 7946007 |drug suspension|
		'''.assertNoErrors
		
		assertEquals(2, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "421720008", "spray dose form")
		assertConceptReference(expression.expression.focusConcepts.last, "7946007", "drug suspension")
		assertNull(expression.expression.refinement)
		
	}

	@Test
	def void test_multiple_focus_concepts_2() {
		
		val expression = '''
			421720008 + 7946007 |drug suspension|
		'''.assertNoErrors
		
		assertEquals(2, expression.expression.focusConcepts.size)
		assertEquals("421720008", expression.expression.focusConcepts.head.id)
		assertNull(expression.expression.focusConcepts.head.term)
		assertConceptReference(expression.expression.focusConcepts.last, "7946007", "drug suspension")
		assertNull(expression.expression.refinement)
		
	}

	@Test
	def void test_multiple_focus_concepts_3() {
		
		val expression = '''
			421720008
			+ 7946007
			|drug suspension|
		'''.assertNoErrors
		
		assertEquals(2, expression.expression.focusConcepts.size)
		assertEquals("421720008", expression.expression.focusConcepts.head.id)
		assertNull(expression.expression.focusConcepts.head.term)
		assertConceptReference(expression.expression.focusConcepts.last, "7946007", "drug suspension")
		assertNull(expression.expression.refinement)
		
	}

	@Test
	def void test_expression_with_definition_type_1() {
		
		val expression = '''
			===  46866001 |fracture of lower limb| + 428881005 |injury of tibia|:
				116676008 |associated morphology| = 72704001 |fracture|,
				363698007 |finding site| = 12611008 |bone structure of tibia|
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(2, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "46866001", "fracture of lower limb")
		assertConceptReference(expression.expression.focusConcepts.last, "428881005", "injury of tibia")
		
		assertEquals(2, expression.expression.refinement.attributes.size)
		assertSimpleAttribute(expression.expression.refinement.attributes.head, "116676008", "associated morphology", "72704001", "fracture")
		assertSimpleAttribute(expression.expression.refinement.attributes.last, "363698007", "finding site", "12611008", "bone structure of tibia")
		
	}
	
	@Test
	def void test_expression_with_definition_type_2() {
		
		val expression = '''
			<<< 73211009 |diabetes mellitus|: 363698007 |finding site| = 113331007 |endocrine system|
		'''.assertNoErrors
		
		assertTrue(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "73211009", "diabetes mellitus")
		
		assertEquals(1, expression.expression.refinement.attributes.size)
		assertSimpleAttribute(expression.expression.refinement.attributes.head, "363698007", "finding site", "113331007", "endocrine system")
		
	}

	@Test
	def void test_expression_with_attribute_group_1() {
		
		val expression = '''
			71388002 |procedure|:
			{
				260686004 |method| = 129304002 |excision - action|,
				405813007 |procedure site - direct| = 15497006 |ovarian structure|
			}
			{
				260686004 |method| = 129304002 |excision - action|,
				405813007 |procedure site - direct| = 31435000 |fallopian tube structure|
			}
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "71388002", "procedure")
		
		assertTrue(expression.expression.refinement.attributes.empty)
		assertEquals(2, expression.expression.refinement.groups.size)
		
		val group1 = expression.expression.refinement.groups.head
		
		assertEquals(2, group1.attributes.size)
		
		assertSimpleAttribute(group1.attributes.head, "260686004", "method", "129304002", "excision - action")
		assertSimpleAttribute(group1.attributes.last, "405813007", "procedure site - direct", "15497006", "ovarian structure")
		
		val group2 = expression.expression.refinement.groups.last
		
		assertEquals(2, group2.attributes.size)
		
		assertSimpleAttribute(group2.attributes.head, "260686004", "method", "129304002", "excision - action")
		assertSimpleAttribute(group2.attributes.last, "405813007", "procedure site - direct", "31435000", "fallopian tube structure")
		
	}

	@Test
	def void test_expression_with_attribute_group_2() {
		
		val expression = '''
			71388002 |procedure|:
			{
				260686004 |method| = 129304002 |excision - action|,
				405813007 |procedure site - direct| = 20837000 |structure of right ovary|,
				424226004 |using device| = 122456005 |laser device|
			}
			{
				260686004 |method| = 261519002 |diathermy excision - action|,
				405813007 |procedure site - direct| = 113293009 |structure of left fallopian tube|
			}
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "71388002", "procedure")
		
		assertTrue(expression.expression.refinement.attributes.empty)
		assertEquals(2, expression.expression.refinement.groups.size)
		
		val group1 = expression.expression.refinement.groups.head
		
		assertEquals(3, group1.attributes.size)
		
		assertSimpleAttribute(group1.attributes.get(0), "260686004", "method", "129304002", "excision - action")
		assertSimpleAttribute(group1.attributes.get(1), "405813007", "procedure site - direct", "20837000", "structure of right ovary")
		assertSimpleAttribute(group1.attributes.get(2), "424226004", "using device", "122456005", "laser device")
		
		val group2 = expression.expression.refinement.groups.last
		
		assertEquals(2, group2.attributes.size)
		
		assertSimpleAttribute(group2.attributes.get(0), "260686004", "method", "261519002", "diathermy excision - action")
		assertSimpleAttribute(group2.attributes.get(1), "405813007", "procedure site - direct", "113293009", "structure of left fallopian tube")
		
	}
	
	@Test
	def void test_expression_with_concrete_value_1() {
		
		val expression = '''
			373873005 |pharmaceutical / biologic product|:
				411116001 |has dose form| = 385049006 |capsule|,
				111115 |active ingredient count| = #1,
				{
					127489000 |has active ingredient| = 96068000 |amoxicillin trihydrate|,
					111115 |has reference basis of strength| = 372687004 |amoxicillin|,
					111115 |strength magnitude equal to| = #500,
					111115 |strength unit| = 258684004 |mg|
				}
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "373873005", "pharmaceutical / biologic product")
		
		assertEquals(2, expression.expression.refinement.attributes.size)
		
		assertSimpleAttribute(expression.expression.refinement.attributes.head, "411116001", "has dose form", "385049006", "capsule")
		assertIntegerAttribute(expression.expression.refinement.attributes.last, "111115", "active ingredient count", 1)
		
		assertEquals(1, expression.expression.refinement.groups.size)
		
		val group = expression.expression.refinement.groups.head
		
		assertEquals(4, group.attributes.size)
		
		assertSimpleAttribute(group.attributes.get(0), "127489000", "has active ingredient", "96068000", "amoxicillin trihydrate")
		assertSimpleAttribute(group.attributes.get(1), "111115", "has reference basis of strength", "372687004", "amoxicillin")
		assertIntegerAttribute(group.attributes.get(2), "111115", "strength magnitude equal to", 500)
		assertSimpleAttribute(group.attributes.get(3), "111115", "strength unit", "258684004", "mg")
		
	}

	@Test
	def void test_expression_with_concrete_value_2() {
		
		val expression = '''
			373873005 |pharmaceutical / biologic product|:
				411116001 |has dose form| = 385023001 |oral solution|,
				111115 |active ingredient count| = #1,
				{
					127489000 |has active ingredient| = 372897005 |albuterol|,
					111115 |has reference basis of strength| = 372897005 |albuterol|,
					111115 |strength magnitude equal to| = #0.083,
					111115 |strength unit| = 118582008 |%|
				}
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "373873005", "pharmaceutical / biologic product")
		
		assertEquals(2, expression.expression.refinement.attributes.size)
		
		assertSimpleAttribute(expression.expression.refinement.attributes.head, "411116001", "has dose form", "385023001", "oral solution")
		assertIntegerAttribute(expression.expression.refinement.attributes.last, "111115", "active ingredient count", 1)
		
		assertEquals(1, expression.expression.refinement.groups.size)
		
		val group = expression.expression.refinement.groups.head
		
		assertEquals(4, group.attributes.size)
		
		assertSimpleAttribute(group.attributes.get(0), "127489000", "has active ingredient", "372897005", "albuterol")
		assertSimpleAttribute(group.attributes.get(1), "111115", "has reference basis of strength", "372897005", "albuterol")
		assertDecimalAttribute(group.attributes.get(2), "111115", "strength magnitude equal to", 0.083bd)
		assertSimpleAttribute(group.attributes.get(3), "111115", "strength unit", "118582008", "%")
		
	}

	@Test
	def void test_expression_with_concrete_value_3() {
		
		val expression = '''
			322236009 |paracetamol 500 mg tablet|: 111115 |trade name| = "PANADOL"
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "322236009", "paracetamol 500 mg tablet")
		
		assertEquals(1, expression.expression.refinement.attributes.size)
		assertStringAttribute(expression.expression.refinement.attributes.head, "111115", "trade name", "PANADOL")
		
	}

	@Test
	def void test_expression_with_concrete_value_4() {
		
		val expression = '''
			373873005 |pharmaceutical / biologic product|:
				411116001 |has dose form| = 385218009 |injection|,
				111115 |active ingredient count| = #2,
				{
					127489000 |has active ingredient| = 428126001 |diphtheria toxoid|,
					111115 |has reference basis of strength| = 428126001 |diphtheria toxoid|,
					111115 |strength magnitude minimum| = #4,
					111115 |strength unit| = 259002007 |IU/mL|
				}
				{
					127489000 |has active ingredient| = 412375000 |tetanus toxoid|,
					111115 |has reference basis of strength| = 412375000 |tetanus toxoid|,
					111115 |strength magnitude equal to| = #40,
					111115 |strength unit| = 259002007 |IU/mL|
				}
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "373873005", "pharmaceutical / biologic product")
		
		assertEquals(2, expression.expression.refinement.attributes.size)
		
		assertSimpleAttribute(expression.expression.refinement.attributes.head, "411116001", "has dose form", "385218009", "injection")
		assertIntegerAttribute(expression.expression.refinement.attributes.last, "111115", "active ingredient count", 2)
		
		assertEquals(2, expression.expression.refinement.groups.size)
		
		val group1 = expression.expression.refinement.groups.head
		
		assertEquals(4, group1.attributes.size)
		
		assertSimpleAttribute(group1.attributes.get(0), "127489000", "has active ingredient", "428126001", "diphtheria toxoid")
		assertSimpleAttribute(group1.attributes.get(1), "111115", "has reference basis of strength", "428126001", "diphtheria toxoid")
		assertIntegerAttribute(group1.attributes.get(2), "111115", "strength magnitude minimum", 4)
		assertSimpleAttribute(group1.attributes.get(3), "111115", "strength unit", "259002007", "IU/mL")
		
		val group2 = expression.expression.refinement.groups.last
		
		assertEquals(4, group2.attributes.size)
		
		assertSimpleAttribute(group2.attributes.get(0), "127489000", "has active ingredient", "412375000", "tetanus toxoid")
		assertSimpleAttribute(group2.attributes.get(1), "111115", "has reference basis of strength", "412375000", "tetanus toxoid")
		assertIntegerAttribute(group2.attributes.get(2), "111115", "strength magnitude equal to", 40)
		assertSimpleAttribute(group2.attributes.get(3), "111115", "strength unit", "259002007", "IU/mL")
		
	}

	@Test
	def void test_expression_with_refinement_1() {
		
		val expression = '''
			83152002 |oophorectomy|:
			405815000|procedure device| = 122456005 |laser device|
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "83152002", "oophorectomy")
		
		assertTrue(expression.expression.refinement.groups.empty)
		assertEquals(1, expression.expression.refinement.attributes.size)
		
		assertSimpleAttribute(expression.expression.refinement.attributes.head, "405815000", "procedure device", "122456005", "laser device")
		
	}

	@Test
	def void test_expression_with_refinement_2() {
		
		val expression = '''
			182201002 |hip joint|:
			272741003 |laterality| = 24028007 |right|
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "182201002", "hip joint")
		
		assertTrue(expression.expression.refinement.groups.empty)
		assertEquals(1, expression.expression.refinement.attributes.size)
		
		assertSimpleAttribute(expression.expression.refinement.attributes.head, "272741003", "laterality", "24028007", "right")
		
	}

	@Test
	def void test_expression_with_refinement_3() {
		
		val expression = '''
			71388002 |procedure|:
				405815000|procedure device| = 122456005 |laser device|,
				260686004 |method| = 129304002 |excision - action|,
				405813007 |procedure site - direct| = 15497006 |ovarian structure|
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "71388002", "procedure")
		
		assertTrue(expression.expression.refinement.groups.empty)
		assertEquals(3, expression.expression.refinement.attributes.size)
		
		assertSimpleAttribute(expression.expression.refinement.attributes.get(0), "405815000", "procedure device", "122456005", "laser device")
		assertSimpleAttribute(expression.expression.refinement.attributes.get(1), "260686004", "method", "129304002", "excision - action")
		assertSimpleAttribute(expression.expression.refinement.attributes.get(2), "405813007", "procedure site - direct", "15497006", "ovarian structure")
		
	}

	@Test
	def void test_expression_with_refinement_4() {
		
		val expression = '''
			65801008 |excision|:
				405813007 |procedure site - direct| = 66754008 |appendix structure|,
				260870009 |priority| = 25876001 |emergency|
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "65801008", "excision")
		
		assertTrue(expression.expression.refinement.groups.empty)
		assertEquals(2, expression.expression.refinement.attributes.size)
		
		assertSimpleAttribute(expression.expression.refinement.attributes.head, "405813007", "procedure site - direct", "66754008", "appendix structure")
		assertSimpleAttribute(expression.expression.refinement.attributes.last, "260870009", "priority", "25876001", "emergency")
		
	}

	@Test
	def void test_expression_with_refinement_5() {
		
		val expression = '''
			313056006 |epiphysis of ulna|: 272741003 |laterality| = 7771000 |left|
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "313056006", "epiphysis of ulna")
		
		assertTrue(expression.expression.refinement.groups.empty)
		assertEquals(1, expression.expression.refinement.attributes.size)
		
		assertSimpleAttribute(expression.expression.refinement.attributes.head, "272741003", "laterality", "7771000", "left")
		
	}

	@Test
	def void test_expression_with_refinement_6() {
		
		val expression = '''
			119189000 |ulna part| + 312845000 |epiphysis of upper limb|:
				272741003 |laterality| = 7771000 |left|
		'''.assertNoErrors
		
		assertEquals(2, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "119189000", "ulna part")
		assertConceptReference(expression.expression.focusConcepts.last, "312845000", "epiphysis of upper limb")
		
		assertTrue(expression.expression.refinement.groups.empty)
		assertEquals(1, expression.expression.refinement.attributes.size)
		
		assertSimpleAttribute(expression.expression.refinement.attributes.head, "272741003", "laterality", "7771000", "left")
		
	}

	@Test
	def void test_expression_with_nested_refinement_1() {
		
		val expression = '''
			373873005 |pharmaceutical / biologic product|:
				411116001 |has dose form| =	(421720008 |spray dose form| + 7946007 |drug suspension|)
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "373873005", "pharmaceutical / biologic product")
		
		assertEquals(1, expression.expression.refinement.attributes.size)
		
		val attribute = expression.expression.refinement.attributes.head

		assertConceptReference(attribute.name, "411116001", "has dose form")
		
		assertTrue(attribute.value instanceof SubExpression)
		val subExpression = attribute.value as SubExpression
		
		assertEquals(2, subExpression.focusConcepts.size)
		assertNull(subExpression.refinement)
		
		assertConceptReference(subExpression.focusConcepts.head, "421720008", "spray dose form")
		assertConceptReference(subExpression.focusConcepts.last, "7946007", "drug suspension")
		
	}

	@Test
	def void test_expression_with_nested_refinement_2() {
		
		val expression = '''
			397956004 |prosthetic arthroplasty of the hip|:
				363704007 |procedure site| = (24136001 |hip joint structure|: 272741003 |laterality| = 7771000 |left|)
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "397956004", "prosthetic arthroplasty of the hip")
		
		assertEquals(1, expression.expression.refinement.attributes.size)
		
		val attribute = expression.expression.refinement.attributes.head

		assertConceptReference(attribute.name, "363704007", "procedure site")
		
		assertTrue(attribute.value instanceof SubExpression)
		val subExpression = attribute.value as SubExpression
		
		assertEquals(1, subExpression.focusConcepts.size)
		assertConceptReference(subExpression.focusConcepts.head, "24136001", "hip joint structure")
		
		assertEquals(1, subExpression.refinement.attributes.size)
		assertSimpleAttribute(subExpression.refinement.attributes.head, "272741003", "laterality", "7771000", "left")
		
	}

	@Test
	def void test_expression_with_nested_refinement_3() {
		
		val expression = '''
			397956004 |prosthetic arthroplasty of the hip|: 
				363704007 |procedure site| = (24136001 |hip joint structure|: 272741003 |laterality| = 7771000 |left|),
				{
					363699004 |direct device| = 304120007 |total hip replacement prosthesis|,
					260686004 |method| = 257867005 |insertion - action|
				}
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "397956004", "prosthetic arthroplasty of the hip")
		
		assertEquals(1, expression.expression.refinement.attributes.size)
		
		val attribute = expression.expression.refinement.attributes.head

		assertConceptReference(attribute.name, "363704007", "procedure site")
		
		assertTrue(attribute.value instanceof SubExpression)
		val subExpression = attribute.value as SubExpression
		
		assertEquals(1, subExpression.focusConcepts.size)
		assertConceptReference(subExpression.focusConcepts.head, "24136001", "hip joint structure")
		
		assertEquals(1, subExpression.refinement.attributes.size)
		assertSimpleAttribute(subExpression.refinement.attributes.head, "272741003", "laterality", "7771000", "left")
		
		assertEquals(1, expression.expression.refinement.groups.size)
		
		val group = expression.expression.refinement.groups.head
		
		assertEquals(2, group.attributes.size)
		
		assertSimpleAttribute(group.attributes.head, "363699004", "direct device", "304120007", "total hip replacement prosthesis")
		assertSimpleAttribute(group.attributes.last, "260686004", "method", "257867005", "insertion - action")
		
	}

	@Test
	def void test_expression_with_nested_refinement_4() {
		
		val expression = '''
			243796009 |situation with explicit context|: 
				{
					408730004 |procedure context| = 385658003 |done|,
					408731000 |temporal context| = 410512000 |current or specified|,
					408732007 |subject relationship context| = 410604004 |subject of record|,
					363589002 |associated procedure| = (
						397956004 |prosthetic arthroplasty of the hip|:
							363704007 |procedure site| = (24136001 |hip joint structure|: 272741003 |laterality| = 7771000 |left|)
							{
								363699004 |direct device| = 304120007 |total hip replacement prosthesis|,
								260686004 |method| = 257867005 |insertion - action|
							}
					) 
				}
		'''.assertNoErrors
		
		assertFalse(expression.isPrimitive)
		assertEquals(1, expression.expression.focusConcepts.size)
		assertConceptReference(expression.expression.focusConcepts.head, "243796009", "situation with explicit context")
		
		assertTrue(expression.expression.refinement.attributes.empty)
		assertEquals(1, expression.expression.refinement.groups.size)
		
		val group = expression.expression.refinement.groups.head
		
		assertEquals(4, group.attributes.size)
		
		assertSimpleAttribute(group.attributes.get(0), "408730004", "procedure context", "385658003", "done")
		assertSimpleAttribute(group.attributes.get(1), "408731000", "temporal context", "410512000", "current or specified")
		assertSimpleAttribute(group.attributes.get(2), "408732007", "subject relationship context", "410604004", "subject of record")
		
		val attribute = group.attributes.get(3)
		
		assertConceptReference(attribute.name, "363589002", "associated procedure")
		
		assertTrue(attribute.value instanceof SubExpression)
		val subExpression = attribute.value as SubExpression
		
		assertEquals(1, subExpression.focusConcepts.size)
		assertConceptReference(subExpression.focusConcepts.head, "397956004", "prosthetic arthroplasty of the hip")
		
		
		assertEquals(1, subExpression.refinement.attributes.size)
		
		assertConceptReference(subExpression.refinement.attributes.head.name, "363704007", "procedure site")
		
		assertTrue(subExpression.refinement.attributes.head.value instanceof SubExpression)
		val subExpression2 = subExpression.refinement.attributes.head.value as SubExpression
		
		assertEquals(1, subExpression2.focusConcepts.size)
		assertConceptReference(subExpression2.focusConcepts.head, "24136001", "hip joint structure")
		
		assertEquals(1, subExpression2.refinement.attributes.size)
		assertSimpleAttribute(subExpression2.refinement.attributes.head, "272741003", "laterality", "7771000", "left")
		
		
		assertEquals(1, subExpression.refinement.groups.size)
		val subGroup = subExpression.refinement.groups.head
		assertEquals(2, subGroup.attributes.size)
		
		assertSimpleAttribute(subGroup.attributes.head, "363699004", "direct device", "304120007", "total hip replacement prosthesis")
		assertSimpleAttribute(subGroup.attributes.last, "260686004", "method", "257867005", "insertion - action")
		
	}

	def private void assertSimpleAttribute(Attribute attribute, String nameId, String nameTerm, String valueId, String valueTerm) {
		assertConceptReference(attribute.name, nameId, nameTerm)
		assertTrue(attribute.value instanceof ConceptReference)
		assertConceptReference((attribute.value as ConceptReference), valueId, valueTerm)
	}
	
	def private void assertIntegerAttribute(Attribute attribute, String nameId, String nameTerm, int intValue) {
		assertConceptReference(attribute.name, nameId, nameTerm)
		assertTrue(attribute.value instanceof IntegerValue)
		assertEquals(intValue, (attribute.value as IntegerValue).value)
	}
	
	def private void assertDecimalAttribute(Attribute attribute, String nameId, String nameTerm, BigDecimal decimalValue) {
		assertConceptReference(attribute.name, nameId, nameTerm)
		assertTrue(attribute.value instanceof DecimalValue)
		assertTrue(decimalValue.equals((attribute.value as DecimalValue).value))
	}
	
	def private void assertStringAttribute(Attribute attribute, String nameId, String nameTerm, String stringValue) {
		assertConceptReference(attribute.name, nameId, nameTerm)
		assertTrue(attribute.value instanceof StringValue)
		assertEquals(stringValue, (attribute.value as StringValue).value)
	}
	
	def private void assertConceptReference(ConceptReference reference, String id, String term) {
		assertEquals(id, reference.id)
		assertEquals('|' + term + '|', reference.term)
	}
	
	private def Expression assertNoErrors(CharSequence it) throws Exception {
		val expression = parse
		assertNotNull('''Cannot parse expression: «it».''', expression)
		expression.assertNoErrors
		return expression
	}
}
