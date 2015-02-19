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
package com.b2international.snowowl.snomed.mrcm.core.validator;

import java.util.List;

import com.b2international.snowowl.core.markers.IDiagnostic;
import com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ContainerWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DescriptionWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ModeledWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipWidgetBean;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * MRCM based {@link ConceptWidgetBean} validator.
 * 
 */
public class MrcmConceptWidgetBeanValidator {

	public IDiagnostic validate(final ConceptWidgetBean conceptWidgetBean) {
		final String conceptId = conceptWidgetBean.getConceptId();
		final ContainerWidgetBean properties = conceptWidgetBean.getProperties();
		final List<IDiagnostic> propertiesDiagnostics = doValidate(conceptId, properties);
		final List<IDiagnostic> descriptionDiagnostics = doValidate(conceptId, conceptWidgetBean.getDescriptions());
		return new WidgetBeanValidationDiagnostic(conceptId, "", 
				ImmutableList.<IDiagnostic>builder().addAll(propertiesDiagnostics).addAll(descriptionDiagnostics).build(), conceptWidgetBean);
	}
	
	private List<IDiagnostic> doValidate(final String conceptId, final ModeledWidgetBean modeledWidgetBean) {
		final Builder<IDiagnostic> builder = ImmutableList.builder();
		if (modeledWidgetBean instanceof ContainerWidgetBean) {
			final ContainerWidgetBean containerWidgetBean = (ContainerWidgetBean) modeledWidgetBean;
			final List<ModeledWidgetBean> elements = containerWidgetBean.getElements();
			for (final ModeledWidgetBean childElement : elements) {
				builder.addAll(doValidate(conceptId, childElement));
			}
		} else if (modeledWidgetBean instanceof RelationshipWidgetBean) {
			final RelationshipWidgetBean relationshipWidgetBean = (RelationshipWidgetBean) modeledWidgetBean;
			if (relationshipWidgetBean.isUnsanctioned()) {
				final WidgetBeanValidationDiagnostic diagnostic = new WidgetBeanValidationDiagnostic(conceptId, "The relationship '" + relationshipWidgetBean.getSelectedType().getLabel() + " " + 
						relationshipWidgetBean.getSelectedValue().getLabel() + "' violates the concept model.", DiagnosticSeverity.WARNING, relationshipWidgetBean);
				builder.add(diagnostic);
			}
		} else if (modeledWidgetBean instanceof DescriptionWidgetBean) {
			final DescriptionWidgetBean descriptionWidgetBean = (DescriptionWidgetBean) modeledWidgetBean;
			// TODO
		}
		
		return builder.build();
	}
}