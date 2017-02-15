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
package com.b2international.snowowl.index.diff.tests.mock;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.datastore.cdo.CDOUtils;

/**
 * Represents indexed document contents (including score) as a POJO.
 * 
 * @since 4.3
 */
public class DiffConceptIndexEntry implements IIndexEntry {

	private static final long serialVersionUID = 8495046782045849702L;

	private final String id;
	private String label;
	private final float score;
	private final long storageKey;

	public DiffConceptIndexEntry(final String id, final String label, final long storageKey, final float score) {
		checkArgument(storageKey > CDOUtils.NO_STORAGE_KEY, "storageKey may not be negative");

		this.id = checkNotNull(id, "id");
		this.label = checkNotNull(label, "label");
		this.storageKey = storageKey;
		this.score = score;
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
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DiffConceptIndexEntry [id=");
		builder.append(id);
		builder.append(", label=");
		builder.append(label);
		builder.append(", score=");
		builder.append(score);
		builder.append(", storageKey=");
		builder.append(storageKey);
		builder.append("]");
		return builder.toString();
	}
}
