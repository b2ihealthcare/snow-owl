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

import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.net4j.EventBusConstants;
import com.b2international.snowowl.internal.eventbus.MessageFactory;

/**
 * Sends EventBus messages over net4j connections.
 * 
 * @since 3.1
 */
public class SendRequest extends Request {

	private final Object body;

	public SendRequest(EventBusProtocol protocol, short signalID, Object body) {
		super(protocol, signalID);
		CheckUtil.checkArg(body instanceof IMessage, "Body should be an IMessage instance");
		this.body = body;
	}

	@Override
	protected void requesting(ExtendedDataOutputStream out) throws Exception {
		switch (getID()) {
		case EventBusConstants.SEND_MESSAGE_SIGNAL: {
			MessageFactory.writeMessage(out, (IMessage) body);
			break;
		}
		default:
			throw new IllegalArgumentException("Unknown signalID: " + getID());
		}
	}

}