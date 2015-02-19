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
package com.b2international.snowowl.api.exception;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @since 1.0
 */
public class AlreadyExistsException extends ConflictException {

	private static final long serialVersionUID = 6347436684320140303L;

	public AlreadyExistsException(String type, String id) {
		super(formatMessage(type, id));
	}

	private static String formatMessage(String type, String id) {
		checkNotNull(type, "type");
		checkNotNull(id, "id");
		return String.format("%s with %s identifier already exists.", type, id);
	}
	
}