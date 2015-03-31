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
package com.b2international.snowowl.datastore.server.snomed.index.init;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;

/**
 * Interface for populating the content of the {@link ImportIndexServerService}.
 */
public interface IImportIndexServiceFeeder {

	/**
	 * Initializes contents of the specified import index service.
	 * 
	 * @param service the import index service to populate
	 * @param branchPath the branch path to use for lookups
	 * @param monitor an {@link IProgressMonitor} for tracking initialization
	 * 
	 * @throws SnowowlServiceException if content initialization fails for some reason
	 */
	void initContent(ImportIndexServerService service, IBranchPath branchPath, IProgressMonitor monitor) throws SnowowlServiceException;
}
