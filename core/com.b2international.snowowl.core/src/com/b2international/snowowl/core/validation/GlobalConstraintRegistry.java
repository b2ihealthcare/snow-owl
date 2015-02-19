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

import static com.google.common.collect.Collections2.transform;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * Registry for global validation constraints. 
 */
public class GlobalConstraintRegistry {
	// extension point ID
	private static final String GLOBAL_VALIDATION_CONSTRAINTS_EXTENSION_ID = "com.b2international.snowowl.core.globalValidationConstraints";
	// extension point attributes and elements
	private static final String DESCRIPTION_ATTRIBUTE = "description";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String ID_ATTRIBUTE = "id";
	private static final String CONSTRAINT_ELEMENT = "constraint";
	private static final String MESSAGE_ATTRIBUTE = "message";
	private static final String SEVERITY_ATTRIBUTE = "severity";
	
	private static final GlobalConstraintRegistry INSTANCE = new GlobalConstraintRegistry();
	private final Map<String, GlobalConstraintDescriptor> descriptors = Maps.newHashMap();
	
	private GlobalConstraintRegistry() {
		init();
	}
	
	public static GlobalConstraintRegistry getInstance() {
		return INSTANCE;
	}
	
	public GlobalConstraintDescriptor getDescriptor(String id) {
		return descriptors.get(id);
	}
	
	public Collection<GlobalConstraintDescriptor> getAllDescriptors() {
		return descriptors.values();
	}
	
	public Collection<String> getAllDescriptorIds() {
		return transform(descriptors.values(), new Function<GlobalConstraintDescriptor, String>() {
			public String apply(GlobalConstraintDescriptor constraintDescriptor) {
				return constraintDescriptor.getId();
			}
		});
	}
	
	private void init() {
		IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().
			getConfigurationElementsFor(GLOBAL_VALIDATION_CONSTRAINTS_EXTENSION_ID);
		for (IConfigurationElement configurationElement : configurationElements) {
			if (CONSTRAINT_ELEMENT.equals(configurationElement.getName())) {
				String constraintId = configurationElement.getAttribute(ID_ATTRIBUTE);
				try {
					IGlobalConstraint constraint = (IGlobalConstraint) configurationElement.createExecutableExtension("constraintClass");
					GlobalConstraintDescriptor constraintDescriptor = new GlobalConstraintDescriptor(constraintId, 
							configurationElement.getAttribute(NAME_ATTRIBUTE), 
							Enum.valueOf(DiagnosticSeverity.class, configurationElement.getAttribute(SEVERITY_ATTRIBUTE)), 
							configurationElement.getAttribute(DESCRIPTION_ATTRIBUTE), 
							configurationElement.getAttribute(MESSAGE_ATTRIBUTE), 
							constraint);
					descriptors.put(constraintId, constraintDescriptor);
				} catch (CoreException e) {
					throw new RuntimeException("Can't instantiate global validation constraint: " + constraintId, e);
				}
			}
		}
	}
}