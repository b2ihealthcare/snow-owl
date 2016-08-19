/*******************************************************************************
 * Copyright (c) 2015 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.api;

/**
 * Asks a server-side provider for the display label of a component by identifier.
 * 
 * @since 4.4
 */
public abstract class ComponentTextProvider {

	private final IComponentNameProvider componentNameProvider;

	public ComponentTextProvider(final IComponentNameProvider componentNameProvider) {
		this.componentNameProvider = componentNameProvider;
	}

	public String getText(final String componentId) {
		return componentNameProvider.getComponentLabel(createPath(), componentId);
	}

	protected abstract IBranchPath createPath();
}
