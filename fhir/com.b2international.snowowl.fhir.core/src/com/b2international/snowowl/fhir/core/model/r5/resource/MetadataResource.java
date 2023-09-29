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

import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.datatype.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.r5.datatype.ContactDetail;
import com.b2international.snowowl.fhir.core.model.r5.datatype.Period;
import com.b2international.snowowl.fhir.core.model.r5.datatype.RelatedArtifact;

/**
 * The MetadataResource represents resources that carry additional publication
 * metadata over other canonical resources, describing their review and use in
 * more details.
 * 
 * @see <a href="https://hl7.org/fhir/R5/metadataresource.html#resource">2.1.27.8.4 Resource Content</a>
 * @since 9.0
 */
public abstract class MetadataResource extends CanonicalResource {

	/** When the resource was approved by the publisher */
	private ZonedDateTime approvalDate;

	/** When the resource was last reviewed by the publisher */
	private ZonedDateTime lastReviewDate;
	
	/** The period when the resource is expected to be used */
	@Summary
	private Period effectivePeriod;
	
	/** Topics relevant to the resource, eg. Education, Treatment, Assessment, ... */
	private List<CodeableConcept> topic;

	/** Who authored the resource */
	private List<ContactDetail> author;

	/** Who edited the resource */
	private List<ContactDetail> editor;

	/** Who reviewed the resource */
	private List<ContactDetail> reviewer;

	/** Who endorsed the resource */
	private List<ContactDetail> endorser;

	/** Additional documentation, citations, etc. */
	private List<RelatedArtifact> relatedArtifact;

	public ZonedDateTime getApprovalDate() {
		return approvalDate;
	}

	public void setApprovalDate(ZonedDateTime approvalDate) {
		this.approvalDate = approvalDate;
	}

	public ZonedDateTime getLastReviewDate() {
		return lastReviewDate;
	}

	public void setLastReviewDate(ZonedDateTime lastReviewDate) {
		this.lastReviewDate = lastReviewDate;
	}

	public Period getEffectivePeriod() {
		return effectivePeriod;
	}

	public void setEffectivePeriod(Period effectivePeriod) {
		this.effectivePeriod = effectivePeriod;
	}

	public List<CodeableConcept> getTopic() {
		return topic;
	}

	public void setTopic(List<CodeableConcept> topic) {
		this.topic = topic;
	}

	public List<ContactDetail> getAuthor() {
		return author;
	}

	public void setAuthor(List<ContactDetail> author) {
		this.author = author;
	}

	public List<ContactDetail> getEditor() {
		return editor;
	}

	public void setEditor(List<ContactDetail> editor) {
		this.editor = editor;
	}

	public List<ContactDetail> getReviewer() {
		return reviewer;
	}

	public void setReviewer(List<ContactDetail> reviewer) {
		this.reviewer = reviewer;
	}

	public List<ContactDetail> getEndorser() {
		return endorser;
	}

	public void setEndorser(List<ContactDetail> endorser) {
		this.endorser = endorser;
	}

	public List<RelatedArtifact> getRelatedArtifact() {
		return relatedArtifact;
	}

	public void setRelatedArtifact(List<RelatedArtifact> relatedArtifact) {
		this.relatedArtifact = relatedArtifact;
	}
}
