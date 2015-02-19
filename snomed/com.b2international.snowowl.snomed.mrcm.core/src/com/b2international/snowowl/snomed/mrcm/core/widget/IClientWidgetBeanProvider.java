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

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.snomed.mrcm.core.configuration.SnomedSimpleTypeRefSetAttributeConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;

/**
 * Client side interface for widget bean providers.
 * 
 */
public interface IClientWidgetBeanProvider {
	
	/**
	 * Builds and returns the concept widget bean.
	 * 
	 * @param conceptId the unique identifier of the concept
	 * @param widgetModel the widget model
	 * @param configuration the attribute configuration
	 * @param monitor the progress monitor
	 * @return the concept widget bean
	 */
	public ConceptWidgetBean createConceptWidgetBean(final String conceptId, final ConceptWidgetModel widgetModel, final SnomedSimpleTypeRefSetAttributeConfiguration configuration, final boolean includeUnsanctioned, final IProgressMonitor monitor);
}