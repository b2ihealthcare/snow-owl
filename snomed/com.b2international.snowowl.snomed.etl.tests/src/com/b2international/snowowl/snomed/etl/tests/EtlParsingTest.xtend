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

import com.b2international.snowowl.snomed.etl.etl.Attribute
import com.b2international.snowowl.snomed.etl.etl.ConceptReference
import com.b2international.snowowl.snomed.etl.etl.ConceptReplacementSlot
import com.b2international.snowowl.snomed.etl.etl.ExpressionReplacementSlot
import com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate
import com.b2international.snowowl.snomed.etl.etl.StringReplacementSlot
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
		
		assertTrue(value.slot instanceof ConceptReplacementSlot)
		
		val conceptReplacementSlot = value.slot as ConceptReplacementSlot
		
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
		'''
			323510009 |Amoxycillin 500mg capsule| : 
			   { 749999999108 |Has pack size magnitude|  = [[+int]], 
			     759999999106 |Has pack size units|  =  428641000 |Capsule| }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_2_Typed_ConcreteValueReplacement_3() {
		'''
			326645001 |Chlorhexidine gluconate 0.02% irrigation solution| : 
			  { 749999999108 |Has pack size magnitude|  = [[+dec]],
			    759999999106 |Has pack size units|  =  258770004 |Liter| }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_2_Typed_ExpressionReplacement_1() {
		'''
			404684003 |Clinical finding| :  255234002 |After|  = [[+scg]]
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_2_Typed_TokenReplacement_1() {
		'''
			[[+tok]]  73211009 |Diabetes mellitus|  :   363698007 |Finding site|   =   113331007 |Endocrine system|
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_3_Constrained_ExpressionConstraints_1() {
		'''
			71388002 |Procedure| :
			  { 260686004 |Method|  =  312251004 |Computed tomography imaging action| , 
			      405813007 |Procedure site - Direct|  = 
			        [[+id (<<  442083009 |Anatomical or acquired body structure| )]]}
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_3_Constrained_ExpressionConstraints_2() {
		'''
			71388002 |Procedure| :
			  { 260686004 |Method|  =  312251004 |Computed tomography imaging action| , 
			      405813007 |Procedure site - Direct|  = 
			        [[+scg (<<  442083009 |Anatomical or acquired body structure| )]]}
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_3_Constrained_RangeConstraints_1() {
		'''
			323510009 |Amoxycillin 500mg capsule|  : 
			   {  749999999108 |Has pack size magnitude|   = [[+int (#20..#30)]], 
			      759999999106 |Has pack size units|   =   428641000 |Capsule|  }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_3_Constrained_RangeConstraints_2() {
		'''
			323510009 |Amoxycillin 500mg capsule|  : 
			    {  749999999108 |Has pack size magnitude|   = [[+int (>#20..<#30)]], 
			       759999999106 |Has pack size units|   =   428641000 |Capsule|  }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_3_Constrained_RangeConstraints_3() {
		'''
			323510009 |Amoxycillin 500mg capsule|  : 
			   {  749999999108 |Has pack size magnitude|   = [[+int (#10..#20 #30..#40)]], 
			      759999999106 |Has pack size units|   =   428641000 |Capsule|  }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_3_Constrained_RangeConstraints_4() {
		'''
			323510009 |Amoxycillin 500mg capsule|  : 
			    {  749999999108 |Has pack size magnitude|   = [[+int (#20..)]], 
			       759999999106 |Has pack size units|   =   428641000 |Capsule|  }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_3_Constrained_RangeConstraints_5() {
		'''
			323510009 |Amoxycillin 500mg capsule|  : 
			    {  749999999108 |Has pack size magnitude|   = [[+int (#20..)]], 
			       759999999106 |Has pack size units|   =   428641000 |Capsule|  }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_3_Constrained_ValueListConstraints_1() {
		'''
			[[+tok (<<< ===)]]  281647001 |Adverse reaction (disorder)| :
			    246075003 |Causative agent (attribute)|  = [[+id]]
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_3_Constrained_ValueListConstraints_2() {
		'''
			322236009 |Paracetamol 500mg tablet|  :  209999999104 |Has trade name|  = [[+str ("PANADOL" "TYLENOL" "HERRON")]]
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_3_Constrained_ValueListConstraints_3() {
		'''
			323510009 |Amoxycillin 500mg capsule|  : 
			     {  749999999108 |Has pack size magnitude|   = [[+int (#10 #20 #30)]], 
			        759999999106 |Has pack size units|   =   428641000 |Capsule|  }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_4_Named_RepeatedSlotNames_1() {
		'''
			404684003 |Finding| : { 363698007 |Finding site|  = [[+ @site]], 363714003 |Interprets| = ( 363787002 |Observable entity| : 704319004 |Inheres in| = [[+ @site]])}
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_4_Named_SlotNames_1() {
		'''
			243796009 |Situation with explicit context| :
			  { 246090004 |Associated finding|  = [[+id (< 404684003 |Clinical finding| ) @finding]],
			    40873100 |Temporal context|  =  410511007 |Current or past (actual)| ,
			    408729009 |Finding context|  =  410515003 |Known present| ,
			    408732007 |Subject relationship context|  =  444148008 |Person in family of subject| }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_5_Information_Cardinality_1() {
		'''
			[[1..3]] [[+id (< 404684003 |Clinical finding| : [0..0] 363698007 |Finding site| = * ) @finding]]: 
			  [[1..1]] 363698007 |Finding site| = [[+id (<< 442083009 |Anatomical or acquired body structure| ) @site ]]
		'''.assertNoErrors
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
		'''
			[[1..*]] [[+id (<< 71388002 |Procedure| )]]: 
			   [[1..*]] { [[1..*]]  260686004 |Method|  = [[+id (<<  129264002 |Action (qualifier value)| )]],
			              [[1..*]]  405813007 |Procedure site - Direct|  = [[+id (<<  442083009 |Anatomical or acquired body structure (body structure)| )]] }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_5_Information_InformationSlotName_1() {
		'''
			71388002 |Procedure|  : [[1..1 @mpGroup]]
			  { 260686004 |Method|  =  312251004 |Computed tomography imaging action| ,
			      405813007 |Procedure site - Direct|  = [[+id (<< 442083009 |Anatomical or acquired body structure| ) @site]] }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_6_Advanced_MultipleCardinalityConstraints_1() {
		'''
			[[1..1]] [[+ (< 71388002 |Procedure| ) @Procedure]] : 
			   [[ 1..2 @SMgroup]] {[[1..1]]  405813007 |Procedure site - direct|  = [[+ (< 91723000 |Anatomical structure| ) @BodySite]], 
			                       [[1..1]]  260686004 |Method|  = [[+ (< 129264002 |Action (qualifier value)| ) @Method]]}
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_6_Advanced_MultipleCardinalityConstraints_2() {
		'''
			[[1..1]] [[+id (<< 413350009 |Finding with explicit context|) @Condition]]:
			   [[ 1..2 @AFgroup ]] {[[1..1]] 246090004 |Associated finding| = ([[+id (<< 404684003 |Clinical finding|) @Finding]]:
			                [[0..1 @SSgroup]] { [[0..1]] 246112005 |Severity| = [[+id (< 272141005 |Severities|) @Severity]],
			                                   [[0..1]] 363698007 |Finding site| = [[+id (< 91723000 |Anatomical structure|) @Site]]}),
			                        [[1..1]] 408732007 |Subject relationship context| = [[+id (< 444148008 |Person in family of subject|) @Relationship]],
			                        [[1..1]] 408731000 |Temporal context|  = [[+id (< 410510008 |Temporal context value|) @Time]],
			                        [[1..1]] 408729009 |Finding context|  = [[+id (< 410514004 |Finding context value|) @Context]] }
		'''.assertNoErrors
	}

	@Test
	def void test_7_1_6_Advanced_MultipleReplacementSlots_1() {
		'''
			[[+ (< 71388002 |Procedure| ) @Procedure]] :
			   { 405813007 |Procedure site - direct|  = [[+ (< 91723000 |Anatomical structure| ) @BodySite]],
			     260686004 |Method|  = [[+ (< 129264002 |Action (qualifier value)| ) @Method]]}
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

	def private void assertConceptReference(ConceptReference reference, String id, String term) {
		assertEquals(id, reference.id)
		assertEquals('|' + term + '|', reference.term)
	}
	
	def private void assertSimpleAttribute(Attribute attribute, String nameId, String nameTerm, String valueId, String valueTerm) {
		assertConceptReference(attribute.name, nameId, nameTerm)
		assertTrue(attribute.value instanceof ConceptReference)
		assertConceptReference((attribute.value as ConceptReference), valueId, valueTerm)
	}
	
	private def ExpressionTemplate assertNoErrors(CharSequence it) throws Exception {
		val template = parse;
		assertNotNull('''Cannot parse expression: «it».''', template);
		template.assertNoErrors;
		return template
	}
	
}
