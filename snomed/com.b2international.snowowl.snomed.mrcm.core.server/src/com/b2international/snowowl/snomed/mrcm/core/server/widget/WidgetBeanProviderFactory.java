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

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.mrcm.core.configuration.SnomedSimpleTypeRefSetAttributeConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;

/**
 * Server side widget bean provider factory.
 * 
 */
public class WidgetBeanProviderFactory {

	/**
	 * Creates and returns a new widget bean provider.
	 * 
	 * @param branchPath the branch path
	 * @param conceptId the unique identifier of the concept
	 * @param conceptWidgetModel the concept widget model
	 * @param configuration the attribute order configuration
	 * 
	 * @return the new widget bean provider
	 */
	public WidgetBeanProvider createProvider(final IBranchPath branchPath, final String conceptId, 
			final ConceptWidgetModel conceptWidgetModel, final SnomedSimpleTypeRefSetAttributeConfiguration configuration, final boolean includeUnsanctioned, final boolean doSort) {
		return new WidgetBeanProvider(conceptId, conceptWidgetModel, branchPath, configuration, includeUnsanctioned, doSort);
	}
}