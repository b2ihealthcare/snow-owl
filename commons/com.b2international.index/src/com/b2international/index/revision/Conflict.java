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
	
	@Override
	public int hashCode() {
		return Objects.hash(objectId, message);
	}
	
	@Override
	public final boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (getClass() != obj.getClass()) return false;
		Conflict other = (Conflict) obj;
		return Objects.equals(objectId, other.objectId) 
				&& Objects.equals(message, other.message)
				&& doEquals(other);
	}

	/**
	 * Subclasses optionally override this method to provide additional equals checks when they have additional registered conflict properties.
	 * <p><i>Please note if you override this method make sure you override the {@link #hashCode()} method as well to properly compute the hash value of the subclass.</i></p> 
	 *  
	 * @param obj
	 * @return
	 */
	protected boolean doEquals(Conflict obj) {
		return true;
	}

}
