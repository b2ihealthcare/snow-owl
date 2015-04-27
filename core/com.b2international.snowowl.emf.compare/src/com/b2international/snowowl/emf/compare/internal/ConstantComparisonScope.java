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
package com.b2international.snowowl.emf.compare.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.scope.FilterComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.b2international.commons.emf.NsUriProvider;

/**
 * Customized comparison scope backed with a namespace URI provider.
 *
 */
public class ConstantComparisonScope extends FilterComparisonScope {

	private final NsUriProvider provider;

	protected ConstantComparisonScope(final Notifier left, final Notifier right, final Notifier origin, final NsUriProvider provider) {
		super(left, right, origin);
		this.provider = checkNotNull(provider, "provider");
	}
	
	@Override
	public Set<String> getNsURIs() {
		return provider.getNsURIs();
	}
	
	@Override
	public Set<String> getResourceURIs() {
		return provider.getResourceURIs();
	}
	
	@Override
	protected void addUri(final EObject eObject) {
	}
	
	@Override
	protected void addUri(final Resource resource) {
	}
	
	@Override
	protected <T> void addUri(final T obj) {
	}

}