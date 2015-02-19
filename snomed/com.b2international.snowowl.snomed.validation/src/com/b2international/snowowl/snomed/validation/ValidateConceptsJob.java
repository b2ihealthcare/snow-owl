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
package com.b2international.snowowl.snomed.validation;

import java.io.Serializable;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.commons.TimedProgressMonitorWrapper;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ValuedJob;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity;
import com.b2international.snowowl.core.markers.MarkerManager;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.IClientComponentValidationService;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.google.common.collect.Iterables;

/**
 * Validates the list of specified components using an {@link IClientComponentValidationService}.
 */
public class ValidateConceptsJob<C extends IComponent<K>, K extends Serializable> extends ValuedJob<Integer> {
	
	private final Collection<C> componentsToValidate;
	private final IClientComponentValidationService<C> validationService;

	public ValidateConceptsJob(final String name, final Object family, final Collection<C> selectedComponents, final IClientComponentValidationService<C> validationService) {
		super(name, family);
		this.componentsToValidate = selectedComponents;
		this.validationService = validationService;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		
		final TimedProgressMonitorWrapper delegateMonitor = new TimedProgressMonitorWrapper(monitor);
		final SubMonitor subMonitor = SubMonitor.convert(delegateMonitor, componentsToValidate.size());
		
		try {
			
			final MarkerManager markerManager = ApplicationContext.getServiceForClass(MarkerManager.class);
			final SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getServiceForClass(SnomedClientTerminologyBrowser.class);
			final Collection<ComponentValidationDiagnostic> validationResults = validationService.validate(componentsToValidate, subMonitor);
			
			if (validationResults.size() == 1 && DiagnosticSeverity.CANCEL.equals(Iterables.get(validationResults, 0).getProblemMarkerSeverity())) {
				return Status.CANCEL_STATUS;
			}
			
			int errorCount = 0;
			
			for (final ComponentValidationDiagnostic diagnostic : validationResults) {
				if (!diagnostic.isOk()) {
					++errorCount;
				}
			
				markerManager.createValidationMarkerOnComponent(terminologyBrowser.getConcept(diagnostic.getId()), diagnostic);
			}
			
			setValue(errorCount);
		} finally {
			delegateMonitor.done();
		}
		
		return Status.OK_STATUS;
	}
}