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

import com.b2international.commons.tree.TreeVisitor;
import com.b2international.commons.tree.emf.EObjectTreeNode;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Concept;

/**
 * Extracts the first occurrence of the specified attribute, limiting the traversal to a specified depth 
 * in the SCG expression.
 * 
 * @param <T>
 */
public class LimitedDepthAttributeValueExtractingVisitor implements TreeVisitor<EObjectTreeNode> {
	
	private int scgExpressionDepth = 0;
	private long attributeValueId = 0l;
	private final int maxScgExpressionDepth;
	private final String attributeNameId;
	
	/**
	 * Class constructor. 
	 * <em>Note: set maxScgExpressionDepth to '2' for stopping at the clinical kernel's top expression level.</em>
	 * 
	 * @param maxScgExpressionDepth the maximum depth of SCG subexpressions to recurse into.
	 */
	public LimitedDepthAttributeValueExtractingVisitor(String attributeNameId, int maxScgExpressionDepth) {
		this.attributeNameId = attributeNameId;
		this.maxScgExpressionDepth = maxScgExpressionDepth;
	}
	
	@Override
	public boolean visit(EObjectTreeNode node) {
		if (node.getFeature() == null && node.getEObject() instanceof com.b2international.snowowl.dsl.scg.Attribute
				&& ((Attribute)node.getEObject()).getName().getId().equals(attributeNameId)	
				&& ((Attribute)node.getEObject()).getValue() instanceof Concept) {
			Attribute attribute = (Attribute)node.getEObject();
			if (attributeValueId == 0l)
				attributeValueId = Long.parseLong(((Concept) attribute.getValue()).getId());
			return false;
		}
		return true;
	}

	@Override
	public boolean entering(EObjectTreeNode node) {
		if (node.getFeature() == null && node.getEObject() instanceof com.b2international.snowowl.dsl.scg.Expression) {
			// don't traverse full depth of expression tree
			if (scgExpressionDepth > maxScgExpressionDepth) {
				return false;
			}
			scgExpressionDepth++;
		}
		return true;
	}

	@Override
	public boolean leaving(EObjectTreeNode node) {
		if (node.getFeature() == null && node.getEObject() instanceof com.b2international.snowowl.dsl.scg.Expression) {
			scgExpressionDepth--;
		}
		return true;
	}

	public Long getAttributeValueId() {
		return attributeValueId;
	}
}