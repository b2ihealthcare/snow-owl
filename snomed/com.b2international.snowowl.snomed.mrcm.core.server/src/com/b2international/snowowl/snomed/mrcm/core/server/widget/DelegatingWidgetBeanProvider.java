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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.mrcm.core.configuration.SnomedSimpleTypeRefSetAttributeConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.widget.IWidgetBeanProvider;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;

/**
 * Server side widget bean provider implementation, which creates a delegate {@link WidgetBeanProvider} and calls it.
 * 
 */
public class DelegatingWidgetBeanProvider implements IWidgetBeanProvider {

	@Override
	public ConceptWidgetBean createConceptWidgetBean(final IBranchPath branchPath, final String conceptId,
			final ConceptWidgetModel conceptWidgetModel, final SnomedSimpleTypeRefSetAttributeConfiguration configuration, final boolean includeUnsanctioned, final boolean doSort, final IProgressMonitor monitor) {
		final WidgetBeanProvider widgetBeanProvider = new WidgetBeanProviderFactory().createProvider(
				checkNotNull(branchPath, "branchPath"), 
				checkNotNull(conceptId, "conceptId"), 
				checkNotNull(conceptWidgetModel, "conceptWidgetModel"), 
				configuration,
				includeUnsanctioned,
				doSort);
		return widgetBeanProvider.createConceptWidgetBean(checkNotNull(monitor, "monitor"));
	}

}