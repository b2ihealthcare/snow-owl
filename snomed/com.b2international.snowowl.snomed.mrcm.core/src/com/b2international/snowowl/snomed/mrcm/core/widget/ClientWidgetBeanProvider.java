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

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.mrcm.core.configuration.SnomedSimpleTypeRefSetAttributeConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;

/**
 * Client side widget bean provider implementation, which delegates to the server.
 * Use this implementation, when the concept is already available and up to date in the lightweight store.
 * 
 */
public class ClientWidgetBeanProvider implements IClientWidgetBeanProvider {

	private final IWidgetBeanProvider wrappedService;
	
	public ClientWidgetBeanProvider(IWidgetBeanProvider wrappedService) {
		this.wrappedService = checkNotNull(wrappedService, "wrappedService");
	}

	@Override
	public ConceptWidgetBean createConceptWidgetBean(String conceptId, ConceptWidgetModel conceptWidgetModel, 
			SnomedSimpleTypeRefSetAttributeConfiguration configuration, final boolean includeUnsanctioned, IProgressMonitor monitor) {
		return wrappedService.createConceptWidgetBean(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE), 
				checkNotNull(conceptId, "conceptId"), 
				checkNotNull(conceptWidgetModel, "conceptWidgetModel"), 
				configuration, 
				includeUnsanctioned,
				true,
				checkNotNull(monitor, "monitor"));
	}

}