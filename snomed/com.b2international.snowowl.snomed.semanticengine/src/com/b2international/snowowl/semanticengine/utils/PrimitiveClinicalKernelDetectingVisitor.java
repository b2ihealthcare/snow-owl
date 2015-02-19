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
import com.b2international.snowowl.dsl.scg.Expression;

/**
 * Visitor implementation that checks if a normalized SCG {@link Expression}'s
 * clinical kernel is composed entirely of primitive concepts (with no
 * additional attributes or groups).
 * 
 */
public class PrimitiveClinicalKernelDetectingVisitor implements TreeVisitor<EObjectTreeNode> {
	
	private boolean isClinicalKernelPrimitive = false;
	private boolean inClinicalKernel = false;

	@Override
	public boolean visit(EObjectTreeNode node) {
		if (node.getFeature() == null && node.getEObject() instanceof Attribute) {
			Attribute attribute = (Attribute) node.getEObject();
			if (attribute.getName().getId().equals(SemanticUtils.ASSOCIATED_FINDING_ID)
					|| attribute.getName().getId().equals(SemanticUtils.ASSOCIATED_PROCEDURE_ID)) {
				inClinicalKernel = true;
			}
		} else if (inClinicalKernel) {
			Attribute clinicalKernelAttribute = (Attribute) node.getEObject();
			if (clinicalKernelAttribute.getValue() instanceof Expression) {
				Expression clinicalKernelExpression = (Expression) clinicalKernelAttribute.getValue();
				if (clinicalKernelExpression.getAttributes().isEmpty() && clinicalKernelExpression.getGroups().isEmpty()) {
					isClinicalKernelPrimitive = true;
				}
				return false;
			} else if (clinicalKernelAttribute.getValue() instanceof Concept) {
				isClinicalKernelPrimitive = true;
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean entering(EObjectTreeNode node) {
		return true;
	}

	@Override
	public boolean leaving(EObjectTreeNode node) {
		return true;
	}

	/**
	 * @return <code>true</code> if the normalized context wrapper's clinical kernel
	 *         contains no refinements (just primitive concepts),
	 *         <code>false</code> otherwise
	 */
	public boolean isClinicalKernelPrimitive() {
		return isClinicalKernelPrimitive;
	}
}