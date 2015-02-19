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
package com.b2international.snowowl.snomed.api.impl.domain.classification;

import com.b2international.snowowl.snomed.api.domain.classification.IEquivalentConcept;

/**
 */
public class EquivalentConcept implements IEquivalentConcept {

	private String id;
	private String label;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("EquivalentConcept [id=");
		builder.append(id);
		builder.append(", label=");
		builder.append(label);
		builder.append("]");
		return builder.toString();
	}
}