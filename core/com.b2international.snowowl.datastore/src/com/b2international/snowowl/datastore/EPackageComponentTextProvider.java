/*******************************************************************************
 * Copyright (c) 2015 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.datastore;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.api.ComponentTextProvider;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentNameProvider;

/**
 * An implementation of {@link ComponentTextProvider} that uses the active branch path for retrieving labels.
 * 
 * @since 4.4
 */
public final class EPackageComponentTextProvider extends ComponentTextProvider {

	private final EPackage ePackage;

	public EPackageComponentTextProvider(final IComponentNameProvider componentNameProvider, final EPackage ePackage) {
		super(componentNameProvider);
		this.ePackage = ePackage;
	}

	@Override
	protected IBranchPath createPath() {
		return BranchPathUtils.createActivePath(ePackage);
	}
}
