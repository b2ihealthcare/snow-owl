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

import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;

/**
 * @since 8.0
 */
public abstract class BaseResourceUpdateRequest extends UpdateRequest<TransactionContext> implements RepositoryAccessControl {

	private static final long serialVersionUID = 1L;

	String url;
	String title;
	String language;
	String description;
	String status;
	String copyright;
	String owner;
	String contact;
	String usage;
	String purpose;
	String bundleId;

	protected BaseResourceUpdateRequest(String componentId) {
		super(componentId);
	}

	@Override
	public final String getOperation() {
		return Permission.OPERATION_EDIT;
	}

	@Override
	public final Boolean execute(TransactionContext context) {
		final ResourceDocument resource = context.lookup(componentId(), ResourceDocument.class);
		final ResourceDocument.Builder updated = ResourceDocument.builder(resource);

		boolean changed = false;

		changed |= updateSpecializedProperties(context, resource, updated);
		
		changed |= updateProperty(url, resource::getUrl, updated::url);
		changed |= updateProperty(title, resource::getTitle, updated::title);
		changed |= updateProperty(language, resource::getLanguage, updated::language);
		changed |= updateProperty(description, resource::getDescription, updated::description);
		changed |= updateProperty(status, resource::getStatus, updated::status);
		changed |= updateProperty(copyright, resource::getCopyright, updated::copyright);
		changed |= updateProperty(owner, resource::getOwner, updated::owner);
		changed |= updateProperty(contact, resource::getContact, updated::contact);
		changed |= updateProperty(usage, resource::getUsage, updated::usage);
		changed |= updateProperty(purpose, resource::getPurpose, updated::purpose);
		changed |= updateProperty(bundleId, resource::getBundleId, updated::bundleId);

		if (changed) {
			context.add(updated.build());
		}

		return changed;
	}

	protected abstract boolean updateSpecializedProperties(TransactionContext context, ResourceDocument resource, Builder updated);
}
