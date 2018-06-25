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
package com.b2international.snowowl.snomed.datastore.index.constraint;

import java.util.Objects;

/**
 * Captures properties required for change tracking on individual components of the MRCM concept model.
 * 
 * @since 6.5
 */
public abstract class ConceptModelComponentFragment {

	private final String uuid;
	private final boolean active;
	private final long effectiveTime;
	private final String author;

	protected ConceptModelComponentFragment(
			final String uuid, 
			final boolean active, 
			final long effectiveTime, 
			final String author) {

		this.uuid = uuid;
		this.active = active;
		this.effectiveTime = effectiveTime;
		this.author = author;
	}

	public final String getUuid() {
		return uuid;
	}

	public final boolean isActive() {
		return active;
	}

	public final long getEffectiveTime() {
		return effectiveTime;
	}

	public final String getAuthor() {
		return author;
	}

	// XXX: Overriding equals and hashCode implementations are mandatory for each subclass
	
	@Override
	public int hashCode() {
		return Objects.hash(active, author, effectiveTime, uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		final ConceptModelComponentFragment other = (ConceptModelComponentFragment) obj;
		
		return active == other.active
				&& Objects.equals(author, other.author)
				&& effectiveTime == other.effectiveTime
				&& Objects.equals(uuid, other.uuid);
	}
}
