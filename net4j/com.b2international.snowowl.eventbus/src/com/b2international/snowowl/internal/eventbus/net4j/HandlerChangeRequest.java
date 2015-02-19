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

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import com.b2international.snowowl.eventbus.net4j.EventBusConstants;

/**
 * @since 3.1
 */
public class HandlerChangeRequest extends RequestWithConfirmation<Object> {

	private Object body;

	public HandlerChangeRequest(EventBusProtocol protocol, short signalID, Object body) {
		super(protocol, signalID);
		CheckUtil.checkArg(body, "body");
		this.body = body;
	}
	
	@Override
	protected void requesting(ExtendedDataOutputStream out) throws Exception {
		switch (getID()) {
			case EventBusConstants.HANDLER_INIT:
			case EventBusConstants.HANDLER_REGISTRATION:
			case EventBusConstants.HANDLER_UNREGISTRATION: {
				out.writeObject(body);
				break;
			}
			default: throw new IllegalArgumentException("Unknown signal ID: " + getID());
		}
	}

	@Override
	protected Object confirming(ExtendedDataInputStream in) throws Exception {
		switch (getID()) {
			case EventBusConstants.HANDLER_INIT: {
				return in.readObject();
			}
			case EventBusConstants.HANDLER_REGISTRATION:
			case EventBusConstants.HANDLER_UNREGISTRATION: {
				return in.readBoolean();
			}
			default: throw new IllegalArgumentException("Unknown signal ID: " + getID());
		}
	}
	
	@Override
	public EventBusProtocol getProtocol() {
		return (EventBusProtocol) super.getProtocol();
	}

}