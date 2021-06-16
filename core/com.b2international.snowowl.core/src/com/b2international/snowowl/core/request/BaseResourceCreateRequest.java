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

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
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
		preExecute(context);
		context.add(createResourceDocument());
		return id;
	}
	
	/**
	 * Subclasses may override this method to perform validation checks and/or attach additional resources to the transaction before creating the main resource.
	 */
	protected void preExecute(final TransactionContext context) { }

	/**
	 * Set the additional fields of the resource
	 */
	protected abstract ResourceDocument.Builder completeResource(final ResourceDocument.Builder builder);
	
	private ResourceDocument createResourceDocument() {
		final Builder builder = ResourceDocument.builder()
				.url(url)
				.title(title)
				.language(language)
				.description(description)
				.status(status)
				.copyright(copyright)
				.owner(owner)
				.contact(contact)
				.usage(usage)
				.purpose(purpose)
				.bundleId(bundleId);
		
		completeResource(builder);
		
		return builder.build();
	}
}
