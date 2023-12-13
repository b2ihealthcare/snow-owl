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

/**
 * @since 9.0.0
 */
public enum DocumentMappingMigrationStrategy {

	/**
	 * No migration is required, all changes are backward compatible and can be picked up automatically via mapping update. Usually new fields without the need to backport values to existing documents.
	 */
	NO_REINDEX,
	
	/**
	 * Strategy where reindexing can be performed using the same index. Usually minor changes, new field aliases, etc.
	 */
	REINDEX_INPLACE,

	/**
	 * Strategy where a new index will be created in order to perform a reindex from the old one using a dedicated Java transformation function.
	 */
	REINDEX_SCRIPT;

}
