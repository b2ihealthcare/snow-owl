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
package com.b2international.commons.inject;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;

/**
 * @since 2.8
 */
public abstract class AbstractGenericModule extends AbstractModule {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGenericModule.class);

	@Override
	protected void configure() {
		// use method resolution to get the proper binding, 
		getBindings().configure(binder());
	}

	/**
	 * Returns the bindings available in this class. Lookup algorithm based on the method prefix name, this
	 * implementation supports only bind* based methods.
	 * 
	 * @return a {@link CompoundModule} containing all {@link MethodBinding}s.
	 * @since 2.9
	 */
	protected CompoundModule getBindings() {
		final Method[] methods = this.getClass().getMethods();
		final CompoundModule result = new CompoundModule();
		for (final Method method : methods) {
			try {
				// TODO only bind* methods supported
				if (method.getName().startsWith("bind")) {
					result.add(createMethodBinding(method));
				}
			} catch (final Exception e) {
				LOGGER.warn("Trying to use method " + method.toGenericString() + " for configuration failed", e);
			}
		}
		return result;
	}

	/**
	 * @param method
	 * @return
	 * @since 2.9
	 */
	protected Module createMethodBinding(final Method method) {
		return new MethodBinding(method, this);
	}

	protected void bindConfig(final String property, final String value) {
		bindConstant().annotatedWith(Names.named(property)).to(value);
	}

	protected void bindConfig(final String property, final short value) {
		bindConstant().annotatedWith(Names.named(property)).to(value);
	}

	protected void bindConfig(final String property, final int value) {
		bindConstant().annotatedWith(Names.named(property)).to(value);
	}
	
	protected void bindConfig(final String property, final long value) {
		bindConstant().annotatedWith(Names.named(property)).to(value);
	}

	/**
	 * @param property
	 * @param value
	 * @since 2.9
	 */
	protected void bindConfig(final String property, final boolean value) {
		bindConstant().annotatedWith(Names.named(property)).to(value);
	}
	
	/**
	 * @param property
	 * @param value
	 * @since 2.9
	 */
	protected <T> void bindConfig(final String property, final Class<T> value) {
		bindConstant().annotatedWith(Names.named(property)).to(value);
	}

}