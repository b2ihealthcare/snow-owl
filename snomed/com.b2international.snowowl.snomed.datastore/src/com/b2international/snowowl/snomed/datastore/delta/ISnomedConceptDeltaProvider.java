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
package com.b2international.snowowl.snomed.datastore.delta;

import java.util.Collection;

import javax.annotation.Nullable;

import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.delta.ComponentDelta;
import com.b2international.snowowl.datastore.delta.HierarchicalComponentDelta;
import com.b2international.snowowl.datastore.delta.IComponentDeltaProvider;

/**
 * Server side interface for the SNOMED CT component delta provider.
 * 
 */
public interface ISnomedConceptDeltaProvider extends IComponentDeltaProvider<HierarchicalComponentDelta> {

	/**
	 * Returns a mapping between reference set member IDs and the changes of the corresponding components, which happened on the specified branch.
	 * 
	 * @param branchPathMap the branch path map where the calculation is performed.
	 * @param identifierConceptId identifier concept ID of a reference set which changes has to be tracked.
	 * @return a mapping between reference set member IDs and the corresponding changes on the branch.
	 */
	<D extends ComponentDelta> Collection<D> getRefSetMemberDeltas(final IBranchPathMap branchPathMap, final String  identifierConceptId);
	
	/**
	 * Promotes the reference set member changes given as a collection of component deltas. The collection of component deltas could be from different branches.
	 * The destination branch of the promotion process will be the parent branch of the component deltas. 
	 * @param deltas the components to promote.
	 * @param identifierId the reference set identifier concept ID.
	 * @param userId unique user identifier.
	 * @param commitComment commit comment for the promotion.
	 * @return a throwable indicating if any error, warning occurred while promoting the changes. Could be {@code null}. 
	 */
	//TODO move this functionality to somewhere else.
	@Nullable Throwable promoteChanges(final Collection<ComponentDelta> deltas, final String identifierId, final String userId, final String commitComment);
}