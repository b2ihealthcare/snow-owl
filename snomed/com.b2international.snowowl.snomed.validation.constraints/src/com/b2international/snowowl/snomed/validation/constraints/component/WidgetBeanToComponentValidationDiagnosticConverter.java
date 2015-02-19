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

import java.util.Collection;
import java.util.Collections;

import com.b2international.snowowl.core.markers.IDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.mrcm.core.validator.WidgetBeanValidationDiagnostic;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ModeledWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipWidgetBean;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Utility class to convert from {@link WidgetBeanValidationDiagnostic} to {@link ComponentValidationDiagnosticImpl}.
 * 
 */
public abstract class WidgetBeanToComponentValidationDiagnosticConverter {

	/**
	 * @param diagnostic the widget bean validation diagnostic to convert
	 * @return the resulting component validation diagnostic
	 */
	public static ComponentValidationDiagnosticImpl convert(final WidgetBeanValidationDiagnostic diagnostic) {
		
		final Builder<IDiagnostic> builder = ImmutableList.builder();
		for (final IDiagnostic childDiagnostic : diagnostic.getChildren()) {
			builder.addAll(doConvert((WidgetBeanValidationDiagnostic) childDiagnostic));
		}
		
		return new ComponentValidationDiagnosticImpl(
				diagnostic.getId(), 
				diagnostic.getMessage(), 
				MrcmValidationConstraint.ID, 
				SnomedTerminologyComponentConstants.CONCEPT_NUMBER, 
				diagnostic.getProblemMarkerSeverity(), 
				builder.build());
	}
	
	private static Collection<ComponentValidationDiagnostic> doConvert(final WidgetBeanValidationDiagnostic diagnostic) {
		final ModeledWidgetBean modeledWidgetBean = diagnostic.getWidgetBean();
		if (modeledWidgetBean instanceof RelationshipWidgetBean) {
			final RelationshipWidgetBean relatonshipWidgetBean = (RelationshipWidgetBean) modeledWidgetBean;
			final String relationshipId = Long.toString(relatonshipWidgetBean.getSctId());
			final ComponentValidationDiagnostic componentValidationDiagnostic = new ComponentValidationDiagnosticImpl(
					relationshipId, 
					diagnostic.getMessage(), 
					MrcmValidationConstraint.ID, 
					SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, 
					diagnostic.getProblemMarkerSeverity());
			return Collections.<ComponentValidationDiagnostic>singleton(componentValidationDiagnostic);
		}
		
		// process child diagnostics
		final Builder<ComponentValidationDiagnostic> builder = ImmutableList.builder();
		for (final IDiagnostic childDiagnostics : diagnostic.getChildren()) {
			builder.addAll(doConvert((WidgetBeanValidationDiagnostic) childDiagnostics));
		}
		return builder.build();
	}

}