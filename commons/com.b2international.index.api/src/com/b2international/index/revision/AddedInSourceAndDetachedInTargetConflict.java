/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * @since 7.0
 */
public final class AddedInSourceAndDetachedInTargetConflict extends Conflict {

	private final ObjectId detachedOnTarget;
	
	private String featureName;

	public AddedInSourceAndDetachedInTargetConflict(ObjectId addedInSource, ObjectId detachedOnTarget) {
		this(addedInSource, detachedOnTarget, "container");
	}
	
	public AddedInSourceAndDetachedInTargetConflict(ObjectId addedInSource, ObjectId detachedOnTarget, String featureName) {
		super(addedInSource, String.format("'%s' added on source, but '%s' removed on target.", addedInSource, detachedOnTarget));
		this.detachedOnTarget = detachedOnTarget;
		this.featureName = featureName;
	}
	
	public ObjectId getAddedOnSource() {
		return getObjectId();
	}
	
	public ObjectId getDetachedOnTarget() {
		return detachedOnTarget;
	}
	
	public String getFeatureName() {
		return featureName;
	}
	
	public AddedInSourceAndDetachedInTargetConflict withFeatureName(String featureName) {
		this.featureName = featureName;
		return this;
	}

}
