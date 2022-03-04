/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.internal.eventbus;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import com.b2international.snowowl.eventbus.IMessage;

/**
 * @since 3.1
 */
public class MessageFactory {

	public static final BaseMessage createMessage(String address, Object message, String tag, final Map<String, String> headers) {
		checkAddress(address);
		checkArgument(message != null, "Message should be specified, null messages are not allowed");
		return new BaseMessage(address, message, tag, headers);
	}
	
	public static final void checkAddress(String address) {
		checkArgument(!isNullOrEmpty(address), "Address cannot be null or empty");		
	}

	public static boolean isNullOrEmpty(String value) {
		return value == null || "".equals(value);
	}

	public static BaseMessage writeMessage(IMessage message) throws IOException {
		checkNotNull(message, "Message should not be null");
		
		/*
		 * Replace message body with an input stream, if hasn't been done already. It
		 * would be useless to send a serialized instance of an input stream over a
		 * message, so there is no possibility of confusion.
		 */
		final String address = message.address();
		final Object body = message.body();
		final Serializable serializableBody;
		
		if (body instanceof Serializable) {
			serializableBody = (Serializable) body;
		} else {
			final String className = (body == null) ? "null" : "'" + body.getClass().getSimpleName() + "'";
			throw new IllegalArgumentException(String.format("Message body should be a subtype of Serializable on address '%s', but was %s", address, className));
		}

		final BaseMessage serializableMessage = createMessage(address, serializableBody, message.tag(), message.headers());
		serializableMessage.replyAddress = message.replyAddress();
		serializableMessage.send = message.isSend();
		serializableMessage.succeeded = message.isSucceeded();
		return serializableMessage;
	}
}
