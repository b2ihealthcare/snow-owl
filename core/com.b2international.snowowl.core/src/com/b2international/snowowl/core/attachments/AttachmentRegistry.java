/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;

/**
 * Registry that can manage file attachments uploaded by clients using this interface.
 * 
 * @since 5.7
 */
public interface AttachmentRegistry {

	/**
	 * @param file - the file to upload
	 * @return an {@link Attachment} descriptor
	 * @since 7.7
	 */
	default Attachment upload(File file) {
		return upload(file, UUID.randomUUID());
	}

	/**
	 * Uploads an attachment using the specified file as source and the specified attachmentId as the identifier of the attachment.
	 * 
	 * @param file - the file to upload
	 * @param attachmentId - the identifier of the attachment to retrieve it later
	 * @return an {@link Attachment} descriptor
	 * @since 7.7
	 */
	default Attachment upload(File file, UUID attachmentId) {
		try (final FileInputStream in = new FileInputStream(file)) {
			upload(attachmentId, in);
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
		return new Attachment(attachmentId, file.getName());
	}

	/**
	 * Uploads an attachment and saves it in the server's current data directory.
	 * 
	 * @param id
	 *            - the unique identifier of the attachment
	 * @param in
	 *            - the contents of the attachment
	 * @throws BadRequestException
	 *             - if the file is not a valid zip file
	 * @throws AlreadyExistsException
	 *             - if an attachment already exists with the given {@link UUID}
	 */
	void upload(UUID id, InputStream in) throws AlreadyExistsException, BadRequestException;

	/**
	 * Downloads an attachment from the server identified by the given {@link UUID}.
	 * 
	 * @param id
	 *            - the unique identifier of the attachment
	 * @param out
	 *            - the stream where we write the contents of the attachment
	 * @throws NotFoundException
	 *             - if the attachment does not exist with the given identifier
	 */
	void download(UUID id, OutputStream out) throws NotFoundException;

	/**
	 * Deletes the attachment associated with the given identifier. Does nothing when the attachment is missing or has been already deleted.
	 * 
	 * @param id
	 *            - the unique identifier of the attachment
	 */
	void delete(UUID id);

}
