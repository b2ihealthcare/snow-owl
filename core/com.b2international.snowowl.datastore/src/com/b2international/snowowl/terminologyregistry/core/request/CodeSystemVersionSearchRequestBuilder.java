/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core.request;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.request.RepositoryIndexRequestBuilder;

/**
 * @since 4.7
 */
public final class CodeSystemVersionSearchRequestBuilder 
		extends SearchResourceRequestBuilder<CodeSystemVersionSearchRequestBuilder, RepositoryContext, CodeSystemVersions>
 		implements RepositoryIndexRequestBuilder<CodeSystemVersions> {

	private String codeSystemShortName;
	private String versionId;
	private Date effectiveDate;
	private String parentBranchPath;

	CodeSystemVersionSearchRequestBuilder() {
		super();
	}

	public CodeSystemVersionSearchRequestBuilder filterByCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
		return getSelf();
	}
	
	public CodeSystemVersionSearchRequestBuilder filterByVersionId(String versionId) {
		this.versionId = versionId;
		return getSelf();
	}
	
	public CodeSystemVersionSearchRequestBuilder filterByEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
		return getSelf();
	}
	
	/**
	 * Returns the code system versions that have matching branch paths
	 * @param branch path to filter by
	 */
	public CodeSystemVersionSearchRequestBuilder filterByBranchPath(String branchPath) {
		
		Path path = Paths.get(branchPath);
		if (path.getNameCount() == 0) {
			throw new IllegalArgumentException("Invalid branch path, only root element exists" + branchPath);
		}
		
		parentBranchPath = path.subpath(0, path.getNameCount() - 1).toString();
		versionId = path.getFileName().toString();
		return getSelf();
	}
	
	/**
	 * Returns the code system versions that have matching parent branch paths
	 * @param parent branch path to filter by
	 */
	public CodeSystemVersionSearchRequestBuilder filterByParentBranchPath(String parentBranchPath) {
		this.parentBranchPath = parentBranchPath;
		return getSelf();
		
	}

	@Override
	protected SearchResourceRequest<RepositoryContext, CodeSystemVersions> createSearch() {
		final CodeSystemVersionSearchRequest req = new CodeSystemVersionSearchRequest();
		req.setCodeSystemShortName(codeSystemShortName);
		req.setVersionId(versionId);
		req.setEffectiveDate(effectiveDate);
		req.setParentBranchPath(parentBranchPath);
		return req;
	}

}
