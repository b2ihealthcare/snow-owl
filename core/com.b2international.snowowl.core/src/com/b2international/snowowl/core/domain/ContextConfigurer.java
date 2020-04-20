/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

import com.b2international.snowowl.core.ServiceProvider;

/**
 * @since 7.5.1
 */
public interface ContextConfigurer {

	/**
	 * No-op context configurer.
	 */
	ContextConfigurer NOOP = new ContextConfigurer() {
		@Override
		public <C extends ServiceProvider> C configure(C context) {
			return context;
		}
	};

	/**
	 * Enhances a context by attaching additional services to it.
	 * 
	 * @param <C> - the type of the context
	 * @param context - the context to enhance
	 * @return the enhanced context
	 */
	<C extends ServiceProvider> C configure(C context);
	
}
