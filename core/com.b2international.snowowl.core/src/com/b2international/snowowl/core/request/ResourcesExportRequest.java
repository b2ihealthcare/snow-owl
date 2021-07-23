/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 8.0
 */
public abstract class ResourcesExportRequest implements Request<ServiceProvider, Attachment> {

	private static final long serialVersionUID = 1L;
	
	private Set<String> resourceIds;

	void setResourceIds(Set<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

	@Override
	public final Attachment execute(ServiceProvider context) {
		File result = null;
		try {
			result = doExport(context, resourceIds);
			return Attachment.upload(context, result.toPath()); 
		} catch (IOException e) {
			throw new SnowowlRuntimeException(String.format("Failed to perform '%s'", getClass().getSimpleName()), e);
		} finally {
			if (result != null) {
				result.delete();
			}
		}
	}

	/**
	 * Performs the content export from the given context and returns the export result as a file.
	 * 
	 * @param context
	 * @param resourceIds
	 * @return
	 * @throws IOException
	 */
	protected abstract File doExport(ServiceProvider context, Set<String> resourceIds2) throws IOException;
	
}
