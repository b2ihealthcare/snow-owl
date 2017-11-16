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
package com.b2international.snowowl.core.domain;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 4.0
 */
public abstract class BaseComponent implements IComponent {

	private long storageKey;
	private String id;
	private Boolean released;
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public Boolean isReleased() {
		return released;
	}
	
	@Override
	public long getStorageKey() {
		return storageKey;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setReleased(final boolean released) {
		this.released = released;
	}
	
	/**
	 * Returns a {@link ComponentIdentifier} instance to identify this component using its {@link #getTerminologyComponentId() type} and {@link #getId() id}.
	 * @return
	 */
	@JsonIgnore
	public final ComponentIdentifier getComponentIdentifier() {
		return ComponentIdentifier.of(getTerminologyComponentId(), getId());
	}
	
	/**
	 * @return the associated terminology component type identifier of this component.
	 */
	@JsonIgnore
	public abstract short getTerminologyComponentId();
	
	/**
	 * @deprecated - see {@link IComponent#getStorageKey()}
	 */
	public void setStorageKey(long storageKey) {
		this.storageKey = storageKey;
	}
	
}