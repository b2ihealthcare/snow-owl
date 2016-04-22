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
package com.b2international.index;

import java.util.Map;

import com.b2international.index.admin.Administrable;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.mapping.MappingProvider;

/**
 * Generic interface for an elasticsearch index.
 * 
 * @since 4.7
 */
public interface Index extends MappingProvider, Searchable, Administrable<IndexAdmin> {

	/**
	 * Returns the name of the index.
	 * 
	 * @return
	 */
	String name();

	/**
	 * Fetch an object by type and key from the index.
	 * 
	 * @param type
	 *            - the object's type to retrieve
	 * @param key
	 *            - the unique identifier of the object
	 * @return the object
	 */
	<T> T get(Class<T> type, String key);

	/**
	 * Fetch an object by type and key from the index.
	 * 
	 * @param type
	 *            - the object's type to retrieve
	 * @param key
	 *            - the unique identifier of the object
	 * @return a {@link Map} of String, Object pairs representing the object
	 */
	Map<String, Object> get(String type, String key);

	/**
	 * Store/Put an object represented by the given {@link Map} of String, Object pairs in this index under the given type, identified with the given
	 * key.
	 * 
	 * @param type
	 *            - the object's type
	 * @param key
	 *            - the unique identifier of the object
	 * @param object
	 *            - the object to store
	 */
	void put(String type, String key, Object object);

	<T> void put(String key, T object);

	/**
	 * Put an object into the index with an index generated random identifier.
	 * 
	 * @param object
	 */
	<T> void put(T object);

	/**
	 * Remove a document from the index from the given types with the given id.
	 * 
	 * @param type
	 *            - the object's type
	 * @param key
	 *            - the unique identifier of the object
	 * @return - <code>true</code> if the document was found and removed, <code>false</code> otherwise
	 */
	boolean remove(String type, String key);

	/**
	 * Remove a document from the index from the given types with the given id.
	 * 
	 * @param type
	 *            - the object's type
	 * @param key
	 *            - the unique identifier of the object
	 * @return - <code>true</code> if the document was found and removed, <code>false</code> otherwise
	 */
	<T> boolean remove(Class<T> type, String key);


}