/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons;

import java.util.Base64;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.authorization.AuthorizedEventBus;
import com.b2international.snowowl.core.authorization.AuthorizedRequest;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;

/**
 * @since 3.3
 */
public class Services {

	private static IEventBus bus;

	/**
	 * Returns a must have service from the {@link ApplicationContext}.
	 * 
	 * @param type
	 * @return
	 */
	public static <T> T service(Class<T> type) {
		return ApplicationContext.getInstance().getServiceChecked(type);
	}
	
	public static ServiceProvider context() {
		return ApplicationContext.getServiceForClass(Environment.class);
	}
	
	public static IEventBus bus() {
		if (bus == null) {
			bus = new AuthorizedEventBus(ApplicationContext.getServiceForClass(IEventBus.class), ImmutableMap.of(AuthorizedRequest.AUTHORIZATION_HEADER, getAuthorizationToken()));
		}
		return bus;
	}

	public static String getAuthorizationToken() {
		final String userPass = String.join(":", RestExtensions.USER, RestExtensions.PASS);
		final String authorizationToken = new String(Base64.getEncoder().encode(userPass.getBytes()), Charsets.UTF_8);
		return "Basic " + authorizationToken;
	}

}
