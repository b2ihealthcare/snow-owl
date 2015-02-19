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

import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeClauseList;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;

/**
 * {@link CollectionComparator} implementation to handle collections of {@link AttributeClause}s.
 * 
 */
public class AttributeGroupCollectionComparator extends CollectionComparator<AttributeClauseList> {

	@Override
	protected boolean itemsEqual(AttributeClauseList expected, AttributeClauseList actual) {
		return getItemDiff(expected, actual).isEmpty();
	}

	private CollectionComparator.CollectionDiff<AttributeClause> getItemDiff(AttributeClauseList expected, AttributeClauseList actual) {
		AttributeCollectionComparator attributeListMatcher = new AttributeCollectionComparator();
		return attributeListMatcher.getDiff(expected.getAttributeClauses(), actual.getAttributeClauses());
	}

}