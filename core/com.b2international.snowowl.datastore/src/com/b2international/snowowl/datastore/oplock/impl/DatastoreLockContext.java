/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.oplock.impl;

import java.io.Serializable;
import java.text.MessageFormat;

import com.google.common.base.Preconditions;

/**
 * Represents a lock context carrying a user identifier and a message describing the operation for which the lock is to
 * be acquired. Context equality is only based on the user ID.
 * 
 */
public class DatastoreLockContext implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String userId;

	private final String description;
	
	private final String parentDescription;

	/**
	 * Creates a new lock context with the specified arguments.
	 * 
	 * @param userId the owning user's identifier (may not be {@code null})
	 * @param description the description of the operation which requires a lock (may not be {@code null})
	 */
	public DatastoreLockContext(final String userId, final String description) {
		this(userId, description, DatastoreLockContextDescriptions.ROOT);
	}

	/**
	 * Creates a new lock context with the specified arguments.
	 * 
	 * @param userId the owning user's identifier (may not be {@code null})
	 * @param description the description of the operation which requires a lock (may not be {@code null})
	 * @param parentDescription the description of the parent operation in which this operation take place, in a nested fashion (may not be {@code null})
	 */
	public DatastoreLockContext(final String userId, final String description, final String parentDescription) {
		this.userId = Preconditions.checkNotNull(userId, "User identifier may not be null.");
		this.description = Preconditions.checkNotNull(description, "Operation description may not be null.");
		this.parentDescription = Preconditions.checkNotNull(parentDescription, "Parent lock context ID may not be null.");
	}

	public String getUserId() {
		return userId;
	}

	public String getDescription() {
		return description;
	}
	
	public String getParentDescription() {
		return parentDescription;
	}

	public boolean isCompatible(final DatastoreLockContext other) {
		return userMatches(other) && parentDescription.equals(other.description);
	}

	public boolean userMatches(final DatastoreLockContext other) {
		return userId.equals(other.userId);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + userId.hashCode();
		result = prime * result + description.hashCode();
		result = prime * result + parentDescription.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof DatastoreLockContext)) {
			return false;
		}
		
		final DatastoreLockContext other = (DatastoreLockContext) obj;
		
		if (!userMatches(other)) {
			return false;
		}
		
		if (!description.equals(other.description)) {
			return false;
		}
		
		if (!parentDescription.equals(other.parentDescription)) {
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		return MessageFormat.format("lock owner: ''{0}'' ({1})", userId, description);
	}
}