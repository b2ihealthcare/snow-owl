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
package com.b2international.snowowl.snomed.api.impl.traceability;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;

/**
 * Captures the concept's state after the commit along with changes to related components.
 */
class ConceptWithChanges {
	
	private ISnomedBrowserConcept concept;
	private final Set<TraceabilityChange> changes = newHashSet();

	public void setConcept(final ISnomedBrowserConcept concept) {
		this.concept = concept;
	}
	
	public void addChange(final TraceabilityChange componentChange) {
		changes.add(componentChange);
	}
	
	public ISnomedBrowserConcept getConcept() {
		return concept;
	}
	
	public Set<TraceabilityChange> getChanges() {
		return changes;
	}
}
