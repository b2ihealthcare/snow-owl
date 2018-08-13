/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.terminology;

import java.util.Collection;

import com.b2international.snowowl.core.domain.IComponent;

/**
 * Represents an available terminology.
 */
public interface Terminology {

	/**
	 * Returns with a human readable name of this instance.
	 * 
	 * @return the human readable name.
	 */
	String getName();

	/**
	 * Returns with the unique identifier of the terminology.
	 * 
	 * @return the unique terminology ID.
	 */
	String getId();

	/**
	 * Returns whether the terminology supports effective time updates and versioning the terminology requires an effective time to be set. By default
	 * it is set to <code>false</code>.
	 * 
	 * @return
	 */
	default boolean isEffectiveTimeSupported() {
		return false;
	}
	
	/**
	 * @return a {@link Collection} of domain classes that represent terminology components. The returned classes must have
	 *         {@link TerminologyComponent} on them.
	 */
	Collection<Class<? extends IComponent>> getTerminologyComponents();

}