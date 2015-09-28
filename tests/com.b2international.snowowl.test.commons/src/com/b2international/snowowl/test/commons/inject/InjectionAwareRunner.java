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
package com.b2international.snowowl.test.commons.inject;

import java.util.Map;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.google.common.collect.Maps;
import com.google.inject.Injector;

/**
 * @since SDD 1.1
 */
public class InjectionAwareRunner extends BlockJUnit4ClassRunner {

	private static Map<Class<?>, IInjectorProvider> injectorProviderClassCache = Maps.newHashMap();

	public InjectionAwareRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected Object createTest() throws Exception {
		Object object = super.createTest();
		IInjectorProvider injectorProvider = getOrCreateInjectorProvider();
		if (injectorProvider != null) {
			Injector injector = injectorProvider.getInjector();
			if (injector != null)
				injector.injectMembers(object);
		}
		return object;
	}

	protected IInjectorProvider getOrCreateInjectorProvider() {
		IInjectorProvider injectorProvider = getInjectorProvider();
		if (injectorProvider == null) {
			injectorProvider = createInjectorProvider();
			injectorProviderClassCache.put(getTestClass().getJavaClass(), injectorProvider);
		}
		return injectorProvider;
	}

	protected IInjectorProvider getInjectorProvider() {
		return injectorProviderClassCache.get(getTestClass().getJavaClass());
	}

	protected IInjectorProvider createInjectorProvider() {
		IInjectorProvider injectorProvider = null;
		InjectWith injectWith = getTestClass().getJavaClass().getAnnotation(InjectWith.class);
		if (injectWith != null) {
			try {
				injectorProvider = injectWith.value().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return injectorProvider;
	}
	
}
