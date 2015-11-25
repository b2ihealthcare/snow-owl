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
package com.b2international.snowowl.api.impl.history.domain;

import com.b2international.snowowl.core.history.domain.ChangeType;
import com.b2international.snowowl.core.history.domain.IHistoryInfoDetails;

/**
 *
 */
public class HistoryInfoDetails implements IHistoryInfoDetails {

	private String componentType;
	private String description;
	private ChangeType changeType;
	
	/**
	 * @param componentType
	 * @param description
	 */
	public HistoryInfoDetails(String componentType, String description, ChangeType changeType) {
		this.componentType = componentType;
		this.description = description;
		this.changeType = changeType;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.rest.api.domain.IHistoryInfoDetails#getComponentType()
	 */
	@Override
	public String getComponentType() {
		return componentType;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.rest.api.domain.IHistoryInfoDetails#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.rest.api.domain.IHistoryInfoDetails#getChangeType()
	 */
	@Override
	public ChangeType getChangeType() {
		return changeType;
	}
}