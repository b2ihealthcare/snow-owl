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
package com.b2international.snowowl.snomed.datastore.id;

import com.b2international.snowowl.core.terminology.ComponentCategory;

/**
 * @since 4.5
 */
public interface IdManager {

	/**
	 * Executes ID related actions after an unsuccessful commit.
	 */
	void rollback();

	/**
	 * Executes ID related actions after a successful commit.
	 */
	void commit();

	/**
	 * Generates a new SNOMED identifier.
	 * 
	 * @param namespace
	 *            the namespace for the ID.
	 * @param category
	 *            the category for the ID.
	 */
	SnomedIdentifier generate(String namespace, ComponentCategory category);

	/**
	 * 
	 */
	void register(SnomedIdentifier identifier);

	/**
	 * Reserves a new SNOMED identifier.
	 * 
	 * @param namespace
	 *            the namespace for the ID.
	 * @param category
	 *            the category for the ID.
	 */
	SnomedIdentifier reserve(String namespace, ComponentCategory category);

	/**
	 * Deprecates the given SNOMED identifier.
	 * 
	 * @param identifier
	 *            the identifier to deprecate.
	 */
	void deprecate(SnomedIdentifier identifier);

	/**
	 * Releases the given SNOMED identifier.
	 * 
	 * @param identifier
	 *            the identifier to release.
	 */
	void release(SnomedIdentifier identifier);

	/**
	 * Publishes the given SNOMED identifier.
	 * 
	 * @param identifier
	 *            the identifier to publish.
	 */
	void publish(SnomedIdentifier identifier);

}
