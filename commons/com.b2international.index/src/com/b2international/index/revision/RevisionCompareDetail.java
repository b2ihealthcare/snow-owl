/*
 * Copyright 2018-2020 B2i Healthcare, https://b2ihealthcare.com
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

	static final String PROPERTY_CHANGE_KEY_SEPARATOR = "#";
	
	// change details (hierarchy)
	private final Operation op;
	private final ObjectId object;
	private final ObjectId component;
	// change details (property)	
	private final String property;
	private final String fromValue;
	private final String value;
	
	@JsonIgnore
	private final String key;
	
	public static RevisionCompareDetail propertyChange(Operation op,
			ObjectId object,
			String property,
			String fromValue,
			String value) {
		return new RevisionCompareDetail(op, object, null, property, fromValue, value);
	}
	
	public static RevisionCompareDetail componentChange(Operation op,
			ObjectId object,
			ObjectId component) {
		return new RevisionCompareDetail(op, object, component, null, null, null);
	}
	
	private RevisionCompareDetail(Operation op,
			ObjectId object,
			ObjectId component,
			String property,
			String fromValue,
			String value) {
		this.op = op;
		this.object = object;
		this.component = component;
		this.property = property;
		this.fromValue = fromValue;
		this.value = value;
		if (isComponentChange()) {
			this.key = component.toString();
		} else {
			this.key = String.join(PROPERTY_CHANGE_KEY_SEPARATOR, object.toString(), property);
		}
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
	public boolean isPropertyChange() {
		return !isComponentChange();
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
		return key;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (getClass() != obj.getClass()) return false;
		RevisionCompareDetail other = (RevisionCompareDetail) obj;
		return Objects.equals(op, other.op)
				&& Objects.equals(object, other.object)
				&& Objects.equals(component, other.component)
				&& Objects.equals(property, other.property)
				&& Objects.equals(fromValue, other.fromValue)
				&& Objects.equals(value, other.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(op, object, component, property, fromValue, value);
	}
	
	@Override
	public String toString() {
		if (isComponentChange()) {
			return String.format("%s[obj=%s, component=%s]", op, object, component);
		} else {
			return String.format("%s[obj=%s, property=%s, from=%s, to=%s]", op, object, property, fromValue, value);
		}
	}

	RevisionCompareDetail merge(RevisionCompareDetail other) {
		checkArgument(key().equals(other.key()), "Cannot merge unrelated compare details.");
		if (isComponentChange()) {
			if ((isAdd() && other.isRemove()) || (isRemove() && other.isAdd())) {
				// NEW and REMOVED, clear it, nothing happened
				return null;
			} else if (isAdd() && other.isChange()) {
				// NEW and CHANGED, register as NEW
				return this;
			} else if (isChange() && other.isAdd()) {
				// NEW and CHANGED, register as NEW
				return other;
			} else if (isRemove() && other.isChange()) {
				// CHANGED and REMOVED, register as REMOVED
				return this;
			} else if (isChange() && other.isRemove()) {
				// CHANGED and REMOVED, register as REMOVED
				return other;
			} else if (getOp() == other.getOp()) {
				// two changes after each other in the commit history, keep only a single component change
				return this; 
			} else {
				throw new UnsupportedOperationException("Unknown case for _component change: " + this.getOp() + " vs. " + other.getOp());
			}
		} else {
			// two property changes
			if (Objects.equals(getFromValue(), other.getValue()) && Objects.equals(getValue(), other.getFromValue())) {
				return null;
			} else {
				// otherwise all new values except the from value, we would like to see the original from value -> latest new value change in the result
				return propertyChange(other.op, other.object, other.property, fromValue, other.value);
			}
		}
	}

}
