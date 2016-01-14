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
package com.b2international.snowowl.core.api.browser;

import com.b2international.snowowl.core.api.IComponent;

/**
 * @since 4.6
 */
public interface TreeContentProvider<T extends IComponent<String>> {

	/**
	 * Return <code>true</code> if the given element has at least one child, <code>false</code> otherwise.
	 * 
	 * @param element
	 * @return
	 */
	boolean hasChildren(T element);

	/**
	 * Returns <code>true</code> if the given element has at least one parent, <code>false</code> otherwise.
	 * 
	 * @param element
	 * @return
	 */
	boolean hasParents(T element);

}
