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
package com.b2international.snowowl.snomed.datastore.index.entry;

import java.io.Serializable;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.index.IIndexEntry;

/**
 * A transfer object representing a SNOMED CT concept.
 */
public class SnomedConceptIndexEntry extends SnomedIndexEntry implements IComponent<String>, IIndexEntry, Serializable {

	private static final long serialVersionUID = -824286402410205210L;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends AbstractBuilder<Builder> {

		private String iconId;
		private boolean primitive;
		private boolean exhaustive;

		private Builder() {
			// Disallow instantiation outside static method
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		public Builder iconId(final String iconId) {
			this.iconId = iconId;
			return getSelf();
		}

		public Builder primitive(final boolean primitive) {
			this.primitive = primitive;
			return getSelf();
		}

		public Builder exhaustive(final boolean exhaustive) {
			this.exhaustive = exhaustive;
			return getSelf();
		}

		public SnomedConceptIndexEntry build() {
			return new SnomedConceptIndexEntry(id, 
					iconId, 
					score, 
					storageKey,
					moduleId, 
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
			final float score, 
			final long storageKey, 
			final String moduleId,
			final boolean released,
			final boolean active,
			final long effectiveTimeLong,
			final boolean primitive,
			final boolean exhaustive) {

		super(id, 
				iconId,
				score, 
				storageKey, 
				moduleId, 
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
	public String toString() {
		return toStringHelper()
				.add("primitive", primitive)
				.add("exhaustive", exhaustive)
				.toString();
	}
}

