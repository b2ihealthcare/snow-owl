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
package com.b2international.snowowl.datastore.server;


import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;

/**
 * Interface for creating {@link ICDOChangeProcessor} instances.
 */
public interface CDOChangeProcessorFactory {

	/**
	 * Creates and returns a {@link ICDOChangeProcessor change processor} on the given branch with the given timestamp.
	 * @param branchPath
	 * @return
	 * @throws SnowowlServiceException
	 */
	ICDOChangeProcessor createChangeProcessor(final IBranchPath branchPath) throws SnowowlServiceException;
	
	/**
	 * Returns with a human readable name or ID of the concrete implementation for better logging.
	 * <p><b>Note:</b> neither ID nor name should be unique. 
	 * @return the name or ID of the factory.
	 */
	String getFactoryName();
}
