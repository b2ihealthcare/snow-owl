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
package com.b2international.snowowl.core.quicksearch;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.google.common.base.Preconditions;

/**
 * Represents a single result of a filter operation carried out using a quick search content provider. 
 *
 */
public abstract class QuickSearchElement implements Serializable {

	private static final long serialVersionUID = -3524236322333923445L;

	protected IQuickSearchProvider parentProvider;
	
	protected final String id;
	protected final String imageId;
	protected final String label;
	protected final boolean approximate;
	
	protected QuickSearchElement(final String id, final String imageId, final String label, final boolean approximate) {
		this.id = Preconditions.checkNotNull(id, "ID argument cannot be null.");
		this.imageId = Preconditions.checkNotNull(imageId, "Image ID argument cannot be null.");
		this.label = Preconditions.checkNotNull(label, "Label argument cannot be null.");
		this.approximate = approximate;
	}
	
	public String getLabel() {
		return label;
	}

	public String getId() {
		return id;
	}
	
	public String getImageId() {
		return imageId;
	}
	
	public abstract String getTerminologyComponentId();

	public boolean isApproximate() {
		return approximate;
	}

	public void execute() {
		parentProvider.handleSelection(this);
	}

	public IQuickSearchProvider getParentProvider() {
		return checkNotNull(parentProvider, "No parent provider has been set on the element.");
	}
	
	public void setParentProvider(final IQuickSearchProvider parentProvider) {
		this.parentProvider = parentProvider;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((getTerminologyComponentId() == null) ? 0 : getTerminologyComponentId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof QuickSearchElement)) {
			return false;
		}
		
		QuickSearchElement other = (QuickSearchElement) obj;
		
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		
		if (getTerminologyComponentId() == null) {
			if (other.getTerminologyComponentId() != null) {
				return false;
			}
		} else if (!getTerminologyComponentId().equals(other.getTerminologyComponentId())) {
			return false;
		}
		
		return true;
	}
}