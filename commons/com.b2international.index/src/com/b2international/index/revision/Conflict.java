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
public abstract class Conflict {

	private final ObjectId objectId;
	private final String message;

	public Conflict(ObjectId objectId, String message) {
		this.objectId = objectId;
		this.message = message;
	}
	
	public ObjectId getObjectId() {
		return objectId;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return getMessage();
	}
	
}
