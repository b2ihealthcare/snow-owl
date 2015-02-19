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

import java.text.MessageFormat;

import com.b2international.commons.tree.TreeVisitor;
import com.b2international.commons.tree.emf.EObjectTreeNode;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Concept;

/**
 * Visitor implementation to extract the value of the first occurrence of an attribute with a specific name.
 */
public class AttributeValueExtractingVisitor implements TreeVisitor<EObjectTreeNode> {
	
	private final String attributeNameId;
	private String extractedAttributeValueId;

	public AttributeValueExtractingVisitor(String attributeNameId) {
		this.attributeNameId = attributeNameId;
	}

	@Override
	public boolean visit(EObjectTreeNode node) {
		if (node.getEObject() instanceof Attribute) {
			Attribute attribute = (Attribute) node.getEObject();
			if (attribute.getName().getId().equals(attributeNameId)) {
				if (attribute.getValue() instanceof Concept) {
					Concept valueConcept = (Concept) attribute.getValue();
					extractedAttributeValueId = valueConcept.getId();
					return false;
				} else {
					throw new IllegalArgumentException(MessageFormat.format(
							"Context wrapper attribute with name {0} has an expression value.", attributeNameId));
				}
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
	 * @return the extracted attribute value, or null if specified attribute not found
	 */
	public String getExtractedAttributeValueId() {
		return extractedAttributeValueId;
	}
}