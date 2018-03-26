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
package com.b2international.snowowl.eventbus.net4j;

/**
 * @since 3.1
 */
public interface EventBusConstants {

	/* Constants used for the IEventBusProtocol */
	public static final String PROTOCOL_NAME = "eventbus";
	public static final short SEND_MESSAGE_SIGNAL = 5000;
	public static final short HANDLER_REGISTRATION = 5001;
	public static final short HANDLER_UNREGISTRATION = 5002;
	public static final short HANDLER_INIT = 5003;
	
	/* Constants used for IEventBus creation */
	public static final String EVENT_BUS_PRODUCT_GROUP = "com.b2international.snowowl.eventbus";
	public static final String GLOBAL_BUS = "eventbus";
	
}