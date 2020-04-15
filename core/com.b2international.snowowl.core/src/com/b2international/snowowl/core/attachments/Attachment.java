/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.attachments;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * Descriptor for attachments attached to the {@link AttachmentRegistry}.
 * 
 * @since 6.17
 */
public final class Attachment implements Serializable {

	private static final long serialVersionUID = 2L;

	/*
	 * Stored as String as it is serialized between the server and client
	 */
	@NotNull
	private UUID attachmentId;

	@NotEmpty
	private String fileName;

	/**
	 * Creates a new attachment descriptor with a random UUID and the given file name.
	 * 
	 * @param fileName
	 *            - the file name of the attachment
	 */
	public Attachment(String fileName) {
		this(UUID.randomUUID(), fileName);
	}

	/**
	 * Creates an attachment descriptor
	 * 
	 * @param attachmentId
	 *            - the identifier of the attachment
	 * @param fileName
	 *            - the file name of the attachment
	 */
	@JsonCreator
	public Attachment(@JsonProperty("attachmentId") String attachmentId, @JsonProperty("fileName") String name) {
		this(UUID.fromString(attachmentId), name);
	}

	/**
	 * Creates an attachment descriptor
	 * 
	 * @param attachmentId
	 *            - the identifier of the attachment
	 * @param fileName
	 *            - the file name of the attachment
	 */
	public Attachment(UUID attachmentId, String fileName) {
		this.attachmentId = attachmentId;
		this.fileName = fileName;
	}

	@JsonProperty("attachmentId")
	public String getAttachmentIdString() {
		return attachmentId.toString();
	}

	public UUID getAttachmentId() {
		return attachmentId;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(attachmentId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Attachment other = (Attachment) obj;
		return Objects.equals(attachmentId, other.attachmentId);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass()).add("attachmentId", attachmentId).add("fileName", fileName).toString();
	}

}
