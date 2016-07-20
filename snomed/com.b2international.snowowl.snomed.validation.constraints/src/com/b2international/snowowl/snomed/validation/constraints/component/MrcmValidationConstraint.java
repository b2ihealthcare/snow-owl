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
package com.b2international.snowowl.snomed.validation.constraints.component;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.core.validation.IComponentValidationConstraint;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.mrcm.core.validator.MrcmConceptWidgetBeanValidator;
import com.b2international.snowowl.snomed.mrcm.core.validator.WidgetBeanValidationDiagnostic;
import com.b2international.snowowl.snomed.mrcm.core.widget.IWidgetBeanProvider;
import com.b2international.snowowl.snomed.mrcm.core.widget.IWidgetModelProvider;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;

/**
 * MRCM based validation constraint.
 * 
 */
public class MrcmValidationConstraint implements IComponentValidationConstraint<SnomedConceptDocument> {

	public static final String ID = "com.b2international.snowowl.snomed.validation.constraints.component.MrcmValidationConstraint";
	
	@Override
	public ComponentValidationDiagnosticImpl validate(final IBranchPath branchPath, final SnomedConceptDocument indexEntry) {
		final IWidgetModelProvider widgetModelProvider = ApplicationContext.getInstance().getService(IWidgetModelProvider.class);
		final IWidgetBeanProvider widgetBeanProvider = ApplicationContext.getInstance().getService(IWidgetBeanProvider.class);
		
		final ConceptWidgetModel widgetModel = widgetModelProvider.createConceptWidgetModel(branchPath, indexEntry.getId(), null);
		final ConceptWidgetBean widgetBean = widgetBeanProvider.createConceptWidgetBean(branchPath, indexEntry.getId(), widgetModel, null, true, false, new NullProgressMonitor());
		final MrcmConceptWidgetBeanValidator validator = new MrcmConceptWidgetBeanValidator();
		final WidgetBeanValidationDiagnostic diagnostic = (WidgetBeanValidationDiagnostic) validator.validate(widgetBean);
		final ComponentValidationDiagnosticImpl componentValidationDiagnostic = WidgetBeanToComponentValidationDiagnosticConverter.convert(diagnostic);
		return componentValidationDiagnostic;
	}

}