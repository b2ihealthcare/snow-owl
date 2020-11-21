/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;

import com.b2international.index.es.client.EsClient;
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
	 * Clears all associated data from the index for the specified types.
	 * 
	 * @param types
	 *            - the types to remove completely from the index
	 */
	void clear(Collection<Class<?>> types);

	/**
	 * Returns the settings of this index.
	 * 
	 * @return
	 */
	Map<String, Object> settings();
	
	/**
	 * Updates the dynamic settings of the underlying indices. NOTE: this currently supports only a few parameters, like the max_result_window.
	 * 
	 * @param newSettings
	 */
	void updateSettings(Map<String, Object> newSettings);

	/**
	 * @return all the mappings available for the underlying indices to work with.
	 */
	Mappings mappings();
	
	/**
	 * Updates the mappings available for the underlying indices to work with.
	 * 
	 * NOTE: keep in mind that these won't affect existing indices and operations on this class might not run on all previously created indices. 
	 */
	void updateMappings(Mappings mappings);

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
	 * @return the Elasticsearch client used by this {@link IndexAdmin}.
	 */
	EsClient client();

	/**
	 * @return the indices maintained by this {@link IndexAdmin}
	 */
	default String[] indices() {
		return mappings().getMappings()
				.stream()
				.map(this::getTypeIndex)
				.distinct()
				.toArray(String[]::new);
	}

}
