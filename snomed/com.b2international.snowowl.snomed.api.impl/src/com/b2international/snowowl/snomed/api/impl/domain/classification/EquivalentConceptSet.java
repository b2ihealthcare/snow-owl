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

import java.util.List;

import com.b2international.snowowl.snomed.api.domain.classification.IEquivalentConcept;
import com.b2international.snowowl.snomed.api.domain.classification.IEquivalentConceptSet;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 */
public class EquivalentConceptSet implements IEquivalentConceptSet {

	private boolean unsatisfiable;
	private List<IEquivalentConcept> equivalentConcepts;

	@Override
	public boolean isUnsatisfiable() {
		return unsatisfiable;
	}

	@Override
	public List<IEquivalentConcept> getEquivalentConcepts() {
		return equivalentConcepts;
	}

	public void setUnsatisfiable(final boolean unsatisfiable) {
		this.unsatisfiable = unsatisfiable;
	}

	@JsonDeserialize(contentAs=EquivalentConcept.class)
	public void setEquivalentConcepts(final List<IEquivalentConcept> equivalentConcepts) {
		this.equivalentConcepts = equivalentConcepts;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("EquivalentConceptSet [unsatisfiable=");
		builder.append(unsatisfiable);
		builder.append(", equivalentConcepts=");
		builder.append(equivalentConcepts);
		builder.append("]");
		return builder.toString();
	}
}