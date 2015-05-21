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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndDetachedInTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInTargetAndDetachedInSourceConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.datastore.cdo.ConflictWrapper;
import com.b2international.snowowl.datastore.cdo.ConflictingChange;

/**
 * Broker for terminology specific {@link ICDOConflictProcessor conflict processors}.
 */
public enum CDOConflictProcessorBroker {

	/**
	 * The singleton instance.
	 */
	INSTANCE;

	/**
	 * Converts CDO conflict representations to application-specific ones, and appends them to the specified set if the
	 * conversion was successful.
	 * 
	 * @param conflict the conflict to process (may not be {@code null})
	 * @param conflictSet the set of conflicts to append to (may not be {@code null})
	 */
	public void processConflict(final Conflict conflict, final Set<ConflictWrapper> conflictSet) {	
		checkNotNull(conflict, "CDO conflict to process may not be null.");
		checkNotNull(conflictSet, "Converted conflicts set may not be null.");

		if (conflict instanceof ChangedInSourceAndTargetConflict) {

			final CDORevisionDelta sourceDelta = ((ChangedInSourceAndTargetConflict) conflict).getSourceDelta();
			final CDORevisionDelta targetDelta = ((ChangedInSourceAndTargetConflict) conflict).getTargetDelta();

			final Map<EStructuralFeature, CDOFeatureDelta> sourceDeltaMap = ((InternalCDORevisionDelta) sourceDelta).getFeatureDeltaMap();
			final Map<EStructuralFeature, CDOFeatureDelta> targetDeltaMap = ((InternalCDORevisionDelta) targetDelta).getFeatureDeltaMap();

			for (final EStructuralFeature targetFeature : targetDeltaMap.keySet()) {
				final CDOFeatureDelta sourceFeatureDelta = sourceDeltaMap.get(targetFeature);
				final CDOFeatureDelta targetFeatureDelta = targetDeltaMap.get(targetFeature);

				if (sourceFeatureDelta instanceof CDOSetFeatureDelta && targetFeatureDelta instanceof CDOSetFeatureDelta) {
					final ConflictingChange changeOnSource = new ConflictingChange(sourceDelta.getID(), targetFeature, ((CDOSetFeatureDelta) sourceFeatureDelta).getValue());
					final ConflictingChange changeOnTarget = new ConflictingChange(targetDelta.getID(), targetFeature, ((CDOSetFeatureDelta) targetFeatureDelta).getValue());
					conflictSet.add(new ConflictWrapper(changeOnTarget, changeOnSource));
				}
			}

		} else if (conflict instanceof ChangedInSourceAndDetachedInTargetConflict){	

			final CDORevisionDelta sourceDelta = ((ChangedInSourceAndDetachedInTargetConflict) conflict).getSourceDelta();
			final Map<EStructuralFeature, CDOFeatureDelta> sourceDeltaMap = ((InternalCDORevisionDelta) sourceDelta).getFeatureDeltaMap();

			for (final EStructuralFeature sourceFeature : sourceDeltaMap.keySet()) {
				final CDOFeatureDelta sourceFeatureDelta = sourceDeltaMap.get(sourceFeature);

				if (sourceFeatureDelta instanceof CDOSetFeatureDelta) {
					final ConflictingChange changeOnSource = new ConflictingChange(sourceDelta.getID(), sourceFeature, ((CDOSetFeatureDelta) sourceFeatureDelta).getValue());
					final ConflictingChange changeOnTarget = new ConflictingChange(DELETED, conflict.getID());
					conflictSet.add(new ConflictWrapper(changeOnTarget, changeOnSource));
				}
			}

		} else if (conflict instanceof ChangedInTargetAndDetachedInSourceConflict) {

			final CDORevisionDelta targetDelta = ((ChangedInTargetAndDetachedInSourceConflict) conflict).getTargetDelta();
			final Map<EStructuralFeature, CDOFeatureDelta> targetDeltaMap = ((InternalCDORevisionDelta) targetDelta).getFeatureDeltaMap();

			for (final EStructuralFeature targetFeature : targetDeltaMap.keySet()) {
				final CDOFeatureDelta targetFeatureDelta = targetDeltaMap.get(targetFeature);

				if (targetFeatureDelta instanceof CDOSetFeatureDelta) {
					final ConflictingChange changeOnSource = new ConflictingChange(DELETED, conflict.getID());
					final ConflictingChange changeOnTarget = new ConflictingChange(targetDelta.getID(), targetFeature, ((CDOSetFeatureDelta) targetFeatureDelta).getValue());
					conflictSet.add(new ConflictWrapper(changeOnTarget, changeOnSource));
				}
			}
		}
	}

	public ICDOConflictProcessor getProcessor(final String repositoryUuid) {
		checkNotNull(repositoryUuid, "Repository identifier may not be null.");

		final Collection<ICDOConflictProcessor> processors = Extensions.getExtensions(ICDOConflictProcessor.EXTENSION_ID, ICDOConflictProcessor.class);
		for (final ICDOConflictProcessor processor : processors) {
			if (repositoryUuid.equals(processor.getRepositoryUuid())) {
				return processor;
			}
		}

		return new NullCDOConflictProcessor(repositoryUuid);
	}
}
