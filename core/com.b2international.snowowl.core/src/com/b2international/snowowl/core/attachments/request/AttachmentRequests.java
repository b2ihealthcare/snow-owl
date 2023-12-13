/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.attachments.request;

import java.util.UUID;

/**
 * @since 8.1.0
 */
public final class AttachmentRequests {

	public static final StartUploadRequestBuilder prepareStartUpload() {
		return new StartUploadRequestBuilder();
	}
	
	public static final UploadChunkRequestBuilder prepareUploadChunk() {
		return new UploadChunkRequestBuilder();
	}
	
	public static final CompleteUploadRequestBuilder prepareCompleteUpload() {
		return new CompleteUploadRequestBuilder();
	}

	public static final StartDownloadRequestBuilder prepareStartDownload() {
		return new StartDownloadRequestBuilder();
	}
	
	public static final DownloadChunkRequestBuilder prepareDownloadChunk() {
		return new DownloadChunkRequestBuilder();
	}
	
	public static CompleteDownloadRequestBuilder prepareCompleteDownload() {
		return new CompleteDownloadRequestBuilder();
	}	
	
	public static final DeleteAttachmentRequestBuilder prepareDelete(UUID id) {
		return new DeleteAttachmentRequestBuilder().setId(id);
	}
	
	private AttachmentRequests() {
		// This class is not supposed to be instantiated
	}
}
