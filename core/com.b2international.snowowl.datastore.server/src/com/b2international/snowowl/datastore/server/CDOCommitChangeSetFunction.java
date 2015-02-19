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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.CDOCommitChangeSet;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOFunction;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Function for evaluating a {@link ICDOCommitChangeSet} instance.
 * @see CDOUtils#apply(CDOFunction)
 */
public abstract class CDOCommitChangeSetFunction implements CDOFunction<Void> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CDOCommitChangeSetFunction.class);
	
	private final IBranchPath branchPath;
	private final CDOChangeSetData changeSetData;
	@Nullable private final String userId;

	private final ICDOConnection connection;

	public CDOCommitChangeSetFunction(final ICDOConnection connection, final IBranchPath branchPath, final CDOChangeSetData changeSetData, @Nullable final String userId) {
		this.connection = Preconditions.checkNotNull(connection, "CDO connection argument cannot be null.");
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		this.changeSetData = Preconditions.checkNotNull(changeSetData, "CDO change set data argument cannot be null.");
		this.userId = userId;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.CDOFunction#apply()
	 */
	@Override
	public final Void apply() {

		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(changeSetData, "CDO change set data was null on branch: "  + branchPath);
		
		final CDOBranch branch = connection.getBranch(branchPath);
		
		CDOView view = null;
		
		try {
			view = connection.createView(branch);
			final long lastCommitTime = CDOServerUtils.getLastCommitTime(view.getBranch());
			
			//detached IDs
			final List<CDOIDAndVersion> detachedIds = changeSetData.getDetachedObjects();
			final CDOID[] detachedObejctIds = new CDOID[detachedIds.size()];
			for (int i = 0; i < detachedObejctIds.length; i++) {
				final CDOID id = detachedIds.get(i).getID();
				detachedObejctIds[i] = id; 
			}
			
			//new objects
			final List<CDOIDAndVersion> newIds = changeSetData.getNewObjects();
			final EObject[] newObjectIds = new EObject[newIds.size()];
			for (int i = 0; i < newObjectIds.length; i++) {
				final CDOID id = newIds.get(i).getID();
				final CDOObject newObject = CDOUtils.getObjectIfExists(CDOUtils.check(view), id);
				Preconditions.checkNotNull(newObject, "New CDO object cannot be found. ID: " + id);
				newObjectIds[i] = newObject; 
			}
			
			final Map<CDOID, CDORevisionDelta> revisionDeltas = newHashMap();
			
			//dirty objects
			final List<CDORevisionKey> dirtyIds = changeSetData.getChangedObjects();
			final List<EObject> dirtyObjectIdsList = Lists.newArrayList();
			for (int i = 0; i < dirtyIds.size(); i++) {
				final CDORevisionKey revisionKey = dirtyIds.get(i);
				final CDOID id = revisionKey.getID();
				final CDOObject changedObject = CDOUtils.getObjectIfExists(CDOUtils.check(view), id);
				Preconditions.checkNotNull(changedObject, "Changed CDO object cannot be found. ID: " + id);
				dirtyObjectIdsList.add(changedObject);
				Preconditions.checkState(revisionKey instanceof CDORevisionDelta, "CDO revision key was not a delta but a " + revisionKey);
				revisionDeltas.put(id, (CDORevisionDelta) revisionKey);
			}
			
			//transform changed objects to an array
			final EObject[] dirtyObjectIds = Iterables.toArray(dirtyObjectIdsList, EObject.class);
			
			final CDOCommitChangeSet commitChangeSet = new CDOCommitChangeSet(
					view,
					userId, 
					newObjectIds, 
					dirtyObjectIds, 
					getDetachedObjectTypes(branchPath, detachedObejctIds),
					revisionDeltas,
					lastCommitTime);
			
			apply(commitChangeSet);
			
		} finally {
			
			LifecycleUtil.deactivate(view);
			
		}
		
		return null; //instance
	}
	
	protected abstract void apply(final ICDOCommitChangeSet commitChangeSet);
	
	/*returns with a map of CDO IDs and class identifying the detached objects*/
	private Map<CDOID, EClass> getDetachedObjectTypes(final IBranchPath branchPath, final CDOID[] detachedObjects) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		if (CompareUtils.isEmpty(detachedObjects)) {
			return Collections.<CDOID, EClass>emptyMap();
		}

		final Map<CDOID, EClass> $ = Maps.newHashMap();
		
		for (final CDOID detachedCdoId : detachedObjects) {
			
			final EClass eclass = EClassProviderBroker.INSTANCE.getEClass(branchPath, detachedCdoId);

			//this is not a real issue.
			//eclass provider is not necessary for object's that changes are not tracked with any ICDOChangeProcessor instance while updating index.
			if (null == eclass) {
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.error("EClass cannot be found for CDO ID " + detachedCdoId);
				}
				
			} else {
				$.put(detachedCdoId, eclass);
			}
			
		}
		return $;
	}
	
}