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
package com.b2international.snowowl.core.request;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 6.1
 */
public abstract class UpdateRequest implements Request<TransactionContext, Boolean> {
	
	@NotEmpty
	private final String componentId;

	public UpdateRequest(String componentId) {
		this.componentId = componentId;
	}

	protected final String componentId() {
		return componentId;
	}
	
	protected final <T> boolean updateProperty(T value, Supplier<T> getter, Consumer<T> setter) {
		if (value != null && !Objects.equals(value, getter.get())) {
			setter.accept(value);
			return true;
		} else {
			return false;
		}
	}

}
