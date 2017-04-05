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
package com.b2international.snowowl.snomed.reasoner.server.diff;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndMolduleAssigner;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChange.Nature;

/**
 * A change processor that collects a list of changes.
 * 
 *
 * @param <T> the change subject's type
 */
public class OntologyChangeRecorder<T extends Serializable> extends OntologyChangeProcessor<T> {
	
	public static <T extends Serializable> OntologyChangeRecorder<T> create(final List<OntologyChange<T>> changes, final NamespaceAndMolduleAssigner assigner) {
		return new OntologyChangeRecorder<T>(changes, assigner);
	}
	
	private final List<OntologyChange<T>> changes;
	
	public OntologyChangeRecorder(final List<OntologyChange<T>> changes, final NamespaceAndMolduleAssigner assigner) {
		super(assigner);
		this.changes = changes;
	}

	@Override
	protected void handleAddedSubject(final String conceptId, final T addedSubject) {
		changes.add(new OntologyChange<T>(Nature.ADD, Long.valueOf(conceptId), addedSubject));
	}
	
	@Override
	protected void handleRemovedSubject(final String conceptId, final T removedSubject) {
		changes.add(new OntologyChange<T>(Nature.REMOVE, Long.valueOf(conceptId), removedSubject));
	}
	
	public void finish() {
		Collections.sort(changes, OntologyChangeOrdering.INSTANCE);
	}
}