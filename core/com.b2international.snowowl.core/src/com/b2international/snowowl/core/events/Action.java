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

/**
 * An {@link Action} represents an executable form of user intent.
 *
 * @since 4.5
 * @param <T>
 *            - the type of context where this {@link Action} can be executed
 * @param <B>
 *            - the type of the result
 */
public interface Action<T extends ServiceProvider, B> extends Event {

	/**
	 * Executes this action on the given {@link ExecutionContext}.
	 *
	 * @param context
	 * @return - the result of the {@link Action}
	 */
	B execute(T context);

}
