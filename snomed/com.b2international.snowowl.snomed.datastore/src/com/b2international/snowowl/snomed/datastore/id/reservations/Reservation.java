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
package com.b2international.snowowl.snomed.datastore.id.reservations;

import java.util.Collection;

import com.b2international.snowowl.snomed.datastore.ComponentNature;

/**
 * Represents a SNOMEDT CT Identifier reservation. A reservation is a range of SNOMED CT Identifiers, which are reserved for later use, therefore they
 * are not allowed to be used as IDs for new components (if their IDs have to be generated).
 * 
 * @since 4.0
 */
public interface Reservation {

	/**
	 * Returns the minimum SNOMED CT Item Identifier number, which is reserved by this {@link Reservation}.
	 * 
	 * @return
	 */
	long getItemIdMin();

	/**
	 * Returns the maximum SNOMED CT Item Identifier number, which is reserved by this {@link Reservation}.
	 * 
	 * @return
	 */
	long getItemIdMax();

	/**
	 * Returns the namespace ID of this {@link Reservation}. May return <code>null</code>, which means that this {@link Reservation} affects
	 * international SNOMED CT Identifiers.
	 * 
	 * @return
	 */
	String getNamespace();

	/**
	 * Returns the affected component types.
	 * 
	 * @return
	 */
	Collection<ComponentNature> getComponents();

	/**
	 * Returns <code>true</code> if the given componentId is conflicting with this {@link Reservation}'s range.
	 * 
	 * @param componentId
	 * @return
	 */
	boolean conflicts(String componentId);

}
