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
package com.b2international.snowowl.datastore.delta;

import java.util.Collection;

import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.core.api.IHistoryInfo;

/**
 * Server side interface for {@link ComponentDelta component delta} providers.
 * 
 */
public interface IComponentDeltaProvider<C extends ComponentDelta> {

	/**
	 * Returns with a collection of {@link ComponentDelta component delta}s representing the 
	 * a change set between a {@link IBranchPointCalculationStrategy#getSourceBranchPoint() source} and 
	 * a{@link IBranchPointCalculationStrategy#getTargetBranchPoint() target} branch points.
	 * @param strategy the branch point calculation strategy.
	 * @return a collection of component deltas representing the changes between two {@link IBranchPoint branch point}s.
	 */
	Collection<C> getComponentDeltas(final IBranchPointCalculationStrategy strategy);
	
	
	/**
	 * Returns with a collection of {@link IHistoryInfo history info} representing all the changes made on a component
	 * given by its repository specific storage key (CDO ID).
	 * @param strategy strategy providing the two branch points where calculation has to be provided.
	 * @param delta representing the changed component.
	 * @return the a collection of history info.
	 */
	Collection<IHistoryInfo> getChangesForComponent(final IBranchPointCalculationStrategy strategy, final IChangedComponentCDOIDs delta);

}