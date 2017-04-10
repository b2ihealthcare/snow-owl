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
import java.text.MessageFormat;
import java.util.List;

/**
 * Represents a regular equivalence set with one suggested concept as a replacement for the others.
 */
public class EquivalenceSet extends AbstractEquivalenceSet implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String suggestedConceptId;

	/**
	 * Creates a new regular equivalence set with the specified arguments.
	 * @param suggestedConcept the concept to suggest as a replacement
	 * @param concepts the list containing the rest of the equivalent concepts (the suggested concept should not be repeated in this list)
	 */
	public EquivalenceSet(final String suggestedConceptId, final List<String> conceptIds) {
		super(conceptIds);
		this.suggestedConceptId = suggestedConceptId;
	}

	/**
	 * @return the concept to suggest as a replacement
	 */
	public String getSuggestedConceptId() {
		return suggestedConceptId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.AbstractEquivalenceSet#isUnsatisfiable()
	 */
	@Override public boolean isUnsatisfiable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.classification.AbstractEquivalenceSet#getTitle()
	 */
	@Override public String getTitle() {
		return MessageFormat.format("Equivalent with {0}", suggestedConceptId);
	}
}