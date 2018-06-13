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

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.b2international.commons.CompareUtils;
import com.b2international.index.Doc;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * @since 6.6
 */
@Doc(nested = false, index = true)
@JsonInclude(Include.NON_EMPTY)
@JsonDeserialize(builder = CommitDetail.Builder.class)
public final class CommitDetail {

	public static Builder added(String objectType, String componentType) {
		return new Builder()
				.op(Operation.ADD)
				.objectType(objectType)
				.componentType(componentType);
	}
	
	public static Builder changed(String objectType, String componentType) {
		return new Builder()
				.op(Operation.CHANGE)
				.objectType(objectType)
				.componentType(componentType);
	}
	
	public static Builder removed(String objectType, String componentType) {
		return new Builder()
				.op(Operation.REMOVE)
				.objectType(objectType)
				.componentType(componentType);
	}
	
	public static CommitDetail changedProperty(String prop, String from, String to, String objectType, Collection<String> objects) {
		return new Builder()
				.op(Operation.CHANGE)
				.prop(prop)
				.from(from)
				.to(to)
				.objectType(objectType)
				.objects(objects)
				.build();
	}
	
	/**
	 * @since 6.6
	 */
	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder {
		
		private Operation op;
		private String prop;
		private String from;
		private String to;
		private List<String> objects;
		private List<Set<String>> components;
		private String objectType;
		private String componentType;
		
		Builder() {
		}
		
		@JsonProperty
		Builder op(Operation op) {
			this.op = op;
			return this;
		}
		
		@JsonProperty
		Builder prop(String prop) {
			this.prop = prop;
			return this;
		}
		
		@JsonProperty
		Builder from(String from) {
			this.from = from;
			return this;
		}
		
		@JsonProperty
		Builder to(String to) {
			this.to = to;
			return this;
		}
		
		@JsonProperty
		Builder objects(Iterable<String> objects) {
			this.objects = objects == null ? null : ImmutableList.copyOf(objects);
			return this;
		}
		
		@JsonProperty
		Builder components(List<Set<String>> components) {
			this.components = components;
			return this;
		}
		
		@JsonProperty
		Builder objectType(String objectType) {
			this.objectType = objectType;
			return this;
		}
		
		
		@JsonProperty
		Builder componentType(String componentType) {
			this.componentType = componentType;
			return this;
		}
		
		public Builder putObjects(String object, Iterable<String> components) {
			if (this.components == null) {
				this.components = newArrayList();
				this.objects = newArrayList();
			}
			this.objects.add(object);
			this.components.add(ImmutableSet.copyOf(components));
			return this;
		}
		
		public CommitDetail build() {
			return new CommitDetail(op, prop, from, to, objectType, objects, componentType, components);
		}
		
	}
	
	private final Operation op;
	private final String prop;
	private final String from;
	private final String to;
	private final String objectType;
	private final List<String> objects;
	private final String componentType;
	private final List<Set<String>> components;

	private CommitDetail(
			Operation op,
			String prop,
			String from,
			String to,
			String objectType,
			List<String> objects,
			String componentType,
			List<Set<String>> components) {
		this.op = op;
		this.prop = prop;
		this.from = from;
		this.to = to;
		this.objectType = objectType;
		this.objects = objects;
		this.componentType = componentType;
		this.components = components;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(op, prop, from, to, objectType, objects, componentType, components);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CommitDetail other = (CommitDetail) obj;
		return Objects.equals(op, other.op) 
				&& Objects.equals(prop, other.prop) 
				&& Objects.equals(from, other.from)
				&& Objects.equals(to, other.to)
				&& Objects.equals(objectType, other.objectType)
				&& Objects.equals(componentType, other.componentType)
				&& Objects.equals(objects, other.objects)
				&& Objects.equals(components, other.components);
	}
	
	/**
	 * @return the {@link Operation} that happened on the {@link #getRoot() root component}.
	 */
	public Operation getOp() {
		return op;
	}

	public String getProp() {
		return prop;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;
	}
	
	public String getObjectType() {
		return objectType;
	}
	
	public List<String> getObjects() {
		return objects;
	}
	
	public String getComponentType() {
		return componentType;
	}
	
	public List<Set<String>> getComponents() {
		return components;
	}
	
	@JsonIgnore
	public boolean isAdd() {
		return Operation.ADD == op; 
	}
	
	@JsonIgnore
	public boolean isRemove() {
		return Operation.REMOVE == op; 
	}
	
	@JsonIgnore
	public boolean isChange() {
		return Operation.CHANGE == op; 
	}
	
	CommitDetail extract(String objectId) {
		if (isPropertyChange()) {
			return changedProperty(prop, from, to, objectType, Collections.singleton(objectId));
		} else {
			// if prop is not present then this represents a hierarchical change
			final Builder result = new Builder()
					.op(op)
					.objectType(objectType)
					.componentType(componentType);
			final int affectedObjectIdx = objects.indexOf(objectId);
			// if the object is not present in the objects list, then it might be added/removed from a container, check the index in the children list
			if (affectedObjectIdx == -1) {
				for (int i = 0; i < this.components.size(); i++) {
					Set<String> children = this.components.get(i);
					if (children.contains(objectId)) {
						result.putObjects(this.objects.get(i), Collections.singleton(objectId));
						break;
					}
				}
			} else {
				result.putObjects(objectId, this.components.get(affectedObjectIdx));
			}
			return result.build();
		}
	}
	
	@JsonIgnore
	public boolean isEmpty() {
		return !isPropertyChange() && CompareUtils.isEmpty(objects);
	}
	
	@Override
	public String toString() {
		final ToStringHelper toString = MoreObjects.toStringHelper(getClass())
				.add("op", op)
				.add("objectType", objectType)
				.add("objects", objects);
		if (!isPropertyChange()) {
			return toString
					.add("componentType", componentType)
					.add("components", components)
					.toString();
		} else {
			return toString
					.add("prop", prop)
					.add("from", from)
					.add("to", to)
					.toString();
		}
	}

	@JsonIgnore
	public boolean isPropertyChange() {
		return !Strings.isNullOrEmpty(prop);
	}
	
}
