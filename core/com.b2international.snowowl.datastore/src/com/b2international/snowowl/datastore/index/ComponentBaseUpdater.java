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
package com.b2international.snowowl.datastore.index;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase;

/**
 * @since 4.3
 * @param <D> - builder type
 */
public class ComponentBaseUpdater<D extends DocumentBuilderBase<D>> extends DocumentUpdaterBase<D> {

	private short type;
	private long storageKey;

	public ComponentBaseUpdater(String componentId, short type, CDOID cdoid) {
		this(componentId, type, CDOIDUtil.getLong(cdoid));
	}
	
	public ComponentBaseUpdater(String componentId, short type, long storageKey) {
		super(componentId);
		this.type = type;
		this.storageKey = storageKey;
	}

	@Override
	public void doUpdate(D doc) {
		doc
			.id(getComponentId())
			.type(type)
			.storageKey(storageKey);
	}

}
