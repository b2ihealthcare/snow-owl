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
package com.b2international.snowowl.snomed.mrcm.core.server.validator;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipWidgetBean;

/**
 * Validator to check whether the concept has at least one stated IS_A relationship.
 * @since 4.4
 */
public class StatedIsARelationshipWidgetBeanValidator implements ModeledWidgetBeanValidator {

	public StatedIsARelationshipWidgetBeanValidator(SnomedTerminologyBrowser browser) {
	}

	@Override
	public void validate(IBranchPath branch, ConceptWidgetBean concept, ValidationStatusReporter reporter) {
		
		//SNOMED CT root concept is an exception, do nothing
		if (concept.getConceptId().equals(Concepts.ROOT_CONCEPT)) {
			return;
		}
		
		// ignore inactive concepts
		if (!concept.isActive()) {
			return;
		}
		
		Iterable<RelationshipWidgetBean> relationships = concept.getRelationships();
		
		int numberOfStatedIsARelationships = 0;
		
		//these are only active relationships by definition
		for (RelationshipWidgetBean relationshipWidgetBean : relationships) {
			if (relationshipWidgetBean.isIsA() && relationshipWidgetBean.getSelectedCharacteristicTypeId().equals(Concepts.STATED_RELATIONSHIP)) {
				numberOfStatedIsARelationships ++;
			}
		}
		if (numberOfStatedIsARelationships < 1) {
			reporter.error(concept, "The concept has no stated is-a relationships.");
		}
	}

}
