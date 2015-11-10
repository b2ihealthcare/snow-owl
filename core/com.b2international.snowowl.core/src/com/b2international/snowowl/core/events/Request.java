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
package com.b2international.snowowl.core.events;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * A {@link Request} represents an executable form of user intent. They can be executed in a specific context usually within a {@link ServiceProvider}
 * . Executing a {@link Request} will result in either a success or failure. Success usually returns the requested object or success message while
 * failure usually delivers an error object or {@link Throwable} to the caller.
 *
 * @since 4.5
 * @param <T>
 *            - the type of context where this {@link Request} can be executed
 * @param <B>
 *            - the type of the result
 */
public interface Request<T extends ServiceProvider, B> extends Event {

	/**
	 * Sends and executes this {@link Request} asynchronously via the given dispatcher. The returned {@link Promise} will be either resolved or
	 * rejected after execution of the {@link Request}.
	 * 
	 * @param bus
	 *            - the dispatcher of the request
	 * @return a {@link Promise} representing the execution of the {@link Request}
	 */
	Promise<B> execute(IEventBus bus);

	/**
	 * Sends this {@link Request} to existing handlers and waits for the response synchronously until it arrives. Basically converts this async
	 * operation to a sync one.
	 * 
	 * @param bus
	 *            - the dispatcher of the request
	 * @return
	 */
	B executeSync(IEventBus bus);

	/**
	 * Sends this {@link Request} to existing handlers and waits for the response synchronously until it arrives or the timeout (defined in
	 * milliseconds) happens.
	 * 
	 * @param bus
	 *            - the dispatcher of the request
	 * @param timeout
	 *            - timeout value defined in milliseconds
	 * @return
	 */
	B executeSync(IEventBus bus, long timeout);

	/**
	 * Executes this action on the given {@link ExecutionContext} directly without dispatching it.
	 *
	 * @param context
	 *            - the context within this {@link Request} can be executed
	 * @return - the result of the {@link Request}, never <code>null</code>.
	 */
	B execute(T context);

}
