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
package com.b2international.snowowl.snomed.datastore.server.internal.refset;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.util.ApiEventHandler;
import com.b2international.snowowl.core.events.util.Handler;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.datastore.server.events.SnomedRefSetAction;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
public final class SnomedReferenceSetEventHandler extends ApiEventHandler {

	private final ServiceProvider serviceProvider;

	public SnomedReferenceSetEventHandler(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	@Handler
	public Object handle(SnomedRefSetAction<?> read) {
		return read.execute(newContext(read.getBranchPath()));
	}

	protected RepositoryContext newContext(String branchPath) {
		final IBranchPath branch = BranchPathUtils.createPath(branchPath);
		return new RepositoryContext() {

			@Override
			public <T> T service(Class<T> type) {
				return serviceProvider.service(type);
			}

			@Override
			public <T> Provider<T> provider(Class<T> type) {
				return serviceProvider.provider(type);
			}

			@Override
			public IBranchPath branch() {
				return branch;
			}
		};
	}

}
