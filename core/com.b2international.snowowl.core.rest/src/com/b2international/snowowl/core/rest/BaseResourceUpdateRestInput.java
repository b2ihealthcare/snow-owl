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
package com.b2international.snowowl.core.rest;

/**
 * @since 8.0
 */
public class BaseResourceUpdateRestInput {
	
	private String bundleId;
	private String url;
	private String title;
	
	private String language;
	private String description;
	private String status;
	private String copyright;
	private String owner;
	private String contact;
	private String usage;
	private String purpose;
	
	public final void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public final String getBundleId() {
		return bundleId;
	}
	
	public final void setUrl(String url) {
		this.url = url;
	}
	
	public final String getUrl() {
		return url;
	}
	
	public final void setTitle(String title) {
		this.title = title;
	}
	
	public final String getTitle() {
		return title;
	}
	
	public final void setLanguage(String language) {
		this.language = language;
	}
	
	public final String getLanguage() {
		return language;
	}
	
	public final void setDescription(String description) {
		this.description = description;
	}
	
	public final String getDescription() {
		return description;
	}
	
	public final void setStatus(String status) {
		this.status = status;
	}
	
	public final String getStatus() {
		return status;
	}
	
	public final void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	
	public final String getCopyright() {
		return copyright;
	}
	
	public final void setOwner(String owner) {
		this.owner = owner;
	}
	
	public final String getOwner() {
		return owner;
	}
	
	public final void setContact(String contact) {
		this.contact = contact;
	}
	
	public final String getContact() {
		return contact;
	}
	
	public final void setUsage(String usage) {
		this.usage = usage;
	}
	
	public final String getUsage() {
		return usage;
	}
	
	public final void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	public final String getPurpose() {
		return purpose;
	}
}
