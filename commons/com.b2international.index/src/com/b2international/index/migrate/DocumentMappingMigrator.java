/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * @since 9.0
 */
public interface DocumentMappingMigrator<T> {

	/**
	 * Migrate the document to a newer schema by transforming it using the old document instance and a {@link Searcher} if additional information is required from the index.
	 * 
	 * @param oldDocument
	 * @param searcher
	 * @return the new document
	 */
	T migrate(T oldDocument, Searcher searcher);
	
	/**
	 * Simple no transformation migrator function that can be used to trigger reindex on a schema change that does not require any actual transformation just the reindex of its values.
	 * 
	 * @since 9.0
	 */
	class ReindexAsIs implements DocumentMappingMigrator<Object> {
		@Override
		public Object migrate(Object oldDocument, Searcher searcher) {
			return oldDocument;
		}
	}; 
	
}
