/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.io.ObjectStreamClass;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedIOUtil;

import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.net4j.IEventBusProtocol;

/**
 * @since 3.1
 */
/*package*/ class BaseMessage implements IMessage {

	private String address;
	private Object body;
	private String tag;

	/*package*/ boolean succeeded = true;
	/*package*/ boolean send;
	/*package*/ String replyAddress;
	/*package*/ EventBus bus;
	// used for message reply only
	/*package*/ IEventBusProtocol replyProtocol;

	/*package*/ BaseMessage(String address, Object body, String tag) {
		this(address, null, true, body, tag);
	}

	/*package*/ BaseMessage(String address, String replyAddress, boolean send, Object body, String tag) {
		this.address = address;
		this.send = send;
		this.replyAddress = replyAddress;
		this.body = body;
		this.tag = tag;
	}

	@Override
	public String replyAddress() {
		return replyAddress;
	}

	@Override
	public String address() {
		return address;
	}

	@Override
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public boolean isSend() {
		return send;
	}

	@Override
	public Object body() {
		return body;
	}
	
	@Override
	public String tag() {
		return tag;
	}

	@Override
	public <T> T body(Class<T> type) {
		return body(type, type.getClassLoader());
	}

	@Override
	public <T> T body(Class<T> type, final ClassLoader classLoader) {
		CheckUtil.checkNull(body, "Body should not be null.");
		if (body instanceof ExtendedDataInputStream) {
			synchronized (this) {
				if (body instanceof ExtendedDataInputStream) {
					try {
						Object readObject = null;
						if (classLoader != null) {
							readObject = ((ExtendedDataInputStream) body).readObject(new ExtendedIOUtil.ClassResolver() {
								@Override
								public Class<?> resolveClass(ObjectStreamClass v) throws ClassNotFoundException {
									return Class.forName(v.getName(), true, classLoader);
								}
							});
						} else {
							readObject = ((ExtendedDataInputStream) body).readObject();
						}
						if (readObject != null) {
							body = readObject;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (type.isInstance(body)) {
			return type.cast(body);
		}
		throw new IllegalArgumentException("Could not resolve message body with class: " + type + " body: " + body);
	}

	@Override
	public void reply(Object message) {
		if (message != null) {
			sendReply(new BaseMessage(replyAddress, message, tag));
		}
	}

	@Override
	public void fail(Object failure) {
		if (failure != null) {
			final BaseMessage reply = new BaseMessage(replyAddress, failure, tag);
			reply.succeeded = false;
			sendReply(reply);
		}
	}

	@Override
	public boolean isSucceeded() {
		return succeeded;
	}

	private void sendReply(BaseMessage reply) {
		if (bus != null && !MessageFactory.isNullOrEmpty(reply.address)) {
			bus.sendReply(replyProtocol, reply, null);
		}
	}
	
	@Override
	public String toString() {
		return String.format("Address: %s, message: %s tag:", address, body, tag);
	}

}