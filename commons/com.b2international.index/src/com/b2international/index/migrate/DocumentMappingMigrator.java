/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.index.migrate;

import com.b2international.index.Searcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @since 9.0.0
 */
public interface DocumentMappingMigrator {

	/**
	 * Init this migrator with a searcher opened on the current index.
	 * 
	 * @param searcher
	 */
	void init(Searcher searcher);
	
	/**
	 * Migrate the document to a newer schema by transforming it using the old document source and a {@link Searcher} opened on the current index content if additional information would be required.
	 * 
	 * @param source
	 * 
	 * @return the new, migrated document
	 */
	ObjectNode migrate(ObjectNode source, ObjectMapper mapper);
	
	/**
	 * Simple no transformation migrator function that can be used to trigger reindex on a schema change that does not require any actual transformation just the reindex of its values into a new index
	 * 
	 * @since 9.0.0
	 * @see DocumentMappingMigrationStrategy#REINDEX_SCRIPT
	 * @see DocumentMappingMigrationStrategy#REINDEX_INPLACE
	 */
	class ReindexAsIs implements DocumentMappingMigrator {
		
		@Override
		public void init(Searcher searcher) {
		}
		
		@Override
		public ObjectNode migrate(ObjectNode oldDocument, ObjectMapper mapper) {
			return oldDocument;
		}
	}; 
	
}
