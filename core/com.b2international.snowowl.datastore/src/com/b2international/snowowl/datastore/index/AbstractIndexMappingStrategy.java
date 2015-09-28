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

import static com.b2international.commons.ClassUtils.checkAndCast;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexMappingStrategy;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.datastore.index.mapping.Mappings;

/**
 * Abstract superclass for {@link IIndexMappingStrategy IIndexMappingStrategies} that work with a Lucene-based {@link AbstractIndexUpdater}. 
 *
 * @param E the {@link IIndexEntry} subtype this index service uses
 */
public abstract class AbstractIndexMappingStrategy implements IIndexMappingStrategy {

	@Override
	public final void index(final IIndexUpdater<?> indexUpdater, final IBranchPath branchPath) {
		checkAndCast(indexUpdater, AbstractIndexUpdater.class).index(branchPath, createDocument(), getStorageKey());
	}
	
	/**
	 * Converts an item supplied in the constructor of this strategy into a {@link Document} for indexing.
	 * 
	 * @return the converted document
	 */
	public abstract Document createDocument();

	protected abstract long getStorageKey();
}
