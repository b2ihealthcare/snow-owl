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
package com.b2international.snowowl.datastore.server.cdo;

import static com.b2international.commons.ChangeKind.ADDED;
import static com.b2international.commons.ChangeKind.DELETED;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.get;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.ConflictWrapper;
import com.b2international.snowowl.datastore.cdo.ConflictingChange;
import com.b2international.snowowl.datastore.server.CDOServerUtils;

/**
 * Abstract superclass for {@link ICDOConflictProcessor}s that only want to report a subset of possible application-level conflicts.
 */
public abstract class AbstractCDOConflictProcessor implements ICDOConflictProcessor {

	@Override
	public ConflictWrapper checkConflictForNewObjects(final CDOChangeSetData targetChangeSet, final CDOIDAndVersion newInSource, final CDOView sourceView) {
		final Collection<CDOID> detachedTargetIds = CDOIDUtils.extractIds(targetChangeSet.getDetachedObjects());
		return checkConflict(detachedTargetIds, newInSource, sourceView);
	}
	
	/**
	 * Creates and returns with a conflict wrapper describing that the conflicting target ID has been detached on target while a 
	 * new component has been created on the source and referencing the detached target component.
	 * <p>May return with {@code null} if the conflicting target component ID is {@code null}.
	 * @param conflictingTargetId the ID that has been detached on the target before the synchronization. Optional. Can be {@code null}.
	 * @param newInSource the new component that has been added on the source which is referencing the detached target.
	 * @return the conflict wrapper describing the conflict, or {@code null} if there are any conflicts.
	 */
	@Nullable protected ConflictWrapper creatConflictWrapper(@Nullable final CDOID conflictingTargetId, final CDOIDAndVersion newInSource) {
		if (null == conflictingTargetId) {
			return null;
		} else {
			final ConflictingChange changeOnTarget = new ConflictingChange(DELETED, conflictingTargetId);
			final ConflictingChange changeOnSource = new ConflictingChange(ADDED, newInSource.getID());
			return new ConflictWrapper(changeOnTarget, changeOnSource);
		}
	}

	@Override
	public ConflictWrapper checkConflictForDetachedObjects(final Map<CDOID, CDORevisionKey> changedComponentsMapping, 
			final CDOIDAndVersion detachedOnSource, final CDOView sourceView, final CDOView targetView) {
		
		if (shouldCheckReleasedFlag()) {
			//here we only care about objects that has been deleted on source and modified on target
			//assuming that deletion is not allowed by default if a component is already released on source
			final CDORevisionKey changedTargetRevision = changedComponentsMapping.get(detachedOnSource.getID());
			if (null != changedTargetRevision) {
				final CDOID id = changedTargetRevision.getID();
				final List<CDORevision> revisions = CDOServerUtils.getRevisions(targetView, id);
				checkState(revisions.size() == 1, "Expected exactly 1 revision for ID: '" + id + "'. Got: '" + revisions.size() + "'.");
				final CDORevision revision = get(revisions, 0);
				if (revision instanceof InternalCDORevision) {
					final ConflictWrapper conflictWrapper = checkReleasedForDetachedObjects((InternalCDORevision) revision);
					if (null != conflictWrapper) {
						return conflictWrapper;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if the object new in the source change set identified by its {@link CDOIDAndVersion} conflicts with the set of objects detached
	 * on the target, identified by the specified {@link CDOID} collection.
	 * @param detachedTargetIds the detached {@link CDOID}s of the target change set.
	 * @param newInSource a new object's {@link CDOIDAndVersion} in the source change set. 
	 * @param sourceView a view opened on the source change set's branch. It is the caller's responsibility to close the view.
	 * @return an instance wrapping the conflict. 
	 */
	protected abstract ConflictWrapper checkConflict(final Collection<CDOID> detachedTargetIds, final CDOIDAndVersion newInSource, final CDOView sourceView);
	
	/**Returns with {@code true} if the released flag for a detached component has to be checked against application specific conflicts.*/
	protected boolean shouldCheckReleasedFlag() {
		return false;
	}
	
	/**Returns with a conflict if the given revision argument (loaded from the target as a changed component) has conflicts
	 *due to released flag property mismatch. In other words component has been deleted from source and released on target meanwhile.*/
	protected ConflictWrapper checkReleasedForDetachedObjects(final InternalCDORevision changedTargetRevision) {
		return null;
	}
	
	
}