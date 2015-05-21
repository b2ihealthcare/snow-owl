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

import static com.b2international.snowowl.datastore.server.index.IIndexPostProcessingConfiguration.DEFAULT_CONFIGURATION;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.delta.SourceToHeadBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.server.CDOChangeSetDataProvider;
import com.b2international.snowowl.datastore.server.CDOCommitChangeSetFunction;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.google.common.base.Stopwatch;

/**
 * Base index post processor configuration.
 *
 */
public abstract class IndexPostProcessor implements IIndexPostProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexPostProcessor.class);
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.index.IIndexPostProcessor#postProcess(com.b2international.snowowl.datastore.server.index.IIndexPostProcessingConfiguration)
	 */
	@Override
	public void postProcess(final IIndexPostProcessingConfiguration configuration) {
		
		checkNotNull(configuration, "configuration");
		
		if (!DEFAULT_CONFIGURATION.equals(configuration)) {
			 
			final AtomicBoolean requiresPostProcessing = checkNotNull(configuration.getRequiresPostProcessing());
			final IBranchPath branchPath = checkNotNull(configuration.getBranchPath());
			final ICDOChangeProcessor cdoChangeProcessor = checkNotNull(getChangeProcessor(branchPath));
			final long timestamp = configuration.getTimestamp();
			final Stopwatch stopwatch = Stopwatch.createStarted();
			
			if (requiresPostProcessing.compareAndSet(true, false)) {

				if (ICDOChangeProcessor.NULL_IMPL == cdoChangeProcessor) {
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("Index reviving took " + stopwatch + " on '" + branchPath + "' [" + getToolingFeatureName() + "].");
					}
					return;
				}

				final ICDOConnectionManager connectionManager = ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
				final ICDOConnection connection = connectionManager.getByUuid(getRepositoryUuid());
				
				final CDOBranch cdoBranch = connection.getBranch(branchPath);
				final long lastCommitTime = CDOServerUtils.getLastCommitTime(cdoBranch);
				
				//no changes on branch
				if (Long.MIN_VALUE == lastCommitTime) {
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("Index reviving took " + stopwatch + " on '" + branchPath + "' [" + getToolingFeatureName() + "].");
					}
					return;
				}
				
				//sanity check. current timestamp is the HEAD on the current branch, nothing to do.
				if (lastCommitTime == timestamp && CDOBranchPoint.UNSPECIFIED_DATE != timestamp) {
					
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("Index reviving took " + stopwatch + " on '" + branchPath + "' [" + getToolingFeatureName() + "].");
					}
					return;
				}
				
				final IBranchPointCalculationStrategy strategy = new SourceToHeadBranchPointCalculationStrategy(connection, branchPath, timestamp);
				final CDOChangeSetData changeSetData = CDOChangeSetDataProvider.INSTANCE.getChangeSetData(strategy);
				
				if (!changeSetData.isEmpty()) {
					
					CDOUtils.apply(new CDOCommitChangeSetFunction(connection, branchPath, changeSetData, /*user ID*/"") {
						@Override protected void apply(final ICDOCommitChangeSet commitChangeSet) {
							
							try {
								
								cdoChangeProcessor.process(commitChangeSet);
								if (cdoChangeProcessor.hadChangesToProcess()) {
									cdoChangeProcessor.commit();
									cdoChangeProcessor.afterCommit();
								}
								
							} catch (final SnowowlServiceException e) {
								throw new IndexException("Failed to revive index.", e);
							}
							
							
						}
					});
					
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("Index reviving with post processing took " + stopwatch + " on '" + branchPath + "' [" + getToolingFeatureName() + "].");
					}
					
				} else {
					
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("Index reviving took " + stopwatch + " on '" + branchPath + "' [" + getToolingFeatureName() + "].");
					}
					
				}
				
			}
		
			
		}
	}
	
	private String getToolingFeatureName() {
		return CodeSystemUtils.getSnowOwlToolingName(getRepositoryUuid());
	}

	/**Returns with the UUID of the associated repository.*/
	protected abstract String getRepositoryUuid();
	
	/**Returns with the underlying CDOish change processor to feed the changes into the index.*/
	protected abstract ICDOChangeProcessor getChangeProcessor(final IBranchPath branchPath);

}