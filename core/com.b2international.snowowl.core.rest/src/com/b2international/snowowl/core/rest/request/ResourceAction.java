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
package com.b2international.snowowl.core.rest.request;

import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;

/**
 * @since 8.0
 */
public enum ResourceAction {
	CREATE, 
	UPDATE, 
	DELETE,
	SYNC;

	public static ResourceAction get(String action) {
		for (ResourceAction type : values()) {
			if (type.name()
					.toLowerCase()
					.equals(action.toLowerCase())) {
				return type;
			}
		}

		throw new BadRequestException("Invalid value set clause action type '%s'. Only {%s} are allowed", action,
				String.join(",", List.of(ResourceAction.values())
						.stream()
						.map(actionType -> actionType.name())
						.collect(Collectors.toList())));
	}
}
