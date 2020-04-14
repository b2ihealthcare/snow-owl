/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import java.util.List;
import java.util.Map;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.TransactionalRequestBuilder;

/**
 * @since 4.7
 */
public final class CodeSystemUpdateRequestBuilder extends BaseRequestBuilder<CodeSystemUpdateRequestBuilder, TransactionContext, Boolean> implements TransactionalRequestBuilder<Boolean> {

	private final String uniqueId;

	private String name;
	private String link;
	private String language;
	private String citation;
	private String branchPath;
	private String iconPath;
	private List<ExtendedLocale> locales;
	private Map<String, Object> additionalProperties;

	CodeSystemUpdateRequestBuilder(final String uniqueId) {
		super();
		this.uniqueId = uniqueId;
	}

	public CodeSystemUpdateRequestBuilder setName(String name) {
		this.name = name;
		return getSelf();
	}

	public CodeSystemUpdateRequestBuilder setLink(String link) {
		this.link = link;
		return getSelf();
	}

	public CodeSystemUpdateRequestBuilder setLanguage(String language) {
		this.language = language;
		return getSelf();
	}

	public CodeSystemUpdateRequestBuilder setCitation(String citation) {
		this.citation = citation;
		return getSelf();
	}

	public CodeSystemUpdateRequestBuilder setBranchPath(String branchPath) {
		this.branchPath = branchPath;
		return getSelf();
	}

	public CodeSystemUpdateRequestBuilder setIconPath(String iconPath) {
		this.iconPath = iconPath;
		return getSelf();
	}
	
	public CodeSystemUpdateRequestBuilder setLocales(List<ExtendedLocale> locales) {
		this.locales = locales;
		return getSelf();
	}
	
	public CodeSystemUpdateRequestBuilder setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
		return getSelf();
	}

	@Override
	protected Request<TransactionContext, Boolean> doBuild() {
		final CodeSystemUpdateRequest req = new CodeSystemUpdateRequest(uniqueId);
		req.setName(name);
		req.setLink(link);
		req.setLanguage(language);
		req.setCitation(citation);
		req.setBranchPath(branchPath);
		req.setIconPath(iconPath);
		req.setLocales(locales);
		req.setAdditionalProperties(additionalProperties);
		return req;
	}

}
