/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;

/**
 * Abstract service implementation for {@link ILookupService}s.
 * 
 * @param <T> type of the searched component, {@link CDOObject} subclass
 * @param <V> should be {@link CDOView} or its subclass.
 */
public abstract class AbstractLookupService<T extends CDOObject, V extends CDOView> implements ILookupService<T, V> {

	@Override
	public T getComponent(String id, V view) {
		checkNotNull(id, "Component ID argument cannot be null.");
		CDOUtils.check(view);

		// check the new components first in the given view
		for (final T component : ComponentUtils2.getNewObjects(view, getType())) {
			if (id.equals(getId(component))) {
				return component;
			}
		}
		
		final long conceptStorageKey = getStorageKey(BranchPathUtils.createPath(view), id);

		if (CDOUtils.NO_STORAGE_KEY == conceptStorageKey) { 
			return null;
		}
		
		return CDOUtils.getObjectIfExists(view, conceptStorageKey);
	}
	
	@Override
	public final boolean exists(final IBranchPath branchPath, final String id) {
		return -1 < getStorageKey(branchPath, id);
	}
	
	protected abstract Class<T> getType();
}
