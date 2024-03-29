/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.messaging;

import jakarta.validation.constraints.NotEmpty;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.0
 */
final class SendMessageRequest implements Request<ServiceProvider, Boolean> {

	@JsonProperty
	@NotEmpty
	private final String message;

	SendMessageRequest(String message) {
		this.message = message;
	}
	
	@Override
	public Boolean execute(ServiceProvider context) {
		new MessageNotification(message).publish(context.service(IEventBus.class));
		return Boolean.TRUE;
	}

}
