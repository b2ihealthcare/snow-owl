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
package com.b2international.snowowl.semanticengine.simpleast.subsumption.test;

import java.util.Collection;

import org.junit.Test;

import com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;

/**
 */
public class SubsumptionTesterTest {

	private static final int TEST_ITERATION_COUNT = 1000;
	
	private void testExpressionSubsumption(ExpressionConstraint predicate, ExpressionConstraint candidate, boolean expectedResult) {
		throw new UnsupportedOperationException("TODO implement me");
//		ESCGEcoreRewriter rewriter = new ESCGEcoreRewriter(escgParser);
//		RValue rewrittenPredicate = rewriter.rewrite(predicate);
//		RValue rewrittenCandidate = rewriter.rewrite(candidate);
//		List<Long> iterationTimesInNanoseconds = Lists.newArrayList();
//		for (int i=0; i<TEST_ITERATION_COUNT; i++) {
//			long iterationStart = System.nanoTime();
//			SubsumptionTester subsumptionTest = new SubsumptionTester(Branch.MAIN_PATH);
//			boolean subsumed = subsumptionTest.isSubsumed(rewrittenPredicate, rewrittenCandidate);
//			long iterationEnd = System.nanoTime();
//			Assert.assertEquals(expectedResult, subsumed);
//			iterationTimesInNanoseconds.add(iterationEnd-iterationStart);
//		}
//		Long max = Collections.max(iterationTimesInNanoseconds);
//		Long min = Collections.min(iterationTimesInNanoseconds);
//		
//		System.out.format("%d subsumption tests, min=%d, max=%s, avg=%d us.\n", TEST_ITERATION_COUNT,
//				min/1000, max/1000, average(iterationTimesInNanoseconds)/1000);
	}
	
	private long average(Collection<Long> values) {
		long avg = 0;
		for (Long value : values) {
			avg += value;
		}
		return avg / values.size();
	}
	
	@Test
	public void testUngroupedAttributes() {
		long parseStart = System.nanoTime();
		/*
		 * "243796009|Situation with explicit context| : 363589002|Associated procedure| =
		 *		17724006|Continuous wave Doppler analogue wave form analysis of upper extremity arteries|
		 *		, 405813007|Procedure site - Direct| = 88727008|Entire left axillary artery|
		 *		, 408729009|finding context| = 410515003|known present|
		 *		, 408731000|temporal context| = 410512000|current or specified|
		 *		, 408732007|subject relationship context| = 410604004|subject of record| " 
		 */
		ExpressionConstraint candidateExpression = TestUtils.parseExpression("243796009:363589002=" +
				"17724006" + 
				",405813007=88727008" +
				",408729009=410515003" +
				",408731000=410512000" +
				",408732007=410604004");
		
		/*
		 * "243796009|Situation with explicit context| : 363589002|Associated procedure| =
		 *		312813005|Ultrasound scan of upper limb arteries|
		 *		, 405813007|Procedure site - Direct| = 244310007|Entire artery of upper extremity|
		 *		, 408729009|finding context| = 410515003|known present|
		 *		, 408731000|temporal context| = 410512000|current or specified|
		 *		, 408732007|subject relationship context| = 410604004|subject of record|" 
		 */
		ExpressionConstraint predicateExpression = TestUtils.parseExpression("243796009:363589002=" + 
				"312813005" + 
				",405813007=244310007" +
				",408729009=410515003" +
				",408731000=410512000" +
				",408732007=410604004");
		long parseEnd = System.nanoTime();
		System.out.println("Expression parsing: " + ((parseEnd - parseStart)/1000) + " us");
		testExpressionSubsumption(predicateExpression, candidateExpression, true);
		testExpressionSubsumption(candidateExpression, predicateExpression, false);
	}

	@Test
	public void testExpressionAttributeValue1() {
		long parseStart = System.nanoTime();
		/*
		 * "243796009|Situation with explicit context| : 246090004|Associated finding| = (
		 *		402138008|Disseminated cutaneous mycosis| : 
		 *			116676008|Associated morphology| = 90120004|Mycosis fungoides|
		 *			, 363714003|Interprets| = 250467003|Determination of growth of fungi|)
		 *		, 408729009|finding context| = 410515003|known present|
		 *		, 408731000|temporal context| = 410512000|current or specified|
		 *		, 408732007|subject relationship context| = 410604004|subject of record| "
		 */
		ExpressionConstraint predicateExpression = TestUtils.parseExpression("243796009:246090004=(" +
				 "402138008:" + 
						"116676008=90120004" +
						",363714003=250467003)" +
			 		",408729009=410515003" +
					",408731000=410512000"+
					",408732007=410604004");
		
		/* 
		 * "243796009|Situation with explicit context| : 246090004|Associated finding| = (
		 * 		14560005|Dermal mycosis| : 116676008|Associated morphology| =
		 * 			419386004|Pagetoid reticulosis| ),
		 *		408729009|finding context| = 410515003|known present|,
		 *		408731000|temporal context| = 410512000|current or specified|,
		 *		408732007|subject relationship context| = 410604004|subject of record| "
		 */
		ExpressionConstraint candidateExpression = TestUtils.parseExpression("243796009:246090004=(" +
				 "14560005:116676008="+
					 "419386004),"+
					 "408729009=410515003,"+
					 "408731000=410512000,"+
					 "408732007=410604004");
	
		long parseEnd = System.nanoTime();
		System.out.println("Expression parsing: " + ((parseEnd - parseStart)/1000) + " us");
		testExpressionSubsumption(predicateExpression, candidateExpression, false);
	}

	@Test
	public void testExpressionAttributeValue2() {
		long parseStart = System.nanoTime();
		/*
		 * "243796009|Situation with explicit context| : 246090004|Associated finding| = (
		 *		14560005|Dermal mycosis| : 116676008|Associated morphology| =
		 * 		415701004|T-cell AND/OR NK-cell neoplasm|
		 * 		, 363714003|Interprets| = 386053000|Evaluation procedure|),
		 * 		408729009|finding context| = 410515003|known present|
		 * 		, 408731000|temporal context| = 410512000|current or specified|
		 * 		, 408732007|subject relationship context| = 410604004|subject of record| "
		 */
		ExpressionConstraint predicateExpression = TestUtils.parseExpression("243796009:246090004= (" + 
				"14560005:116676008=" +
					"415701004" +
					",363714003=386053000)," +
					 "408729009=410515003" +
					",408731000=410512000" +
					",408732007=410604004");
		
		/* 
		 * "243796009|Situation with explicit context| : 246090004|Associated finding| = (
		 * 		402138008|Disseminated cutaneous mycosis| : 116676008|Associated morphology| =
		 * 		90120004|Mycosis fungoides|
		 * 		, 363714003|Interprets| = 250467003|Determination of growth of fungi|),
		 * 		408729009|finding context| = 410515003|known present|
		 * 		, 408731000|temporal context| = 410512000|current or specified|
		 * 		, 408732007|subject relationship context| = 410604004|subject of record| "
		 */
		ExpressionConstraint candidateExpression = TestUtils.parseExpression("243796009:246090004=(" +
				"402138008:116676008=" +
					"90120004" +
					",363714003=250467003)," +
					 "408729009=410515003" +
					",408731000=410512000" +
					",408732007=410604004");
		
		long parseEnd = System.nanoTime();
		System.out.println("Expression parsing: " + ((parseEnd - parseStart)/1000) + " us");
		testExpressionSubsumption(predicateExpression, candidateExpression, true);
	}

	@Test
	public void testExpressionAttributeValue3() {
		long parseStart = System.nanoTime();
		/*
		 * "243796009|Situation with explicit context| : 246090004|Associated finding| = (
		 *		14560005|Dermal mycosis| : 116676008|Associated morphology| =
		 * 		415701004|T-cell AND/OR NK-cell neoplasm|
		 * 		, 363714003|Interprets| = 386053000|Evaluation procedure|),
		 * 		408729009|finding context| = 410515003|known present|
		 * 		, 408731000|temporal context| = 410512000|current or specified|
		 * , 408732007|subject relationship context| =125676002|Person| "		 */
		ExpressionConstraint predicateExpression = TestUtils.parseExpression("243796009:246090004=(" +
				"14560005:116676008=" +
					"415701004" +
					",363714003=386053000),"+
					 "408729009=410515003"+
					",408731000=410512000"+
					",408732007=125676002");
		
		/* 
		 * "243796009|Situation with explicit context| : 246090004|Associated finding| = (
		 * 		14560005|Dermal mycosis| : 116676008|Associated morphology| =
		 * 		415701004|T-cell AND/OR NK-cell neoplasm|
		 * 		, 363714003|Interprets| = 386053000|Evaluation procedure|),
		 * 		408729009|finding context| = 410515003|known present|
		 * 		, 408731000|temporal context| = 410512000|current or specified|
		 * 		, 408732007|subject relationship context| = 410604004|subject of record| "
		 */
		ExpressionConstraint candidateExpression = TestUtils.parseExpression("243796009:246090004=("+
				"14560005:116676008=" +
					"415701004"+
					",363714003=386053000),"+
					 "408729009=410515003"+
					",408731000=410512000"+
					",408732007=410604004");
		
		
		long parseEnd = System.nanoTime();
		System.out.println("Expression parsing: " + ((parseEnd - parseStart)/1000) + " us");
		testExpressionSubsumption(predicateExpression, candidateExpression, true);
	}

	@Test
	public void testKnownAbsent() {
		long parseStart = System.nanoTime();
		/*
		 * "243796009|Situation with explicit context| : 246090004|Associated finding| = (
		 * 		14560005|Dermal mycosis| : 116676008|Associated morphology| =
		 * 		415701004|T-cell AND/OR NK-cell neoplasm|
		 * 		, 363714003|Interprets| = 386053000|Evaluation procedure|),
		 * 		408729009|finding context| = 410516002|Known absent|
		 * 		, 408731000|temporal context| = 410512000|current or specified|
		 * 		, 408732007|subject relationship context| =410604004|subject of record| "
		 */
		ExpressionConstraint predicateExpression = TestUtils.parseExpression("243796009:{246090004=("+
				"14560005:116676008="+
					"415701004"+
					",363714003=386053000),"+
					 "408729009=410516002"+
					",408731000=410512000"+
					",408732007=410604004}");
		
		/* 
		 * "243796009|Situation with explicit context| : 246090004|Associated finding| = (
		 * 		14560005|Dermal mycosis| : 116676008|Associated morphology| =
		 * 		415701004|T-cell AND/OR NK-cell neoplasm|
		 * 		, 363714003|Interprets| = 386053000|Evaluation procedure|),
		 * 		408729009|finding context| = 410594000|Definitely not present|
		 * 		, 408731000|temporal context| = 410512000|current or specified|
		 * 		, 408732007|subject relationship context| =125676002|Person| "
		 */
		ExpressionConstraint candidateExpression = TestUtils.parseExpression("243796009:{246090004=("+
				"14560005:116676008="+
					"415701004"+
					",363714003=386053000),"+
					 "408729009=410594000"+
					", 408731000=410512000"+
					", 408732007=125676002}");
		
		long parseEnd = System.nanoTime();
		System.out.println("Expression parsing: " + ((parseEnd - parseStart)/1000) + " us");
		testExpressionSubsumption(predicateExpression, candidateExpression, true);
	}
	
	@Test
	public void testFullyDefinedAttributeValues() {
		long parseStart = System.nanoTime();
		/*
		 * "243796009|Situation with explicit context| : {246090004|Associated finding| = (
		 * 		14560005|Dermal mycosis| : 116676008|Associated morphology| =111214005|Arthropathy associated with a mycosis|
		 *		, 363714003|Interprets| = 386053000|Evaluation procedure|),
 		 * 		408729009|finding context| = 410515003|known present|
 		 * , 408731000|temporal context| = 410512000|current or specified|
 		 * , 408732007|subject relationship context| =125676002|Person| }"
		 */
		ExpressionConstraint predicateExpression = TestUtils.parseExpression("243796009:{246090004=("+
				"14560005:116676008=111214005"+
				",363714003=386053000),"+
				"408729009=410515003"+
				",408731000=410512000"+
				",408732007=125676002}");
		
		/* 
		 * "243796009|Situation with explicit context| : {246090004|Associated finding| = (
		 * 		14560005|Dermal mycosis| : 116676008|Associated morphology| =111212009|Arthropathy associated with bacterial disease|
		 * 		, 363714003|Interprets| = 386053000|Evaluation procedure|),
		 *		408729009|finding context| = 410515003|known present|
		 *		, 408731000|temporal context| = 410512000|current or specified|
		 *		, 408732007|subject relationship context| =125676002|Person| }"
		 */
		ExpressionConstraint candidateExpression = TestUtils.parseExpression("243796009:{246090004=("+
				"14560005:116676008=111212009"+
				",363714003= 386053000),"+
				"408729009=410515003"+
				",408731000=410512000"+
				",408732007=125676002}");
		
		long parseEnd = System.nanoTime();
		System.out.println("Expression parsing: " + ((parseEnd - parseStart)/1000) + " us");
		testExpressionSubsumption(predicateExpression, candidateExpression, false);
	}
	
	@Test
	public void testFullyDefinedAttributeValuesKnownAbsent() {
		long parseStart = System.nanoTime();
		/*
		 * "243796009|Situation with explicit context| : { 246090004|Associated finding| = (
		 *		14560005|Dermal mycosis| : 116676008|Associated morphology| =
		 *		111214005|Arthropathy associated with a mycosis|
		 *		, 363714003|Interprets| = 231428006|Cryotherapy to dorsal root ganglion|),
 		 *		408729009|finding context| = 410516002|Known absent|
		 *		, 408731000|temporal context| = 410512000|current or specified|
		 *		, 408732007|subject relationship context| =410604004|subject of record| }"
		 */
		ExpressionConstraint predicateExpression = TestUtils.parseExpression("243796009:{246090004=("+
				"14560005:116676008="+
					"111214005"+
					",363714003=231428006),"+
					 "408729009=410516002"+
					",408731000=410512000"+
					",408732007=410604004}");
		
		/* 
		 * "243796009|Situation with explicit context| : { 246090004|Associated finding| = (
		 * 		14560005|Dermal mycosis| : 116676008|Associated morphology| =
		 *		111214005|Arthropathy associated with a mycosis|
		 *		, 363714003|Interprets| = 231428006|Cryotherapy to dorsal root ganglion|),
 		 *		408729009|finding context| = 410516002|Known absent|
		 *		, 408731000|temporal context| = 410512000|current or specified|
		 *		, 408732007|subject relationship context| =125676002|Person| }"
		 */
		ExpressionConstraint candidateExpression = TestUtils.parseExpression("243796009:{246090004=("+
				"14560005:116676008="+
					"111214005"+
					",363714003=231428006),"+
					 "408729009=410516002"+
					",408731000=410512000"+
					",408732007=125676002}");
		
		long parseEnd = System.nanoTime();
		System.out.println("Expression parsing: " + ((parseEnd - parseStart)/1000) + " us");
		testExpressionSubsumption(predicateExpression, candidateExpression, true);
	}

	@Test
	public void testMultipleFocusConcepts() {
		long parseStart = System.nanoTime();
		
		/* 
		 * "243796009|Situation with explicit context| : { 246090004|Associated finding| = (
		 *		14560005|Dermal mycosis| +312646002|Burn of skin of eye region|),
 		 *		408729009|finding context| =410515003|known present|
		 *		, 408731000|temporal context| = 410512000|current or specified|
		 * 		, 408732007|subject relationship context| =410604004|subject of record| }"
		 */
		ExpressionConstraint predicateExpression = TestUtils.parseExpression("243796009:{246090004=("+
				"14560005+312646002),"+
				"408729009=410515003"+
				",408731000=410512000"+
				",408732007=410604004}");
		
		/*
		 * "243796009|Situation with explicit context| : { 246090004|Associated finding| = (
		 *		63041002|Acladiosis| +312646002|Burn of skin of eye region|),
 		 * 		408729009|finding context| =410515003|known present|
		 *		, 408731000|temporal context| = 410512000|current or specified|
		 * 		, 408732007|subject relationship context| =410604004|subject of record| }"
		 */
		ExpressionConstraint candidateExpression = TestUtils.parseExpression("243796009:{246090004=("+
				"63041002+312646002),"+
				 "408729009=410515003"+
				",408731000=410512000"+
				",408732007=410604004}");
		
		long parseEnd = System.nanoTime();
		System.out.println("Expression parsing: " + ((parseEnd - parseStart)/1000) + " us");
		testExpressionSubsumption(predicateExpression, candidateExpression, true);
	}
	
	@Test
	public void testInactiveConcept() {
		long parseStart = System.nanoTime();
		
		/* 
		 * "243796009|Situation with explicit context| : {246090004|Associated finding| =
		 *		(71620000 | fracture of femur |: 116676008 | associated morphology | = 
		 *			(123735002|Fracture with displacement| :363698007 | finding site | = 71341001 | bone structure of femur |))
		 *		, 408729009|finding context| = 410515003|known present|
		 * 		, 408731000|temporal context| = 410512000|current or specified|
		 * 		, 408732007|subject relationship context| = 410604004|subject of record| }"
		 */
		ExpressionConstraint predicateExpression = TestUtils.parseExpression("243796009:{246090004="+
				"(71620000:116676008=(123735002:363698007=71341001))"+
				",408729009=410515003"+
				",408731000=410512000"+
		",408732007=410604004}");
		
		/*
		 * "243796009|Situation with explicit context| : {246090004|Associated finding| =
		 * 		(71620000 | fracture of femur |:116676008 | associated morphology | = 
		 * 			(134341006 | displaced fracture | :363698007 | finding site | = 71341001 | bone structure of femur |))
		 * 		, 408729009|finding context| = 410515003|known present|
		 * 		, 408731000|temporal context| = 410512000|current or specified|
		 *		, 408732007|subject relationship context| = 410604004|subject of record| }"		 
		 */
		ExpressionConstraint candidateExpression = TestUtils.parseExpression("243796009:{246090004="+
				"(71620000:116676008=(134341006:363698007=71341001))"+
				",408729009=410515003"+
				",408731000=410512000"+
				",408732007=410604004}");
		
		long parseEnd = System.nanoTime();
		System.out.println("Expression parsing: " + ((parseEnd - parseStart)/1000) + " us");
		testExpressionSubsumption(predicateExpression, candidateExpression, false);
	}
}
