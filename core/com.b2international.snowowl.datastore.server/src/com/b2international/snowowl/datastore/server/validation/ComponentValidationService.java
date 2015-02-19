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
package com.b2international.snowowl.datastore.server.validation;

import static com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity.INFO;
import static com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl.createOk;
import static com.b2international.snowowl.core.validation.IComponentValidationConstraint.EXTENSION_POINT_ID;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.text.MessageFormat.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;
import static org.eclipse.core.runtime.IProgressMonitor.UNKNOWN;
import static org.eclipse.core.runtime.Platform.getExtensionRegistry;
import static org.eclipse.core.runtime.SubMonitor.convert;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;

import com.b2international.commons.concurrent.ConcurrentCollectionUtils;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.IdAndTerminologyComponentIdProvider;
import com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.core.validation.GlobalConstraintDescriptor;
import com.b2international.snowowl.core.validation.GlobalConstraintRegistry;
import com.b2international.snowowl.core.validation.GlobalConstraintStatus;
import com.b2international.snowowl.core.validation.IComponentValidationConstraint;
import com.b2international.snowowl.core.validation.IComponentValidationService;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

/**
 * Server side validation service implementation.
 * 
 */
public abstract class ComponentValidationService<C extends IComponent<String>> implements IComponentValidationService<C> {

	private static final String UNKNOWN_CONSTRAINT = "Unknown constraint";
	private static final Logger LOGGER = getLogger(ComponentValidationService.class);
	private static final String VALIDATING_TASK_NAME_TEMPLATE = "Validating ''{0}''...";
	
	//cache ensuring a mapping between terminology component IDs and the available component validation constraints
	private final LoadingCache<String, Collection<IComponentValidationConstraint<IComponent<?>>>> cache = //
			CacheBuilder.newBuilder().build(new CacheLoader<String, Collection<IComponentValidationConstraint<IComponent<?>>>>() {

		private static final String TERMINOLOGY_COMPONENT_ID_ATTRIBUTE = "terminologyComponent";
				
		@SuppressWarnings("unchecked")
		public Collection<IComponentValidationConstraint<IComponent<?>>> load(final String terminologyComponentId) throws Exception {
			final Collection<IComponentValidationConstraint<IComponent<?>>> constraints = newHashSet(); 
			
			for (final IConfigurationElement element : getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID)) {
				if (nullToEmpty(terminologyComponentId).equals(element.getAttribute(TERMINOLOGY_COMPONENT_ID_ATTRIBUTE))) {
					constraints.add((IComponentValidationConstraint<IComponent<?>>) CoreTerminologyBroker.getInstance().createExecutableExtension(element));
				}
			}
			
			return unmodifiableCollection(constraints);
		}
	});

	@Override
	public Collection<ComponentValidationDiagnostic> validate(final IBranchPath branchPath, final C component, final IProgressMonitor monitor) {
		final IProgressMonitor subMonitor = convert(monitor);
		subMonitor.beginTask(format(VALIDATING_TASK_NAME_TEMPLATE, component), UNKNOWN);

		try {
			return newArrayList(ConcurrentCollectionUtils.transform(getConstraints(component).iterator(),
				new Function<IComponentValidationConstraint<IComponent<?>>, ComponentValidationDiagnostic>() {
				public ComponentValidationDiagnostic apply(final IComponentValidationConstraint<IComponent<?>> input) {
					return input.validate(branchPath, component);
				}
			}));
		} finally {
			subMonitor.done();
		}
	}
	
	@Override
	public Collection<ComponentValidationDiagnostic> validate(final IBranchPath branchPath, final Collection<C> components, final IProgressMonitor monitor) {
		final Collection<ComponentValidationDiagnostic> results = newArrayList();
		final SubMonitor subMonitor = convert(monitor, components.size());
		
		try {
			
			int counter = 0;
			for (final C component : components) {
				if (0 == ++counter % 1000 && subMonitor.isCanceled()) {
					return emptyList();
				}
				results.addAll(validate(branchPath, component, subMonitor.newChild(1)));
			}
			
			return results;
			
		} finally {
			subMonitor.done();
		}

	}
	
	@Override
	public Collection<ComponentValidationDiagnostic> validateAll(final IBranchPath branchPath, final IProgressMonitor monitor) {

		final SubMonitor subMonitor = SubMonitor.convert(monitor, 2);
		final SubMonitor globalSubMonitor = subMonitor.newChild(1);
		final Collection<ComponentValidationDiagnostic> results = Lists.newArrayList();

		// execute global validation
		results.addAll(validateGlobalConstraints(branchPath, getAllGlobalConstraintIds(), globalSubMonitor));
		
		// validate each component
		final SubMonitor validateAllSubMonitor = subMonitor.newChild(1);
		results.addAll(doValidateAll(branchPath, validateAllSubMonitor));

		return results;
	}

	@Override
	public Collection<ComponentValidationDiagnostic> validateGlobalConstraints(final IBranchPath branchPath, final Collection<String> globalValidationConstraintIds, final IProgressMonitor monitor) {
		final SubMonitor globalSubMonitor = convert(monitor, 1);
		// execute global validation
		final Collection<GlobalConstraintDescriptor> allDescriptors = GlobalConstraintRegistry.getInstance().getAllDescriptors();
		globalSubMonitor.setWorkRemaining(globalValidationConstraintIds.size());
		final Collection<ComponentValidationDiagnostic> globalValidationDiagnostics = Lists.newArrayList();
		
		for (final GlobalConstraintDescriptor globalConstraintDescriptor : allDescriptors) {
			
			final String constraintId = globalConstraintDescriptor.getId();
			if (!globalValidationConstraintIds.contains(constraintId)) {
				continue; //skip validation
			}
			
			final GlobalConstraintStatus validationResult = doValidate(branchPath, globalConstraintDescriptor, globalSubMonitor);
			final GlobalConstraintDescriptor constraintDescriptor = getDescriptor(constraintId);
			final DiagnosticSeverity constraintSeverity = getSeverity(constraintDescriptor);
			final String constraintDescription = getDescription(constraintDescriptor);
			
			for (final IdAndTerminologyComponentIdProvider component : validationResult.getViolatingComponents()) {
				final ComponentValidationDiagnosticImpl diagnostic = new ComponentValidationDiagnosticImpl(component, constraintDescription, constraintId, constraintSeverity);
				globalValidationDiagnostics.add(diagnostic);
			}
			
			for (final IdAndTerminologyComponentIdProvider component : validationResult.getComplyingComponents()) {
				globalValidationDiagnostics.add(createOk(component, constraintId));
			}
			
			if (globalSubMonitor.isCanceled()) {
				return emptyList();
			}
			
			globalSubMonitor.worked(1);
		}
		
		return globalValidationDiagnostics;
	}

	protected abstract Collection<ComponentValidationDiagnostic> doValidateAll(final IBranchPath branchPath, final IProgressMonitor monitor);

	private GlobalConstraintStatus doValidate(final IBranchPath branchPath, final GlobalConstraintDescriptor globalConstraintDescriptor, final IProgressMonitor monitor) {
		return globalConstraintDescriptor.getConstraint().validate(branchPath, monitor);
	}

	private GlobalConstraintDescriptor getDescriptor(final String constraintId) {
		return GlobalConstraintRegistry.getInstance().getDescriptor(constraintId);
	}

	private Collection<String> getAllGlobalConstraintIds() {
		return GlobalConstraintRegistry.getInstance().getAllDescriptorIds();
	}

	private String getDescription(final GlobalConstraintDescriptor constraintDescriptor) {
		return null == constraintDescriptor.getDescription() ? UNKNOWN_CONSTRAINT : constraintDescriptor.getDescription();
	}

	private DiagnosticSeverity getSeverity(final GlobalConstraintDescriptor constraintDescriptor) {
		return null == constraintDescriptor.getSeverity() ? INFO : constraintDescriptor.getSeverity();
	}
	
	private Collection<IComponentValidationConstraint<IComponent<?>>> getConstraints(final IComponent<?> component) {
		final String terminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(component);
		try {
			return cache.get(terminologyComponentId);
		} catch (final ExecutionException e) {
			LOGGER.error("Error while retrieving validation constraints for component: " + component, e);
			return emptyList();
		}
	}
}