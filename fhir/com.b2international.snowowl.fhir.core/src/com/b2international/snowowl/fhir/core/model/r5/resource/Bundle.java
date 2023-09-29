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
import com.b2international.snowowl.fhir.core.model.r5.datatype.Identifier;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.CodeType;
import com.b2international.snowowl.fhir.core.model.r5.element.bundle.Entry;
import com.b2international.snowowl.fhir.core.model.r5.element.bundle.Link;
import com.b2international.snowowl.fhir.core.model.r5.element.bundle.Signature;

/**
 * One common operation performed with resources is to gather a collection of
 * resources into a single instance with containing context. In FHIR this is
 * referred to as "bundling" the resources together.
 *
 * @see <a href="https://hl7.org/fhir/R5/bundle.html#resource">2.4.4 Resource Content</a>
 * @since 9.0
 */
public class Bundle extends Resource {

	/** Persistent identifier for the resource */
	@Summary
	private Identifier identifier;

	/** The bundle's type (search set, collection, history, etc.) */
	@Summary
	private CodeType type;
	
	/** When the bundle was assembled */
	@Summary
	private ZonedDateTime timestamp;
	
	/** The total number of matches (if this bundle represents a search set) */
	@Summary
	private Integer total;
	
	/** Links related to this bundle */
	@Summary
	private List<Link> link;
	
	/** Bundle entries (carrying resources or information) */
	@Summary
	private List<Entry> entry;

	/** A digital signature for this bundle */
	@Summary
	private Signature signature;
	
	/** Issues reported for this bundle */
	@Summary
	private Resource issues;

	public Identifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public CodeType getType() {
		return type;
	}

	public void setType(CodeType type) {
		this.type = type;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List<Link> getLink() {
		return link;
	}

	public void setLink(List<Link> link) {
		this.link = link;
	}

	public List<Entry> getEntry() {
		return entry;
	}

	public void setEntry(List<Entry> entry) {
		this.entry = entry;
	}

	public Signature getSignature() {
		return signature;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	public Resource getIssues() {
		return issues;
	}

	public void setIssues(Resource issues) {
		this.issues = issues;
	}
}
