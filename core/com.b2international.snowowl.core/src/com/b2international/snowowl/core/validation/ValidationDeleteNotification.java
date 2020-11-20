/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation;

import java.util.Set;

import com.b2international.snowowl.core.events.SystemNotification;

/**
 * @since 6.20.0
 */
public final class ValidationDeleteNotification extends SystemNotification {

	private static final long serialVersionUID = 2L;
	
	private final Set<String> resourceURIs;
	private final Set<String> toolingIds;

	public ValidationDeleteNotification(Set<String> resourceURIs, Set<String> toolingIds) {
		this.resourceURIs = resourceURIs;
		this.toolingIds = toolingIds;
	}

	public Set<String> getResourceURIs() {
		return resourceURIs;
	}
	
	public Set<String> getToolingIds() {
		return toolingIds;
	}
	
}
