/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

/**
 * @since 4.0
 */
public abstract class BaseComponent implements IComponent {

	private long storageKey;
	private String id;
	private Boolean released;
	
	@Override
	public final String getId() {
		return id;
	}

	@Override
	public final Boolean isReleased() {
		return released;
	}
	
	@Override
	public final long getStorageKey() {
		return storageKey;
	}

	public final void setId(final String id) {
		this.id = id;
	}

	public final void setReleased(final boolean released) {
		this.released = released;
	}
	
	/**
	 * @deprecated - see {@link IComponent#getStorageKey()}
	 */
	public final void setStorageKey(long storageKey) {
		this.storageKey = storageKey;
	}
	
}