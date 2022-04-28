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

import java.util.Map;

import com.b2international.snowowl.eventbus.IMessage;
import com.google.common.base.Strings;

/**
 * @since 3.1
 */
public class MessageFactory {

	public static final BaseMessage createMessage(String address, Object message, String tag, final Map<String, String> headers) {
		return createMessage(address, message, tag, headers, true, true);
	}
	
	public static final BaseMessage createMessage(String address, Object message, String tag, Map<String, String> headers, boolean send, boolean succeeded) {
		checkAddress(address);
		checkArgument(message != null, "Message should be specified, null messages are not allowed");
		
		final BaseMessage baseMessage = new BaseMessage(address, message, tag, headers);
		baseMessage.send = send;
		baseMessage.succeeded = succeeded;
		
		return baseMessage;
	}

	public static final void checkAddress(String address) {
		checkArgument(!Strings.isNullOrEmpty(address), "Address cannot be null or empty");		
	}

	public static BaseMessage writeMessage(IMessage message) {
		checkNotNull(message, "Message should not be null");
		
		/*
		 * Replace message body with an input stream, if hasn't been done already. It
		 * would be useless to send a serialized instance of an input stream over a
		 * message, so there is no possibility of confusion.
		 */
		final String address = message.address();
		final Object body = message.body();

		final BaseMessage serializableMessage = createMessage(address, body, message.tag(), message.headers(), message.isSend(), message.isSucceeded());
		serializableMessage.replyAddress = message.replyAddress();
		return serializableMessage;
	}

	public static BaseMessage writeFailure(IMessage message, Throwable t) {
		checkNotNull(message, "Message should not be null");
		checkNotNull(t, "Throwable should not be null");
		
		final String address = message.address();
		final Object body = t;
		
		final BaseMessage failureMessage = createMessage(address, body, message.tag(), message.headers(), message.isSend(), false);
		failureMessage.replyAddress = message.replyAddress();
		return failureMessage;
	}
}
