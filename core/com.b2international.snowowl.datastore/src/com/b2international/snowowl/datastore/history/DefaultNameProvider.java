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
package com.b2international.snowowl.datastore.history;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.commons.StringUtils;

/**
 * Default implementation of {@link INameProvider} interface. Returns the {@link EClass} name attribute for all
 * {@link EObject} instances.
 * 
 * @since 3.1
 */
public class DefaultNameProvider implements INameProvider {

	private final PolymorphicDispatcher<String> nameProviderDispatcher = createNameProviderDispatcher();

	protected PolymorphicDispatcher<String> createNameProviderDispatcher() {
		return PolymorphicDispatcher.createForSingleTarget(PolymorphicDispatcher.Predicates.forName("_provideName", 1),
				this);
	}

	@Override
	public String provideName(final EObject object) {
		if (object == null) {
			return "N/A";
		}
		return this.nameProviderDispatcher.invoke(object);
	}

	protected String _provideName(final EObject object) {
		return StringUtils.splitCamelCaseAndCapitalize(object.eClass().getName());
	}

}