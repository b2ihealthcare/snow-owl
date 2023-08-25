/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Map;
import java.util.Optional;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.id.IDs;
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
	
	@JsonProperty
	private Map<String, Object> settings;
	
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
	
	protected final Map<String, Object> getSettings() {
		return settings;
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

	protected final void setSettings(Map<String, Object> settings) {
		this.settings = settings;
	}
	
	@Override
	public final String execute(TransactionContext context) {
		// prevent creating a resource with the -1 default ROOT ID
		if (IComponent.ROOT_ID.equals(id)) {
			throw new ConflictException("Special '-1' identifier is being used by the Root Bundle.");
		}
		
		// validate ID before use, IDs sometimes being used as branch paths, so must be a valid branch path
		IDs.checkBase64(id, getClass().getSimpleName().replace("CreateRequest", ".id"));
			
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
		
		// Validate settings
		if (settings != null) {
			final Optional<String> nullValueProperty = settings.entrySet()
				.stream()
				.filter(e -> e.getValue() == null)
				.map(e -> e.getKey())
				.findFirst();
			
			nullValueProperty.ifPresent(key -> {
				throw new BadRequestException("Setting value for key '%s' is null.", key);	
			});
		}
		
		final List<String> collectionAncestorIds = checkParentCollection(context);
		
		preExecute(context);
		
		context.add(createResourceDocument(context, collectionAncestorIds));
		return id;
	}

	private List<String> checkParentCollection(TransactionContext context) {
		if (IComponent.ROOT_ID.equals(bundleId)) {
			// "-1" is the only key that will show up both as the parent and as an ancestor
			return List.of(IComponent.ROOT_ID);
		} else {
			final Resources bundles = ResourceRequests.prepareSearchCollections()
				.filterById(bundleId)
				.one()
				.build()
				.execute(context);
			
			if (bundles.getTotal() == 0) {
				throw new NotFoundException("Bundle parent", bundleId).toBadRequestException();
			}
	
			final Resource parentCollection = bundles.first().get();
			
			checkParentCollection(context, parentCollection);
			
			return parentCollection.getResourcePathSegments();
		}
	}
	
	/**
	 * Subclasses may optionally check whether the given selected parent collection (Bundle or Terminology Resource Collection) is allowed to be used
	 * or not for the to be created resource. By default this method does nothing.
	 * 
	 * @param context
	 * @param parentCollection
	 */
	protected void checkParentCollection(TransactionContext context, Resource parentCollection) {
		
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
	@OverridingMethodsMustInvokeSuper
	protected ResourceDocument.Builder completeResource(final ResourceDocument.Builder builder) {
		return builder;
	}
	
	private ResourceDocument createResourceDocument(TransactionContext context, List<String> collectionAncestorIds) {
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
				.owner(Optional.ofNullable(owner).orElseGet(() -> context.service(User.class).getUserId()))
				.contact(contact)
				.usage(usage)
				.purpose(purpose)
				// explicitly set all resources created by this request to visible
				.hidden(false)
				.bundleAncestorIds(collectionAncestorIds)
				.bundleId(bundleId)
				.settings(settings == null ? Map.of() : settings);
		
		completeResource(builder);
		
		return builder.build();
	}
}
