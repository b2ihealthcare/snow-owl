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
package com.b2international.snowowl.snomed.api.rest.action;

import java.util.Map;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;

/**
 * @since 4.5
 */
public class RefSetMemberActionResolver implements ActionResolver {

	private String userId;
	private String branch;
	private String memberId;

	public RefSetMemberActionResolver(String userId, String branch, String memberId) {
		this.userId = userId;
		this.branch = branch;
		this.memberId = memberId;
	}
	
	@Override
	public Request<ServiceProvider, ?> resolve(String action, Map<String, Object> source) {
		switch (action) {
		case "update": return toUpdateRequest(source);
		default: throw new BadRequestException("Invalid action type '%s'.", action); 
		}
	}
	
	private Request<ServiceProvider, ?> toUpdateRequest(Map<String, Object> source) {
		return SnomedRequests
				.prepareUpdateQueryRefSetMember()
				.setMemberId(memberId)
				.setModuleId((String) source.get("moduleId"))
				.build(userId, branch, (String) source.get("commitComment"));
	}
	
}
