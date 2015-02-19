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
package com.b2international.snowowl.datastore.validation;

import static java.util.Collections.singleton;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.slf4j.Logger;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.core.validation.IClientComponentValidationService;
import com.b2international.snowowl.core.validation.IComponentValidationService;
import com.b2international.snowowl.datastore.ActiveBranchPathAwareService;

/**
 * Abstract, terminology independent client side validation service implementation, which simply delegates to the equivalent server side service.
 * Clients should create a subclass for each terminology. 
 * 
 */
public abstract class AbstractComponentValidationClientService<C extends IComponent<?>> extends ActiveBranchPathAwareService implements IClientComponentValidationService<C> {

	private static final Logger LOGGER = getLogger(AbstractComponentValidationClientService.class);
	private static final Set<ComponentValidationDiagnostic> CANCEL_DIAGNOSTIC = singleton(ComponentValidationDiagnosticImpl.createCancel());
	
	private final IComponentValidationService<C> wrappedService;

	public AbstractComponentValidationClientService(final IComponentValidationService<C> wrappedService) {
		this.wrappedService = wrappedService;
	}

	@Override
	public Collection<ComponentValidationDiagnostic> validate(final C component, final IProgressMonitor monitor) {
		try {
			return wrappedService.validate(getBranchPath(), component, monitor);
		} catch (final OperationCanceledException e) {
			logUserAbort();
			return createCanceledDiagnostic();
		}
	}

	@Override
	public Collection<ComponentValidationDiagnostic> validate(final Collection<C> components, final IProgressMonitor monitor) {
		try {
			return wrappedService.validate(getBranchPath(), components, monitor);
		} catch (final OperationCanceledException e) {
			logUserAbort();
			return createCanceledDiagnostic();
		}
	}

	@Override
	public Collection<ComponentValidationDiagnostic> validateAll(final IProgressMonitor monitor) {
		try {
			return wrappedService.validateAll(getBranchPath(), monitor);
		} catch (final OperationCanceledException e) {
			logUserAbort();
			return createCanceledDiagnostic();
		}
	}

	@Override
	public Collection<ComponentValidationDiagnostic> validateGlobalConstraints(final Collection<String> globalValidationConstraintIds, final IProgressMonitor monitor) {
		try {
			return wrappedService.validateGlobalConstraints(getBranchPath(), globalValidationConstraintIds, monitor);
		} catch (final OperationCanceledException e) {
			logUserAbort();
			return createCanceledDiagnostic();
		}
	}
	
	private Collection<ComponentValidationDiagnostic> createCanceledDiagnostic() {
		return CANCEL_DIAGNOSTIC;
	}
	
	private void logUserAbort() {
		LOGGER.info("User abort for component validation.");
	}

}