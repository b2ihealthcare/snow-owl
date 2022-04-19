/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.internal.eventbus;

import java.io.Serializable;
import java.util.Set;

/**
 * @since 3.1
 */
public class HandlerChangedEvent implements Serializable {

	/**
	 * Enumerates handler change event types.
	 */
	public enum Type {
		/** Request two-way synchronization of event bus address books */
		SYNC,
		/** Indicates that a handler has been registered to the specified address(es) */
		ADDED,
		/** Indicates that no handlers remain for the specified address(es) */ 
		REMOVED;
	}
	
	private final Type type;
	private final Set<String> addresses;
	
	public HandlerChangedEvent(final Type type, final Set<String> addresses) {
		this.type = type;
		this.addresses = addresses;
	}

	public Type getType() {
		return type;
	}
	
	public Set<String> getAddresses() {
		return addresses;
	}
	
	@Override
	public String toString() {
		return String.format("Handler %s for addresses %s", type, addresses);
	}
}
