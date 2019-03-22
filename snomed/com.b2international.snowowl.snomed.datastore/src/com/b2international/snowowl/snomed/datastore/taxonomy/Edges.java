/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.taxonomy;

import java.util.Arrays;
import java.util.Objects;

/**
 * @since 6.14
 */
final class Edges {
		
	final long sourceId;
	final long[] destinationIds;
	
	Edges(long sourceId, long[] destinationIds) {
		this.sourceId = sourceId;
		this.destinationIds = destinationIds;
		// keep the destinationIds sorted, so Edges will be equal even if the destinationIds long array was not in the same order
		Arrays.sort(this.destinationIds);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(sourceId, destinationIds);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Edges other = (Edges) obj;
		return sourceId == other.sourceId
				&& Arrays.equals(destinationIds, other.destinationIds);
	}
		
}