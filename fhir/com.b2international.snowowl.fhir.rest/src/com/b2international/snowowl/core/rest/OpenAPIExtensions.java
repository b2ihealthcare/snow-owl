/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * Extension constants for Snow Owl's {@link OpenAPI} implementation.
 * 
 * @since 8.0.0
 */
public class OpenAPIExtensions {
	
	/**
	 * Class-level OpenAPI extension to describe a resource type
	 */
	public static final String B2I_OPENAPI_X_NAME = "x-b2i-fhir";
	public static final String B2I_OPENAPI_PROFILE = "profile";
	
	/**
	 * Method-level OpenAPI extension to describe an operation (FHIR interaction)
	 */
	public static final String B2I_OPENAPI_X_INTERACTION = "x-interaction";
	public static final String B2I_OPENAPI_INTERACTION_READ = "read";
	public static final String B2I_OPENAPI_INTERACTION_CREATE = "create";

}
