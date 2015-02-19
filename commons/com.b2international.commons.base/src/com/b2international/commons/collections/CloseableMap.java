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
package com.b2international.commons.collections;

import java.util.HashMap;

import com.b2international.commons.AutoCloseables;

/**
 * A closeable map implementation for storing closeable values.
 *
 */
public class CloseableMap<K, V extends AutoCloseable> extends HashMap<K, V> implements AutoCloseable {

	private static final long serialVersionUID = -1194826010993270732L;

	/**
	 * Creates a closeable map.
	 * @return a mutable closeable map instance.
	 */
	public static <K, V extends AutoCloseable> CloseableMap<K, V> newCloseableMap() {
		final CloseableMap<K, V> map = new CloseableMap<>();
		return map;
	}
	
	@Override
	public void close() throws Exception {
		AutoCloseables.close(values());
	}
	
}