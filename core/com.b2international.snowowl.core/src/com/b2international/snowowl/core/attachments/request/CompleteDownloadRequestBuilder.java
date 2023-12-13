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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.google.common.hash.HashCode;

public class CompleteDownloadRequestBuilder 
	extends BaseRequestBuilder<CompleteDownloadRequestBuilder, ServiceProvider, Boolean> 
	implements SystemRequestBuilder<Boolean> {

	private UUID attachmentId;
	private HashCode expectedHashCode;

	/*package*/ CompleteDownloadRequestBuilder() { }
	
	public CompleteDownloadRequestBuilder setAttachmentId(UUID attachmentId) {
		this.attachmentId = attachmentId;
		return getSelf();
	}
	
	public CompleteDownloadRequestBuilder setExpectedHashCode(HashCode expectedHashCode) {
		this.expectedHashCode = expectedHashCode;
		return getSelf();
	}
	
	@Override
	protected CompleteDownloadRequest doBuild() {
		return new CompleteDownloadRequest(attachmentId, expectedHashCode);
	}
}
