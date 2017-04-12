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
package com.b2international.snowowl.datastore;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.google.common.collect.Sets;

/**
 * Deletion plan for storing the components that will be deleted.
 * 
 */
public class ComponentDeletionPlan {
	//keep terminology component identifier and component identifier identifying the deleted objects
	private final Set<ComponentIdentifier> deletedComponents = Sets.newHashSet();

	// keep deletedItems sorted by type for nicer display to the user
	private final Set<CDOObject> deletedItems = new TreeSet<CDOObject>(ComponentUtils2.CDO_OBJECT_COMPARATOR);

	/**
	 * Marks a component for deletion.
	 * @param cdoObject the component to delete.
	 */
	public void markForDeletion(final CDOObject cdoObject) {
		internalMarkForDeletion(Collections.singletonList(cdoObject));
	}

	private void internalMarkForDeletion(final Collection<? extends CDOObject> items) {
		for (final EObject object : items) {
			final ComponentIdentifier pair = createIdentifierPair(object);
			if (null == pair) {
				continue;
			}
			deletedComponents.add(pair);
		}
		deletedItems.addAll(items);
	}

	private ComponentIdentifier createIdentifierPair(final Object object) {
		final IComponent<?> component = CoreTerminologyBroker.getInstance().adapt(object);
		if (null == component) {
			return null;
		}
		final short terminologyComponentId = getTerminolgyComponentId(object);
		return ComponentIdentifier.of(terminologyComponentId, String.valueOf(component.getId()));
	}

	private short getTerminolgyComponentId(final Object object) {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsShort(object);
	}

	public Set<ComponentIdentifier> getDeletedComponents() {
		return deletedComponents;
	}

	public Set<CDOObject> getDeletedItems() {
		return deletedItems;
	}

	/**
	 * Returns with a copy of the deleted components represented as {@link ComponentIdentifier identifier pair} instances.
	 * @return a set of component identifier pairs. Generally another representation of the components marked for deletion.
	 */
	public Set<ComponentIdentifier> getDeletedComponentIdentifiers() {
		return Collections.unmodifiableSet(deletedComponents);
	}
}