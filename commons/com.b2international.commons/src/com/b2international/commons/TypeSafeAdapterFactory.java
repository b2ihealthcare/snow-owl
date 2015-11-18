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
package com.b2international.commons;

import java.util.Set;

import org.eclipse.core.runtime.IAdapterFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Provides type-safe delegates of the methods described in {@link IAdapterFactory}. Can be used to avoid multiple
 * {@link SuppressWarnings} annotations.
 */
public abstract class TypeSafeAdapterFactory implements IAdapterFactory {

	private final Set<Class<?>> supportedClasses;

	public TypeSafeAdapterFactory(final Class<?>... supportedClasses) {
		this.supportedClasses = ImmutableSet.copyOf(supportedClasses);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final Object getAdapter(final Object adaptableObject, final Class adapterType) {
		if (null != adaptableObject && supportedClasses.contains(adapterType)) {
			return getAdapterSafe(adaptableObject, adapterType);
		} else {
			return null;
		}
	}

	protected abstract <T> T getAdapterSafe(Object adaptableObject, Class<T> adapterType);

	@Override
	@SuppressWarnings("rawtypes")
	public final Class[] getAdapterList() {
		return getAdapterListSafe();
	}

	private Class<?>[] getAdapterListSafe() {
		return Iterables.toArray(supportedClasses, Class.class);
	}
}
