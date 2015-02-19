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
package com.b2international.snowowl.core.api;

import com.google.common.base.Optional;

/**
 * Generic {@link ITerminologyComponent} implementation.
 * 
 */
public class TerminologyComponent<K> implements ITerminologyComponent<K> {

	private static final long serialVersionUID = -3128810042911273744L;
	
	final private K id;
	final private String label;
	final private K iconId;
	final private String terminologyShortName;
	final private Optional<String> terminologyOidOptional;
	
	public TerminologyComponent(K id, String label, K iconId, String terminologyShortName, String terminologyOid) {
		this.id = id;
		this.label = label;
		this.iconId = iconId;
		this.terminologyShortName = terminologyShortName;
		this.terminologyOidOptional = Optional.fromNullable(terminologyOid);
	}

	@Override
	public K getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public K getIconId() {
		return iconId;
	}
	
	@Override
	public String getTerminologyShortName() {
		return terminologyShortName;
	}

	@Override
	public Optional<String> getTerminologyOid() {
		return terminologyOidOptional;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((terminologyShortName == null) ? 0 : terminologyShortName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TerminologyComponent other = (TerminologyComponent) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (terminologyShortName == null) {
			if (other.terminologyShortName != null)
				return false;
		} else if (!terminologyShortName.equals(other.terminologyShortName))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "TerminologyComponent [id=" + id + ", label=" + label + ", terminologyShortName=" + terminologyShortName
				+ ", terminologyOidOptional=" + terminologyOidOptional + "]";
	}
	
}