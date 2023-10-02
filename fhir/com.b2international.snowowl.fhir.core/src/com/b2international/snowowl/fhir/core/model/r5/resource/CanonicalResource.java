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

import java.time.ZonedDateTime;
import java.util.List;

import com.b2international.snowowl.fhir.core.model.r5.ModifierElement;
import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.datatype.*;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.CodeType;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.UriType;

/**
 *  A canonical resource represents resources that have the following properties:
 *  
 *  <ul>
 *  <li>They have a canonical URL (note: all resources with a canonical URL are specializations of this type)
 *  <li>They have version, status, and data properties to help manage their publication
 *  <li>They carry some additional metadata about their use, including copyright information
 *  </ul>
 * 
 * @see <a href="https://hl7.org/fhir/R5/canonicalresource.html#resource">2.1.27.7.4 Resource Content</a>
 * @since 9.0
 */
public abstract class CanonicalResource extends Resource {

	/** Canonical identifier for this resource, represented as an absolute URI (globally unique) */
	@Summary
	private UriType url;

	/** Additional identifier for the resource */
	@Summary
	private List<Identifier> identifier;

	/** Business version of the resource */
	@Summary
	private String version;
	
	/** Algorithm used for comparing versions */
	@Summary
	private String versionAlgorithmString;
	
	/** Algorithm used for comparing versions */
	@Summary
	private Coding versionAlgorithmCoding;
	
	/** Name for this resource (computer friendly) */
	@Summary
	private String name;

	/** Name for this resource (human friendly) */
	@Summary
	private String title;
	
	/** The publication status of this resource */
	@ModifierElement
	@Summary
	private CodeType status;
	
	/** Is the resource for testing purposes or real usage? */
	@Summary
	private Boolean experimental;
	
	/** Date last changed */
	@Summary
	private ZonedDateTime date;

	/** Name of the publisher/steward (organization or individual) */
	@Summary
	private String publisher;

	/** Contact details for the publisher */
	@Summary
	private List<ContactDetail> contact;

	/** Natural language description of the resource (supports Markdown) */
	private String description;
	
	/** The context that the content is intended to support */
	@Summary
	private List<UsageContext> useContext;
	
	/** Intended jurisdiction for the resource */
	@Summary
	private List<CodeableConcept> jurisdiction;
	
	/** Describes why this resource was defined (supports Markdown) */
	private String purpose;
	
	/** Use and/or publishing restrictions (supports Markdown) */
	private String copyright;
	
	/** Copyright holder and year(s) */
	private String copyrightLabel;

	public UriType getUrl() {
		return url;
	}

	public void setUrl(UriType url) {
		this.url = url;
	}

	public List<Identifier> getIdentifier() {
		return identifier;
	}

	public void setIdentifier(List<Identifier> identifier) {
		this.identifier = identifier;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersionAlgorithmString() {
		return versionAlgorithmString;
	}

	public void setVersionAlgorithmString(String versionAlgorithmString) {
		this.versionAlgorithmString = versionAlgorithmString;
	}

	public Coding getVersionAlgorithmCoding() {
		return versionAlgorithmCoding;
	}

	public void setVersionAlgorithmCoding(Coding versionAlgorithmCoding) {
		this.versionAlgorithmCoding = versionAlgorithmCoding;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public CodeType getStatus() {
		return status;
	}

	public void setStatus(CodeType status) {
		this.status = status;
	}

	public Boolean getExperimental() {
		return experimental;
	}

	public void setExperimental(Boolean experimental) {
		this.experimental = experimental;
	}

	public ZonedDateTime getDate() {
		return date;
	}

	public void setDate(ZonedDateTime date) {
		this.date = date;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public List<ContactDetail> getContact() {
		return contact;
	}

	public void setContact(List<ContactDetail> contact) {
		this.contact = contact;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<UsageContext> getUseContext() {
		return useContext;
	}

	public void setUseContext(List<UsageContext> useContext) {
		this.useContext = useContext;
	}

	public List<CodeableConcept> getJurisdiction() {
		return jurisdiction;
	}

	public void setJurisdiction(List<CodeableConcept> jurisdiction) {
		this.jurisdiction = jurisdiction;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getCopyrightLabel() {
		return copyrightLabel;
	}

	public void setCopyrightLabel(String copyrightLabel) {
		this.copyrightLabel = copyrightLabel;
	}
}
