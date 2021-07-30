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
package com.b2international.snowowl.core.request.io;

import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.b2international.commons.exceptions.ApiException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.attachments.InternalAttachmentRegistry;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

/**
 * @since 8.0
 */
public abstract class ResourcesImportRequest<R extends Resource> implements Request<ServiceProvider, ImportResponse> {

	private static final long serialVersionUID = 1L;

	@JsonProperty 
	@NotNull
	private Attachment sourceFile;
	
	void setSourceFile(Attachment sourceFile) {
		this.sourceFile = sourceFile;
	}

	protected String getFileName() {
		return sourceFile.getFileName();
	}
	
	@Override
	public ImportResponse execute(ServiceProvider context) {
		final InternalAttachmentRegistry attachmentRegistry = (InternalAttachmentRegistry) context.service(AttachmentRegistry.class);
		final File file = attachmentRegistry.getAttachment(sourceFile.getAttachmentId());
		
		try {
			
			final List<R> sourceFileContent = loadSourceFileContent(context, file);
			final Map<String, R> existingResources = newHashMap(loadExistingResources(context, sourceFileContent));
			
			final ImportDefectAcceptor defectAcceptor = new ImportDefectAcceptor(sourceFile.getFileName());
			validateSourceFileContent(context, sourceFileContent, existingResources, defectAcceptor);
			List<ImportDefect> defects = defectAcceptor.getDefects();
			
			// Content with validation errors can not be imported
			ImportResponse validationResponse = ImportResponse.defects(defects);
			if (!validationResponse.getErrors().isEmpty()) {
				return validationResponse;
			}
			
			final Set<ComponentURI> visitedComponents = Sets.newHashSet();
			
			// Import each resource present in the source file, along with its content
			for (final R resource : sourceFileContent) {
				final String id = resource.getId();
				ComponentURI importedResource = importResource(context, resource, existingResources.get(id));
				if (importedResource != null) {
					visitedComponents.add(importedResource);
				}
				visitedComponents.addAll(importContent(context, resource, existingResources.get(id)));
			}
			
			return ImportResponse.success(visitedComponents, defects);
			
		} catch (final ApiException e) {
			throw e;
		} catch (final Exception e) {
			String error = "Unexpected error happened during the import of the source file: " + sourceFile.getFileName();
			context.log().error(error, e);
			return ImportResponse.error(error);
		} finally {
			if (sourceFile != null && attachmentRegistry != null) {
				attachmentRegistry.delete(sourceFile.getAttachmentId());
			}
		}
	}
	
	protected abstract List<R> loadSourceFileContent(final ServiceProvider context, final File sourceFile) throws Exception;
	
	protected abstract Map<String, R> loadExistingResources(final ServiceProvider context, final List<R> sourceFileContent);
	
	protected void validateSourceFileContent(
			final ServiceProvider context, 
			final List<R> resourcesToImport, 
			final Map<String, R> existingResources,
			final ImportDefectAcceptor defectAcceptor) {
	}
	
	protected abstract ComponentURI importResource(
			final ServiceProvider context, 
			final R resource, 
			final R existingResource);
	
	protected abstract Set<ComponentURI> importContent(
			final ServiceProvider context, 
			final R resource,
			final R existingResource);
}
