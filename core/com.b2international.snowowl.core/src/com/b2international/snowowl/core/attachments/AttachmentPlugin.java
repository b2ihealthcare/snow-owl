/*
 * Copyright 2018-2022 B2i Healthcare, https://b2ihealthcare.com
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

import java.nio.file.Path;

import com.b2international.snowowl.core.client.TransportConfiguration;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * @since 7.0
 */
@Component
public final class AttachmentPlugin extends Plugin {

	private static final String ATTACHMENTS_FOLDER = "attachments";

	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
		final IEventBus bus = env.service(IEventBus.class);
		
		if (env.isServer()) {
			final Path attachmentsPath = env.getDataPath().resolve(ATTACHMENTS_FOLDER);
			final DefaultAttachmentRegistry attachmentRegistry = new DefaultAttachmentRegistry(attachmentsPath);
			attachmentRegistry.register(bus);
			env.services().registerService(AttachmentRegistry.class, attachmentRegistry);
		} else {
			
			TransportConfiguration transportConfig = env.service(SnowOwlConfiguration.class).getModuleConfig(TransportConfiguration.class);
			
			env.services().addServiceListener(IEventBus.class, (oldBus, newBus) -> {
				final AttachmentRegistryClient attachmentRegistryClient = new AttachmentRegistryClient(newBus, transportConfig.getUploadChunkSize(), transportConfig.getDownloadChunkSize());
				env.services().registerService(AttachmentRegistry.class, attachmentRegistryClient);
			});
			
		}
	}
}
