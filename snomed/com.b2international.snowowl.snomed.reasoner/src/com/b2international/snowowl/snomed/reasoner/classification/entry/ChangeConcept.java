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

import com.b2international.snowowl.core.api.component.IdProvider;

/**
 * A compact representation of a SNOMED CT concept, used in reasoner change reports.
 */
public class ChangeConcept implements IdProvider<Long>, Serializable {

	private static final long serialVersionUID = -1213161598884377108L;

	private final long id;
	private final long iconId;

	public ChangeConcept(final long id, final long iconId) {
		this.id = id;
		this.iconId = iconId;
	}

	@Override 
	public Long getId() {
		return id;
	}

	public Long getIconId() {
		return iconId;
	}

	@Override 
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (iconId ^ (iconId >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override 
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final ChangeConcept other = (ChangeConcept) obj;

		if (iconId != other.iconId) { return false;	}
		if (id != other.id) { return false; }
		return true;
	}
}
