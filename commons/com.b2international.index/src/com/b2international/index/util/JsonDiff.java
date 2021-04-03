/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.util;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonPatch;
import com.google.common.base.Preconditions;

/**
 * @since 7.16.2
 */
public class JsonDiff implements Iterable<JsonDiff.JsonChange> {

	/**
	 * Important DIFF flags required to produce the JSON patch needed for proper compare and branch merge operation behavior, for proper index schema migration, and so on.
	 * Should be used by default when using jsonpatch.
	 */
	public static final EnumSet<DiffFlags> DIFF_FLAGS = EnumSet.of(DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE, DiffFlags.OMIT_COPY_OPERATION, DiffFlags.OMIT_MOVE_OPERATION);

	private final JsonNode diff;
	
	private List<JsonChange> changes;
	
	public JsonDiff(JsonNode diff) {
		Preconditions.checkNotNull("diff may not be null");
		this.diff = diff;
	}
	
	public boolean hasChanges() {
		return diff.size() > 0;
	}
	
	@Override
	public Iterator<JsonChange> iterator() {
		return getChanges().iterator();
	}
	
	public List<JsonChange> getChanges() {
		if (changes == null) {
			changes = new ArrayList<>(diff.size());
			final Iterator<JsonNode> it = diff.iterator();
			while (it.hasNext()) {
				changes.add(new JsonChange((ObjectNode) it.next()));
			}
		}
		return changes;
	}
	
	public JsonDiff withoutNullAdditions(ObjectMapper mapper) {
		final ArrayNode newDiff = mapper.createArrayNode();
		for (JsonChange change : this) {
			if (change.isAdd() && change.getValue().isNull()) {
				continue;
			}
			newDiff.add(change.getRawChange());
		}
		return new JsonDiff(newDiff);
	}
	
	public JsonNode apply(JsonNode source) {
		return JsonPatch.apply(diff, source);
	}
	
	public void applyInPlace(JsonNode source) {
		JsonPatch.applyInPlace(diff, source);
	}
	
	@Override
	public int hashCode() {
		return diff.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		JsonDiff other = (JsonDiff) obj;
		return Objects.equals(diff, other.diff);
	}
	
	@Override
	public String toString() {
		return diff.toString();
	}
	
	/**
	 * @since 7.16.2
	 */
	public enum JsonDiffOperation {
		ADD,
		REMOVE,
		REPLACE,
		// unsupported
		MOVE,
		COPY;
		
		public static JsonDiffOperation valueOfIgnoreCase(String value) {
			for (JsonDiffOperation literal : values()) {
				if (literal.name().toLowerCase().equals(value)) {
					return literal;
				}
			}
			throw new UnsupportedOperationException(String.format("The JSON Diff operation literal '%s' is not supported.", value));
		}
	}
	
	/**
	 * @since 7.16.2
	 */
	public static final class JsonChange {
		
		private final ObjectNode change;
		private final JsonDiffOperation op;
		
		public JsonChange(ObjectNode change) {
			this.change = Preconditions.checkNotNull(change, "json diff change object may not be null.");
			this.op = JsonDiffOperation.valueOfIgnoreCase(change.get("op").asText());
		}
		
		public ObjectNode getRawChange() {
			return change;
		}
		
		public JsonDiffOperation getOp() {
			return op;
		}
		
		public String getPath() {
			return change.get("path").asText();
		}
		
		/**
		 * Returns the top/root level property that has been changed.
		 * Ignores array indexes and nested property paths.
		 * 
		 * @param change - the change to extract the root level prop name from 
		 * @return the root property name that has been changed
		 */
		public String getRootFieldPath() {
			String property = getFieldPath();
			final int nextSegmentIdx = property.indexOf("/");
			if (nextSegmentIdx >= 0) {
				property = property.substring(0, nextSegmentIdx);
			}
			return property;
		}
		
		/**
		 * Returns the actual field path that has been changed, be it nested or an array index.
		 * 
		 * @param change - the change to extract the prop name from 
		 * @return the absolute property name that has been changed
		 */
		public String getFieldPath() {
			return getPath().substring(1);
		}
		
		public JsonNode getFromValue() {
			return change.get("fromValue");
		}
		
		public JsonNode getValue() {
			return change.get("value");
		}
		
		public String serializeFromValue() {
			return serialize(change, "fromValue");
		}
		
		public String serializeValue() {
			return serialize(change, "value");
		}

		public boolean isAdd() {
			return JsonDiffOperation.ADD == op;
		}
		
		public boolean isRemove() {
			return JsonDiffOperation.REMOVE == op;
		}
		
		public boolean isReplace() {
			return JsonDiffOperation.REPLACE == op;
		}
		
		public boolean isMove() {
			return JsonDiffOperation.MOVE == op;
		}
		
		public boolean isCopy() {
			return JsonDiffOperation.COPY == op;
		}
		
		@Override
		public int hashCode() {
			return change.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			JsonChange other = (JsonChange) obj;
			return Objects.equals(change, other.change);
		}
		
		@Override
		public String toString() {
			return change.toString();
		}

	}
	
	/**
	 * Computes a diff between two {@link JsonNode} objects.
	 * 
	 * @param source
	 * @param target
	 * @return a list of {@link JsonChange} representing the diff between source and target, or an empty {@link List} if there is no change between the two.
	 * @throws NullPointerException if either source or target is <code>null</code>
	 */
	public static JsonDiff diff(JsonNode source, JsonNode target) {
		Preconditions.checkNotNull(source, "source may not be null");
		Preconditions.checkNotNull(source, "target may not be null");
		final JsonNode changes = com.flipkart.zjsonpatch.JsonDiff.asJson(source, target, DIFF_FLAGS);
		return new JsonDiff(changes);
	}
	
	public static String serialize(ObjectNode change, String property) {
		if (change.has(property)) {
			final JsonNode node = change.get(property);
			if (node.isNull()) {
				return null;
			} else if (node.isArray() || node.isObject()) {
				return node.toString();
			} else {
				return node.asText();
			}
		} else {
			return "";
		}
	}

}
