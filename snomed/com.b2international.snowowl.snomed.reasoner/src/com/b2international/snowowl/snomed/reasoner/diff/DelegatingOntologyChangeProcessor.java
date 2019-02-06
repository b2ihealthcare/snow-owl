/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * @since 6.11
 */
public abstract class DelegatingOntologyChangeProcessor<T extends Serializable> extends OntologyChangeProcessor<T> {

	private final OntologyChangeProcessor<T> delegate;

	public DelegatingOntologyChangeProcessor(final OntologyChangeProcessor<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected void handleAddedSubject(final String conceptId, final T addedSubject) {
		delegate.handleAddedSubject(conceptId, addedSubject);
	}
	
	@Override
	protected void handleRemovedSubject(final String conceptId, final T removedSubject) {
		delegate.handleRemovedSubject(conceptId, removedSubject);
	}
}
