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

import java.util.List;

import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Expression;


public class RefinementsComparator extends ObjectComparator<Expression> {

	@Override
	public boolean equal(Expression expected, Expression actual) {
		if (expected == null && actual == null)
			return true;
		
//		List<Attribute> expectedUngroupedAttributes = SemanticUtils.getUngroupedAttributes(expected);
//		List<Attribute> actualUngroupedAttributes = SemanticUtils.getUngroupedAttributes(actual);
		List<Attribute> expectedUngroupedAttributes = expected.getAttributes();
		List<Attribute> actualUngroupedAttributes = actual.getAttributes();

		if (SemanticUtils.isNullOrEmpty(expectedUngroupedAttributes) && SemanticUtils.isNullOrEmpty(actualUngroupedAttributes))
			return true;
		if (SemanticUtils.isNullOrEmpty(expectedUngroupedAttributes) && !SemanticUtils.isNullOrEmpty(actualUngroupedAttributes))
			return false;
		if (!SemanticUtils.isNullOrEmpty(expectedUngroupedAttributes) && SemanticUtils.isNullOrEmpty(actualUngroupedAttributes))
			return false;
		
		GroupCollectionComparator GroupCollectionComparator = new GroupCollectionComparator();
		boolean groupsEqual = GroupCollectionComparator.equal(expected.getGroups(),	actual.getGroups());
		AttributeCollectionComparator attributeCollectionComparator = new AttributeCollectionComparator();
		boolean ungroupedAttributesEqual = attributeCollectionComparator.equal(expectedUngroupedAttributes, 
				actualUngroupedAttributes);
		return groupsEqual && ungroupedAttributesEqual;
	}
}