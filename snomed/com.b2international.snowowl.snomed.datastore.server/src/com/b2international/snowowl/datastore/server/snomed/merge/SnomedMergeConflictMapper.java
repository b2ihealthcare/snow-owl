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
package com.b2international.snowowl.datastore.server.snomed.merge;

import static java.util.Collections.singletonList;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndDetachedInTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInTargetAndDetachedInSourceConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.merge.MergeConflictImpl;
import com.b2international.snowowl.datastore.server.cdo.AddedInSourceAndDetachedInTargetConflict;
import com.b2international.snowowl.datastore.server.cdo.AddedInSourceAndTargetConflict;
import com.b2international.snowowl.datastore.server.cdo.AddedInTargetAndDetachedInSourceConflict;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;

/**
 * @since 4.7
 */
public class SnomedMergeConflictMapper {

	public static MergeConflict convert(final Conflict conflict, final CDOTransaction sourceTransaction, final CDOTransaction targetTransaction) {
		if (conflict instanceof ChangedInSourceAndTargetConflict) {
			return from((ChangedInSourceAndTargetConflict) conflict, targetTransaction);
		} else if (conflict instanceof ChangedInSourceAndDetachedInTargetConflict) {
			return from((ChangedInSourceAndDetachedInTargetConflict) conflict, sourceTransaction);
		} else if (conflict instanceof ChangedInTargetAndDetachedInSourceConflict) {
			return from((ChangedInTargetAndDetachedInSourceConflict) conflict, targetTransaction);
		} else if (conflict instanceof AddedInSourceAndTargetConflict) {
			return from((AddedInSourceAndTargetConflict) conflict, targetTransaction);
		} else if (conflict instanceof AddedInSourceAndDetachedInTargetConflict) {
			return from((AddedInSourceAndDetachedInTargetConflict) conflict, sourceTransaction);
		} else if (conflict instanceof AddedInTargetAndDetachedInSourceConflict) {
			return from((AddedInTargetAndDetachedInSourceConflict) conflict, targetTransaction);
		}
		throw new IllegalArgumentException("Unknown conflict type: " + conflict);
	}

	public static MergeConflict from(final ChangedInSourceAndTargetConflict conflict, final CDOTransaction targetTransaction) {
		return MergeConflictImpl.builder()
				.withArtefactId(getComponentId(targetTransaction, conflict.getTargetDelta().getID()))
				.withArtefactType(getType(targetTransaction, conflict.getTargetDelta().getID()))
				.withConflictingAttributes(getConflictingAttributes(conflict.getTargetDelta()))
				.withType(ConflictType.CONFLICTING_CHANGE)
				.build();
	}

	public static MergeConflict from(final ChangedInSourceAndDetachedInTargetConflict conflict, final CDOTransaction sourceTransaction) {
		return MergeConflictImpl.builder()
				.withArtefactId(getComponentId(sourceTransaction, conflict.getSourceDelta().getID()))
				.withArtefactType(getType(sourceTransaction, conflict.getSourceDelta().getID()))
				.withConflictingAttributes(getConflictingAttributes(conflict.getSourceDelta()))
				.withType(ConflictType.DELETED_WHILE_CHANGED)
				.build();
	}

	public static MergeConflict from(final ChangedInTargetAndDetachedInSourceConflict conflict, final CDOTransaction targetTransaction) {
		return MergeConflictImpl.builder()
				.withArtefactId(getComponentId(targetTransaction, conflict.getTargetDelta().getID()))
				.withArtefactType(getType(targetTransaction, conflict.getTargetDelta().getID()))
				.withConflictingAttributes(getConflictingAttributes(conflict.getTargetDelta()))
				.withType(ConflictType.CHANGED_WHILE_DELETED)
				.build();
	}

	public static MergeConflict from(final AddedInSourceAndTargetConflict conflict, final CDOTransaction targetTransaction) {
		return MergeConflictImpl.builder()
				.withArtefactId(getComponentId(targetTransaction, conflict.getTargetId()))
				.withArtefactType(getType(targetTransaction, conflict.getTargetId()))
				.withConflictingAttributes(Collections.singletonList("id")) // FIXME
				.withType(ConflictType.CONFLICTING_CHANGE)
				.build();
	}

	public static MergeConflict from(final AddedInSourceAndDetachedInTargetConflict conflict, final CDOTransaction sourceTransaction) {
		return MergeConflictImpl.builder()
				.withArtefactId(getComponentId(sourceTransaction, conflict.getTargetId()))
				.withArtefactType(getType(sourceTransaction, conflict.getTargetId()))
				.withType(ConflictType.CAUSES_MISSING_REFERENCE)
				.build();
	}
	
	public static MergeConflict from(final AddedInTargetAndDetachedInSourceConflict conflict, final CDOTransaction targetTransaction) {
		return MergeConflictImpl.builder()
				.withArtefactId(getComponentId(targetTransaction, conflict.getTargetId()))
				.withConflictingAttributes(Strings.isNullOrEmpty(conflict.getFeatureName()) ? Collections.<String>emptyList() : singletonList(conflict.getFeatureName()))
				.withArtefactType(getType(targetTransaction, conflict.getTargetId()))
				.withType(ConflictType.HAS_MISSING_REFERENCE)
				.build();
	}

	private static String getComponentId(final CDOTransaction transaction, final CDOID id) {
		try {
			final CDOObject object = transaction.getObject(id);
			if (object != null) {
				if (object instanceof Component) {
					return ((Component) object).getId();
				} else if (object instanceof SnomedRefSetMember) {
					return ((SnomedRefSetMember) object).getUuid();
				}
			}
		} catch (final ObjectNotFoundException e) {
			// fall through
		}
		return id.toString();
	}

	private static String getType(final CDOTransaction transaction, final CDOID id) {
		try {
			final CDOObject object = transaction.getObject(id);
			return object.eClass().getName();
		} catch (final ObjectNotFoundException e) {
			// fall through
		}
		return null;
	}
	
	private static List<String> getConflictingAttributes(final CDORevisionDelta cdoRevisionDelta) {
		return FluentIterable.from(cdoRevisionDelta.getFeatureDeltas()).transform(new Function<CDOFeatureDelta, String>() {
			@Override 
			public String apply(CDOFeatureDelta featureDelta) {
				
				String key = featureDelta.getFeature().getName();
				Object value = null;
				
				if (featureDelta instanceof CDOSetFeatureDelta) {
					CDOSetFeatureDelta setFeatureDelta = (CDOSetFeatureDelta) featureDelta;
					if (!(setFeatureDelta.getValue() instanceof CDOID)) {
						if (setFeatureDelta.getValue() instanceof Date) {
							value = Dates.formatByHostTimeZone(setFeatureDelta.getValue(), DateFormats.SHORT);
						} else {
							value = setFeatureDelta.getValue();
						}
					}
				}
				
				if (value != null) {
					return String.format(MergeConflictImpl.ATTRIBUTE_KEY_VALUE_TEMPLATE, key, value);
				}
				
				return key;
			}
		}).toSortedList(Ordering.natural());
	}

	private SnomedMergeConflictMapper() { /* prevent instantiation */ }
	
}
