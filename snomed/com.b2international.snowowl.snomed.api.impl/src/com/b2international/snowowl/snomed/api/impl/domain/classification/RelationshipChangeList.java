/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.impl.domain.classification;

import java.util.List;

import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChange;
import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChangeList;

/**
 */
public class RelationshipChangeList implements IRelationshipChangeList {

	private final List<IRelationshipChange> changes;
	private final int offset;
	private final int limit;
	private final int total;

	public RelationshipChangeList(List<IRelationshipChange> changes, int offset, int limit, int total) {
		this.changes = changes;
		this.offset = offset;
		this.limit = limit;
		this.total = total;
	}
	
	public List<IRelationshipChange> getChanges() {
		return changes;
	}

	public int getTotal() {
		return total;
	}

	@Override
	public int getOffSet() {
		return offset;
	}
	
	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RelationshipChangeList [changes=");
		builder.append(changes);
		builder.append(", total=");
		builder.append(total);
		builder.append("]");
		return builder.toString();
	}
}