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
package com.b2international.snowowl.core.validation.detail;

import java.util.Map;

import com.b2international.snowowl.core.ComponentIdentifier;

/**
 * @since 6.4
 */
public class IssueDetail {
	
	private final ComponentIdentifier affectedComponent;
	private final Map<String, Object> details;
	
	public IssueDetail(ComponentIdentifier affectedComponent, Map<String, Object> details) {
		this.affectedComponent = affectedComponent;
		this.details = details;
	}
	
	public ComponentIdentifier getAffectedComponent() {
		return affectedComponent;
	}
	
	public Map<String, Object> getDetails() {
		return details;
	}
	
}