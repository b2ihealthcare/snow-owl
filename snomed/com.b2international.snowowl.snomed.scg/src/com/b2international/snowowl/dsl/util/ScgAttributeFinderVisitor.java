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
package com.b2international.snowowl.dsl.util;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.tree.TreeVisitor;
import com.b2international.commons.tree.emf.EObjectTreeNode;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Concept;
import com.google.common.collect.Lists;

/**
 * Extracts all matching attributes.
 * 
 * @param <T>
 */
public class ScgAttributeFinderVisitor<T extends IComponent<String>> implements TreeVisitor<EObjectTreeNode> {
	
	private final Logger logger = LoggerFactory.getLogger(ScgAttributeFinderVisitor.class);
	
	private final int maxScgExpressionDepth;
	private final String nameId;
	private final String valueId;
	private int scgExpressionDepth = 0;
	private List<Attribute> matchingAttributes = Lists.newArrayList();
	private final Collection<Attribute> attributesToIgnore;
	
	/**
	 * Class constructor. 
	 * <em>Note: set maxScgExpressionDepth to '2' for stopping at the clinical kernel's top expression level.</em>
	 * @param maxScgExpressionDepth the maximum depth of SCG subexpressions to recurse into.
	 * @param attributesToIgnore the attributes to ignore when traversing the expression
	 */
	public ScgAttributeFinderVisitor(final String nameId, final String valueId, final int maxScgExpressionDepth, final Collection<Attribute> attributesToIgnore) {
		this.nameId = nameId;
		this.valueId = valueId;
		this.maxScgExpressionDepth = maxScgExpressionDepth;
		this.attributesToIgnore = attributesToIgnore;
	}
	
	@Override
	public boolean visit(final EObjectTreeNode node) {
		if (node.getFeature() == null && node.getEObject() instanceof com.b2international.snowowl.dsl.scg.Attribute
				&& ((Attribute)node.getEObject()).getValue() instanceof Concept) {
			final Attribute attribute = (Attribute)node.getEObject();
			if (hasMatchingName(attribute) && hasMatchingValue(attribute) && !attributesToIgnore.contains(attribute)) {
				matchingAttributes.add(attribute);
			}
		}
		return true;
	}

	private boolean hasMatchingValue(final Attribute attribute) {
		return valueId.equals(((Concept) attribute.getValue()).getId());
	}

	private boolean hasMatchingName(final Attribute attribute) {
		return nameId.equals(attribute.getName().getId());
	}

	@Override
	public boolean entering(final EObjectTreeNode node) {
		if (node.getFeature() == null && node.getEObject() instanceof com.b2international.snowowl.dsl.scg.Expression) {
			// don't traverse full depth of expression tree
			if (scgExpressionDepth > maxScgExpressionDepth) {
				return false;
			}
			scgExpressionDepth++;
			logger.debug("Entering (depth=" + scgExpressionDepth +"): " + node.getEObject());
		}
		return true;
	}

	@Override
	public boolean leaving(final EObjectTreeNode node) {
		if (node.getFeature() == null && node.getEObject() instanceof com.b2international.snowowl.dsl.scg.Expression) {
			scgExpressionDepth--;
			logger.debug("Leaving (depth=" + scgExpressionDepth +"): " + node.getEObject());
		}
		return true;
	}

	/**
	 * @return all matching attributes
	 */
	public List<Attribute> getMatchingAttributes() {
		return matchingAttributes;
	}
}