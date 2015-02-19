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

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.cdo.EmptyCDOChangeSetData;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Singleton service providing {@link CDOChangeSetData change set data} based on 
 * the {@link IBranchPointCalculationStrategy branch point calculation strategy}.
 * @see IBranchPointCalculationStrategy
 */
public enum CDOChangeSetDataProvider implements ICDOChangeSetDataProvider {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.ICDOChangeSetDataProvider#getChangeSetData(com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy)
	 */
	@Override
	public CDOChangeSetData getChangeSetData(final IBranchPointCalculationStrategy strategy) {
		
		Preconditions.checkNotNull(strategy, "Strategy argument cannot be null.");
		
		final IBranchPoint sourceBranchPoint = Preconditions.checkNotNull(strategy.getSourceBranchPoint(), "Source branch point cannot be null.");
		final IBranchPoint targetBranchPoint = Preconditions.checkNotNull(strategy.getTargetBranchPoint(), "Target branch point cannot be null.");
		
		final CDOBranchPoint cdoSourceBranchPoint = BranchPointUtils.convert(sourceBranchPoint);
		final CDOBranchPoint cdoTargetBranchPoint = BranchPointUtils.convert(targetBranchPoint);
		
		final CDOChangeSetData changeSetData = CDOServerUtils.compareRevisions(cdoSourceBranchPoint, cdoTargetBranchPoint);

		return Preconditions.checkNotNull(changeSetData, "Change set data was null.");
		
	}
	
	/**
	 * Calculates the CDO change set between two branch points in all repositories. 
	 * @param sourceBranchPath the source branch path. 
	 * @param sourceTimestamp the source timestamp.
	 * @param targetBranchPath the target branch path.
	 * @param targetTimestamp the target timestamp.
	 * @return a map of change set data where the keys are the repository unique IDs and the values are the actual change set.
	 */
	public Map<String, CDOChangeSetData> getChangeSetData(final IBranchPath sourceBranchPath, final long sourceTimestamp, final IBranchPath targetBranchPath, final long targetTimestamp) {
		
		final ICDOConnectionManager manager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
		final Iterable<Pair<String, CDOChangeSetData>> itr = 
				Iterables.transform(manager, new GetChangeSetDataFunction(sourceBranchPath, sourceTimestamp, targetBranchPath, targetTimestamp));
			
		final Map<String, CDOChangeSetData> $ = Maps.newHashMap();
		
		for (final Pair<String, CDOChangeSetData> pair : itr) {
			
			$.put(pair.getA(), pair.getB());
			
		}
		
		return Collections.unmodifiableMap($);
		
	}
	
	/**
	 * Function for specifying the {@link CDOChangeSetData change set data} between two branch points. 
	 *
	 */
	private static final class GetChangeSetDataFunction implements Function<ICDOConnection, Pair<String, CDOChangeSetData>> {

		private final IBranchPath sourceBranchPath;
		private final IBranchPath targetBranchPath;
		private final long sourceTimestamp;
		private final long targetTimestamp;

		private GetChangeSetDataFunction(final IBranchPath sourceBranchPath, final long sourceTimestamp, final IBranchPath targetBranchPath, final long targetTimestamp) {
			this.sourceBranchPath = Preconditions.checkNotNull(sourceBranchPath, "Source branch path argument cannot be null.");
			this.targetBranchPath = Preconditions.checkNotNull(targetBranchPath, "Target branch path argument cannot be null.");
			this.sourceTimestamp = sourceTimestamp;
			this.targetTimestamp = targetTimestamp;
		}
		
		
		/* (non-Javadoc)
		 * @see com.google.common.base.Function#apply(java.lang.Object)
		 */
		@Override
		public Pair<String, CDOChangeSetData> apply(final ICDOConnection connection) {
			
			Preconditions.checkNotNull(connection, "CDO connection argument cannot be null.");
			
			final CDOBranch sourceBranch = connection.getBranch(sourceBranchPath);
			final CDOBranch targetBranch = connection.getBranch(targetBranchPath);
			
			if (null == sourceBranch || null == targetBranch) {
				
				return Pair.of(connection.getUuid(), EmptyCDOChangeSetData.INSTANCE);
				
			}
			
			final CDOBranchPoint sourceBranchPoint = sourceBranch.getPoint(sourceTimestamp);
			final CDOBranchPoint targetBranchPoint = targetBranch.getPoint(targetTimestamp);
			
			return Pair.of(connection.getUuid(), CDOServerUtils.compareRevisions(sourceBranchPoint, targetBranchPoint));
			
		}
		
	}
	
}