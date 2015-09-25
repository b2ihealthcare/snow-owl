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

import com.b2international.snowowl.core.api.index.IIndexEntry;

/**
 * @since 4.3
 */
public class MockIndexEntry implements IIndexEntry {

	private static final long serialVersionUID = 8495046782045849702L;
	
	private String id;
	private String label;
	private float score;
	private long storageKey;
	
	public MockIndexEntry(String id, String label, long storageKey, float score) {
		this.id = id;
		this.label = label;
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
	public float getScore() {
		return score;
	}
	
	@Override
	public long getStorageKey() {
		return storageKey;
	}
	
}
