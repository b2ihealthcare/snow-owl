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
package com.b2international.snowowl.snomed.etl.tests

import com.b2international.snowowl.snomed.ecl.Ecl
import com.b2international.snowowl.snomed.ecl.ecl.Any
import com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint
import com.b2international.snowowl.snomed.ecl.ecl.AttributeValueEquals
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOf
import com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf
import com.b2international.snowowl.snomed.ecl.ecl.EclConceptReference
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint
import com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint
import com.b2international.snowowl.snomed.etl.etl.Attribute
import com.b2international.snowowl.snomed.etl.etl.ConceptIdReplacementSlot
import com.b2international.snowowl.snomed.etl.etl.ConceptReference
import com.b2international.snowowl.snomed.etl.etl.DecimalReplacementSlot
import com.b2international.snowowl.snomed.etl.etl.ExpressionReplacementSlot
import com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate
import com.b2international.snowowl.snomed.etl.etl.IntegerReplacementSlot
import com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange
import com.b2international.snowowl.snomed.etl.etl.SlotIntegerValue
import com.b2international.snowowl.snomed.etl.etl.StringReplacementSlot
import com.b2international.snowowl.snomed.etl.etl.SubExpression
import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue

@RunWith(XtextRunner)
@InjectWith(EtlInjectorProvider)
@FixMethodOrder(NAME_ASCENDING)
class EtlParsingTest {

	@Inject extension ParseHelper<ExpressionTemplate>
	@Inject extension ValidationTestHelper

	@Test
	def void test_empty() {
		''.assertNoErrors
	}

	@Test
	def void test_7_1_1_Simple_AttributeName_1() {
		
		val expression = '''
			404684003 |Clinical finding| : [[+]] =  80166006 |Streptococcus pyogenes|
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "404684003", "Clinical finding")
		
		val refinement = expression.expression.refinement
		
		assertTrue(refinement.groups.empty)
		assertEquals(1, refinement.attributes.size)
		
		val attribute = refinement.attributes.head
		
		assertNull(attribute.slot)
		
		assertNull(attribute.name.id)
		assertNull(attribute.name.term)
		
		assertTrue(attribute.name.slot instanceof ExpressionReplacementSlot)
		
		val expressionReplacementSlot = attribute.name.slot as ExpressionReplacementSlot
		
		assertNull(expressionReplacementSlot.name)
		assertNull(expressionReplacementSlot.constraint)
		
		assertTrue(attribute.value instanceof ConceptReference)
		assertConceptReference(attribute.value as ConceptReference, "80166006", "Streptococcus pyogenes")
		
	}

	@Test
	def void test_7_1_1_Simple_AttributeValue_1() {
		
		val expression = '''
			404684003 |Clinical finding| :  363698007 |Finding site|  = [[+]]
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "404684003", "Clinical finding")
		
		val refinement = expression.expression.refinement
		
		assertTrue(refinement.groups.empty)
		assertEquals(1, refinement.attributes.size)
		
		val attribute = refinement.attributes.head
		
		assertNull(attribute.slot)
		assertConceptReference(attribute.name, "363698007", "Finding site")
		
		assertTrue(attribute.value instanceof ConceptReference)
		
		val value = attribute.value as ConceptReference
		
		assertNull(value.id)
		assertNull(value.term)
		
		assertTrue(value.slot instanceof ExpressionReplacementSlot)
		
		val expressionReplacementSlot = value.slot as ExpressionReplacementSlot
		
		assertNull(expressionReplacementSlot.name)
		assertNull(expressionReplacementSlot.constraint)
		
	}

	@Test
	def void test_7_1_1_Simple_FocusConcept_1() {
		
		val expression = '''
			[[+]]: 272741003 |Laterality| =  24028007 |Right|
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		
		assertNull(focusConcept.concept.id)
		assertNull(focusConcept.concept.term)
		
		assertTrue(focusConcept.concept.slot instanceof ExpressionReplacementSlot)
		
		val expressionReplacementSlot = focusConcept.concept.slot as ExpressionReplacementSlot
		assertNull(expressionReplacementSlot.name)
		assertNull(expressionReplacementSlot.constraint)
		
		val refinement = expression.expression.refinement
		
		assertTrue(refinement.groups.empty)
		assertEquals(1, refinement.attributes.size)

		assertSimpleAttribute(refinement.attributes.head, "272741003", "Laterality", "24028007", "Right")
		
	}

	@Test
	def void test_7_1_2_Typed_ConceptReplacement_1() {
		
		val expression = '''
			404684003 |Clinical finding| :  255234002 |After|  = [[+id]]
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "404684003", "Clinical finding")
		
		val refinement = expression.expression.refinement
		
		assertTrue(refinement.groups.empty)
		assertEquals(1, refinement.attributes.size)
		
		val attribute = refinement.attributes.head
		
		assertNull(attribute.slot)
		assertConceptReference(attribute.name, "255234002", "After")
		
		assertTrue(attribute.value instanceof ConceptReference)
		
		val value = attribute.value as ConceptReference
		
		assertNull(value.id)
		assertNull(value.term)
		
		assertTrue(value.slot instanceof ConceptIdReplacementSlot)
		
		val conceptReplacementSlot = value.slot as ConceptIdReplacementSlot
		
		assertNull(conceptReplacementSlot.name)
		assertNull(conceptReplacementSlot.constraint)
		
	}

	@Test
	def void test_7_1_2_Typed_ConcreteValueReplacement_1() {
		
		val expression = '''
			322236009 |Paracetamol 500mg tablet|  :   209999999104 |Has trade name|   = [[+str]]
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "322236009", "Paracetamol 500mg tablet")
		
		val refinement = expression.expression.refinement
		
		assertTrue(refinement.groups.empty)
		assertEquals(1, refinement.attributes.size)
		
		val attribute = refinement.attributes.head
		
		assertNull(attribute.slot)
		assertConceptReference(attribute.name, "209999999104", "Has trade name")
		
		assertTrue(attribute.value instanceof StringReplacementSlot)
		
		val stringReplacementSlot = attribute.value as StringReplacementSlot
		
		assertNull(stringReplacementSlot.name)
		assertTrue(stringReplacementSlot.values.empty)
		
	}

	@Test
	def void test_7_1_2_Typed_ConcreteValueReplacement_2() {
		
		val expression = '''
			323510009 |Amoxycillin 500mg capsule| : 
			   { 749999999108 |Has pack size magnitude|  = [[+int]], 
			     759999999106 |Has pack size units|  =  428641000 |Capsule| }
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "323510009", "Amoxycillin 500mg capsule")
		
		val refinement = expression.expression.refinement
		
		assertEquals(1, refinement.groups.size)
		assertTrue(refinement.attributes.empty)
		
		val group = refinement.groups.head
		
		assertNull(group.slot)
		
		val attribute1 = group.attributes.head
		
		assertNull(attribute1.slot)
		assertConceptReference(attribute1.name, "749999999108", "Has pack size magnitude")
		
		assertTrue(attribute1.value instanceof IntegerReplacementSlot)
		
		val integerReplacementSlot = attribute1.value as IntegerReplacementSlot
		
		assertNull(integerReplacementSlot.name)
		assertTrue(integerReplacementSlot.values.empty)
		
		val attribute2 = group.attributes.last
		assertNull(attribute2.slot)
		assertSimpleAttribute(attribute2, "759999999106", "Has pack size units", "428641000", "Capsule")		
		
	}

	@Test
	def void test_7_1_2_Typed_ConcreteValueReplacement_3() {
		
		val expression = '''
			326645001 |Chlorhexidine gluconate 0.02% irrigation solution| : 
			  { 749999999108 |Has pack size magnitude|  = [[+dec]],
			    759999999106 |Has pack size units|  =  258770004 |Liter| }
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "326645001", "Chlorhexidine gluconate 0.02% irrigation solution")
		
		val refinement = expression.expression.refinement
		
		assertEquals(1, refinement.groups.size)
		assertTrue(refinement.attributes.empty)
		
		val group = refinement.groups.head
		
		assertNull(group.slot)
		
		val attribute1 = group.attributes.head
		
		assertNull(attribute1.slot)
		assertConceptReference(attribute1.name, "749999999108", "Has pack size magnitude")
		
		assertTrue(attribute1.value instanceof DecimalReplacementSlot)
		
		val decimalReplacementSlot = attribute1.value as DecimalReplacementSlot
		
		assertNull(decimalReplacementSlot.name)
		assertTrue(decimalReplacementSlot.values.empty)
		
		val attribute2 = group.attributes.last
		assertNull(attribute2.slot)
		assertSimpleAttribute(attribute2, "759999999106", "Has pack size units", "258770004", "Liter")		
		
	}

	@Test
	def void test_7_1_2_Typed_ExpressionReplacement_1() {
		
		val expression = '''
			404684003 |Clinical finding| :  255234002 |After|  = [[+scg]]
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "404684003", "Clinical finding")
		
		val refinement = expression.expression.refinement
		
		assertTrue(refinement.groups.empty)
		assertEquals(1, refinement.attributes.size)
		
		val attribute = refinement.attributes.head
		
		assertNull(attribute.slot)
		assertConceptReference(attribute.name, "255234002", "After")
		
		assertTrue(attribute.value instanceof ConceptReference)
		
		val value = attribute.value as ConceptReference
		
		assertNull(value.id)
		assertNull(value.term)
		
		assertTrue(value.slot instanceof ExpressionReplacementSlot)
		
		val expressionReplacementSlot = value.slot as ExpressionReplacementSlot
		
		assertNull(expressionReplacementSlot.name)
		assertNull(expressionReplacementSlot.constraint)
		
	}

	@Test
	def void test_7_1_2_Typed_TokenReplacement_1() {
		
		val expression = '''
			[[+tok]]  73211009 |Diabetes mellitus|  :   363698007 |Finding site|   =   113331007 |Endocrine system|
		'''.assertNoErrors
		
		assertNotNull(expression.slot)
		assertNull(expression.slot.name)
		assertTrue(expression.slot.tokens.empty)
		
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		assertConceptReference(focusConcept.concept, "73211009", "Diabetes mellitus")
		
		val refinement = expression.expression.refinement
		
		assertTrue(refinement.groups.empty)
		assertEquals(1, refinement.attributes.size)
		
		val attribute = refinement.attributes.head
		
		assertNull(attribute.slot)
		assertNull(attribute.name.slot)
		assertNull((attribute.value as ConceptReference).slot)
		
		assertSimpleAttribute(refinement.attributes.head, "363698007", "Finding site", "113331007", "Endocrine system")
		
	}

	@Test
	def void test_7_1_3_Constrained_ExpressionConstraints_1() {
		
		val expression = '''
			71388002 |Procedure| :
			  {
			  	260686004 |Method|  =  312251004 |Computed tomography imaging action| ,
			  	405813007 |Procedure site - Direct| = [[+id (<<  442083009 |Anatomical or acquired body structure| )]]
			  }
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "71388002", "Procedure")
		
		val refinement = expression.expression.refinement
		
		assertEquals(1, refinement.groups.size)
		assertTrue(refinement.attributes.empty)
		
		val group = refinement.groups.head
		
		assertNull(group.slot)
		
		val attribute1 = group.attributes.head
		
		assertNull(attribute1.slot)
		assertNull(attribute1.name.slot)
		assertNull((attribute1.value as ConceptReference).slot)
		assertSimpleAttribute(attribute1, "260686004", "Method", "312251004", "Computed tomography imaging action")		
		
		val attribute2 = group.attributes.last
		
		assertNull(attribute2.slot)
		assertNull(attribute2.name.slot)
		
		assertConceptReference(attribute2.name, "405813007", "Procedure site - Direct")
		
		assertTrue(attribute2.value instanceof ConceptReference)
		
		val conceptReference = attribute2.value as ConceptReference
		
		assertNull(conceptReference.id)
		assertNull(conceptReference.term)
		
		assertTrue(conceptReference.slot instanceof ConceptIdReplacementSlot)
		
		val slot = conceptReference.slot as ConceptIdReplacementSlot
		
		assertNull(slot.name)
		
		assertDescendantOrSelfOfExpression(slot.constraint, "442083009", "Anatomical or acquired body structure")
		
	}

	@Test
	def void test_7_1_3_Constrained_ExpressionConstraints_2() {
		
		val expression = '''
			71388002 |Procedure| :
			  { 260686004 |Method|  =  312251004 |Computed tomography imaging action| , 
			      405813007 |Procedure site - Direct|  = 
			        [[+scg (<<  442083009 |Anatomical or acquired body structure| )]]}
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "71388002", "Procedure")
		
		val refinement = expression.expression.refinement
		
		assertEquals(1, refinement.groups.size)
		assertTrue(refinement.attributes.empty)
		
		val group = refinement.groups.head
		
		assertNull(group.slot)
		
		val attribute1 = group.attributes.head
		
		assertNull(attribute1.slot)
		assertNull(attribute1.name.slot)
		assertNull((attribute1.value as ConceptReference).slot)
		assertSimpleAttribute(attribute1, "260686004", "Method", "312251004", "Computed tomography imaging action")		
		
		val attribute2 = group.attributes.last
		
		assertNull(attribute2.slot)
		assertNull(attribute2.name.slot)
		
		assertConceptReference(attribute2.name, "405813007", "Procedure site - Direct")
		
		assertTrue(attribute2.value instanceof ConceptReference)
		
		val conceptReference = attribute2.value as ConceptReference
		
		assertNull(conceptReference.id)
		assertNull(conceptReference.term)
		
		assertTrue(conceptReference.slot instanceof ExpressionReplacementSlot)
		
		val slot = conceptReference.slot as ExpressionReplacementSlot
		
		assertNull(slot.name)
		
		assertDescendantOrSelfOfExpression(slot.constraint, "442083009", "Anatomical or acquired body structure")
		
	}

	@Test
	def void test_7_1_3_Constrained_RangeConstraints_1() {
		
		val expression = '''
			323510009 |Amoxycillin 500mg capsule|  : 
			   {  749999999108 |Has pack size magnitude|   = [[+int (#20..#30)]], 
			      759999999106 |Has pack size units|   =   428641000 |Capsule|  }
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "323510009", "Amoxycillin 500mg capsule")
		
		val refinement = expression.expression.refinement
		
		assertEquals(1, refinement.groups.size)
		assertTrue(refinement.attributes.empty)
		
		val group = refinement.groups.head
		
		assertNull(group.slot)
		
		val attribute1 = group.attributes.head
		
		assertNull(attribute1.slot)
		assertConceptReference(attribute1.name, "749999999108", "Has pack size magnitude")
		
		assertTrue(attribute1.value instanceof IntegerReplacementSlot)
		
		val integerReplacementSlot = attribute1.value as IntegerReplacementSlot
		
		assertNull(integerReplacementSlot.name)
		assertEquals(1, integerReplacementSlot.values.size)
		
		assertTrue(integerReplacementSlot.values.head instanceof SlotIntegerRange)
		val range = integerReplacementSlot.values.head as SlotIntegerRange
		
		assertEquals(20, range.minimum.value)
		assertFalse(range.minimum.isExclusive)
		assertEquals(30, range.maximum.value)
		assertFalse(range.maximum.isExclusive)
		
		val attribute2 = group.attributes.last
		assertNull(attribute2.slot)
		assertSimpleAttribute(attribute2, "759999999106", "Has pack size units", "428641000", "Capsule")
		
	}

	@Test
	def void test_7_1_3_Constrained_RangeConstraints_2() {
		
		val expression = '''
			323510009 |Amoxycillin 500mg capsule|  : 
			    {  749999999108 |Has pack size magnitude|   = [[+int (>#20..<#30)]], 
			       759999999106 |Has pack size units|   =   428641000 |Capsule|  }
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "323510009", "Amoxycillin 500mg capsule")
		
		val refinement = expression.expression.refinement
		
		assertEquals(1, refinement.groups.size)
		assertTrue(refinement.attributes.empty)
		
		val group = refinement.groups.head
		
		assertNull(group.slot)
		
		val attribute1 = group.attributes.head
		
		assertNull(attribute1.slot)
		assertConceptReference(attribute1.name, "749999999108", "Has pack size magnitude")
		
		assertTrue(attribute1.value instanceof IntegerReplacementSlot)
		
		val integerReplacementSlot = attribute1.value as IntegerReplacementSlot
		
		assertNull(integerReplacementSlot.name)
		assertEquals(1, integerReplacementSlot.values.size)
		
		assertTrue(integerReplacementSlot.values.head instanceof SlotIntegerRange)
		val range = integerReplacementSlot.values.head as SlotIntegerRange
		
		assertEquals(20, range.minimum.value)
		assertTrue(range.minimum.isExclusive)
		assertEquals(30, range.maximum.value)
		assertTrue(range.maximum.isExclusive)
		
		val attribute2 = group.attributes.last
		assertNull(attribute2.slot)
		assertSimpleAttribute(attribute2, "759999999106", "Has pack size units", "428641000", "Capsule")
		
	}

	@Test
	def void test_7_1_3_Constrained_RangeConstraints_3() {
		
		val expression = '''
			323510009 |Amoxycillin 500mg capsule|  : 
			   {  749999999108 |Has pack size magnitude|   = [[+int (#10..#20 #30..#40)]], 
			      759999999106 |Has pack size units|   =   428641000 |Capsule|  }
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "323510009", "Amoxycillin 500mg capsule")
		
		val refinement = expression.expression.refinement
		
		assertEquals(1, refinement.groups.size)
		assertTrue(refinement.attributes.empty)
		
		val group = refinement.groups.head
		
		assertNull(group.slot)
		
		val attribute1 = group.attributes.head
		
		assertNull(attribute1.slot)
		assertConceptReference(attribute1.name, "749999999108", "Has pack size magnitude")
		
		assertTrue(attribute1.value instanceof IntegerReplacementSlot)
		
		val integerReplacementSlot = attribute1.value as IntegerReplacementSlot
		
		assertNull(integerReplacementSlot.name)
		assertEquals(2, integerReplacementSlot.values.size)
		
		assertTrue(integerReplacementSlot.values.head instanceof SlotIntegerRange)
		val range = integerReplacementSlot.values.head as SlotIntegerRange
		
		assertEquals(10, range.minimum.value)
		assertFalse(range.minimum.isExclusive)
		assertEquals(20, range.maximum.value)
		assertFalse(range.maximum.isExclusive)
		
		assertTrue(integerReplacementSlot.values.last instanceof SlotIntegerRange)
		val range2 = integerReplacementSlot.values.last as SlotIntegerRange
		
		assertEquals(30, range2.minimum.value)
		assertFalse(range2.minimum.isExclusive)
		assertEquals(40, range2.maximum.value)
		assertFalse(range2.maximum.isExclusive)
		
		val attribute2 = group.attributes.last
		assertNull(attribute2.slot)
		assertSimpleAttribute(attribute2, "759999999106", "Has pack size units", "428641000", "Capsule")
		
	}

	@Test
	def void test_7_1_3_Constrained_RangeConstraints_4() {
		
		val expression = '''
			323510009 |Amoxycillin 500mg capsule|  : 
			    {  749999999108 |Has pack size magnitude|   = [[+int (#20..)]], 
			       759999999106 |Has pack size units|   =   428641000 |Capsule|  }
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "323510009", "Amoxycillin 500mg capsule")
		
		val refinement = expression.expression.refinement
		
		assertEquals(1, refinement.groups.size)
		assertTrue(refinement.attributes.empty)
		
		val group = refinement.groups.head
		
		assertNull(group.slot)
		
		val attribute1 = group.attributes.head
		
		assertNull(attribute1.slot)
		assertConceptReference(attribute1.name, "749999999108", "Has pack size magnitude")
		
		assertTrue(attribute1.value instanceof IntegerReplacementSlot)
		
		val integerReplacementSlot = attribute1.value as IntegerReplacementSlot
		
		assertNull(integerReplacementSlot.name)
		assertEquals(1, integerReplacementSlot.values.size)
		
		assertTrue(integerReplacementSlot.values.head instanceof SlotIntegerRange)
		val range = integerReplacementSlot.values.head as SlotIntegerRange
		
		assertEquals(20, range.minimum.value)
		assertFalse(range.minimum.isExclusive)
		
		assertNull(range.maximum)
		
		val attribute2 = group.attributes.last
		assertNull(attribute2.slot)
		assertSimpleAttribute(attribute2, "759999999106", "Has pack size units", "428641000", "Capsule")
		
	}

	@Test
	def void test_7_1_3_Constrained_RangeConstraints_5() {
		
		val expression = '''
			323510009 |Amoxycillin 500mg capsule|  : 
			    {  749999999108 |Has pack size magnitude|   = [[+int (..#20)]], 
			       759999999106 |Has pack size units|   =   428641000 |Capsule|  }
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "323510009", "Amoxycillin 500mg capsule")
		
		val refinement = expression.expression.refinement
		
		assertEquals(1, refinement.groups.size)
		assertTrue(refinement.attributes.empty)
		
		val group = refinement.groups.head
		
		assertNull(group.slot)
		
		val attribute1 = group.attributes.head
		
		assertNull(attribute1.slot)
		assertConceptReference(attribute1.name, "749999999108", "Has pack size magnitude")
		
		assertTrue(attribute1.value instanceof IntegerReplacementSlot)
		
		val integerReplacementSlot = attribute1.value as IntegerReplacementSlot
		
		assertNull(integerReplacementSlot.name)
		assertEquals(1, integerReplacementSlot.values.size)
		
		assertTrue(integerReplacementSlot.values.head instanceof SlotIntegerRange)
		val range = integerReplacementSlot.values.head as SlotIntegerRange
		
		assertNull(range.minimum)
		
		assertEquals(20, range.maximum.value)
		assertFalse(range.maximum.isExclusive)
		
		val attribute2 = group.attributes.last
		assertNull(attribute2.slot)
		assertSimpleAttribute(attribute2, "759999999106", "Has pack size units", "428641000", "Capsule")
		
	}

	@Test
	def void test_7_1_3_Constrained_ValueListConstraints_1() {
		
		val expression = '''
			[[+tok (<<< ===)]]  281647001 |Adverse reaction (disorder)| :
			    246075003 |Causative agent (attribute)|  = [[+id]]
		'''.assertNoErrors
		
		assertNotNull(expression.slot)
		assertNull(expression.slot.name)
		
		assertEquals(2, expression.slot.tokens.size)
		assertEquals("<<<", expression.slot.tokens.head)
		assertEquals("===", expression.slot.tokens.last)
		
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		assertConceptReference(focusConcept.concept, "281647001", "Adverse reaction (disorder)")
		
		val refinement = expression.expression.refinement
		
		assertTrue(refinement.groups.empty)
		assertEquals(1, refinement.attributes.size)
		
		val attribute = refinement.attributes.head
		
		assertNull(attribute.slot)
		assertNull(attribute.name.slot)
		assertConceptReference(attribute.name, "246075003", "Causative agent (attribute)")
		
		assertTrue(attribute.value instanceof ConceptReference)
		val value = attribute.value as ConceptReference
		
		assertNull(value.id)
		assertNull(value.term)
		
		assertTrue(value.slot instanceof ConceptIdReplacementSlot)
		
		val conceptReplacementSlot = value.slot as ConceptIdReplacementSlot
		
		assertNull(conceptReplacementSlot.name)
		assertNull(conceptReplacementSlot.constraint)
		
	}

	@Test
	def void test_7_1_3_Constrained_ValueListConstraints_2() {
		
		val expression = '''
			322236009 |Paracetamol 500mg tablet|  :  209999999104 |Has trade name|  = [[+str ("PANADOL" "TYLENOL" "HERRON")]]
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "322236009", "Paracetamol 500mg tablet")
		
		val refinement = expression.expression.refinement
		
		assertTrue(refinement.groups.empty)
		assertEquals(1, refinement.attributes.size)
		
		val attribute = refinement.attributes.head
		
		assertNull(attribute.slot)
		assertConceptReference(attribute.name, "209999999104", "Has trade name")
		
		assertTrue(attribute.value instanceof StringReplacementSlot)
		
		val stringReplacementSlot = attribute.value as StringReplacementSlot
		
		assertNull(stringReplacementSlot.name)
		assertEquals(3, stringReplacementSlot.values.size)
		
		assertEquals("PANADOL", stringReplacementSlot.values.get(0))
		assertEquals("TYLENOL", stringReplacementSlot.values.get(1))
		assertEquals("HERRON", stringReplacementSlot.values.get(2))
		
	}

	@Test
	def void test_7_1_3_Constrained_ValueListConstraints_3() {
		
		val expression = '''
			323510009 |Amoxycillin 500mg capsule|  : 
			     {  749999999108 |Has pack size magnitude|   = [[+int (#10 #20 #30)]], 
			        759999999106 |Has pack size units|   =   428641000 |Capsule|  }
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "323510009", "Amoxycillin 500mg capsule")
		
		val refinement = expression.expression.refinement
		
		assertEquals(1, refinement.groups.size)
		assertTrue(refinement.attributes.empty)
		
		val group = refinement.groups.head
		
		assertNull(group.slot)
		
		val attribute1 = group.attributes.head
		
		assertNull(attribute1.slot)
		assertConceptReference(attribute1.name, "749999999108", "Has pack size magnitude")
		
		assertTrue(attribute1.value instanceof IntegerReplacementSlot)
		
		val integerReplacementSlot = attribute1.value as IntegerReplacementSlot
		
		assertNull(integerReplacementSlot.name)
		assertEquals(3, integerReplacementSlot.values.size)
		
		assertTrue(integerReplacementSlot.values.get(0) instanceof SlotIntegerValue)
		val first = integerReplacementSlot.values.get(0) as SlotIntegerValue
		assertEquals(10, first.value)
		
		assertTrue(integerReplacementSlot.values.get(1) instanceof SlotIntegerValue)
		val second = integerReplacementSlot.values.get(1) as SlotIntegerValue
		assertEquals(20, second.value)
		
		assertTrue(integerReplacementSlot.values.get(2) instanceof SlotIntegerValue)
		val third = integerReplacementSlot.values.get(2) as SlotIntegerValue
		assertEquals(30, third.value)
		
		val attribute2 = group.attributes.last
		assertNull(attribute2.slot)
		assertSimpleAttribute(attribute2, "759999999106", "Has pack size units", "428641000", "Capsule")
		
	}

	@Test
	def void test_7_1_4_Named_RepeatedSlotNames_1() {
		
		val expression = '''
			404684003 |Finding| : {
				363698007 |Finding site| = [[+ @site]],
				363714003 |Interprets| = ( 363787002 |Observable entity| :	704319004 |Inheres in| = [[+ @site]] )
			}
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "404684003", "Finding")
		
		val refinement = expression.expression.refinement
		
		assertEquals(1, refinement.groups.size)
		assertTrue(refinement.attributes.empty)
		
		val group = refinement.groups.head
		
		assertNull(group.slot)
		assertEquals(2, group.attributes.size)
		
		val attribute1 = group.attributes.head
		
		assertNull(attribute1.slot)
		assertConceptReference(attribute1.name, "363698007", "Finding site")
		
		assertTrue(attribute1.value instanceof ConceptReference)
		
		val value1 = attribute1.value as ConceptReference
		
		assertNull(value1.id)
		assertNull(value1.term)
		
		assertTrue(value1.slot instanceof ExpressionReplacementSlot)
		
		val expressionSlot = value1.slot as ExpressionReplacementSlot
		
		assertEquals("@site", expressionSlot.name)
		assertNull(expressionSlot.constraint)
		
		
		val attribute2 = group.attributes.last
		
		assertNull(attribute2.slot)
		assertConceptReference(attribute2.name, "363714003", "Interprets")
		
		assertTrue(attribute2.value instanceof SubExpression)
		
		val subExpression = attribute2.value as SubExpression
		
		assertEquals(1, subExpression.focusConcepts.size)
		
		assertNull(subExpression.focusConcepts.head.slot)
		assertConceptReference(subExpression.focusConcepts.head.concept, "363787002", "Observable entity")
		
		assertEquals(1, subExpression.refinement.attributes.size)
		
		val subAttribute = subExpression.refinement.attributes.head
		
		assertNull(subAttribute.slot)
		assertConceptReference(subAttribute.name, "704319004", "Inheres in")
		
		assertTrue(subAttribute.value instanceof ConceptReference)
		
		val value2 = subAttribute.value as ConceptReference
		
		assertNull(value2.id)
		assertNull(value2.term)
		
		assertTrue(value2.slot instanceof ExpressionReplacementSlot)
		
		val expressionSlot2 = value2.slot as ExpressionReplacementSlot
		
		assertEquals("@site", expressionSlot2.name)
		assertNull(expressionSlot2.constraint)
		
	}

	@Test
	def void test_7_1_4_Named_SlotNames_1() {
		
		val expression = '''
			243796009 |Situation with explicit context| :
			  { 246090004 |Associated finding|  = [[+id (< 404684003 |Clinical finding| ) @finding]],
			    40873100 |Temporal context|  =  410511007 |Current or past (actual)| ,
			    408729009 |Finding context|  =  410515003 |Known present| ,
			    408732007 |Subject relationship context|  =  444148008 |Person in family of subject| }
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertFalse(expression.primitive)
		
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		assertNull(focusConcept.concept.slot)
		
		assertConceptReference(focusConcept.concept, "243796009", "Situation with explicit context")
		
		val refinement = expression.expression.refinement
		
		assertEquals(1, refinement.groups.size)
		assertTrue(refinement.attributes.empty)
		
		val group = refinement.groups.head
		
		assertNull(group.slot)
		assertEquals(4, group.attributes.size)
		
		val attribute1 = group.attributes.get(0)
		
		assertNull(attribute1.slot)
		assertNull(attribute1.name.slot)
		
		assertConceptReference(attribute1.name, "246090004", "Associated finding")
		
		assertTrue(attribute1.value instanceof ConceptReference)
		
		val conceptReference = attribute1.value as ConceptReference
		
		assertNull(conceptReference.id)
		assertNull(conceptReference.term)
		
		assertTrue(conceptReference.slot instanceof ConceptIdReplacementSlot)
		
		val slot = conceptReference.slot as ConceptIdReplacementSlot
		
		assertEquals("@finding", slot.name)
		
		assertDescendantOfExpression(slot.constraint, "404684003", "Clinical finding")
		
		assertSimpleAttribute(group.attributes.get(1), "40873100", "Temporal context", "410511007", "Current or past (actual)")
		assertSimpleAttribute(group.attributes.get(2), "408729009", "Finding context", "410515003", "Known present")
		assertSimpleAttribute(group.attributes.get(3), "408732007", "Subject relationship context", "444148008", "Person in family of subject")
		
	}

	@Test
	def void test_7_1_5_Information_Cardinality_1() {
		
		val expression = '''
			[[1..3]] [[+id (< 404684003 |Clinical finding| : [0..0] 363698007 |Finding site| = * ) @finding]]: 
			  [[1..1]] 363698007 |Finding site| = [[+id (<< 442083009 |Anatomical or acquired body structure| ) @site ]]
		'''.assertNoErrors
		
		assertNull(expression.slot)
		assertEquals(1, expression.expression.focusConcepts.size)
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNotNull(focusConcept.slot)
		assertNull(focusConcept.slot.name)
		assertEquals(1, focusConcept.slot.cardinality.min)
		assertEquals(3, focusConcept.slot.cardinality.max)
		
		assertNull(focusConcept.concept.id)
		assertNull(focusConcept.concept.term)
		assertTrue(focusConcept.concept.slot instanceof ConceptIdReplacementSlot)
		
		val idSlot = focusConcept.concept.slot as ConceptIdReplacementSlot
		
		assertEquals("@finding", idSlot.name)
		
		assertTrue(idSlot.constraint instanceof RefinedExpressionConstraint)
		val refinedConstraint = idSlot.constraint as RefinedExpressionConstraint
		
		assertDescendantOfExpression(refinedConstraint.constraint, "404684003", "Clinical finding")
		
		assertTrue(refinedConstraint.refinement instanceof AttributeConstraint)
		val attributeConstraint = refinedConstraint.refinement as AttributeConstraint
		
		assertEquals(0, attributeConstraint.cardinality.min)
		assertEquals(0, attributeConstraint.cardinality.max)
		
		assertTrue(attributeConstraint.attribute instanceof EclConceptReference)
		assertEclConceptReference(attributeConstraint.attribute as EclConceptReference, "363698007", "Finding site")
		assertTrue(attributeConstraint.comparison instanceof AttributeValueEquals) 
		assertTrue((attributeConstraint.comparison as AttributeValueEquals).constraint instanceof Any)
		
		assertEquals(1, expression.expression.refinement.attributes.size)
		assertTrue(expression.expression.refinement.groups.empty)

		val attribute = expression.expression.refinement.attributes.head
		
		assertNotNull(attribute.slot)
		assertNull(attribute.slot.name)
		assertEquals(1, attribute.slot.cardinality.min)
		assertEquals(1, attribute.slot.cardinality.max)
		
		assertNull(attribute.name.slot)
		assertConceptReference(attribute.name, "363698007", "Finding site")
		
		assertTrue(attribute.value instanceof ConceptReference)
		
		val conceptReference = attribute.value as ConceptReference
		
		assertEquals("@site", conceptReference.slot.name)
		assertTrue(conceptReference.slot instanceof ConceptIdReplacementSlot)
		
		assertDescendantOrSelfOfExpression((conceptReference.slot as ConceptIdReplacementSlot).constraint, "442083009", "Anatomical or acquired body structure")
		
	}

	@Test
	def void test_7_1_5_Information_DefaultCardinality_1() {
		'''
			[[+id (<< 71388002 |Procedure| )]]: 
			     {  260686004 |Method|  = [[+id (<<  129264002 |Action (qualifier value)| )]],
			        405813007 |Procedure site - Direct|  = [[+id (<<  442083009 |Anatomical or acquired body structure (body structure)| )]] }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_5_Information_DefaultCardinality_2() {
		
		val expression = '''
			[[1..*]] [[+id (<< 71388002 |Procedure| )]]: 
			   [[1..*]] {
			   		[[1..*]]  260686004 |Method|  = [[+id (<<  129264002 |Action (qualifier value)| )]],
			   		[[1..*]]  405813007 |Procedure site - Direct|  = [[+id (<<  442083009 |Anatomical or acquired body structure (body structure)| )]]
			   	}
		'''.assertNoErrors
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot.name)
		assertEquals(1, focusConcept.slot.cardinality.min)
		assertEquals(Ecl.MAX_CARDINALITY, focusConcept.slot.cardinality.max)
		
		assertTrue(focusConcept.concept.slot instanceof ConceptIdReplacementSlot)
		
		val idSlot = focusConcept.concept.slot as ConceptIdReplacementSlot
		
		assertNull(idSlot.name)
		assertDescendantOrSelfOfExpression(idSlot.constraint, "71388002", "Procedure")
		
		assertEquals(1, expression.expression.refinement.groups.size)
		
		val group =  expression.expression.refinement.groups.head
		
		assertNull(group.slot.name)
		assertEquals(1, group.slot.cardinality.min)
		assertEquals(Ecl.MAX_CARDINALITY, group.slot.cardinality.max)
		
		assertEquals(2, group.attributes.size)
		
		assertNull(group.attributes.head.slot.name)
		assertEquals(1, group.attributes.head.slot.cardinality.min)
		assertEquals(Ecl.MAX_CARDINALITY, group.attributes.head.slot.cardinality.max)
		
		assertNull(group.attributes.last.slot.name)
		assertEquals(1, group.attributes.last.slot.cardinality.min)
		assertEquals(Ecl.MAX_CARDINALITY, group.attributes.last.slot.cardinality.max)
		
	}

	@Test
	def void test_7_1_5_Information_InformationSlotName_1() {
		
		val expression = '''
			71388002 |Procedure| :
				[[1..1 @mpGroup]] {
					260686004 |Method|  =  312251004 |Computed tomography imaging action| ,
					405813007 |Procedure site - Direct|  = [[+id (<< 442083009 |Anatomical or acquired body structure| ) @site]]
				}
		'''.assertNoErrors
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot)
		
		assertConceptReference(focusConcept.concept, "71388002", "Procedure")
		
		assertEquals(1, expression.expression.refinement.groups.size)
		
		val group =  expression.expression.refinement.groups.head
		
		assertEquals("@mpGroup", group.slot.name)
		assertEquals(1, group.slot.cardinality.min)
		assertEquals(1, group.slot.cardinality.max)
		
		assertEquals(2, group.attributes.size)
		
		assertNull(group.attributes.head.slot)
		
		assertSimpleAttribute(group.attributes.head, "260686004", "Method", "312251004", "Computed tomography imaging action")
		
		assertNull(group.attributes.last.slot)
		
		assertConceptReference(group.attributes.last.name, "405813007", "Procedure site - Direct")
		
		assertTrue(group.attributes.last.value instanceof ConceptReference)
		
		val reference = group.attributes.last.value as ConceptReference
		
		assertTrue(reference.slot instanceof ConceptIdReplacementSlot)
		
		val idSlot = reference.slot as ConceptIdReplacementSlot
		
		assertEquals("@site", idSlot.name)
		assertDescendantOrSelfOfExpression(idSlot.constraint, "442083009", "Anatomical or acquired body structure")
		 
	}

	@Test
	def void test_7_1_6_Advanced_MultipleCardinalityConstraints_1() {
		
		val expression = '''
			[[1..1]] [[+ (< 71388002 |Procedure| ) @Procedure]] : 
			   [[ 1..2 @SMgroup]] {
			   		[[1..1]]  405813007 |Procedure site - direct|  = [[+ (< 91723000 |Anatomical structure| ) @BodySite]],
			   		[[1..1]]  260686004 |Method|  = [[+ (< 129264002 |Action (qualifier value)| ) @Method]]
			   }
		'''.assertNoErrors
		
		val focusConcept = expression.expression.focusConcepts.head
		
		assertNull(focusConcept.slot.name)
		assertEquals(1, focusConcept.slot.cardinality.min)
		assertEquals(1, focusConcept.slot.cardinality.max)
		
		assertTrue(focusConcept.concept.slot instanceof ExpressionReplacementSlot)
		
		val idSlot = focusConcept.concept.slot as ExpressionReplacementSlot
		
		assertEquals("@Procedure", idSlot.name)
		assertDescendantOfExpression(idSlot.constraint, "71388002", "Procedure")
		
		assertEquals(1, expression.expression.refinement.groups.size)
		
		val group =  expression.expression.refinement.groups.head
		
		assertEquals("@SMgroup", group.slot.name)
		assertEquals(1, group.slot.cardinality.min)
		assertEquals(2, group.slot.cardinality.max)
		
		assertEquals(2, group.attributes.size)
		
		assertNull(group.attributes.head.slot.name)
		assertEquals(1, group.attributes.head.slot.cardinality.min)
		assertEquals(1, group.attributes.head.slot.cardinality.max)
		
		assertTrue(group.attributes.head.value instanceof ConceptReference)
		val firstReference = group.attributes.head.value as ConceptReference
		assertTrue(firstReference.slot instanceof ExpressionReplacementSlot)
		val firstExpression = firstReference.slot as ExpressionReplacementSlot
		
		assertEquals("@BodySite", firstExpression.name)
		assertDescendantOfExpression(firstExpression.constraint, "91723000", "Anatomical structure") 
		
		assertNull(group.attributes.last.slot.name)
		assertEquals(1, group.attributes.last.slot.cardinality.min)
		assertEquals(1, group.attributes.last.slot.cardinality.max)
		
		assertTrue(group.attributes.last.value instanceof ConceptReference)
		val secondReference = group.attributes.last.value as ConceptReference
		assertTrue(secondReference.slot instanceof ExpressionReplacementSlot)
		val secondExpression = secondReference.slot as ExpressionReplacementSlot
		
		assertEquals("@Method", secondExpression.name)
		assertDescendantOfExpression(secondExpression.constraint, "129264002", "Action (qualifier value)") 
		
	}

	@Test
	def void test_7_1_6_Advanced_MultipleCardinalityConstraints_2() {
		'''
			[[1..1]] [[+id (<< 413350009 |Finding with explicit context|) @Condition]]:
			   [[ 1..2 @AFgroup ]] {
			   		[[1..1]] 246090004 |Associated finding| = (
			   			[[+id (<< 404684003 |Clinical finding|) @Finding]]:
			                [[0..1 @SSgroup]] {
			                	[[0..1]] 246112005 |Severity| = [[+id (< 272141005 |Severities|) @Severity]],
			                	[[0..1]] 363698007 |Finding site| = [[+id (< 91723000 |Anatomical structure|) @Site]]
			                }
			            ),
			        [[1..1]] 408732007 |Subject relationship context| = [[+id (< 444148008 |Person in family of subject|) @Relationship]],
			        [[1..1]] 408731000 |Temporal context|  = [[+id (< 410510008 |Temporal context value|) @Time]],
			        [[1..1]] 408729009 |Finding context|  = [[+id (< 410514004 |Finding context value|) @Context]]
			   }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_6_Advanced_MultipleReplacementSlots_1() {
		'''
			[[+ (< 71388002 |Procedure| ) @Procedure]] :
			   	{
			   		405813007 |Procedure site - direct|  = [[+ (< 91723000 |Anatomical structure| ) @BodySite]],
			   		260686004 |Method|  = [[+ (< 129264002 |Action (qualifier value)| ) @Method]]
			   	}
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_6_Advanced_MultipleReplacementSlots_2() {
		'''
			243796009 |Situation with explicit context| :
			  {  246090004 |Associated finding|  = [[+id (<  404684003 |Clinical finding| ) @Finding]],
			     408731000 |Temporal context|  =  410511007 |Current or past (actual)| ,
			     408729009 |Finding context|  =  410515003 |Known present| ,
			     408732007 |Subject relationship context|   =  [[+id (<<  444148008 |Person in family of subject| ) @Relationship]] }
		'''.assertNoErrors
	}
	
	// IHTSDO templates
	
	@Test
	def void test_adverse_reaction_caused_by_substance_disorder_v1_outdated() {
		'''
			281647001 |Adverse reaction (disorder)|:
				[[~1..1]] {
					[[~1..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_allergic_reaction_caused_by_substance_disorder_v1_outdated() {
		'''
			281647001 |Adverse reaction (disorder)|:
				[[~1..1]] {
					[[~1..1]] 370135005 |Pathological process (attribute)| = 472964009 |Allergic process (qualifier value)|
				},
				[[~1..1]] {
					[[~1..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_allergic_reaction_caused_by_substance_disorder_v2() {
		'''
			281647001 |Adverse reaction (disorder)|:
				[[~1..1]] {
					[[~1..1]] 370135005 |Pathological process (attribute)| = 472964009 |Allergic process (qualifier value)|,
					[[~1..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_allergic_disease_caused_by_substance_disorder_v1_outdated() {
		'''
			64572001 |Disease (disorder)|:
				[[~1..1]] {
					[[~1..1]] 370135005 |Pathological process (attribute)| = [[+id(<<472964009 |Allergic process (qualifier value)|) @pathologicalProcess]]
				},
				[[~0..1]] {
					[[~1..1]] 42752001 |Due to (attribute)| = 419076005 |Allergic reaction (disorder)|
				},
				[[~0..1]] {
					[[~1..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]
				},
				[[~1..1]] {
					[[~1..1]] 116676008 |Associated morphology (attribute)| = 409774005 |Inflammatory morphology (morphologic abnormality)|,
					[[~1..1]] 363698007 |Finding site (attribute)| = [[+id(<<442083009 |Anatomical or acquired body structure (body structure)|) @findingSite]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_allergic_disease_caused_by_substance_disorder_v2() {
		'''
			64572001 |Disease (disorder)|:
				[[~1..1]] {
					[[~1..1]] 116676008 |Associated morphology (attribute)| = [[+id(<< 49755003 |Morphologically abnormal structure (morphologic abnormality)|) @morphology]],
					[[~1..1]] 370135005 |Pathological process (attribute)| = [[+id(<<472964009 |Allergic process (qualifier value)|) @pathologicalProcess]],
					[[~1..1]] 363698007 |Finding site (attribute)| = [[+id(<<442083009 |Anatomical or acquired body structure (body structure)|) @bodyStructure]],
					[[~0..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_allergy_to_substance_disorder_v1_outdated() {
		'''
			420134006 |Propensity to adverse reactions (disorder)|:	
				[[~1..1]] {
					[[~1..1]] 370135005 |Pathological process (attribute)| = 472964009 |Allergic process (qualifier value)|
				},
				[[~1..1]] {
					[[~1..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]],
					[[~1..1]] 255234002 |After (attribute)| = 609327009 |Allergic sensitization (disorder)|
				}
		'''.assertNoErrors
	}

	@Test
	def void test_allergy_to_substance_finding_v2() {
		'''
			420134006 |Propensity to adverse reactions (finding)|:
				[[~1..1]] {
					[[~1..1]] 719722006 |Has realization (attribute)| = 472964009 |Allergic process (qualifier value)|,
					[[~1..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_cataract_disorder() {
		'''
			64572001 |Disease (disorder)|:
				[[~1..2]] {
					[[~0..1]] 246454002 |Occurrence (attribute)| = [[ +id ( << 282032007 |Periods of life (qualifier value)|) @occurance]],
					[[~1..1]] 363698007 |Finding site (attribute)| = [[ +id( <<  78076003 |Structure of lens of eye (body structure)| ) @site]],
					[[~1..1]] 116676008 |Associated morphology (attribute)| = 128305008 |Abnormally opaque structure (morphologic abnormality)|,
					[[~0..1]] 370135005 |Pathological process (attribute)| = [[ +id ( << 308489006 |Pathological process (qualifier value)|) @proc]],
					[[~0..1]] 246075003 |Causative agent (attribute)| = [[ +id ( < 105590001 |Substance (substance)|) @causeAgent]]
				},
				{
					[[~0..1]] 255234002 |After (attribute)| = [[ +id ( << 404684003 |Clinical finding (finding)| OR << 71388002 |Procedure (procedure)|) @after]]
				},
				{
					[[~0..1]] 42752001 |Due to (attribute)| = [[ +id (<< 404684003 |Clinical finding (finding)| OR << 272379006 |Event (event)| OR << 71388002 |Procedure (procedure)|) @due]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_congenital_morphology_of_body_structure_disorder() {
		'''
			64572001 |Disease (disorder)|:
				{
					246454002 |Occurrence (attribute)| = 255399007 |Congenital (qualifier value)|,
					116676008 |Associated morphology (attribute)| = [[+id(<< 49755003 |Morphologically abnormal structure (morphologic abnormality)|) @morphology]],
					363698007 |Finding site (attribute)| = [[+id(<<442083009 |Anatomical or acquired body structure (body structure)|) @findingSite]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_ct_angiography_of_body_structure_procedure() {
		'''
			71388002 |Procedure (procedure)|:
				[[~1..1]] {
					260686004 |Method (attribute)| = 312251004 |Computed tomography imaging - action (qualifier value)|,
					405813007 |Procedure site - Direct (attribute)| = [[+id(<<59820001 |Blood vessel structure (body structure)|) @procSite]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_ct_arteriography_of_body_structure_procedure() {
		'''
			71388002 |Procedure (procedure)|:
				[[~1..1]] {
					260686004 |Method (attribute)| = 312251004 |Computed tomography imaging - action (qualifier value)|,
					405813007 |Procedure site - Direct (attribute)| = [[+id(<<11527006 |Arterial system structure (body structure)|) @procSite]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_ct_arthrography_of_body_structure_procedure() {
		'''
			71388002 |Procedure (procedure)|:
				[[~1..1]] {
					260686004 |Method (attribute)| = 312251004 |Computed tomography imaging - action (qualifier value)|,
					405813007 |Procedure site - Direct (attribute)| = [[+id(<<39352004 |Joint structure (body structure)|) @procSite]]
				}
			
		'''.assertNoErrors
	}

	@Test
	def void test_ct_guided_procedure_of_body_structure_procedure() {
		'''
			71388002 |Procedure|:
				[[~1..1]] {
					260686004 |Method| = 312251004 |Computed tomography imaging action|,
					[[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @procSite]],
					363703001 |Has intent| = 429892002 |Guidance intent|
				},
				{
					260686004 |Method| = [[+id (<< 129264002 |Action|) @action]],
					[[~1..1]] 405813007 |Procedure site - Direct| = [[+id @procSite]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_ct_of_body_structure_procedure() {
		'''
			71388002 |Procedure|:
				[[~1..1]] {
					260686004 |Method| = 312251004 |Computed tomography imaging action|,
					[[~1..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure|) @procSite]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_ct_of_body_structure_with_contrast_procedure() {
		'''
			71388002 |Procedure (procedure)|:
				[[~1..1]] {
					260686004 |Method (attribute)| = 312251004 |Computed tomography imaging - action (qualifier value)|,
					405813007 |Procedure site - Direct (attribute)| = [[+id(<<442083009 |Anatomical or acquired body structure (body structure)|) @procSite]],
					424361007 |Using substance (attribute)| = [[+id(<<385420005 |Contrast media (substance)|) @substance]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_ct_venography_of_body_structure_procedure() {
		'''
			71388002 |Procedure (procedure)|:
				[[~1..1]] {
					260686004 |Method (attribute)| = 312251004 |Computed tomography imaging - action (qualifier value)|,
					405813007 |Procedure site - Direct (attribute)| = [[+id( <<119553000 |Venous system structure (body structure)|) @procSite]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_family_history_of_findingdisorder_situation() {
		'''
			243796009 |Situation with explicit context (situation)|:
				{
					246090004 |Associated finding (attribute)| = [[+id(<404684003 |Clinical finding (finding)|) @finding]],
					408731000 |Temporal context (attribute)| = 410511007 |Current or past (actual) (qualifier value)|,
					408729009 |Finding context (attribute)| = 410515003 |Known present (qualifier value)|,
					408732007 |Subject relationship context (attribute)| = 444148008 |Person in family of subject (person)|
				}
		'''.assertNoErrors
	}

	@Test
	def void test_family_history_of_procedure_situation() {
		'''
			243796009 |Situation with explicit context (situation)|:
				{
					363589002 |Associated procedure (attribute)| = [[+id(<71388002 |Procedure (procedure)|) @procedure]],
					408731000 |Temporal context (attribute)| = 410511007 |Current or past (actual) (qualifier value)|,
					408730004 |Procedure context (attribute)| = 385658003 |Done (qualifier value)|,
					408732007 |Subject relationship context (attribute)| = 444148008 |Person in family of subject (person)|
				}
		'''.assertNoErrors
	}

	@Test
	def void test_foreign_body_of_body_structure_disorder() {
		'''
			64572001 |Disease (disorder)|:
				[[~1..1]] {
					[[~0..1]] 363698007 |Finding site (attribute)| = [[ +id( <  123037004 |Body structure (body structure)| ) @site]],
					[[~1..1]]  116676008 |Associated morphology (attribute)| = [[ +id( << 19227008 |Foreign body (morphologic abnormality)|) @morph]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_history_of_findingdisorder_situation() {
		'''
			243796009 |Situation with explicit context (situation)|:
				{
					246090004 |Associated finding (attribute)| = [[+id(<404684003 |Clinical finding (finding)|) @finding]],
					408731000 |Temporal context (attribute)| = 410513005 |In the past (qualifier value)|,
					408729009 |Finding context (attribute)| = 410515003 |Known present (qualifier value)|,
					408732007 |Subject relationship context (attribute)| = 410604004 |Subject of record (person)|
				}
		'''.assertNoErrors
	}

	@Test
	def void test_history_of_procedure_situation() {
		'''
			243796009 |Situation with explicit context (situation)|:
				{
					363589002 |Associated procedure (attribute)| = [[+id(<71388002 |Procedure (procedure)|) @procedure]],
					408731000 |Temporal context (attribute)| = [[+id(<<410513005 |In the past (qualifier value)|) @past]],
					408730004 |Procedure context (attribute)| = 385658003 |Done (qualifier value)|,
					408732007 |Subject relationship context (attribute)| = 410604004 |Subject of record (person)|
				}
		'''.assertNoErrors
	}

	@Test
	def void test_infection_caused_by_bacteria_disorder_v2() {
		'''
			64572001 |Disease (disorder)|:
				[[~0..1]] {
					[[~1..1]] 263502005 |Clinical course (attribute)| = [[+id(<288524001 |Courses (qualifier value)|) @course]]
				},
				[[~1..1]] {
					[[~1..1]] 370135005 |Pathological process (attribute)| = 441862004 |Infectious process (qualifier value)|,
					[[~0..1]] 363698007 |Finding site (attribute)| = [[+id(<<442083009 |Anatomical or acquired body structure (body structure)|) @bodyStructure]],
					[[~0..1]] 116676008 |Associated morphology (attribute)| = [[+id(<<49755003 |Morphologically abnormal structure (morphologic abnormality)|) @morphology]],
					[[~1..1]] 246075003 |Causative agent (attribute)| = [[+id(<<409822003 |Superkingdom Bacteria (organism)|) @bacteria]],
					[[~0..1]] 246454002 |Occurrence (attribute)| = [[+id(<282032007 |Periods of life (qualifier value)|) @periodsOfLife]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_infection_caused_by_fungus_disorder() {
		'''
			64572001 |Disease (disorder)|:
				[[~1..1]] {
					[[~0..1]] 246454002 |Occurrence (attribute)| = [[+id(<282032007 |Periods of life (qualifier value)|) @periodsOfLife]],
					[[~0..1]] 363698007 |Finding site (attribute)| = [[+id(<<442083009 |Anatomical or acquired body structure (body structure)|) @bodyStructure]],
					[[~0..1]] 116676008 |Associated morphology (attribute)| = [[+id(<<49755003 |Morphologically abnormal structure (morphologic abnormality)|) @morphology]],
					[[~1..1]] 370135005 |Pathological process (attribute)| = 441862004 |Infectious process (qualifier value)|,
					[[~1..1]] 246075003 |Causative agent (attribute)| = [[+id(<<414561005 |Kingdom Fungi (organism)|) @fungus]]
				},
				[[~0..1]] {
					[[~1..1]] 263502005 |Clinical course (attribute)| = [[+id(<288524001 |Courses (qualifier value)|) @course]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_infection_caused_by_virus() {
		'''
			64572001 |Disease (disorder)|:
				[[~0..1]] {
					[[~1..1]] 263502005 |Clinical course (attribute)| = [[+id(<288524001 |Courses (qualifier value)|) @course]]
				},
				[[~1..1]] {
					[[~0..1]] 246454002 |Occurrence (attribute)| = [[+id(<282032007 |Periods of life (qualifier value)|) @periodsOfLife]],
					[[~1..1]] 370135005 |Pathological process (attribute)| = 441862004 |Infectious process (qualifier value)|,
					[[~0..1]] 363698007 |Finding site (attribute)| = [[+id(<442083009 |Anatomical or acquired body structure (body structure)|) @bodyStructure]],
					[[~1..1]] 246075003 |Causative agent (attribute)| = [[+id(<<49872002 |Virus (organism)|) @virus]],
					[[~0..1]] 116676008 |Associated morphology (attribute)| = [[+id(<<49755003 |Morphologically abnormal structure (morphologic abnormality)|) @morphology]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_infection_of_body_structure_caused_by_bacteria_disorder_v1_outdated() {
		'''
			64572001 |Disease (disorder)|:
				{
					370135005 |Pathological process (attribute)| = 441862004 |Infectious process (qualifier value)|,
					363698007 |Finding site (attribute)| = [[+id(<<442083009 |Anatomical or acquired body structure (body structure)|) @findingSite]],
					246075003 |Causative agent (attribute)| = [[+id(<<409822003 |Superkingdom Bacteria (organism)|) @bacteria]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_mri_of_body_structure_procedure() {
		'''
			71388002 |Procedure (procedure)|:
				[[~1..1]] {
					260686004 |Method (attribute)| = 312250003 |Magnetic resonance imaging - action (qualifier value)|,
					405813007 |Procedure site - Direct (attribute)| = [[+id( <<442083009 |Anatomical or acquired body structure (body structure)|) @procSite]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_nonallergic_hypersensitivity_to_substance_disorder_v2() {
		'''
			420134006 |Propensity to adverse reactions (finding)|:
				[[~1..1]]	{
					[[~1..1]] 719722006 |Has realization (attribute)| = 609404002 |Non-allergic hypersensitivity process (qualifier value)|,
					[[~1..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_parasitic_disease_disorder() {
		'''
			64572001 |Disease (disorder)|:
				[[~1..1]] {
					[[~1..1]] 370135005 |Pathological process (attribute)| = 442614005 |Parasitic process (qualifier value)|,
					[[~0..1]] 246075003 |Causative agent (attribute)| = [[+id(<410607006 |Organism (organism)|) @organism]],
					[[~0..1]] 116676008 |Associated morphology (attribute)| = [[+id(<49755003 |Morphologically abnormal structure (morphologic abnormality)|) @associatedMorphology]],
					[[~0..1]] 363698007 |Finding site (attribute)| = [[+id(<442083009 |Anatomical or acquired body structure (body structure)|) @bodyStructure]],
					[[~0..1]] 246454002 |Occurrence (attribute)| = [[+id(<282032007 |Periods of life (qualifier value)|) @occurrence]]
				},
				{
					[[~0..1]] 263502005 |Clinical course (attribute)| = [[+id(<288524001 |Courses (qualifier value)|) @clinicalCourse]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_superficial_bite_wound_of_site_without_infection_disorder() {
		'''
			25358000 |Superficial injury without infection (disorder)|:	
				[[~1..1]] {
					[[~1..1]] 116676008 |Associated morphology (attribute)| = [[+id(<<37205004 |Superficial wound (morphologic abnormality)|) @associatedMorphology]],
					[[~0..1]] 363698007 |Finding site (attribute)| = [[+id(<91723000 |Anatomical structure (body structure)|) @site]]
				},
				{
					[[~1..1]] 42752001 |Due to (attribute)| = [[+id(<<782161000 |Bite (event)|) @biteEvent]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_ultrasonography_of_body_structure_procedure() {
		'''
			71388002 |Procedure (procedure)|:
				[[~1..1]] {
					260686004 |Method (attribute)| = 278292003 |Ultrasound imaging - action (qualifier value)|,
					405813007 |Procedure site - Direct (attribute)| = [[+id(<<442083009 |Anatomical or acquired body structure (body structure)|) @procSite]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_clinical_course_contact_dermatitis_of_body_structure_caused_by_substance_disorder_v2() {
		'''
			64572001 |Disease (disorder)|:
				[[~0..1]] {
					[[~1..1]] 263502005 |Clinical course (attribute)| = [[+id(<288524001 |Courses (qualifier value)|) @clinicalCourse]]
				},
				[[~1..*]] {
					[[~0..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]],
					[[~1..1]] 116676008 |Associated morphology (attribute)| = 409774005 |Inflammatory morphology (morphologic abnormality)|,
					[[~1..1]] 370135005 |Pathological process (attribute)| = 769258001 |Contact hypersensitivity process (qualifier value)|,
					[[~0..1]] 363698007 |Finding site (attribute)| = [[+id(<<442083009 |Anatomical or acquired body structure (body structure)|) @bodyStructure]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_clinical_course_contact_dermatitis_of_body_structure_caused_by_substance_v1__outdated() {
		'''
			40275004 |Contact dermatitis (disorder)|:
				[[~0..1]] {
					[[~1..1]] 263502005 |Clinical course (attribute)| = [[+id(<288524001 |Courses (qualifier value)|) @clinicalCourse]]
				},
				[[~0..1]] {
					[[~1..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]]
				},
				[[~1..*]] {
					[[~1..1]] 116676008 |Associated morphology (attribute)| = 23583003 |Inflammation (morphologic abnormality)|,
					[[~1..1]] 370135005 |Pathological process (attribute)| = [[+id(<<472963003 |Hypersensitivity process (qualifier value)|) @pathologicalProcess]],
					[[~1..1]] 363698007 |Finding site (attribute)| = [[+id(<<442083009 |Anatomical or acquired body structure (body structure)|) @bodyStructure]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_course_periods_of_life_morphology_of_body_structure_disorder() {
		'''
			64572001 |Disease (disorder)|:
				[[~0..1]] 263502005 |Clinical course (attribute)| = [[+id(< 288524001 |Courses (qualifier value)|) @course]],
				{
					[[~0..1]] 246454002 |Occurrence (attribute)| = [[+id(< 282032007 |Periods of life (qualifier value)|) @periodsOfLife]],
					370135005 |Pathological process (attribute)| = [[+id(<< 441862004 |Infectious process (qualifier value)|) @infectiousProcess]],
					[[~0..1]] 363698007 |Finding site (attribute)| = [[+id(<< 442083009 |Anatomical or acquired body structure (body structure)|) @bodyStructure]],
					[[~0..1]] 246075003 |Causative agent (attribute)| = [[+id(<< 410607006 |Organism (organism)|) @organism]],
					[[~0..1]] 116676008 |Associated morphology (attribute)| = [[+id(<< 49755003 |Morphologically abnormal structure (morphologic abnormality)|) @morphology]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_fracture_morphology_of_bone_structure_disorder() {
		'''
			64572001 |Disease (disorder)|:
				[[~1..*]] {
					[[~1..1]] 116676008 |Associated morphology (attribute)| = [[+id(<<72704001 |Fracture (morphologic abnormality)|) @fractureMorphology]],
					[[~1..1]] 363698007 |Finding site (attribute)| = [[+id(<<272673000 |Bone structure (body structure)|) @boneStructure]],
					[[~0..1]] 246454002 |Occurrence (attribute)| = [[+id(<<282032007 |Periods of life (qualifier value)|) @periodsOfLife]]
				}
		'''.assertNoErrors
	}

	@Test
	def void test_substance_pseudoallergy_disorder_v1__outdated() {
		'''
			420134006 |Propensity to adverse reactions (disorder)|:
				[[~1..1]] {
					[[~1..1]] 370135005 |Pathological process (attribute)| = 609404002 |Non-allergic hypersensitivity process (qualifier value)|
				},
				[[~1..1]] {
					[[~1..1]] 246075003 |Causative agent (attribute)| = [[+id(<105590001 |Substance (substance)|) @substance]],
					[[~1..1]] 255234002 |After (attribute)| = 609406000 |Pseudoallergic reaction (disorder)|
				}
		'''.assertNoErrors
	}

	@Test
	def void test_wound_morphology_of_body_structure_due_to_event_disorder() {
		'''
			64572001 |Disease (disorder)|:
				[[~0..1]] 42752001 |Due to (attribute)| = [[+id(<< 272379006 |Event (event)|) @event]],
				{
					116676008 |Associated morphology (attribute)| = [[+id(<< 13924000 |Wound (morphologic abnormality)|) @woundMorphology]],
					[[~0..1]] 363698007 |Finding site (attribute)| = [[+id(<< 123037004 |Body structure (body structure)|) @bodyStructure]],
					[[~0..1]] 246075003 |Causative agent (attribute)| = [[+id(<< 260787004 |Physical object (physical object)| OR << 78621006 |Physical force (physical force)| OR << 105590001 |Substance (substance)|) @physicalObject]],
					[[~0..1]] 370135005 |Pathological process (attribute)| = [[+id(<< 441862004 |Infectious process (qualifier value)|)@infectiousProcess]]
				}
		'''.assertNoErrors
	}
		
	def private void assertConceptReference(ConceptReference reference, String id, String term) {
		assertEquals(id, reference.id)
		assertEquals('|' + term + '|', reference.term)
	}
	
	def private void assertEclConceptReference(EclConceptReference reference, String id, String term) {
		assertEquals(id, reference.id)
		assertEquals('|' + term + '|', reference.term)
	}
	
	def private void assertSimpleAttribute(Attribute attribute, String nameId, String nameTerm, String valueId, String valueTerm) {
		assertConceptReference(attribute.name, nameId, nameTerm)
		assertTrue(attribute.value instanceof ConceptReference)
		assertConceptReference((attribute.value as ConceptReference), valueId, valueTerm)
	}
	
	def private void assertDescendantOfExpression(ExpressionConstraint constraint, String id, String term) {
		assertTrue(constraint instanceof DescendantOf)
		val descendantOf = constraint as DescendantOf
		assertTrue(descendantOf.constraint instanceof EclConceptReference)
		val reference = descendantOf.constraint as EclConceptReference
		assertEquals(id, reference.id)
		assertEquals('|' + term + '|', reference.term)
	}
	
	def private void assertDescendantOrSelfOfExpression(ExpressionConstraint constraint, String id, String term) {
		assertTrue(constraint instanceof DescendantOrSelfOf)
		val descendantOrSelfOf = constraint as DescendantOrSelfOf
		assertTrue(descendantOrSelfOf.constraint instanceof EclConceptReference)
		val reference = descendantOrSelfOf.constraint as EclConceptReference
		assertEquals(id, reference.id)
		assertEquals('|' + term + '|', reference.term)
	}
	
	private def ExpressionTemplate assertNoErrors(CharSequence it) throws Exception {
		val template = parse;
		assertNotNull('''Cannot parse expression: «it».''', template);
		template.assertNoErrors;
		return template
	}
	
}
