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
package com.b2international.snowowl.snomed.exporter.server;

/**
 * Enumeration for all SNOMED&nbsp;CT component where the export to RF1/RF2 form is supported.
 * <p>Available types:
 * <ul>
 * <li>{@link #CONCEPT SNOMED&nbsp;CT concept}</li>
 * <li>{@link #DESCRIPTION SNOMED&nbsp;CT description}</li>
 * <li>{@link #TEXT_DEFINITION SNOMED&nbsp;CT text definition}</li>
 * <li>{@link #RELATIONSHIP SNOMED&nbsp;CT relationship}</li>
 * <li>{@link #STATED_RELATIONSHIP SNOMED&nbsp;CT stated relationship}</li>
 * <li>{@link #REF_SET SNOMED&nbsp;CT reference set}</li>
 * </ul>
 * </p>
 */
public enum ComponentExportType {

	/**
	 * SNOMED&nbsp;CT concept.
	 * @see ComponentExportType
	 */
	CONCEPT("Concept"),
	/**
	 * SNOMED&nbsp;CT description.
	 * @see ComponentExportType
	 */
	DESCRIPTION("Description"),
	/**
	 * SNOMED&nbsp;CT text definition.
	 * @see ComponentExportType
	 */
	TEXT_DEFINITION("TextDefinition"),
	/**
	 * SNOMED&nbsp;CT relationship.
	 * @see ComponentExportType
	 */
	RELATIONSHIP("Relationship"),
	/**
	 * SNOMED&nbsp;CT stated relationship.
	 * @see ComponentExportType
	 */
	STATED_RELATIONSHIP("StatedRelationship"),
	/**
	 * SNOMED&nbsp;CT reference set.
	 * @see ComponentExportType
	 */
	REF_SET("Refset");
	
	private String name;
	
	private ComponentExportType(final String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}