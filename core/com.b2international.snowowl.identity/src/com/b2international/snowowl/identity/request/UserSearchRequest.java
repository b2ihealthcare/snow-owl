/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.identity.request;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.identity.domain.Users;

/**
 * @since 5.11
 */
final class UserSearchRequest extends SearchResourceRequest<ServiceProvider, Users> {

	UserSearchRequest() {}
	
	@Override
	protected Users createEmptyResult(int offset, int limit) {
		return new Users(offset, limit, 0);
	}

	@Override
	protected Users doExecute(ServiceProvider context) throws IOException {
		return context.service(IdentityProvider.class)
				.searchUsers(getCollection(OptionKey.COMPONENT_IDS, String.class), offset(), limit())
				.getSync(5, TimeUnit.MINUTES);
	}

}
