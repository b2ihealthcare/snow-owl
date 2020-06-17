/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.identity.Permission;

/**
 * @since 7.7
 * @param <C>
 */
public abstract class ExportRequest<C extends ServiceProvider> extends ResourceRequest<C, Attachment> implements AccessControl {

	private static final long serialVersionUID = 1L;

	@Override
	public final Attachment execute(C context) {
		File result = null;
		try {
			result = doExport(context);
			return context.service(AttachmentRegistry.class).upload(result);
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
	 * @return
	 * @throws IOException
	 */
	protected abstract File doExport(C context) throws IOException;

	@Override
	public String getOperation() {
		return Permission.OPERATION_EXPORT;
	}
	
}
