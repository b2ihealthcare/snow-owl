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
package com.b2international.snowowl.core.events.util;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.metrics.MetricsProvider;

/**
 * Generic Request handler class that handles all requests by executing them immediately.
 * 
 * @since 4.5
 */
public final class ApiRequestHandler extends ApiEventHandler {

	private final ServiceProvider context;

	public ApiRequestHandler(ServiceProvider context, ClassLoader classLoader) {
		super(Request.class, classLoader);
		this.context = context;
	}
	
	@Handler
	public Object handle(Request<ServiceProvider, Object> req) {
		return context.service(MetricsProvider.class).measure(req).execute(context);
	}
	
}
