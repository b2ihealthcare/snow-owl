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
package com.b2international.snowowl.semanticengine.simpleast.utils;

import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinition;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;

/**
 * An {@link ObjectComparator} implementation to handle {@link ConceptDefinition}s.
 * 
 */
public class ExpressionComparator extends ObjectComparator<RValue> {

	@Override
	public boolean equal(RValue expected, RValue actual) {
		// TODO Auto-generated method stub
//		LValueCollectionComparator lValueCollectionComparator = new LValueCollectionComparator();
//		boolean lValuesEqual = lValueCollectionComparator.equal(expected.getLValues(), actual.getLValues());
//		RefinementsComparator refinementsComparator = new RefinementsComparator();
//		boolean refinementsEqual = refinementsComparator.equal(expected.getRefinements(), actual.getRefinements());
//		return lValuesEqual && refinementsEqual;

		return expected.toString().equals(actual.toString());
	}
}