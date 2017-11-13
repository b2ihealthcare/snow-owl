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
package com.b2international.snowowl.semanticengine.simpleast.normalform.test;

import org.junit.Ignore;
import org.junit.Test;

import com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;

public class NormalFormGeneratorTestFromSnomedDocument {
	
	@Test
	public void testFractureOfFemur() {
		ExpressionConstraint originalExpression = TestUtils.parseExpression("71620000 | fracture of femur |");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression("64572001 | disease | : {116676008 | associated morphology | = 72704001 | fracture | ," +
				"363698007 | finding site | = 71341001 | bone structure of femur | }");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression( 
				"64572001 | disease | : {116676008 | associated morphology | = 72704001 | fracture | ," +
				"363698007 | finding site | = 71341001 | bone structure of femur | }");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	@Ignore
	public void testAsthma() {
		// Ignored, different in current SNOMED CT
		ExpressionConstraint originalExpression = TestUtils.parseExpression("195967001 | asthma |");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"195967001 | asthma | : {116676008 | associated morphology | = 26036001 | obstruction | ," +
				"363698007 | finding site | = 955009 | bronchial structure | }");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"195967001 | asthma |");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	@Ignore
	public void testAllergicAsthma() {
		// Ignored, 'allergic asthma' is no longer fully defined.
		ExpressionConstraint originalExpression = TestUtils.parseExpression("389145006 | allergic asthma |");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"195967001 | asthma | : 42752001 | due to | = 419076005 | allergic reaction | " +
				"{116676008 | associated morphology | = 26036001 | obstruction | ," +
				"363698007 | finding site | = 955009 | bronchial structure | }");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"195967001 | asthma | : 42752001 | due to | = 419076005 | allergic reaction |");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testNeoplasmOfRightLowerLobeOfLung() {
		ExpressionConstraint originalExpression = TestUtils.parseExpression("126716006 | neoplasm of right lower lobe of lung |");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"64572001 | disease | : {116676008 | associated morphology | = 108369006 | neoplasm |," +
				"363698007 | finding site | = " +
				"(90572001 | structure of lower lobe of lung | :272741003 | laterality | = 24028007 | right | )}");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"64572001 | disease | : {116676008 | associated morphology | = 108369006 | neoplasm |," +
				"363698007 | finding site | = " +
				"(90572001 | structure of lower lobe of lung | :272741003 | laterality | = 24028007 | right | )}");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testFractureOfFemurDisplacedFracture() {
		ExpressionConstraint originalExpression = TestUtils.parseExpression("71620000 | fracture of femur | : " +
				"116676008 | associated morphology | = 134341006 | displaced fracture |");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"64572001 | disease | : {116676008 | associated morphology | = " +
				"134341006 | displaced fracture |,363698007 | finding site | = 71341001 | bone structure of femur | }");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"64572001 | disease | : {116676008 | associated morphology | = " +
				"134341006 | displaced fracture |,363698007 | finding site | = 71341001 | bone structure of femur | }");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testFractureOfFemurDisplacedFractureBoneStructureOfFemur() {
		ExpressionConstraint originalExpression = TestUtils.parseExpression("71620000 | fracture of femur | : " +
				"116676008 | associated morphology | = 134341006 | displaced fracture | ," +
				"363698007 | finding site | = 71341001 | bone structure of femur |");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"64572001 | disease | : {116676008 | associated morphology | = 134341006 | displaced fracture |," +
				"363698007 | finding site | = 71341001 | bone structure of femur | }");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"64572001 | disease | : {116676008 | associated morphology | = 134341006 | displaced fracture |," +
				"363698007 | finding site | = 71341001 | bone structure of femur | }");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testFractureOfFemurSevere() {
		ExpressionConstraint originalExpression = TestUtils.parseExpression("71620000 | fracture of femur | : " +
				"246112005 | severity | = 24484000 | severe |");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"64572001 | disease | : 246112005 | severity | = 24484000 | severe | " +
				"{116676008 | associated morphology | = 72704001 | fracture |," +
				"363698007 | finding site | = 71341001 | bone structure of femur | }");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"64572001 | disease | : 246112005 | severity | = 24484000 | severe | " +
				"{116676008 | associated morphology | = 72704001 | fracture |," +
				"363698007 | finding site | = 71341001 | bone structure of femur | }");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testFractureOfFemurLeft() {
		ExpressionConstraint originalExpression = TestUtils.parseExpression("71620000 | fracture of femur | : " +
				"363698007 | finding site | = " +
				"(71341001 | bone structure of femur | :272741003 | laterality | = 7771000 | left | )");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"64572001 | disease | : {116676008 | associated morphology | = 72704001 | fracture |," +
				"363698007 | finding site | = " +
				"(71341001 | bone structure of femur | : 272741003 | laterality | = 7771000 | left | )}");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"64572001 | disease | : {116676008 | associated morphology | = 72704001 | fracture |," +
				"363698007 | finding site | = " +
				"(71341001 | bone structure of femur | : 272741003 | laterality | = 7771000 | left | )}");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testFootPainLeft() {
		ExpressionConstraint originalExpression = TestUtils.parseExpression("47933007 | foot pain | : " +
				"363698007 | finding site | = " +
				"(56459004 | foot structure | :272741003 | laterality | = 7771000 | left | )");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"22253000 | pain | : 363698007 | finding site | = " +
				"(56459004 | foot structure | : 272741003 | laterality | = 7771000 | left | )");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"22253000 | pain | : 363698007 | finding site | = " +
				"(56459004 | foot structure | : 272741003 | laterality | = 7771000 | left | )");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testFootPainStructureOfLeftFoot() {
		ExpressionConstraint originalExpression = TestUtils.parseExpression("47933007 | foot pain | : " +
				"363698007 | finding site | = 22335008 | structure of left foot |");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"22253000 | pain | : 363698007 | finding site | = " +
				"(56459004 | foot structure | : 272741003 | laterality | = 7771000 | left | )");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"22253000 | pain | : 363698007 | finding site | = (56459004 | foot structure | : 272741003 | laterality | = 7771000 | left | )");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	@Ignore
	public void testAuscultationFromDocument() {
		// Ignored, auscultation doesn't point to (retired) concept 'anatomical concepts' anymore.
		ExpressionConstraint originalExpression = TestUtils.parseExpression("37931006 | auscultation | ");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"315306007 | examination by method | : {260686004 | method | = 129436005 | auscultation - action |" +
				",363704007 | procedure site | = 257728006 | anatomical concepts |}");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
		"195967001 | asthma |");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	@Ignore
	public void testExpiratoryCracklesEntireLowerLobeOfLung() {
		// Ignored, 'auscultation' doesn't point to (retired) concept 'anatomical concepts' anymore.
		// 'examination by method' is fully defined, first primitive supertype is 'procedure'
		ExpressionConstraint originalExpression = TestUtils.parseExpression("12529006 | expiratory crackles | : " +
				"363698007 | finding site | = 303549000 | entire lower lobe of lung |");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"12529006 | expiratory crackles | : 363698007 | finding site | = 303549000 | entire lower lobe of lung |," +
				"363714003 | interprets | = 78064003 | respiratory function | ," +
				"418775008 | finding method | =" +
				"(315306007 | examination by method | : " +
				"{260686004 | method | = 129436005 | auscultation - action |," +
				"363704007 | procedure site | = 257728006 | anatomical concepts |})");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"12529006 | expiratory crackles | : " +
				"363698007 | finding site | = 303549000 | entire lower lobe of lung |");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testExpiratoryCrackles() {
		ExpressionConstraint originalExpression = TestUtils.parseExpression("12529006 | expiratory crackles | ");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression("12529006 | expiratory crackles | :" +
				"363698007 | finding site | = 82094008 | lower respiratory tract structure |," +
				"418775008 | finding method | =" +
				"(37931006 | auscultation | : " +
				"260686004 | method | = 129436005 | auscultation - action |)");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"12529006 | expiratory crackles | ");	// TODO: verify short normal form
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	@Ignore
	public void testAllergicAsthmaDustMite() {
		// Ignored, 'allergic asthma' is no longer fully defined.
		ExpressionConstraint originalExpression = TestUtils.parseExpression("389145006 | allergic asthma | : " +
				"246075003 | causative agent | = 260147004 | house dust mite |");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"195967001 | asthma | : 246075003 | causative agent | = 260147004 | house dust mite |," +
				"42752001 | due to | = 419076005 | allergic reaction | " +
				"{116676008 | associated morphology | = 26036001 | obstruction |," +
				"363698007 | finding site | = 955009 | bronchial structure | }");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"195967001 | asthma | : 246075003 | causative agent | = 260147004 | house dust mite | ," +
				"42752001 | due to | = 419076005 | allergic reaction |");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testSalpingoOophorectomyEntireLeftFallopianTube() {
		ExpressionConstraint originalExpression = TestUtils.parseExpression("116028008 | salpingo-oophorectomy | : " +
				"363704007 | procedure site | = 280107002 | entire left fallopian tube |");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"71388002 | procedure | : {260686004 | method | = 129304002 | excision - action |," +
				"363704007 | procedure site | = " +
				"(181463001 | entire fallopian tube | :" +
				"272741003 | laterality | = 7771000 | left | )} " +
				"{260686004 | method | = 129304002 | excision - action |," +
				"363704007 | procedure site | = 15497006 | ovarian structure | }");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"71388002 | procedure | : {260686004 | method | = 129304002 | excision - action |," +
				"363704007 | procedure site | = " +
				"(181463001 | entire fallopian tube | :" +
				"272741003 | laterality | = 7771000 | left | )} " +
				"{260686004 | method | = 129304002 | excision - action |," +
				"363704007 | procedure site | = 15497006 | ovarian structure | }");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testSalpingoOophorectomyLeft() {
		ExpressionConstraint originalExpression = TestUtils.parseExpression("116028008 | salpingo-oophorectomy | : " +
				"272741003 | laterality | = 7771000 | left |");
		ExpressionConstraint expectedLongNormalFormExpression = TestUtils.parseExpression(
				"71388002 | procedure | : {260686004 | method | = 129304002 | excision - action |," +
				"363704007 | procedure site | = " +
				"(15497006 | ovarian structure | :" +
				"272741003 | laterality | = 7771000 | left | )} " +
				"{260686004 | method | = 129304002 | excision - action |," +
				"363704007 | procedure site | = " +
				"(31435000 | fallopian tube structure | :" +
				"272741003 | laterality | = 7771000 | left | )}");
		ExpressionConstraint expectedShortNormalFormExpression = TestUtils.parseExpression(
				"71388002 | procedure | : {260686004 | method | = 129304002 | excision - action |," +
				"363704007 | procedure site | = " +
				"(15497006 | ovarian structure | :" +
				"272741003 | laterality | = 7771000 | left | )} " +
				"{260686004 | method | = 129304002 | excision - action |," +
				"363704007 | procedure site | = " +
				"(31435000 | fallopian tube structure | :" +
				"272741003 | laterality | = 7771000 | left | )}");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	private void testNormalFormGenerator(ExpressionConstraint originalExpression, ExpressionConstraint expectedLongNormalFormExpression, 
			ExpressionConstraint expectedShortNormalFormExpression) {
		throw new UnsupportedOperationException("TODO implement me");
//		SimpleAstExpressionNormalFormGenerator normalFormGenerator = new SimpleAstExpressionNormalFormGenerator(Branch.MAIN_PATH);
//		ESCGEcoreRewriter rewriter = new ESCGEcoreRewriter(escgParser);
//		RValue longNormalFormExpression = normalFormGenerator.getLongNormalForm(rewriter.rewrite(originalExpression));
//		TestUtils.assertExpressionsEqual("Long normal form expression different from expected.", rewriter.rewrite(expectedLongNormalFormExpression), longNormalFormExpression);
//		
//		normalFormGenerator = new SimpleAstExpressionNormalFormGenerator(Branch.MAIN_PATH);
//		RValue shortNormalFormExpression = normalFormGenerator.getShortNormalForm(rewriter.rewrite(originalExpression));
//		TestUtils.assertExpressionsEqual("Short normal form expression different from expected.", rewriter.rewrite(expectedShortNormalFormExpression), shortNormalFormExpression);
	}

}
