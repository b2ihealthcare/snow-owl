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
package com.b2international.snowowl.snomed.reasoner.diff;

import java.io.Serializable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A change processor that collects a list of changes.
 *
 * @param <T> the change subject's type
 */
public class OntologyChangeRecorder<T extends Serializable> extends OntologyChangeProcessor<T> {
	
	private final Multimap<String, T> addedSubjects = ArrayListMultimap.create();
	private final Multimap<String, T> removedSubjects = ArrayListMultimap.create();

	@Override
	protected void handleAddedSubject(final String conceptId, final T addedSubject) {
		addedSubjects.put(conceptId, addedSubject);
	}
	
	@Override
	protected void handleRemovedSubject(final String conceptId, final T removedSubject) {
		removedSubjects.put(conceptId, removedSubject);
	}
	
	public Multimap<String, T> getAddedSubjects() {
		return addedSubjects;
	}
	
	public Multimap<String, T> getRemovedSubjects() {
		return removedSubjects;
	}
}
