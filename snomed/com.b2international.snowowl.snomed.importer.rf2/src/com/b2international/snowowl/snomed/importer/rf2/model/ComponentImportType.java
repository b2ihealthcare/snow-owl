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
package com.b2international.snowowl.snomed.importer.rf2.model;

import com.google.common.base.Preconditions;

/**
 * Enumerates all supported SNOMED CT component importers. Enumeration order
 * also determines the order in which release files are imported.
 * 
 */
public enum ComponentImportType {
	
	CONCEPT("concept"),
	DESCRIPTION("description"),
	TEXT_DEFINITION("text definition"),
	RELATIONSHIP("relationship"),
	STATED_RELATIONSHIP("stated relationship"),
	LANGUAGE_TYPE_REFSET("language type reference set member"),
	SIMPLE_TYPE_REFSET("simple type reference set member"),
	ATTRIBUTE_VALUE_REFSET("attribute value type reference set member"),
	ASSOCIATION_TYPE_REFSET("association type reference set member"),
	SIMPLE_MAP_TYPE_REFSET("simple map type reference set member"),
	SIMPLE_MAP_TYPE_REFSET_WITH_DESCRIPTION("simple map type reference set member"),
	COMPLEX_MAP_TYPE_REFSET("complex map type reference set member"),
	QUERY_TYPE_REFSET("query type reference set member"),
	DESCRIPTION_TYPE_REFSET("description format reference set member"),
	CONCRETE_DOMAIN_REFSET("concrete domain reference set member"),
	EXTENDED_CONCRETE_DOMAIN_REFSET("concrete domain reference set member"), // XXX: display name intentionally duplicated
	EXTENDED_MAP_TYPE_REFSET("extended map type reference set member"),
	TERMINOLOGY_REGISTRY("terminology registry"),
	MODULE_DEPENDENCY_REFSET("module dependency reference set member"),
	OWL_AXIOM_REFSET("owl axiom reference set member");
	
	private final String displayName;

	private ComponentImportType(final String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getDirectoryPartName() {
		return name().toLowerCase();
	}
	
	/**Returns {@code true} if the type argument is not {@code null} and represents any of the available reference set types.*/
	public static boolean isRefSetType(final ComponentImportType type) {
		return !CONCEPT.equals(Preconditions.checkNotNull(type, "Component import type argument cannot be null.")) 
				&& !DESCRIPTION.equals(type)
				&& !TEXT_DEFINITION.equals(type)
				&& !RELATIONSHIP.equals(type)
				&& !STATED_RELATIONSHIP.equals(type)
				&& !TERMINOLOGY_REGISTRY.equals(type);
	}
}
