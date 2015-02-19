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
package com.b2international.snowowl.semanticengine.utils;

import com.b2international.snowowl.dsl.scg.AttributeValue;
import com.b2international.snowowl.dsl.scg.Concept;


public class AttributeValueComparator extends ObjectComparator<AttributeValue> {

	@Override
	public boolean equal(AttributeValue expected, AttributeValue actual) {
		if (expected == null || actual == null)
			throw new NullPointerException("Null value not supported in LValueComparator.");
		if (expected instanceof Concept && actual instanceof Concept) {
			Concept expectedConcept = (Concept) expected;
			Concept actualConcept = (Concept) expected;
			String expectedConceptId = expectedConcept.getId();
			String actualConceptId = actualConcept.getId();
			return expectedConceptId.equals(actualConceptId);
		} else {
			throw new IllegalArgumentException("Invalid arguments: " + expected + ", " + actual);
		}
	}

}