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
package com.b2international.snowowl.snomed.mrcm.core.server.widget;

import java.util.Collection;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.mrcm.core.widget.ConceptModelConstraintToWidgetModelConverter;
import com.b2international.snowowl.snomed.mrcm.core.widget.IWidgetModelProvider;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;

public enum WidgetModelProvider implements IWidgetModelProvider {
	
	INSTANCE;

	@Override
	public ConceptWidgetModel createConceptWidgetModel(final IBranchPath branchPath, final Iterable<String> ruleParentIds, final String ruleRefSetId) {
		final Collection<SnomedConstraintDocument> applicablePredicates = predicateBrowser.getPredicates(branchPath, ruleParentIds, ruleRefSetId);
		return ConceptModelConstraintToWidgetModelConverter.processConstraints(branchPath, applicablePredicates);
	}
	
	@Override
	public ConceptWidgetModel createConceptWidgetModel(final IBranchPath branchPath, final String conceptId, final String ruleRefSetId) {
		final Collection<SnomedConstraintDocument> applicablePredicates = predicateBrowser.getPredicates(branchPath, conceptId, ruleRefSetId);
		return ConceptModelConstraintToWidgetModelConverter.processConstraints(branchPath, applicablePredicates);
	}		
}
 