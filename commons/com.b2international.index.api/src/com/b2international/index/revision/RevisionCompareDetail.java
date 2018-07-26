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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;

/**
 * @since 7.0
 */
public final class RevisionCompareDetail {

	// commit details
	private final String author;
	private final long timestamp;
	private final String comment;
	// change details (hierarchy)
	private final Operation op;
	private final ObjectId object;
	private final ObjectId component;
	// change details (property)	
	private final String property;
	private final String fromValue;
	private final String value;
	
	public static RevisionCompareDetail propertyChange(String author, 
			long timestamp, 
			String comment, 
			Operation op,
			ObjectId object,
			String property,
			String fromValue,
			String value) {
		return new RevisionCompareDetail(author, timestamp, comment, op, object, null, property, fromValue, value);
	}
	
	public static RevisionCompareDetail componentChange(String author, 
			long timestamp, 
			String comment, 
			Operation op,
			ObjectId object,
			ObjectId component) {
		return new RevisionCompareDetail(author, timestamp, comment, op, object, component, null, null, null);
	}
	
	private RevisionCompareDetail(
			String author, 
			long timestamp, 
			String comment, 
			Operation op,
			ObjectId object,
			ObjectId component,
			String property,
			String fromValue,
			String value) {
		this.author = author;
		this.timestamp = timestamp;
		this.comment = comment;
		this.op = op;
		this.object = object;
		this.component = component;
		this.property = property;
		this.fromValue = fromValue;
		this.value = value;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getComment() {
		return comment;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public Operation getOp() {
		return op;
	}
	
	public ObjectId getObject() {
		return object;
	}
	
	public ObjectId getComponent() {
		return component;
	}
	
	public String getProperty() {
		return property;
	}
	
	public String getFromValue() {
		return fromValue;
	}

	public String getValue() {
		return value;
	}
	
	@JsonIgnore
	public boolean isComponentChange() {
		return Strings.isNullOrEmpty(property);
	}
	
	@JsonIgnore
	public boolean isAdd() {
		return Operation.ADD == op;
	}
	
	@JsonIgnore
	public boolean isChange() {
		return Operation.CHANGE == op;
	}
	
	@JsonIgnore
	public boolean isRemove() {
		return Operation.REMOVE == op;
	}

	@JsonIgnore
	String key() {
		if (isComponentChange()) {
			return String.format("%s_%s", object, component);
		} else {
			return String.format("%s_%s_%s", op, object, property);
		}
	}

	RevisionCompareDetail merge(RevisionCompareDetail other) {
		checkArgument(key().equals(other.key()), "Cannot merge unrelated compare details.");
		if (isComponentChange()) {
			// other is a revert of this detail return null 
			if ((isAdd() && other.isRemove()) || 
					(isRemove() && other.isAdd())) {
				return null;
			} else if (isAdd() && other.isChange()) {
				return this;
			} else if (isChange() && other.isAdd()) {
				return other;
			} else {
				throw new UnsupportedOperationException("Unknown case for _component change: " + this + " vs. " + other);
			}
		} else {
			// if this is a property change, but it is a revert then return null
			if (Objects.equals(getFromValue(), other.getValue()) && Objects.equals(getValue(), other.getFromValue())) {
				return null;
			} else {
				// otherwise all new values except the from value, we would like to see the original from value -> latest new value change in the result
				return propertyChange(other.author, other.timestamp, other.comment, other.op, other.object, other.property, fromValue, other.value);
			}
		}
	}

}
