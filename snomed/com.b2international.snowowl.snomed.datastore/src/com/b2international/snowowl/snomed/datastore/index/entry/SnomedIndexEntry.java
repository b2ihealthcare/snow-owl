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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.AbstractIndexEntry;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Common superclass for SNOMED CT transfer objects.
 */
public abstract class SnomedIndexEntry extends AbstractIndexEntry implements IComponent<String>, Serializable {

	private static final long serialVersionUID = 1158021444792053062L;

	// XXX: Type parameter reveals subclass to AbstractBuilder for fluent API
	protected static abstract class AbstractBuilder<B extends AbstractBuilder<B>> {

		protected String id;
		protected String moduleId;
		protected long storageKey;
		protected float score;
		protected boolean active;
		protected boolean released;
		protected long effectiveTimeLong;

		public B id(final String id) {
			this.id = id;
			return getSelf();
		}

		public B moduleId(final String moduleId) {
			this.moduleId = moduleId;
			return getSelf();
		}

		public B storageKey(final long storageKey) {
			this.storageKey = storageKey;
			return getSelf();
		}

		public B score(final float score) {
			this.score = score;
			return getSelf();
		}

		public B active(final boolean active) {
			this.active = active;
			return getSelf();
		}

		public B released(final boolean released) {
			this.released = released;
			return getSelf();
		}

		public B effectiveTimeLong(final long effectiveTimeLong) {
			this.effectiveTimeLong = effectiveTimeLong;
			return getSelf();
		}

		protected abstract B getSelf();
	}

	protected final String moduleId;
	protected final boolean released;
	protected final boolean active;
	protected final long effectiveTimeLong;

	protected SnomedIndexEntry(final String id, 
			final String iconId, 
			final float score, 
			final long storageKey, 
			final String moduleId, 
			final boolean released, 
			final boolean active, 
			final long effectiveTimeLong) {

		super(id, 
				null, // XXX: As there are no definitive labels for SnomedIndexEntries, the identifier is set to null 
				iconId, 
				score, 
				storageKey);

		checkArgument(effectiveTimeLong >= EffectiveTimes.UNSET_EFFECTIVE_TIME, "Effective time argument '%s' is invalid.", effectiveTimeLong);

		this.moduleId = checkNotNull(moduleId, "Component module identifier may not be null.");
		this.released = released;
		this.active = active;
		this.effectiveTimeLong = effectiveTimeLong;
	}

	@Override
	public String getLabel() {
		throw new UnsupportedOperationException("Labels are not supported in SNOMED CT entries.");
	}

	/**
	 * @return {@code true} if the component has already appeared in an RF2 release, {@code false} otherwise
	 */
	public boolean isReleased() {
		return released;
	}

	/**
	 * @return {@code true} if the component is active, {@code false} otherwise
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @return the module concept identifier of this component
	 */
	public String getModuleId() {
		return moduleId;
	}

	/**
	 * @return the effective time of the component, or {@link EffectiveTimes#UNSET_EFFECTIVE_TIME} if the component currently has
	 *         no effective time set
	 */
	public long getEffectiveTimeAsLong() {
		return effectiveTimeLong;
	}

	@Override
	public int hashCode() {
		return 31 + id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedIndexEntry other = (SnomedIndexEntry) obj;

		if (!Objects.equal(id, other.id)) { return false; }
		return true;
	}

	protected ToStringHelper toStringHelper() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("label", label)
				.add("iconId", iconId)
				.add("moduleId", moduleId)
				.add("score", score)
				.add("storageKey", storageKey)
				.add("released", released)
				.add("active", active)
				.add("effectiveTime", effectiveTimeLong);
	}
}
