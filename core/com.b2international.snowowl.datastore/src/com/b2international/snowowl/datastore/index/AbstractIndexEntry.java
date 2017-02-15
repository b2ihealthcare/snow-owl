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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.snowowl.core.api.component.IconIdProvider;
import com.b2international.snowowl.core.api.index.IIndexEntry;

/**
 * Represents an abstract search result returned from a component search.
 * <p>
 * The following properties are supported:
 * <ul>
 * <li><em>{@link #getId() component identifier}</em>
 * <li><em>{@link #getLabel() display label}</em>
 * <li><em>{@link #getScore() calculated score}</em>
 * <li><em>{@link #getStorageKey() storage key}</em>
 * <li><em>{@link #getIconId() icon ID}</em>
 * </ul>
 * 
 */
public abstract class AbstractIndexEntry implements IIndexEntry, IconIdProvider<String> {

	private static final long serialVersionUID = -4158003369170743548L;

	protected final String id;
	protected String label;
	protected final float score;
	protected final long storageKey;
	protected final String iconId;

	
	public AbstractIndexEntry(final String id, final String label, String iconId, final float score, final long storageKey) {
		this.id = checkNotNull(id, "id");
		this.label = checkNotNull(label, "label");
		this.iconId = iconId;
		this.score = score;
		this.storageKey = storageKey;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public void setLabel(String label) {
		this.label = label;
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
	public String getIconId() {
		return iconId;
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