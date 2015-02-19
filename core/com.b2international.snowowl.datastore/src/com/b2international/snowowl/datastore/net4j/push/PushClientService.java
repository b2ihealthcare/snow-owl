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
package com.b2international.snowowl.datastore.net4j.push;

import java.io.Serializable;

/**
 * Client-side service for the push notification mechanism.
 * 
 * @since 2.8
 */
public enum PushClientService {
	INSTANCE;
	
	private PushClientProtocol protocol;

	public void setProtocol(PushClientProtocol protocol) {
		this.protocol = protocol;
	}

	/**
	 * Subscribes the client to the specified topic.
	 * 
	 * @param topic the topic to subscribe to
	 * @throws PushServiceException 
	 */
	public void subscribe(Serializable topic) throws PushServiceException {
		protocol.sendSubscribeRequest(topic);
	}
	
	/**
	 * Unsubscribes the client from the specified topic.
	 * 
	 * @param topic the topic to unsubscribe from
	 * @throws PushServiceException 
	 */
	public void unsubscribe(Serializable topic) throws PushServiceException {
		protocol.sendUnsubscribeRequest(topic);
	}
}