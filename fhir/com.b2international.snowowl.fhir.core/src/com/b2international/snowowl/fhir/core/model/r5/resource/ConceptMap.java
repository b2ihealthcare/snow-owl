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
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.UriType;
import com.b2international.snowowl.fhir.core.model.r5.element.conceptmap.AdditionalAttribute;
import com.b2international.snowowl.fhir.core.model.r5.element.conceptmap.Group;
import com.b2international.snowowl.fhir.core.model.r5.element.conceptmap.Property;

/**
 * A concept map defines a mapping from a set of concepts defined in a code
 * system to one or more concepts defined in other code systems.
 * 
 * @see <a href="https://hl7.org/fhir/R5/conceptmap.html#resource">4.10.5 Resource Content</a>
 * @since 9.0
 */
public class ConceptMap extends MetadataResource {

	/** Additional properties of the mapping */
	@Summary
	private List<Property> property;
	
	/** Definition of an additional attribute to act as a data source or target */
	@Summary
	private List<AdditionalAttribute> additionalAttribute;
	
	/** The source value set that contains the concepts that are being mapped */
	@Summary
	private UriType sourceScopeUri;
	
	/** The source value set that contains the concepts that are being mapped (canonical) */
	@Summary
	private UriType sourceScopeCanonical;
	
	/** The target value set that contains the concepts that are being mapped */
	@Summary
	private UriType targetScopeUri;
	
	/** The target value set that contains the concepts that are being mapped (canonical) */
	@Summary
	private UriType targetScopeCanonical;
	
	/** Mapping groups (using same source and target systems for contained mappings) */
	private List<Group> group;

	public List<Property> getProperty() {
		return property;
	}

	public void setProperty(List<Property> property) {
		this.property = property;
	}

	public List<AdditionalAttribute> getAdditionalAttribute() {
		return additionalAttribute;
	}

	public void setAdditionalAttribute(List<AdditionalAttribute> additionalAttribute) {
		this.additionalAttribute = additionalAttribute;
	}

	public UriType getSourceScopeUri() {
		return sourceScopeUri;
	}

	public void setSourceScopeUri(UriType sourceScopeUri) {
		this.sourceScopeUri = sourceScopeUri;
	}

	public UriType getSourceScopeCanonical() {
		return sourceScopeCanonical;
	}

	public void setSourceScopeCanonical(UriType sourceScopeCanonical) {
		this.sourceScopeCanonical = sourceScopeCanonical;
	}

	public UriType getTargetScopeUri() {
		return targetScopeUri;
	}

	public void setTargetScopeUri(UriType targetScopeUri) {
		this.targetScopeUri = targetScopeUri;
	}

	public UriType getTargetScopeCanonical() {
		return targetScopeCanonical;
	}

	public void setTargetScopeCanonical(UriType targetScopeCanonical) {
		this.targetScopeCanonical = targetScopeCanonical;
	}

	public List<Group> getGroup() {
		return group;
	}

	public void setGroup(List<Group> group) {
		this.group = group;
	}
}
