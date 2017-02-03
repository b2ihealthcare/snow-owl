/*
 * Copyright 2015-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
