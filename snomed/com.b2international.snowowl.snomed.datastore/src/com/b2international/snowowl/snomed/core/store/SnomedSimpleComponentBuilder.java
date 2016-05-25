/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.store;

import org.eclipse.emf.cdo.CDOObject;

import com.b2international.snowowl.core.domain.TransactionContext;

/**
 * @since 4.7
 */
public abstract class SnomedSimpleComponentBuilder<B extends SnomedSimpleComponentBuilder<B, T>, T extends CDOObject> extends SnomedBaseComponentBuilder<B, T> {

	public final T build() {
		final T component = create();
		init(component);
		return component;
	}
	
	/**
	 * Initialize any additional properties on the given component.
	 * 
	 * @param component
	 *            - the component to initialize with additional props
	 */
	protected abstract void init(T component);
	
	@Override
	protected void init(final T component, final TransactionContext context) {
		throw new UnsupportedOperationException();
	}
	
}
