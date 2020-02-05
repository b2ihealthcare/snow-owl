/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import java.util.UUID;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.BranchRequest;
import com.b2international.snowowl.datastore.request.RepositoryRequest;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;

/**
 * @since 6.0.0
 */
public final class SnomedRf2ImportRequestBuilder 
		extends BaseRequestBuilder<SnomedRf2ImportRequestBuilder, BranchContext, Rf2ImportResponse> 
		implements RevisionIndexRequestBuilder<Rf2ImportResponse> {

	private String userId;
	private UUID rf2ArchiveId;
	private Rf2ReleaseType releaseType;
	private String codeSystemShortName;
	private boolean createVersions;
	
	SnomedRf2ImportRequestBuilder() {
	}
	
	public SnomedRf2ImportRequestBuilder setUserId(String userId) {
		this.userId = userId;
		return getSelf();
	}
	
	public SnomedRf2ImportRequestBuilder setRf2ArchiveId(UUID rf2ArchiveId) {
		this.rf2ArchiveId = rf2ArchiveId;
		return getSelf();
	}

	public SnomedRf2ImportRequestBuilder setReleaseType(Rf2ReleaseType releaseType) {
		this.releaseType = releaseType;
		return getSelf();
	}
	
	public SnomedRf2ImportRequestBuilder setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
		return getSelf();
	}
	
	public SnomedRf2ImportRequestBuilder setCreateVersions(boolean createVersions) {
		this.createVersions = createVersions;
		return getSelf();
	}
	
	@Override
	protected Request<BranchContext, Rf2ImportResponse> doBuild() {
		final SnomedRf2ImportRequest req = new SnomedRf2ImportRequest(rf2ArchiveId);
		req.setReleaseType(releaseType);
		req.setCreateVersions(createVersions);
		req.setUserId(userId);
		req.setCodeSystemShortName(codeSystemShortName);
		return req;
	}
	
	@Override
	public AsyncRequest<Rf2ImportResponse> build(String repositoryId, String branch) {
		return new AsyncRequest<>(
			new RepositoryRequest<>(repositoryId,
				new BranchRequest<>(branch, 
					new RevisionIndexReadRequest<>(build(), false)
				)
			)
		);
	}

}
