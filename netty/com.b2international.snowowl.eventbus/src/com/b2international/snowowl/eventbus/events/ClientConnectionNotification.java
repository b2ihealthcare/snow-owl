/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.eventbus.events;

/**
 * @since 8.1.0
 */
public final class ClientConnectionNotification extends SystemNotification {

	private final boolean joining;
	private final String clientId;

	public ClientConnectionNotification(final boolean joining, final String clientId) {
		this.joining = joining;
		this.clientId = clientId;
	}

	public boolean isJoining() {
		return joining;
	}

	public String getClientId() {
		return clientId;
	}
	
	@Override
	public String toString() {
		return String.format("%s is %s", clientId, joining ? "joining" : "leaving");
	}
}
