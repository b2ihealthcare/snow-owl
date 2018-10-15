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
package com.b2international.snowowl.eventbus;

/**
 * Represents a message to be send/receive over/from the {@link IEventBus}.
 *
 * @since 3.1
 */
public interface IMessage {

	public static final String DEFAULT_TAG = "event";
	public static final String REQUEST_TAG = "request";
	public static final String NOTIFICATION_TAG = "notification";

	/**
	 * Returns the body of this {@link IMessage}, can be used for simple types
	 * with explicit cast.
	 *
	 * @return
	 * @throws ClassNotFoundException
	 */
	Object body();

	/**
	 * Returns the body of this message. If the body was not resolved already
	 * then resolves it using the {@link ClassLoader} of the given {@link Class}
	 * , after resolution the method always return the resolved object with the
	 * given type.
	 *
	 * @return
	 */
	<T> T body(Class<T> type);

	/**
	 *
	 * @param type
	 * @param classLoader
	 * @return
	 * @since 3.2
	 */
	<T> T body(Class<T> type, ClassLoader classLoader);

	/**
	 * Returns the replyAddress for this message.
	 *
	 * @return
	 */
	String replyAddress();

	/**
	 * Returns the address this message is belongs to.
	 *
	 * @return
	 */
	String address();

	/**
	 * Returns if this message is a send only message.
	 *
	 * @return
	 */
	boolean isSend();

	/**
	 * Replies with the given message.
	 *
	 * @param message
	 */
	void reply(Object message);

	/**
	 * Returns a failure message to the replyAddress.
	 *
	 * @param failure
	 */
	void fail(Object failure);

	/**
	 * Sets the address for this message;
	 *
	 * @param address
	 */
	void setAddress(String address);
	
	/**
	 * @return the tag associated with this message.
	 */
	String tag();
	
	/**
	 * Returns <code>true</code> if the original message was successfully
	 * delivered to the address, <code>false</code> if some error happened, in
	 * this case the {@link #body(Class)} method should return the
	 * {@link Exception} for this error.
	 *
	 * @return
	 * @see IMessage#body(Class)
	 */
	boolean isSucceeded();

}