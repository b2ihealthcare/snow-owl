/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.taxonomy;

import java.util.Objects;
import java.util.Set;

import com.google.common.base.MoreObjects;

/**
 * @since 8.0.1
 */
public class SimpleEdge {

	private final String sourceId;
	private final Set<String> destinationIds;
	
	public SimpleEdge(final String sourceId, final String destinationId) {
		this(sourceId, Set.of(destinationId));
	}
	
	public SimpleEdge(final String sourceId, final Set<String> destinationIds) {
		this.sourceId = sourceId;
		this.destinationIds = Set.copyOf(destinationIds);
	}

	public String getSourceId() {
		return sourceId;
	}
	
	public Set<String> getDestinationIds() {
		return destinationIds;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sourceId, destinationIds);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		
		final SimpleEdge other = (SimpleEdge) obj;
		return Objects.equals(sourceId, other.sourceId)
			&& Objects.equals(destinationIds, other.destinationIds);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("sourceId", sourceId)
			.add("destinationIds", destinationIds)
			.toString();
	}
}
