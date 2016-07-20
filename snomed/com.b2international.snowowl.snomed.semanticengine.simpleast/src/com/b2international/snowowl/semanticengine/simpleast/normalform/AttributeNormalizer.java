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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.snowowl.semanticengine.simpleast.utils.QueryAstUtils;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastFactory;

/**
 * The value of every attribute specified in the expression refinement (including grouped 
 * and ungrouped attributes) is treated as an expression and normalized according to the 
 * full set of rules in section 5.3. 
 * To ensure depth-first processing, this recursive process is carried out before any 
 * other processing of the expression refinement.<br/>
 * Recursive normalization should be applied to all values even if they are 
 * represented by single conceptIds.
 * When all attribute values in the expression refinement have been processed, the 
 * refinement is passed to the "Merge refinement" process (5.3.5).
 * 
 */
public class AttributeNormalizer {
	
	private final SimpleAstExpressionNormalFormGenerator normalFormGenerator;
	
	public AttributeNormalizer(SnomedClientTerminologyBrowser terminologyBrowser) {
		normalFormGenerator = new SimpleAstExpressionNormalFormGenerator(terminologyBrowser);
	}

	/**
	 * @param attributeGroups
	 * @param ungroupedAttributes
	 * @return the normalized concept definition
	 */
	public ConceptDefinition normalizeAttributes(Collection<AttributeClauseList> attributeGroups, Collection<AttributeClause> ungroupedAttributes) {
		// attribute groups
		Collection<AttributeClauseList> normalizedAttributeClauseGroups = new ArrayList<AttributeClauseList>();
		for (AttributeClauseList attributeGroup : attributeGroups) {
			Collection<AttributeClause> normalizedAttributes = new ArrayList<AttributeClause>();
			for (AttributeClause attribute : attributeGroup.getAttributeClauses()) {
				AttributeClause normalizedAttribute = normalizeAttribute(attribute);
				normalizedAttributes.add(normalizedAttribute);
			}
			AttributeClauseList normalizedAttributeClauseGroup = new AttributeClauseList();
			normalizedAttributeClauseGroup.getAttributeClauses().addAll(normalizedAttributes);
			normalizedAttributeClauseGroups.add(normalizedAttributeClauseGroup);
		}
	
		// ungrouped attributes
		Collection<AttributeClause> normalizedUngroupedAttributes = new ArrayList<AttributeClause>();
		for (AttributeClause attribute : ungroupedAttributes) {
			normalizedUngroupedAttributes.add(normalizeAttribute(attribute));
		}
		
		ConceptDefinition normalizedAttributes = new ConceptDefinition();
		normalizedAttributes.getAttributeClauseLists().addAll(normalizedAttributeClauseGroups);
		normalizedAttributes.getUngroupedAttributes().addAll(normalizedUngroupedAttributes);
		return normalizedAttributes;
	}

	private AttributeClause normalizeAttribute(AttributeClause attribute) {
		RValue value = QueryAstUtils.getAttributeValueExpression(attribute);
		RValue normalFormValueExpression = normalFormGenerator.getLongNormalForm(value);
		AttributeClause normalizedAttribute = ecoreastFactory.eINSTANCE.createAttributeClause();
		if (normalFormValueExpression instanceof ConceptRef) {
			normalizedAttribute.setRight(normalFormValueExpression);
		} else {
			SubExpression valueSubExpression = ecoreastFactory.eINSTANCE.createSubExpression();
			valueSubExpression.setValue(normalFormValueExpression);
			normalizedAttribute.setRight(valueSubExpression);
		}
		normalizedAttribute.setLeft(EcoreUtil.copy(attribute.getLeft()));
		return normalizedAttribute;
	}

}