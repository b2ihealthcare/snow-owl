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
package com.b2international.snowowl.snomed.api.rest.request;

import com.b2international.commons.exceptions.BadRequestException;

/**
 * @since 4.5
 */
public enum Action {

	CREATE,
	UPDATE,
	DELETE,
	SYNC;
	
	public static Action get(String action) {
		for (Action type : values()) {
			if (type.name().toLowerCase().equals(action)) {
				return type;
			}
		}
		throw new BadRequestException("Invalid action type '%s'.", action);
	}
	
}
