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
package com.b2international.snowowl.core.uri;

import java.time.LocalDate;

import com.b2international.commons.exceptions.BadRequestException;

/**
 * @since 8.0
 */
public interface ResourceURLSchemaSupport {

	default void validate(String uri) throws BadRequestException {
		// by default any URI is valid, specific terminology schemas can provide their
	}
	
	default String withVersion(String uri, String version, LocalDate effectiveTime) {
		return String.join("/version/", uri, version);
	}
	
	ResourceURLSchemaSupport DEFAULT = new ResourceURLSchemaSupport() {};
	
}
