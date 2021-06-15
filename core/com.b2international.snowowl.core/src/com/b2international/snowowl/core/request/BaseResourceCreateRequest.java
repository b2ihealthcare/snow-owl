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

/**
 * @since 8.0
 */
public abstract class BaseResourceCreateRequest implements Request<TransactionContext, String> {

	protected static final long serialVersionUID = 1L;

	// the new ID, if not specified, it will be auto-generated
	@NotEmpty
	String id;
	
	@NotEmpty
	String url;
	
	@NotEmpty
	String title;
	
	String language;
	String description;
	String status;
	String copyright;
	String owner;
	String contact;
	String usage;
	String purpose;
	
	@NotEmpty
	String bundleId;
	
	@Override
	public final String execute(TransactionContext context) {
		executeAdditionalLogic(context);
		context.add(createResourceDocument());
		return id;
	}
	
	public final String getId() {
		return id;
	}
	
	public final String getTitle() {
		return title;
	}
	
	/**
	 * Subclasses may override this method to add extra logic to request execution.
	 */
	protected void executeAdditionalLogic(final TransactionContext context) { }

	protected abstract ResourceDocument.Builder setSpecializedFields(final ResourceDocument.Builder builder);
	
	private ResourceDocument createResourceDocument() {
		final Builder builder = ResourceDocument.builder();
				
		return setSpecializedFields(builder)
				.id(id)
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
				.bundleId(bundleId)
				.build();
	}
}
