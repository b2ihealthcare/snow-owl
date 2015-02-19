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
package com.b2international.snowowl.snomed.dsl.query.walker;

import java.util.ArrayList;
import java.util.List;

import com.b2international.commons.tree.TreeVisitor;
import com.b2international.commons.tree.emf.EObjectTreeNode;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;

public class ESCGExpressionVisitor implements TreeVisitor<EObjectTreeNode> {

	private List<RValue> ungrouped = new ArrayList<RValue>();
	private List<RValue> groups = new ArrayList<RValue>();
	private boolean isInGroup = false;
	
	@Override
	public boolean visit(EObjectTreeNode node) {
		
		// visit only whole nodes, not {eObject, feature} pairs
		if(node.getFeature() != null) {
			return true;
		}
		
		if(node.getEObject() instanceof AttributeClauseGroup) {
			isInGroup = true;
		}
		
		
		return true;
	}

	@Override
	public boolean entering(EObjectTreeNode node) {
		// visid only whole nodes, not {eObject, feature} pairs
		return node.getFeature() == null;
	}

	@Override
	public boolean leaving(EObjectTreeNode node) {

		
		if(node.getEObject() instanceof AttributeClauseGroup) {
			isInGroup = false;
			groups.add(((AttributeClauseGroup) node.getEObject()).getValue());
		
		}
		
		return true;
	}

}