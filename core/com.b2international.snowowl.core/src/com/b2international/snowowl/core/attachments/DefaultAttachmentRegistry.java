/*
 * Copyright 2017-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.*;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.events.ClientConnectionNotification;
import com.b2international.snowowl.eventbus.events.SystemNotification;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.hash.HashCode;
import com.google.common.io.*;

/**
 * The default implementation of the {@link AttachmentRegistry} interface.
 * Manages attachments in a local directory defined via the constructor
 * parameter.
 * 
 * @since 5.7
 */
public final class DefaultAttachmentRegistry implements InternalAttachmentRegistry, IHandler<IMessage>, IDisposableService {

	private final Path folder;
	
	private final Table<String, UUID, CountingOutputStream> partialUploads = Tables.synchronizedTable(HashBasedTable.create());
	private final Table<String, UUID, InputStream> partialDownloads = Tables.synchronizedTable(HashBasedTable.create());
	private final AtomicBoolean active = new AtomicBoolean(false);
	
	private IEventBus bus;
	
	public DefaultAttachmentRegistry(Path folder) {
		this.folder = folder;
		this.folder.toFile().mkdirs();
	}
	
	
	public void register(final IEventBus bus) {
		if (!active.compareAndExchange(false, true)) {
			this.bus = bus;
			bus.registerHandler(SystemNotification.ADDRESS, this);
		}
	}
	
	@Override
	public void upload(UUID id, InputStream in) throws AlreadyExistsException, BadRequestException {
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
	public void download(UUID id, OutputStream out) throws NotFoundException {
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
		
		if (partialUploads.containsColumn(id)) {
			throw new BadRequestException("Attachment '%s' is incomplete", id.toString());
		}
		
		return requestedFile;
	}
	
	@Override
	public void delete(UUID id) {
		toFile(id).delete();
	}

	@Override
	public UUID startUpload(String clientId, UUID id) {
		final File file = toFile(id);
		if (file.exists()) {
			throw new AlreadyExistsException("Attachment", id.toString());
		}
		
		synchronized (partialUploads) {
			if (partialUploads.containsColumn(id)) {
				throw new AlreadyExistsException("Partial upload", id.toString());
			}
			
			try {
				final OutputStream os = java.nio.file.Files.newOutputStream(file.toPath(), StandardOpenOption.CREATE_NEW);
				final CountingOutputStream cos = new CountingOutputStream(os); // lgtm[java/output-resource-leak]
				partialUploads.put(clientId, id, cos);
				return id;
			} catch (IOException e) {
				throw new SnowowlRuntimeException("Failed to open output stream for partial upload '" + id + "'.", e);
			}
		}
	}

	@Override
	public long uploadChunk(UUID id, byte[] chunk) {
		final CountingOutputStream cos = getPartialUpload(id);
		
		synchronized (cos) {
			try {
				cos.write(chunk);
			} catch (IOException e) {
				
				try { 
					Closeables.close(cos, true); 
				} catch (IOException impossible) {
					// "swallowIOException" ensures no IOException is thrown here 
					throw new AssertionError(impossible); 
				} finally {
					delete(id);
					partialUploads.column(id).clear();
				}
				
				throw new SnowowlRuntimeException("Failed to write chunk of " + chunk.length + " bytes for partial upload '" + id + "'.", e);
			}
			
			return cos.getCount();
		}
	}
	
	@Override
	public void completeUpload(UUID id, HashCode expectedHashCode) {
		final CountingOutputStream cos = getPartialUpload(id);
		
		synchronized (cos) {
			try {
				cos.close();
			
				if (!hashCodeMatches(id, expectedHashCode)) {
					delete(id);
					throw new BadRequestException("Upload failed, hash codes did not match.");
				}
				
			} catch (IOException e) {
				delete(id);
				throw new SnowowlRuntimeException("Failed to complete partial upload of attachment '" + id + "'.", e);
			} finally {
				partialUploads.column(id).clear();
			}
		}
	}
	
	private boolean hashCodeMatches(UUID id, HashCode expectedHashCode) {
		final File file = toFile(id);
		
		try {
			
			return Files.asByteSource(file)
				.hash(HASH_ALGORITHM)
				.equals(expectedHashCode);
			
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Failed to compute hash code of attachment '" + id + "'.", e);
		}
	}
	
	@Override
	public void startDownload(String clientId, UUID id) {
		final File file = toFile(id);
		if (!file.exists()) {
			throw new NotFoundException("Attachment", id.toString());
		}
		
		synchronized (partialDownloads) {
			if (partialDownloads.contains(clientId, id)) {
				throw new BadRequestException("Download for attachment %s has already started.", id.toString());
			}

			try {
				final InputStream is = java.nio.file.Files.newInputStream(file.toPath(), StandardOpenOption.READ);
				partialDownloads.put(clientId, id, is);
			} catch (IOException e) {
				throw new SnowowlRuntimeException("Failed to open output stream for partial upload '" + id + "'.", e);
			}
		}
	}
	
	@Override
	public int downloadChunk(String clientId, UUID id, byte[] chunk, int length) {
		synchronized (partialDownloads) {
			final InputStream is = partialDownloads.get(clientId, id);
			if (is == null) {
				throw new NotFoundException("Download for attachment", id.toString());
			}
			
			synchronized (is) {
				try {
					return is.readNBytes(chunk, 0, length);
				} catch (IOException e) {
					completeDownload(clientId, id);
					throw new SnowowlRuntimeException("Failed to read input stream for download '" + id + "'.", e);
				}
			}
		}
	}

	@Override
	public void completeDownload(String clientId, UUID id, HashCode expectedHashCode) {
		completeDownload(clientId, id);
		
		if (!hashCodeMatches(id, expectedHashCode)) {
			throw new BadRequestException("Download failed, hash codes do not match.");
		}
	}
	
	private void completeDownload(String clientId, UUID id) {
		final InputStream is = partialDownloads.remove(clientId, id);
		Closeables.closeQuietly(is);
	}

	private CountingOutputStream getPartialUpload(UUID id) {
		final Collection<CountingOutputStream> partialUploadsById = partialUploads.column(id).values();
		
		if (partialUploadsById.size() < 1) {
			throw new NotFoundException("Partial upload", id.toString());
		}
		
		if (partialUploadsById.size() > 1) {
			throw new IllegalStateException("Multiple clients registered for partial upload with ID '" + id.toString() + "'.");
		}
		
		return Iterables.getOnlyElement(partialUploadsById);
	}

	private File toFile(UUID id) {
		return this.folder.resolve(id.toString()).toFile();
	}
	
	@Override
	public void handle(final IMessage message) {
		final Object body = message.body();
		if (!(body instanceof ClientConnectionNotification)) {
			return;
		}
		
		final ClientConnectionNotification notification = (ClientConnectionNotification) body;
		if (!notification.isJoining()) {
			clientLogout(notification.getClientId());
		}
	}

	private void clientLogout(String clientId) {
		synchronized (partialUploads) {
			final Map<UUID, CountingOutputStream> clientUploads = partialUploads.row(clientId);
			final Map<UUID, CountingOutputStream> clientUploadsCopy = Map.copyOf(clientUploads);
			clientUploads.clear();
			
			clientUploadsCopy.forEach((id, os) -> {
				try {
					Closeables.close(os, true);
				} catch (IOException impossible) {
					throw new AssertionError(impossible);
				} finally {
					delete(id);
				}
			});
		}
		
		synchronized (partialDownloads) {
			final Map<UUID, InputStream> clientDownloads = partialDownloads.row(clientId);
			final List<InputStream> inputStreams = List.copyOf(clientDownloads.values());
			clientDownloads.clear();
			
			inputStreams.forEach(Closeables::closeQuietly);
		}
	}

	@Override
	public void dispose() {
		if (active.compareAndExchange(true, false)) {
			this.bus.unregisterHandler(SystemNotification.ADDRESS, this);
			this.bus = null;
		}
	}

	@Override
	public boolean isDisposed() {
		return active.get();
	}
}
