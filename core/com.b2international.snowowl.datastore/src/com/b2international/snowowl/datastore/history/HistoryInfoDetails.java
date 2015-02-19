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
package com.b2international.snowowl.datastore.history;

import java.io.Serializable;

import com.b2international.commons.ChangeKind;
import com.b2international.snowowl.core.api.IHistoryInfoDetails;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * POJO class for history info details.
 *
 */
@XStreamAlias("HistoryInfoDetails")
public class HistoryInfoDetails implements IHistoryInfoDetails, Serializable {

	private static final long serialVersionUID = 981378024773635666L;

	@XStreamAlias("componentType")
	private final String componentType;
	
	@XStreamAlias("description")
	private final String description;
	
	@XStreamAlias("changeType")
	private final ChangeKind changeType;
	
	public HistoryInfoDetails(final String componentType, final String description, final ChangeKind changeType) {
		this.componentType = componentType;
		this.description = description;
		this.changeType = changeType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.history.IHistoryInfo.IHistoryInfoDetails#getComponentType()
	 */
	@Override
	public String getComponentType() {
		return componentType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.history.IHistoryInfo.IHistoryInfoDetails#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.history.IHistoryInfo.IHistoryInfoDetails#getChangeType()
	 */
	@Override
	public ChangeKind getChangeType() {
		return changeType;
	}
	
}