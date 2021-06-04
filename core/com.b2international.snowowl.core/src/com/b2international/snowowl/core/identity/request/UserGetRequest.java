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
package com.b2international.snowowl.core.identity.request;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.identity.Users;
import com.b2international.snowowl.core.request.GetResourceRequest;

/**
 * @since 5.11
 */
final class UserGetRequest extends GetResourceRequest<UserSearchRequestBuilder, ServiceProvider, Users, User> {

	UserGetRequest(String username) {
		super(username);
	}

	@Override
	protected UserSearchRequestBuilder createSearchRequestBuilder() {
		return UserRequests.prepareSearch();
	}

}
