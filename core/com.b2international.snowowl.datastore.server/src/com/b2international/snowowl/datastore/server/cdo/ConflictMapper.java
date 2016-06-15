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

import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndDetachedInTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInSourceAndTargetConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.ChangedInTargetAndDetachedInSourceConflict;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;

/**
 * @since 4.7
 */
public class ConflictMapper {

	public static final String CHANGED_IN_SOURCE_AND_TARGET_MESSAGE = "%s was changed both in source and target branch.";
	public static final String CHANGED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE = "%s was changed in source branch but detached in target branch.";
	public static final String CHANGED_IN_TARGET_DETACHED_IN_SOURCE_MESSAGE = "%s was changed in target branch but detached in source branch.";
	public static final String ADDED_IN_SOURCE_AND_TARGET_MESSAGE = "%s with ID '%s' was introduced both on source and target branch.";
	public static final String ADDED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE = "%s on source branch referencing %s which was detached on target branch.";

	public static MergeConflict convert(final Conflict conflict) {
		if (conflict instanceof ChangedInSourceAndTargetConflict) {
			return from((ChangedInSourceAndTargetConflict) conflict);
		} else if (conflict instanceof ChangedInSourceAndDetachedInTargetConflict) {
			return from((ChangedInSourceAndDetachedInTargetConflict) conflict);
		} else if (conflict instanceof ChangedInTargetAndDetachedInSourceConflict) {
			return from((ChangedInTargetAndDetachedInSourceConflict) conflict);
		} else if (conflict instanceof AddedInSourceAndTargetConflict) {
			return from((AddedInSourceAndTargetConflict) conflict);
		} else if (conflict instanceof AddedInSourceAndDetachedInTargetConflict) {
			return from((AddedInSourceAndDetachedInTargetConflict) conflict);
		}
		throw new IllegalArgumentException("Unknown conflict type: " + conflict);
	}
	
	public static MergeConflict from(final ChangedInSourceAndTargetConflict conflict) {
		final String id = conflict.getID().toString();
		return new MergeConflict(id, id, String.format(CHANGED_IN_SOURCE_AND_TARGET_MESSAGE, id));
	}
	
	public static MergeConflict from(final ChangedInSourceAndDetachedInTargetConflict conflict) {
		final String id = conflict.getID().toString();
		return new MergeConflict(id, null, String.format(CHANGED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE, id));
	}
	
	public static MergeConflict from(final ChangedInTargetAndDetachedInSourceConflict conflict) {
		final String id = conflict.getID().toString();
		return new MergeConflict(null, id, String.format(CHANGED_IN_TARGET_DETACHED_IN_SOURCE_MESSAGE, id));
	}
	
	public static MergeConflict from(final AddedInSourceAndTargetConflict conflict) {
		final String sourceId = conflict.getSourceId().toString();
		final String targetId = conflict.getTargetId().toString();
		return new MergeConflict(sourceId, targetId, String.format(ADDED_IN_SOURCE_AND_TARGET_MESSAGE, conflict.getType(), sourceId));
	}
	
	public static MergeConflict from(final AddedInSourceAndDetachedInTargetConflict conflict) {
		final String sourceId = conflict.getSourceId().toString();
		final String targetId = conflict.getTargetId().toString();
		return new MergeConflict(sourceId, targetId, String.format(ADDED_IN_SOURCE_DETACHED_IN_TARGET_MESSAGE, sourceId, targetId));
	}
}
