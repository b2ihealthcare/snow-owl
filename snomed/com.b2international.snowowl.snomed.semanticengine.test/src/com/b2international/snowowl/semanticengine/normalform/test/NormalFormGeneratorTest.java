/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.semanticengine.normalform.test;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.dsl.SCGStandaloneSetup;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.semanticengine.normalform.ScgExpressionNormalFormGenerator;
import com.b2international.snowowl.semanticengine.test.utils.TestUtils;
import com.b2international.snowowl.snomed.datastore.RecursiveTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.google.common.collect.Lists;

/**
 *
 */
public class NormalFormGeneratorTest {
	
	@Test
	public void testAuscultation() {
		// updated expected normal forms to match SNOMED CT INT 20120131
		Expression originalExpression = (Expression) SCGStandaloneSetup.parse("37931006 | auscultation | ");
		Expression expectedLongNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"71388002 | procedure | : 260686004 | method | = 129436005 | auscultation - action |");
		Expression expectedShortNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"71388002 | procedure | : 260686004 | method | = 129436005 | auscultation - action |");	// TODO: check short form
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testExpiratoryCrackles() {
		// updated expected normal forms to match SNOMED CT INT 20120131
		Expression originalExpression = (Expression) SCGStandaloneSetup.parse("12529006 | expiratory crackles | ");
		Expression expectedLongNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"12529006 | expiratory crackles | :" +
				"363698007 | finding site | = 82094008 | lower respiratory tract structure |," +
				"418775008 | finding method | =" +
				"(71388002 | auscultation | : " +
				"260686004 | method | = 129436005 | auscultation - action |)");
		Expression expectedShortNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"12529006 | expiratory crackles | ");	// TODO: verify short normal form
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}

	//////////////////////////////////////////////// old tests from Orsi ///////////////////////////////////////////
	
	@Test
	public void testTetralogyOfFallot() {
		Expression originalExpression = (Expression) SCGStandaloneSetup.parse("86299006 | tetralogy of fallot |");
		Expression expectedLongNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
		"86299006 | tetralogy of Fallot | :" +
			"246454002 | occurrence | = 255399007 | congenital |" + 
			"{ 116676008 | associated morphology | = 107656002 | congenital anomaly |" + 
			", 363698007 | finding site | = 21814001 | cardiac ventricular structure |" + 
			"}" +
			"{ 116676008 | associated morphology | = 30812002 | overriding structures |" + 
			", 363698007 | finding site | = 113262008 | thoracic aorta structure |" + 
			"}" +
			"{ 116676008 | associated morphology | = 415582006 | stenosis |" + 
			", 363698007 | finding site | = 39057004 | pulmonary valve structure |" + 
			"}" +
			"{ 116676008 | associated morphology | = 56246009 | hypertrophy |" + 
			", 363698007 | finding site | = 53085002 | right ventricular structure |" + 
			"}" +
			"{ 116676008 | associated morphology | = 6920004 | defect |" + 
			", 363698007 | finding site | = 589001 | interventricular septum structure |" + 
			"}");
		
		Expression expectedShortNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"86299006 | tetralogy of Fallot |");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testTetralogyOfFallotRoundTwo() {
		Expression originalExpression = (Expression) SCGStandaloneSetup.parse("86299006 | tetralogy of Fallot | :"+
				"246454002 | occurrence | = 255399007 | congenital |"+ 
				"{ 116676008 | associated morphology | = 107656002 | congenital anomaly |"+ 
				", 363698007 | finding site | = 21814001 | cardiac ventricular structure |"+ 
				"}"+
				"{ 116676008 | associated morphology | = 30812002 | overriding structures |"+ 
				", 363698007 | finding site | = 113262008 | thoracic aorta structure |"+ 
				"}"+
				"{ 116676008 | associated morphology | = 415582006 | stenosis |"+ 
				", 363698007 | finding site | = 39057004 | pulmonary valve structure |"+ 
				"}"+
				"{ 116676008 | associated morphology | = 56246009 | hypertrophy |"+ 
				", 363698007 | finding site | = 53085002 | right ventricular structure |"+ 
				"}"+
				"{ 116676008 | associated morphology | = 6920004 | defect |"+ 
				", 363698007 | finding site | = 589001 | interventricular septum structure |"+ 
		"}");

		Expression expectedLongNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"86299006 | tetralogy of Fallot | :"+
						"246454002 | occurrence | = 255399007 | congenital |"+ 
						"{ 116676008 | associated morphology | = 107656002 | congenital anomaly |"+ 
						", 363698007 | finding site | = 21814001 | cardiac ventricular structure |"+ 
						"}"+
						"{ 116676008 | associated morphology | = 30812002 | overriding structures |"+ 
						", 363698007 | finding site | = 113262008 | thoracic aorta structure |"+ 
						"}"+
						"{ 116676008 | associated morphology | = 415582006 | stenosis |"+ 
						", 363698007 | finding site | = 39057004 | pulmonary valve structure |"+ 
						"}"+
						"{ 116676008 | associated morphology | = 56246009 | hypertrophy |"+ 
						", 363698007 | finding site | = 53085002 | right ventricular structure |"+ 
						"}"+
						"{ 116676008 | associated morphology | = 6920004 | defect |"+ 
						", 363698007 | finding site | = 589001 | interventricular septum structure |"+ 
				"}");
		Expression expectedShortNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"86299006 | tetralogy of Fallot |");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testExcisionOfSubmandibularGland() {
		Expression originalExpression = (Expression) SCGStandaloneSetup.parse("47227006 | excision of submandibular gland | ");
		Expression expectedLongNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"71388002 | procedure | :"+
				"{ 260686004 | method | = 129304002 | excision - action |"+ 
				", 405813007 | procedure site - Direct | = 385296007 | submandibular salivary gland structure |"+ 
				"}");
		Expression expectedShortNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"71388002 | procedure | :"+
				"{ 260686004 | method | = 129304002 | excision - action |"+ 
				", 405813007 | procedure site - Direct | = 385296007 | submandibular salivary gland structure |}");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	@Ignore
	public void testGaleazziFractureDislocation() {
		Expression originalExpression = (Expression) SCGStandaloneSetup.parse("271576001 | Galeazzi fracture dislocation |");
		Expression expectedLongNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"263079005 | fracture dislocation of wrist joint | :"+
				"{ 116676008 | associated morphology | = 72704001 | fracture |"+ 
				", 363698007 | finding site | = 299706009 | bone structure of wrist and/or hand |"+ 
				"}"+
				"{ 116676008 | associated morphology | = 72704001 | fracture |"+ 
				", 363698007 | finding site | = 47728000 | bone structure of shaft of radius |"+ 
				"}"+
				"{ 116676008 | associated morphology | = 87642003 | dislocation |"+ 
				", 363698007 | finding site | = 74670003 | wrist joint structure |"+ 
				"}");
		Expression expectedShortNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"263079005 | fracture dislocation of wrist joint | :"+
				"{ 116676008 | associated morphology | = 72704001 | fracture |"+ 
				", 363698007 | finding site | = 47728000 | bone structure of shaft of radius |}");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}

	//////////////////////////////////////////////// new tests from Orsi (2012-03-23) ///////////////////////////////////////////
	
	@Test
	public void testAcuteAppendicitisWithAppendixAbscess() {
		Expression originalExpression = (Expression) SCGStandaloneSetup.parse("266439004");
		Expression expectedLongNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"64572001 | disease | :"+
					"263502005 | clinical course | = 424124008 | sudden onset AND/OR short duration |"+ 
					"{ 116676008 | associated morphology | = 44132006 | abscess morphology |"+ 
					", 363698007 | finding site | = 66754008 | appendix structure |"+ 
					"}"+
					"{ 116676008 | associated morphology | = 4532008 | acute inflammation |"+ 
					", 363698007 | finding site | = 66754008 | appendix structure |"+ 
					"}");
		// TODO: figure out expected short normal form
		Expression expectedShortNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"64572001 | disease | :"+
						"263502005 | clinical course | = 424124008 | sudden onset AND/OR short duration |"+ 
						"{ 116676008 | associated morphology | = 44132006 | abscess morphology |"+ 
						", 363698007 | finding site | = 66754008 | appendix structure |"+ 
						"}"+
						"{ 116676008 | associated morphology | = 4532008 | acute inflammation |"+ 
						", 363698007 | finding site | = 66754008 | appendix structure |"+ 
						"}");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testViralHepatitis() {
		Expression originalExpression = (Expression) SCGStandaloneSetup.parse("3738000");
		Expression expectedLongNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"64572001 | disease | :"+
					"246075003 | causative agent | = 49872002 | virus |"+ 
					", 370135005 | pathological process | = 441862004 | infectious process |"+ 
					"{ 116676008 | associated morphology | = 23583003 | inflammation |"+ 
					", 363698007 | finding site | = 10200004 | liver structure |"+ 
					"}");
		// TODO: figure out expected short normal form
		Expression expectedShortNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"64572001 | disease | :"+
						"246075003 | causative agent | = 49872002 | virus |"+ 
						", 370135005 | pathological process | = 441862004 | infectious process |"+ 
						"{ 116676008 | associated morphology | = 23583003 | inflammation |"+ 
						", 363698007 | finding site | = 10200004 | liver structure |"+ 
						"}");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testCerebellarInfarction() {
		Expression originalExpression = (Expression) SCGStandaloneSetup.parse("95460007");
		Expression expectedLongNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"230696001 | posterior cerebral circulation infarction | :"+
				"{ 116676008 | associated morphology | = 55641003 | infarct |"+ 
				", 363698007 | finding site | = 113305005 | cerebellar structure |" +
				"}");
		// TODO: figure out expected short normal form
		Expression expectedShortNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"230696001 | posterior cerebral circulation infarction | :"+
				"{ 116676008 | associated morphology | = 55641003 | infarct |"+ 
				", 363698007 | finding site | = 113305005 | cerebellar structure |" +
				"}");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testCuttingToenails() {
		Expression originalExpression = (Expression) SCGStandaloneSetup.parse("229832002");
		Expression expectedLongNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"71388002 | procedure | :"+
				"{ 260686004 | method | = 360314001 | cutting - action |"+ 
				", 405813007 | procedure site - Direct | = 76578001 | structure of nail of toe |"+ 
				"}");
		// TODO: figure out expected short normal form
		Expression expectedShortNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"71388002 | procedure | :"+
				"{ 260686004 | method | = 360314001 | cutting - action |"+ 
				", 405813007 | procedure site - Direct | = 76578001 | structure of nail of toe |"+ 
				"}");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}
	
	@Test
	public void testRastelliOperation() {
		Expression originalExpression = (Expression) SCGStandaloneSetup.parse("18932005 | Rastelli operation in repair of transposition of great vessels |");
		Expression expectedLongNormalFormExpression = (Expression) SCGStandaloneSetup.parse(
				"18932005 | Rastelli operation in repair of transposition of great vessels | :"+
					"363702006 | has focus | = ( 71388002 | procedure | :"+
					"260686004 | method | = 129377008 | reconstruction - action | )"+
					"{ 260686004 | method | = 129284003 | surgical action |"+ 
					", 363704007 | procedure site | = 13418002 | structure of outflow tract of left ventricle |"+ 
					"}"+
					"{ 260686004 | method | = 129407005 | grafting - action |"+ 
					", 363700003 | direct morphology | = 32776006 | congenital vascular anomaly |"+ 
					", 405813007 | procedure site - Direct | = 15825003 | aortic structure |"+ 
					", 424226004 | using device | = 386028003 | surgical patch |"+ 
					"}"+
					"{ 260686004 | method | = 129407005 | grafting - action |"+ 
					", 363700003 | direct morphology | = 32776006 | congenital vascular anomaly |"+ 
					", 405813007 | procedure site - Direct | = 87878005 | left ventricular structure |"+ 
					", 424226004 | using device | = 386028003 | surgical patch |"+ 
					"}"+
					"{ 260686004 | method | = 257741005 | anastomosis - action |"+ 
					", 405813007 | procedure site - Direct | = 15825003 | aortic structure |"+ 
					"}"+
					"{ 260686004 | method | = 257741005 | anastomosis - action |"+ 
					", 405813007 | procedure site - Direct | = 87878005 | left ventricular structure |"+ 
					"}"+
					"{ 260686004 | method | = 257741005 | anastomosis - action |"+ 
					", 405814001 | procedure site - Indirect | = 44627009 | structure of outflow tract of right ventricle |"+ 
					"}"+
					"{ 260686004 | method | = 257903006 | repair - action |"+ 
					", 363700003 | direct morphology | = 6920004 | defect |"+ 
					", 405813007 | procedure site - Direct | = 589001 | interventricular septum structure |"+ 
					"}"+
					"{ 260686004 | method | = 257903006 | repair - action |"+ 
					", 405813007 | procedure site - Direct | = 34202007 | aortic valve structure |"+ 
					"}"+
					"{ 260686004 | method | = 360021005 | bypass - action |"+ 
					", 363700003 | direct morphology | = 32776006 | congenital vascular anomaly |"+ 
					", 405813007 | procedure site - Direct | = 53085002 | right ventricular structure |"+ 
					", 424226004 | using device | = 386028003 | surgical patch |"+ 
					"}"+
					"{ 260686004 | method | = 360021005 | bypass - action |"+ 
					", 363700003 | direct morphology | = 32776006 | congenital vascular anomaly |"+ 
					", 405813007 | procedure site - Direct | = 81040000 | pulmonary artery structuree |"+ 
					", 424226004 | using device | = 257283004 | valved cardiac conduit |"+ 
					"}");

		// TODO: short normal form probably wrong
		Expression expectedShortNormalFormExpression = (Expression) SCGStandaloneSetup.parse("18932005 | Rastelli operation in repair of transposition of great vessels |");
		testNormalFormGenerator(originalExpression, expectedLongNormalFormExpression, expectedShortNormalFormExpression);
	}	
	
	/**
	 * Recursively feeds the output of the normal form generators into the same generators for a given number of times.
	 * @see TestUtils#TEST_ITERATION_COUNT
	 * 
	 * @param originalExpression
	 * @param expectedLongNormalFormExpression
	 * @param expectedShortNormalFormExpression
	 */
	private void testNormalFormGenerator(Expression originalExpression, Expression expectedLongNormalFormExpression, 
			Expression expectedShortNormalFormExpression) {
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		RecursiveTerminologyBrowser<SnomedConceptIndexEntry,String> recursiveTerminologyBrowser = RecursiveTerminologyBrowser.create(terminologyBrowser);
		SnomedClientStatementBrowser statementBrowser = ApplicationContext.getInstance().getService(SnomedClientStatementBrowser.class);
		
		Expression longNormalFormTestOriginalExpression = originalExpression;
		List<Long> iterationTimesInNanoseconds = Lists.newArrayList();
		for (int i=0; i<TestUtils.TEST_ITERATION_COUNT; i++) {
			long iterationStart = System.nanoTime();
			ScgExpressionNormalFormGenerator normalFormGenerator = new ScgExpressionNormalFormGenerator(recursiveTerminologyBrowser, statementBrowser);
			Expression longNormalFormExpression = normalFormGenerator.getLongNormalForm(longNormalFormTestOriginalExpression);
			long iterationEnd = System.nanoTime();
			TestUtils.assertExpressionsEqual("Long normal form expression does not match,", expectedLongNormalFormExpression, longNormalFormExpression);
			iterationTimesInNanoseconds.add(iterationEnd-iterationStart);
			longNormalFormTestOriginalExpression = longNormalFormExpression;
		}
		TestUtils.printPerformanceStats("Long normal form generator test", iterationTimesInNanoseconds);
		
		Expression shortNormalFormTestOriginalExpression = originalExpression;
		iterationTimesInNanoseconds = Lists.newArrayList();
		for (int i=0; i<TestUtils.TEST_ITERATION_COUNT; i++) {
			long iterationStart = System.nanoTime();
			ScgExpressionNormalFormGenerator normalFormGenerator = new ScgExpressionNormalFormGenerator(recursiveTerminologyBrowser, statementBrowser);
			Expression shortNormalFormExpression = normalFormGenerator.getShortNormalForm(shortNormalFormTestOriginalExpression);
			long iterationEnd = System.nanoTime();
			TestUtils.assertExpressionsEqual("Short normal form expression does not match,", expectedShortNormalFormExpression, shortNormalFormExpression);
			iterationTimesInNanoseconds.add(iterationEnd-iterationStart);
			shortNormalFormTestOriginalExpression = shortNormalFormExpression;
		}
		TestUtils.printPerformanceStats("Short normal form generator test", iterationTimesInNanoseconds);
	}
}
