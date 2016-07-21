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

import java.io.Serializable;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.ILookupService;

/**
 * Abstract service implementation for {@link ILookupService}s.
 * 
 * 
 * @param <K> serializable unique identifier of the component
 * @param <T> type of the searched component
 * @param <V> should be CDO View or its subclass.
 */
public abstract class AbstractLookupService<K extends Serializable, T, V> implements ILookupService<K, T, V> {

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.ILookupService#exists(com.b2international.snowowl.core.api.IBranchPath, java.io.Serializable)
	 */
	@Override
	public boolean exists(final IBranchPath branchPath, final K id) {
		return -1 < getStorageKey(branchPath, id);
	}
	
	protected abstract EPackage getEPackage();
}