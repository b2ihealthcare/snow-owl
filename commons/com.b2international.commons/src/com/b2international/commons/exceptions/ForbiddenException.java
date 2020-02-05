/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.exceptions;

/**
 * @since 7.2
 */
public final class ForbiddenException extends ApiException {

	public ForbiddenException(String message) {
		super(message);
	}
	
	public ForbiddenException(String message, Object...args) {
		super(message, args);
	}

	@Override
	protected Integer getStatus() {
		return 403;
	}

}
