/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.ecl.tests

import com.b2international.snowowl.snomed.ecl.ecl.Script
import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*

/**
 * Parsing test for the SNOMED CT ECL.
 */
@InjectWith(EclInjectorProvider)
@RunWith(XtextRunner)
@FixMethodOrder(NAME_ASCENDING)
class EclParsingTest {

	@Inject extension ParseHelper<Script>
	@Inject extension ValidationTestHelper

	@Test
	def void test_empty() {
		''.assertNoErrors;
	}

	@Test
	def void test_5_4_1_UnaryOperators_1() {
		'''
			< ^ 700043003 |example problem list concepts reference set|
		'''.assertNoErrors
	}

	@Test
	def void test_5_4_2_BinaryOperators_1() {
		'''
			(< 19829001|disorder of lung| OR ^ 700043003 |example problem list concepts reference set|) 
			MINUS ^ 450976002|disorders and diseases reference set for GP/FP reason for encounter|
		'''.assertNoErrors
	}
	
	@Test
	def void test_5_4_2_BinaryOperators_1_lowerCase() {
		'''
			(< 19829001|disorder of lung| or ^ 700043003 |example problem list concepts reference set|) 
			minus ^ 450976002|disorders and diseases reference set for GP/FP reason for encounter|
		'''.assertNoErrors
	}

	@Test
	def void test_5_4_2_BinaryOperators_2() {
		'''
			(< 19829001|disorder of lung| MINUS ^ 700043003 |example problem list concepts reference set|) MINUS
			^ 450976002|disorders and diseases reference set for GP/FP reason for encounter|
		'''.assertNoErrors
	}

	@Test
	def void test_5_4_2_BinaryOperators_3() {
		'''
			< 19829001|disorder of lung| OR ^ 700043003 |example problem list concepts reference set| OR
			^ 450976002|disorders and diseases reference set for GP/FP reason for encounter|
		'''.assertNoErrors
	}

	@Test
	def void test_6_1_1_Self_1() {
		'''
			404684003 |clinical finding|
		'''.assertNoErrors
	}

	@Test
	def void test_6_1_2_DescendantOf_1() {
		'''
			< 404684003 |clinical finding|
		'''.assertNoErrors
	}

	@Test
	def void test_6_1_3_DescendantOrSelfOf_1() {
		'''
			<< 73211009 |diabetes mellitus|
		'''.assertNoErrors
	}

	@Test
	def void test_6_1_4_ChildOf_1() {
		'''
			<! 404684003 |clinical finding|
		'''.assertNoErrors
	}

	@Test
	def void test_6_1_5_AncestorOf_1() {
		'''
			> 40541001 |acute pulmonary edema|
		'''.assertNoErrors
	}

	@Test
	def void test_6_1_6_AncestorOrSelfOf_1() {
		'''
			>> 40541001|acute pulmonary edema|
		'''.assertNoErrors
	}

	@Test
	def void test_6_1_7_ParentOf_1() {
		'''
			>! 40541001 |acute pulmonary edema|
		'''.assertNoErrors
	}

	@Test
	def void test_6_1_8_MemberOf_1() {
		'''
			^ 700043003 |example problem list concepts reference set |
		'''.assertNoErrors
	}

	@Test
	def void test_6_1_9_Any_1() {
		'''
			*
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_1_Attribute_1() {
		'''
			< 19829001 |disorder of lung|: 
			116676008 |associated morphology| = 79654002 |edema|
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_1_Attribute_2() {
		'''
			< 19829001 |disorder of lung|: 
			116676008 |associated morphology| = << 79654002 |edema|
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_1_Attribute_3() {
		'''
			< 404684003 |clinical finding|:
			363698007 |finding site| = << 39057004 |pulmonary valve structure|, 
			116676008 |associated morphology| = << 415582006 |stenosis|
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_1_Attribute_4() {
		'''
			* : 246075003 |causative agent| = 387517004 |paracetamol|
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_2_AttributeGroup_1() {
		'''
			< 404684003 |clinical finding|:
			{ 363698007 |finding site| = << 39057004 |pulmonary valve structure|,
			  116676008 |associated morphology| = << 415582006 |stenosis|},
			{ 363698007 |finding site| = << 53085002 |right ventricular structure|, 
			  116676008 |associated morphology| = << 56246009 |hypertrophy|}
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_3_AttributeConstraintOperator_1() {
		'''
			<< 404684003 |clinical finding|:
			<< 47429007 |associated with| = << 267038008 |edema|
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_3_AttributeConstraintOperator_2() {
		'''
			<< 404684003 |clinical finding|:
			>> 246075003 |causative agent| = << 267038008 |edema|
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_4_ConcreteValues_1() {
		'''
			< 27658006 |amoxicillin |:
			411116001 |has dose form| = << 385049006 |capsule|,
			{ 111115 |has basis of strength| = ( 111115 |amoxicillin only|:
			   111115 |strength magnitude| >= #500,
			   111115 |strength unit| = 258684004 |mg|)}
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_4_ConcreteValues_2() {
		'''
			< 27658006 |amoxicillin |:
			411116001 |has dose form| = << 385049006 |capsule|,
			{ 111115 |has basis of strength| = ( 111115 |amoxicillin only|:
			   111115 |strength magnitude| >= #500,   111115 |strength magnitude| <= #800, 
			   111115 |strength unit| = 258684004 |mg|)}
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_4_ConcreteValues_3() {
		'''
			< 373873005 |pharmaceutical / biologic product|:
			111115 |trade name| = "PANADOL"
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_5_ReverseAttributes_1() {
		'''
			< 105590001 |substance|: 
			R 127489000 |has active ingredient| = 111115 |TRIPHASIL tablet|
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_6_DottedAttributes_1() {
		'''
			<  91723000 |Anatomical structure|  AND (<  125605004 |Fracture of bone| . 363698007 |Finding site| )
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_6_DottedAttributes_2() {
		'''
			<  125605004 |Fracture of bone| . 363698007 |Finding site|
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_6_DottedAttributes_3() {
		'''
			<  105590001 |Substance|  AND ( 249999999101 |TRIPHASIL tablet| . 127489000 |Has active ingredient| )
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_6_DottedAttributes_4() {
		'''
			249999999101 |TRIPHASIL tablet| . 127489000 |Has active ingredient|
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_6_DottedAttributes_5() {
		'''
			< 19829001 |Disorder of lung| . < 47429007 |Associated with| . 363698007 |Finding site|
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_6_DottedAttributes_6() {
		'''
			((< 19829001 |Disorder of lung|) . < 47429007 |Associated with|) . 363698007 |Finding site|
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_7_AnyAttributeNameValue_1() {
		'''
			< 404684003 |clinical finding|: * = 79654002 |edema|
		'''.assertNoErrors
	}

	@Test
	def void test_6_2_7_AnyAttributeNameValue_2() {
		'''
			< 404684003 |clinical finding|: 116676008 |associated morphology| = *
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_1_AttributeCardinality_1() {
		'''
			< 373873005 |pharmaceutical / biologic product|:
			[1..3] 127489000 |has active ingredient| = < 105590001 |substance|
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_1_AttributeCardinality_2() {
		'''
			< 373873005 |pharmaceutical / biologic product|:
			[1..1] 127489000 |has active ingredient| = < 105590001 |substance|
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_1_AttributeCardinality_3() {
		'''
			< 373873005 |pharmaceutical / biologic product|:
			[0..1] 127489000 |has active ingredient| = < 105590001 |substance|
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_1_AttributeCardinality_4() {
		'''
			< 373873005 |pharmaceutical / biologic product|:
			[1..*] 127489000 |has active ingredient| = < 105590001 |substance|
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_1_AttributeCardinality_5() {
		'''
			< 404684003 |clinical finding|:
			[1..1] 363698007 |finding site| = < 91723000 |anatomical structure|
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_1_AttributeCardinality_6() {
		'''
			< 404684003 |clinical finding|: 
			[2..*] 363698007 |finding site| = < 91723000 |anatomical structure|
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_1_AttributeCardinality_7() {
		'''
			< 404684003 |clinical finding|: 
			{ [2..*] 363698007 |finding site| = < 91723000 |anatomical structure| }
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_2_AttributeGroupCardinality_1() {
		'''
			< 373873005 |pharmaceutical / biologic product|:
			[1..3] { [1..*] 127489000 |has active ingredient| = < 105590001 |substance|}
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_2_AttributeGroupCardinality_2() {
		'''
			< 373873005 |pharmaceutical / biologic product|:
			[0..1] { 127489000 |has active ingredient| = < 105590001 |substance|}
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_2_AttributeGroupCardinality_3() {
		'''
			< 373873005 |pharmaceutical / biologic product|:
			[1..*] { 127489000 |has active ingredient| = < 105590001 |substance|}
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_2_AttributeGroupCardinality_4() {
		'''
			< 373873005 |pharmaceutical / biologic product|:
			[1..*] { [1..*] 127489000 |has active ingredient| = < 105590001 |substance|}
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_2_AttributeGroupCardinality_5() {
		'''
			< 404684003 |clinical finding|:
			[1..1] { 363698007 |finding site| = < 91723000 |anatomical structure|}
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_2_AttributeGroupCardinality_6() {
		'''
			< 404684003 |clinical finding|: 
			[0..0] { [2..*] 363698007 |finding site| = < 91723000 |anatomical structure|}
		'''.assertNoErrors
	}

	@Test
	def void test_6_3_3_ReverseCardinalities_1() {
		'''
			< 105590001 |Substance| : [3..3] R 127489000 |Has active ingredient| = *
		'''.assertNoErrors
	}

	@Test
	def void test_6_4_1_CompoundExpressionConstraints_1() {
		'''
			< 19829001 |disorder of lung| AND < 301867009 |edema of trunk|
		'''.assertNoErrors
	}

	@Test
	def void test_6_4_1_CompoundExpressionConstraints_2() {
		'''
			< 19829001 |disorder of lung| OR < 301867009 |edema of trunk|
		'''.assertNoErrors
	}

	@Test
	def void test_6_4_1_CompoundExpressionConstraints_3() {
		'''
			< 19829001|disorder of lung| AND ^ 700043003 |example problem list concepts reference set|
		'''.assertNoErrors
	}

	@Test
	def void test_6_4_1_CompoundExpressionConstraints_4() {
		'''
			(< 19829001 |disorder of lung| AND < 301867009 |edema of trunk|) AND 
			^ 700043003 |example problem list concepts reference set|
		'''.assertNoErrors
	}

	@Test
	def void test_6_4_1_CompoundExpressionConstraints_5() {
		'''
			(< 19829001 |disorder of lung| AND < 301867009 |edema of trunk|) OR 
			^ 700043003 |example problem list concepts reference set|
		'''.assertNoErrors
	}
	
	@Test
	def void test_6_4_1_CompoundExpressionConstraints_5_lowerCase() {
		'''
			(< 19829001 |disorder of lung| and < 301867009 |edema of trunk|) or 
			^ 700043003 |example problem list concepts reference set|
		'''.assertNoErrors
	}

	@Test
	def void test_6_4_2_AttributeConjunctionDisjunction_1() {
		'''
			< 404684003 |clinical finding|:
			363698007 |finding site| = << 39057004 |pulmonary valve structure| AND 
			116676008 |associated morphology| = << 415582006 |stenosis|
		'''.assertNoErrors
	}

	@Test
	def void test_6_4_2_AttributeConjunctionDisjunction_2() {
		'''
			< 404684003 |clinical finding|: 
			116676008 |associated morphology| = << 55641003 |infarct| OR 
			42752001 |due to| = << 22298006 |myocardial infarction|
		'''.assertNoErrors
	}

	@Test
	def void test_6_4_2_AttributeConjunctionDisjunction_3() {
		'''
			< 404684003 |clinical finding|:
			( 363698007 |finding site| = << 39057004 |pulmonary valve structure| AND 
			116676008 |associated morphology| = << 415582006 |stenosis| ) AND
			42752001 |due to| = << 445238008|malignant carcinoid tumor|
		'''.assertNoErrors
	}

	@Test
	def void test_6_4_2_AttributeConjunctionDisjunction_4() {
		'''
			< 404684003 |clinical finding| : 
			( 363698007 |finding site| = << 39057004 |pulmonary valve structure| AND 
			116676008 |associated morphology| = << 415582006 |stenosis|) OR
			42752001 |due to| = << 445238008|malignant carcinoid tumor|
		'''.assertNoErrors
	}

	@Test
	def void test_6_4_3_AttributeGroupConjunctionDisjunction_1() {
		'''
			< 404684003 |clinical finding|:
			{ 363698007 |finding site| = << 39057004 |pulmonary valve structure|,
			   116676008 |associated morphology| = << 415582006 |stenosis|} OR
			{ 363698007 |finding site| = << 53085002 |right ventricular structure|,
			   116676008 |associated morphology| = << 56246009 |hypertrophy|}
		'''.assertNoErrors
	}

	@Test
	def void test_6_4_4_AttributeValueConjunctionDisjunction_1() {
		'''
			^ 450990004 |adverse drug reactions reference set for GP/FP health issue|: 246075003 |causative agent| =
			(< 373873005 |pharmaceutical / biologic product| OR < 105590001 |substance|)
		'''.assertNoErrors
	}

	@Test
	def void test_6_4_4_AttributeValueConjunctionDisjunction_2() {
		'''
			< 404684003 |clinical finding|: 116676008 |associated morphology| =
			(<< 56208002|ulcer| AND << 50960005|hemorrhage|)
		'''.assertNoErrors
	}

	@Test
	def void test_6_5_1_ExclusionSimpleExpressions_1() {
		'''
			<< 19829001 |disorder of lung| MINUS << 301867009 |edema of trunk|
		'''.assertNoErrors
	}

	@Test
	def void test_6_5_1_ExclusionSimpleExpressions_2() {
		'''
			<< 19829001 |disorder of lung| MINUS ^ 700043003 |example problem list concepts reference set|
		'''.assertNoErrors
	}

	@Test
	def void test_6_5_2_ExclusionAttributeValues_1() {
		'''
			< 404684003 |clinical finding|: 116676008 |associated morphology| =
			((<< 56208002 |ulcer| AND << 50960005 |hemorrhage|) MINUS << 26036001 |obstruction|)
		'''.assertNoErrors
	}

	@Test
	def void test_6_5_3_NotEqualToAttributeValue_1() {
		'''
			< 404684003 |clinical finding|: 
			116676008 |associated morphology| !=  << 26036001 |obstruction|
		'''.assertNoErrors
	}

	@Test
	def void test_6_5_3_NotEqualToAttributeValue_2() {
		'''
			< 404684003 |clinical finding|: 
			[0..0] 116676008 |associated morphology| =  << 26036001 |obstruction|
		'''.assertNoErrors
	}

	@Test
	def void test_6_5_3_NotEqualToAttributeValue_3() {
		'''
			< 404684003 |clinical finding|: 
			[0..0] 116676008 |associated morphology| != << 26036001 |obstruction|
		'''.assertNoErrors
	}

	@Test
	def void test_6_5_3_NotEqualToAttributeValue_4() {
		'''
			< 404684003 |clinical finding|: 
			[0..0] 116676008 |associated morphology| !=  << 26036001 |obstruction| and
			[1..*] 116676008 |associated morphology| =   << 26036001 |obstruction|
		'''.assertNoErrors
	}

	@Test
	def void test_6_6_1_NestedConstraintOperators_1() {
		'''
			<< (^ 700043003 |Example problem list concepts reference set| )
		'''.assertNoErrors
	}

	@Test
	def void test_6_6_2_NestedMemberOfFunction_1() {
		'''
			^ (< 450973005 |GP/FP health issue reference set| )  
		'''.assertNoErrors
	}

	@Test
	def void test_6_6_3_NestedCompoundExpressionConstraints_1() {
		'''
			(<   404684003 |Clinical finding|  :
			         363698007 |Finding site|   = <<   39057004 |Pulmonary valve structure|  ) 
			  AND ^  700043003 |Example problem list concepts reference set|
		'''.assertNoErrors
	}

	@Test
	def void test_6_6_3_NestedCompoundExpressionConstraints_2() {
		'''
			(<   404684003 |Clinical finding|  :   363698007 |Finding site|   = <<   39057004 |Pulmonary valve structure|  ) 
			   AND (<   64572001 |Disease|  :   116676008 |Associated morphology|   = <<   415582006 |Stenosis|  )
		'''.assertNoErrors
	}

	@Test
	def void test_6_6_4_NestedDottedAttributes_1() {
		'''
			(<< 17636008 |Specimen collection| : 
			    424226004 |Using device|  = <<  19923001 |Catheter| ) 
			      .  363701004 |Direct substance|
		'''.assertNoErrors
	}

	@Test
	def void test_6_6_5_NestedRefinement_1() {
		'''
			(<<  404684003 |Clinical finding (finding)|  OR <<  272379006 |Event (event)|  ): 
			 255234002 |After|  = <<  71388002 |Procedure (procedure)|
		'''.assertNoErrors
	}

	@Test
	def void test_6_6_6_NestedAttributeName_1() {
		'''
			<<  125605004 |Fracture of bone|  :
			[0..0] ((<<  410662002 |Concept model attribute|  MINUS  363698007 |Finding site| ) MINUS  16676008 |Associated morphology|  ) = *
		'''.assertNoErrors
	}

	@Test
	def void test_6_6_7_NestedAttributeValue_1() {
		'''
			<  404684003 |Clinical finding|  :
			 47429007 |Associated with|  = (<  404684003 |Clinical finding|  : 
			 116676008 |Associated morphology|  = <<  55641003 |Infarct|  )
		'''.assertNoErrors
	}

	private def void assertNoErrors(CharSequence it) throws Exception {
		val script = parse;
		assertNotNull('''Cannot parse expression: «it».''', script);
		script.assertNoErrors;
	}

}
