/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.version;

import java.time.LocalDate;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.context.ResourceRepositoryRequestBuilder;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 5.7
 */
public final class VersionCreateRequestBuilder 
		extends BaseRequestBuilder<VersionCreateRequestBuilder, RepositoryContext, Boolean>
		implements ResourceRepositoryRequestBuilder<Boolean> {

	private String version;
	private String description;
	private LocalDate effectiveTime;
	private ResourceURI resource;
	private boolean force = false;
	private String commitComment;

	public VersionCreateRequestBuilder setResource(ResourceURI resource) {
		this.resource = resource;
		return getSelf();
	}
	
	public VersionCreateRequestBuilder setDescription(String description) {
		this.description = description;
		return getSelf();
	}
	
	/**
	 * The version effective time that the recently changed components will get during versioning. Format: yyyyMMdd.
	 * @param effectiveTime
	 * @return
	 */
	public VersionCreateRequestBuilder setEffectiveTime(String effectiveTime) {
		return setEffectiveTime(effectiveTime == null ? null : EffectiveTimes.parse(effectiveTime));
	}
	
	public VersionCreateRequestBuilder setEffectiveTime(LocalDate effectiveTime) {
		this.effectiveTime = effectiveTime;
		return getSelf();
	}
	
	public VersionCreateRequestBuilder setVersion(String version) {
		this.version = version;
		return getSelf();
	}
	
	public VersionCreateRequestBuilder setForce(boolean force) {
		this.force = force;
		return getSelf();
	}
	
	@Override
	protected Request<RepositoryContext, Boolean> doBuild() {
		final VersionCreateRequest req = new VersionCreateRequest();
		req.version = version;
		req.description= description;
		req.effectiveTime = effectiveTime;
		req.resource = resource;
		req.force = force;
		req.commitComment = commitComment;
		return req;
	}

	public String getCommitComment() {
		return commitComment;
	}

	public VersionCreateRequestBuilder setCommitComment(String commitComment) {
		this.commitComment = commitComment;
		return getSelf();
	}

}
