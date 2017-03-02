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
package com.b2international.snowowl.snomed.api.rest.request;

import java.util.Map;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.request.DeleteRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberUpdateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 4.5
 */
public class RefSetMemberRequestResolver implements RequestResolver<TransactionContext> {

	@Override
	public Request<TransactionContext, ?> resolve(String action, Map<String, Object> source) {
		switch (Action.get(action)) {
		case CREATE: return SnomedRequests.prepareNewMember().setSource(source).build();
		case UPDATE: {
			final SnomedRefSetMemberUpdateRequestBuilder req = SnomedRequests.prepareUpdateMember().setSource(source);
			final Object forceValue = source.get("force");
			if (forceValue instanceof Boolean) {
				req.force((Boolean) forceValue);
			}
			return req.build();
		}
		case DELETE: {
			final DeleteRequestBuilder req = SnomedRequests.prepareDeleteMember((String) source.get("memberId"));
			final Object forceValue = source.get("force");
			if (forceValue instanceof Boolean) {
				req.force((Boolean) forceValue);
			}
			return req.build();
		}
		case SYNC: return SnomedRequests.prepareUpdateQueryRefSetMember().setSource(source).build();
		default: throw new BadRequestException("Unsupported member action '%s'", action); 
		}
	}
	
}
