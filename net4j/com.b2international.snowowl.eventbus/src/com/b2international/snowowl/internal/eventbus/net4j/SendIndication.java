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
package com.b2international.snowowl.internal.eventbus.net4j;

import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.net4j.EventBusConstants;
import com.b2international.snowowl.internal.eventbus.MessageFactory;

/**
 * Indication that someone from a remote node wants to send a message to this
 * system's {@link IEventBus}.
 * 
 * @since 3.1
 */
class SendIndication extends Indication {

	public SendIndication(EventBusProtocol protocol, short signalID) {
		super(protocol, signalID);
	}

	@Override
	protected void indicating(ExtendedDataInputStream in) throws Exception {
		final IEventBus eb = getProtocol().getInfraStructure();
		switch (getID()) {
		case EventBusConstants.SEND_MESSAGE_SIGNAL: {
			eb.receive(MessageFactory.readMessage(in, getProtocol()));
		}
		}
	}

	@Override
	public EventBusProtocol getProtocol() {
		return (EventBusProtocol) super.getProtocol();
	}

}