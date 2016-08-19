/*******************************************************************************
 * Copyright (c) 2015 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.datastore;

import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.api.ComponentTextProvider;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentNameProvider;

/**
 * An implementation of {@link ComponentTextProvider} that uses the specified CDO view or transaction.
 * 
 * @since 4.4
 */
public final class CdoViewComponentTextProvider extends ComponentTextProvider {

	private final CDOView view;

	public CdoViewComponentTextProvider(final IComponentNameProvider componentNameProvider, final CDOView view) {
		super(componentNameProvider);
		this.view = view;
	}
	
	@Override
	public String getText(String componentId) {
		// TODO: Peek into the CDO view, and see if we find some additional info before/after(?) calling super.getText
		return super.getText(componentId);
	}

	@Override
	protected IBranchPath createPath() {
		return BranchPathUtils.createPath(view);
	}
}
