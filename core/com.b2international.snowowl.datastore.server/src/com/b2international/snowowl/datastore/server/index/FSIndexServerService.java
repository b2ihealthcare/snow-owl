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
package com.b2international.snowowl.datastore.server.index;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * File system backed index server service.
 */
public abstract class FSIndexServerService<E extends IIndexEntry> extends IndexServerService<E> {

	private final Supplier<IDirectoryManager> directorySupplier = Suppliers.memoize(new Supplier<IDirectoryManager>() {
		@Override public IDirectoryManager get() {
			return new FSDirectoryManager(getRepositoryUuid(), indexPath, FSIndexServerService.this);
		}
	});

	private final File indexPath;	
	
	protected FSIndexServerService(final File indexPath) {
		this.indexPath = checkNotNull(indexPath, "indexPath");
	}
	
	@Override
	protected IDirectoryManager getDirectoryManager() {
		return directorySupplier.get();
	}


}