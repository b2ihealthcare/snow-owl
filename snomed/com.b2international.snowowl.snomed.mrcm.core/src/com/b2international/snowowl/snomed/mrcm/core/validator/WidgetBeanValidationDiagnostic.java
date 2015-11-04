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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.api.component.IdProvider;
import com.b2international.snowowl.core.markers.IDiagnostic;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ModeledWidgetBean;
import com.google.common.collect.Lists;

/**
 * {@link ModeledWidgetBean Widget bean} validation diagnostic.
 * 
 */
public class WidgetBeanValidationDiagnostic implements IDiagnostic, Serializable, IdProvider<String> {

	private static final long serialVersionUID = 601445811802952280L;

	public static final String SOURCE_ID = "com.b2international.snowowl.snomed.mrcm.core.validator.WidgetBeanValidationDiagnostic";
	
	private final String message;
	private final String source = SOURCE_ID;
	private final DiagnosticSeverity problemMarkerSeverity;
	private final ModeledWidgetBean widgetBean;
	private List<IDiagnostic> children = Lists.newArrayList();
	private final String conceptId;
	
	public WidgetBeanValidationDiagnostic(final String conceptId, final String message, final List<IDiagnostic> children, final ModeledWidgetBean widgetBean) {
		this.conceptId = conceptId;
		this.message = message;
		this.widgetBean = widgetBean;
		this.children = children;
		this.problemMarkerSeverity = getMaxSeverity(children);
	}
	
	public WidgetBeanValidationDiagnostic(final String conceptId, final String message, final DiagnosticSeverity problemMarkerSeverity, final ModeledWidgetBean widgetBean) {
		this.conceptId = conceptId;
		this.message = message;
		this.problemMarkerSeverity = problemMarkerSeverity;
		this.widgetBean = widgetBean;
	}

	@Override
	public boolean isOk() {
		return problemMarkerSeverity.equals(DiagnosticSeverity.OK);
	}

	@Override
	public DiagnosticSeverity getProblemMarkerSeverity() {
		return problemMarkerSeverity;
	}

	@Override
	public Collection<IDiagnostic> getChildren() {
		return children;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getSource() {
		return source;
	}
	
	@Override
	public String getId() {
		return conceptId;
	}
	
	public ModeledWidgetBean getWidgetBean() {
		return widgetBean;
	}

	private DiagnosticSeverity getMaxSeverity(final List<IDiagnostic> children) {
		int maxSeverity = DiagnosticSeverity.OK.getErrorCode();
		for (final IDiagnostic childStatus : children) {
			final int severity = childStatus.getProblemMarkerSeverity().getErrorCode();
			if (severity > maxSeverity)
				maxSeverity = severity;
		}
		return DiagnosticSeverity.valueOf(maxSeverity);
	}
}