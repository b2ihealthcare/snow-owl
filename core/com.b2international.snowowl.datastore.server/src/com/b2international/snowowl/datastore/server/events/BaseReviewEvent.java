/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.events;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.events.BaseEvent;

/**
 * Abstract superclass for events related to reviewing changes between branches.
 * 
 * @since 4.2
 */
public abstract class BaseReviewEvent extends BaseEvent {

	private final String repositoryId;

	protected BaseReviewEvent(final String repositoryId) {
		this.repositoryId = checkNotNull(repositoryId, "repositoryId");
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	@Override
	protected final String getAddress() {
		return "/" + repositoryId + "/reviews";
	}
}
