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
package com.b2international.snowowl.internal.eventbus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.ExtendedIOUtil;

import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.net4j.IEventBusProtocol;

/**
 * @since 3.1
 */
public class MessageFactory {

	public static final BaseMessage createMessage(String address, Object message, String tag) {
		checkAddress(address);
		CheckUtil.checkArg(message, "Message should be specified, null messages are not allowed");
		return new BaseMessage(address, message, tag);
	}
	
	public static final void checkAddress(String address) {
		CheckUtil.checkArg(!isNullOrEmpty(address), "Address cannot be null or empty");		
	}

	public static boolean isNullOrEmpty(String value) {
		return value == null || "".equals(value);
	}

	public static void writeMessage(ExtendedDataOutputStream out, IMessage message) throws IOException {
		CheckUtil.checkNull(message, "Message should not be null");
		CheckUtil.checkArg(message.body() instanceof Serializable, String.format("Message body type should be subtype of Serializable on address: %s, but was %s", message.address(), message.body()));
		out.writeString(message.address());
		final String replyAddress = message.replyAddress() == null ? "" : message.replyAddress();
		out.writeString(replyAddress);
		out.writeBoolean(message.isSend());
		out.writeBoolean(message.isSucceeded());
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ExtendedDataOutputStream wrap = ExtendedDataOutputStream.wrap(stream);
		ExtendedIOUtil.writeObject(wrap, message.body());
		ExtendedIOUtil.writeByteArray(out, stream.toByteArray());
		out.writeString(message.tag());
	}
	
	public static IMessage readMessage(ExtendedDataInputStream in, IEventBusProtocol protocol) throws IOException {
		final String address = in.readString();
		final String replyAddress = in.readString();
		final boolean send = in.readBoolean();
		final boolean succeeded = in.readBoolean();
		final byte[] body = ExtendedIOUtil.readByteArray(in);
		final String tag = in.readString();
		final BaseMessage message = createMessage(address, ExtendedDataInputStream.wrap(new ByteArrayInputStream(body)), tag);
		message.replyAddress = isNullOrEmpty(replyAddress) ? null : replyAddress;
		message.replyProtocol = protocol;
		message.send = send;
		message.succeeded = succeeded;
		return message;
	}
	
}