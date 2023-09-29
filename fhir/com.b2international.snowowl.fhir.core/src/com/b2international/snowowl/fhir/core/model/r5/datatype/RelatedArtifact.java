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
package com.b2international.snowowl.fhir.core.model.r5.datatype;

import java.time.ZonedDateTime;
import java.util.List;

import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.base.DataType;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.CodeType;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.UriType;

/**
 * @since 9.0
 */
public class RelatedArtifact extends DataType {

	@Summary
	private CodeType type;
	
	@Summary
	private List<CodeableConcept> classifier;
	
	@Summary
	private String label;
	
	@Summary
	private String display;
	
	@Summary
	private String citation;
	
	@Summary
	private Attachment document;
	
	/** What artifact is being referenced (canonical) */
	@Summary
	private UriType resource;
	
	@Summary
	private Reference resourceReference;

	@Summary
	private CodeType publicationStatus;
	
	@Summary
	private ZonedDateTime publicationDate;

	public CodeType getType() {
		return type;
	}

	public void setType(CodeType type) {
		this.type = type;
	}

	public List<CodeableConcept> getClassifier() {
		return classifier;
	}

	public void setClassifier(List<CodeableConcept> classifier) {
		this.classifier = classifier;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getCitation() {
		return citation;
	}

	public void setCitation(String citation) {
		this.citation = citation;
	}

	public Attachment getDocument() {
		return document;
	}

	public void setDocument(Attachment document) {
		this.document = document;
	}

	public UriType getResource() {
		return resource;
	}

	public void setResource(UriType resource) {
		this.resource = resource;
	}

	public Reference getResourceReference() {
		return resourceReference;
	}

	public void setResourceReference(Reference resourceReference) {
		this.resourceReference = resourceReference;
	}

	public CodeType getPublicationStatus() {
		return publicationStatus;
	}

	public void setPublicationStatus(CodeType publicationStatus) {
		this.publicationStatus = publicationStatus;
	}

	public ZonedDateTime getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(ZonedDateTime publicationDate) {
		this.publicationDate = publicationDate;
	}
}
