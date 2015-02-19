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

import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;

/**
 * A {@link Request} sent to the server to unsubscribe from a particular topic.
 * 
 * @since 2.8
 */
public class UnsubscribeRequest extends Request {

	private final Serializable topic;

	public UnsubscribeRequest(SignalProtocol<?> protocol, Serializable topic) {
		super(protocol, Net4jProtocolConstants.UNSUBSCRIBE_SIGNAL);
		this.topic = topic;
	}

	@Override
	protected void requesting(ExtendedDataOutputStream out) throws Exception {
		out.writeObject(topic);
	}

}