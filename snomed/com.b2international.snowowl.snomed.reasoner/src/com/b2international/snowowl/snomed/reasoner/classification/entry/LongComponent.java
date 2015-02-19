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

import com.b2international.snowowl.core.api.IComponentWithIconId;

/**
 * Minimal {@link IComponentWithIconId} implementation with {@code long} component key.
 * <p>
 * <b>TODO</b>: add non-boxing getter methods if the current ones cause performance problems
 */
public class LongComponent implements IComponentWithIconId<Long> {

	private static final long serialVersionUID = -1213161598884377108L;

	private final long id;
	private final String label;
	private final long iconId;

	/**
	 * Creates a new {@link LongComponent} instance with the specified arguments.
	 * 
	 * @param id the {@code long} component key
	 * @param label the component label
	 * @param iconId the {@code long} icon
	 */
	public LongComponent(final long id, final String label, final long iconId) {
		this.id = id;
		this.label = label;
		this.iconId = iconId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponent#getId()
	 */
	@Override public Long getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponent#getLabel()
	 */
	@Override public String getLabel() {
		return label;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponentWithIconId#getIconId()
	 */
	@Override public Long getIconId() {
		return iconId;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (iconId ^ (iconId >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((label == null) ? 0 : label.hashCode());
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
		final LongComponent other = (LongComponent) obj;
		if (iconId != other.iconId)
			return false;
		if (id != other.id)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
}