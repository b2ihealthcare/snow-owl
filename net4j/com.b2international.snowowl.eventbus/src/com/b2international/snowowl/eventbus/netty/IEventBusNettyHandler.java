/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.eventbus.netty;

import com.b2international.snowowl.eventbus.IHandler;
import com.b2international.snowowl.eventbus.IMessage;

import io.netty.channel.ChannelInboundHandler;
import io.netty.util.AttributeKey;

/**
 * @since 8.1.0
 */
public interface IEventBusNettyHandler extends ChannelInboundHandler, IHandler<IMessage> {
	
	/**
	 * Channel attribute and user event key to indicate that the address book exchange was completed.
	 */
	public static final AttributeKey<Boolean> KEY_ADDRESS_BOOK_SYNCHRONIZED = AttributeKey.valueOf("addressBookSynchronized");
}
