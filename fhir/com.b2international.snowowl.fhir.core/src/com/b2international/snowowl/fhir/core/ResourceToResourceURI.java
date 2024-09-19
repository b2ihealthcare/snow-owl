/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core;

import org.hl7.fhir.r5.model.*;

import com.b2international.snowowl.core.ResourceURI;

/**
 * @since 9.3
 */
public class ResourceToResourceURI {

	public static ResourceURI from(Resource resource) {
		return ResourceURI.of(resource.getResourceType().name().toLowerCase() + "s", resource.getId());
	}
	
}
