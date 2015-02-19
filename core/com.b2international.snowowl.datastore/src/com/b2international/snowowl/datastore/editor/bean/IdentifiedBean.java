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
package com.b2international.snowowl.datastore.editor.bean;

import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.commons.beans.BeanPropertyChangeSupporter;

/**
 * @since 2.9
 */
public class IdentifiedBean extends BeanPropertyChangeSupporter {
	
	private static final long serialVersionUID = 7729171909038594392L;
	
	protected final String id;
	protected final CDOID storageKey;
	
	public IdentifiedBean(String id, CDOID storageKey) {
		this.id = id;
		this.storageKey = storageKey;
	}

	public String getId() {
		return id;
	}

	public CDOID getStorageKey() {
		return storageKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdentifiedBean other = (IdentifiedBean) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}