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

import java.util.Set;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import com.b2international.snowowl.eventbus.net4j.EventBusConstants;

/**
 * @since 3.1
 */
public class HandlerChangeIndication extends IndicationWithResponse {

	public HandlerChangeIndication(EventBusProtocol protocol, short signalID) {
		super(protocol, signalID);
	}

	@Override
	protected void indicating(ExtendedDataInputStream in) throws Exception {
		final Object set = in.readObject();
		if (set instanceof Set) {
			if (getID() == EventBusConstants.HANDLER_UNREGISTRATION) {
				getProtocol().unregisterAddressBook((Set<String>)set);
			} else {
				getProtocol().registerAddressBook((Set<String>)set);
			}
		}
	}

	@Override
	protected void responding(ExtendedDataOutputStream out) throws Exception {
		switch (getID()) {
		case EventBusConstants.HANDLER_INIT: {
			out.writeObject(getProtocol().getInfraStructure().getAddressBook());
			break;
		}
		case EventBusConstants.HANDLER_REGISTRATION:
		case EventBusConstants.HANDLER_UNREGISTRATION:
			out.writeBoolean(true);
			break;
		default:
			throw new IllegalArgumentException("Unknown signalID: " + getID());
		}
	}
	
	@Override
	public EventBusProtocol getProtocol() {
		return (EventBusProtocol) super.getProtocol();
	}

}