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
package com.b2international.snowowl.core.validation;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.IComponent;

/**
 * Client side validation service interface.
 * 
 */
public interface IClientComponentValidationService<C extends IComponent<?>> {

	String CANCEL_SOURCE_ID = "com.b2international.snowowl.core.validation.IClientComponentValidationService.CANCEL"; 
	
	Collection<ComponentValidationDiagnostic> validate(C component, IProgressMonitor monitor);
	
	Collection<ComponentValidationDiagnostic> validate(Collection<C> components, IProgressMonitor monitor);
	
	Collection<ComponentValidationDiagnostic> validateAll(IProgressMonitor monitor);
	
	/**
	 * Validates a bunch of {@link IGlobalConstraint} instances given with their unique IDs.
	 * @param globalValidationConstraintIds a collection of constraint IDs that has to be used for the validation. Others are ignored.
	 * @param monitor the monitor for the process.
	 * @return a collection of diagnostics as the outcome of the validation process.
	 */
	Collection<ComponentValidationDiagnostic> validateGlobalConstraints(final Collection<String> globalValidationConstraintIds, final IProgressMonitor monitor);
}