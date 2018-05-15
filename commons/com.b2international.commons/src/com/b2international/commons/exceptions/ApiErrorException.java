/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;

/**
 * @since 6.4
 */
public final class ApiErrorException extends ApiException {

	private static final long serialVersionUID = -2150049598258902905L;
	
	private final ApiError error;

	public ApiErrorException(ApiError error) {
		super(error.getMessage());
		this.error = error;
	}
	
	@Override
	protected Integer getStatus() {
		return error.getStatus();
	}
	
	@Override
	protected Integer getCode() {
		return error.getCode();
	}
	
	@Override
	protected String getDeveloperMessage() {
		return error.getDeveloperMessage();
	}
	
	@Override
	protected Map<String, Object> getAdditionalInfo() {
		return error.getAdditionalInfo();
	}

}
