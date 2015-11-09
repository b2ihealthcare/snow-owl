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
package com.b2international.snowowl.snomed.datastore;

import java.io.Serializable;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexEntry;
import com.google.common.base.Objects;

/**
 * A transfer object representing a SNOMED CT concept.
 */
public class SnomedConceptIndexEntry extends SnomedIndexEntry implements IComponent<String>, IIndexEntry, Serializable {

	private static final long serialVersionUID = -824286402410205210L;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String id;
		private String iconId;
		private String moduleId;
		private long storageKey;
		private float score;
		private boolean active;
		private boolean released;
		private long effectiveTimeLong;
		private boolean primitive;
		private boolean exhaustive;

		private Builder() {
			// Disallow instantiation outside static method
		}

		public Builder id(final String id) {
			this.id = id;
			return this;
		}

		public Builder iconId(final String iconId) {
			this.iconId = iconId;
			return this;
		}

		public Builder moduleId(final String moduleId) {
			this.moduleId = moduleId;
			return this;
		}

		public Builder storageKey(final long storageKey) {
			this.storageKey = storageKey;
			return this;
		}

		public Builder score(final float score) {
			this.score = score;
			return this;
		}

		public Builder active(final boolean active) {
			this.active = active;
			return this;
		}

		public Builder released(final boolean released) {
			this.released = released;
			return this;
		}

		public Builder effectiveTimeLong(final long effectiveTimeLong) {
			this.effectiveTimeLong = effectiveTimeLong;
			return this;
		}

		public Builder primitive(final boolean primitive) {
			this.primitive = primitive;
			return this;
		}

		public Builder exhaustive(final boolean exhaustive) {
			this.exhaustive = exhaustive;
			return this;
		}

		public SnomedConceptIndexEntry build() {
			return new SnomedConceptIndexEntry(id, 
					iconId, 
					moduleId, 
					score,
					storageKey, 
					released, 
					active, 
					effectiveTimeLong, 
					primitive, 
					exhaustive);
		}
	}

	private final boolean primitive;
	private final boolean exhaustive;

	protected SnomedConceptIndexEntry(final String id, 
			final String iconId, 
			final String moduleId, 
			final float score, 
			final long storageKey,
			final boolean released,
			final boolean active,
			final long effectiveTimeLong,
			final boolean primitive,
			final boolean exhaustive) {

		super(id, 
				id, // XXX: concept ID is the same as the label, client code requires localization
				iconId, 
				moduleId, 
				score, 
				storageKey, 
				released,
				active,
				effectiveTimeLong);

		this.primitive = primitive;
		this.exhaustive = exhaustive;
	}

	/**
	 * @return {@code true} if the concept definition status is 900000000000074008 (primitive), {@code false} otherwise
	 */
	public boolean isPrimitive() {
		return primitive;
	}

	/**
	 * @return {@code true} if the concept subclass definition status is exhaustive, {@code false} otherwise
	 */
	public boolean isExhaustive() {
		return exhaustive;
	}

	@Override
	public int hashCode() {
		return 31 + getId().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		
		final SnomedConceptIndexEntry other = (SnomedConceptIndexEntry) obj;
		
		if (!Objects.equal(getId(), other.getId())) { return false; }
		return true;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("label", label)
				.add("iconId", iconId)
				.add("moduleId", getModuleId())
				.add("score", score)
				.add("storageKey", storageKey)
				.add("released", isReleased())
				.add("active", isActive())
				.add("effectiveTime", getEffectiveTimeAsLong())
				.add("primitive", primitive)
				.add("exhaustive", exhaustive)
				.toString();
	}
}
