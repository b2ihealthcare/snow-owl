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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.datastore.editor.bean.IdentifiedBean;
import com.b2international.snowowl.datastore.validation.IBeanValidationConstraint;
import com.b2international.snowowl.datastore.validation.IBeanValidationConstraintProvider;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Extension based context validation constraint provider implementation.
 * 
 */
public class ExtensionBasedBeanValidationConstraintProvider implements IBeanValidationConstraintProvider {

	private static final String TERMINOLOGY_COMPONENT_ID_ATTRIBUTE = "terminologyComponent";
	private static final String EXTENSION_POINT_ID = "com.b2international.snowowl.datastore.server.beanValidationConstraint";
	
	@Override
	public List<IBeanValidationConstraint> getConstraints(String terminologyComponentId) {
		checkNotNull(terminologyComponentId, "Terminology component ID must not be null.");
		final Set<IConfigurationElement> elements = Sets.newHashSet();
		for (final IConfigurationElement element : Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID)) {
			if (terminologyComponentId.equals(element.getAttribute(TERMINOLOGY_COMPONENT_ID_ATTRIBUTE))) {
				elements.add(element);
			}
		}
		List<IBeanValidationConstraint> constraints = ImmutableList.copyOf(Collections2.transform(elements, new Function<IConfigurationElement, IBeanValidationConstraint>() {
			@Override public IBeanValidationConstraint apply(IConfigurationElement input) {
				return (IBeanValidationConstraint) CoreTerminologyBroker.getInstance().createExecutableExtension(input);
			}
		}));
		return constraints;
	}
	
	@Override
	public List<IBeanValidationConstraint> getConstraints(IdentifiedBean bean) {
		checkNotNull(bean, "Bean must not be null.");
		String terminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(bean);
		return getConstraints(terminologyComponentId);
	}

}