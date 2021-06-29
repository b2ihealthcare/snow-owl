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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.commons.ChangeKind;


/**
 * Default implementation of {@link IDescriptionProvider} interface. Provides
 * the toString representation of an {@link EObject}. Uses an
 * {@link INameProvider} instance for getting the right component type name for
 * an {@link EObject}.
 * 
 * @since 3.1
 */
public class DefaultDescriptionProvider implements IDescriptionProvider {

	private PolymorphicDispatcher<String> provideDescriptionDispatcher = createDescriptionDispatcher();
	
	protected PolymorphicDispatcher<String> createDescriptionDispatcher() {
		return PolymorphicDispatcher.createForSingleTarget(PolymorphicDispatcher.Predicates.forName("_provideDescription", 1), this);
	}
	
	/* (non-Javadoc)
	 * @see sg.com.ihis.sdd.sourcedata.core.provider.IDescriptionProvider#provideDescription(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String provideDescription(final EObject object, final ChangeKind type) {
		if (object == null) {
			return "N/A";
		}
		return provideDescriptionDispatcher.invoke(object);
	}
	
	protected String _provideDescription(final EObject object) {
		return object.toString();
	}
		
}