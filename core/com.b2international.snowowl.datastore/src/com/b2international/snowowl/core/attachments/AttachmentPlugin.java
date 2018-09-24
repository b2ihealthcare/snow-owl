/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcUtil;

/**
 * @since 7.0
 */
@Component
public final class AttachmentPlugin extends Plugin {

	private static final String ATTACHMENTS_FOLDER = "attachments";

	@Override
	public void preRun(SnowOwlConfiguration configuration, Environment env) throws Exception {
		// register service proxy in remote mode
		if (!env.isEmbedded()) {
			env.services().registerService(AttachmentRegistry.class, RpcUtil.createProxy(env.container(), AttachmentRegistry.class));
		}
		if (env.isServer() || env.isEmbedded()) {
			env.services().registerService(AttachmentRegistry.class, new DefaultAttachmentRegistry(env.getDataPath().resolve(ATTACHMENTS_FOLDER)));
			final RpcSession session = RpcUtil.getInitialServerSession(env.container());
			session.registerClassLoader(AttachmentRegistry.class, DefaultAttachmentRegistry.class.getClassLoader());
		}
	}
	
}
