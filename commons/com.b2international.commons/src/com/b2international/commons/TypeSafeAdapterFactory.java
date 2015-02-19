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

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Provides type-safe delegates of the methods described in {@link IAdapterFactory}. Can be used to avoid multiple
 * SuppressWarnings annotations.
 * 
 */
public abstract class TypeSafeAdapterFactory implements IAdapterFactory {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		return getAdapterSafe(adaptableObject, adapterType);
	}
	
	public abstract <T> T getAdapterSafe(Object adaptableObject, Class<T> adapterType);

	@Override
	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return getAdapterListSafe();
	}

	public abstract Class<?>[] getAdapterListSafe();
}