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
package com.b2international.snowowl.core.attachments.request;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.attachments.InternalAttachmentRegistry;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.RequestHeaders;
import com.b2international.snowowl.eventbus.netty.EventBusNettyUtil;
import com.google.common.hash.HashCode;

/*package*/ class CompleteDownloadRequest implements Request<ServiceProvider, Boolean> {

	@NotNull
	private final UUID attachmentId;
	
	private final HashCode expectedHashCode;

	/*package*/ CompleteDownloadRequest(final UUID attachmentId, final HashCode expectedHashCode) {
		this.attachmentId = attachmentId;
		this.expectedHashCode = expectedHashCode;
	}

	@Override
	public Boolean execute(final ServiceProvider context) {
		final InternalAttachmentRegistry service = (InternalAttachmentRegistry) context.service(AttachmentRegistry.class);
		final RequestHeaders requestHeaders = context.service(RequestHeaders.class);
		final String clientId = requestHeaders.header(EventBusNettyUtil.HEADER_CLIENT_ID);
		
		service.completeDownload(clientId, attachmentId, expectedHashCode);
		return Boolean.TRUE;
	}
}
