/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.classification.entry;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract base class for reasoner change entries.
 */
public abstract class ChangeEntry implements Serializable {

	private static final long serialVersionUID = 2L;

	/**
	 * Enumerates the possible natures of an {@link ChangeEntry}.
	 */
	public enum Nature {

		INFERRED("Inferred"), 
		REDUNDANT("Redundant");

		private String name;

		private Nature(final String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private final Nature nature;

	private final String sourceId;
	private final String typeId;
	private final int group;

	protected ChangeEntry(final Nature nature, 
			final String sourceId,
			final String typeId,
			final int group) {

		this.nature = nature;
		this.sourceId = sourceId;
		this.typeId = typeId;
		this.group = group;
	}

	public Nature getNature() {
		return nature;
	}

	public String getSourceId() {
		return sourceId;
	}

	public String getTypeId() {
		return typeId;
	}

	public int getGroup() {
		return group;
	}

	@Override 
	public int hashCode() {
		return Objects.hash(nature, sourceId, typeId, group);
	}

	@Override 
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final ChangeEntry other = (ChangeEntry) obj;

		if (nature != other.nature) { return false; }
		if (!Objects.equals(sourceId, other.sourceId)) { return false; }
		if (!Objects.equals(typeId, other.typeId)) { return false; }
		if (group != other.group) { return false; }

		return true;
	}
}
