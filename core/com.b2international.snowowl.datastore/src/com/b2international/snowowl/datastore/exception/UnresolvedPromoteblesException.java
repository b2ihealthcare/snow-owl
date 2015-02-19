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
package com.b2international.snowowl.datastore.exception;

import java.util.Collection;
import java.util.Collections;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.datastore.delta.ComponentDelta;
import com.google.common.base.Preconditions;

/**
 * Exception indicating that a task promotion operation has unresolved items hence 
 * some items were not promoted.
 *
 */
public class UnresolvedPromoteblesException extends Exception {

	private static final long serialVersionUID = 4533024951716106650L;
	private final Collection<ComponentDelta> deltas;

	/**
	 * Creates a new exception instance.
	 * @param deltas. Should not be empty collection.
	 */
	public UnresolvedPromoteblesException(final Collection<ComponentDelta> deltas) {
		Preconditions.checkNotNull(deltas, "Deltas argument cannot be null.");
		Preconditions.checkState(!CompareUtils.isEmpty(deltas), "Component deltas cannot be null.");
		this.deltas = deltas;
	}
	
	/**
	 * Returns with the collection of unresolved promotable components.
	 */
	public Collection<ComponentDelta> getDeltas() {
		return Collections.unmodifiableCollection(deltas);
	}
	
}