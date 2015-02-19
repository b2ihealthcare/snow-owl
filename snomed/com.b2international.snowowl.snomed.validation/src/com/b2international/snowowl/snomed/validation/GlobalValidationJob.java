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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.b2international.commons.TimedProgressMonitorWrapper;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ValuedJob;
import com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity;
import com.b2international.snowowl.core.markers.MarkerManager;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.validation.IClientSnomedComponentValidationService;
import com.google.common.collect.Iterables;

/**
 * Evaluates all global validation constraints, then proceeds with evaluating all concept validation rules
 * on all concepts.
 * 
 */
public class GlobalValidationJob extends ValuedJob<Integer> {
	
	private static final String[] EMPTY_ARRAY = {};
	
	private final IClientSnomedComponentValidationService validationService;
	private Collection<String> globalValidationConstraintIds;

	public GlobalValidationJob(final String name, final Object family) {
		this(name, family, EMPTY_ARRAY);
	}
	
	public GlobalValidationJob(final String name, final Object family, final String... globalValidationConstraintIds) {
		super(name, family);
		this.validationService = getServiceForClass(IClientSnomedComponentValidationService.class);
		this.globalValidationConstraintIds = newHashSet(globalValidationConstraintIds);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		
		final TimedProgressMonitorWrapper delegateMonitor = new TimedProgressMonitorWrapper(monitor);
		
		try {
			
			final MarkerManager markerManager = ApplicationContext.getInstance().getService(MarkerManager.class);
			final SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getServiceForClass(SnomedClientTerminologyBrowser.class);
			final Collection<ComponentValidationDiagnostic> validationResults;
			if (isEmpty(globalValidationConstraintIds)) {
				validationResults = validationService.validateAll(delegateMonitor);
			} else {
				validationResults = validationService.validateGlobalConstraints(globalValidationConstraintIds, delegateMonitor);
			}
			
			if (validationResults.size() == 1 && DiagnosticSeverity.CANCEL.equals(Iterables.get(validationResults, 0).getProblemMarkerSeverity())) {
				return Status.CANCEL_STATUS;
			}

			final Collection<String> violatingComponentIds = newHashSet(); 
			
			for (final ComponentValidationDiagnostic diagnostic : validationResults) {
				
				if (!diagnostic.isOk()) {
					violatingComponentIds.add(diagnostic.getId());
				}
				
				markerManager.createValidationMarkerOnComponent(terminologyBrowser.getConcept(diagnostic.getId()), diagnostic);
			}
			
			setValue(violatingComponentIds.size());
		} finally {
			delegateMonitor.done();
		}
		
		return Status.OK_STATUS;
	}
}