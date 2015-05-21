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
package com.b2international.snowowl.api.impl.domain;

import com.b2international.snowowl.api.domain.IComponentRef;

/**
 * @since 1.0
 */
public class ComponentRef extends StorageRef implements InternalComponentRef {

	private String componentId;

	@Override
	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(final String componentId) {
		this.componentId = componentId;
	}

	@Override
	public final int compareTo(final IComponentRef other) {
		int result = 0;
		if (result == 0) { result = getShortName().compareTo(other.getShortName()); }
		if (result == 0) { result = getBranchPath().compareTo(other.getBranchPath()); }
		if (result == 0) { result = getComponentId().compareTo(other.getComponentId()); }
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ComponentRef [getShortName()=");
		builder.append(getShortName());
		builder.append(", getBranchPath()=");
		builder.append(getBranchPath());
		builder.append(", componentId=");
		builder.append(componentId);
		builder.append("]");
		return builder.toString();
	}

}