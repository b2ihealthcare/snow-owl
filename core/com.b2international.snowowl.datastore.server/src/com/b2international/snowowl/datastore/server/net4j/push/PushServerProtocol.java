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
package com.b2international.snowowl.datastore.server.net4j.push;

import java.io.Serializable;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.spi.net4j.ServerProtocolFactory;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.datastore.net4j.push.PushClientProtocol;
import com.b2international.snowowl.datastore.net4j.push.PushServiceException;

/**
 * Server-side signal protocol for the push notification mechanism.
 * 
 * @since 2.8
 */
public class PushServerProtocol extends SignalProtocol<Object> {

	public static final String PROTOCOL_NAME = PushClientProtocol.PROTOCOL_NAME;

	public PushServerProtocol() {
		super(PushClientProtocol.PROTOCOL_NAME);
	}
	
	@Override
	protected SignalReactor createSignalReactor(short signalID) {
		switch (signalID) {
		case Net4jProtocolConstants.SUBSCRIBE_SIGNAL:
			return new SubscribeIndication(this);
		case Net4jProtocolConstants.UNSUBSCRIBE_SIGNAL:
			return new UnsubscribeIndication(this);
		default:
			return super.createSignalReactor(signalID);
		}
	}

	/**
	 * Sends a push request to the client.
	 * 
	 * @param topic the topic of the notification
	 * @param message the notification message
	 * @throws PushServiceException
	 */
	public <T extends Serializable, M extends Serializable> void sendPushRequest(T topic, M message) throws PushServiceException {
		try {
			new PushRequest<T, M>(this, topic, message).sendAsync();
		} catch (Exception e) {
			throw new PushServiceException("Error when sending push request.", e);
		}
	}

	public static final class Factory extends ServerProtocolFactory {
		
		public Factory() {
			super(PROTOCOL_NAME);
		}
	
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.net4j.util.factory.IFactory#create(java.lang.String)
		 */
		@Override
		public Object create(final String description) throws ProductCreationException {
			PushServerProtocol protocol = new PushServerProtocol();
			return protocol;
		}
	}
}