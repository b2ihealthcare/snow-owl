/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.7
 */
public class CodeSystemCreateRequestBuilder extends BaseTransactionalRequestBuilder<CodeSystemCreateRequestBuilder, String> {

	protected CodeSystemCreateRequestBuilder(final String repositoryId) {
		super(repositoryId);
	}

	private String branchPath;
	private String citation;
	private String oid;
	private String iconPath;
	private String language;
	private String link;
	private String name;
	private String repositoryUuid;
	private String shortName;
	private String terminologyId;

	public CodeSystemCreateRequestBuilder setBranchPath(String branchPath) {
		this.branchPath = branchPath;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setCitation(String citation) {
		this.citation = citation;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setOid(String oid) {
		this.oid = oid;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setIconPath(String iconPath) {
		this.iconPath = iconPath;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setLanguage(String language) {
		this.language = language;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setLink(String link) {
		this.link = link;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setName(String name) {
		this.name = name;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setRepositoryUuid(String repositoryUuid) {
		this.repositoryUuid = repositoryUuid;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setShortName(String shortName) {
		this.shortName = shortName;
		return getSelf();
	}

	public CodeSystemCreateRequestBuilder setTerminologyId(String terminologyId) {
		this.terminologyId = terminologyId;
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
		req.setRepositoryUuid(repositoryUuid);
		req.setShortName(shortName);
		req.setTerminologyId(terminologyId);

		return req;
	}

}
