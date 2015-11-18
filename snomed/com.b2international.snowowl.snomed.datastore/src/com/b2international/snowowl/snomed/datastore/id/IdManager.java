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

import java.util.Collection;

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
	String generate(String namespace, ComponentCategory category);

	/**
	 * Registers the SNOMED identifier.
	 * 
	 * @param componentId
	 *            the ID to register.
	 */
	void register(String componentId);

	/**
	 * Reserves a new SNOMED identifier.
	 * 
	 * @param namespace
	 *            the namespace for the ID.
	 * @param category
	 *            the category for the ID.
	 */
	String reserve(String namespace, ComponentCategory category);

	/**
	 * Deprecates the given SNOMED identifier.
	 * 
	 * @param componentId
	 *            the component ID to deprecate.
	 */
	void deprecate(String componentId);

	/**
	 * Publishes the given SNOMED identifier.
	 * 
	 * @param componentId
	 *            the component ID to publish.
	 */
	void publish(String componentId);

	/**
	 * Generates multiple SNOMED identifiers.
	 * 
	 * @param namespace
	 *            the namespace for the ID.
	 * @param category
	 *            the category for the ID.
	 * @param quantity
	 *            the number of IDs to generate.
	 */
	Collection<String> bulkGenerate(String namespace, ComponentCategory category, int quantity);

	/**
	 * Registers multiple SNOMED identifiers.
	 * 
	 * @param componentIds
	 *            the IDs to register.
	 */
	void bulkRegister(Collection<String> componentIds);

	/**
	 * Reserves multiple new SNOMED identifiers.
	 * 
	 * @param namespace
	 *            the namespace for the ID.
	 * @param category
	 *            the category for the ID.
	 * @param quantity
	 *            the number of IDs to reserve.
	 */
	Collection<String> bulkReserve(String namespace, ComponentCategory category, int quantity);

	/**
	 * Deprecates the given SNOMED identifiers.
	 * 
	 * @param componentIds
	 *            the component IDs to deprecate.
	 */
	void bulkDeprecate(Collection<String> componentIds);

	/**
	 * Publishes the given SNOMED identifiers.
	 * 
	 * @param componentIds
	 *            the component IDs to publish.
	 */
	void bulkPublish(Collection<String> componentIds);

}
