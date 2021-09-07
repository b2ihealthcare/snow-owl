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

import java.util.List;
import java.util.Optional;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.revision.RevisionBranch.BranchNameValidator;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.bundle.Bundles;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
import com.b2international.snowowl.core.uri.ResourceURLSchemaSupport;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 8.0
 */
public abstract class BaseResourceCreateRequest implements Request<TransactionContext, String> {
	
	protected static final long serialVersionUID = 1L;

	// the new ID, if not specified, it will be auto-generated
	@JsonProperty
	@NotEmpty
	private String id;
	
	@JsonProperty
	@NotEmpty
	private String url;
	
	@JsonProperty
	@NotEmpty
	private String title;
	
	@JsonProperty
	@NotEmpty
	private String bundleId;
	
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
	
	protected final String getId() {
		return id;
	}
	
	protected final String getUrl() {
		return url;
	}
	
	protected final String getTitle() {
		return title;
	}
	
	protected final String getBundleId() {
		return bundleId;
	}
	
	protected final String getLanguage() {
		return language;
	}
	
	protected final String getDescription() {
		return description;
	}
	
	protected final String getStatus() {
		return status;
	}
	
	protected final String getCopyright() {
		return copyright;
	}
	
	protected final String getOwner() {
		return owner;
	}
	
	protected final String getContact() {
		return contact;
	}
	
	protected final String getUsage() {
		return usage;
	}
	
	protected final String getPurpose() {
		return purpose;
	}
	
	protected final void setId(String id) {
		this.id = id;
	}
	
	protected final void setUrl(String url) {
		this.url = url;
	}
	
	protected final void setTitle(String title) {
		this.title = title;
	}
	
	protected final void setBundleId(String bundleId) {
		this.bundleId = bundleId;
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
	
	@Override
	public final String execute(TransactionContext context) {
		// validate ID before use, IDs sometimes being used as branch paths, so must be a valid branch path
		try {
			BranchNameValidator.DEFAULT.checkName(id);
		} catch (BadRequestException e) {
			throw new BadRequestException(e.getMessage().replace("Branch name", getClass().getSimpleName().replace("CreateRequest", ".id")));
		}
		
		// validate URL format
		getResourceURLSchemaSupport(context).validate(url);
		
		// id checked against all resources
		final boolean existingId = ResourceRequests.prepareSearch()
			.setLimit(0)
			.filterById(getId())
			.build()
			.execute(context)
			.getTotal() > 0;
			
		if (existingId) {
			throw new AlreadyExistsException("Resource", getId());
		}
		
		// url checked against all resources
		final boolean existingUrl = ResourceRequests.prepareSearch()
			.setLimit(0)
			.filterByUrl(getUrl())
			.build()
			.execute(context)
			.getTotal() > 0;
			
		if (existingUrl) {
			throw new AlreadyExistsException("Resource", ResourceDocument.Fields.URL, getUrl());
		}
		
		preExecute(context);
		
		final List<String> bundleAncestorIds;
		if (IComponent.ROOT_ID.equals(bundleId)) {
			// "-1" is the only key that will show up both as the parent and as an ancestor
			bundleAncestorIds = List.of(IComponent.ROOT_ID);
		} else {
			final Bundles bundles = ResourceRequests.bundles()
				.prepareSearch()
				.filterById(bundleId)
				.one()
				.build()
				.execute(context);
			
			if (bundles.getTotal() == 0) {
				throw new NotFoundException("Bundle parent", bundleId).toBadRequestException();
			}
	
			final Bundle bundleParent = bundles.first().get();
			bundleAncestorIds = bundleParent.getBundleAncestorIdsForChild();
		}
		
		context.add(createResourceDocument(context, bundleAncestorIds));
		return id;
	}
	
	@JsonIgnore
	protected abstract String getResourceType();
	
	/**
	 * Subclasses may override to provide their own URL Schema support implementation for validation purposes.
	 * 
	 * @param context
	 * @return
	 */
	protected ResourceURLSchemaSupport getResourceURLSchemaSupport(ServiceProvider context) {
		return ResourceURLSchemaSupport.DEFAULT;
	}

	/**
	 * Subclasses may override this method to perform validation checks and/or attach additional resources to the transaction before creating the main resource.
	 */
	protected void preExecute(final TransactionContext context) { }

	/**
	 * Subclasses may configure the new resource before persisting.
	 */
	protected ResourceDocument.Builder completeResource(final ResourceDocument.Builder builder) {
		return builder;
	}
	
	private ResourceDocument createResourceDocument(TransactionContext context, List<String> bundleAncestorIds) {
		final Builder builder = ResourceDocument.builder()
				.resourceType(getResourceType())
				.id(id)
				.url(url)
				.title(title)
				.language(language)
				.description(description)
				// resources start their lifecycle in draft mode
				.status(status == null ? "draft" : status)
				.copyright(copyright)
				.owner(Optional.ofNullable(owner).orElseGet(() -> context.service(User.class).getUsername()))
				.contact(contact)
				.usage(usage)
				.purpose(purpose)
				.bundleAncestorIds(bundleAncestorIds)
				.bundleId(bundleId);
		
		completeResource(builder);
		
		return builder.build();
	}
}
