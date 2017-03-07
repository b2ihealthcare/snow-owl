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
package com.b2international.snowowl.datastore.internal.file;

import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.DatastoreActivator;
import com.b2international.snowowl.datastore.file.FileRegistry;
import com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob;

/**
 * @since 5.7
 */
public class FileRegistryServerServiceConfigJob extends AbstractServerServiceConfigJob<FileRegistry> {

	public FileRegistryServerServiceConfigJob() {
		super("Attachment registry configuration...", DatastoreActivator.PLUGIN_ID);
	}

	@Override
	protected Class<FileRegistry> getServiceClass() {
		return FileRegistry.class;
	}

	@Override
	protected FileRegistry createServiceImplementation() throws SnowowlServiceException {
		return new DefaultFileRegistry(getEnvironment().getDataDirectory().toPath().resolve("attachments"));
	}
	
}
