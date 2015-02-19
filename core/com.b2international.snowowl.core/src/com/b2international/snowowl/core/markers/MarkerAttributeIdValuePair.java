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
package com.b2international.snowowl.core.markers;

import org.eclipse.core.resources.IMarker;

import com.google.common.base.Preconditions;

/**
 * Represents a pair of attribute identifier and a value of a {@link IMarker marker}.
 * @param <T> type of the attribute
 */
public class MarkerAttributeIdValuePair<T> {

	private final T attributeValue;
	private final Class<T> type;
	private final String attributeId;
	
	public MarkerAttributeIdValuePair(final String attributeId, final T attributeValue) {
		this.attributeId = attributeId;
		this.attributeValue = attributeValue;
		Preconditions.checkNotNull(attributeId, "Attribute identifier argument cannot be null.");
		Preconditions.checkNotNull(attributeValue, "Attribute value argument cannot be null.");
		type = (Class<T>) attributeValue.getClass();
	}
	
	/**
	 * Returns the class of the attribute value.
	 * @return the class of the attribute value.
	 */
	public Class<T> getType() {
		return type;
	}

	/**
	 * Returns with the unique attribute identifier.
	 * @return the unique ID.
	 */
	public String getAttributeId() {
		return attributeId;
	}
	
	/**
	 * Returns with the value of the attribute.
	 * @return the value of the attribute.
	 */
	public T getAttributeValue() {
		return attributeValue;
	}
	
}