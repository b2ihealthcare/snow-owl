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

import com.b2international.commons.tree.TreeVisitor;
import com.b2international.commons.tree.emf.EObjectTreeNode;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;

public class AstConceptLabelingVisitor implements TreeVisitor<EObjectTreeNode> {

	private final IClientTerminologyBrowser<? extends IComponent<String>, String> terminologyBrowser;
	
	public AstConceptLabelingVisitor(IClientTerminologyBrowser<? extends IComponent<String>, String> terminologyBrowser) {
		this.terminologyBrowser = terminologyBrowser;
	}

	@Override
	public boolean visit(EObjectTreeNode node) {
		return true;
	}

	@Override
	public boolean entering(EObjectTreeNode node) {

		if (node.getFeature() == null && node.getEObject() instanceof ConceptRef) {
			ConceptRef conceptRef = (ConceptRef) node.getEObject();
			if(conceptRef.getLabel() == null) {
				IComponent<String> concept = terminologyBrowser.getConcept(conceptRef.getConceptId());
				if(concept != null) {
					conceptRef.setLabel(concept.getLabel());
				}
			}
		}
		
		return true;
	}

	@Override
	public boolean leaving(EObjectTreeNode node) {
		return true;
	}
}