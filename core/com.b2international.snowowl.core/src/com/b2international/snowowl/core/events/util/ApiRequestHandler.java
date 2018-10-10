/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.eclipse.emf.common.util.WrappedException;

import com.b2international.commons.exceptions.ApiException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.monitoring.MonitoredRequest;
import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

/**
 * Generic Request handler class that handles all requests by executing them immediately.
 * 
 * @since 4.5
 */
public final class ApiRequestHandler implements IHandler<IMessage> {

	private final ServiceProvider context;
	private final ClassLoader classLoader;
	
	public ApiRequestHandler(ServiceProvider context, ClassLoader classLoader) {
		this.context = context;
		this.classLoader = classLoader;
	}
	
	@Override
	public final void handle(IMessage message) {
		try {
			final Request<ServiceProvider, ?> req = message.body(Request.class, classLoader);
			message.reply(new MonitoredRequest<>(req).execute(context));
		} catch (WrappedException e) {
			message.fail(e.getCause());
		} catch (ApiException e) {
			message.fail(e);
		} catch (Throwable e) {
			message.fail(e);
		}
	}
	
}
