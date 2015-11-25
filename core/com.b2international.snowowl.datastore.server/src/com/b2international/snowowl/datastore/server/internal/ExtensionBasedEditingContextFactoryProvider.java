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
package com.b2international.snowowl.datastore.server.internal;

import java.util.Collection;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.datastore.server.EditingContextFactory;
import com.b2international.snowowl.datastore.server.EditingContextFactoryProvider;

/**
 * @since 4.5
 */
public final class ExtensionBasedEditingContextFactoryProvider implements EditingContextFactoryProvider {

	private static final String EXT_POINT_ID = "com.b2international.snowowl.datastore.server.editingContextFactory";
	
	private final Collection<EditingContextFactory> factories;

	public ExtensionBasedEditingContextFactoryProvider() {
		this.factories = Extensions.getExtensions(EXT_POINT_ID, EditingContextFactory.class);
	}
	
	@Override
	public EditingContextFactory get(String repositoryId) {
		for (EditingContextFactory factory : factories) {
			if (factory.belongsTo(repositoryId)) {
				return factory;
			}
		}
		throw new UnsupportedOperationException("No editing context factory has been registered for repository: " + repositoryId); 
	}

}
