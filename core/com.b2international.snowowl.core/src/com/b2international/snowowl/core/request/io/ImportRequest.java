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
package com.b2international.snowowl.core.request.io;

import java.io.File;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.constraints.NotNull;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.commons.exceptions.ApiException;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.attachments.InternalAttachmentRegistry;
import com.b2international.snowowl.core.authorization.BranchAccessControl;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.request.LockRequest;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.google.common.collect.Sets;

/**
 * @since 7.12
 */
public abstract class ImportRequest extends LockRequest<TransactionContext, ImportResponse> implements BranchAccessControl {
	
	private static final long serialVersionUID = 1L;
	
	@NotNull
	private Attachment attachment;
	
	public ImportRequest() {
		super(DatastoreLockContextDescriptions.IMPORT);
	}
	
	public ImportRequest(String lockContext, String parentLockContext) {
		super(lockContext, parentLockContext);
	}
	
	final void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}
	
	protected Attachment attachment() {
		return attachment;
	}
	
	@Override
	public final ImportResponse doExecute(TransactionContext context) {
		context.log().info("Importing components from source file '%s'.", this.attachment.getFileName());
		try {
			InternalAttachmentRegistry iar = (InternalAttachmentRegistry) context.service(AttachmentRegistry.class);
			File attachment = iar.getAttachment(this.attachment.getAttachmentId());
			
			ImportDefectAcceptor defectsAcceptor = new ImportDefectAcceptor(this.attachment.getFileName());
			doValidate(context, attachment, defectsAcceptor, context.service(IProgressMonitor.class));
			
			final ImportResponse validationResponse = ImportResponse.defects(defectsAcceptor.getDefects());
			if (!validationResponse.getErrors().isEmpty()) {
				return validationResponse;
			} else {
				final Set<ComponentURI> visitedComponents = Sets.newHashSet();
				doImport(context, attachment, visitedComponents::add, context.service(IProgressMonitor.class));
				return ImportResponse.success(visitedComponents, validationResponse.getDefects());
			}
			
		} catch (ApiException e) {
			throw e;
		} catch (Exception e) {
			String error = "Unexpected error happened during the import of the source file: " + attachment.getFileName();
			context.log().error(error, e);
			return ImportResponse.error(error);
		} finally {
			context.log().info("Finished importing components from source file '%s'.", this.attachment.getFileName());
		}
	}

	/**
	 * Subclasses optionally provider validation functionality to verify the integrity of the attachment before proceeding to the actual import in {@link #doImport(File, IProgressMonitor)}.
	 * 
	 * @param context - the context to run the validation on
	 * @param attachment - the file attachment to validate
	 * @param defectsAcceptor - the acceptor that collects {@link ImportDefect}s through a few helper methods
	 * @param monitor - the monitor that can be used to track progress
	 * @throws Exception
	 */
	protected void doValidate(TransactionContext context, File attachment, ImportDefectAcceptor defectsAcceptor, IProgressMonitor monitor) throws Exception {
	}

	/**
	 * Performs the import from the given file attachment.
	 * 
	 * @param context - the context that can be used to commit changes
	 * @param attachment - the attachment file to import
	 * @param visitor - visitor that accepts visited component URIs
	 * @param monitor - the monitor that can be used to track progress
	 * @throws Exception
	 */
	protected abstract void doImport(TransactionContext context, File attachment, Consumer<ComponentURI> visitor, IProgressMonitor monitor) throws Exception;
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_IMPORT;
	}
	
}
