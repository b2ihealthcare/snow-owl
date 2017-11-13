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
package com.b2international.snowowl.semanticengine.simpleast.subsumption.test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.xtext.parser.IParser;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;

/**
 * Performance test to compare performance of ESCG expression parser and EMF binary resource parser.
 * Does not require a full Snow Owl application to be initialized in the background.
 * 
 */
public class EscgVsBinaryResourcePerformanceTest {

	private static IParser escgParser;

	@BeforeClass
	public static void initSpecific() {
		escgParser = TestUtils.createESCGParser();
		// initialize parser, trying to avoid first parsing overhead when measuring performance
		escgParser.parse(new StringReader("")).getRootASTElement();
	}
	
	private void performanceTest(String escgExpression, String binaryResourceName) throws IOException {
		System.out.println("---------------------------------");
		long escgStartTime = System.nanoTime();
		ExpressionConstraint escgRootASTElement = TestUtils.parseExpression(escgExpression);
		long escgEstimatedTime = System.nanoTime() - escgStartTime;
		BinaryResourceImpl binaryResource = new BinaryResourceImpl(URI.createFileURI(binaryResourceName));
		binaryResource.getContents().add(escgRootASTElement);
		binaryResource.save(Collections.emptyMap());
		
		long binaryResourceStartTime = System.nanoTime();
		BinaryResourceImpl binaryResource2 = new BinaryResourceImpl(URI.createFileURI(binaryResourceName));
		binaryResource2.load(Collections.emptyMap());
		ExpressionConstraint binaryResourceFirstRootElement = (ExpressionConstraint) binaryResource2.getContents().get(0);
		long binaryResourceEstimatedTime = System.nanoTime() - binaryResourceStartTime;
		System.out.println("ESCG parser: " + escgEstimatedTime / 1000 + " us");
		System.out.println("Resulting expression: " + escgRootASTElement);
		System.out.println("Binary resource parser: " + binaryResourceEstimatedTime / 1000 + " us");
		System.out.println("Resulting expression: " + binaryResourceFirstRootElement);
		System.out.println("Performance difference: " + Math.round(((double)escgEstimatedTime / (double)binaryResourceEstimatedTime * 100))+ " %");
	}
	
	private void testExpressionPerformance() throws IOException {
		performanceTest("243796009:363589002=" +
				"17724006" + 
				",405813007=88727008" +
				",408729009=410515003" +
				",408731000=410512000" +
				",408732007=410604004", "escg1.bin");
		performanceTest("243796009:363589002=" + 
				"312813005" + 
				",405813007=244310007" +
				",408729009=410515003" +
				",408731000=410512000" +
				",408732007=410604004", "escg2.bin");
		performanceTest("243796009:246090004=(" +
				 "402138008:" + 
					"116676008=90120004" +
					",363714003=250467003)" +
		 		",408729009=410515003" +
				",408731000=410512000"+
				",408732007=410604004", "escg3.bin");
		performanceTest("243796009:246090004=(" +
				 "14560005:116676008="+
				 "419386004),"+
				 "408729009=410515003,"+
				 "408731000=410512000,"+
				 "408732007=410604004", "escg4.bin");
		performanceTest("243796009:246090004= (" + 
				"14560005:116676008=" +
					"415701004" +
					",363714003=386053000)," +
					 "408729009=410515003" +
					",408731000=410512000" +
					",408732007=410604004", "escg5.bin");
		performanceTest("243796009:246090004=(" +
				"402138008:116676008=" +
				"90120004" +
				",363714003=250467003)," +
				 "408729009=410515003" +
				",408731000=410512000" +
				",408732007=410604004", "escg6.bin");
		performanceTest("243796009:246090004=(" +
				"14560005:116676008=" +
				"415701004" +
				",363714003=386053000),"+
				 "408729009=410515003"+
				",408731000=410512000"+
				",408732007=125676002", "escg7.bin");
		performanceTest("243796009:246090004=("+
				"14560005:116676008=" +
					"415701004"+
					",363714003=386053000),"+
					 "408729009=410515003"+
					",408731000=410512000"+
					",408732007=410604004", "escg8.bin");
		performanceTest("243796009:{246090004=("+
				"14560005:116676008="+
					"415701004"+
					",363714003=386053000),"+
					 "408729009=410516002"+
					",408731000=410512000"+
					",408732007=410604004}", "escg9.bin");
		performanceTest("243796009:{246090004=("+
				"14560005:116676008="+
					"415701004"+
					",363714003=386053000),"+
					 "408729009=410594000"+
					", 408731000=410512000"+
					", 408732007=125676002}", "escg10.bin");
	}
	
	@Test
	public void test() throws IOException {
		System.out.println("Round 1:\n");
		testExpressionPerformance();
		System.out.println("\nRound 2:\n");
		testExpressionPerformance();
		System.out.println("\nRound 3:\n");
		testExpressionPerformance();
	}
}
