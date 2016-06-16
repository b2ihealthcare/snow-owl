/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed;

import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndDetachedInTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInTargetAndDetachedInSourceConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;

import com.b2international.snowowl.datastore.server.cdo.AddedInSourceAndDetachedInTargetConflict;
import com.b2international.snowowl.datastore.server.cdo.AddedInSourceAndTargetConflict;
import com.b2international.snowowl.datastore.server.cdo.AddedInTargetAndDetachedInSourceConflict;
import com.b2international.snowowl.datastore.server.cdo.ConflictMapper;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.7
 */
public class SnomedMergeConflictMapper {

	public static SnomedMergeConflict convert(final Conflict conflict, final CDOTransaction sourceTransaction, final CDOTransaction targetTransaction) {
		if (conflict instanceof ChangedInSourceAndTargetConflict) {
			return from((ChangedInSourceAndTargetConflict) conflict, sourceTransaction, targetTransaction);
		} else if (conflict instanceof ChangedInSourceAndDetachedInTargetConflict) {
			return from((ChangedInSourceAndDetachedInTargetConflict) conflict, sourceTransaction, targetTransaction);
		} else if (conflict instanceof ChangedInTargetAndDetachedInSourceConflict) {
			return from((ChangedInTargetAndDetachedInSourceConflict) conflict, sourceTransaction, targetTransaction);
		} else if (conflict instanceof AddedInSourceAndTargetConflict) {
			return from((AddedInSourceAndTargetConflict) conflict, sourceTransaction, targetTransaction);
		} else if (conflict instanceof AddedInSourceAndDetachedInTargetConflict) {
			return from((AddedInSourceAndDetachedInTargetConflict) conflict, sourceTransaction, targetTransaction);
		} else if (conflict instanceof AddedInTargetAndDetachedInSourceConflict) {
			return from((AddedInTargetAndDetachedInSourceConflict) conflict, sourceTransaction, targetTransaction);
		}
		throw new IllegalArgumentException("Unknown conflict type: " + conflict);
	}

	public static SnomedMergeConflict from(final ChangedInSourceAndTargetConflict conflict, final CDOTransaction sourceTransaction,
			final CDOTransaction targetTransaction) {

		final String sourceComponentId = getComponentId(sourceTransaction, conflict.getSourceDelta().getID());
		final String targetComponentId = getComponentId(targetTransaction, conflict.getTargetDelta().getID());

		final SnomedMergeConflict snomedMergeConflict = new SnomedMergeConflict(sourceComponentId, targetComponentId, String.format(
				ConflictMapper.CHANGED_IN_SOURCE_AND_TARGET_MESSAGE, sourceComponentId));

		snomedMergeConflict.setSourceType(conflict.getSourceDelta().getEClass().getName());
		snomedMergeConflict.getChangedSourceFeatures().addAll(transformFeatureDeltas(conflict.getSourceDelta().getFeatureDeltas()));

		snomedMergeConflict.setTargetType(conflict.getTargetDelta().getEClass().getName());
		snomedMergeConflict.getChangedTargetFeatures().addAll(transformFeatureDeltas(conflict.getTargetDelta().getFeatureDeltas()));

		return snomedMergeConflict;
	}

	public static SnomedMergeConflict from(final ChangedInSourceAndDetachedInTargetConflict conflict, final CDOTransaction sourceTransaction,
			final CDOTransaction targetTransaction) {

		final String componentId = getComponentIdWithFallback(sourceTransaction, targetTransaction, conflict.getSourceDelta().getID());

		final SnomedMergeConflict snomedMergeConflict = new SnomedMergeConflict(componentId, null, String.format(
				ConflictMapper.CHANGED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE, componentId));

		snomedMergeConflict.setSourceType(conflict.getSourceDelta().getEClass().getName());
		snomedMergeConflict.getChangedSourceFeatures().addAll(transformFeatureDeltas(conflict.getSourceDelta().getFeatureDeltas()));

		return snomedMergeConflict;
	}

	public static SnomedMergeConflict from(final ChangedInTargetAndDetachedInSourceConflict conflict, final CDOTransaction sourceTransaction,
			final CDOTransaction targetTransaction) {

		final String targetComponentId = getComponentIdWithFallback(targetTransaction, sourceTransaction, conflict.getTargetDelta().getID());

		final SnomedMergeConflict snomedMergeConflict = new SnomedMergeConflict(null, targetComponentId, String.format(
				ConflictMapper.CHANGED_IN_TARGET_DETACHED_IN_SOURCE_MESSAGE, targetComponentId));

		snomedMergeConflict.setTargetType(conflict.getTargetDelta().getEClass().getName());
		snomedMergeConflict.getChangedTargetFeatures().addAll(transformFeatureDeltas(conflict.getTargetDelta().getFeatureDeltas()));

		return snomedMergeConflict;
	}

	public static SnomedMergeConflict from(final AddedInSourceAndTargetConflict conflict, final CDOTransaction sourceTransaction,
			final CDOTransaction targetTransaction) {

		final String sourceComponentId = getComponentId(sourceTransaction, conflict.getSourceId());
		final String type = getType(conflict.getSourceId(), sourceTransaction);
		
		final SnomedMergeConflict snomedMergeConflict = new SnomedMergeConflict(sourceComponentId, null, String.format(
				ConflictMapper.ADDED_IN_SOURCE_AND_TARGET_MESSAGE, type, sourceComponentId));

		snomedMergeConflict.setSourceType(type);
		snomedMergeConflict.setTargetType(type);

		return snomedMergeConflict;
	}

	public static SnomedMergeConflict from(final AddedInSourceAndDetachedInTargetConflict conflict, final CDOTransaction sourceTransaction,
			final CDOTransaction targetTransaction) {

		final String sourceComponentId = getComponentIdWithFallback(sourceTransaction, targetTransaction, conflict.getSourceId());
		final String targetComponentId = getComponentIdWithFallback(targetTransaction, sourceTransaction, conflict.getTargetId());
		final String sourceType = getType(conflict.getSourceId(), sourceTransaction);
		final String targetType = getType(conflict.getTargetId(), sourceTransaction);

		final SnomedMergeConflict snomedMergeConflict = new SnomedMergeConflict(sourceComponentId, targetComponentId, 
				String.format(ConflictMapper.ADDED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE, sourceType, sourceComponentId, targetType, targetComponentId));

		snomedMergeConflict.setSourceType(sourceType);
		snomedMergeConflict.setTargetType(targetType);

		return snomedMergeConflict;
	}
	
	public static SnomedMergeConflict from(final AddedInTargetAndDetachedInSourceConflict conflict, final CDOTransaction sourceTransaction, final CDOTransaction targetTransaction) {
		final String sourceComponentId = getComponentIdWithFallback(sourceTransaction, targetTransaction, conflict.getSourceId());
		final String targetComponentId = getComponentIdWithFallback(targetTransaction, sourceTransaction, conflict.getTargetId());
		final String sourceType = getType(conflict.getSourceId(), targetTransaction);
		final String targetType = getType(conflict.getTargetId(), targetTransaction);

		final SnomedMergeConflict snomedMergeConflict = new SnomedMergeConflict(sourceComponentId, targetComponentId, 
				String.format(ConflictMapper.ADDED_IN_TARGET_DETACHED_IN_SOURCE_MESSAGE, targetType, targetComponentId, sourceType, sourceComponentId));

		snomedMergeConflict.setSourceType(sourceType);
		snomedMergeConflict.setTargetType(targetType);

		return snomedMergeConflict;
	}

	private static List<String> transformFeatureDeltas(final List<CDOFeatureDelta> featureDeltas) {
		return FluentIterable.from(featureDeltas).transform(new Function<CDOFeatureDelta, String>() {
			@Override public String apply(final CDOFeatureDelta input) {
				return input.getFeature().getName();
			}
		}).toList();
	}

	private static String getComponentId(final CDOTransaction transaction, final CDOID id) {
		try {
			final CDOObject object = transaction.getObject(id);
			if (object != null) {
				if (object instanceof Component) {
					return ((Component) object).getId();
				} else if (object instanceof SnomedRefSetMember) {
					return ((SnomedRefSetMember) object).getUuid();
				} else if (object instanceof SnomedRefSet) { // not sure about this??
					return ((SnomedRefSet) object).getIdentifierId();
				}
			}
		} catch (final ObjectNotFoundException e) {
			// fall through
		}
		return id.toString();
	}

	private static String getComponentIdWithFallback(final CDOTransaction primaryTransaction, final CDOTransaction secondaryTransaction,
			final CDOID id) {
		String componentId = getComponentId(primaryTransaction, id);
		if (componentId.equals(id.toString())) {
			componentId = getComponentId(secondaryTransaction, id);
		}
		return componentId;
	}

	private static String getType(final CDOID id, final CDOTransaction transaction) {
		try {
			final CDOObject object = transaction.getObject(id);
			return object.eClass().getName();
		} catch (final ObjectNotFoundException e) {
			// fall through
		}
		return "Unknown";
	}
}
