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
package com.b2international.snowowl.datastore.request;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOView;
import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.terminology.ComponentCategory;

/**
 * @since 4.5
 * @param <R>
 *            - the return type of the GET request
 */
public abstract class GetRequest<R> extends BaseResourceRequest<BranchContext, R> {

	private final String category;
	
	@NotEmpty
	private String componentId;
	
	protected GetRequest(ComponentCategory category) {
		this(category.getDisplayName());
	}
	
	protected GetRequest(String category) {
		this.category = checkNotNull(category, "category");
	}
	
	protected final void setComponentId(String componentId) {
		this.componentId = componentId;
	}
	
	@Override
	public final R execute(BranchContext context) {
		final IComponent<String> component = getLookupService().getComponent(context.branch().branchPath(), componentId);
		if (component == null) {
			throw new ComponentNotFoundException(category, componentId);
		} else {
			return process(context, component, expand());
		}
	}

	protected abstract R process(BranchContext context, IComponent<String> component, Options expand);

	protected abstract ILookupService<String, ? extends CDOObject, CDOView> getLookupService();
	
	@Override
	public final String toString() {
		return String.format("{type:'%s', componentId:'%s', expand:%s, locales:%s}", 
				getClass().getSimpleName(), 
				componentId,
				expand(), 
				formatStringList(locales()));
	}

}
