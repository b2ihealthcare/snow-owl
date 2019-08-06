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
package com.b2international.snowowl.snomed.reasoner.diff;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

/**
 * Represents an arbitrary change in the ontology. 
 * 
 *
 * @param <T> the change subject type
 */
public final class OntologyChange<T extends Serializable> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum Nature {
		ADD,
		REMOVE
	}
	
	private final Nature nature;
	private final long conceptId;
	private final T subject;
	
	public OntologyChange(final Nature nature, final long conceptId, final T subject) {
		this.nature = checkNotNull(nature, "nature");
		this.conceptId = conceptId;
		this.subject = checkNotNull(subject, "subject");
	}

	public Nature getNature() {
		return nature;
	}
	
	public long getConceptId() {
		return conceptId;
	}
	
	public T getSubject() {
		return subject;
	}
}