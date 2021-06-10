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

/**
 * @since 8.0
 */
public abstract class BaseResourceCreateRequest implements Request<TransactionContext, String>  {

	protected static final long serialVersionUID = 1L;

	// the new codesystem's ID, if not specified, it will be auto-generated
	@NotEmpty
	protected String id;
	
	@NotEmpty
	protected String url;
	
	@NotEmpty
	protected String title;
	
	protected String language;
	protected String description;
	protected String status;
	protected String copyright;
	protected String owner;
	protected String contact;
	protected String usage;
	protected String purpose;
	
	@NotEmpty
	protected String bundleId;
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void setContact(String contact) {
		this.contact = contact;
	}
	
	public void setUsage(String usage) {
		this.usage = usage;
	}
	
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}
}
