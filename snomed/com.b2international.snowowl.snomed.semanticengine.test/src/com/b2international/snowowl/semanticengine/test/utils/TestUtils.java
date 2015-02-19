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
package com.b2international.snowowl.semanticengine.test.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.ComparisonFailure;

import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.semanticengine.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.utils.ConceptDefinitionComparator;
import com.b2international.snowowl.semanticengine.utils.ExpressionComparator;

/**
 * Various utility functions for testing.
 * 
 */
public class TestUtils {
	
	public static final int TEST_ITERATION_COUNT = 100;

	public static void printPerformanceStats(String message, List<Long> iterationTimesInNanoseconds) {
		Long max = Collections.max(iterationTimesInNanoseconds);
		Long min = Collections.min(iterationTimesInNanoseconds);
		
		System.out.format("%s: totalIterations=%d, min=%d us, max=%d us, avg=%d us.\n", message, iterationTimesInNanoseconds.size(),
				min/1000, max/1000, average(iterationTimesInNanoseconds)/1000);
	}
	
	private static long average(Collection<Long> values) {
		long avg = 0;
		for (Long value : values) {
			avg += value;
		}
		return avg / values.size();
	}
	
	public static void assertConceptDefinitionsEqual(ConceptDefinition expectedConceptDefinition,
			ConceptDefinition actualConceptDefinition) {
		ConceptDefinitionComparator conceptDefinitionComparator = new ConceptDefinitionComparator();
		boolean equal = conceptDefinitionComparator.equal(expectedConceptDefinition, actualConceptDefinition);
		
		if (!equal) {
			throw new ComparisonFailure("Concept definitions are not equal.", expectedConceptDefinition.toString(), actualConceptDefinition.toString());
		}
	}
	
	public static void assertExpressionsEqual(String message, Expression expected, Expression actual) {
		ExpressionComparator expressionComparator = new ExpressionComparator();
		boolean equal = expressionComparator.equal(expected, actual);
		if (!equal)
			throw new ComparisonFailure(message, expected.toString(), actual.toString());
	}
}
