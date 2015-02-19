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
package com.b2international.snowowl.core.api.index;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Translates the generic model element to an index document.
 * 
 * @param E the {@link IIndexEntry} subtype this index service uses
 *  
 */
public interface IIndexMappingStrategy {
	
	/**
	 * Accepts an {@link IIndexUpdater} and indexes a new entry, or updates an existing one if present.
	 * 
	 * @param indexUpdater the updater service to use (may not be {@code null})
	 * @param branchPath the branch path reference limiting visibility to a particular branch (may not be {@code null})
	 */
	public void index(final IIndexUpdater<?> indexUpdater, final IBranchPath branchPath);
}