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
package com.b2international.snowowl.datastore.internal.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.file.FileRegistry;
import com.google.common.io.Files;

/**
 * The default implementation of the {@link FileRegistry} interface. Manages files in a local directory defined via the constructor parameter.
 * 
 * @since 5.7
 */
public final class DefaultFileRegistry implements InternalFileRegistry {

	private static final int ZIP_HEADER_LEN = 4; /*4 bytes*/
	private static final byte[] ZIP_HEADER = new byte[]{80, 75, 3, 4};
	
	private final Path folder;

	public DefaultFileRegistry(Path folder) {
		this.folder = folder;
		this.folder.toFile().mkdirs();
	}
	
	@Override
	public void upload(UUID id, InputStream in) {
		final Path destination = this.folder.resolve(id.toString());
		if (destination.toFile().exists()) {
			throw new AlreadyExistsException("Zip File", id.toString());
		}
		
		final BufferedInputStream bin = new BufferedInputStream(in);
		if (!isZip(bin)) {
			throw new BadRequestException("Attachment of '%s' is not a zip file", id);
		}
		
		try {
			Files.copy(() -> bin, destination.toFile());
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Failed to upload attachment of " + id, e);
		}
	}

	@Override
	public void download(UUID id, OutputStream out) {
		final File requestedFile = getFile(id);
		
		try {
			Files.copy(requestedFile, out);
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Failed to download attachment of " + id, e); 
		}
	}

	@Override
	public File getFile(UUID id) {
		final Path requestedPath = this.folder.resolve(id.toString());
		final File requestedFile = requestedPath.toFile();
		if (!requestedFile.exists()) {
			throw new NotFoundException("File", id.toString());
		}
		return requestedFile;
	}
	
	private static boolean isZip(InputStream in) {
		try {
			in.mark(ZIP_HEADER_LEN);
			byte[] header = new byte[ZIP_HEADER_LEN];
			in.read(header, 0, ZIP_HEADER_LEN);
			in.reset();
			return Arrays.equals(ZIP_HEADER, header);
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Failed to check zip header", e);
		}
	}

}
