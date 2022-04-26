/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.UUID;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.attachments.request.AttachmentRequests;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashingInputStream;
import com.google.common.hash.HashingOutputStream;

/**
 * @since 8.1.0
 */
public final class AttachmentRegistryClient implements AttachmentRegistry {
	
	private final IEventBus bus;
	private final int uploadChunkSize;
	private final int downloadChunkSize;

	public AttachmentRegistryClient(IEventBus bus, int uploadChunkSize, int downloadChunkSize) {
		this.bus = bus;
		this.uploadChunkSize = uploadChunkSize;
		this.downloadChunkSize = downloadChunkSize;
	}

	@Override
	public void upload(UUID id, InputStream in) throws AlreadyExistsException, BadRequestException {
		final HashingInputStream hin = new HashingInputStream(HASH_ALGORITHM, in);
		
		AttachmentRequests.prepareStartUpload()
			.setId(id)
			.buildAsync()
			.execute(bus)
			.thenWith(attachmentId -> sendNextChunk(attachmentId, hin))
			.getSync();
	}

	private Promise<Boolean> sendNextChunk(UUID attachmentId, HashingInputStream hin) {
		try {
			
			final byte[] chunk = hin.readNBytes(uploadChunkSize);
			
			if (chunk.length > 0) {
				
				return AttachmentRequests.prepareUploadChunk()
					.setAttachmentId(attachmentId)
					.setChunk(chunk)
					.buildAsync()
					.execute(bus)
					.thenWith(unused -> sendNextChunk(attachmentId, hin));
				
			} else {
				
				final HashCode expectedHashCode = hin.hash();
				return AttachmentRequests.prepareCompleteUpload()
					.setAttachmentId(attachmentId)
					.setExpectedHashCode(expectedHashCode)
					.buildAsync()
					.execute(bus);
			}

		} catch (IOException e) {
			return Promise.fail(e);
		}
	}

	@Override
	public void download(UUID id, OutputStream out) throws NotFoundException {
		final HashingOutputStream hos = new HashingOutputStream(HASH_ALGORITHM, out);
		
		AttachmentRequests.prepareStartDownload()
			.setId(id)
			.buildAsync()
			.execute(bus)
			.thenWith(unused -> receiveNextChunk(id, hos))
			.getSync();
	}

	private Promise<Boolean> receiveNextChunk(UUID attachmentId, HashingOutputStream hos) {

		return AttachmentRequests.prepareDownloadChunk()
			.setAttachmentId(attachmentId)
			.setChunkSize(downloadChunkSize)
			.buildAsync()
			.execute(bus)
			.thenWith(chunk -> {
				if (chunk.length > 0) {
					try {
						hos.write(chunk);
					} catch (IOException e) {
						Promise.fail(e);
					}
					
					return receiveNextChunk(attachmentId, hos);
				} else {
					
					final HashCode expectedHashCode = hos.hash();
					
					return AttachmentRequests.prepareCompleteDownload()
						.setAttachmentId(attachmentId)
						.setExpectedHashCode(expectedHashCode)
						.buildAsync()
						.execute(bus);
				}
			});
		
	}

	@Override
	public void delete(UUID id) {
		AttachmentRequests.prepareDelete(id)
			.buildAsync()
			.execute(bus)
			.getSync();
	}
}
