/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.branch.compare;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.ComponentIdentifier;

/**
 * @since 9.0
 */
public final class BranchCompareChangeStatistic implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String property;
	private final Set<ComponentIdentifier> componentIds;
	
	public BranchCompareChangeStatistic(final String property, final Set<ComponentIdentifier> componentIds) {
		this.property = Objects.requireNonNull(property);
		this.componentIds = Collections3.toImmutableSet(componentIds);
	}
	
	public String getProperty() {
		return property;
	}
	
	public Set<ComponentIdentifier> getComponentIds() {
		return componentIds;
	}
	
}
