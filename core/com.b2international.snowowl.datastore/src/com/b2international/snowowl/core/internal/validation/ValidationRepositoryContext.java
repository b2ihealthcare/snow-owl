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
package com.b2international.snowowl.core.internal.validation;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Set;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.DelegatingContext;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteList;
import com.b2international.snowowl.core.validation.whitelist.WhiteListNotification;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.collect.Iterables;

/**
 * @since 6.3
 */
public final class ValidationRepositoryContext extends DelegatingContext {

	private final Map<String, ValidationWhiteList> newObjects = newHashMap();
	private final Map<Class<?>, Set<String>> objectsToDelete = newHashMap();
	
	ValidationRepositoryContext(ServiceProvider delegate) {
		super(delegate);
	}

	public ValidationRepository repository() {
		return service(ValidationRepository.class);
	}
	
	public void save(ValidationWhiteList whiteList) {
		newObjects.put(whiteList.getId(), whiteList);
	}
	
	public void delete(Set<String> ids) {
		objectsToDelete.put(ValidationWhiteList.class, ids);
	}

	public void commit() {
		if (!newObjects.isEmpty() || !objectsToDelete.isEmpty()) {
			repository().write(writer -> {
				writer.putAll(newObjects);
				writer.removeAll(objectsToDelete);
				writer.commit();
				return null;
			});
			
			if (!newObjects.isEmpty()) {
				WhiteListNotification.added(newObjects.keySet()).publish(service(IEventBus.class));
			} else if (!objectsToDelete.isEmpty()) {
				WhiteListNotification.removed(Iterables.getOnlyElement(objectsToDelete.values())).publish(service(IEventBus.class));
			}
		}
	}

}