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
package com.b2international.snowowl.semanticengine.simpleast.normalform;

import java.util.List;

import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.google.common.collect.Lists;

/**
 * Simple class to hold attribute groups and ungrouped attributes that belong together.
 * 
 */
public final class ConceptDefinition {
	private final List<AttributeClause> ungroupedAttributes = Lists.newArrayList();
	private final List<AttributeClauseList> attributeGroups = Lists.newArrayList();
	
	public List<AttributeClauseList> getAttributeClauseLists() {
		return attributeGroups;
	}
	
	public List<AttributeClause> getUngroupedAttributes() {
		return ungroupedAttributes;
	}

	@Override
	public String toString() {
		return attributeGroups + ", " + getUngroupedAttributes();
	}
}