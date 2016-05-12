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
package com.b2international.snowowl.datastore.index;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.datastore.BranchPathUtils;

/**
 * An abstract client index service which uses the repository ePackage to determine the active branch path.  
 */
public abstract class ActiveBranchClientIndexService<E extends IIndexEntry> extends AbstractClientIndexService<E> {

	/**
	 * Creates a new instance with the specified arguments.
	 * @param wrappedService the wrapped branch-using index service
	 */
	protected ActiveBranchClientIndexService(final IIndexService<E> wrappedService) {
		super(wrappedService);
	}

	@Override
	public IBranchPath getBranchPath() {
		return BranchPathUtils.createActivePath(getEPackage());
	}

	protected abstract EPackage getEPackage();
}