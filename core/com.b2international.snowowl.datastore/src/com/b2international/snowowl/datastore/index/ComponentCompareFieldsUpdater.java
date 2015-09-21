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

import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase;
import com.b2international.snowowl.datastore.index.mapping.Mappings;

/**
 * @since 4.3
 */
public class ComponentCompareFieldsUpdater<D extends DocumentBuilderBase<D>> extends DocumentUpdaterBase<D> {

	private boolean relevant;
	private long storageKey;

	public ComponentCompareFieldsUpdater(String componentId, long storageKey) {
		this(componentId, true, storageKey);
	}
	
	public ComponentCompareFieldsUpdater(String componentId, boolean relevant, long storageKey) {
		super(componentId);
		this.relevant = relevant;
		this.storageKey = storageKey;
	}

	@Override
	public void doUpdate(D doc) {
		doc.removeAll(Mappings.compareUniqueKey());
		doc.removeAll(Mappings.compareIgnoreUniqueKey());
		if (relevant) {
			doc.compareUniqueKey(storageKey);
		} else {
			doc.compareIgnoreUniqueKey(storageKey);
			doc.compareUniqueKey(CDOUtils.NO_STORAGE_KEY);
		}
	}

}
