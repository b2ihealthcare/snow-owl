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
package com.b2international.snowowl.snomed.reasoner.classification.entry;

/**
 * Abstract base class for reasoner change entries.
 */
public abstract class AbstractChangeEntry implements IChangeEntry {

	private static final long serialVersionUID = -4015153137195793820L;

	/**
	 * Enumerates the possible natures of an {@link AbstractChangeEntry}.
	 */
	public enum Nature {
		INFERRED, REDUNDANT
	}

	private final LongComponent source;
	private final Nature nature;

	protected AbstractChangeEntry(final Nature nature, final LongComponent source) {
		this.source = source;
		this.nature = nature;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.entry.IChangeEntry#getSource()
	 */
	@Override public LongComponent getSource() {
		return source;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.entry.IChangeEntry#getNature()
	 */
	@Override public Nature getNature() {
		return nature;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nature == null) ? 0 : nature.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractChangeEntry other = (AbstractChangeEntry) obj;
		if (nature != other.nature)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}
}