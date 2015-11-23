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
package com.b2international.snowowl.snomed.datastore.server.request;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.CommitInfo;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;

/**
 * @since 4.5
 */
final class IdRequest extends DelegatingRequest<BranchContext, BranchContext, CommitInfo> {

	private static final long serialVersionUID = 1L;

	protected IdRequest(final Request<BranchContext, CommitInfo> next) {
		super(next);
	}

	@Override
	public CommitInfo execute(BranchContext context) {
		final ISnomedIdentifierService identifierService = context.service(ISnomedIdentifierService.class);
		final SnomedIdentifiers snomedIdentifiers = new SnomedIdentifiers(identifierService);
		final IdContext idContext = new IdContext(context, snomedIdentifiers);

		try {
			final CommitInfo commitInfo = next(idContext);

			snomedIdentifiers.commit();
			return commitInfo;
		} catch (Exception e) {
			// TODO check exception type and decide what to do (e.g. rollback ID
			// request or not)
			snomedIdentifiers.rollback();

			throw e;
		}
	}

}
