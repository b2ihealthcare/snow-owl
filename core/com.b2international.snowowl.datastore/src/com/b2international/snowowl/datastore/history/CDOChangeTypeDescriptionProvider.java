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
package com.b2international.snowowl.datastore.history;

import org.eclipse.emf.ecore.EObject;

import com.b2international.commons.ChangeKind;
import com.b2international.commons.StringUtils;
import com.google.inject.Inject;

/**
 * Description provider, that provides description prefixed with CDO change type
 * and the component name. Use it with {@link ChangeKind#ADDED} and with {@link ChangeKind#DELETED}.
 * 
 * @since 3.1
 */
public class CDOChangeTypeDescriptionProvider extends DefaultDescriptionProvider {

	private final INameProvider nameProvider;

	@Inject
	public CDOChangeTypeDescriptionProvider(INameProvider nameProvider) {
		super();
		this.nameProvider = nameProvider;
	}
	
	@Override
	public String provideDescription(EObject object, ChangeKind type) {
		final String componentType = this.nameProvider.provideName(object);
		final String description = super.provideDescription(object, type);
		return String.format("%s, %s: '%s'.", getChangeTypeLiteral(type), componentType, description);
	}
	
	protected String getChangeTypeLiteral(ChangeKind type) {
		return StringUtils.capitalizeFirstLetter(type.name().toLowerCase());
	}
	
}