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

import javax.annotation.Nullable;

import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;

/**
 */
public interface IClientWidgetModelProvider {

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
	 * @param conceptId the identifier of the concept to compute the model for
	 * @param ruleRefSetId additional reference set identifier
	 * @return the created {@link ConceptWidgetModel}
	 */
	ConceptWidgetModel createConceptWidgetModel(String conceptId, @Nullable String ruleRefSetId);
	
	/**
	 * Computes the widget model for a not yet persisted concept, using {@code ruleParentIds} and 
	 * {@code ruleRefSetId}, if given, for finding applicable MRCM rules. Validation rules with the following domain 
	 * definitions are taken into account:
	 * <ul>
	 * <li>self and subtypes {@code "<<"}, subtypes only {@code "<"} for {@code ruleParentIds};
	 * <li>reference set membership {@code "^"} for {@code ruleRefSetId} and any existing membership(s) of {@code conceptId}.
	 * </ul>
	 * 
	 * @param ruleParentIds additional parent identifier(s)
	 * @param ruleRefSetId additional reference set identifier
	 * @return the created {@link ConceptWidgetModel}
	 */
	ConceptWidgetModel createConceptWidgetModel(Iterable<String> ruleParentIds, @Nullable String ruleRefSetId);
}