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
package com.b2international.snowowl.core.events.util;

import com.b2international.commons.collections.Procedure;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @since 4.1
 * @param <T>
 *            - the type of the return value
 */
public interface Promise<T> extends ListenableFuture<T> {

	/**
	 * Define what to do when the promise becomes resolved.
	 * 
	 * @param then
	 * @return
	 */
	Promise<T> then(Procedure<T> then);

	/**
	 * Define what to do when the promise becomes rejected.
	 * 
	 * @param fail
	 * @return
	 */
	Promise<T> fail(Procedure<Throwable> fail);

}
