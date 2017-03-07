/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;

/**
 * Registry that can manage zip files uploaded by clients via FileRequests.
 * 
 * @since 5.7
 */
public interface FileRegistry {

	/**
	 * Uploads a zip file and saves it in the server's current data directory.
	 * 
	 * @param id
	 *            - the unique identifier of the file
	 * @param in - the contents of the file
	 * @throws BadRequestException - if the file is not a valid zip file
	 * @throws AlreadyExistsException
	 *             - if a file already exists with the given {@link UUID}
	 */
	void upload(UUID id, InputStream in);

	/**
	 * Downloads a zip file from the server identified by the given {@link UUID}.
	 * 
	 * @param id
	 *            - the unique identifier of the file
	 * @param out - the stream where we write the contents of the file
	 * @throws NotFoundException
	 *             - if the file does not exist with the given identifier
	 */
	void download(UUID id, OutputStream out);
	
	/**
	 * Deletes the file associated with the given identifier. Does nothing when the file is missing or already deleted.
	 * 
	 * @param id - the unique identifier of the file
	 */
	void delete(UUID id);

}
