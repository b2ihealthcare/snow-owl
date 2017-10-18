/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.taxonomy;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IStatus;

/**
 * @since 5.0
 */
public final class SnomedTaxonomyStatus {

	private final IStatus status;
	private final Collection<InvalidRelationship> invalidRelationships;

	public SnomedTaxonomyStatus(final IStatus status) {
		this(status, Collections.<InvalidRelationship> emptyList());
	}

	public SnomedTaxonomyStatus(final IStatus status, final Collection<InvalidRelationship> invalidRelationships) {
		this.status = status;
		this.invalidRelationships = invalidRelationships;
	}

	public IStatus getStatus() {
		return status;
	}

	public Collection<InvalidRelationship> getInvalidRelationships() {
		return invalidRelationships;
	}

}
