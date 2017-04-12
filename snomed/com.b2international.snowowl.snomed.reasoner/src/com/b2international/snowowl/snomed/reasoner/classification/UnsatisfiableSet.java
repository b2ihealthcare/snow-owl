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

/**
 * Represents unsatisfiable equivalence sets (holding concepts equivalent to {@code owl:Nothing}).
 */
public class UnsatisfiableSet extends AbstractEquivalenceSet implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new unsatisfiable equivalence set with the specified arguments.
	 * @param unsatisfiableConcepts the list containing unsatisfiable concepts
	 */
	public UnsatisfiableSet(final List<String> unsatisfiableConceptIds) {
		super(unsatisfiableConceptIds);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.AbstractEquivalenceSet#isUnsatisfiable()
	 */
	@Override public boolean isUnsatisfiable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.AbstractEquivalenceSet#getTitle()
	 */
	@Override public String getTitle() {
		return "Unsatisfiable concepts";
	}
}