/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.UUID;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

/**
 * The default implementation of the {@link AttachmentRegistry} interface.
 * Manages attachments in a local directory defined via the constructor
 * parameter.
 * 
 * @since 5.7
 */
public final class DefaultAttachmentRegistry implements InternalAttachmentRegistry {

	private final Path folder;

	public DefaultAttachmentRegistry(Path folder) {
		this.folder = folder;
		this.folder.toFile().mkdirs();
	}

	@Override
	public void upload(UUID id, InputStream in) {
		final File file = toFile(id);
		if (file.exists()) {
			throw new AlreadyExistsException("Zip File", id.toString());
		}

		final BufferedInputStream bin = new BufferedInputStream(in);

		try {
			new ByteSource() {
				@Override
				public InputStream openStream() throws IOException {
					return bin;
				}
			}.copyTo(Files.asByteSink(file));
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Failed to upload attachment of " + id, e);
		}
	}

	@Override
	public void download(UUID id, OutputStream out) {
		final File requestedFile = getAttachment(id);

		try {
			Files.asByteSource(requestedFile).copyTo(new ByteSink() {
				@Override
				public OutputStream openStream() throws IOException {
					return out;
				}
			});
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Failed to download attachment of " + id, e);
		}
	}

	@Override
	public File getAttachment(UUID id) {
		final File requestedFile = toFile(id);
		if (!requestedFile.exists()) {
			throw new NotFoundException("File", id.toString());
		}
		return requestedFile;
	}

	@Override
	public void delete(UUID id) {
		toFile(id).delete();
	}

	private File toFile(UUID id) {
		return this.folder.resolve(id.toString()).toFile();
	}

}
