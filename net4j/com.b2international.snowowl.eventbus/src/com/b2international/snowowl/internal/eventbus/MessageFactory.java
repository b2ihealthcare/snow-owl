/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.*;
import java.util.Map;

import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.netty.IEventBusNettyHandler;

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

	public static IMessage writeMessage(IMessage message) throws IOException {
		checkNotNull(message, "Message should not be null");
		checkArgument(message.body() instanceof Serializable, String.format("Message body type should be subtype of Serializable on address: %s, but was %s", message.address(), message.body()));

		/* 
		 * Replace message body with an input stream; it is unlikely that someone would want to 
		 * send a stream over a message.
		 */
		final Object serializedBody;
		if (message.body() instanceof ByteArrayInputStream) {
			serializedBody = message.body();
		} else {
			try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				try (final ObjectOutputStream oos = new ObjectOutputStream(baos)) {
					oos.writeObject(message.body());
				}
				
				serializedBody = new ByteArrayInputStream(baos.toByteArray());
			}
		}
			
		final BaseMessage serializedMessage = createMessage(message.address(), 
			serializedBody, 
			message.tag(), 
			message.headers());
		
		serializedMessage.replyAddress = message.replyAddress();
		serializedMessage.send = message.isSend();
		serializedMessage.succeeded = message.isSucceeded();
		
		return serializedMessage;
	}
	
	public static IMessage readMessage(IMessage message, IEventBusNettyHandler via) throws IOException {
		// Keep the message body in input stream form; it will be de-serialized on demand
		final BaseMessage messageWithSource = createMessage(message.address(), 
			message.body(), 
			message.tag(), 
			message.headers());
		
		messageWithSource.replyAddress = message.replyAddress();
		messageWithSource.send = message.isSend();
		messageWithSource.succeeded = message.isSucceeded();

		// Indicate that the message came from a bridge, which should be used when replying
		messageWithSource.via = via;
		
		return messageWithSource;
	}
}
