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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.internal.file.DefaultFileRegistry;
import com.b2international.snowowl.datastore.internal.file.InternalFileRegistry;
import com.google.common.io.Resources;

/**
 * @since 5.7
 */
public class FileRegistryTest {

	private static final Path FOLDER = Paths.get("target", FileRegistry.class.getSimpleName().toLowerCase());
	private InternalFileRegistry registry;

	@Before
	public void setup() {
		this.registry = new DefaultFileRegistry(FOLDER);
	}
	
	@Test
	public void upload() throws Exception {
		final UUID id = UUID.randomUUID();
		upload(id, "file-reg-upload.zip");
		assertTrue(exists(id));
		
		// then download it and check size
		File downloaded = download(id, "file-reg-downloaded.zip");
		assertEquals(149 /*bytes*/, downloaded.length());
	}
	
	@Test(expected = NotFoundException.class)
	public void downloadMissing() throws Exception {
		download(UUID.randomUUID(), "missing.zip");
	}
	
	@Test
	public void delete() throws Exception {
		final UUID id = UUID.randomUUID();
		upload(id, "file-reg-upload.zip");
		assertTrue(exists(id));
		
		registry.delete(id);
		
		try {
			registry.getFile(id);
			fail("Expected exception " + NotFoundException.class.getName());
		} catch (NotFoundException e) {
			// expected
		}
	}

	private boolean exists(UUID id) {
		return registry.getFile(id).exists();
	}

	private void upload(final UUID id, final String resourceName) throws IOException {
		try (final InputStream in = Resources.getResource(FileRegistryTest.class, resourceName).openStream()) {
			registry.upload(id, in);
		}
	}
	
	private File download(final UUID id, final String resourceName) throws IOException {
		final File file = FOLDER.resolve(resourceName).toFile();
		try (final OutputStream out = new FileOutputStream(file)) {
			registry.download(id, out);
		}
		return file;
	}
	
}
