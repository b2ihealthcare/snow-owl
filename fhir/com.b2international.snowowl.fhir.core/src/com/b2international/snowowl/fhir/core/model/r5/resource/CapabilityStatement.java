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
import com.b2international.snowowl.fhir.core.model.r5.element.capabilitystatement.Document;
import com.b2international.snowowl.fhir.core.model.r5.element.capabilitystatement.Implementation;
import com.b2international.snowowl.fhir.core.model.r5.element.capabilitystatement.Rest;
import com.b2international.snowowl.fhir.core.model.r5.element.capabilitystatement.Software;

/**
 * A Capability Statement documents a set of capabilities (behaviors) of a FHIR
 * Server or Client for a particular version of FHIR that may be used as a
 * statement of actual server functionality or a statement of required or
 * desired server implementation.
 *
 * @see <a href="https://hl7.org/fhir/R5/capabilitystatement.html#resource">5.3.4 Resource Content</a>
 * @since 9.0
 */
public class CapabilityStatement extends DomainResource {

	/** Canonical URL of another capability statement this implements */
	@Summary
	private List<UriType> instantiates;
	
	/** Canonical URL of another capability statement this adds to */
	@Summary
	private List<UriType> imports;
	
	/** Software that is covered by this capability statement */
	@Summary
	private Software software;
	
	/** A particular instance or installation that is described by this capability statement */
	@Summary
	private Implementation implementation;

	/** FHIR Version the system supports */
	@Summary
	private CodeType fhirVersion;
	
	/** Supported formats ("xml", "json", "ttl" or a MIME type) */
	@Summary
	private List<CodeType> format;
	
	/** Supported patch formats */
	@Summary
	private List<CodeType> patchFormat;

	/** Languages supported */
	@Summary
	private List<CodeType> acceptLanguage;
	
	/** Canonical URL of supported Implementation Guides */
	@Summary
	private List<UriType> implementationGuide;
	
	/** RESTful endpoint descriptions */
	@Summary
	private List<Rest> rest;
	
	/** Description of documents consumed / produced */
	@Summary
	private List<Document> document;

	public List<UriType> getInstantiates() {
		return instantiates;
	}

	public void setInstantiates(List<UriType> instantiates) {
		this.instantiates = instantiates;
	}

	public List<UriType> getImports() {
		return imports;
	}

	public void setImports(List<UriType> imports) {
		this.imports = imports;
	}

	public Software getSoftware() {
		return software;
	}

	public void setSoftware(Software software) {
		this.software = software;
	}

	public Implementation getImplementation() {
		return implementation;
	}

	public void setImplementation(Implementation implementation) {
		this.implementation = implementation;
	}

	public CodeType getFhirVersion() {
		return fhirVersion;
	}

	public void setFhirVersion(CodeType fhirVersion) {
		this.fhirVersion = fhirVersion;
	}

	public List<CodeType> getFormat() {
		return format;
	}

	public void setFormat(List<CodeType> format) {
		this.format = format;
	}

	public List<CodeType> getPatchFormat() {
		return patchFormat;
	}

	public void setPatchFormat(List<CodeType> patchFormat) {
		this.patchFormat = patchFormat;
	}

	public List<CodeType> getAcceptLanguage() {
		return acceptLanguage;
	}

	public void setAcceptLanguage(List<CodeType> acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}

	public List<UriType> getImplementationGuide() {
		return implementationGuide;
	}

	public void setImplementationGuide(List<UriType> implementationGuide) {
		this.implementationGuide = implementationGuide;
	}

	public List<Rest> getRest() {
		return rest;
	}

	public void setRest(List<Rest> rest) {
		this.rest = rest;
	}

	public List<Document> getDocument() {
		return document;
	}

	public void setDocument(List<Document> document) {
		this.document = document;
	}
}
