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

import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;

import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy;

/**
 * Interface for providing {@link CDOChangeSetData CDO change set data}.
 */
public interface ICDOChangeSetDataProvider {

	/**
	 * Returns with the change set data representing the changes between two {@link IBranchPoint branch point}s
	 * given as a {@link IBranchPointCalculationStrategy strategy}.
	 * @param strategy the strategy specifying the two {@link IBranchPoint branch point}s. 
	 * @return the changes between branch point as a {@link CDOChangeSetData}.
	 */
	CDOChangeSetData getChangeSetData(final IBranchPointCalculationStrategy strategy);
	
}