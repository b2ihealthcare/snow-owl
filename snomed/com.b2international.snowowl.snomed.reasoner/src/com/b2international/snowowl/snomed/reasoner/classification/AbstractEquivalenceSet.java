/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.classification;

import java.io.Serializable;
import java.util.List;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;

/**
 * Represents an abstract equivalence set, covering both concepts equivalent with another one, and unsatisfiable concepts (equivalent to
 * {@code owl:Nothing}).
 */
public abstract class AbstractEquivalenceSet implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<String> conceptIds;

	protected AbstractEquivalenceSet(final List<String> conceptIds) {
		this.conceptIds = conceptIds;
	}

	/**
	 * @return a list of {@link SnomedConceptDocument} objects participating in this equivalence set
	 */
	public List<String> getConceptIds() {
		return conceptIds;
	}

	/**
	 * @return {@code true} if this equivalence set is unsatisfiable (its members are equivalent to {@code owl:Nothing}), {@code false} otherwise
	 */
	public abstract boolean isUnsatisfiable();

	/**
	 * @return the display title for this equivalence set
	 */
	public abstract String getTitle();
}