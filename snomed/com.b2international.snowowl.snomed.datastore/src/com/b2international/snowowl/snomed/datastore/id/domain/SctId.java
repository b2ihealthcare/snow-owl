/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id.domain;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.matchRange;

import java.io.Serializable;
import java.util.Collection;

import com.b2international.commons.CompareUtils;
import com.b2international.index.Doc;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a SNOMED CT identifier.
 * <p>
 * Note that properties must match the format used by SNOMED's component identifier service,
 * as this class is also used for marshalling results via Jackson when communicating over an HTTP client.
 * 
 * @since 4.5
 */
@Doc
public class SctId implements Serializable {

	public static class Fields {
		public static final String SEQUENCE = "sequence";
		public static final String NAMESPACE = "namespace";
		public static final String PARTITION_ID = "partitionId";
	}
	
	public static class Expressions {

		public static Expression namespace(String namespace) {
			final boolean intNamespace = CompareUtils.isEmpty(namespace);
			return exactMatch(Fields.NAMESPACE, intNamespace ? SnomedIdentifiers.INT_NAMESPACE : namespace);
		}

		public static Expression partitionId(String namespace, ComponentCategory category) {
			final boolean intNamespace = CompareUtils.isEmpty(namespace);
			return exactMatch(Fields.PARTITION_ID, (intNamespace ? "0" : "1") + Integer.toString(category.ordinal()));
		}

		public static Expression sequenceBetween(Long fromInclusive, Long toExclusive) {
			return matchRange(Fields.SEQUENCE, fromInclusive, toExclusive, true, false);
		}
	}
	
	private String sctid;

	private long sequence;

	private String namespace;

	private String partitionId;

	private long checkDigit;

	private String systemId;

	private String status;

	private String author;

	private String software;

	private String expirationDate;

	private String comment;

	private String jobId;

	private String created_at;

	private String modified_at;

	private Collection<String> additionalIds;

	/**
	 * @return the sctid
	 */
	public String getSctid() {
		return sctid;
	}

	/**
	 * @param sctid
	 *            the sctid to set
	 */
	public void setSctid(String sctid) {
		this.sctid = sctid;
	}

	/**
	 * @return the sequence
	 */
	public long getSequence() {
		return sequence;
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace
	 *            the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = CompareUtils.isEmpty(namespace) ? SnomedIdentifiers.INT_NAMESPACE : namespace;
	}
	
	/**
	 * @return the partitionId
	 */
	public String getPartitionId() {
		return partitionId;
	}

	/**
	 * @param partitionId
	 *            the partitionId to set
	 */
	public void setPartitionId(String partitionId) {
		this.partitionId = partitionId;
	}

	/**
	 * @return the checkDigit
	 */
	public long getCheckDigit() {
		return checkDigit;
	}

	/**
	 * @param checkDigit
	 *            the checkDigit to set
	 */
	public void setCheckDigit(long checkDigit) {
		this.checkDigit = checkDigit;
	}

	/**
	 * @return the systemId
	 */
	public String getSystemId() {
		return systemId;
	}

	/**
	 * @param systemId
	 *            the systemId to set
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the software
	 */
	public String getSoftware() {
		return software;
	}

	/**
	 * @param software
	 *            the software to set
	 */
	public void setSoftware(String software) {
		this.software = software;
	}

	/**
	 * @return the expirationDate
	 */
	public String getExpirationDate() {
		return expirationDate;
	}

	/**
	 * @param expirationDate
	 *            the expirationDate to set
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}

	/**
	 * @param jobId
	 *            the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	/**
	 * @return the created_at
	 */
	public String getCreated_at() {
		return created_at;
	}

	/**
	 * @param created_at
	 *            the created_at to set
	 */
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	/**
	 * @return the modified_at
	 */
	public String getModified_at() {
		return modified_at;
	}

	/**
	 * @param modified_at
	 *            the modified_at to set
	 */
	public void setModified_at(String modified_at) {
		this.modified_at = modified_at;
	}

	public Collection<String> getAdditionalIds() {
		return additionalIds;
	}

	public void setAdditionalIds(Collection<String> additionalIds) {
		this.additionalIds = additionalIds;
	}
	
	@JsonIgnore
	public boolean matches(final IdentifierStatus... status) {
		for (final IdentifierStatus s : status) {
			if (s.getSerializedName().equals(this.status)) {
				return true;
			}
		}
		
		return false;
	}
	
	@JsonIgnore
	public boolean isAvailable() {
		return IdentifierStatus.AVAILABLE.getSerializedName().equals(this.status);
	}
	
	@JsonIgnore
	public boolean isAssigned() {
		return IdentifierStatus.ASSIGNED.getSerializedName().equals(this.status);
	}
	
	@JsonIgnore
	public boolean isReserved() {
		return IdentifierStatus.RESERVED.getSerializedName().equals(this.status);
	}
	
	@JsonIgnore
	public boolean isPublished() {
		return IdentifierStatus.PUBLISHED.getSerializedName().equals(this.status);
	}
	
	@JsonIgnore
	public boolean isDeprecated() {
		return IdentifierStatus.DEPRECATED.getSerializedName().equals(this.status);
	}

	@JsonIgnore
	public ComponentCategory getComponentCategory() {
		return SnomedIdentifiers.getComponentCategory(sctid);
	}
	
}
