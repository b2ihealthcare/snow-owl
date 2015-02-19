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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import com.b2international.commons.reflect.MethodInvokerUtil;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.binder.LinkedBindingBuilder;

/**
 * @since 2.9
 */
public class MethodBinding implements Module {

	private final Method method;
	private final Object owner;

	public MethodBinding(Method method, Object owner) {
		this.method = method;
		this.owner = owner;
	}

	public Method getMethod() {
		return method;
	}

	public Object getOwner() {
		return owner;
	}

	@Override
	public void configure(Binder binder) {
		Object result;
		try {
			result = MethodInvokerUtil.invoke(getMethod(), getOwner());
			// binding only available if the binding method returns something not null
			if (result != null && !Void.class.equals(result)) {
				if (isClassBinding()) {
					doClassBinding(binder, (Class<?>) result);
				} else {
					doInstanceBinding(binder, result);
				}
			}
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	protected void doInstanceBinding(Binder binder, Object value) {
		Type key = getKeyType();
		final Key<Object> type = (Key<Object>) Key.get(key);
		binder.bind(type).toInstance(value);
	}

	protected void doClassBinding(Binder binder, Class<?> value) {
		Type key = getKeyType();
		final Key<Object> type = (Key<Object>) Key.get(key);
		LinkedBindingBuilder<Object> bind = binder.bind(type);
		if (!key.equals(value)) {
			bind.to(value);
		}
		if (isSingleton()) {
			bind.in(Scopes.SINGLETON);
		}
	}

	public Type getKeyType() {
		Type genericReturnType = getMethod().getGenericReturnType();
		if (isClassBinding()) {
			Type type = genericReturnType;
			if (type instanceof ParameterizedType) {
				return getFirstTypeParameter((ParameterizedType) type);
			}
			throw new IllegalStateException(
					String.format(
							"Return type of '%s' should be declared with wildcard and upperbound (i.e. Class<? extends IOntologyGenerator>)",
							method.getName()));
		} else {
			return genericReturnType;
		}
	}

	protected Type getFirstTypeParameter(ParameterizedType type) {
		Type firstParam = type.getActualTypeArguments()[0];
		if (firstParam instanceof WildcardType) {
			return ((WildcardType) firstParam).getUpperBounds()[0];
		}
		return firstParam;
	}

	public boolean isClassBinding() {
		return Class.class.equals(getMethod().getReturnType());
	}

	public boolean isSingleton() {
		return getMethod().getAnnotation(Singleton.class) != null;
	}

}