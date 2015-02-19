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
public class RegularRefSetMemberNode implements IRegularRefSetMemberNode {
	
	private static final long serialVersionUID = 1L;
	
	private final long conceptId;
	private final String id;
	private final String label;
	private final String uuid;
	private final long effectiveTime;
	private final long moduleId;
	private final long storageKey;
	private final boolean active;
	private final boolean released;

	public RegularRefSetMemberNode(final long conceptId, final String label, final String uuid, final long effectiveTime, final long moduleId, final long storageKey, 
			final boolean active, final boolean released) {
		
		this.conceptId = conceptId;
		this.id = Long.toString(conceptId);
		this.label = label;
		
		this.uuid = uuid;
		this.effectiveTime = effectiveTime;
		this.moduleId = moduleId;
		this.storageKey = storageKey;
		this.active = active;
		this.released = released;
	}
	
	@Override
	public boolean isRegularNode() {
		return true;
	}

	@Override
	public IRegularRefSetMemberNode asRegularNode() {
		return this;
	}

	@Override
	public long getConceptId() {
		return conceptId;
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
	public String getUuid() {
		return uuid;
	}

	@Override
	public long getEffectiveTime() {
		return effectiveTime;
	}

	@Override
	public long getModuleId() {
		return moduleId;
	}

	@Override
	public long getStorageKey() {
		return storageKey;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public boolean isReleased() {
		return released;
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
		if (!(obj instanceof RegularRefSetMemberNode)) {
			return false;
		}
		final RegularRefSetMemberNode other = (RegularRefSetMemberNode) obj;
		if (conceptId != other.conceptId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RegularRefSetMemberNode [conceptId=");
		builder.append(conceptId);
		builder.append(", id=");
		builder.append(id);
		builder.append(", label=");
		builder.append(label);
		builder.append(", uuid=");
		builder.append(uuid);
		builder.append(", effectiveTime=");
		builder.append(effectiveTime);
		builder.append(", moduleId=");
		builder.append(moduleId);
		builder.append(", storageKey=");
		builder.append(storageKey);
		builder.append(", active=");
		builder.append(active);
		builder.append(", released=");
		builder.append(released);
		builder.append("]");
		return builder.toString();
	}
}