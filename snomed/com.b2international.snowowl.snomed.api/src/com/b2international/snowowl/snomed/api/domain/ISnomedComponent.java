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
package com.b2international.snowowl.snomed.api.domain;

import java.util.Date;

import com.b2international.snowowl.api.domain.IComponent;

/**
 * Holds common properties of SNOMED CT components.
 */
public interface ISnomedComponent extends IComponent {

	/**
	 * Returns the component's current status as a boolean value.
	 *  
	 * @return {@code true} if the component is active, {@code false} if it is inactive
	 */
	boolean isActive();

	/**
	 * Returns the date at which the current state of the component becomes effective.
	 * 
	 * @return the component's effective time
	 */
	Date getEffectiveTime();

	/**
	 * Returns the containing module's concept identifier.
	 * 
	 * @return the module identifier for the component
	 */
	String getModuleId();
}
