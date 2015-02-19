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
package com.b2international.snowowl.snomed.reasoner.classification;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableList;

/**
 * The return type of {@link SnomedReasonerService#getEquivalentConcepts(UUID)} requests.
 * 
 */
public class GetEquivalentConceptsResponse extends AbstractResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<AbstractEquivalenceSet> equivalenceSets;

	/**
	 * Creates a new response with the specified type and an empty list of equivalence sets.
	 * @param type the response type (preferably {@link Type#NOT_AVAILABLE} or {@link Type#STALE})
	 */
	public GetEquivalentConceptsResponse(final Type type) {
		this(type, ImmutableList.<AbstractEquivalenceSet>of());
	}

	/**
	 * Creates a new response with the specified type and list of equivalence sets.
	 * @param type the response type
	 * @param equivalenceSets the list of equivalence sets found during classification
	 */
	public GetEquivalentConceptsResponse(final Type type, final List<? extends AbstractEquivalenceSet> equivalenceSets) {
		super(type);
		this.equivalenceSets = ImmutableList.copyOf(equivalenceSets);
	}

	/**
	 * @return the list of equivalence sets found during classification (never {@code null})
	 */
	public List<AbstractEquivalenceSet> getEquivalenceSets() {
		return equivalenceSets;
	}
}