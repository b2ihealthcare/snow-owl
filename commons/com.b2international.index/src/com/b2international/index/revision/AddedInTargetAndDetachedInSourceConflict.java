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
public final class AddedInTargetAndDetachedInSourceConflict extends Conflict {

	private final ObjectId addedOnTarget;
	
	private String featureName;

	public AddedInTargetAndDetachedInSourceConflict(final ObjectId detachedOnSource, final ObjectId addedOnTarget) {
		this(detachedOnSource, addedOnTarget, "container");
	}
	
	public AddedInTargetAndDetachedInSourceConflict(final ObjectId detachedOnSource, final ObjectId addedOnTarget, final String featureName) {
		super(detachedOnSource, String.format("'%s' remove on source, but '%s' added on target.", detachedOnSource, addedOnTarget));
		this.addedOnTarget = addedOnTarget;
		this.featureName = featureName;
	}
	
	public ObjectId getAddedOnTarget() {
		return addedOnTarget;
	}
	
	public ObjectId getDetachedOnSource() {
		return getObjectId();
	}
	
	public String getFeatureName() {
		return featureName;
	}

	public AddedInTargetAndDetachedInSourceConflict withFeatureName(String featureName) {
		this.featureName = featureName;
		return this;
	}

}
