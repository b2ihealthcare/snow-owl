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

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 8.0
 */
public abstract class BaseResourceUpdateRequest extends UpdateRequest<TransactionContext> {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	private String url;
	
	@JsonProperty
	private String title;
	
	@JsonProperty
	private String language;
	
	@JsonProperty
	private String description;
	
	@JsonProperty
	private String status;
	
	@JsonProperty
	private String copyright;
	
	@JsonProperty
	private String owner;
	
	@JsonProperty
	private String contact;
	
	@JsonProperty
	private String usage;
	
	@JsonProperty
	private String purpose;

	@JsonProperty
	private String bundleId;
	
	protected final void setUrl(String url) {
		this.url = url;
	}
	
	protected final void setTitle(String title) {
		this.title = title;
	}
	
	protected final void setLanguage(String language) {
		this.language = language;
	}
	
	protected final void setDescription(String description) {
		this.description = description;
	}

	protected final void setStatus(String status) {
		this.status = status;
	}
	
	protected final void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	
	protected final void setOwner(String owner) {
		this.owner = owner;
	}
	
	protected final void setContact(String contact) {
		this.contact = contact;
	}
	
	protected final void setUsage(String usage) {
		this.usage = usage;
	}
	
	protected final void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	protected final void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}
	
	protected BaseResourceUpdateRequest(String componentId) {
		super(componentId);
	}

	@Override
	public final Boolean execute(TransactionContext context) {
		final ResourceDocument resource = context.lookup(componentId(), ResourceDocument.class);
		final ResourceDocument.Builder updated = ResourceDocument.builder(resource);

		boolean changed = false;

		changed |= updateSpecializedProperties(context, resource, updated);

		// url checked against all resources
		if (url != null && !url.equals(resource.getUrl())) {
			if (url.isBlank()) {
				throw new BadRequestException("Resource.url should not be empty string");
			}
			
			final boolean existingUrl = ResourceRequests.prepareSearch()
				.setLimit(0)
				.filterByUrl(url)
				.build()
				.execute(context)
				.getTotal() > 0;
					
			if (existingUrl) {
				throw new AlreadyExistsException("Resource", ResourceDocument.Fields.URL, url);
			}
			
			changed |= updateProperty(url, resource::getUrl, updated::url);
		}

		changed |= updateBundle(context, resource.getBundleId(), updated);
		
		changed |= updateProperty(title, resource::getTitle, updated::title);
		changed |= updateProperty(language, resource::getLanguage, updated::language);
		changed |= updateProperty(description, resource::getDescription, updated::description);
		changed |= updateProperty(status, resource::getStatus, updated::status);
		changed |= updateProperty(copyright, resource::getCopyright, updated::copyright);
		changed |= updateProperty(owner, resource::getOwner, updated::owner);
		changed |= updateProperty(contact, resource::getContact, updated::contact);
		changed |= updateProperty(usage, resource::getUsage, updated::usage);
		changed |= updateProperty(purpose, resource::getPurpose, updated::purpose);

		if (changed) {
			context.add(updated.build());
		}

		return changed;
	}

	private boolean updateBundle(TransactionContext context, String oldBundleId, Builder updated) {
		if (bundleId == null || bundleId.equals(oldBundleId)) {
			return false;
		}
		
		if (IComponent.ROOT_ID.equals(bundleId)) {
			updated.bundleId(bundleId);
			return true;
		}
		
		boolean bundleExist = ResourceRequests.bundles().prepareSearch()
				.filterById(bundleId)
				.setLimit(0)
				.build()
				.execute(context)
				.getTotal() > 0;
				
		if (bundleExist) {
			updated.bundleId(bundleId);
			return true;
		} else {
			throw new NotFoundException("Bundle", bundleId).toBadRequestException();
		}
	}

	protected abstract boolean updateSpecializedProperties(TransactionContext context, ResourceDocument resource, Builder updated);
}
