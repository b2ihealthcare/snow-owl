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

import com.google.common.collect.Ordering;

/**
 * Ordering implementation for ontology changes of arbitrary subjects; orders additions in front of removals, placing
 * null values at the end.
 * 
 */
public final class OntologyChangeOrdering extends Ordering<OntologyChange<?>> {
	
	public static final Ordering<OntologyChange<?>> INSTANCE = new OntologyChangeOrdering().nullsLast();
	
	private OntologyChangeOrdering() {
		// This class should not be instantiated.
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.google.common.collect.Ordering#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final OntologyChange<?> o1, final OntologyChange<?> o2) {
		return o1.getNature().compareTo(o2.getNature());
	}
}