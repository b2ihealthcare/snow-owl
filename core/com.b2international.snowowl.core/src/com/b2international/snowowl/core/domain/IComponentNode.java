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
package com.b2international.snowowl.core.domain;

/**
 * Represents a component node in a component graph.
 * <p>
 * Component node representations can be different from {@link IComponent component representations}; the former is
 * usually a subset of the latter.
 * 
 * @see IComponentEdge
 */
public interface IComponentNode {

	/**
	 * Returns the component identifier.
	 * 
	 * @return the component identifier
	 */
	String getId();
}
