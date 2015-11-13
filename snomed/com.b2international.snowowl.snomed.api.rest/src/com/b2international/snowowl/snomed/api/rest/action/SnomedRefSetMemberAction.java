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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

/**
 * @since 4.5
 */
@JsonTypeInfo(property = "type", use = JsonTypeInfo.Id.NAME, include = As.PROPERTY)
@JsonSubTypes(
	@JsonSubTypes.Type(name="update", value = UpdateQueryRefSetMemberAction.class)
)
public interface SnomedRefSetMemberAction<R> {

	/**
	 * The type of the action the reference set member supports.
	 * 
	 * @return
	 */
	String getType();

	/**
	 * Converts this REST action to a {@link Request} object that can be executed on the member identified by the given memberId parameter on the
	 * given branch.
	 *
	 * @param branch - where the returned {@link Request} will be executed
	 * @param userId - the user who executes the action
	 * @param memberId - on which member the returned {@link Request} will be executed
	 * @return
	 */
	Request<ServiceProvider, R> toRequest(String branch, String userId, String memberId);

}
