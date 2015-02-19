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
package com.b2international.snowowl.core.api;

import java.io.Serializable;

/**
 * Null implementation of the {@link IComponent} interface.
 * 
 */
public enum NullComponent implements IComponent<Object>, Serializable {

	/**The singleton NULL instance with {@code null} ID and empty string label.*/
	INSTANCE;
	
	/**
	 * <b>NOTE:&nbsp;</b>this method always returns with {@code null}.<br><br>
	 * {@inheritDoc}
	 */
	@Override
	public Object getId() {
		return null;
	}

	/**
	 * <b>NOTE:&nbsp;</b>this method always returns with an empty string.<br><br>
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return "";
	}
	
	@Override
	public String toString() {
		return "";
	}
	
	/**
	 * @return the {@link #INSTANCE null implementation} singleton instance.
	 */
	@SuppressWarnings("unchecked")
	public static <K> IComponent<K> getNullImplementation() {
		return (IComponent<K>) INSTANCE;
	}
	
	/**
	 * Returns {@code true} only and if only the specified object is either {@code null} or equals with the {@link #INSTANCE null instance}.
	 * @param object the object to check.
	 * @return {@code true} if the specified object is either {@code null} or equals with the {@link #INSTANCE null instance}.
	 *  Otherwise returns with false.
	 */
	public static boolean isNullComponent(final Object object) {
		return null == object || INSTANCE.equals(object);
	}
	
}