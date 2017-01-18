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
package com.b2international.snowowl.dsl;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.inject.Provider;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class SCGRuntimeModule extends com.b2international.snowowl.dsl.AbstractSCGRuntimeModule {

	public Provider<IEventBus> provideIEventBus() {
		return new Provider<IEventBus>() {
			@Override
			public IEventBus get() {
				return ApplicationContext.getServiceForClass(IEventBus.class);
			}
		};
	}
	
}