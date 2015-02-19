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

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;

/**
 * {@link IClientWidgetBeanProvider Client side widget bean provider } factory.
 * 
 */
public class ClientWidgetBeanProviderFactory {
	
	/**
	 * Returns a widget bean provider, depending on the state of the {@link Concept concept} passed in.
	 * 
	 * @param conceptWidgetModel
	 * @param concept
	 * @return
	 */
	public IClientWidgetBeanProvider createProvider(final ConceptWidgetModel conceptWidgetModel, final Concept concept, final boolean includeUnsanctioned) {
		checkNotNull(conceptWidgetModel, "conceptWidgetModel");
		checkNotNull(concept, "concept");
		
		if (concept.cdoView().isDirty()) {
			return new CDOClientWidgetBeanProvider(conceptWidgetModel, concept, includeUnsanctioned);
		} else {
			return ApplicationContext.getInstance().getService(IClientWidgetBeanProvider.class);
		}
	}
}