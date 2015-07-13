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
package com.b2international.snowowl.datastore.server.internal.review;

import java.util.Set;

import com.b2international.snowowl.datastore.server.review.ConceptChanges;

/**
 * @since 4.2
 */
public class ConceptChangesImpl implements ConceptChanges {

	private final String id;
	private final Set<String> newConcepts;
	private final Set<String> changedConcepts;
	private final Set<String> deletedConcepts;

	public ConceptChangesImpl(final String id,
			final Set<String> newConcepts, 
			final Set<String> changedConcepts, 
			final Set<String> deletedConcepts) {

		this.id = id;
		this.newConcepts = newConcepts;
		this.changedConcepts = changedConcepts;
		this.deletedConcepts = deletedConcepts;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public Set<String> newConcepts() {
		return newConcepts;
	}

	@Override
	public Set<String> changedConcepts() {
		return changedConcepts;
	}

	@Override
	public Set<String> deletedConcepts() {
		return deletedConcepts;
	}
}
