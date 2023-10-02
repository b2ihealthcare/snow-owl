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
package com.b2international.snowowl.fhir.core.model.r5.resource;

import java.util.List;

import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.CodeType;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.UriType;
import com.b2international.snowowl.fhir.core.model.r5.element.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.r5.element.codesystem.Filter;
import com.b2international.snowowl.fhir.core.model.r5.element.codesystem.Property;

/**
 * Code systems define which codes (symbols and/or expressions) exist, and how
 * they are understood. Value sets select a set of codes from one or more code
 * systems to specify which codes can be used in a particular context.
 * <p>
 * The CodeSystem resource is used to declare the existence of a code system,
 * and its key properties:
 * 
 * <ul>
 * <li>Identifying URL and version
 * <li>Description, Copyright, publication date, and other metadata
 * <li>Some key properties of the code system itself - e.g. whether it exhibits 
 * concept permanence, whether it defines a compositional grammar, and whether 
 * the codes that it defines are case sensitive
 * <li>What filters can be used in value sets that use the code system in a 
 * <code>ValueSet.compose</code> element
 * <li>What concept properties are defined by the code system
 * </ul>
 * 
 * In addition, the CodeSystem resource may list some or all of the concepts in
 * the code system, along with their basic properties (code, display,
 * definition), designations, and additional properties. Code System resources
 * may also be used to define supplements, which extend an existing code system
 * with additional designations and properties.
 *
 * @see <a href="https://hl7.org/fhir/R5/codesystem.html#resource">4.8.5 Resource Content</a>
 * @since 9.0
 */
public class CodeSystem extends MetadataResource {

	/** Is code comparison is case sensitive? */
	@Summary
	private Boolean caseSensitive;
	
	/** Canonical reference to the value set with entire code system */
	@Summary
	private UriType valueSet;
	
	/** The meaning of the hierarchy of concepts */
	@Summary
	private CodeType hierarchyMeaning;
	
	/** Does the code system define a compositional grammar? */
	@Summary
	private Boolean compositional;
	
	/**
	 * Can concepts definitions be considered stable? If the value is set to
	 * <code>true</code>, concepts can be redefined and so a version must be
	 * specified when referencing this code system.
	 */
	@Summary
	private Boolean versionNeeded;
	
	/** The extent of the content that is represented in this code system resource */
	@Summary
	private CodeType content;

	/** Canonical URL of Code System this adds designations and properties to */
	@Summary
	private UriType supplements;
	
	/** Total concepts in the code system */
	@Summary
	private Integer count;
	
	/** Filter that can be used in a value set */
	@Summary
	private List<Filter> filter;
	
	/** Additional information supplied about each concept */
	@Summary
	private List<Property> property;
	
	/** Concepts in the code system */
	@Summary
	private List<Concept> concept;

	public Boolean getCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(Boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public UriType getValueSet() {
		return valueSet;
	}

	public void setValueSet(UriType valueSet) {
		this.valueSet = valueSet;
	}

	public CodeType getHierarchyMeaning() {
		return hierarchyMeaning;
	}

	public void setHierarchyMeaning(CodeType hierarchyMeaning) {
		this.hierarchyMeaning = hierarchyMeaning;
	}

	public Boolean getCompositional() {
		return compositional;
	}

	public void setCompositional(Boolean compositional) {
		this.compositional = compositional;
	}

	public Boolean getVersionNeeded() {
		return versionNeeded;
	}

	public void setVersionNeeded(Boolean versionNeeded) {
		this.versionNeeded = versionNeeded;
	}

	public CodeType getContent() {
		return content;
	}

	public void setContent(CodeType content) {
		this.content = content;
	}

	public UriType getSupplements() {
		return supplements;
	}

	public void setSupplements(UriType supplements) {
		this.supplements = supplements;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<Filter> getFilter() {
		return filter;
	}

	public void setFilter(List<Filter> filter) {
		this.filter = filter;
	}

	public List<Property> getProperty() {
		return property;
	}

	public void setProperty(List<Property> property) {
		this.property = property;
	}

	public List<Concept> getConcept() {
		return concept;
	}

	public void setConcept(List<Concept> concept) {
		this.concept = concept;
	}
}
