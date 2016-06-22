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
package com.b2international.snowowl.snomed.mrcm.core.widget;

import java.util.Set;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;

/**
 * @deprecated - use {@link SnomedRequests#prepareGetApplicablePredicates(String, Set, Set, Set)} with proper parameters instead
 */
public interface IWidgetModelProvider {

	/**
	 * Computes the widget model for an already persisted concept, using {@code ruleRefSetId}, if given, for finding applicable MRCM rules. 
	 * Validation rules with the following domain definitions are taken into account:
	 * <ul>
	 * <li>self {@code "="}, self and subtypes {@code "<<"} for {@code conceptId};
	 * <li>self and subtypes {@code "<<"}, subtypes only {@code "<"} for any existing ancestor(s) of {@code conceptId};
	 * <li>reference set membership {@code "^"} for {@code ruleRefSetId} and any existing membership(s) of {@code conceptId};
	 * <li>relationship-based rules for {@code conceptId} only.
	 * </ul>
	 * 
	 * @param branchPath the branch path to use for creating the widget model
	 * @param conceptId the identifier of the concept to compute the model for
	 * @param ruleRefSetId additional reference set identifier, may be <code>null</code>
	 * @return the created {@link ConceptWidgetModel}
	 */
	ConceptWidgetModel createConceptWidgetModel(IBranchPath branchPath, String conceptId, String ruleRefSetId);
	
	/**
	 * Computes the widget model for a not yet persisted concept, using {@code ruleParentIds} and 
	 * {@code ruleRefSetId}, if given, for finding applicable MRCM rules. Validation rules with the following domain 
	 * definitions are taken into account:
	 * <ul>
	 * <li>self and subtypes {@code "<<"}, subtypes only {@code "<"} for {@code ruleParentIds};
	 * <li>reference set membership {@code "^"} for {@code ruleRefSetId} and any existing membership(s) of {@code conceptId}.
	 * </ul>
	 * 
	 * @param branchPath the branch path to use for creating the widget model
	 * @param ruleParentIds additional parent identifier(s)
	 * @param ruleRefSetId additional reference set identifier, may be <code>null</code>
	 * @return the created {@link ConceptWidgetModel}
	 */
	ConceptWidgetModel createConceptWidgetModel(IBranchPath branchPath, Iterable<String> ruleParentIds, String ruleRefSetId);
}