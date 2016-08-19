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
package com.b2international.snowowl.snomed.core.store;

import org.eclipse.emf.cdo.CDOObject;

import com.b2international.snowowl.core.domain.TransactionContext;

/**
 * @since 4.5
 */
public abstract class SnomedBaseComponentBuilder<B extends SnomedBaseComponentBuilder<B, T>, T extends CDOObject> {

	protected SnomedBaseComponentBuilder() {
	}
	
	public final T build(TransactionContext context) {
		final T component = create();
		init(component, context);
		return component;
	}
	
	/**
	 * Initialize any additional properties on the given component.
	 * 
	 * @param component
	 *            - the component to initialize with additional props
	 * @param context
	 *            - the context to use to get configuration options and other components
	 */
	protected abstract void init(T component, TransactionContext context);

	/**
	 * Creates an instance of the component.
	 * 
	 * @return
	 */
	protected abstract T create();
	
	@SuppressWarnings("unchecked")
	protected final B getSelf() {
		return (B) this;
	}
	
}
