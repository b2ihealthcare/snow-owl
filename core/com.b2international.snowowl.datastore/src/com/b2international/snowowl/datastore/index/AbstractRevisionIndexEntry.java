/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.api.component.IconIdProvider;
import com.b2international.snowowl.core.api.index.IIndexEntry;

/**
 * @since 4.7
 */
public abstract class AbstractRevisionIndexEntry extends Revision implements IIndexEntry, IconIdProvider<String> {

	private final String id;
	private final String label;
	private final float score;
	private final long storageKey;
	private final String iconId;
	
	protected AbstractRevisionIndexEntry(final String id, final String label, String iconId, final float score, final long storageKey) {
		this.id = checkNotNull(id, "id");
		this.label = checkNotNull(label, "label");
		this.iconId = iconId;
		this.score = score;
		this.storageKey = storageKey;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getIconId() {
		return iconId;
	}

	@Override
	public float getScore() {
		return score;
	}

	@Override
	public long getStorageKey() {
		return storageKey;
	}
	
	@Override
	public String toString() {
		return String.format("%02.2f %s - %s [%d]", score, id, label, storageKey);
	}

	/**
	 * (non-API)
	 * 
	 * @return returns with the storage key of the current component as a CDO ID.
	 */
	public CDOID cdoID() {
		return CDOIDUtil.createLong(storageKey);
	}

}
