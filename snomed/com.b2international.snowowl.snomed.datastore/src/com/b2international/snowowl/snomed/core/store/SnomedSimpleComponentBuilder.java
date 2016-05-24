/*******************************************************************************
 * Copyright (c) 2016 B2i Healthcare. All rights reserved.
 *******************************************************************************/
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
	protected void init(final T component, final TransactionContext context) {}
	
}
