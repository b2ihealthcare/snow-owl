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
package com.b2international.snowowl.datastore.server.cdo;

import static com.b2international.snowowl.datastore.server.cdo.GenericCDOMergeConflictMessages.ADDED_IN_SOURCE_AND_TARGET_SOURCE_MESSAGE;
import static com.b2international.snowowl.datastore.server.cdo.GenericCDOMergeConflictMessages.ADDED_IN_SOURCE_AND_TARGET_TARGET_MESSAGE;
import static com.b2international.snowowl.datastore.server.cdo.GenericCDOMergeConflictMessages.ADDED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE;
import static com.b2international.snowowl.datastore.server.cdo.GenericCDOMergeConflictMessages.ADDED_IN_TARGET_DETACHED_IN_SOURCE_MESSAGE;
import static com.b2international.snowowl.datastore.server.cdo.GenericCDOMergeConflictMessages.CHANGED_IN_SOURCE_AND_TARGET_MESSAGE;
import static com.b2international.snowowl.datastore.server.cdo.GenericCDOMergeConflictMessages.CHANGED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE;
import static com.b2international.snowowl.datastore.server.cdo.GenericCDOMergeConflictMessages.CHANGED_IN_TARGET_DETACHED_IN_SOURCE_MESSAGE;

import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndDetachedInTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInTargetAndDetachedInSourceConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;

/**
 * @since 4.7
 */
public class ConflictMapper {

	public static GenericCDOMergeConflict convert(final Conflict conflict, final CDOTransaction sourceTransaction, final CDOTransaction targetTransaction) {
		if (conflict instanceof ChangedInSourceAndTargetConflict) {
			return from((ChangedInSourceAndTargetConflict) conflict);
		} else if (conflict instanceof ChangedInSourceAndDetachedInTargetConflict) {
			return from((ChangedInSourceAndDetachedInTargetConflict) conflict);
		} else if (conflict instanceof ChangedInTargetAndDetachedInSourceConflict) {
			return from((ChangedInTargetAndDetachedInSourceConflict) conflict);
		} else if (conflict instanceof AddedInSourceAndTargetConflict) {
			return from((AddedInSourceAndTargetConflict) conflict, sourceTransaction);
		} else if (conflict instanceof AddedInSourceAndDetachedInTargetConflict) {
			return from((AddedInSourceAndDetachedInTargetConflict) conflict, sourceTransaction);
		} else if (conflict instanceof AddedInTargetAndDetachedInSourceConflict) {
			return from((AddedInTargetAndDetachedInSourceConflict) conflict, targetTransaction);
		}
		throw new IllegalArgumentException("Unknown conflict type: " + conflict);
	}
	
	public static GenericCDOMergeConflict from(final ChangedInSourceAndTargetConflict conflict) {
		final String id = conflict.getID().toString();
		return new GenericCDOMergeConflict(id, id, String.format(CHANGED_IN_SOURCE_AND_TARGET_MESSAGE, id));
	}
	
	public static GenericCDOMergeConflict from(final ChangedInSourceAndDetachedInTargetConflict conflict) {
		final String id = conflict.getID().toString();
		return new GenericCDOMergeConflict(id, null, String.format(CHANGED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE, id));
	}
	
	public static GenericCDOMergeConflict from(final ChangedInTargetAndDetachedInSourceConflict conflict) {
		final String id = conflict.getID().toString();
		return new GenericCDOMergeConflict(null, id, String.format(CHANGED_IN_TARGET_DETACHED_IN_SOURCE_MESSAGE, id));
	}
	
	public static GenericCDOMergeConflict from(final AddedInSourceAndTargetConflict conflict, final CDOTransaction sourceTransaction) {
		final String sourceId = conflict.getSourceId().toString();
		final String targetId = conflict.getTargetId().toString();
		final String type = sourceTransaction.getObject(conflict.getSourceId()).getClass().getSimpleName(); // FIXME
		return new GenericCDOMergeConflict(sourceId, targetId, String.format(conflict.isAddedInSource() ? ADDED_IN_SOURCE_AND_TARGET_SOURCE_MESSAGE
				: ADDED_IN_SOURCE_AND_TARGET_TARGET_MESSAGE, type, sourceId));
	}
	
	public static GenericCDOMergeConflict from(final AddedInSourceAndDetachedInTargetConflict conflict, final CDOTransaction sourceTransaction) {
		final String sourceId = conflict.getSourceId().toString();
		final String targetId = conflict.getTargetId().toString();
		final String sourceType = sourceTransaction.getObject(conflict.getSourceId()).getClass().getSimpleName();
		final String targetType = sourceTransaction.getObject(conflict.getTargetId()).getClass().getSimpleName();
		return new GenericCDOMergeConflict(sourceId, targetId, String.format(ADDED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE, sourceType, sourceId, targetType, targetId));
	}
	
	public static GenericCDOMergeConflict from(final AddedInTargetAndDetachedInSourceConflict conflict, final CDOTransaction targetTransaction) {
		final String sourceId = conflict.getSourceId().toString();
		final String targetId = conflict.getTargetId().toString();
		final String sourceType = targetTransaction.getObject(conflict.getSourceId()).getClass().getSimpleName();
		final String targetType = targetTransaction.getObject(conflict.getTargetId()).getClass().getSimpleName();
		return new GenericCDOMergeConflict(sourceId, targetId, String.format(ADDED_IN_TARGET_DETACHED_IN_SOURCE_MESSAGE, targetType, targetId, sourceType, sourceId));
	}
	
	public static Conflict invert(final Conflict conflict) {
		if (conflict instanceof ChangedInSourceAndDetachedInTargetConflict) {
			final ChangedInSourceAndDetachedInTargetConflict oldConflict = (ChangedInSourceAndDetachedInTargetConflict) conflict;
			return new ChangedInTargetAndDetachedInSourceConflict(oldConflict.getSourceDelta());
		} else if (conflict instanceof ChangedInTargetAndDetachedInSourceConflict) {
			final ChangedInTargetAndDetachedInSourceConflict oldConflict = (ChangedInTargetAndDetachedInSourceConflict) conflict;
			return new ChangedInSourceAndDetachedInTargetConflict(oldConflict.getTargetDelta());
		} else if (conflict instanceof ChangedInSourceAndTargetConflict) {
			final ChangedInSourceAndTargetConflict oldConflict = (ChangedInSourceAndTargetConflict) conflict;
			return new ChangedInSourceAndTargetConflict(oldConflict.getTargetDelta(), oldConflict.getSourceDelta());
		} else if (conflict instanceof AddedInSourceAndDetachedInTargetConflict) {
			final AddedInSourceAndDetachedInTargetConflict oldConflict = (AddedInSourceAndDetachedInTargetConflict) conflict;
			return new AddedInTargetAndDetachedInSourceConflict(oldConflict.getTargetId(), oldConflict.getSourceId());
		} else if (conflict instanceof AddedInTargetAndDetachedInSourceConflict) {
			final AddedInTargetAndDetachedInSourceConflict oldConflict = (AddedInTargetAndDetachedInSourceConflict) conflict;
			return new AddedInSourceAndDetachedInTargetConflict(oldConflict.getTargetId(), oldConflict.getSourceId());
		}
		return conflict;
	}
	
	private ConflictMapper() { /* prevent instantiation */ }
}
