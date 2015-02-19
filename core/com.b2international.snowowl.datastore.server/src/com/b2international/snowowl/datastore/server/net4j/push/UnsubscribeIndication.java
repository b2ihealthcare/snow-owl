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

import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;

/**
 * An {@link Indication} for processing a request to unsubscribe.
 * 
 * @since 2.8
 */
public class UnsubscribeIndication extends Indication {

	public UnsubscribeIndication(SignalProtocol<?> protocol) {
		super(protocol, Net4jProtocolConstants.SUBSCRIBE_SIGNAL);
	}

	@Override
	protected void indicating(ExtendedDataInputStream in) throws Exception {
		Serializable topic = (Serializable) in.readObject();
		PushServerService.INSTANCE.unsubscribe(topic, (PushServerProtocol) getProtocol());
	}

}