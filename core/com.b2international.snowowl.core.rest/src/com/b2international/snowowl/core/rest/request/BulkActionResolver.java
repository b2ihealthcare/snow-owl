/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.request;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 8.0
 */
/**
 * Classes that implement this interface can convert an action and the additional data 
 *  to {@link Request}s
 * 
 * @param <S> - the type of Request to Resolve the actions to
 * @param <T> - The Type of the additional data for the resolve operation
 */
public interface BulkActionResolver<S extends ServiceProvider, T> {
	Request<S, ?> resolve(String action, T additionalResolveData);
}
