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
package com.b2international.commons.hierarchy;

/**
 * Represents a hierarchical element that holds parentage information about its
 * direct ancestor based on any kind of logical classification.
 *
 */
public interface Descendant<E> {

	/**
	 * Returns with the ancestors.
	 * @return the ancestors.
	 */
	E getParent();
	
}