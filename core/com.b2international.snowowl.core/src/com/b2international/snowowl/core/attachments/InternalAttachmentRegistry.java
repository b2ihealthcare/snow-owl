/*
 * Copyright 2017-2022 B2i Healthcare, https://b2ihealthcare.com
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
import java.util.UUID;

import com.b2international.commons.exceptions.NotFoundException;
import com.google.common.hash.HashCode;

/**
 * @since 5.7
 */
public interface InternalAttachmentRegistry extends AttachmentRegistry {

	/**
	 * Returns the attachment associated with the given identifier.
	 * @param id - the unique identifier of the attachment
	 * @return the file
	 * @throws NotFoundException
	 *             - if the attachment does not exist with the given identifier
	 */
	File getAttachment(UUID id) throws NotFoundException;

	/**
	 * Indicates that a client is intending to upload a file in smaller parts.
	 * @param clientId - the client identifier
	 * @param id - the identifier for the upload (can be <code>null</code>)
	 * @return <code>id</code> if specified, a random-generated UUID otherwise
	 */
	UUID startUpload(String clientId, UUID id);

	/**
	 * Sends the next section of a partial upload.
	 * @param id - the identifier for the upload
	 * @param chunk - the next section of the upload file to store
	 * @return number of bytes written so far
	 */
	long uploadChunk(UUID id, byte[] chunk);
	
	/**
	 * Indicated that the partial upload for the attachment is complete.
	 * @param id - the identifier of the upload
	 * @param expectedHashCode - the client-side computed hash which is compared against contents received on the server
	 */
	void completeUpload(UUID id, HashCode expectedHashCode);
	
	/**
	 * Indicates that a client is intending to download a file in smaller parts.
	 * @param clientId - the client identifier
	 * @param id - the attachment identifier
	 */	
	void startDownload(String clientId, UUID id);

	/**
	 * Populates the input array with the next section of the download file.
	 * @param clientId - the client identifier
	 * @param id - the attachment identifier
	 * @param chunk - the buffer to populate (with offset 0)
	 * @param length - the number of bytes to read
	 * @return - number of bytes actually read, or 0 if the end of file has been reached 
	 */
	int downloadChunk(String clientId, UUID id, byte[] chunk, int length);

	/**
	 * Indicates that the client has finished downloading the file.
	 * @param clientId - the client identifier
	 * @param id - the attachment identifier
	 * @param expectedHashCode - the client-side computed hash which is compared against the original file on the server
	 */
	void completeDownload(String clientId, UUID id, HashCode expectedHashCode);
}
