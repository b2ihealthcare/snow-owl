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
package com.b2international.snowowl.core.api.browser;

import java.io.Serializable;

/**
 * Representation of a filtered client side terminology browser.
 * @see IClientTerminologyBrowser
 * @param <C> - type of the component.
 * @param <K> - type of the unique key of the component.
 */
public interface IFilterClientTerminologyBrowser<C, K> extends IClientTerminologyBrowser<C, K>, Serializable{

	/**
	 * Returns {@code true} if the current terminology browser instance contains the component given with it unique ID.
	 * @param componentId the unique ID of the component.
	 * @return {@code true} if the component exists in the filtered terminology, otherwise {@code false}.
	 */
	boolean contains(final K componentId);
	
	/**
	 * Returns with the number of components.
	 * @return the component number.
	 */
	int size();
	
	/**
	 * Returns with an iterable of the filtered component unique IDs. 
	 * @return the component IDs.
	 */
	Iterable<K> getFilteredIds();
	
	/**
	 * Sets the type of the filtered browser.
	 * @param type the desired type.
	 */
	void setType(final FilterTerminologyBrowserType type);
	
	/**
	 * Returns with the {@link FilterTerminologyBrowserType type} of the current filtered browser instance.
	 * @return the type.
	 */
	FilterTerminologyBrowserType getType();
	
}