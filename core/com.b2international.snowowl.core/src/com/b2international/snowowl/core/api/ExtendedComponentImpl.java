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
package com.b2international.snowowl.core.api;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link ExtendedComponent} implementation.
 */
public class ExtendedComponentImpl implements ExtendedComponent {

	private static final long serialVersionUID = -2246748848181836434L;

	private final String id;
	private final String label;
	private final String iconId;
	private final short terminologyComponentId;
	
	public ExtendedComponentImpl(String id, String label, String iconId, short terminologyComponentId) {
		this.id = checkNotNull(id, "id");
		this.label = checkNotNull(label, "label");
		this.iconId = iconId;
		this.terminologyComponentId = terminologyComponentId;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponent#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.LabelProvider#getLabel()
	 */
	@Override
	@Deprecated
	public String getLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IconIdProvider#getIconId()
	 */
	@Override
	public String getIconId() {
		return iconId;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.TerminologyComponentIdProvider#getTerminologyComponentId()
	 */
	@Override
	public short getTerminologyComponentId() {
		return terminologyComponentId;
	}

}