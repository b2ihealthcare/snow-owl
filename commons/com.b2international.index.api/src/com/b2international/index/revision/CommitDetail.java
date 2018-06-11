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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.b2international.index.Doc;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
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

	public enum Operation {
		
		ADD("+"),
	    REMOVE("-"),
	    CHANGE("~");
	
		private final static Map<String, Operation> OPS = initOps();

	    private static Map<String, Operation> initOps() {
	        Map<String, Operation> map = new HashMap<String, Operation>();
	        map.put(ADD.opType, ADD);
	        map.put(REMOVE.opType, REMOVE);
	        map.put(CHANGE.opType, CHANGE);
	        return Collections.unmodifiableMap(map);
	    }

	    private String opType;

	    Operation(String opType) {
	        this.opType = opType;
	    }

	    @JsonCreator
	    static Operation fromOpType(String opType) throws IllegalArgumentException {
	        if (opType == null) throw new IllegalArgumentException("opType cannot be null");
	        Operation op = OPS.get(opType.toLowerCase());
	        if (op == null) throw new IllegalArgumentException("unknown / unsupported operation " + opType);
	        return op;
	    }

	    @JsonValue
	    String opType() {
	        return this.opType;
	    }

		static Operation fromRfcName(String rfcName) {
			if (rfcName == null) throw new IllegalArgumentException("opType cannot be null");
			switch (rfcName) {
			case "add": return Operation.ADD;
			case "remove": return Operation.REMOVE;
			case "replace": return Operation.CHANGE;
			default: throw new IllegalArgumentException("unknown / unsupported operation " + rfcName);
			}
		}
		
	}
	
	public static Builder added() {
		return new Builder().op(Operation.ADD);
	}
	
	public static Builder changed() {
		return new Builder().op(Operation.CHANGE);
	}
	
	public static Builder removed() {
		return new Builder().op(Operation.REMOVE);
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
		private List<Set<String>> children;
		
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
		Builder children(List<Set<String>> children) {
			this.children = children;
			return this;
		}
		
		public Builder propertyChange(String prop, String from, String to, Collection<String> objects) {
			return prop(prop).from(from).to(to).objects(objects);
		}
		
		public Builder putObjects(String object, Iterable<String> children) {
			if (this.children == null) {
				this.children = newArrayList();
				this.objects = newArrayList();
			}
			this.objects.add(object);
			this.children.add(ImmutableSet.copyOf(children));
			return this;
		}
		
		public CommitDetail build() {
			return new CommitDetail(op, prop, from, to, objects, children);
		}
		
	}
	
	private final Operation op;
	private final String prop;
	private final String from;
	private final String to;
	private final List<String> objects;
	private final List<Set<String>> children;

	private CommitDetail(
			Operation op,
			String prop,
			String from,
			String to,
			List<String> objects,
			List<Set<String>> children) {
		this.op = op;
		this.prop = prop;
		this.from = from;
		this.to = to;
		this.objects = objects;
		this.children = children;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(op, prop, from, to, objects, children);
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
				&& Objects.equals(objects, other.objects)
				&& Objects.equals(children, other.children);
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
	
	public List<String> getObjects() {
		return objects;
	}
	
	public List<Set<String>> getChildren() {
		return children;
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
	
	public CommitDetail extract(String objectId) {
		// if prop is not present then this represents a hierarchical change
		final int affectedObjectIdx = objects.indexOf(objectId);
		final Builder result = new Builder().op(op);
		if (isPropertyChange()) {
			// if the object is not present in the objects list, then it might be added/removed from a container, check the index in the children list
			if (affectedObjectIdx == -1) {
				for (int i = 0; i < this.children.size(); i++) {
					Set<String> children = this.children.get(i);
					if (children.contains(objectId)) {
						result.putObjects(this.objects.get(i), Collections.singleton(objectId));
						break;
					}
				}
			} else {
				result.putObjects(objectId, this.children.get(affectedObjectIdx));
			}
		} else if (affectedObjectIdx != -1) {
			result.propertyChange(prop, from, to, Collections.singleton(objectId));
		}
		return result.build();
	}
	
	@JsonIgnore
	public boolean isEmpty() {
		return !isPropertyChange() && objects.isEmpty();
	}
	
	@Override
	public String toString() {
		final ToStringHelper toString = MoreObjects.toStringHelper(getClass())
				.add("op", op)
				.add("objects", objects);
		if (!isPropertyChange()) {
			return toString
					.add("children", children)
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
	
//	/**
//	 * @return a set of identifiers that are marked as NEW in the corresponding commit.
//	 */
//	@JsonIgnore
//	public Set<String> getNewComponents() {
//		return changes.stream()
//				.filter(CommitDetailDelta::isAdd)
//				.filter(delta -> CHILD_PROP.equals(delta.getPath()))
//				.map(CommitDetailDelta::getValue)
//				.collect(Collectors.toSet());
//	}
	
//	/**
//	 * @return a set of identifiers that are marked as CHANGED in the corresponding commit.
//	 */
//	@JsonIgnore
//	public Set<String> getChangedComponents() {
//		return changes.stream()
//				.filter(CommitDetailDelta::isChange)
//				.filter(delta -> CHILD_PROP.equals(delta.getPath()))
//				.map(CommitDetailDelta::getValue)
//				.collect(Collectors.toSet());
//	}
	
//	/**
//	 * @return a set of identifiers that are marked as REMOVED in the corresponding commit.
//	 */
//	@JsonIgnore
//	public Set<String> getRemovedComponents() {
//		return changes.stream()
//				.filter(CommitDetailDelta::isRemove)
//				.filter(delta -> CHILD_PROP.equals(delta.getPath()))
//				.map(CommitDetailDelta::getValue)
//				.collect(Collectors.toSet());
//	}
	
//	/**
//	 * @return the changes if this component
//	 */
//	public List<CommitDetailDelta> getChanges() {
//		return changes;
//	}

//	/**
//	 * @return <code>true</code> if this commit detail object is empty, <code>false</code> otherwise
//	 */
//	@JsonIgnore
//	public boolean isEmpty() {
//		return Operation.CHANGE == op && CompareUtils.isEmpty(changes);
//	}
	
}
