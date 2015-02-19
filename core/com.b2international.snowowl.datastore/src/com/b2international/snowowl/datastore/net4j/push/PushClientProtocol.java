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

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.spi.net4j.ClientProtocolFactory;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;


/**
 * Client-side signal protocol for the push notification mechanism.
 * 
 * @since 2.8
 */
public class PushClientProtocol extends SignalProtocol<Object> {

	public static final String PROTOCOL_NAME = "push";
	
	public PushClientProtocol() {
		super(PROTOCOL_NAME);
	}
	
	@Override
	protected SignalReactor createSignalReactor(short signalID) {
		switch (signalID) {
		case Net4jProtocolConstants.PUSH_SIGNAL:
			return new PushIndication(this);
		default:
			return super.createSignalReactor(signalID);
		}
	}
	
	/**
	 * Sends a subscribe request to the server.
	 * 
	 * @param topic the topic to subscribe to
	 * @throws PushServiceException 
	 */
	public void sendSubscribeRequest(Serializable topic) throws PushServiceException {
		try {
			new SubscribeRequest(this, topic).sendAsync();
		} catch (Exception e) {
			throw new PushServiceException("Error when sending subscribe request.", e);
		}
	}
	
	/**
	 * Sends an unsubscribe request to the server.
	 * 
	 * @param topic the topic to unsubscribe from
	 * @throws PushServiceException
	 */
	public void sendUnsubscribeRequest(Serializable topic) throws PushServiceException {
		try {
			new UnsubscribeRequest(this, topic).sendAsync();
		} catch (Exception e) {
			throw new PushServiceException("Error when sending unsubscribe request.", e);
		}
	}

	public static final class Factory extends ClientProtocolFactory {

		public Factory() {
			super(PROTOCOL_NAME);
		}
		
		@Override
		public Object create(String description) throws ProductCreationException {
			PushClientProtocol protocol = new PushClientProtocol();
			PushClientService.INSTANCE.setProtocol(protocol);
			return protocol;
		}
		
	}
}