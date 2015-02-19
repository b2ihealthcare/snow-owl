/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.filteredrefset;

/**
 * 
 */
public class TopLevelRefSetMemberNode implements IRefSetMemberNode {

	private static final long serialVersionUID = 1L;

	private final long conceptId;

	private final String id;
	
	private final String label;
	
	public TopLevelRefSetMemberNode(final long conceptId, final String label) {
		this.conceptId = conceptId;
		this.id = Long.toString(conceptId);
		this.label = label;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isRegularNode() {
		return false;
	}

	@Override
	public IRegularRefSetMemberNode asRegularNode() {
		throw new ClassCastException("Can't cast top level reference set node to regular node.");
	}

	@Override
	public long getConceptId() {
		return conceptId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (conceptId ^ (conceptId >>> 32));
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
		if (!(obj instanceof TopLevelRefSetMemberNode)) {
			return false;
		}
		final TopLevelRefSetMemberNode other = (TopLevelRefSetMemberNode) obj;
		if (conceptId != other.conceptId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("TopLevelRefSetMemberNode [conceptId=");
		builder.append(conceptId);
		builder.append(", label=");
		builder.append(label);
		builder.append("]");
		return builder.toString();
	}
}