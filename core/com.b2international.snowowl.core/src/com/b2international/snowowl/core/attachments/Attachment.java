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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

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
	public Attachment(@JsonProperty("attachmentId") String attachmentId, @JsonProperty("fileName") String fileName) {
		this(UUID.fromString(attachmentId), fileName);
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

	/**
	 * Downloads the file to the specified exportPath. The exportPath argument accepts both directory and file paths. Passing a file path replaces the
	 * default file name with the desired one. If the target path is a file path and there is an existing file, it will automatically overwrite the
	 * target file.
	 * 
	 * @param context
	 *            - the context to use for downloading the file
	 * @param target
	 *            - either target directory where the file needs to be downloaded or an abolute path to a file which will replace the default file
	 *            name included in this attachment
	 * @return the absolute file path to the downloaded file
	 * @since 7.7
	 * @see #download(ServiceProvider, Path)
	 */
	public Path download(ServiceProvider context, String target) {
		return download(context, Paths.get(target));
	}
	
	/**
	 * Downloads the file to the specified exportPath. The exportPath argument accepts both directory and file paths. Passing a file path replaces the
	 * default file name with the desired one. If the target path is a file path and there is an existing file, it will automatically overwrite the
	 * target file.
	 * 
	 * @param context
	 *            - the context to use for downloading the file
	 * @param target
	 *            - either target directory where the file needs to be downloaded or an abolute path to a file which will replace the default file
	 *            name included in this attachment
	 * @return the absolute file path to the downloaded file
	 * @since 7.7
	 */
	public Path download(ServiceProvider context, Path target) {
		Preconditions.checkNotNull(context, "Context cannot be null");
		Preconditions.checkNotNull(target, "ExportPath cannot be null");
		Path resultFile;
		if (Files.isDirectory(target)) {
			resultFile = target.resolve(getFileName());
		} else {
			resultFile = target;
		}
		try (OutputStream out = Files.newOutputStream(resultFile)) {
			context.service(AttachmentRegistry.class).download(attachmentId, out);
		} catch (IOException e) {
			throw new SnowowlRuntimeException(String.format("Couldn't download file '%s'.", getFileName()), e);
		}
		return resultFile;
	}
	
	/**
	 * @param context - the context to use for uploading
	 * @param source - the source path to upload
	 * @return an {@link Attachment} descriptor
	 * @since 7.7
	 */
	public static Attachment upload(ServiceProvider context, Path source) {
		return upload(context, source, UUID.randomUUID());
	}

	/**
	 * Uploads an attachment using the specified file path as source and the specified attachmentId as the identifier of the attachment.
	 * 
	 * @param context - the context to use for uploading
	 * @param source - the source path to upload
	 * @param attachmentId - the identifier of the attachment to retrieve it later
	 * @return an {@link Attachment} descriptor
	 * @since 7.7
	 */
	public static Attachment upload(ServiceProvider context, Path source, UUID attachmentId) {
		Preconditions.checkArgument(Files.isRegularFile(source), "Source is not a valid file: %s", source);
		try (final InputStream in = Files.newInputStream(source)) {
			context.service(AttachmentRegistry.class).upload(attachmentId, in);
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
		return new Attachment(attachmentId, source.getFileName().toString());
	}

}
