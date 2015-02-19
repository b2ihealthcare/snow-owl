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

import static com.b2international.commons.ChangeKind.DELETED;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndDetachedInTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInTargetAndDetachedInSourceConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ChangeKind;
import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ConflictWrapper;
import com.b2international.snowowl.datastore.cdo.ConflictingChange;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;

/**
 * Broker for terminology specific {@link ICDOConflictProcessor conflict processors}.
 */
public enum CDOConflictProcessorBroker {

	/**The singleton broker.*/
	INSTANCE;

	/**
	 * Processes the conflict. Delegates the terminology specific behavior to the terminology specific {@link ICDOConflictProcessor}. 
	 * @param conflict the conflict to process.<br>Could be {@code null}.
	 * @return the conflict wrapper as the reason of the conflict.
	 */
	@Nullable public ConflictWrapper processConflict(final Conflict conflict) {	
		
		//the same attribute of an object has been changed on the main and the task branch
		if (conflict instanceof ChangedInSourceAndTargetConflict) {
			
			final CDORevisionDelta taskDelta = ((ChangedInSourceAndTargetConflict) conflict).getSourceDelta();
			final CDORevisionDelta mainDelta = ((ChangedInSourceAndTargetConflict) conflict).getTargetDelta();
			
			//because of the type of the merger (DefaultCDOMerger.PerFeature.ManyValued()) this type of conflict can only occur if the same attribute has been changed
			if (1 != taskDelta.getFeatureDeltas().size() || 1 != mainDelta.getFeatureDeltas().size()) {
				
				LOGGER.error("Number of feature deltas should be 1. Was " + taskDelta.getFeatureDeltas() + 
						" on source and " + mainDelta.getFeatureDeltas() + " on target.");
				
				throw new IllegalArgumentException("Number of feature deltas should be 1.");
				
			}
			
			if (mainDelta.getFeatureDeltas().get(0) instanceof CDOSetFeatureDelta && taskDelta.getFeatureDeltas().get(0) instanceof CDOSetFeatureDelta) {
				
				final CDOSetFeatureDelta mainFeatureDelta = (CDOSetFeatureDelta) mainDelta.getFeatureDeltas().get(0);
				final CDOSetFeatureDelta taskFeatureDelta = (CDOSetFeatureDelta) taskDelta.getFeatureDeltas().get(0);
				
				final ConflictingChange changeOnMain = new ConflictingChange(mainDelta.getID(), mainFeatureDelta.getFeature(), mainFeatureDelta.getValue());
				final ConflictingChange changeOnTask = new ConflictingChange(taskDelta.getID(), taskFeatureDelta.getFeature(), taskFeatureDelta.getValue());
				return new ConflictWrapper(changeOnMain, changeOnTask);
			}
			
			
		} else if (conflict instanceof ChangedInSourceAndDetachedInTargetConflict){	

			// ChangedInSourceAndDetachedInTargetConflict -> an object has been deleted on the main branch, and modified on the task branch
			final CDORevisionDelta taskDelta = ((ChangedInSourceAndDetachedInTargetConflict) conflict).getSourceDelta();
			// handle only attribute changes here
			if (taskDelta.getFeatureDeltas().size() == 1 && taskDelta.getFeatureDeltas().get(0) instanceof CDOSetFeatureDelta) {
				
				final CDOSetFeatureDelta setFeatureDelta = (CDOSetFeatureDelta) taskDelta.getFeatureDeltas().get(0);
				final ConflictingChange changeOnMain = new ConflictingChange(DELETED, conflict.getID());
				final ConflictingChange changeOnTask = new ConflictingChange(taskDelta.getID(), setFeatureDelta.getFeature(), setFeatureDelta.getValue());
				return new ConflictWrapper(changeOnMain, changeOnTask);
				
			}
			
		// ChangedInTargetAndDetachedInSourceConflict -> an object has been deleted on the task branch, and modified on the main branch									
		} else if (conflict instanceof ChangedInTargetAndDetachedInSourceConflict) {
			
			final CDORevisionDelta mainDelta = ((ChangedInTargetAndDetachedInSourceConflict) conflict).getTargetDelta();
			// list changes in ChangedInTargetAndDetachedInSourceConflict -> not a conflict, resolved separately
			if (mainDelta.getFeatureDeltas().size() == 1 && mainDelta.getFeatureDeltas().get(0) instanceof CDOSetFeatureDelta) {
				
				final CDOSetFeatureDelta setFeatureDelta = (CDOSetFeatureDelta) mainDelta.getFeatureDeltas().get(0);
				final ConflictingChange changeOnMain = new ConflictingChange(mainDelta.getID(), setFeatureDelta.getFeature(), setFeatureDelta.getValue());
				final ConflictingChange changeOnTask = new ConflictingChange(ChangeKind.DELETED, conflict.getID());
				return new ConflictWrapper(changeOnMain, changeOnTask);
				
			}
			
		}
		
		return null;
		
	}

	/**
	 * See {@link ICDOConflictProcessor#checkConflictForNewObjects(CDOChangeSetData, CDOIDAndVersion, CDOView)}.
	 */
	@Nullable public ConflictWrapper checkConflictForNewObjects(final CDOChangeSetData targetChangeSet, final CDOIDAndVersion newInSource, final CDOView sourceView) {
		return getProcessor(newInSource.getID()).checkConflictForNewObjects(targetChangeSet, newInSource, sourceView);
	}
	
	/**
	 * See {@link ICDOConflictProcessor#checkConflictForDetachedObjects(Map, CDOIDAndVersion, CDOView, CDOView)}.
	 */
	@Nullable public ConflictWrapper checkConflictForDetachedObjects(final Map<CDOID, CDORevisionKey> changedComponentsMapping, final CDOIDAndVersion detachedOnSource, final CDOView sourceView, final CDOView targetView) {
		return getProcessor(detachedOnSource.getID()).checkConflictForDetachedObjects(changedComponentsMapping, detachedOnSource, sourceView, targetView);
	}

	/**
	 * Removes the objects specified with a bunch of CDO ID.
	 * @param objectToRemove the collection of CDO IDs which object should be detached. 
	 * @param view view for resolving the CDO IDs to the corresponding objects.
	 */
	public void detachConflictingObject(final Collection<CDOID> objectToRemove, final CDOView view) {
		checkNotNull(objectToRemove, "objectToRemove");
		check(view);
		for (final CDOID id : objectToRemove) {
			final CDOObject object = CDOUtils.check(view).getObject(id);
			if (null != object) {
				getProcessor(id).detachConflictingObject(object);
			} else {
				LOGGER.warn("Cannot resolve object. ID: " + id + ". [View : " + view + "]");
			}
		}
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CDOConflictProcessorBroker.class);

	/*returns with the terminology specific conflict processor*/
	public ICDOConflictProcessor getProcessor(final CDOID id) {
		checkNotNull(id, "id");
		final String repositoryUuid = getServiceForClass(ICDOConnectionManager.class).get(id).getUuid();
		for (final ICDOConflictProcessor processor : Extensions.getExtensions(ICDOConflictProcessor.CONFLICT_PROCESSOR_EXTENSION_ID, ICDOConflictProcessor.class)) {
			if (checkNotNull(repositoryUuid, "repositoryUuid").equals(processor.getRepositoryUuid())) {
				return processor;
			}
		}
		
		return NullCDOConflictProcessor.INSTANCE;
	}

	
}