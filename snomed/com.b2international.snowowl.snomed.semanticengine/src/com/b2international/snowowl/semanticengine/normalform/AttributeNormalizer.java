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
package com.b2international.snowowl.semanticengine.normalform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.dsl.scg.ScgFactory;
import com.b2international.snowowl.semanticengine.utils.SemanticUtils;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

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
	
	private final IClientTerminologyBrowser<SnomedConceptIndexEntry, String> terminologyBrowser;
	private final ScgExpressionNormalFormGenerator normalFormGenerator;
	
	public AttributeNormalizer(IClientTerminologyBrowser<SnomedConceptIndexEntry, String> terminologyBrowser, SnomedClientStatementBrowser statementBrowser) {
		this.terminologyBrowser = terminologyBrowser;
		normalFormGenerator = new ScgExpressionNormalFormGenerator(terminologyBrowser, statementBrowser);
	}

	/**
	 * @param Groups
	 * @param ungroupedAttributes
	 * @return the normalized concept definition
	 */
	public ConceptDefinition normalizeAttributes(List<Group> Groups, List<Attribute> ungroupedAttributes) {
		// attribute groups
		Collection<Group> normalizedGroups = new ArrayList<Group>();
		for (Group group : Groups) {
			Collection<Attribute> normalizedAttributes = new ArrayList<Attribute>();
			for (Attribute attribute : group.getAttributes()) {
				Attribute normalizedAttribute = normalizeAttribute(attribute);
				normalizedAttributes.add(normalizedAttribute);
			}
			Group normalizedGroup = ScgFactory.eINSTANCE.createGroup();
			normalizedGroup.getAttributes().addAll(normalizedAttributes);
			normalizedGroups.add(normalizedGroup);
		}
	
		// ungrouped attributes
		Collection<Attribute> normalizedUngroupedAttributes = new ArrayList<Attribute>();
		for (Attribute attribute : ungroupedAttributes) {
			normalizedUngroupedAttributes.add(normalizeAttribute(attribute));
		}
		
		ConceptDefinition normalizedAttributes = new ConceptDefinition();
		normalizedAttributes.getGroups().addAll(normalizedGroups);
		normalizedAttributes.getUngroupedAttributes().addAll(normalizedUngroupedAttributes);
		return normalizedAttributes;
	}

	private Attribute normalizeAttribute(Attribute attribute) {
		Expression valueExpression = SemanticUtils.getAttributeValueExpression(attribute);
		Expression normalFormValueExpression = normalFormGenerator.getLongNormalForm(valueExpression);
		Attribute normalizedAttribute = ScgFactory.eINSTANCE.createAttribute();
		normalizedAttribute.setValue(SemanticUtils.buildRValue(normalFormValueExpression));
		normalizedAttribute.setName(EcoreUtil.copy(attribute.getName()));
		return normalizedAttribute;
	}

}