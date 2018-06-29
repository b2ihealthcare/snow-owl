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
package com.b2international.snowowl.snomed.reasoner.domain;

import java.io.Serializable;

import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 7.0
 */
public final class EquivalentConceptSet implements Serializable {

	/**
	 * Enumerates expandable property keys.
	 */
	public static final class Expand {
		public static final String EQUIVALENT_CONCEPTS = "equivalentConcepts";
	}

	private String classificationId;
	private boolean unsatisfiable;
	private SnomedConcepts equivalentConcepts;

	@JsonIgnore
	public String getClassificationId() {
		return classificationId;
	}

	public void setClassificationId(final String classificationId) {
		this.classificationId = classificationId;
	}

	public boolean isUnsatisfiable() {
		return unsatisfiable;
	}

	public void setUnsatisfiable(final boolean unsatisfiable) {
		this.unsatisfiable = unsatisfiable;
	}

	public SnomedConcepts getEquivalentConcepts() {
		return equivalentConcepts;
	}

	public void setEquivalentConcepts(final SnomedConcepts equivalentConcepts) {
		this.equivalentConcepts = equivalentConcepts;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("EquivalentConceptSet [classificationId=");
		builder.append(classificationId);
		builder.append(", unsatisfiable=");
		builder.append(unsatisfiable);
		builder.append(", equivalentConcepts=");
		builder.append(equivalentConcepts);
		builder.append("]");
		return builder.toString();
	}
}
