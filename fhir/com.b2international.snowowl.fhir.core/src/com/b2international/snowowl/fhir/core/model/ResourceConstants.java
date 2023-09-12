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
package com.b2international.snowowl.fhir.core.model;

import org.hl7.fhir.r5.model.Base;

import com.b2international.snowowl.core.ResourceURI;

/**
 * 
 * @since 9.0
 */
public abstract class ResourceConstants {

	// Keys for getUserData / setUserData
	public static final String TOOLING_ID = "toolingId";
	public static final String RESOURCE_URI = "resourceUri";
	public static final String CURRENT_PAGE_ID = "currentPageId";
	public static final String NEXT_PAGE_ID = "nextPageId";

	public static ResourceURI getResourceUri(final Base resource) {
		return (ResourceURI) resource.getUserData(RESOURCE_URI);
	}
	
	private ResourceConstants() {
		// This class is not supposed to be instantiated
	}
}
