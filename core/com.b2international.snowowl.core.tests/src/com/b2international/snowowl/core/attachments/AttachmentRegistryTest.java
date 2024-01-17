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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.client.TransportConfiguration;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.IdentityProvider;
import com.b2international.snowowl.core.repository.ApiRequestHandler;
import com.b2international.snowowl.eventbus.EventBusUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.internal.eventbus.EventBus;
import com.google.common.io.Resources;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * @since 5.7
 */
@SuppressWarnings("restriction")
public class AttachmentRegistryTest {

	private static final Path FOLDER = Paths.get("target", AttachmentRegistry.class.getSimpleName().toLowerCase());
	
	private IEventBus bus;
	private InternalAttachmentRegistry registry;

	@Before
	public void setup() {
		this.registry = new DefaultAttachmentRegistry(FOLDER);
		this.bus = EventBusUtil.getBus();
		
		final ServiceProvider context = ServiceProvider.EMPTY
			.inject()
			.bind(MeterRegistry.class, new SimpleMeterRegistry())
			.bind(IdentityProvider.class, IdentityProvider.UNPROTECTED)
			.bind(AttachmentRegistry.class, registry)
			.bind(IEventBus.class, bus)
			.build();

		for (int i = 0; i < 4; i++) {
			bus.registerHandler(Request.ADDRESS, new ApiRequestHandler(context));
		}
	}
	
	@After
	public void teardown() {
		((EventBus) bus).deactivate();
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
			registry.getAttachment(id);
			fail("Expected exception " + NotFoundException.class.getName());
		} catch (NotFoundException e) {
			// expected
		}
	}
	
	@Test
	public void uploadWithClient() throws Exception {
		final AttachmentRegistryClient client = new AttachmentRegistryClient(bus, TransportConfiguration.DEFAULT_UPLOAD_CHUNK_SIZE, TransportConfiguration.DEFAULT_DOWNLOAD_CHUNK_SIZE);

		final UUID id = UUID.randomUUID();
		upload(client, id, "file-reg-upload.zip");
		assertTrue(exists(id));
		
		// then download it and check size
		File downloaded = download(client, id, "file-client-reg-downloaded.zip");
		assertEquals(149 /*bytes*/, downloaded.length());
	}

	@Test
	public void deleteWithClient() throws Exception {
		final AttachmentRegistryClient client = new AttachmentRegistryClient(bus, TransportConfiguration.DEFAULT_UPLOAD_CHUNK_SIZE, TransportConfiguration.DEFAULT_DOWNLOAD_CHUNK_SIZE);
		
		final UUID id = UUID.randomUUID();
		upload(id, "file-reg-upload.zip");
		assertTrue(exists(id));
		
		client.delete(id);
		
		try {
			registry.getAttachment(id);
			fail("Expected exception " + NotFoundException.class.getName());
		} catch (NotFoundException e) {
			// expected
		}
	}
	
	private boolean exists(UUID id) {
		return registry.getAttachment(id).exists();
	}

	private void upload(final UUID id, final String resourceName) throws IOException {
		upload(this.registry, id, resourceName);
	}
	
	private void upload(final AttachmentRegistry registry, final UUID id, final String resourceName) throws IOException {
		try (final InputStream in = Resources.getResource(AttachmentRegistryTest.class, resourceName).openStream()) {
			registry.upload(id, in);
		}
	}
	
	private File download(final UUID id, final String resourceName) throws IOException {
		return download(this.registry, id, resourceName);
	}
	
	private File download(final AttachmentRegistry registry, final UUID id, final String resourceName) throws IOException {
		final File file = FOLDER.resolve(resourceName).toFile();
		try (final OutputStream out = new FileOutputStream(file)) {
			registry.download(id, out);
		}
		return file;
	}
}
