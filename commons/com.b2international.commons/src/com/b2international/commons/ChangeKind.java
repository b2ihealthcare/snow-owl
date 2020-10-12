/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * The type of the change of the particular object.
 * <p>
 * The order of enumeration represents priority; 
 * <br>values that come first will be preferred over values that come later when displaying a change.
 */
public enum ChangeKind {

	/** Deletion. */
	DELETED("Deleted"),

	/** Represent a creation or addition. */
	ADDED("New"),
	
	/** Represents a modification. */
	UPDATED("Changed"),
	
	/** Represents no modification. */
	UNCHANGED("Unchanged");
	
	private final String label;
	
	private ChangeKind(final String label) {
		this.label = checkNotNull(label, "label");
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	public boolean isDeleted() {
		return DELETED == this;
	}
	
	public boolean isAdded() {
		return ADDED == this;
	}
	
	public boolean isUpdated() {
		return UPDATED == this;
	}
	
	public boolean isUnchanged() {
		return UNCHANGED == this;
	}
	
}