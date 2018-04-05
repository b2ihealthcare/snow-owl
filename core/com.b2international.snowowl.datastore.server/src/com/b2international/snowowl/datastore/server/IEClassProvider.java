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
package com.b2international.snowowl.datastore.server;

import java.io.IOException;

import org.eclipse.emf.ecore.EClass;

import com.b2international.index.revision.RevisionSearcher;

/**
 * Interface for providing {@link EClass}.
 */
public interface IEClassProvider {

	/**
	 * Returns with the {@link EClass} of an object identified by a unique storage key.
	 * @param searcher - a searcher to use to get the EClass for the storageKey
	 * @param storageKey the unique storage key.
	 * @return the {@link EClass}.
	 * @throws IOException 
	 */
	EClass getEClass(RevisionSearcher searcher, final long storageKey) throws IOException;
	
	/**
	 * Returns with the UUID of the repository where the current provider works on.  
	 * @return the repositroy UUID.
	 */
	String getRepositoryUuid();
	
}