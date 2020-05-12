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
import com.b2international.snowowl.core.uri.CodeSystemURI;

/**
 * @since 4.7
 */
public final class CodeSystemCreateRequestBuilder extends BaseRequestBuilder<CodeSystemCreateRequestBuilder, TransactionContext, String> implements TransactionalRequestBuilder<String> {

	private String branchPath;
	private String citation;
	private String oid;
	private String iconPath;
	private String language;
	private String link;
	private String name;
	private String repositoryId;
	private String shortName;
	private String terminologyId;
	private CodeSystemURI extensionOf;
	private List<ExtendedLocale> locales;
	private Map<String, Object> additionalProperties;

	CodeSystemCreateRequestBuilder() {}

	public CodeSystemCreateRequestBuilder setBranchPath(final String branchPath) {
		this.branchPath = branchPath;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setCitation(final String citation) {
		this.citation = citation;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setOid(final String oid) {
		this.oid = oid;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setIconPath(final String iconPath) {
		this.iconPath = iconPath;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setLanguage(final String language) {
		this.language = language;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setLink(final String link) {
		this.link = link;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setName(final String name) {
		this.name = name;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setRepositoryId(final String repositoryId) {
		this.repositoryId = repositoryId;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setShortName(final String shortName) {
		this.shortName = shortName;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setTerminologyId(final String terminologyId) {
		this.terminologyId = terminologyId;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setExtensionOf(final CodeSystemURI extensionOf) {
		this.extensionOf = extensionOf;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setLocales(final List<ExtendedLocale> locales) {
		this.locales = locales;
		return getSelf();
	}
	
	public CodeSystemCreateRequestBuilder setAdditionalProperties(final Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
		return getSelf();
	}

	@Override
	protected Request<TransactionContext, String> doBuild() {
		final CodeSystemCreateRequest req = new CodeSystemCreateRequest();
		req.setBranchPath(branchPath);
		req.setCitation(citation);
		req.setIconPath(iconPath);
		req.setLanguage(language);
		req.setLink(link);
		req.setName(name);
		req.setOid(oid);
		req.setRepositoryId(repositoryId);
		req.setShortName(shortName);
		req.setTerminologyId(terminologyId);
		req.setExtensionOf(extensionOf);
		req.setLocales(locales);
		req.setAdditionalProperties(additionalProperties);
		return req;
	}
}
