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
package com.b2international.index.admin;

import java.util.Map;

import org.slf4j.Logger;

import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;

/**
 * Administration interface managing an elasticsearch index.
 * 
 * @since 4.7
 */
public interface IndexAdmin {

	/**
	 * Returns the {@link Logger} assigned to this index.
	 * 
	 * @return
	 */
	Logger log();

	/**
	 * Returns <code>true</code> if the index already exists, otherwise returns <code>false</code>.
	 */
	boolean exists();

	/**
	 * Creates the index if and only if does not exist yet, otherwise this method is no-op. Configure the defined {@link #mappings()} as well.
	 */
	void create();

	/**
	 * Deletes the entire index with its data if and only if does exist, otherwise this method is no-op.
	 */
	void delete();

	/**
	 * Clears a type and all its associated data from the index.
	 * 
	 * @param type
	 *            - the type to remove completely from the index
	 */
	<T> void clear(Class<T> type);

	/**
	 * Returns the settings of this index.
	 * 
	 * @return
	 */
	Map<String, Object> settings();

	/**
	 * Returns the mappings defined for this index.
	 * 
	 * @return
	 */
	Mappings mappings();

	/**
	 * Returns the index name prefix which will be used to identify all indexes that managed by this {@link IndexAdmin}.
	 * 
	 * @return
	 */
	String name();
	
	/**
	 * Returns the actual index name for the given {@link DocumentMapping}.
	 * 
	 * @param mapping
	 * @return
	 */
	String getTypeIndex(DocumentMapping mapping);

	/**
	 * Closes the underlying index.
	 */
	void close();

	/**
	 * Optimizes the underlying index until it has less than or equal segments than the supplied maxSegments number.
	 * 
	 * @param maxSegments
	 *            - max number of segments to force on the index
	 */
	void optimize(int maxSegments);

	/**
	 * @return whether the underlying index service supports _hash values on revision documents or not, by default assumes it does
	 */
	default boolean isHashSupported() {
		return true;
	}

}