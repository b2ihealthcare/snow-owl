/*
 * Copyright 2018-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.index.revision;

import java.util.Objects;

import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;

/**
 * @since 7.0
 */
public final class ChangedInSourceAndTargetConflict extends Conflict {

	private final RevisionPropertyDiff sourceChange;
	private final RevisionPropertyDiff targetChange;

	public ChangedInSourceAndTargetConflict(ObjectId objectId, RevisionPropertyDiff sourceChange, RevisionPropertyDiff targetChange) {
		super(objectId, String.format("ChangedInSourceAndTarget[id=%s, property=%s, sourceChange=%s, targetChange=%s]", objectId, sourceChange.getProperty(), sourceChange.toValueChangeString(), targetChange.toValueChangeString()));
		this.sourceChange = sourceChange;
		this.targetChange = targetChange;
	}
	
	public RevisionPropertyDiff getSourceChange() {
		return sourceChange;
	}
	
	public RevisionPropertyDiff getTargetChange() {
		return targetChange;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getObjectId(), getMessage(), getSourceChange(), getTargetChange());
	}
	
	@Override
	protected boolean doEquals(Conflict obj) {
		ChangedInSourceAndTargetConflict other = (ChangedInSourceAndTargetConflict) obj;
		return Objects.equals(sourceChange, other.sourceChange)
				&& Objects.equals(targetChange, other.targetChange);
	}
	
}
